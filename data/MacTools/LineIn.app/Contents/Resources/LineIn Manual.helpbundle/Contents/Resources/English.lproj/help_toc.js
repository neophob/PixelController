//*** Do Not Localize ***
// This creates an expanding TOC based on the structure of the HTML.
// 
// For this to work, you need to identify various things in the TOC by 
// adding a toc attribute to elements.  The possible values for a toc 
// attribute are:
//
//  section: Contains the disclosure triangle, chapter link, and 
//           the collapsible area that has sub-links for the chapter.
//           Typically this is a div, though you can make it any element.
//
//  collapsible: Contains sub-links for a chapter, and is the portion to
//           expand and collapse. Typically this is a span, though you
//           can make it any element.
//
//  section_link: A link that should collapse/expand the section it is a part of.
//           The section is closed/opened on clicking this link or the triangle.
//           Typically both the link for the triangle and the link for the text
//           next to the triangle are set to this.
//
// In addition to the toc attributes, if you want a disclosure triangle you 
// need to provide img elements with 'open' and 'closed' attributes indicating
// what image to display in each case.
//
// Here is the typical structure of a toc section (additional formatting/table tags
// can be placed throughout as needed):
//
// <div toc="section">
//      ...<a toc="section_link" href="content.html"><img src="open.gif" open="open.gif" closed="closed.gif"/></a>...
//      ...<a toc="section_link" href="content.html">Bundles</a>...
//      ...<span toc="collapsible">collapsible content</span>...
// </div>

var from_toc_frame = 0;     // Track whether request originated from TOC frame to deal with a Mac IE bug.
var isJavaScriptTOC = 0;    // If this looks like a JavaScript TOC this is set to 1.
var ignore_page_load = 0;   // If user clicks TOC link to load page, don't do display stuff to TOC that's done for next/previous buttons, etc.
var lastSelectedItem;       // So we can change the formatting back to normal.
var lastSelectedColor;      // The color it used to be.
var lastSelectedWeight;     // The font weight it used to be


function initialize_toc() {
    // Called on page load to setup the page.    
    set_initial_state();
}

function set_initial_state() {    
    // Set action for all links, expand first chapter and collapse the rest.
    for(var i = 0; i < document.links.length; i++) {
        document.links[i].onmousedown = link_action;
    }
   
    var divs = document.getElementsByTagName("DIV");
    if (divs.length) {
        isJavaScriptTOC = 1;
        from_toc_frame = 1;
                
        for (var i = 0; i < divs.length; i++) {

			if( divs[i].getAttribute('expanded') == "true" ) {
                expand(divs[i]);
            } else {
                collapse(divs[i]);
            }
            
        }
        
        from_toc_frame = 0;
    }    
}

function link_action() {
    // Called by a link when clicked.
    // If this is a regular link, open the section if it's closed.
    // If it contains a disclosure triangle, toggle the section.
    
    // Since the page load is being driven by the user clicking on the TOC,
    // we don't want to go through the display stuff we do if the page is
    // being loaded by clicking on links in the content.
    ignore_page_load = 1;
    
    var div = toc_section_parent(this);
    
    if (div && div.className) {
        from_toc_frame = 1;
        // If it's a section_link, toggle the state of this section.
        if (this.getAttribute("toc") == "section_link" || this.parentNode.getAttribute("toc") == "section_link") {
            toggle(div);
        }
        
        from_toc_frame = 0; 
    }
}

function toggle(div) {
    // Toggle the div's disclosure status.
    if (div.className) {
        if (div.className.indexOf("jtoc_open") != -1) {
            collapse(div);
        } else if (div.className == "jtoc_closed") {
        
        	// if topics toc, expand_only
        	if ( this.is_topics_toc(div) ) {
        		expand_only(div);
        	} else {
            	expand(div);
        	}
        }
    }
}

function expand_only(div) {
    // Collapse all but the specified section.
    // If div is null, everything is collapsed.
    var divs = document.getElementsByTagName("DIV");
    if (divs.length) {
        for (var i = 0; i < divs.length; i++) {
            var current = divs[i];
 
            if (div && current == div) {
                expand(current);
            } else {
                collapse(current);
            }
        }
    }
}

