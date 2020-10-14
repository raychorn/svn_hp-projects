jQuery.fn.liveUpdate = function(list){
	list = jQuery(list);

	if ( list.length ) {
		var rows = list.children('li'),
			cache = rows.map(function(){
				return jQuery(this).text().toLowerCase();
			});
		
		this
			.keyup(filter).keyup();
	}

  function callback() {
    var moreLists = list.slice(1);

    callback = (moreLists.length) ? function() {
      moreLists.prev('h2').toggle(!!moreLists.children(':visible').length);
    } : function() {};
    
  };
		
	function filter(){
		var term = jQuery.trim( jQuery(this).val().toLowerCase() ), scores = [];
		
		if ( !term ) {
			rows.show().addClass('keynav withoutfocus');
		} else {
			rows.hide().removeClass('keynav withfocus withoutfocus');

			cache.each(function(i){
				if ( this.indexOf( term ) > -1 ) {
					jQuery(rows[i]).show().addClass('keynav withoutfocus');
				}
			});
		}
	  callback();
	}
	
	return this;
};
