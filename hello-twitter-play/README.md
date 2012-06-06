# WebSockets in practice with Play Framework 2.0 (Java) 

This project is part of the WebSocket implementation comparison : WebSocket in practice

## Usage

This project has to be used with Play Framework. Simply run play with

	play run

## Configuration

To use Twitter connection, it requires a file named twitter4j.properties in app folder which contains :

	debug=true
	oauth.consumerKey=***
	oauth.consumerSecret=***
	oauth.accessToken=***
	oauth.accessTokenSecret=***
	
The access token for the application has to be created on Twitter administration tools

## Short description

The class controlers.Application extends Controller from [Play framework 2.0](http://www.playframework.org/). It implements a twitter methods which return the WebSocket object from Play API. The method and the WebSocket is mapped to the "/twitter" URL with the conf/routes file. 