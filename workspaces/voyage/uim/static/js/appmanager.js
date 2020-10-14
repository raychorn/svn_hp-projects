var AppManager = {  //Start of AppManager namespace  
    apps : {},
    loadedApps : [],
    currentAppId : null,
    defaultAppId : null,
    listeners : [],

    addAppListener : function(listener){
        AppManager.listeners.push(listener);
    },

    notifyAppListeners : function(appId){
        for (l in AppManager.listeners){
            try{
                AppManager.listeners[l](appId);
            } catch (e){}
        }
    },

    loadAppFromURL : function(appId, appURL, destination)
    {
        $.ajax({
             type: "GET",
             url: appURL,             
             success: function(app) {                
                $(destination).append(app); 
                // On the first load we'll notify listeners before we show the app. 
                // This prevents double loads on the first load.
                AppManager.notifyAppListeners(appId);
                if(AppManager.currentAppId)
                {
                    $('#'+AppManager.currentAppId).hide();
                }
                AppManager.currentAppId = appId;
                AppManager.loadedApps.push(appId);                
                $('#'+appId).show();
                main_layout.resizeAll();
             },
             error: function(jqXHR, textStatus, errorThrown) {
                alert('Error Loading App ' + appId + " :" + textStatus);
             }
        });
    },
    
    loadApp : function(appId)
    {        
        if(!appId)
        {
            appId = AppManager.defaultAppId
        }
        if(!(appId in AppManager.apps) )
        {
            alert("App '" + appId + "' has not been loaded.  Check configuration files.");
            return;
        }
        if($.inArray(appId, AppManager.loadedApps) >= 0)
        {
            if(AppManager.currentAppId)
            {
                $('#'+AppManager.currentAppId).hide();
            }
            AppManager.currentAppId = appId;            
            $('#'+appId).show();
            AppManager.notifyAppListeners(appId);
            main_layout.resizeAll();
        }
        else
        {            
            AppManager.loadAppFromURL(appId, AppManager.apps[appId].url + "?" + window.location.search.substring(1), '#main_contents');
        }
        
    },
    
    start : function(defaultApp)
    {        
        /* Get the list of registered apps from the uim and load the overview app */
        if(defaultApp)
        {
            AppManager.defaultAppId = defaultApp;
        }
        
        $.getJSON("/config/apps" + "?" + window.location.search.substring(1),
            function(apps_data) {
                AppManager.apps = apps_data;                
                if(AppManager.defaultAppId)
                {
                    AppManager.loadApp(AppManager.defaultAppId);
                }
            }
        );        				
    }

}; //End of AppManager namespace
