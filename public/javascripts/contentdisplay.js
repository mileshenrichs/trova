/**
 * Created by Henrichs on 11/15/2017.
 * JS for content filtering, hide/show functionality
 */

var filters = [];
var filtersOn = [];
var postCounts = [];

var instaPosts = Array.from(document.getElementsByClassName('instagram'));
var tweets = Array.from(document.getElementsByClassName('twitter'));
var youtubeVids = Array.from(document.getElementsByClassName('youtube'));

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
                    $(post).slideDown(800);
                });
                break;
            case 1:
                tweets.forEach(function (tweet) {
                    $(tweet).slideDown(800);
                });
                break;
            default:
                youtubeVids.forEach(function (video) {
                    $(video).slideDown(800);
                });
        }
    }
}

function updatePostCounts() {
    postCounts[0].getElementsByTagName('b')[0].innerHTML = instaPosts.length;
    postCounts[1].getElementsByTagName('b')[0].innerHTML = tweets.length;
    postCounts[2].getElementsByTagName('b')[0].innerHTML = youtubeVids.length;
}