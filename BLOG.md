# Les WebSockets en pratique

L'HTML 5 fait bien des émules en ce moment grâce à la quantité de nouvelles fonctionnalités qu'il apporte mais aussi et surtout par l'empleur des possibilités qu'ouvrent chacune de ces fonctionnalités.

Les WebSockets en sont un parfait exemple. L'API est ridiculement petite : connection, reception et envois d'un message, on a déjà fait le tour et pourtant cela pourait bien révolutionner la physionomie du Web.

Maintenant, en qualité de développeur Web, la question que je me suis posée est : quel sont les outils qui me sont donné pour utiliser cette nouvelle possibilité (avec comme sous entendu : il est hors de question que je m'ammuse à gérer ma petite socket à la main, je veux qu'on me mache le travail).

## Objectifs

Pour répondre à ces questions j'ai démarré un repo GitHub regroupant l'implémentation de la même application mettant en oeuvre les WebSockets avec un panel des technologies existantes pour le faire.

L'application est très simple mais met en oeuvre des concepts pas forcément courant pour un développement serveur : push serveur vers client, code asynchrone...

Le but n'était pas de valider les performances promises par chaque technos, je m'en tiendrais au théorique et aux benchs existant sur internet. Mais plutôt de vérifier si c'est facilement mis en oeuvre, compréhensible, maintenable, enfin tous ces critères qui font qu'on est content du code produit.

## L'application

L'objectif est d'avoir une application très courte mais qui met en oeuvre des évennements côté serveur à transmettre au client. Pour cela, rien de mieux que l'application de démo si populaire en ce moment : le Twitter Wall.

J'ai justement assisté à la présentation d'un live coding sur Backbone JS pour un Twitter Wall, j'ai donc récupéré les sources pour avoir ma partie cliente clé en main, reste plus qu'à faire le serveur.

Comme j'ai récupéré pas mal de code, je cite mes sources : Bodil Stokke ([@bodiltv](https://twitter.com/#!/bodiltv)), présentation au [Mix IT 2012](http://www.mix-it.fr) intitullé "[Painless Web App Development with Backbone](http://www.mix-it.fr/session/95/painless-web-app-development-with-backbone)" (la conversion CS > JS est de moi par contre :p).

