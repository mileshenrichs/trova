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
    $(navIcon).attr('src', '../../../public/images/menu-icon.png');
});

$(navIcon).on('mouseover', function () {
    mouseOnNavIcon = true;
   $(this).attr('src', '../../../public/images/menu-icon-color.png');
   $(dropdown).animate({top: "65px"}, 500);
});

$(navIcon).on('mouseout', function () {
    mouseOnNavIcon = false;
    setTimeout(function () {
        if(!mouseInNav) {
            $(this).attr('src', '../../../public/images/menu-icon.png');
            $(dropdown).animate({top: "-300px"}, 500);
        }
    }, 300);
});
