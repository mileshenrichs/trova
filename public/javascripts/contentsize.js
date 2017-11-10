/**
 * Created by Henrichs on 10/26/2017.
 * Calculates and sets height of each Tweet container on feed page
 */

var iframes;
var containers;
var mediaIndices = [];

window.onload = function() {
    iframes = document.getElementsByTagName('iframe');
    containers = document.getElementsByClassName('twitter');
    setTweetContainerHeights(containers, iframes);
};

function setTweetContainerHeights(containers, iframes) {
    setTimeout(function() { // give iframes time to render
        for(var i = 0; i < containers.length; i++) {
            var iframeDoc = (iframes[i].contentDocument) ? iframes[i].contentDocument : iframes[i].contentWindow.document;
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
            containers[i].style.height = (iframes[i].offsetHeight + 40).toString() + "px";
        }
    }, 100);
    setTimeout(function () {
        mediaIndices.forEach(function (index) {
            containers[index].style.height = (iframes[index].offsetHeight + 40).toString() + "px";
        })
    }, 300);
}