Toutes les sources sont disponibles sur GitHub : [https://github.com/Swiip/hello-twitter](https://github.com/Swiip/hello-twitter)

## Les technos choisies

En tant que développeur Web Java, j'ai une pas trop mauvaise vision des mondes Java et JS. Je ne sais pas si c'est le sentiment général, mais ma vision en commençant est que le leader pour faire du WebSocket n'est pas côté Java mais côté JS avec [Socket.IO](http://socket.io/). Si je cible principalement des technos Java, j'ai pris cette implémentaiton JS comme référence.

Suite à la notoriété de Play Framework et une présentation interessante sur le sujet parlant notamment de la gestion des I/O en asynchrone et son support des WebSockets, cela a été ma première implémentaiton Java.

Et puis il y a la percée de [vert.x](http://vertx.io/) avec des benchs tonitruant sur Internet le plaçant devant node.js, j'ai donc essayé, voir si le fonctionnel suivait.

Avec ces trois premier, moi qui fait pas mal de JEE, je me suis demandé : mais ou est passé le JEE dans tout ça ? J'ai donc pris comme dernière implémentation la WebSocketServlet de Jetty (je détaillerais pourquoi celle ci et pas Atmosphere ou Tomcat 7).

## node.js & Socket.IO

L'implémentation node.js et Socket.IO n'a pas démentie sa qualité. Alors bien sur c'est du JavaScript et plus précisément du node.js donc pour ceux qui ont encore des boutons à voir du code JavaScript, il faudra repasser.

Pour les autres, après avoir installé quelques modules avec le très bon npm, l'API est parfaite : socket.listen, socket.on, le site et donc la doc sympatique et pas de poblèmes de sérialisation des données puisqu'on est en JSON côté serveur et côté client.

Un focus sur le système d'évennement que je trouve très bien. Avec Socket.IO, on est pas directement sur la socket mais audessus d'un bus d'évennement. Chaque message est un évennement avec un nom. c'est très pratique et cela évite à avoir à gérer un dispatch manuel en fonction du contenu du message.

En plus de cela, Socket.IO a une compatibilité navigateur très large avec un transport fallback sur énormément de technos : WebSocket, Adobe Flash Socket, Ajax long polling, Ajax multipart streaming, Forever Iframe, JSONP Polling !

Et l'extrait du code pour aller avec :

	io.sockets.on("connection", function(socket) {
	    socket.on("filter", function(text) {
	        console.log("open stream on ", text);
	        t.stream("statuses/filter", {
	            track: text
	        }, function(s) {
	            s.on("data", function(data) {
	                console.log("received tweet ", data)
	                socket.emit("tweet", data);
	            });
	            s.on("error", function(error) {
	                console.log(util.inspect(error));
	            });
	            s.on("end", function() {
	                console.log("****** ERROR: Twitter stream terminated!")
	            });
	        });
	    });
	    socket.on("disconnect", function() {
	        console.log("socket closed");
	    });
	});

## Play 2

Je n'ai pas une grand experience de Play Framework, mais toujours au [Mix IT 2012](http://www.mix-it.fr), j'ai vu la présentation [Play 2.0](http://www.mix-it.fr/session/99/play-2-0) qui parlait de sa gestion des I/O asynchrone et d'un support des WebSocket, j'ai donc essayé.

Il a fallu démarrer le projet : avec l'ecosystème Scala pour le build et les templates notamment, c'est sur que cela destabilise un peu. Passer cela, le framework est assez simple d'utilisation et on arrive vite à l'implémentaiton de la WebSocket et si la doc ne regorge pas d'exemple, il y a tout de même ce qu'il faut.

Une WebSocket en Play 2 ça veut dire implémenter la classe WebSocket et la retourner comme valeur au controleur, c'est pas mal. Dans la WebSocket, on trouve des onReady et onMessage bien compréhensible et le système de Callback Java de Play, cela se code assez bien.

Après Socket.IO on est obligé de regretter la sérialisation manuelle en String et l'absence de système de "transport fallback" : s'il y a un mauvais proxy sur le chemin ou un vieux navigateur, bye bye...

Et voici pour le code :

    return new WebSocket<String>() {
        @Override
        public void onReady(WebSocket.In<String> in, final WebSocket.Out<String> out) {
            debug("web socket ready");

            final StatusListener statusListener = new StatusAdapter() {
                @Override
                public void onStatus(twitter4j.Status status) {
                    if (status.getInReplyToScreenName() == null) {
                        out.write(Json.toJson(status).toString());
                    }
                }
            };

            // For each event received on the socket,
            in.onMessage(new Callback<String>() {
                @Override
                public void invoke(String message) throws Throwable {
                    twitterStream.cleanUp();
                    twitterStream.addListener(statusListener);
                    FilterQuery filterQuery = new FilterQuery();
                    filterQuery.track(new String[] { message });
                    twitterStream.filter(filterQuery);
                }
            });

            // When the socket is closed.
            in.onClose(new Callback0() {
                public void invoke() {
                    debug("on close");
                }
            });
        }
    };

## vert.x

Le petit nouveau des frameworks "asynchrones" qui se veut polyglot et plus performant que node.js, ce n'est pas mal sur le papier.

Pour démarrer le projet, la documentation n'est pas mal du tout et pour lancer le premier hello world, un script qui lance bêtement une classe Java, ça sent bon la simplicité. Petite précision : Java 7 minimum ;)

La WebSocket maintenant, et la bonne nouvelle, ce n'est pas "juste" une WebSocket (même si c'est possible) mais une implémentation de SockJS. SockJS ressemble beaucoup à Socket.IO à la différence qu'il est polyglot (node.js, erlang, python, Java et j'en passe).

Un peu plus jeune que Socket.IO mais il a l'air d'être déjà au point, gère tout autant de "transport fallback", je regrette juste le petit système d'évennement de Socket.IO.

Côté API, vertx.createSockJSServer, sockJSServer.installApp et sock.dataHandler on trouve rapidement nos billes. Seul bisarerie, pourquoi suis-je obliger de me palucher un buffer (oui je suis très très fainéant).

Et le code complet :

    SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);

    JsonObject config = new JsonObject().putString("prefix", "/twitter");

    sockJSServer.installApp(config, new Handler<SockJSSocket>() {
        public void handle(final SockJSSocket sock) {
            final StatusListener statusListener = new StatusAdapter() {
                @Override
                public void onStatus(twitter4j.Status status) {
                    if (status.getInReplyToScreenName() == null) {
                        Buffer buffer = new Buffer(Json.encode(status));
                        sock.writeBuffer(buffer);
                    }
                }
            };

            sock.dataHandler(new Handler<Buffer>() {
                public void handle(Buffer buffer) {
                    twitterStream.cleanUp();
                    twitterStream.addListener(statusListener);
                    FilterQuery filterQuery = new FilterQuery();
                    filterQuery.track(new String[] { buffer.toString() });
                    twitterStream.filter(filterQuery);
                }
            });
        }
    });

