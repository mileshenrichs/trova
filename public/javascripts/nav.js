/**
 * Created by Henrichs on 12/2/2017.
 * Defines navigation behavior for find page
 */

var navIcon = $("#nav-icon");
var dropdown = $("#dropdown-nav");
var mouseOnNavIcon = false;
var mouseInNav = false;

$(dropdown).on('mouseover', function () {
   mouseInNav = true;
});

$(dropdown).on('mouseleave', function () {
   mouseInNav = false;
    $(dropdown).animate({top: "-300px"}, 500);
});

$(navIcon).on('mouseover', function () {
    mouseOnNavIcon = true;
   $(this).attr('src', '../../../public/images/menu-icon-color.png');
   $(dropdown).animate({top: "65px"}, 500);
});

$(navIcon).on('mouseout', function () {
    mouseOnNavIcon = false;
    $(this).attr('src', '../../../public/images/menu-icon.png');
    setTimeout(function () {
        if(!mouseInNav) {
            $(dropdown).animate({top: "-300px"}, 500);
        }
    }, 300);
});
