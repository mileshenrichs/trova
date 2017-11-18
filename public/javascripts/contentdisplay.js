/**
 * Created by Henrichs on 11/15/2017.
 * JS for content filtering, hide/show functionality
 */

const INITIAL_DISPLAY_AMT = 6; // amount of content displayed prior to 'Show More' click

var filters = [];
var filtersOn = [];
var postCounts = [];

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

// initial content cataloging
for(var z = 0; z < INITIAL_DISPLAY_AMT; z++) {
    if(allContent[z].className.includes('instagram')) {
        instaIndex++;
    } else if(allContent[z].className.includes('twitter')) {
        tweetIndex++;
    } else if(allContent[z].className.includes('youtube')) {
        youtubeVidIndex++;
    }
}

var spans = Array.from(document.getElementById('filter-box').getElementsByTagName('span'));
spans.forEach(function (spn) {
    if(spans.indexOf(spn) % 2 === 0) {
        filters.push(spn);
        filtersOn.push(true);
        spn.addEventListener('click', toggleFilter);
    } else {
        postCounts.push(spn);
    }
});

updatePostCounts();

function initializeDisplay() {
    allContent.forEach(function (c) {
        if(allContent.indexOf(c) > INITIAL_DISPLAY_AMT) {
            $(c).hide();
        }
    });
}

var showMoreButton = document.getElementsByClassName('show-more')[0];
showMoreButton.addEventListener('click', showMore);

function toggleFilter() {
    var filtersIndex = filters.indexOf(this);
    if(filtersOn[filtersIndex]) { // toggled on
        filtersOn[filtersIndex] = false;
        this.className = 'filtered-out';
        this.title = 'Show' + this.title.substring(4);
        switch(filtersIndex) {
            case 0:
                instaPosts.forEach(function (post) {
                    $(post).slideUp(800);
                });
                break;
            case 1:
                tweets.forEach(function (tweet) {
                    $(tweet).slideUp(800);
                });
                break;
            default:
                youtubeVids.forEach(function (video) {
                    $(video).slideUp(800);
                });
        }

    } else { // toggled off
        filtersOn[filtersIndex] = true;
        this.className = '';
        this.title = 'Hide' + this.title.substring(4);
        switch(filtersIndex) {
            case 0:
                instaPosts.forEach(function (post) {
                    if(instaPosts.indexOf(post) <= instaIndex) {
                        $(post).slideDown(800);
                    }
                });
                break;
            case 1:
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
    postCounts[0].getElementsByTagName('b')[0].innerHTML = instaPosts.length;
    postCounts[1].getElementsByTagName('b')[0].innerHTML = tweets.length;
    postCounts[2].getElementsByTagName('b')[0].innerHTML = youtubeVids.length;
}

function showMore() {
    var i = 0;
    startIndex = 0;
    while(startIndex === 0 && i < allContent.length) {
        if(allContent[i].style.display === 'none') startIndex = i;
        i++;
    }

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

    // show and set height for INITIAL_DISPLAY_AMT more content rows
    var j = startIndex;
    while(j < startIndex + INITIAL_DISPLAY_AMT && j < allContent.length) {
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
        if(j + 1 === allContent.length) {
            $(showMoreButton).slideUp();
        }
        $(allContent[j]).show();
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