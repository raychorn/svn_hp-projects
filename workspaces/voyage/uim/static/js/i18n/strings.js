
var Strings = {

    loadStrings: function(){
        try{
            result = $.ajax({url:'/strings/'+window.location.search, async:false, dataType: 'script'});
        } catch (e){
        }
    }

}
Strings.loadStrings();

//function get_str(key, args){
function get_str(){
    if (arguments.length == 0){
        return "";
    }

    var key = arguments[0];
    args = Array.prototype.slice.call(arguments);
    args = args.slice(1, args.length);

    // Account for the case when this function is called like ('message', [<array>]) instead of ('message', 'arg1', ...)
    try{
        if (args[0].length > 1){
            args = args[0];
        }
    } catch (e){}
  
    try{
        // Check to see if the key looks like a strings file key.
        // We have to do this because a string like "$.Testing %0" will eval to NaN.
        var str = '';
        var pattern = /[\w+\.]+\.\w+$/;
        if (key.match(pattern) != null){
            str = eval('$.'+key);
            if (typeof(str) == 'undefined'){
                str = key;
            }
        } else {
            str = key;
        }

        if (args.length > 0){
            return format_str(str, args);
        } else {
            return str;
        }
    }catch (e){
        return (typeof(key)=='undefined') ? '' : key;
    }
}

function format_str(str, args){
    for (var i=0 ; i<args.length ; i++){
        str = str.replace('%'+i, args[i]);
    }
    return str;
}
