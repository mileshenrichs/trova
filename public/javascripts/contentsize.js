/**
 * Created by Henrichs on 10/26/2017.
 * Calculates and sets heights of each Tweet and Insta post containers on page
 */

var tweetFrames;
var tweetContainers;
var tweetContainerHeights = [];
var mediaIndices = [];

var instaFrames;
var instaContainers;
var instaContainerHeights = [];

var fbFrames;
var fbContainers;
var fbContainerHeights = [];
var fbFrameHeights = [];


window.onload = function() {
    tweetFrames = document.getElementsByClassName('twitter-tweet twitter-tweet-rendered');
    tweetContainers = document.getElementsByClassName('twitter');
    setTweetContainerHeights();

    instaContainers = document.getElementsByClassName('instagram');
    instaFrames = document.getElementsByClassName('instagram-media instagram-media-rendered');
    var findFrames = setInterval(function () {
        instaFrames = document.getElementsByClassName('instagram-media instagram-media-rendered');
        if(instaFrames.length === instaContainers.length) {
            clearInterval(findFrames);
            setInstaContainerHeights();
        }
    }, 1000);

    fbContainers = document.getElementsByClassName('facebook');
    var findFbFrames = setInterval(function () {
        fbFrames = Array.from(document.getElementsByClassName('fb-post fb_iframe_widget'));
        fbFrameHeights = [];
        fbFrames.forEach(function (frame) {
            fbFrameHeights.push(frame.offsetHeight);
        });
        if(fbFrames.length === fbContainers.length && !fbFrameHeights.includes(0) && !fbFrameHeights.includes(20)) {
            clearInterval(findFbFrames);
            setFacebookContainerHeights();
        }
    }, 1000);
};

function setTweetContainerHeights() {
    setTimeout(function() { // give iframes time to render
        for(var i = 0; i < tweetContainers.length; i++) {
            var iframeDoc = (tweetFrames[i].contentDocument) ? tweetFrames[i].contentDocument : tweetFrames[i].contentWindow.document;
            var media = iframeDoc.getElementsByTagName('article');
            if(media.length > 0) { // tweet contains media attachment
                var width = media[0].offsetWidth;
                var height = media[0].offsetHeight;
                if(height > width) {
                    tweetContainers[i].getElementsByClassName('tweet-center')[0].className += '-tall'; // shrink tweet
                    mediaIndices.push(i);
                }
            }
            tweetContainers[i].style.height = (tweetFrames[i].offsetHeight + 40).toString() + 'px';
            tweetContainerHeights.push((tweetFrames[i].offsetHeight + 40).toString() + 'px');
        }
    }, 100);
    setTimeout(function () {
        mediaIndices.forEach(function (index) {
            tweetContainers[index].style.height = (tweetFrames[index].offsetHeight + 40).toString() + 'px';
            tweetContainerHeights[index] = (tweetFrames[index].offsetHeight + 40).toString() + 'px';
        });
    }, 300);
}

function setInstaContainerHeights() {
    for(var i = 0; i < instaContainers.length; i++) {
        instaContainers[i].style.height = (instaFrames[i].offsetHeight + 40).toString() + 'px';
        instaContainerHeights.push((instaFrames[i].offsetHeight + 40).toString() + 'px');
    }
    // initializeDisplay();
}

function setFacebookContainerHeights() {
    fbFrames.forEach(function (frm) {
        console.log(frm.offsetHeight.toString());
    });
    var frameBgs = Array.from(document.getElementsByClassName('post-white-bg'));
    for(var i = 0; i < fbContainers.length; i++) {
        fbContainers[i].style.height = (fbFrames[i].offsetHeight + 40).toString() + 'px';
        frameBgs[i].style.height = fbFrames[i].offsetHeight.toString() + 'px';
        frameBgs[i].style.visibility = 'visible';
        fbContainerHeights.push((fbFrames[i].offsetHeight + 40).toString() + 'px');
    }
}

