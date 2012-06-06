package controllers;

import static play.Logger.debug;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import twitter4j.FilterQuery;
import twitter4j.StatusAdapter;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamAdapter;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static WebSocket<String> twitter() throws TwitterException {

        final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new UserStreamAdapter());
        twitterStream.user();

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
    }
}