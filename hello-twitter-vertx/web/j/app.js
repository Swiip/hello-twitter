define([ "jquery", "underscore", "backbone" ], function($, _, Backbone, Twitter) {

    var Tweet = Backbone.Model.extend({
        validate : function(attrs) {
            if (attrs.text.length > 140) {
                return "A tweet cannot be longer than 140 characters";
            }
        }
    });

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
            console.log("ajout � ", this.$el, " du tweet ", tweet)
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
                text = this.$el.val();
                this.$el.val("");
                twitter.tweed(text);
            }
        }
    });

    window.tweets = tweets = new Tweets();

    console.log($("#tweets"));
    window.list = list = new TweetList({
        collection : tweets,
        el : $("#tweets")
    });
    console.log(list.$el, list.el);

    window.input = input = Input.extend({
        el : $("#entry")
    });
    
    sock = new SockJS('http://localhost:8080/twitter');
    sock.onopen = function() {
        console.log('open');
    };
    sock.onmessage = function(event) {
        var tweet = new Tweet(JSON.parse(event.data));
        tweets.add(tweet);
        console.log("message", event, tweet);
    };
    sock.onclose = function() {
        console.log('close');
    };
    
});