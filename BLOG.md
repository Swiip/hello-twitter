# Les WebSockets en pratique

L'HTML 5 fait bien des émules en ce moment grâce à la quantité de nouvelles fonctionnalités qu'il apporte mais aussi et surtout par l'empleur des possibilités qu'ouvrent chacune de ces fonctionnalités.

Les WebSockets en sont un parfait exemple. L'API est ridiculement petite : connection, reception et envois d'un message, on a déjà fait le tour et pourtant cela pourait bien révolutionner la physionomie du Web.

Maintenant, en qualité de développeur Web, la question que je me suis posée est : quel sont les outils qui me sont donné pour utiliser cette nouvelle possibilité (avec comme sous entendu : il est hors de question que je m'ammuse à gérer ma petite socket à la main, je veux qu'on me mache le travail).

## Objectifs

Pour répondre à ces questions j'ai démarré un repo GitHub regroupant l'implémentation de la même application mettant en oeuvre les WebSockets avec un panel des technologies existantes pour le faire.

L'application est très simple mais met en oeuvre des concepts pas forcément courant pour un développement serveur : push serveur vers client, code asynchrone...

Le but n'était pas de valider les performances promises par chaque technos, je m'en tiendrais au théorique et aux benchs existant sur internet. Mais plutôt de vérifier si c'est facilement mis en oeuvre, compréhensible, maintenable, enfin tous ces critères qui font qu'on est content du code produit.

## L'application

L'objectif est d'avoir un application très courte mais qui met en oeuvre des évennements côté serveur à transmettre au client. Pour cela, rien de mieux que l'application de démo si populaire en ce moment : le Twitter Wall.

J'ai justement assisté à la présentation d'un live coding sur Backbone JS pour un Twitter Wall, j'ai donc récupéré les sources pour avoir ma partie cliente clé en main, reste plus qu'à faire le serveur.

Comme j'ai récupéré pas mal de code, je cite mes sources : Bodil Stokke ([@bodiltv](https://twitter.com/#!/bodiltv)), présentation au [Mix IT 2012](http://www.mix-it.fr) intitullé "[Painless Web App Development with Backbone](http://www.mix-it.fr/session/95/painless-web-app-development-with-backbone)" (la conversion CS > JS est de moi par contre :p).

## Les technos choisies

En tant que développeur Web Java, j'ai une pas trop mauvaise vision des mondes Java et JS. Je ne sais pas si c'est le sentiment général, mais ma vision en commençant est que le leader pour faire du WebSocket n'est pas côté Java mais côté JS avec [Socket.IO](http://socket.io/). Si je cible principalement des technos Java, j'ai pris cette implémentaiton JS comme référence.

Suite à la notoriété de Play Framework et une présentation interessante sur le sujet parlant notamment de la gestion des I/O en asynchrone et son support des WebSockets, cela a été ma première implémentaiton Java.

Et puis il y a la percée de [vert.x](http://vertx.io/) avec des benchs tonitruant sur Internet le plaçant devant node.js, j'ai donc essayé, voir si le fonctionnel suivait.

Avec ces trois premier, moi qui fait pas mal de JEE, je me suis demandé : mais ou est passé le JEE dans tout ça ? J'ai donc pris comme dernière implémentation la WebSocketServlet de Jetty (je détaillerais pourquoi celle ci et pas Atmosphere ou Tomcat 7).

## node.js & Socket.IO

## play

## vert.x

## servlet

## conclusions

