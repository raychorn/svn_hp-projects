/*
 * Simple helper functions
 */
function isArray(obj) {
	return obj && !(obj.propertyIsEnumerable('length')) && typeof obj === 'object' && typeof obj.length === 'number';
}

function isObject(obj) {
	return ((typeof(obj) == "object") || (typeof(obj) == "function"));
}

function yn(val) {
	return val ? "Yes" : "No";
}

function alertobj(obj) {
        var s = "";
        for(var i in obj) {
                s += i+":  "+obj[i]+"\n";
        }
        alert(s);
}

function addElement(obj, elem) {
	// Get around an IE bug rendering objects created in JS
	if (document.all) {
		var d = document.createElement("div");
		d.appendChild(elem);
		obj.innerHTML += d.innerHTML;
	} else {
		obj.appendChild(elem);
	}
	return obj;
}

function comboBox(id, items, selected) {
	var cb = document.createElement('select');
	cb.id = id;
	cb.className = "VSphereSelect";
	for(var i=0; i<items.length; i++) {
        var opt= new Option(items[i][1], items[i][0]);
        if (document.all)
            cb.add(opt)
        else
            cb.options[cb.options.length] = opt;
		if (selected == items[i][0])
			cb.selectedIndex = i;
	}
	return cb
}

function autobind(obj, path)
{
        var el, val;
        if (path == undefined)
                path = "";

        for(var prop in obj) {
                val = obj[prop];
                if (typeof(val) == "object") {
                        autobind(val, path+prop+".");
                } else {
                        el = document.getElementById(path+prop);
                        if (el) el.value = val;
                }
        }
}

function jobject(path, namelist) {
        var k =  new Object();
        var name;
        for(var i=0; i<namelist.length; i++) {
            name = namelist[i];
            var el = document.getElementById(path+'.'+name);
            if (el) {
                k[name] = el.value;
            }
        }
        return k;
}

function rand() {
    return Math.floor(Math.random()*1000000);
}
function randqs() {
    return "&rand="+rand();
}

function linkout(url) {
    window.open(url+window.location.search);
}

function get_by_key(objlist, key) {
    for(var i=0; i<objlist.length; i++) {
        if (objlist[i].key == key)
            return objlist[i];
    }
    return null;
}

// Extend jQuery to have a postJSON method that takes an object
try
{
	$.postJSON = function(url, obj, callback) {
	        return $.post(url, JSON.stringify(obj), callback, "json");
	}
}
catch(e)
{
	
}

// Extend jQuery to have a deleteJSON method.
try{
    $.deleteJSON = function(url, obj, callback){
        return $.ajax({type:"DELETE", url:url, data:obj, success:callback, dataType:"json"});
    }
} catch (e){}

function setTextBoxEnabled(id, enabled){
    //I'm not sure why, but jquery doesn't work on some pages.
    //We'll have to do it the old fashioned way.
    //var textbox = $('#'+id);
    var textbox = document.getElementById(id);
    try{
        if (textbox){
            if (enabled){
               textbox.removeAttribute('disabled');
               textbox.className = 'VSphereTextBox';
            } else {
                textbox.setAttribute('disabled', 'true');
                textbox.className = 'VSphereTextBoxDisabled';
            }
        }
    }catch (e){}
}

/*
 * This function gets an object containing all query string parameters.
 * This can be called by a flex application to get the login information required
 * for the JSON interface.
*/
function getQueryParameters(){
    var query = window.location.search;
    var queryObj = new Object();
    var queryArray = query.split("&");
    for (var param in queryArray){
        paramArray = queryArray[param].split("=");
        if (paramArray && paramArray.length == 2){
            var key = paramArray[0].substring(paramArray[0].indexOf('?')+1);
            queryObj[key] = paramArray[1];
        }
    }
    return queryObj;
}

function isValidIPAddress(address){

    var ipPattern = /^([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$/;
    var ipArray = address.match(ipPattern);

    return (ipArray != null);
}

function doPaste(text_id){
    try{
        var val = window.clipboardData.getData("Text");
        $("#"+text_id).val(val);
    } catch (e){}
}

function doCopy(text_id){
    try{
        var val = $("#" + text_id).val();
        window.clipboardData.setData("Text", val);
    } catch (e){}
}

function statusFormatter(cellValue){
    switch (cellValue){
        case "OK":
            return '<img src="/static/img/icons/green_check_large.png" />&nbsp;' + cellValue;
        case "WARNING":
        case "DEGRADED":
            return '<img src="/static/img/icons/orange_triangle_large.png" />&nbsp;' + cellValue;
        case "FAILED":
        case "ERROR":
            return '<img src="/static/img/icons/red_stop_large.png" />&nbsp;' + cellValue;
        case "INFORMATION":
            return '<img src="/static/img/icons/white_info_large.png" />&nbsp;' + cellValue;
        default:
            return cellValue;
    }
}

function getStatusIconPath(status){
    switch (status){
        case "OK":
            return '/static/img/icons/green_check_large.png';
        case "WARNING":
            return '/static/img/icons/orange_triangle_large.png' ;
        case "ERROR":
            return '/static/img/icons/red_stop_large.png';
        case "INFORMATION":
            return '/static/img/icons/white_info_large.png' ;
        default:
            return '/static/img/icons/unknown.png' ;
    }
}

function launch_help(elem)
{
    window.open('/static/webhelp/nc_content/index.html#' + elem.id+".html");
}
// vim: set ts=4 sts=4 sw=4:
