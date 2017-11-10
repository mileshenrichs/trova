/**
 * Created by Henrichs on 10/26/2017.
 * Calculates and sets height of each Tweet container on feed page
 */

var tweetFrames;
var containers;
var mediaIndices = [];

window.onload = function() {
    tweetFrames = document.getElementsByClassName('twitter-tweet twitter-tweet-rendered');
    containers = document.getElementsByClassName('twitter');
    setTweetContainerHeights(containers, tweetFrames);
};

function setTweetContainerHeights(containers, tweetFrames) {
    setTimeout(function() { // give iframes time to render
        for(var i = 0; i < containers.length; i++) {
            var iframeDoc = (tweetFrames[i].contentDocument) ? tweetFrames[i].contentDocument : tweetFrames[i].contentWindow.document;
            var media = iframeDoc.getElementsByTagName('article');
            if(media.length > 0) { // tweet contains media attachment
                var width = media[0].offsetWidth;
                var height = media[0].offsetHeight;
                debugger;
                if(height > width) {
                    containers[i].getElementsByClassName('tweet-center')[0].className += '-tall'; // shrink tweet
                    mediaIndices.push(i);
                    debugger;
                }
            }
            containers[i].style.height = (tweetFrames[i].offsetHeight + 40).toString() + "px";
        }
    }, 100);
    setTimeout(function () {
        mediaIndices.forEach(function (index) {
            containers[index].style.height = (tweetFrames[index].offsetHeight + 40).toString() + "px";
        })
    }, 300);
}

