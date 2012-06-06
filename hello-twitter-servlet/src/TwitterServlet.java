import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import twitter4j.FilterQuery;
import twitter4j.StatusAdapter;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@WebServlet("/twitter")
public class TwitterServlet extends WebSocketServlet {
    private static final long serialVersionUID = -5677827008838868510L;

    final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("<!DOCTYPE html><html><body><h1>Twitter !</h1></body></html>");
    }

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

}
