/*----------------------------------------------------------------------------
 * JavaScript for webhelp search
 *----------------------------------------------------------------------------
 This file is part of the webhelpsearch plugin for DocBook WebHelp
 Copyright (c) 2007-2008 NexWave Solutions All Rights Reserved.
 www.nexwave.biz Nadege Quaine
 http://kasunbg.blogspot.com/ Kasun Gajasinghe
 
 Lionbridge I18n: externalized search UI strings; l.158
 */

//string initialization
var htmlfileList = "htmlFileList.js";
var htmlfileinfoList = "htmlFileInfoList.js";
var useCJKTokenizing = false;


/* Cette fonction verifie la validite de la recherche entrre par l utilisateur */
function Verifie(ditaSearch_Form) {

    // Check browser compatibitily
    if (navigator.userAgent.indexOf("Konquerer") > -1) {

        alert(Encoder.htmlDecode(txt_browser_not_supported));
        return;
    }

    var expressionInput = document.ditaSearch_Form.textToSearch.value;
    //Set a cookie to store the searched keywords
    $.cookie('textToSearch', expressionInput);

    if (expressionInput.length < 1) {

        // expression is invalid
        alert(Encoder.htmlDecode(txt_enter_at_least_1_char));
        // reactive la fenetre de search (utile car cadres)
        document.ditaSearch_Form.textToSearch.focus();
    }
    if (expressionInput.length == 1) {
        var resultlist = characterSearch(expressionInput);
        //alert("resultlist="+resultlist);
        w[expressionInput] = resultlist ;
        Effectuer_recherche(expressionInput);
        document.ditaSearch_Form.textToSearch.focus();
    }
    else {

        // Effectuer la recherche
        Effectuer_recherche(expressionInput);
        // reactive la fenetre de search (utile car cadres)
        document.ditaSearch_Form.textToSearch.focus();
    }
}

var stemQueryMap = new Array();  // A hashtable which maps stems to query words

