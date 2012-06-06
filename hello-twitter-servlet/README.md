# WebSockets in practice with WebSocketServlet and Jetty 8.1

This project is part of the WebSocket implementation comparison : WebSocket in practice

## Usage

This project has to be used with [Jetty 8.1](http://www.eclipse.org/jetty/). I used [Eclipse Jetty plugin](http://sourceforge.net/projects/eclipse-jetty/) for development but can work with any Jetty with the right packaging.

## Configuration

To use Twitter connection, it requires a file named twitter4j.properties in src folder which contains :

	debug=true
	oauth.consumerKey=***
	oauth.consumerSecret=***
	oauth.accessToken=***
	oauth.accessTokenSecret=***
	
The access token for the application has to be created on Twitter administration tools

## Short description

The class TwitterServlet implements WebSocketServlet which is the implementation proposed by Jetty. It allows to implements doWebSocketConnect method to handle the WebSocket.