function expand(div) {
    if (isNetscape()) {
        // Netscape can't deal with setting display to none after initial page load,
        // so skip expanding/collapsing.
        return;
    }
    
    if (div.className.indexOf("jtoc_open") == -1) {
        
        div.className = "jtoc_open";
                
        var child = collapsible_child(div);
        if (child) { child.style.display = ""; }
        
        // Skip image changing for Mac IE since some frame-related bug
        // messes that up when things originated from another frame.
        if (from_toc_frame || !isMacIE()) {
            var image = disclosure_image(div);
            if (image) { image.src = image.getAttribute("open"); }
        }
    }
	
	// Check each section to set the borders according to whether the previous/next section is open and such.
    var divs = document.getElementsByTagName("DIV");
    if (divs.length) {
		
    	for (var i = 0; i < divs.length; i++) {
			
    		var current_div = divs[i];
    		
    		if (current_div.className && current_div.className.indexOf("jtoc_open") != -1 ) {
    			
				var prev = prev_div_sibling(current_div);
				var next_sib = next_div_sibling(current_div);
				
				var gets_top = 0;
				var gets_bottom = 0;
				
				
				
				if ( prev && prev.className && prev.className.indexOf("jtoc_open_bottom_line") == -1 && prev.className.indexOf("jtoc_open_both_lines") == -1) {
					gets_top = 1;
				}
				
				if ( next_sib && next_sib.className && next_sib.className.indexOf("jtoc_open_top_line") == -1 && next_sib.className.indexOf("jtoc_open_both_lines") == -1) {
					gets_bottom = 1;
				}
				
				if ( i == divs.length - 1 ) {
					gets_bottom = 1;
				}
				
				// If I'm the first div
				if ( prev && !prev.className ) {
					gets_top = 1;
				}
				
				// If I'm the last div
				if ( next_sib && !next_sib.className ) {
					gets_bottom = 1;
				}
				
				if ( gets_top == 1 && gets_bottom == 1) {
					current_div.className = "jtoc_open_both_lines";
				} else if ( gets_top == 1) {
					current_div.className = "jtoc_open_top_line";
				} else if ( gets_bottom == 1) {
					current_div.className = "jtoc_open_bottom_line";
				} else {
					current_div.className = "jtoc_open";
				}
			}
		}
	}	
}

function next_div_sibling(sibling) {
		
	var nextS = sibling.nextSibling;
	if ( nextS ) {
    var sibling2 = nextS.nextSibling;
    return sibling2;
	}
    
}

function prev_div_sibling(sibling) {
		
	var prevS = sibling.previousSibling;
	if ( prevS ) {
	
    var sibling2 = prevS.previousSibling;

    return sibling2;
	}
}

function collapse(div) {
    if (isNetscape()) {
        // Netscape can't deal with setting display to none after initial page load,
        // so skip expanding/collapsing.
        return;
    }

    if (div.className != "jtoc_closed") {
        div.className = "jtoc_closed";
        
        var child = collapsible_child(div);
        if (child) { child.style.display = "none"; }
        
        // Skip image changing for Mac IE since some frame-related bug
        // messes that up when things originated from another frame.
        if (from_toc_frame || !isMacIE()) {
            var image = disclosure_image(div);
            if (image) { image.src = image.getAttribute("closed"); }
        }
    }
}

function toc_section_parent(element) {
    // Find the first parent with a toc attribute of "section".
    var toc_parent = element.parentNode;
    
    while (toc_parent) {
        if (toc_parent.tagName == "BODY") {
            // Have to stop checking here, because the HTML element above this doesn't have a getAttribute() function
			// and will crash the next check.
            return;
        }
        
        if (toc_parent.getAttribute("TOC") == "section") {
            return toc_parent;
        }
        
        toc_parent = toc_parent.parentNode;
    }
}

function is_topics_toc(element) {
    // Return if my parent div's id attribute is "topics"
    var toc_parent = element.parentNode;
    
    while (toc_parent && toc_parent.getAttribute("id") && toc_parent.getAttribute("id") != "topics") {

        toc_parent = toc_parent.parentNode;
    }
    
    if (toc_parent.getAttribute("id") == "topics") {
        return toc_parent;
    }
}

function disclosure_image(div) {
    var images = div.getElementsByTagName("IMG");
    if (images.length) {
        for (var i = 0; i < images.length; i++) {
            var image = images[i];
            var open = image.getAttribute("open")
            if (open) {
                return image;
            }
        }
    }
}

function collapsible_child(parent) {
    // Get the first descendant with toc="collapsible".
    // To get all descendants, ask for *.
    var children = parent.getElementsByTagName("*");
    if (children.length) {
        for (var i = 0; i < children.length; i++) {
            var child = children[i];
            if (child.getAttribute("toc") == "collapsible") {
                return child;
            }
        }
    }
}

function disclosure_triangle() {
    // The mapping table sets disclosure triangles to call this.
    var parent = toc_section_parent(this);
    if (parent) { toggle(parent); }
    return false;
}