/* This function parses the search expression, loads the indices and displays the results*/
function Effectuer_recherche(expressionInput) {

    /* Display a waiting message */
    //DisplayWaitingMessage();

    /*data initialisation*/
    var searchFor = "";       // expression en lowercase et sans les caracte    res speciaux
    //w = new Object();  // hashtable, key=word, value = list of the index of the html files
    scriptLetterTab = new Scriptfirstchar(); // Array containing the first letter of each word to look for
    var wordsList = new Array(); // Array with the words to look for
    var finalWordsList = new Array(); // Array with the words to look for after removing spaces
    var linkTab = new Array();
    var fileAndWordList = new Array();
    var txt_wordsnotfound = "";

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

    /*nqu: expressionInput, la recherche est lower cased, plus remplacement des char speciaux*/
    // Adjustments for . / - ' ‘ ’ 
    searchFor = expressionInput.toLowerCase().replace(/<\//g, "_st_").replace(/\$_/g, "_di_").replace(/'/g," ").replace(/\.|%27|%60|%B4|%2F/g, "").replace(/-|%2C|%3B|%21|%3A|@|\/|\*/g, " ").replace(/(%20)+/g, " ").replace(/_st_/g, "</").replace(/_di_/g, "%24_");

    searchFor = searchFor.replace(/  +/g, " ");
    searchFor = searchFor.replace(/ $/, "").replace(/^ /, "");

    wordsList = searchFor.split(" ");
    wordsList.sort();

    //set the tokenizing method; Lionbridge i18n added Thai
    if(typeof language != "undefined" && (language=="zh" || language=="zh_cn" || language=="zh_tw" ||  language=="zh-cn" || language=="zh-tw" || language=="ja" ||language=="ko" )){
        useCJKTokenizing=true;
    } else {
        useCJKTokenizing=false;
    }
    //If Lucene CJKTokenizer was used as the indexer, then useCJKTokenizing will be true. Else, do normal tokenizing.
    // 2-gram tokenizinghappens in CJKTokenizing,  
    if(useCJKTokenizing){
        finalWordsList = cjkTokenize(wordsList);
    } else { 
        finalWordsList = tokenize(wordsList);
    }

    //load the scripts with the indices: the following lines do not work on the server. To be corrected
    /*if (IEBrowser) {
     scriptsarray = loadTheIndexScripts (scriptLetterTab);
     } */

    /**
     * Compare with the indexed words (in the w[] array), and push words that are in it to tempTab.
     */
    var tempTab = new Array();
    for (var t in finalWordsList) {
        if (w[finalWordsList[t].toString()] == undefined) {
    //        txt_wordsnotfound += finalWordsList[t] + " ";
	 txt_wordsnotfound += wordsList + " ";
        } else {
            tempTab.push(finalWordsList[t]);
        }
    }
    finalWordsList = tempTab;
    // alert("finalWordsList="+finalWordsList.toString()+" txt_wordsnotfound="+txt_wordsnotfound);

    if (finalWordsList.length) {
        //search 'and' and 'or' one time
        fileAndWordList = SortResults(finalWordsList); 
         // Lionbridge i18n string externalization 2012-7-16
         // in search-01.html 30 hidden iframes are intro as search buffers to see which
         // of the candidates fileAndWordList contains the actual exact search text 
         // - due to load latency of the docs - both for txtFrame.src = tempPath
         //    and also for text = win.document.body.innerHTML , we split this
         // across 2 loops and have a brief wait in between
        var filetextbufs = ['docSrchBuf1', 'docSrchBuf2', 'docSrchBuf3','docSrchBuf4','docSrchBuf5', 'docSrchBuf6', 'docSrchBuf7','docSrchBuf8','docSrchBuf9','docSrchBuf10',
 	                            'docSrchBuf11', 'docSrchBuf12', 'docSrchBuf13','docSrchBuf14','docSrchBuf15', 'docSrchBuf16', 
 	                            'docSrchBuf17','docSrchBuf18','docSrchBuf19','docSrchBuf20',
 	                            'docSrchBuf21', 'docSrchBuf22', 'docSrchBuf23','docSrchBuf24','docSrchBuf25', 'docSrchBuf26', 
 	                            'docSrchBuf27','docSrchBuf28','docSrchBuf29','docSrchBuf30',
 	                            'docSrchBuf31', 'docSrchBuf32', 'docSrchBuf33','docSrchBuf34','docSrchBuf35', 'docSrchBuf36', 
 	                            'docSrchBuf37','docSrchBuf38','docSrchBuf39','docSrchBuf40',
 	                            'docSrchBuf41', 'docSrchBuf42', 'docSrchBuf43','docSrchBuf44','docSrchBuf45', 'docSrchBuf46', 
 	                            'docSrchBuf47','docSrchBuf48','docSrchBuf49','docSrchBuf50'] ;
        var cpt = fileAndWordList.length;
        var i = cpt - 1;
         if (fileAndWordList[i] != undefined) {
            for (t in fileAndWordList[i]) {
            if (t<50) {
                    //DEBUG: alert("DEBUG: "+ fileAndWordList[i][t].filenb+" " +fileAndWordList[i][t].motsliste);
                    //linkTab.push("<li><a href=\"../"+fl[fileAndWordList[i][t].filenb]+"\">"+fl[fileAndWordList[i][t].filenb]+"</a></li>");
                    var tempInfo = fil[fileAndWordList[i][t].filenb];
                    var pos1 = tempInfo.indexOf("@@@");
                    var pos2 = tempInfo.lastIndexOf("@@@");
                    var tempPath = tempInfo.substring(0, pos1);
                    var tempTitle = tempInfo.substring(pos1 + 3, pos2);
                    var tempShortdesc = tempInfo.substring(pos2 + 3, tempInfo.length);
                    var txtFrame = document.getElementById(filetextbufs[t]);
                    txtFrame.src = tempPath; 
					// loads the file containing the search term into the iframe
                    //alert("tempPath="+tempPath);
                    //file:///home/kasun/docbook/WEBHELP/webhelp-draft-output-format-idea/src/main/resources/web/webhelp/installation.html
                    var linkString = "<li><a target='mainhelp_pane' href=" + tempPath + ">" + tempTitle + "</a>";
                    // Petrone added target to results links to work in the ess webhelp frameset
                    // var linkString = "<li><a href=\"installation.html\">" + tempTitle + "</a>";
                    //     if ((tempShortdesc != "null")) {
                    //       linkString += "\n<div class=\"shortdesclink\">" + tempShortdesc + "</div>";
                    //    }
                    linkString += "</li>";
                    linkTab.push(linkString);
                }
                }
            }
    }  
    // show some results initially and then re-evaluate/update every few seconds
   // setTimeout(function(){  getresults(filetextbufs,linkTab,searchFor,expressionInput,cpt,fileAndWordList,txt_wordsnotfound); }, 2500);
	setTimeout(function(){  getresults(filetextbufs,linkTab,searchFor,expressionInput,cpt,fileAndWordList,txt_wordsnotfound); }, 5000);
  //  setTimeout(function(){  getresults(filetextbufs,linkTab,searchFor,expressionInput,cpt,fileAndWordList,txt_wordsnotfound); }, 1000);
      setTimeout(function(){  getresults(filetextbufs,linkTab,searchFor,expressionInput,cpt,fileAndWordList,txt_wordsnotfound); }, 2000);

    getresults(filetextbufs,linkTab,searchFor,expressionInput,cpt,fileAndWordList,txt_wordsnotfound);
}

function getresults(filetextbufs,linkTab,searchFor,expressionInput,cpt,fileAndWordList,txt_wordsnotfound) {
    var results = "";
    if (linkTab.length > 0) { 
        /*writeln ("<p>" + txt_results_for + " " + "<span class=\"searchExpression\">"  + cleanwordsList + "</span>" + "<br/>"+"</p>");*/
        //write("<ul class='searchresult'>");
        results ="<p>";
        results += "<p>" + txt_results_for + " " + "<span class='searchExpression'>" + fileAndWordList[cpt-1][0].motslisteDisplay + "</span>" + "</p>" ;
        results += "<ul class='searchresult' type='disc'>";
        for (t in linkTab) {
            var d = document ;
			try {
	    var txtFrame = d.getElementById(filetextbufs[t]);
		}
		catch (err)
		{
		sleep(1);
		var txtFrame = d.getElementById(filetextbufs[t]);
		}
		
	    var text = '';
		try {
		
	    var win = txtFrame.contentWindow;
		}
		catch (err)
		{
		sleep(0.0005);
		var win = txtFrame.contentWindow;
		}
		sleep(0.0005);
	    // sleep(1);
	    text = win.document.body.innerHTML;
	    text = text.toLowerCase().replace(/'/g," ").replace(/\.|%27|%60|%B4|%2F/g, "").replace(/-|%2C|%3B|%21|%3A|@|\/|\*/g, " ") ;
	    var expressionInput2=expressionInput.toLowerCase().replace(/'/g," ").replace(/\.|%27|%60|%B4|%2F/g, "").replace(/-|%2C|%3B|%21|%3A|@|\/|\*/g, " ") ;
		//alert("searchFor="+searchFor);
		//alert("expressionInput2="+expressionInput2);
		//alert("expressionInput="+expressionInput);
		//alert("text="+text);
           if ( ( -1 != text.indexOf(searchFor)) || ( -1 !=  text.indexOf(expressionInput2)) || ( -1 !=  text.indexOf(expressionInput)) ) {   
                 results += linkTab[t].toString(); }
        }
        results += "</ul>" ;
        results += "</p>" ;
    } else {
        // Lionbridge i18n string externalization 2011-10-04
        results = "<p>" + txt_Your_Search_Returned_No_Results_For + "<span class=\"searchExpression\">" + txt_wordsnotfound + "</span>" + "</p>";
    }    
    //alert(results);
    document.getElementById('searchResults').innerHTML = results; 
}

function tokenize(wordsList){
    var stemmedWordsList = new Array(); // Array with the words to look for after removing spaces
    var cleanwordsList = new Array(); // Array with the words to look for
    var browser = navigator.userAgent;
    for(var j in wordsList){
        var word = wordsList[j];
        if(typeof stemmer != "undefined" ){
            stemQueryMap[stemmer(word)] = word;
        } else {
            stemQueryMap[word] = word;
        }
    } 
     //stemmedWordsList is the stemmed list of words separated by spaces.
    for (var t in wordsList) {
        wordsList[t] = wordsList[t].replace(/(%22)|^-/g, "");
        if (wordsList[t] != "%20") {
            scriptLetterTab.add(wordsList[t].charAt(0));
            cleanwordsList.push(wordsList[t]);
            // save search word for search highlighting
            if (browser.indexOf('MSIE') != -1) {   // IE
              $.cookie('search-word' + t, wordsList[t]);
            }
            else {   // Chrome and Firefox
              $.jStorage.set("search-word" + t, wordsList[t]);
            }
        }
    }
    if(typeof stemmer != "undefined" ){
        //Do the stemming using Porter's stemming algorithm
        for (var i = 0; i < cleanwordsList.length; i++) {
            var stemWord = stemmer(cleanwordsList[i]);
            stemmedWordsList.push(stemWord);
            // save search word for search highlighting
            var j = i + 20;
            if (browser.indexOf('MSIE') != -1) {   // IE
              $.cookie('search-word' + j, stemWord);
            }
            else {   // Chrome and Firefox
              $.jStorage.set("search-word" + j, stemWord);
            }
        }
    } else {
        stemmedWordsList = cleanwordsList;
    }
    return stemmedWordsList;
}

//Invoker of CJKTokenizer class methods.
function cjkTokenize(wordsList){
    var browser = navigator.userAgent;
    var allTokens= new Array();
    var notCJKTokens= new Array();
    var j=0;
    for(j=0;j<wordsList.length;j++){
        var word = wordsList[j];
        if(getAvgAsciiValue(word) < 127){
            notCJKTokens.push(word);
        } else { 
            var i = j + 10;
            if (browser.indexOf('MSIE') != -1) {   // IE
              $.cookie('search-word' + i, word);
            }
            else {   // Chrome and Firefox
              $.jStorage.set("search-word" + i, word);
            }
            var tokenizer = new CJKTokenizer(word);
            var tokensTmp = tokenizer.getAllTokens();
            allTokens = allTokens.concat(tokensTmp);
        }
    }
    allTokens = allTokens.concat(tokenize(notCJKTokens));
    return allTokens;
}

//A simple way to determine whether the query is in english or not.
function getAvgAsciiValue(word){
    var tmp = 0;
    var num = word.length < 5 ? word.length:5;
    for(var i=0;i<num;i++){
        if(i==5) break;
        tmp += word.charCodeAt(i);
    }
    return tmp/num;
}

//CJKTokenizer
function CJKTokenizer(input){
    this.input = input;
    this.offset=-1;
    this.tokens = new Array(); 
    this.incrementToken = incrementToken;
    this.tokenize = tokenize;
    this.getAllTokens = getAllTokens;
    this.unique = unique;

    function incrementToken(){
		if(this.input.length - 2 <= this.offset){
		//	console.log("false "+offset);
			return false;
		}
		else {
			this.offset+=1;
			return true;
		}
	}

	function tokenize(){
		//document.getElementById("content").innerHTML += x.substring(offset,offset+2)+"<br>";
		return this.input.substring(this.offset,this.offset+2);
	}

	function getAllTokens(){
 	        // Liox i18n - this covers special singleton case per Lucene CJK Analyzer
 	        if (this.input.length == 1) return this.input;
		while(this.incrementToken()){
			var tmp = this.tokenize();
			this.tokens.push(tmp);
		}
        return this.unique(this.tokens);
//		document.getElementById("content").innerHTML += tokens+" ";
//		document.getElementById("content").innerHTML += "<br>dada"+sortedTokens+" ";
//		console.log(tokens.length+"dsdsds");
		/*for(i=0;i<tokens.length;i++){
			console.log(tokens[i]);
			var ss = tokens[i] == sortedTokens[i];

//			document.getElementById("content").innerHTML += "<br>dada"+un[i]+"- "+stems[i]+"&nbsp;&nbsp;&nbsp;"+ ss;
			document.getElementById("content").innerHTML += "<br>"+sortedTokens[i];
		}*/
	}

	function unique(a)
	{
	   var r = new Array();
	   o:for(var i = 0, n = a.length; i < n; i++)
	   {
	      for(var x = 0, y = r.length; x < y; x++)
	      {
		 if(r[x]==a[i]) continue o;
	      }
	      r[r.length] = a[i];
	   }
	   return r;
	} 
}


/* Scriptfirstchar: to gather the first letter of index js files to upload */
function Scriptfirstchar() {
    this.strLetters = "";
    this.add = addLettre;
}

function addLettre(caract) {

    if (this.strLetters == 'undefined') {
        this.strLetters = caract;
    } else if (this.strLetters.indexOf(caract) < 0) {
        this.strLetters += caract;
    }

    return 0;
}
/* end of scriptfirstchar */

/*main loader function*/
/*tab contains the first letters of each word looked for*/
function loadTheIndexScripts(tab) {

    //alert (tab.strLetters);
    var scriptsarray = new Array();

    for (var i = 0; i < tab.strLetters.length; i++) {

        scriptsarray[i] = "..\/search" + "\/" + tab.strLetters.charAt(i) + ".js";
    }
    // add the list of html files
    i++;
    scriptsarray[i] = "..\/search" + "\/" + htmlfileList;

    //debug
    for (var t in scriptsarray) {
        //alert (scriptsarray[t]);
    }

    tab = new ScriptLoader();
    for (t in scriptsarray) {
        tab.add(scriptsarray[t]);
    }
    tab.load();
    //alert ("scripts loaded");
    return (scriptsarray);
}

/* ScriptLoader: to load the scripts and wait that it's finished */
function ScriptLoader() {
    this.cpt = 0;
    this.scriptTab = new Array();
    this.add = addAScriptInTheList;
    this.load = loadTheScripts;
    this.onScriptLoaded = onScriptLoadedFunc;
}

function addAScriptInTheList(scriptPath) {
    this.scriptTab.push(scriptPath);
}

function loadTheScripts() {
    var script;
    var head;

    head = document.getElementsByTagName('head').item(0);

    //script = document.createElement('script');

    for (var el in this.scriptTab) {
        //alert (el+this.scriptTab[el]);
        script = document.createElement('script');
        script.src = this.scriptTab[el];
        script.type = 'text/javascript';
        script.defer = false;

        head.appendChild(script);
    }

}

function onScriptLoadedFunc(e) {
    e = e || window.event;
    var target = e.target || e.srcElement;
    var isComplete = true;
    if (typeof target.readyState != undefined) {

        isComplete = (target.readyState == "complete" || target.readyState == "loaded");
    }
    if (isComplete) {
        ScriptLoader.cpt++;
        if (ScriptLoader.cpt == ScriptLoader.scripts.length) {
            ScriptLoader.onLoadComplete();
        }
    }
}

/*
function onLoadComplete() {
    alert("loaded !!");
} */

/* End of scriptloader functions */
 
// Array.unique( strict ) - Remove duplicate values
function unique(tab) {
    var a = new Array();
    var i;
    var l = tab.length;

    if (tab[0] != undefined) {
        a[0] = tab[0];
    }
    else {
        return -1
    }

    for (i = 1; i < l; i++) {
        if (indexof(a, tab[i], 0) < 0) {
            a.push(tab[i]);
        }
    }
    return a;
}
function indexof(tab, element, begin) {
    for (var i = begin; i < tab.length; i++) {
        if (tab[i] == element) {
            return i;
        }
    }
    return -1;

}
/* end of Array functions */


/*
 Param: mots= list of words to look for.
 This function creates an hashtable:
 - The key is the index of a html file which contains a word to look for.
 - The value is the list of all words contained in the html file.

 Return value: the hashtable fileAndWordList
 */
function SortResults(mots) {

    var fileAndWordList = new Object();
    if (mots.length == 0) {
        return null;
    }

    for (var t in mots) {
        // get the list of the indices of the files.
        var listNumerosDesFicStr = w[mots[t].toString()];
        //alert ("listNumerosDesFicStr "+listNumerosDesFicStr);
        var tab = listNumerosDesFicStr.split(",");

        //for each file (file's index):
        for (var t2 in tab) {
            var temp = tab[t2].toString();
            if (fileAndWordList[temp] == undefined) {

                fileAndWordList[temp] = "" + mots[t];
            } else {

                fileAndWordList[temp] += "," + mots[t];
            }
        }
    }

    var fileAndWordListValuesOnly = new Array();

    // sort results according to values
    var temptab = new Array();
    for (t in fileAndWordList) {
        tab = fileAndWordList[t].split(',');

        var tempDisplay = new Array();
        for (var x in tab) {
            if(stemQueryMap[tab[x]] != undefined){
                tempDisplay.push(stemQueryMap[tab[x]]); //get the original word from the stem word.
            } else {
                tempDisplay.push(tab[x]); //no stem is available. (probably a CJK language)
            }
        }
        var tempDispString = tempDisplay.join(", ");

        temptab.push(new resultPerFile(t, fileAndWordList[t], tab.length, tempDispString));
        fileAndWordListValuesOnly.push(fileAndWordList[t]);
    }


    //alert("t"+fileAndWordListValuesOnly.toString());

    fileAndWordListValuesOnly = unique(fileAndWordListValuesOnly);
    fileAndWordListValuesOnly = fileAndWordListValuesOnly.sort(compare_nbMots);
    //alert("t: "+fileAndWordListValuesOnly.join(';'));

    var listToOutput = new Array();

    for (var j in fileAndWordListValuesOnly) {
        for (t in temptab) {
            if (temptab[t].motsliste == fileAndWordListValuesOnly[j]) {
                if (listToOutput[j] == undefined) {
                    listToOutput[j] = new Array(temptab[t]);
                } else {
                    listToOutput[j].push(temptab[t]);
                }
            }
        }
    }
    return listToOutput;
}

function resultPerFile(filenb, motsliste, motsnb, motslisteDisplay) {
    this.filenb = filenb;
    this.motsliste = motsliste;
    this.motsnb = motsnb;
    this.motslisteDisplay= motslisteDisplay;
}

function compare_nbMots(s1, s2) {
    var t1 = s1.split(',');
    var t2 = s2.split(',');
    //alert ("s1:"+t1.length + " " +t2.length)
    if (t1.length == t2.length) {
        return 0;
    } else if (t1.length > t2.length) {
        return 1;
    } else {
        return -1;
    }
    //return t1.length - t2.length);
}

function sleep(n) {
for (var i=0, y=1.0 ; i <n*10000000; i++ ) { y  =  y * 1.0000001 ; }
}

function characterSearch(expressionInput) {
var chArr = new Array(); // if index complexity is N , this is square root of N complexity
var i=0;
var ch;
// alert(" char search input = " + expressionInput );
for (var chstr in w) {
 var n=chstr.length;
  for (i=0; i<n; i++ ) {
    if (chstr.charCodeAt(i)>255) { // look for only the Asian glyphs
       if (chstr.charAt(i) in chArr) {
                  chArr[chstr.charAt(i)] = merge( chArr[chstr.charAt(i)] , w[chstr] ) ;
       } else { chArr[chstr.charAt(i)] = w[chstr] ; }
    }
  }
 }
 var result =  chArr[expressionInput] ;
 // alert(" char search result = " + result );
 return result;
}

function merge(a,b)
{  var c = (a+","+b).split(",").sort(function(a,b){return a-b}) ; 
   //alert("merged = "+c);
   for(var i=0; i<c.length; ++i) { 
            if (c[i] == c[i+1]) { c.splice(i, 1); }
    }
   //alert("merged unique = "+c);
   return c.join(",");
}
