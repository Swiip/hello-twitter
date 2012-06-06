# WebSockets in practice with node.js and Socket.IO 

This project is part of the WebSocket implementation comparison : WebSocket in practice

## Usage

This project has to be used with [node.js](http://nodejs.org). Simply run node with

	node app

## Configuration

To use Twitter connection, it requires a file named .twitter-auth.json in root folder which contains :

	{
    	"consumer_key" : "...",
    	"consumer_secret" : "...",
    	"access_token_key" : "...",
    	"access_token_secret" : "..."
	}
	
The access token for the application has to be created on Twitter administration tools

## Short description

The app module start an [Express](http://expressjs.com/) server to handle static files and instantiate [Socket.IO](http://socket.io/) for handling the WebSocket. 