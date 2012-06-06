define([ "jquery", "underscore", "backbone" ], function($, _, Backbone, Twitter) {

    var Tweet = Backbone.Model.extend();

    var Tweets = Backbone.Collection.extend({
        model : Tweet,
        comparator : function(tweet) {
            return -tweet.id;
        }
    });

    var TweetItem = Backbone.View.extend({
        tagName : "li",
        template : _.template($("#tweet-item-template").html()),
        retweetTemplate : _.template($("#tweet-retweet-template").html()),

        initialize : function() {
            this.model.bind("change", this.render);
            this.model.view = this;
        },

        render : function() {
            var tweet = this.model.toJSON();
            if (tweet.retweetedStatus) {
                this.$el.html(this.template(tweet.retweetedStatus));
                this.$el.append(this.retweetTemplate(tweet));
            } else {
                this.$el.html(this.template(tweet));
            }
        }
    });

    var TweetList = Backbone.View.extend({
        initialize : function() {
            console.log("init TweetList");
            this.collection.bind("add", this.addTweet, this);
            this.collection.bind("remove", this.removeTweet, this);
        },

        addTweet : function(tweet, collection, options) {
            var view = new TweetItem({
                model : tweet
            });
            view.render();
            var before = this.$el.children()[options.index];
            if (before) {
                $(before).before(view.el);
            } else {
                this.$el.append(view.el);
            }
        },

        removeTweet : function(tweet) {
            tweet.view.remove();
        }
    });

    var Input = Backbone.View.extend({
        events : {
            "keydown" : "onKeydown"
        },

        onKeydown : function(event) {
            if (event.keyCode == 13) {
                webSocket.send(this.$el.val());
                tweets.reset();
                list.$el.empty();
            }
        }
    });

    window.tweets = tweets = new Tweets();

    window.list = list = new TweetList({
        collection : tweets,
        el : $("#tweets")
    });

    window.input = input = new Input({
        el : $("#entry")
    });
    

    
    var webSocket = new WebSocket("ws://localhost:8080/hello-twitter-servlet/twitter");
    webSocket.onopen = function(event) {
        console.log("open", event);
    };
    webSocket.onmessage = function(event) {
        var tweet = new Tweet(JSON.parse(event.data));
        tweets.add(tweet);
        console.log("message", event, tweet);
    };
    webSocket.onerror = function(event) {
        console.log("error", event);
    };
    webSocket.onclose = function(event) {
        console.log("close", event);
    };
    
});