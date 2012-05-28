import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.impl.Json;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.core.sockjs.SockJSSocket;
import org.vertx.java.deploy.Verticle;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamAdapter;
import twitter4j.UserStreamListener;

public class App extends Verticle {
    public void start() {
        HttpServer httpServer = vertx.createHttpServer();

        httpServer.requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                String file = "";
                if (req.path.equals("/")) {
                    file = "index.html";
                } else if (!req.path.contains("..")) {
                    file = req.path;
                }
                req.response.sendFile("../web/" + file);
            }
        });

        final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new UserStreamAdapter());
        twitterStream.user();
        
        SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);

        JsonObject config = new JsonObject().putString("prefix", "/twitter");

        sockJSServer.installApp(config, new Handler<SockJSSocket>() {
            public void handle(final SockJSSocket sock) {
                
                UserStreamListener userStreamListener = new UserStreamAdapter() {
                    @Override
                    public void onStatus(twitter4j.Status status) {
                        if (status.getInReplyToScreenName() == null) {
                            Buffer buffer = new Buffer(Json.encode(status));
                            sock.writeBuffer(buffer);
                            System.out.println("writing " + buffer);
                        }
                    }
                };
                
                twitterStream.addListener(userStreamListener);
                
                sock.dataHandler(new Handler<Buffer>() {
                    public void handle(Buffer buffer) {
                        System.out.println("toto: " + buffer.toString());
                    }
                });
            }
        });

        httpServer.listen(8080);
    }
}
