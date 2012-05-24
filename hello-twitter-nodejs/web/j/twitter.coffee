define ["backbone"], (Backbone) ->
  class Twitter
    constructor: ->
      @[k] = v for own k, v of Backbone.Events
      @socket = io.connect "http://localhost"
      @socket.on "tweet", (e) => @trigger "tweet", e
      @socket.on "delete", (e) => @trigger "delete", e
      @socket.on "friends", (e) => @trigger "friends", e

    tweet: (text) ->
      @socket.emit "tweet", text
