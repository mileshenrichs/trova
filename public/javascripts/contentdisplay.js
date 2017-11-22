/**
 * Created by Henrichs on 11/15/2017.
 * JS for content filtering, hide/show functionality
 */

const DISPLAY_AMT = 6; // amount of content displayed prior to 'Show More' click

var filters = new Map(); // spans representing filter buttons
filters.set('insta', null);
filters.set('twitter', null);
filters.set('youtube', null);

var filtersOn = new Map(); // booleans indicating whether content is currently visible
filtersOn.set('insta', false);
filtersOn.set('twitter', false);
filtersOn.set('youtube', false);

var postCounts = new Map(); // spans representing number of posts from each media source
postCounts.set('insta-posts-amt', null);
postCounts.set('tweets-amt', null);
postCounts.set('youtube-vids-amt', null);

var instaPosts = Array.from(document.getElementsByClassName('instagram'));
var tweets = Array.from(document.getElementsByClassName('twitter'));
var youtubeVids = Array.from(document.getElementsByClassName('youtube'));

var allContent = Array.from(document.getElementsByClassName('content-row'));
// index of last content visible from respective sources
var instaIndex = -1;
var tweetIndex = -1;
var youtubeVidIndex = -1;
// index among all content at which to begin displaying more posts
var startIndex = 0;
// index of last content that was hidden by a filter
var lastHiddenIndex = 0;

// initial content cataloging
for(var z = 0; z < DISPLAY_AMT; z++) {
    if(allContent[z].className.includes('instagram')) {
        instaIndex++;
    } else if(allContent[z].className.includes('twitter')) {
        tweetIndex++;
    } else if(allContent[z].className.includes('youtube')) {
        youtubeVidIndex++;
    }
}

// put filter buttons into objects
var spans = Array.from(document.getElementById('filter-box').getElementsByTagName('span'));
spans.forEach(function (spn) {
    if(spans.indexOf(spn) % 2 === 0) {
        filters.set(spn.id, spn);
        filtersOn.set(spn.id, true);
        spn.addEventListener('click', toggleFilter);
    } else {
        postCounts.set(spn.id, spn);
    }
});

updatePostCounts();
checkMissingSocial();

function initializeDisplay() {
    allContent.forEach(function (c) {
        if(allContent.indexOf(c) > DISPLAY_AMT) {
            $(c).hide();
        }
    });
}

var showMoreButton = document.getElementsByClassName('show-more')[0];
showMoreButton.addEventListener('click', showMore);

function toggleFilter() {
    // find lastHiddenIndex prior to hiding stuff
    var currentLastIndex = 0;
    var canModifyIndex = true;
    while(currentLastIndex < allContent.length) {
        if(allContent[currentLastIndex].style.display === 'none' && canModifyIndex) {
            lastHiddenIndex = currentLastIndex;
            canModifyIndex = false;
        }
        if(!canModifyIndex) {
            if(allContent[currentLastIndex].style.display !== 'none') {
                canModifyIndex = true;
            }
        }
        currentLastIndex++;
    }

    if(filtersOn.get(this.id)) { // toggled on (turning off)
        filtersOn.set(this.id, false);
        this.className = 'filtered-out';
        this.title = 'Show' + this.title.substring(4);
        switch(this.id) {
            case 'insta':
                instaPosts.forEach(function (post) {
                    $(post).slideUp(800);
                });
                break;
            case 'twitter':
                tweets.forEach(function (tweet) {
                    $(tweet).slideUp(800);
                });
                break;
            default:
                youtubeVids.forEach(function (video) {
                    $(video).slideUp(800);
                });
        }

    } else { // toggled off (turning back on)
        filtersOn.set(this.id, true);
        this.className = '';
        this.title = 'Hide' + this.title.substring(4);
        switch(this.id) {
            case 'insta':
                instaPosts.forEach(function (post) {
                    if(instaPosts.indexOf(post) <= instaIndex) {
                        $(post).slideDown(800);
                    }
                });
                break;
            case 'twitter':
                tweets.forEach(function (tweet) {
                    if(tweets.indexOf(tweet) <= tweetIndex) {
                        $(tweet).slideDown(800);
                    }
                });
                break;
            default:
                youtubeVids.forEach(function (video) {
                    if(youtubeVids.indexOf(video) <= youtubeVidIndex) {
                        $(video).slideDown(800);
                    }
                });
        }
    }
}

