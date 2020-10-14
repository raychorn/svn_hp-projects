// Copyright 2011 Hewlett-Packard Development Company, L.P.
//
// Hewlett-Packard and the Hewlett-Packard logo are trademarks of
// Hewlett-Packard Development Company, L.P. in the U.S. and/or other countries.
//
// Confidential computer software. Valid license from Hewlett-Packard required
// for possession, use or copying. Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and Technical
// Data for Commercial Items are licensed to the U.S. Government under
// vendor's standard commercial license.
//
// Author:
//    Andy Yates
//

function show_tool_menu(eventObject)
{        
    $('.tool_menu').hide();
    
    var p = $(this).offset();       
    var menu = $('#'+this.id+'_menu');
    menu.css("left", p.left).css("top", p.top-menu.outerHeight()).show();
}

function hide_tool_menu(eventObject)
{
    $(this).hide();
}   

function is_menu_authorized(user_role, action)
{
    if (typeof action.web_client_only != 'undefined' && action.web_client_only == true)
    {
        return false;
    }
    if(user_role == undefined)
    {
        return false;
    }
    if(action.role != undefined)
    {
        if(user_role == action.role)
        {
            return true;
        }
        return false;
    }
    if(action.role_not != undefined)
    {
        if(user_role == action.role_not)
        {
            return false;
        }
        return true;
    }
    
    return true;
}

function compareBoxes(a, b)
{   
    if(a.sort_order == undefined)a.sort_order = 99;
    if(b.sort_order == undefined)b.sort_order = 99;
    return a.sort_order - b.sort_order;
}

$(document).ready(function() {
    /*
	$("#footer_left_template" ).tmpl().appendTo( "#footer-left" );	
    $('.tool_menu').hide().mouseleave(hide_tool_menu);
    $('.tool_icon').mouseover(show_tool_menu);
    */
});