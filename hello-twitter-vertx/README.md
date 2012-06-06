# WebSockets in practice with Vert.x 1.0 (Java) and Sock JS 0.3 

This project is part of the WebSocket implementation comparison : WebSocket in practice

## Usage

This project has to be used with [vert.x](http://vertx.io/). Simply compile App.java and run

	vertx run App

## Configuration

To use Twitter connection, it requires a file named twitter4j.properties in src folder which contains :

	debug=true
	oauth.consumerKey=***
	oauth.consumerSecret=***
	oauth.accessToken=***
	oauth.accessTokenSecret=***
	
The access token for the application has to be created on Twitter administration tools

## Short description

The class App implements Verticle from [vert.x](http://vertx.io/) project. First part of this verticle aims to serve static resources and second part instantiate a [SockJS](http://sockjs.org) server with the [vert.x](http://vertx.io/) integration.