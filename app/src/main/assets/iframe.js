
function parseiFrame(reverse) {
    
$("iframe").each(function() {
    
    var outer = this.outerHTML;
    var substring = "ads";
    var twitterExcept = "twitter-widget";
    var youtube = "enablejsapi";
    
    if (outer.indexOf(youtube) > 0) {
        $(this).wrap("<div class='fluid-video-wrapper'></div>");
    }
    
    if (outer.indexOf(twitterExcept) < 0) {
                 
            if (outer.indexOf(substring) < 0) {
                var filtered = outer.replace(new RegExp(' src=', 'g'), ' data-src=');
                if (reverse == true) {
                    filtered = outer.replace(new RegExp(' data-src=', 'g'), ' src=');
                }
                this.outerHTML = filtered;
            }

    }

});

}

window.onload = function() {
 //parseiFrame(true);
    
};

$(function() {
// parseiFrame(false);
});
