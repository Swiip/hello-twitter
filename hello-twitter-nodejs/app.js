var express = require("express");
var socketio = require("socket.io");
var twitter = require("twitter");
var util = require("util");
var fs = require("fs");
var path = require("path");
var _ = require("underscore");

var app = express.createServer()
app.use(express.bodyParser());
app.use(express.errorHandler());
app.use(express.static(__dirname + "/web"));

app.get("/", function(req, res) {
    res.sendfile(__dirname + "/web/index.html")
});

var io = socketio.listen(app);
var port = parseInt(process.env.PORT, 10) || 1337;
app.listen(port);
console.log("Listening on http://localhost:" + port + "/");

var twitterConfigPath = path.join(process.env.HOME, ".twitter-auth.json");
var twitterConfigContent = fs.readFileSync(twitterConfigPath, "utf8");
var twitterConfig = JSON.parse(twitterConfigContent);
var t = new twitter(twitterConfig);

io.sockets.on("connection", function(socket) {
    socket.on("filter", function(text) {
        console.log("open stream on ", text);
        t.stream("statuses/filter", {
            track: text
        }, function(s) {
            s.on("data", function(data) {
                console.log("received tweet ", data)
                socket.emit("tweet", data);
            });
            s.on("error", function(error) {
                console.log(util.inspect(error));
            });
            s.on("end", function() {
                console.log("****** ERROR: Twitter stream terminated!")
            });
        });
    });
    socket.on("disconnect", function() {
        console.log("socket closed");
    });
});