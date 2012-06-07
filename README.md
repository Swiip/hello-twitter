# WebSockets in practice

This GitHub repository aims to regroup some implementations of the same application based on WebSockets.

As a Java specialist, most of the chosen implementations are in Java but I've added the [node.js](http://nodejs.org) / [Socket.IO](http://socket.io/) implementation as sort of reference.

## Hello Twitter

The application each implementation is targeting is very simple and based on the [Twitter Streaming API](https://dev.twitter.com/docs/streaming-apis).

The text field on top of the column is for entering a search query, when the user push enter, the query is sent through the WebSocket to the server which will open the stream on this filter. On each tweets from the stream, the server will push it to its client which will show it in the main column.

The Web part is almost the same in each example and heavily inspired by Bodil Stokke [@bodiltv](https://twitter.com/#!/bodiltv) and [her talk](http://www.mix-it.fr/session/95/painless-web-app-development-with-backbone) about [Backbone.JS](http://backbonejs.org/) at [Mix IT 2012](http://www.mix-it.fr/). The [node.js](http://nodejs.org) implementation as well.

##Implementations

These are all the implementation I first planned to make but it's possible to add one or another if asked or needed.

- [node.js](http://nodejs.org) & [Socket.IO](http://socket.io/)
- [Play framework 2.0](http://www.playframework.org/)
- [vert.x 1.0](http://vertx.io/)
- [WebSocketServket with Jetty 8.1](http://wiki.eclipse.org/Jetty/Feature/WebSockets) 