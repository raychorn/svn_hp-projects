// Porter stemmer in Javascript. Few comments, but it's easy to follow against the rules in the original
// paper, in
//
//  Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14,
//  no. 3, pp 130-137,
//
// see also http://www.tartarus.org/~martin/PorterStemmer

// Release 1 be 'andargor', Jul 2004
// Release 2 (substantially revised) by Christopher McKenzie, Aug 2009


var stemmer = (function(){
	var step2list = {
			"ational" : "ate",
			"tional" : "tion",
			"enci" : "ence",
			"ency" : "ence",
			"anci" : "ance",
			"ancy" : "ance",
			"izer" : "ize",
			"bli" : "ble",
			"bly" : "ble" ,
			"alli" : "al",
			"ally" : "al",
			"ently" : "ent",
			"entli" : "ent",
			"ely" : "e",
			"eli" : "e",
			"ously" : "ous",
			"ousli" : "ous",
			"ization" : "ize",
			"ation" : "ate",
			"ator" : "ate",
			"alism" : "al",
			"iveness" : "ive",
			"fulness" : "ful",
			"ousness" : "ous",
			"ality" : "al",
			"aliti" : "al",
			"ivity" : "ive",
			"iviti" : "ive",
			"bility" : "ble",
			"biliti" : "ble",
			"archy" : "archi",
			"ity" : "",
			"ery" : "eri",
			"any" : "ani",
			"ory" : "ori",
			"logy" : "log",
			"logi" : "log"
		},

		step3list = {
			"icate" : "ic",
			"ative" : "",
			"alize" : "al",
			"iciti" : "ic",
			"ical" : "ic",
			"ful" : "",
			"ness" : ""
		},

		c = "[^aeiou]",          // consonant
		v = "[aeiouy]",          // vowel
		C = c + "[^aeiouy]*",    // consonant sequence
		V = v + "[aeiou]*",      // vowel sequence
		y = "y",
		a_v =  "^(" + C + ")?" + V + y, 
		c_v =C + y,
		
		mgr0 = "^(" + C + ")?" + V + C,               // [C]VC... is m>0
		meq1 = "^(" + C + ")?" + V + C + "(" + V + ")?$",  // [C]VC[V] is m=1
		mgr1 = "^(" + C + ")?" + V + C + V + C,       // [C]VCVC... is m>1
		s_v = "^(" + C + ")?" + v;                   // vowel in stem

	return function (w) {
		var 	stem,
			suffix,
			firstch,
			re,
			re2,
			re3,
			re4,
			origword = w;

		if (w.length < 3) { return w; }

		firstch = w.substr(0,1);
		if (firstch == "y") {
			w = firstch.toUpperCase() + w.substr(1);
		}

		// Step 1a
		re = /^(.+?)(ss|i)es$/;
		re2 = /^(.+?)([^s])s$/;

		if (re.test(w)) { w = w.replace(re,"$1$2"); }
		else if (re2.test(w)) {	w = w.replace(re2,"$1$2"); }

		// Step 1b
		re = /^(.+?)eed$/;
		re2 = /^(.+?)(ed|ing)$/;
		if (re.test(w)) {
			var fp = re.exec(w);
			re = new RegExp(mgr0);
			if (re.test(fp[1])) {
				re = /.$/;
				w = w.replace(re,"");
			}
		} else if (re2.test(w)) {
			var fp = re2.exec(w);
			stem = fp[1];
			re2 = new RegExp(s_v);
			if (re2.test(stem)) {
				w = stem;
				re2 = /(at|bl|iz)$/;
				re3 = new RegExp("([^aeiouylsz])\\1$");
				re4 = new RegExp("^" + C + v + "[^aeiouwxy]$");
				if (re2.test(w)) {	w = w + "e"; }
				else if (re3.test(w)) { re = /.$/; w = w.replace(re,""); }
				else if (re4.test(w)) { w = w + "e"; }
			}
		}

		// Step 1c
		re = /^(.+?)y$/; //pattern for words ending with a y
	
		if (re.test(w)) {
			var fp = re.exec(w);
			stem = fp[1];
		//re = new RegExp(s_v); 
	//	re = new RegExp(a_v); //trp replaced pattern to search for consonant sequence + vowel sequence+ y
	//	if (re.test(stem)) { w = stem + "y"; }        
//		else { w = stem + "i";}
	re = new RegExp("^(" + c + ")?"+ "[aeiou]" + "[^y]$");
	re3 = new RegExp("(ay|ey|iy|oy|uy)$");
	//	re2 = new RegExp("(by|cy|dy|fy|gy|hy|jy|ky|ly|my|ny|py|qy|ry|sy|ty|vy|xy|wy|zy)$");
	if (re3.test(stem)) { w = stem + "y"; }
	//	else if (re2.test(stem)){w = stem + "i";}
		
//		if (re2.test(stem)) {
//		w = stem + "i";
//		}
		
		}
		

		// Step 2
	//	re = /^(.+?)(ational|tional|enci|anci|izer|bli|alli|entli|eli|ousli|ization|ation|ator|alism|iveness|fulness|ousness|aliti|iviti|biliti|logi)$/;
				re = /^(.+?)(ational|tional|enci|ency|ancy|izer|bli|bly|alli|ally|entli|ently|eli|ely|ousli|iously|ization|ation|ator|alism|iveness|fulness|ousness|aliti|ality|iviti|ivitylbiliti|bility|logi|logy|archy|ity|ery|any|ory)$/;
if (re.test(w)) {
			var fp = re.exec(w);
			stem = fp[1];
			suffix = fp[2];
			re = new RegExp(mgr0);
			if (re.test(stem)) {
				w = stem + step2list[suffix];
			}
		}

		// Step 3
		re = /^(.+?)(icate|ative|alize|iciti|ical|ful|ness)$/;
		if (re.test(w)) {
			var fp = re.exec(w);
			stem = fp[1];
			suffix = fp[2];
			re = new RegExp(mgr0);
			if (re.test(stem)) {
				w = stem + step3list[suffix];
			}
		}

		// Step 4
//		re = /^(.+?)(al|ance|ence|er|ic|able|ible|ant|ement|ment|ent|ou|ism|ate|iti|ous|ive|ize)$/;  
re = /^(.+?)(al|ance|ence|er|able|ible|ant|ement|ment|ent|ou|ism|ate|iti|ous|ive|ize)$/;  // trp: removed the suffix ic from the list.... ic should be left as part of the stem for words like communication or communicate  this is how the lucerne stemmer creates the search index entries
		re2 = /^(.+?)(s|t)(ion)$/;
		if (re.test(w)) {
			var fp = re.exec(w);
			stem = fp[1];
			re = new RegExp(mgr1);
			if (re.test(stem)) {
				w = stem;
			}
		} else if (re2.test(w)) {
			var fp = re2.exec(w);
			stem = fp[1] + fp[2];
			re2 = new RegExp(mgr1);
			if (re2.test(stem)) {
				w = stem;
			}
		}

		// Step 5
		re = /^(.+?)e$/;
		if (re.test(w)) {
			var fp = re.exec(w);
			stem = fp[1];
			re = new RegExp(mgr1);
			re2 = new RegExp(meq1);
			re3 = new RegExp("^" + C + v + "[^aeiouwxy]$");
			if (re.test(stem) || (re2.test(stem) && !(re3.test(stem)))) {
				w = stem;
			}
		}

		re = /ll$/;
		re2 = new RegExp(mgr1);
		if (re.test(w) && re2.test(w)) {
			re = /.$/;
			w = w.replace(re,"");
		}

		// and turn initial Y back to y

		if (firstch == "y") {
			w = firstch.toLowerCase() + w.substr(1);
		}

		return w;
	}
})();