function selected_div(page_location) {
    // Called by a page on loading, so we can track what page is displayed.
    if (isJavaScriptTOC) {
        var page_suffix = path_suffix(page_location.pathname);
        var all_links = document.links;
        
        for(var i = 0; i < all_links.length; i++) {
            var anchor = all_links[i];
            var anchor_suffix = path_suffix(anchor.getAttribute("HREF"));
            
            if (page_suffix == anchor_suffix) {
                return toc_section_parent(anchor);
            }
        }
    }
}

function page_loaded(page_location) {
    // Called by a page on loading, so we can track what page is displayed.
    // If there is a link that points to the loaded page, make sure that TOC
    // section is disclosed and turn that link black.
    if (isJavaScriptTOC) {
        var page_suffix = last_path_component(page_location.pathname);
        var all_links = document.links;
		
        for(var i = 0; i < all_links.length; i++) {
            var anchor = all_links[i];
            var anchor_suffix = last_path_component(anchor.getAttribute("HREF"));
            
            if (page_suffix == anchor_suffix) {
                if (lastSelectedItem) { lastSelectedItem.style.color = lastSelectedColor; }

                lastSelectedItem = anchor;
                lastSelectedColor = anchor.style.color;
                lastSelectedWeight = anchor.style.fontWeight;
                
                anchor.style.color = "black";
                anchor.style.fontWeight = "bold";

                // If this page load didn't come from the TOC,
                // get the parent section, expand it and close others.
                if (ignore_page_load) {
                    ignore_page_load = 0;
                } else {
                    var parent = toc_section_parent(anchor);
                    expand_only(parent);
                }
                break;
            }
        }
    }
}

function previous_link(page_location) {
    // For Topic pages, dynamically provide next/previous links based on TOC.
    // The page calls this to fill in its Previous Link.
    var page_suffix = last_path_component(page_location.pathname);
    var all_links = document.links;
    
    if (all_links.length < 2) {
        return;
    }
    
    for(var i = 0; i < all_links.length; i++) {
        var anchor = all_links[i];
        var anchor_suffix = last_path_component(anchor.getAttribute("HREF"));
        
        if (page_suffix == anchor_suffix) {
            if (i == 0) {
                return;
            } else {
                var previous_link = all_links[i-1];
                return previous_link;
            }
        }
    }    
}

function next_link(page_location) {
    // For Topic pages, dynamically provide next/previous links based on TOC.
    // The page calls this to fill in its Next Link.
    var page_suffix = last_path_component(page_location.pathname);
    var all_links = document.links;
    
    if (all_links.length < 2) {
        return;
    }
    
    for(var i = 0; i < (all_links.length - 1); i++) {
        var anchor = all_links[i];
        var anchor_suffix = last_path_component(anchor.getAttribute("HREF"));
        
        if (page_suffix == anchor_suffix) {
            var next_link = all_links[i+1];
            
            // So we don't include non-article links in the chain, only include links whose target is this content pane.
            var target = next_link.getAttribute("TARGET");
            if (target && target == "content") {
                return next_link;
            } else {
                return;
            }
        }
    }    
}

function path_suffix(path) {
    // Returns last two path segments as a string: "leaf_dir/filename.html".
    var leaf = "";
    var parent = "";

    // First split on # to get rid of any anchor at end of path.
    var path_array = path.split('#');
    path = path_array[0];
        
    // Now split apart the path.
    // 3379110: Was using array.pop() function, but it failed in Mac IE.
    path_array = path.split('/');
    var length = path_array.length;
    
    if (length) { leaf = path_array[length - 1]; }
    if (length > 1) { parent = path_array[length - 2]; }
    if (parent && leaf) {
    	return parent + '/' + leaf;
	} else {
		
		return leaf;
	}
}

function last_path_component(path) {
    // Returns the last path segments as a string: "filename.html" from "leaf_dir/filename.html".
    var leaf = "";
    var parent = "";
    
    // First split on # to get rid of any anchor at end of path.
    var path_array = path.split('#');
    path = path_array[0];
        
    // Now split apart the path.
    // 3379110: Was using array.pop() function, but it failed in Mac IE.
    path_array = path.split('/');
    var length = path_array.length;
    
    if (length) { leaf = path_array[length - 1]; }
	return leaf;

}

function isMacIE() {
    if (navigator.appName == "Microsoft Internet Explorer") {
        // There are some limitations on MacIE so look for that.
        var regex = /Macintosh/;
        if (regex.test(navigator.appVersion)) {
            return 1;
        }
    }
    
    return 0;
}

function isNetscape() {
    // WebKit shows up as Netscape but doesn't have the same problems, so exclude that case.
  
    
    return 0;
}