define ["jquery", "underscore", "backbone", "cs!twitter"
], ($, _, Backbone, Twitter) -> $ ->

  class Tweet extends Backbone.Model
    validate: (attrs) ->
      if attrs.text.length > 140
        return "A tweet cannot be longer than 140 characters"

  class Tweets extends Backbone.Collection
    model: Tweet
    comparator: (tweet) -> -tweet.id

  class TweetItem extends Backbone.View
    tagName: "li"
    template: _.template $("#tweet-item-template").html()
    retweetTemplate: _.template $("#tweet-retweet-template").html()

    initialize: =>
      @model.bind "change", @render
      @model.view = this

    render: =>
      tweet = @model.toJSON()
      if tweet.retweeted_status?
        @$el.html(@template(tweet.retweeted_status))
        @$el.append(@retweetTemplate(tweet))
      else
        @$el.html(@template(tweet))

  class TweetList extends Backbone.View
    initialize: =>
      @collection.bind "add", @addTweet
      @collection.bind "remove", @removeTweet

    addTweet: (tweet, collection, options) =>
      view = new TweetItem({ model: tweet })
      view.render()
      before = @$el.children()[options.index]
      if before
        $(before).before(view.el)
      else
        @$el.append(view.el)

    removeTweet: (tweet) =>
      tweet.view.remove()

  class Input extends Backbone.View
    events:
      "keydown": "onKeydown"

    onKeydown: (e) =>
      if e.keyCode == 13
        text = @$el.val()
        @$el.val("")
        twitter.tweet(text)

  window.tweets = tweets = new Tweets()

  window.list = list = new TweetList
    collection: tweets
    el: $("#tweets")

  window.input = input = new Input
    el: $("#entry")

  twitter = new Twitter
  twitter.on "tweet", (tweet) ->
    tweets.add(new Tweet(tweet))
  twitter.on "delete", (id) ->
    tweets.remove(tweets.get(id))