function updatePostCounts() {
    postCounts.get('insta-posts-amt').getElementsByTagName('b')[0].innerHTML = instaPosts.length;
    postCounts.get('tweets-amt').getElementsByTagName('b')[0].innerHTML = tweets.length;
    if(postCounts.get('youtube-vids-amt') !== null) {
        postCounts.get('youtube-vids-amt').getElementsByTagName('b')[0].innerHTML = youtubeVids.length;
    }
}

var showMoreClicked = false;

function showMore() {
    showMoreClicked = true;
    var i = 0;
    startIndex = 0;
    var foundHidden = false;
    while(!foundHidden && i < allContent.length) {
        if(allContent[i].style.display === 'none') {
            startIndex = i;
            foundHidden = true;
        }
        i++;
    }
    startIndex += lastHiddenIndex;

    // catalogue source of each content row up to startIndex
    tweetIndex = -1;
    instaIndex = -1;
    youtubeVidIndex = -1;
    for(var z = 0; z < startIndex; z++) {
        if(allContent[z].className.includes('instagram')) {
            instaIndex++;
        } else if(allContent[z].className.includes('twitter')) {
            tweetIndex++;
        } else if(allContent[z].className.includes('youtube')) {
            youtubeVidIndex++;
        }
    }

    // show and set height for DISPLAY_AMT more content rows
    var j = startIndex;
    while(j < startIndex + DISPLAY_AMT && j < allContent.length) {
        var contentSource = '';
        if(allContent[j].className.includes('instagram')) {
            instaIndex++;
            contentSource = 'insta';
        } else if(allContent[j].className.includes('twitter')) {
            tweetIndex++;
            contentSource = 'twitter';
        } else if(allContent[j].className.includes('youtube')) {
            youtubeVidIndex++;
        }

        // hide show more button after last content is visible
        if(j + 1 === allContent.length) {
            $(showMoreButton).slideUp();
        }

        // show content if not currently filtered
        var contentClass = allContent[j].className.substring(22);
        if(contentClass === 'instagram') contentClass = 'insta';
        if(filtersOn.get(contentClass)) {
            $(allContent[j]).show();
        }

        if(contentSource !== '') {
            if(contentSource === 'insta') {
                allContent[j].style.height = instaContainerHeights[instaIndex];
            } else {
                allContent[j].style.height = tweetContainerHeights[tweetIndex];
            }
        }
        j++;
    }
}

function checkMissingSocial() {
    var missingSocial = [];
    var socialLinks = document.getElementById('social-links');

    // populate missingSocial with missing social media sources
    var instaLink = document.getElementsByClassName('insta-link');
    var twitterLink = document.getElementsByClassName('twitter-link');
    var youtubeLink = document.getElementsByClassName('youtube-link');
    if(instaLink.length === 0) missingSocial.push('insta');
    if(twitterLink.length === 0) missingSocial.push('twitter');
    if(youtubeLink.length === 0) missingSocial.push('youtube');

    // push social links down to fill bottom space
    socialLinks.style.paddingTop = (31 * missingSocial.length).toString() + "px";

    var instaFilterBtn = document.getElementById('insta');
    var twitterFilterBtn = document.getElementById('twitter');
    var youtubeFilterBtn = document.getElementById('youtube');
    if(missingSocial.length === 1) {
        if(missingSocial[0] === 'youtube') {
            instaFilterBtn.style.width = "50%";
            instaFilterBtn.getElementsByTagName('img')[0].style.marginLeft = "18%";
            twitterFilterBtn.getElementsByTagName('img')[0].style.marginLeft = "18%";
            twitterFilterBtn.style.width = "50%";
            twitterFilterBtn.style.borderRight = "none";
            twitterFilterBtn.style.borderRadius = "0 0 6px 0";
        }
    }
}