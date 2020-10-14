function getLanguage() {
	
	var language = getParameterValue("locale", window.location.toString());
    var suppportedLanguages = getSupportedLanguages();
    language = findSupportedLanguage(language, suppportedLanguages);
    
    return language;
}


// Name: loadJSFiles
// Description:
//      loadJSFiles recursively calls itself until all javascript
//      files passed in through jsFiles has been loaded.  The
//      files are loaded into the HTML right before the ending
//      </body> tag.  An array of strings that contain the names
//      of the javascript files to be loaded should be passed in
//      for the jsFiles parameter.  Zero should be passed in for
//      the index parameter.  By passing in zero, loadJSFiles will
//      start to load the first string in jsFiles.
//
// Parameters:
//      jsFiles An array of strings that contain the names of the
//              javascript files to load.
//              Example array:
//                   var jsFiles = new Array("http://localhost:8080/ui_hpicsm_ws/js/i18n/grid.locale-en.js",
//                                           "http://localhost:8080/ui_hpicsm_ws/js/jquery.jqGrid.min.js");
//      index An integer that is used by loadJSFiles
//            to determine which position from jsFiles to read from.
//            When loadJSFiles is called this should be zero to
//            start loading from the first position in jsFiles.
function loadJSFiles(jsFiles, index) {
    
    if (index < jsFiles.length) {
        jsFile = jsFiles[index];
        index++;

        var body = document.getElementsByTagName('body')[0];
        var scrScript = document.createElement('script');
        scrScript.type= "text/javascript";
        
        //IE code to determine when script has been loaded
        scrScript.onreadystatechange = function () {
            if (scrScript.readyState == 'loaded' ||
                scrScript.readyState == 'complete') {
                //alert('scrScript onreadystate fired');
                loadJSFiles(jsFiles, index);
            }
        };
        
        // Code for other browsers (Firefox, Chrome) to determin when script
        // has been loaded.
        scrScript.onload= function () {
            loadJSFiles(jsFiles, index);
        };
        scrScript.src= jsFile;
        body.appendChild(scrScript);
     }
}

function loadJQGridLocalizationJSFiles() {
	var language = getLanguage();
    var jsFiles = new Array("/static/js/i18n/jqgrid/grid.locale-" + language + ".js",
                            "/static/js/jquery.jqGrid.min.js"
                           );

    loadJSFiles(jsFiles, 0);
}

function loadHPICSMTranslationsFile() {
	var language = getLanguage();
    var jsFiles = new Array("/static/js/i18n/" + language + "/storage-" + language + ".js");

    loadJSFiles(jsFiles, 0);
}

// Name: getParameterValue
// Description:
//      getParameterValue takes in a url and returns the value of
//      the key that is passed in.  For example if the parameters
//      are as follows:
//           url: http://localhost:3550/static/host_overview.html?&moref=HostSystem:host-57&locale=en_US
//           key: locale
//      For these parameters, getParameterValue returns en_US.
//      If the key
//
// Parameters:
//      key The name of the key in the key/value pair.
//      url The URL where the key/value pair will be searched for.
function getParameterValue(key, url) {
    ERROR = 'ERROR';
    
    String.prototype.get = function(p){
        return (match = this.match(new RegExp("[?|&]?" + p + "=([^&]*)"))) ? match[1] : ERROR;
    };
    
    var value = window.location.search.get(key);
    if (value == ERROR) {
        return null;
    }
    
    return value;
}

function getSupportedLanguages() {
    // Want to use an array for supportedLanguages, but the function
    // verifyValidLanguage requires the use of array.indexof(), and that
    // does not seem to be supported on IE (it does work on FF).
    var suppportedLanguages = "en";
    //var suppportedLanguages = new Array("en", "fr");
    return suppportedLanguages;
}

// Name: verifyValidLanguage
// Description:
//      verifyValidLanguage verifies if the language passed is an element
//      in the array suppportedLanguages.  If it is then language is
//      returned.  If it is not a supported language then 'en' is returned.
//      The language can be composed of only a language code
//      (i.e. 'en', 'fr', ...) or a language code paired with a
//      country code (i.e. 'en_US', 'en_UK', ...).  Note that an
//      underscore ('_') must separate the language code and the country
//      code.  Additionally, the language code should be lowercase
//      while the country code should be uppercase.  If the
//      language/country code pair is not a supported language, but the
//      language code itself is supported, then the language code is
//      returned by itself.  Ex: If 'en_UK' is not supported, but 'en' is,
//      then 'en' is returned.
//
// Parameters:
//      language A string that contains the language code to verify.
//      suppportedLanguages An array of strings of all the supported
//                          languages.
function findSupportedLanguage(language, suppportedLanguages) {
    
    // English is the default if the language passed in is unsupported 
    var defaultLanguage = 'en';
    
    if (language == null || language == 'ERROR') {
        return defaultLanguage;
    }
    
    var lang = language;
    
    var indexOfUnderscore = lang.indexOf('_');
    var languageCode = '';
    var countryCode = '';
    
    // Normalize language so languageCode is lowercase and countryCode is
    // uppercase (ex: en_US).
    if ( indexOfUnderscore > -1) {
        languageCode = lang.substring(0, indexOfUnderscore).toLowerCase();
        countryCode = lang.substring(indexOfUnderscore+1).toUpperCase();
        lang = languageCode + '_' + countryCode;
    } else {
        lang = lang.toLowerCase();
    }
    
    if (suppportedLanguages.indexOf(lang) > -1) {
        return lang;
    }
    
    if (indexOfUnderscore > -1) {
        if (suppportedLanguages.indexOf(languageCode) > -1) {
            return languageCode;
        }
    }

    return defaultLanguage;
}