## WebSocketServlet

Cette implémentation m'a donné du fil à retordre et pourtant je voulais au moins un exemple avec une technologie JEE.

Premier étonnement, la norme Servlet 3 qui se veut asynchrone, à la mode et tout ça... Ne gère pas les WebSocket. Elle propose de faire de l'asynchrone dans une servlet normale, mais pas de gérer réellement la WebSocket.

Ensuite il y a le cas d'[Atmosphere](https://github.com/Atmosphere/atmosphere/). Ici j'admet être très subjectif et ne m'étenderais pas pour ne pas faire du Troll gratuit mais pour dire les choses clairement : je n'ai rien compris. J'ai lu des articles, regardé des exemples de code et je n'étais toujours pas capable de faire mon Twitter Wall, j'ai fini par passer mon chemin et j'en arrête là pour le Troll.

Du coup il restait les implémentations maisons d'une WebSocketServlet par Jetty et Tomcat 7, l'API proposé par Jetty était un tout petit peu plus sympatique, je suis parti la dessus.

A partir de là c'est allez mieux : public class TwitterServlet extends WebSocketServlet, doWebSocketConnect, onMessage on y était vite.

Evidemment, on a pas ici de transport fallback ou des choses comme cela.

Avec un bout du code :

	@Override
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
        return new WebSocket.OnTextMessage() {
            private Connection connection;

            private final StatusListener statusListener = new StatusAdapter() {
                @Override
                public void onStatus(twitter4j.Status status) {
                    if (status.getInReplyToScreenName() == null) {
                        try {
                            connection.sendMessage(new ObjectMapper().writeValueAsString(status));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            @Override
            public void onOpen(Connection connection) {
                this.connection = connection;
                System.out.println("open");
            }

            @Override
            public void onMessage(String message) {
                twitterStream.cleanUp();
                twitterStream.addListener(statusListener);
                FilterQuery filterQuery = new FilterQuery();
                filterQuery.track(new String[] { message });
                twitterStream.filter(filterQuery);
            }

            @Override
            public void onClose(int arg0, String arg1) {
                System.out.println("close");
            }
        };
    } 

## conclusions

En conclusion Socket.IO est effectivement très bien placé. Très bonne API, de bonnes idées, une bonne doc et toutes les fonctionnalités nécessaire. Les autres sont obligés de s'y comparer.

Le petit nouveau vert.x s'en sort très bien, c'est le seul à proposer du transport fallback facilement. Evidemment, il ne sera pas toujours facile de convaincre SI de mettre cela en prod tout de suite.

Si vous avez un site Play, vous avez un support interessant, si vous n'avez pas encore de site Play, je ne suis pas sur qu'il faut y aller juste pour cela.

Enfin, le monde des normes et du JEE est vraiment à la traine à mon avis.

Rappel de la localisation des sources : [https://github.com/Swiip/hello-twitter](https://github.com/Swiip/hello-twitter)