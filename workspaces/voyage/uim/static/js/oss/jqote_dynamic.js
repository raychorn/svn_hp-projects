/*
 * Some functions for dynamically loading jqote templates instead of
 * embedding them in script tags on the page.

the templates parameter should look something like:

templates = [
    {url: 'some_url', id: 'some_id'},
    {url: 'some_url', id: 'some_id'},
    {url: 'some_url', id: 'some_id'}
];

*/

function load_templates(templates){

    for (var i=0 ; i<templates.length ; i++){

        var template = templates[i];
        if (typeof(template.id) != 'undefined' && typeof(template.url) != 'undefined'){
            create_script_tag(template.id);
            load_template(template);
        }
    }
}

function create_script_tag(id){
    var s_tag = document.createElement('script');
    s_tag.type = 'text/html';
    s_tag.setAttribute('id', id);
    document.body.appendChild(s_tag);
}

function load_template(template){
    $.ajax({
        url: template.url,
        async: false,
        success: function(result){
            document.getElementById(template.id).text = result;
        }
    });
}
