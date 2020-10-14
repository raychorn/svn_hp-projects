cc=0
var openbooks = new Array()
openbooks["firstbookicon"]="open"



function showsubtoc(elmnt)
{
document.getElementById(elmnt).style.display="block"
}
function hidemenu(elmnt)
{
document.getElementById(elmnt).style.display="none"
}

function changetocimage(icon)
{
var bi = document.getElementById(icon).id
var f = openbooks[bi];
var ext = ".jpg"
// 0 means closed icon and 1 means open icon
switch (f)
{
case 0:
document.getElementById(icon).src="images/expanded_open_book_icon" + ext
openbooks[bi] = 1
break
case 1:
document.getElementById(icon).src="images/collasped_closed_book_icon" + ext
openbooks[bi] = 0
break
default:
document.getElementById(icon).src="images/expanded_open_book_icon" + ext
openbooks[bi] = 1
break
}
}

function dynamicmenu(element,icon)
{
var bi = document.getElementById(icon).id
var f = openbooks[bi];
var ext = ".jpg"
// 0 means closed icon and 1 means open icon
switch (f)
{
case 0:
document.getElementById(icon).src="images/expanded_open_book_icon" + ext
document.getElementById(element).style.display="block"
openbooks[bi] = 1
break
case 1:
document.getElementById(icon).src="images/collasped_closed_book_icon" + ext
document.getElementById(element).style.display="none"
openbooks[bi] = 0
break
default:
document.getElementById(icon).src="images/expanded_open_book_icon" + ext
document.getElementById(element).style.display="block"
openbooks[bi] = 1
break
}


}

function showtoclocation(element,icon,link,ancestor)
{


}


function cleartoclocation(link,element)
{

}

function highlightString() { 
 
// When TOC is loaded turn on highlighting on search strings from last search
if (window.find) {  // Chrome and Firefox browsers
  setCallingURL();
  if (window.getSelection) { // Using nested if because ampersands choke indexer
    var index = $.jStorage.index();  // array holding search strings
    try {
      document.designMode = "on";
      var sel = window.getSelection();
      numberOfSearchStrings = index.length -1;
      for (var i = numberOfSearchStrings; i >= 0; i--) {
        sel.collapse(document.body, 0);
        while (window.find($.jStorage.get(index[i]), false, false, false, false, false, false)) {
          document.execCommand("HiliteColor", false, "yellow");
          sel.collapseToEnd();
        }
        sel.collapse(document.body, 0);
      }
      javascript:scroll(0,0);
      document.designMode = "off";
    }
    catch(err) {
    }
  }
  getCallingURL();

} else if (document.body.createTextRange) { // IE browsers
    if (document.cookie) { // using nested if because ampersands choke indexer
    if (document.cookie != '') { // check cookies for search strings
      var split = document.cookie.split('; ');
      var cookies = {};
      for (var i = 0; i != split.length; i++) { // Get individual cookies from array
        var name_value = split[i].split("=");
        cookies[decodeURIComponent(name_value[0])] = decodeURIComponent(name_value[1]);
      }
      // Select the body element content as the text range
      var docBody = document.body.createTextRange();
      // For each search string hit set background color to yellow
      for(var name in cookies) {
        if (name.indexOf("search-word") != -1) {  // All search string cookies begin with search-word
          var t = docBody.duplicate();            // Use a copy of the text range (html body) for each pass
          while (t.findText(cookies[name])) {
            t.execCommand("BackColor", false, "yellow");
            t.collapse(false);
          }
        }
      }
    }
    }
}

}

function loaddynamicTOC(topic,icon,link,ancestor) {
  highlightString();
  if  (frames.name!='mainhelp_pane'){
  }
  else {
   window.parent.frames['contentspane'].frames['nav-main'].showtoclocation(topic,icon,link,ancestor);
  }
}

function clearbgcolortoc(link,element)
{
  try {
    window.parent.frames['contentspane'].frames['nav-main'].cleartoclocation(link,element);
  }
  catch(err) {
  }
}

function closeHelp() {
myWindow = window.top;

// window.parent.frames['contentspane'].close();
// myWindow.close();
window.parent.closehelpframe();
}

// Remove previously saved search hit strings 
function flushSearch() {
    // Remove any search strings from previous search by flushing storage (Chrome,Firefox) or
    // setting cookies to null value (IE)
    var browser = navigator.userAgent;
    if (browser.indexOf('MSIE') == -1) {
      $.jStorage.flush(); // Chrome and Firefox search strings use jstorage and json
    }
    // IE search strings are in cookies
    if (document.cookie && document.cookie != '') { // check cookies for search strings
      var split = document.cookie.split('; ');
      var cookies = {};
      for (var i = 0; i < split.length; i++) {
        var name_value = split[i].split("=");
        cookies[decodeURIComponent(name_value[0])] = decodeURIComponent(name_value[1]);
      }
      // For each previously-existing search string cookie set value to null
      for(var name in cookies) {
        if (name.indexOf("search-word") != -1) {  // Search string cookies begin with "search-word"
          $.cookie(name, null);
        }
      }
    }
}

// Save current URL 
function setCallingURL() {
  $.jStorage.set("previousUrl", window.location.href); 
}

// Scroll to saved URL 
function getCallingURL() {
  var prevUrl = $.jStorage.get("previousUrl"); 
  var start = prevUrl.indexOf("#");
  start++;
  var prevUrlId = prevUrl.slice(start);
  var e = document.getElementById(prevUrlId);
  if (e) {
    e.scrollIntoView();
  }
}
