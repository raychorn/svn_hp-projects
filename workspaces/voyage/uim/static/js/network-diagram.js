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
//    Chris Frantz
//    Andy Yates
//

(function() 
{
    //
    // Network topology rendering
    // 
    var greyfont = {font: '12px Arial', fill: '#888', 'text-anchor': 'start', "font-family": "Arial, Helvetica, sans-serif"};
    var smallwhitefont = {font: '12px Arial', fill: '#fff', 'text-anchor': 'start', "font-family": "Arial, Helvetica, sans-serif"};
    var smallblackfont = {font: '12px Arial', fill: '#000', 'text-anchor': 'start', "font-family": "Arial, Helvetica, sans-serif"};
    var titlefont = smallwhitefont;
    var blackfont = {font: '14px Arial', fill: '#000', 'text-anchor': 'start', "font-family": "Arial, Helvetica, sans-serif"};
    var smallblackfont = {font: '12px Arial', fill: '#000', 'text-anchor': 'start', "font-family": "Arial, Helvetica, sans-serif"};
    var baycolors = new Array();
    baycolors['c7000'] = [ '#aaa', '#f70', '#f70', '#fd0', '#fd0', '#2b0', '#2b0', '#39e', '#39e' ];
    baycolors['c3000'] = [ '#aaa', '#f70', '#fd0', '#2b0', '#2b0' ];

    var port_rj45 = "M0 5 L0 25 L20 25 L20 5 L15 5 L15 0 L5 0 L5 5 Z";
    var port_cx4 = "M0 0 L5 25 L35 25 L40 0 Z";
    var port_unknown = "M0 0 L0 25 L25 25 L25 0 Z";    
    var port_fiber = "M0 4 L4 4 L4 0 L14 0 L14 4 L20 4 L20 0 L30 0 L30 4 L34 4 L34 22 L0 22 Z";

    var used_colors = new Array();

    var network_objs = {};
    var downlink_objs = {};
    var portlink_objs = {};
    var uplink_objs = {};
    var uplink_networks = {};
    var pnic_macs = {};

    // Returns a closure to handle mouse over events on network bubbles
    function networkColorEventHandler(name, color, stroke)
    {
        return function (event) {
            var i;
            
            for(i in network_objs[name])
            {                                
                network_objs[name][i]._foreground.attr({'fill': color});
            }                
            for(i in downlink_objs[name])
            {                
                downlink_objs[name][i].attr({'stroke': color});
            }
            for(i in portlink_objs[name])
            {
                portlink_objs[name][i].attr({'stroke': color});
            }
            for(i in uplink_objs[name])
            {
                if(event.type == 'mouseover')
                    uplink_objs[name][i].attr({'stroke': color});
                else 
                    uplink_objs[name][i].attr({'stroke': '#000'});
            }
        };
    }

    function stroke_width_calc(speed)
    {
        if(speed>10)
        {
            speed = speed/1000.0;
        }
        return Math.min(10,Math.max(2,speed/1));
    }
    
    // Check if we're running in Internet Explorer
    var msie=false;
    if (/MSIE (\d+\.\d+);/.test(navigator.userAgent)) {
        msie = new Number(RegExp.$1);
    }

    Raphael.fn.hp = 
    {                
        text: function(x, y, text, attr, tooltip) 
        {
            if(!attr)attr = {};
            var st = this.set();            
            var t = this.text(0, 0, text).attr(attr);            
            var b = t.getBBox();
            t.translate(-b.x, -b.y);    //Make sure we are left justified
            if(msie)t.translate(0,2);   // IE draws text 2 px higher than everyone else                
            st.push(t);
            if(tooltip) 
            {
                var image = "/static/img/network-diagram/bubble.png";                
                var img = this.image(image, b.width+4, 0, 16, 16);                
                NetworkDiagram.addTip(img.node,tooltip);
                st.push(img);                                                                                             
            }
                                        
            st.translate(x, y);                
            return st;
        },
        
        bubble: function(title, attr, title_attr, ttip, drawfnc) 
        {
            var margin = 6;            
            var st = this.set();
            title = this.hp.text(margin, 0, title, title_attr, ttip);            
            var title_bb = title.getBBox();
            
            var width = title_bb.width + (2 * margin);
            var height = title_bb.height;
            if (attr['skip'] != true) 
            {
                var contents = drawfnc(this);
                if(contents.length)
                {
                    var b = contents.getBBox();
                    contents.translate(margin, title_bb.height+margin);                        
                    st.push(contents);
                    if(title_bb.width < b.width)
                    {
                        width = b.width + (2 * margin);
                    }
                    height += b.height + (2 * margin);
                    if (contents.bindings) {
                        st.bindings = contents.bindings;
                        // FIXME: do something with the content bindings so they
                        // can be automatically clamped to the border of the bubble.
                        // Idea: binding.x==0 means clamp to left side, while
                        //       binding.x==-1 means clamp to the right side.
                        //       Perhaps do the same for the y values.
                        // All other values mean "leave the binding alone"
                        //
                        // Currently, there are a number of places where -4 or +4 is
                        // used as a fudge factor to line up the binding endpoints
                    }
                }                
            }
            st.push(title);
            
            var w = attr['width'], h = attr['height'];
            if (w && width<w) width=w;        
            if (h && height<h) height=h;
            
            var background = this.rect(0, 0, width, height, 5).attr({stroke: attr['stroke'], fill: attr['stroke']}).insertBefore(title);                
            st.push(background);
            st._background = background;
            
            if(!attr['fill'])attr['fill'] = '#fff';
            var foreground = this.rect(0, title_bb.height, width, height-title_bb.height, 5).attr({stroke: attr['stroke'], fill: attr['fill']}).insertAfter(background);
            st.push(foreground);    
            st._foreground = foreground;
            
            if (attr['shadow'] != false) 
            {
                var sz = attr['shadow-size'];
                if(!sz) sz = 3;
                var shadow = this.rect(sz, sz, width, height, 5).attr({fill: '#ccc', stroke: '#eee'}).insertBefore(background);               
                st.push(shadow);
            }
            
            return st;
        },
        
        
        
        ////////////////////////////////////////////////////////////
        // hp.colorcode: compute a color based on the unicode values
        //      of the letters in id
        ////////////////////////////////////////////////////////////
        
        colorcode: function(id, s, v) 
        {
            var i, sum = 0;
            var num_colors = 24;
            if (v == undefined) v = 0.99;
            if (s == undefined) s = 0.30;
            
            for(i=0; i<id.length; i++)
                sum += id.charCodeAt(i);
            var hue = sum % num_colors;
            
            // If color has already been used then use one that hasn't
            // If there are no colors left then just use the original color            
            if((hue in used_colors) && (used_colors[hue] != id))
            {
                var h;
                for(h = hue+1; h != hue; h++)
                {                
                    if(h>=num_colors)h = 0;
                    if(!(h in used_colors))
                    {
                        hue = h;
                        used_colors[hue] = id;
                        break;
                    }
                    else if(used_colors[h] == id)
                    {
                        hue = h;
                        break;
                    }
                    else if(h==hue)
                    {                        
                        break;
                    }
                }
            }
            else
            {
                used_colors[hue] = id;
            }		
            
            return Raphael.hsb2rgb(hue/(num_colors/1.0), s, v);
        },
        ////////////////////////////////////////////////////////////
        // hp.curve: a nice 4-point curve
        ////////////////////////////////////////////////////////////
        curve: function(x0, y0, x1, y1, p0, p1) 
        {
            // A nice 4-point curve from (x0,y0) to (x1,y1).
            var cx0, cx, cy0, cy1, f;
            f0 = (x1-x0) * p0;
            f1 = (x1-x0) * p1;
            cx0 = x0+f0;
            cx1 = x1-f1;
            cy0 = y0;
            cy1 = y1;
            return this.path(Raphael.format("M{0} {1}C{2} {3} {4} {5} {6} {7}", x0, y0, cx0, cy0, cx1, cy1, x1, y1))
        },
        //////////////////////////////////////////////////////////////////////
        // The hp.vc namespace contains functions for visualizing Virtual Connect
        // data structures.
        //////////////////////////////////////////////////////////////////////
        vc: 
        {
            
            ////////////////////////////////////////////////////////////
            // hp.vc.uplink: draw an uplink port
            ////////////////////////////////////////////////////////////
            net_uplink: function(port, color) {
                var st = this.set();
                var b, pp;
                var width;

                st.portid = port.id;
                var tt_text = "Connector: {0}<br>Physical layer: {1}<br>Link status: {2}<br>Duplex: {3}<br>Speed: {4} Gb<br>Supported speeds: {5}";
                if(port.telemetry)
                {
                    tt_text = tt_text + '<div id="' + port.id + '" class="telemetry_graph" style="height:110px; width:250px; z-index:150;"></div>';
                }
                var tt = Raphael.format(tt_text,
                        port.connectorType,
                        port.physicalLayer,
                        port.linkStatus,
                        port.duplexStatus,
                        port.speedGb,
                        port.supportedSpeeds);
                
                if (port.connectorType.indexOf("RJ45") != -1) {
                    pp = port_rj45;
                    width = 10;
                } else if (port.connectorType == "CX4") {
                    pp = port_cx4;
                    width = 18;
                } else if (port.connectorType == "SFP-SR"){
                    pp = port_fiber;
                    width = 16;
                } else {
                    width = 12;
                    pp = port_unknown;
                }
                
                var p = this.path(pp).attr({
                            stroke: '#000',
                            'stroke-width': 1,
                            fill: color
                        });                
                //NetworkDiagram.addTip(p.node,tt);
                b = p.getBBox();
                var px = (b.x + b.width)/2, py=(b.y + b.height)/2;
                var name = this.text(0, 0, port.portLabel).attr(smallblackfont);
                NetworkDiagram.addTip([p.node, name.node],tt);
                b = name.getBBox();
                var cx = (b.x + b.width)/2, cy=(b.y + b.height)/2;
                name.translate(px-cx, py-cy+6);
                st.push(p);
                st.push(name);
                var bl = st.binding('left', px-width+3, py);
                bl.speedGb = port.speedGb;
                var br = st.binding('right', px+width-3, py);   //Nudge the right binding left so thick lines go all the way under
                br.speedGb = port.speedGb;
                
                if(port.telemetry)
                {
                    try
                    {
                        var units = 'kbps';
                        var maxY = Math.max(Math.max.apply(Math, port.telemetry.rx_kbps), Math.max.apply(Math, port.telemetry.tx_kbps) );                    
                        
                        if(maxY > 1000)
                        {
                            units = 'Mbps';                        
                            for( x in port.telemetry.rx_kbps)
                            {
                                port.telemetry.rx_kbps[x] = port.telemetry.rx_kbps[x]/1000.0;
                            }
                            for( x in port.telemetry.tx_kbps)
                            {
                                port.telemetry.tx_kbps[x] = port.telemetry.tx_kbps[x]/1000.0;
                            }
                        }
                        maxY = Math.max(Math.max.apply(Math, port.telemetry.rx_kbps), Math.max.apply(Math, port.telemetry.tx_kbps) );                    
                        if(maxY > 1000)
                        {
                            units = 'Gbps';                        
                            for( x in port.telemetry.rx_kbps)
                            {
                                port.telemetry.rx_kbps[x] = port.telemetry.rx_kbps[x]/1000.0;
                            }
                            for( x in port.telemetry.tx_kbps)
                            {
                                port.telemetry.tx_kbps[x] = port.telemetry.tx_kbps[x]/1000.0;
                            }
                        }
                    
                        var r = Raphael(port.id);
                        r.g.text(20, 35, units);
                        r.g.text(10, 49, "In").attr({fill: '#006'});
                        r.g.text(26, 49, "Out").attr({fill: '#060'});
                        r.g.text(125, 100, "Minutes Ago");
                        tcount = parseInt(port.telemetry.properties.port_telemetry_entry_count);
                        tperiod = parseInt(port.telemetry.properties.port_telemetry_period);                                        
                        var min_ago = new Array(tcount);
                        var sec = 0;
                        for(var cnt = 0; cnt < tcount; cnt++)
                        {
                            min_ago[cnt] = -sec/60;         
                            sec+=tperiod;
                        }
                                            
                        //[0,-5,-10,-15,-20,-25,-30,-35,-40,-45,-50,-55]
                        r.g.linechart(60, 10, 175, 75, min_ago, [ port.telemetry.rx_kbps, port.telemetry.tx_kbps ], {colors: ['#006', '#060'], nostroke: false, axis: "0 0 1 1", smooth: false})
                    }
                    catch(err)
                    {
                        //If we can't draw the telemetry graph just gulp and go on.
                    }
                }
                
                return st;
            },
            ////////////////////////////////////////////////////////////
            // hp.vc.network: draw a network bubble.            
            ////////////////////////////////////////////////////////////
            network: function(network) {
                var name = network.displayName;
                var color = this.hp.colorcode(name);            
                var hi_color = this.hp.colorcode(name, .75, 0.99);       //Hilight color     
                var title = "VC Network";
                var obj = this.hp.bubble(title,
                    { stroke: '#555', fill: color, shadow: false}, titlefont, 0,
                    function(paper) {                   
                            var st = paper.set(); 
                            var net_text = name;
                            if(network.uplinkVLANId)
                            {
                                net_text += '  Vlan ID: ';
                                net_text += network.uplinkVLANId;                                
                                net_text += ' ';
                            }                                                        
                            net_text = paper.hp.text(0, 0, net_text, blackfont); 
                            net_text.mouseover(networkColorEventHandler(name, hi_color, 3));
                            net_text.mouseout(networkColorEventHandler(name, color, 2));
                            var b = net_text.getBBox();
                            st.push(net_text);
                            if(network.bottleNeck)
                            {
                                var image = "/static/img/network-diagram/bottleneck.png";                
                                var img = paper.image(image, b.width+8, 0, 15, 15);                
                                NetworkDiagram.addTip(img.node,'Possible Bottleneck');
                                st.push(img);                                                                                             
                            }
                            return st;
                        }
                );
                var b = obj.getBBox();
                var y = b.y + b.height/2;
                obj.color = color;     
                obj.hi_color = hi_color;
                obj.name = name;
                obj.binding('left', 0, y);
                obj.binding('right', b.x+b.width, y);                
                
                //Store network bubbles by name so we can light up all networks with the same name on mouseover
                //Store network downlinks too
                if(!(name in network_objs))
                {
                    network_objs[name] = new Array();
                    downlink_objs[name] = new Array();
                    portlink_objs[name] = new Array();
                    uplink_objs[name] = new Array();
                }
                network_objs[name].push(obj);
                obj._foreground.mouseover(networkColorEventHandler(name, hi_color, 3));
                obj._foreground.mouseout(networkColorEventHandler(name, color, 2));
                
                return obj;
            },
            ////////////////////////////////////////////////////////////
            // hp.vc.enet_module: draw a virtual connect ethernet module        
            ////////////////////////////////////////////////////////////
            module: function(module, enclosureType) {
                var pg = this.set();
                var i, j, y, x0, y0, x1, y1, cx, cy, b;
                var networks = [], portlines = [];
                var downlink = [];

                // Prep the title and tooltip            
                var bay = module.commonIoModuleAttrs.bay;
                var enclosureId = module.commonIoModuleAttrs.enclosureId;
                var title = Raphael.format("{0} in {1} bay {2}",
                    module.commonIoModuleAttrs.productName,
                    module.commonIoModuleAttrs.enclosureName,
                    module.commonIoModuleAttrs.bay);
                var ttip = Raphael.format("Product: {0}<br>Status: {1}<br>Serial Number: {2}<br>Rack: {3}<br>Enclosure: {4}<br>Bay: {5}<br>Firmware: {6}<br>Address: {7}<br>Management Role: {8}",                    
                    module.commonIoModuleAttrs.productName,
                    module.commonAttrs.overallStatus,
                    module.commonIoModuleAttrs.serialNumber,
                    module.commonIoModuleAttrs.rackName,
                    module.commonIoModuleAttrs.enclosureName,
                    module.commonIoModuleAttrs.bay,
                    module.commonIoModuleAttrs.fwRev,
                    module.commonIoModuleAttrs.ipaddress,
                    module.commonIoModuleAttrs.vcManagerRole);
                
                //Set the module color based on enclosure type
                var baycolor = '#aaa';
                if(enclosureType in baycolors && module.commonAttrs.overallStatus != 'MISSING' && module.commonAttrs.overallStatus != 'NO-COMM' && module.commonAttrs.overallStatus != 'CRITICAL' && module.commonAttrs.overallStatus != 'ABSENT')
                {
                    baycolor = baycolors[enclosureType][bay];
                }                        
                var attr = {'stroke': baycolor};
                
                var title_attr = smallwhitefont;
                // If the module is yellow, change the font color to black
                if (attr['stroke'] == '#fd0')
                {
                    title_attr = smallblackfont;
                }
                
                var obj = this.hp.bubble(title, attr, title_attr, ttip, function(paper) {
                    var st = paper.set();                
                    var y_net=0, y_fab=0, y_port=8;                
                    
                    // Draw each uplink port                
                    for( up in module.uplinks)
                    { 
                        if(module.uplinks[up].uplinkType == 'fc')
                        {                            
                            var obj = paper.hp.vc.fc_uplink(module.uplinks[up], baycolor);
                        }
                        else
                        {
                            var obj = paper.hp.vc.net_uplink(module.uplinks[up], baycolor);
                        }
                        obj.translate(0, y_port);                        
                        y_port += obj.getBBox().height + 8;
                        module.uplinks[up].drawing = obj;
                        pg.push(obj);
                    }
                    if(pg.length)st.push(pg);
                    
                    // Draw each network on the module                    
                    for(var n in module.networks) 
                    {
                        var obj = paper.hp.vc.network(module.networks[n]);
                        obj.translate(16, y_net);
                        y_net += obj.getBBox().height + 8;
                        module.networks[n].drawing = obj;
                        st.push(obj);
                        
                        // Save off the uplink ports - used to light-up uplinks
                        for(var p in module.networks[n].portlinks)
                        {
                            var pid = module.networks[n].portlinks[p];
                            if(!(pid in uplink_networks))uplink_networks[pid] = [];
                            uplink_networks[pid].push({'name':obj.name, 'color':obj.color, 'hi_color':obj.hi_color});
                        }
                    }
                    
                    // Draw each fabric on the module
                    y_fab = y_net;
                    for(var f in module.fabrics) 
                    {
                        var obj = paper.hp.vc.fabric(module.fabrics[f]);
                        obj.translate(16, y_fab);
                        y_fab += obj.getBBox().height + 8;
                        module.fabrics[f].drawing = obj;
                        st.push(obj);
                        
                        
                        // Save off the uplink ports - used to light-up uplinks
                        for(var p in module.fabrics[f].portlinks)
                        {
                            var pid = module.fabrics[f].portlinks[p];
                            if(!(pid in uplink_networks))uplink_networks[pid] = [];
                            uplink_networks[pid].push({'name':obj.name, 'color':obj.color, 'hi_color':obj.hi_color});
                        }
                        
                    }
                    
                    return st;
                });

                // Center all of the port icons within the pg bounding box
                if(pg.length)   //Make sure there are uplink ports first
                {
                    b = pg.getBBox();
                    cx = (b.x+b.width)/2;
                    for(i=0; i<pg.length; i++) {
                        var p = pg[i];
                        b = p.getBBox();
                        var px = (b.x+b.width)/2;
                        p.translate(cx-px, 0);
                    }
                }
                
                // Now center the pg set on the right border of the VCM box and
                // add it to the VCM box's set
                // The -4 is a fudge factor for the extra width provided by the shadow
                b = obj.getBBox();
                pg.translate(b.width-cx-4, 0);
                
                
                
                // Now draw a nice curve between all of the network bubbles and
                // their uplink ports            
                for(var n in module.networks) 
                {
                    for(p in module.networks[n].portlinks)
                    {
                        for(u in module.uplinks)
                        {
                            if(module.uplinks[u].id == module.networks[n].portlinks[p]) 
                            {
                                var start = module.networks[n].drawing.bindings['right'];
                                var end = module.uplinks[u].drawing.bindings['left'];
                                var stroke_width = stroke_width_calc(module.uplinks[u].speedGb);
                                var link = this.hp.curve(start.x, start.y, end.x, end.y, 0.75, 0.75).attr({'stroke': module.networks[n].drawing.color, 'stroke-width': stroke_width});      
                                link.stroke_width = stroke_width;
                                module.networks[n].portlinks[p].drawing = link;
                                portlink_objs[module.networks[n].displayName].push(link);
                                obj.push(link);
                            }
                        }
                    }
                }
                
                // Now draw a nice curve between all of the fabric bubbles and
                // their uplink ports            
                for(var f in module.fabrics) 
                {
                    for(p in module.fabrics[f].portlinks)
                    {
                        for(u in module.uplinks)
                        {
                            if(module.uplinks[u].id == module.fabrics[f].portlinks[p]) 
                            {
                                var end = module.fabrics[f].drawing.bindings['right'];
                                var start = module.uplinks[u].drawing.bindings['left'];
                                var stroke_width = stroke_width_calc(module.uplinks[u].speedGb);
                                var link = this.hp.curve(start.x, start.y, end.x, end.y, 0.75, 0.75).attr({'stroke': module.fabrics[f].drawing.color, 'stroke-width': stroke_width});      
                                link.stroke_width = stroke_width;
                                module.fabrics[f].portlinks[p].drawing = link;
                                portlink_objs[module.fabrics[f].displayName].push(link);
                                obj.push(link);
                            }
                        }
                    }
                }     
                
                module.drawing = obj;            
                return obj;
            },
            
                    
            ////////////////////////////////////////////////////////////
            // hp.vc.external_switch: draw an external switch
            ////////////////////////////////////////////////////////////
            external_switch: function(sw) {            
                var title = sw['remote-system-desc'] || "Unknown";
                var id = sw['remote-chassis-id'] || "Unknown";
                var ttip = title;
                title = title.split(',')[0];
                var obj = this.hp.bubble(title, {stroke: '#555' }, titlefont, ttip,
                    function(paper) {
                        var y=8;
                        var st = paper.set();
                        var name = paper.hp.text(4, 0, Raphael.format("{0} \n{1} ",                             
                                title, id), blackfont);
                        st.push(name);
                        
                        // Now draw all the ports                    
                        for(var p in sw.ports) 
                        {                        
                            var port = sw.ports[p];
                            var tt = "Port ID:  " + port['remote-port-id'] + "<br>Port description:  " + port['remote-port-desc'];
                            var c = paper.circle(-6, y, 4).attr({stroke: '#000', fill: '#0a0'});
                            NetworkDiagram.addTip(c.node,tt);
                            st.push(c);
                            st.binding(port.id, -6, y);
                            sw.drawing = st;
                            y += 16;                        
                        }
                        return st;
                    });
                    
                    return obj;
            },
            
            ////////////////////////////////////////////////////////////
            // hp.vc.external_storage: draw external storage
            ////////////////////////////////////////////////////////////
            external_storage: function(s) {            
                var title = 'FC Storage';                
                var obj = this.hp.bubble(title, {stroke: '#555' }, titlefont, '',
                    function(paper) 
                    {
                        var y=8;
                        var st = paper.set();
                        var name = paper.hp.text(4, 0, Raphael.format('WWN: {0}',s.WWN), blackfont);
                        st.push(name);
                        for(var p in s.portWWN)
                        {
                            var tt = "Port WWN:  " + s.portWWN[p];
                            var c = paper.circle(-6, y, 4).attr({stroke: '#000', fill: '#0a0'});
                            NetworkDiagram.addTip(c.node,tt);
                            st.push(c);                            
                            st.binding(s.portWWN[p], -6, y);    
                            y += 16;
                        }
                        return st;
                    });
                    
                s.drawing = obj;
                return obj;
            },
            ////////////////////////////////////////////////////////////
            // hp.vc.connect_uplinks: connect VCM uplink ports to
            // the external switches
            ////////////////////////////////////////////////////////////
            connect_uplinks: function(vcm) 
            {
                var st=this.set();
                for(var e in vcm.enclosures) 
                {                
                    for(var m in vcm.enclosures[e].allVcModuleG1s)
                    {                    
                        for(var u in vcm.enclosures[e].allVcModuleG1s[m].uplinks)
                        {
                            var uplink = vcm.enclosures[e].allVcModuleG1s[m].uplinks[u];
                            var id = uplink['id'];
                            
                            for(var es in vcm.externalSwitches) 
                            {
                                if(id in vcm.externalSwitches[es].drawing.bindings)
                                {
                                    b0 = vcm.externalSwitches[es].drawing.bindings[id];                        
                                    b1 = uplink.drawing.bindings['right'];
                                    var stroke_width = stroke_width_calc(b1.speedGb);
                                    var link = this.hp.curve(b0.x, b0.y, b1.x, b1.y, 0.75, 0.75).attr({stroke: '#000', 'stroke-width': stroke_width});                            
                                    link.toBack();
                                    link.stroke_width = stroke_width;
                                    if(uplink.linkStatus == 'LINKED-STANDBY')
                                    {
                                        link.attr({'stroke-dasharray':'-'});
                                    }
                                    link.mouseover(function (event) {
                                            this.attr({stroke: "red"});
                                        });
                                    link.mouseout(function (event) {
                                            this.attr({stroke: "black"});
                                            this.attr({stroke: "black"});   // on some graphs the uplink disappears on mouseout.
                                        });
                                                                    
                                    for(var n = 0; n < uplink_networks[id].length; n++)
                                    {
                                        uplink_objs[uplink_networks[id][n].name].push(link);
                                    } 
                                    
                                    st.push(link);                            
                                }
                            }
                            uplink.drawing.toFront();
                        }
                    }
                }
                return st;
            },
            
            ////////////////////////////////////////////////////////////
            // hp.vc.connect_fc_uplinks: connect VCM uplink ports to
            // the external storage
            ////////////////////////////////////////////////////////////
            connect_fc_uplinks: function(vcm) 
            {
                var st=this.set();
                for(var e in vcm.enclosures) 
                {                
                    for(var m in vcm.enclosures[e].allVcModuleG1s)
                    {                    
                        for(var u in vcm.enclosures[e].allVcModuleG1s[m].uplinks)
                        {
                            var uplink = vcm.enclosures[e].allVcModuleG1s[m].uplinks[u];
                            var id = uplink['portWWN'];
                            
                            for(var es in vcm.externalStorage) 
                            {
                                if(id in vcm.externalStorage[es].drawing.bindings)
                                {
                                    b0 = vcm.externalStorage[es].drawing.bindings[id];                        
                                    b1 = uplink.drawing.bindings['right'];
                                    var stroke_width = stroke_width_calc(b1.speedGb);
                                    var link = this.hp.curve(b0.x, b0.y, b1.x, b1.y, 0.75, 0.75).attr({stroke: '#000', 'stroke-width': stroke_width});                            
                                    link.toBack();
                                    link.stroke_width = stroke_width;
                                    if(uplink.portConnectStatus != 'LOGGED-IN' && uplink.portConnectStatus != 'LoggedIn')
                                    {
                                        link.attr({'stroke-dasharray':'-'});
                                    }
                                    link.mouseover(function (event) {
                                            this.attr({stroke: "red"});
                                        });
                                    link.mouseout(function (event) {
                                            this.attr({stroke: "black"});
                                        });
                                    
                                    
                                    for(var n = 0; n < uplink_networks[uplink.id].length; n++)
                                    {
                                        uplink_objs[uplink_networks[uplink.id][n].name].push(link);
                                    } 
                                    
                                    st.push(link);                            
                                }
                            }
                            uplink.drawing.toFront();
                        }
                    }
                }
                return st;
            },
            
            ////////////////////////////////////////////////////////////
            // hp.vc.connect_fc_downlinks: connect VCM FC ports to HBAs
            ////////////////////////////////////////////////////////////
            connect_fc_downlinks: function(vcm, datastores)
            {
                var st=this.set();
                for(var e in vcm.enclosures) 
                {                
                    for(var m in vcm.enclosures[e].allVcModuleG1s)
                    {                    
                        for(var f in vcm.enclosures[e].allVcModuleG1s[m].fabrics)
                        {
                            var fabric = vcm.enclosures[e].allVcModuleG1s[m].fabrics[f]
                            for(var d in vcm.enclosures[e].allVcModuleG1s[m].fabrics[f].downlinks)
                            {
                                var downlink = vcm.enclosures[e].allVcModuleG1s[m].fabrics[f].downlinks[d];
                                if(downlink.portWWN)    //Make sure the portWWN is not null - if null it could match null datastores
                                {
                                    for(var ds in datastores)
                                    {
                                        for(var h in datastores[ds].hbas)
                                        {                                        
                                            if(datastores[ds].hbas[h].portWorldWideName == downlink.portWWN)
                                            {                                        
                                                var b0 = datastores[ds].hbas[h].drawing.bindings.right;
                                                var b1 = fabric.drawing.bindings.left;       
                                                var cc = this.hp.colorcode(fabric.displayName);
                                                var stroke_width = stroke_width_calc(b0.speedGb);
                                                var link = this.hp.curve(b0.x+6, b0.y, b1.x, b1.y, 0.75, 0.75).attr({stroke: cc, 'stroke-width': stroke_width});
                                                if(datastores[ds].hbas[h].pathState != 'active')
                                                {
                                                    link.attr({'stroke-dasharray':'-'});
                                                }
                                                link.stroke_width = stroke_width;
                                                st.push(link);
                                                downlink.drawing = link;
                                                downlink_objs[fabric.displayName].push(link);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return st;
            },
                        
            ////////////////////////////////////////////////////////////
            // hp.vc.connect_vs_downlinks: connect VCM networks to
            // the server nics.  This relies on some extra data
            // being stuffed into the server NIC bindings.
            ////////////////////////////////////////////////////////////
            connect_vs_downlinks: function(vcm, vswitches) 
            {            
                var st = this.set();
                
                for(var e in vcm.enclosures) 
                {                
                    for(var m in vcm.enclosures[e].allVcModuleG1s)
                    {                    
                        for(var n in vcm.enclosures[e].allVcModuleG1s[m].networks) 
                        {
                            var network = vcm.enclosures[e].allVcModuleG1s[m].networks[n]                        
                            for(var d in network.downlinks)
                            {
                                var mac = network.downlinks[d].macAddress;                            
                                for(var k in vswitches) 
                                {
                                    for(var b in vswitches[k].bindings) 
                                    {
                                        var b1 = vswitches[k].bindings[b];
                                        if (mac == b1.mac) 
                                        {                                        
                                            var b0 = network.drawing.bindings['left'];
                                            var cc = this.hp.colorcode(network.displayName);
                                            var stroke_width = stroke_width_calc(b1.speedGb);
                                            var link = this.hp.curve(b0.x, b0.y, b1.x+5, b1.y, 0.75, 0.75).attr({stroke: cc, 'stroke-width': stroke_width});
                                            link.stroke_width = stroke_width;
                                            st.push(link);
                                            network.downlinks[d].drawing = link;
                                            downlink_objs[network.displayName].push(link);
                                        }                                            
                                    }
                                }
                            }
                        }
                    }
                }
                return st;
            },
            
            connect_iscsi_downlinks: function(vcm, datastores)
            {
                var st = this.set();
                
                for(var e in vcm.enclosures) 
                {                
                    for(var m in vcm.enclosures[e].allVcModuleG1s)
                    {                    
                        for(var n in vcm.enclosures[e].allVcModuleG1s[m].networks) 
                        {
                            var network = vcm.enclosures[e].allVcModuleG1s[m].networks[n]                        
                            for(var d in network.downlinks)
                            {
                                var downlink = network.downlinks[d];   
                                for(var ds in datastores)
                                {
                                    for(var h in datastores[ds].hbas)
                                    {
                                        if(datastores[ds].hbas[h].mac == downlink.macAddress)
                                        {                                        
                                            var b0 = datastores[ds].hbas[h].drawing.bindings.right;
                                            var b1 = network.drawing.bindings['left'];       
                                            var cc = this.hp.colorcode(network.displayName);
                                            var stroke_width = stroke_width_calc(b0.speedGb);
                                            var link = this.hp.curve(b0.x+6, b0.y, b1.x, b1.y, 0.75, 0.75).attr({stroke: cc, 'stroke-width': stroke_width});
                                            if(datastores[ds].hbas[h].pathState != 'active')
                                            {
                                                link.attr({'stroke-dasharray':'-'});
                                            }
                                            link.stroke_width = stroke_width;
                                            st.push(link);
                                            downlink.drawing = link;
                                            downlink_objs[network.displayName].push(link);
                                        }
                                    }
                                }
                            }
                        }
                    }   
                }     
            },
            
            ////////////////////////////////////////////////////////////
            // hp.vc.connect_dvsuplinks: connect VCM networks to
            // the server nics.  This relies on some extra data
            // being stuffed into the server NIC bindings.
            ////////////////////////////////////////////////////////////
            connect_dvs_downlinks: function(vcm, dvswitches) {
                var st = this.set();
                //   deviceBay = enclosure bays
                //   physicalPortMapping.mezz   'LOM' = motherboard  'MEZZ#'  for nics on daughterboards
                //   physicalPortMapping.port   PORT# is the nic number in oa
                //   physicalPortMapping.physFunc   NONE or Flexnic sub number
                
                for(var e in vcm.enclosures) 
                {                
                    for(var m in vcm.enclosures[e].allVcModuleG1s)
                    {                    
                        for(var n in vcm.enclosures[e].allVcModuleG1s[m].networks) 
                        {
                            var network = vcm.enclosures[e].allVcModuleG1s[m].networks[n]                        
                            for(var d in network.downlinks)
                            {
                                var mac = network.downlinks[d].macAddress;                            
                                for(k in dvswitches) 
                                {
                                    for(b in dvswitches[k].bindings) 
                                    {
                                        var b1 = dvswitches[k].bindings[b];                                    
                                        if (mac == b1.mac) 
                                        {
                                            var b0 = network.drawing.bindings['left'];
                                            var cc = this.hp.colorcode(network.displayName);
                                            var stroke_width = stroke_width_calc(b1.speedGb);
                                            var link = this.hp.curve(b0.x, b0.y, b1.x+4, b1.y, 0.75, 0.75).attr({stroke: cc, 'stroke-width': stroke_width});
                                            st.push(link);   
                                            network.downlinks[d].drawing = link;
                                            downlink_objs[network.displayName].push(link);
                                        }
                                    }
                                }                    
                            }
                        }
                    }
                }
                return st;
            },
            
            ////////////////////////////////////////////////////////////
            // hp.vc.uplink: draw an uplink port
            ////////////////////////////////////////////////////////////
            fc_uplink: function(port, color) {
                var st = this.set();
                var b, pp;                

                st.portid = port.id;
                var tt = Raphael.format("Connection Status: {0}<br>Port WWN: {1}<br>Connected To WWN: {2}<br>Speed: {3} Gb",
                        port.portConnectStatus,
                        port.portWWN,
                        port.connectedToWWN,
                        port.speedGb);
                
                                
                pp = port_fiber;
                                
                var p = this.path(pp).attr({
                            stroke: '#000',
                            'stroke-width': 1,
                            fill: color
                        });
                NetworkDiagram.addTip(p.node,tt);
                var pb = p.getBBox();
                var px = (pb.x + pb.width)/2, py=(pb.y + pb.height)/2;
                var name = this.text(0, 0, port.portLabel).attr({
                            font: '12px Arial',
                            fill: '#000',
                            'text-anchor': 'start'
                        });
                NetworkDiagram.addTip(name.node,tt);
                b = name.getBBox();
                var cx = (b.x + b.width)/2, cy=(b.y + b.height)/2;
                name.translate(px-cx, py-cy+6);
                st.push(p);
                st.push(name);
                var bl = st.binding('left', px-(pb.width/2), py);
                bl.speedGb = port.speedGb;
                var br = st.binding('right', px+(pb.width/2), py);                
                br.speedGb = port.speedGb;
                return st;
            },
            
            ////////////////////////////////////////////////////////////
            // hp.vc.network: draw a network bubble.
            // Can also draw FC fabric bubbles
            ////////////////////////////////////////////////////////////
            fabric: function(fabric) {
                var name = fabric.displayName;
                var color = this.hp.colorcode(name);            
                var hi_color = this.hp.colorcode(name, .75, 0.99);       //Hilight color     
                var title = "VC FC Fabric";
                var obj = this.hp.bubble(title,
                    { stroke: '#555', fill: color, shadow: false}, titlefont, 0,
                    function(paper) { 
                            var fab_name = paper.hp.text(0, 0, name, blackfont); 
                            fab_name.mouseover(networkColorEventHandler(name, hi_color, 3));
                            fab_name.mouseout(networkColorEventHandler(name, color, 2));
                            return fab_name
                        }
                );
                var b = obj.getBBox();
                var y = b.y + b.height/2;
                obj.color = color;     
                obj.hi_color = hi_color;
                obj.name = name;
                obj.binding('left', 0, y);
                obj.binding('right', b.x+b.width, y);                
                
                
                //Store network bubbles by name so we can light up all networks with the same name on mouseover
                //Store network downlinks too
                if(!(name in network_objs))
                {
                    network_objs[name] = new Array();
                    downlink_objs[name] = new Array();
                    portlink_objs[name] = new Array();
                    uplink_objs[name] = new Array();
                }
                network_objs[name].push(obj);
                
                obj._foreground.mouseover(networkColorEventHandler(name, hi_color, 3));
                obj._foreground.mouseout(networkColorEventHandler(name, color, 2));
                
                return obj;
            }
                    
        }, // end of VC namespace
        //////////////////////////////////////////////////////////////////////
        // The hp.vmware namespace contains functions for visualizing VMware
        // data structures.
        //////////////////////////////////////////////////////////////////////
        vmware: 
        {
            portgroup: function(portgroup) 
            {
                var obj = this.hp.bubble("Port Group", {fill: '#fff', stroke: '#999', shadow: false}, titlefont, 0,
                    function(paper) {
                        var st = paper.set();
                        var images = paper.set();
                        var i, j, b, y, x;

                        var name = paper.hp.text(0, 0, portgroup.name, blackfont);
                        b = name.getBBox();
                        x = b.width;
                        y = b.height;
                        images.push(paper.image("/static/img/network-diagram/portgroup.png", 0, 0, 16, 16));
                        st.binding(0, 0, 8);
                        st.push(name);

                        // Get the VM count
                        var count = paper.hp.text(0, y, Raphael.format("Virtual Machines ({0})", portgroup.vms.length), greyfont);
                        st.push(count);
                        b = count.getBBox();
                        y += b.height;
                        if (b.width > x) x = b.width;

                        // Draw the VMs
                        for(i=0; i<portgroup.vms.length; i++) {
                            var ttip = Raphael.format("CPUs:  {0}\nMemory:  {1}MB",
                                portgroup.vms[i].hardware.numCPU,
                                portgroup.vms[i].hardware.memoryMB);
                            //var vname = paper.hp.text(0, y, portgroup.vms[i].name, blackfont, ttip);
                            var vname = paper.hp.text(0, y, portgroup.vms[i].name, blackfont);
                            st.push(vname);
                            images.push(paper.image("/static/img/network-diagram/vm-on.png", 0, y, 16, 16));
                            st.binding(i+1, 0, y+8);
                            b = vname.getBBox();
                            y += b.height;
                            if (b.width > x) x = b.width;
                        }
                        images.translate(x+16, 0);
                        st.push(images);

                        b = st.getBBox();
                        for(i in st.bindings) {
                            st.bindings[i].x = b.x+b.width;
                        }
                        return st;
                    });                
                return obj;
            },
            
            hba: function(hba) 
            {
                var st = this.set();
                var x;
                
                var img;
                                
                var portMapping = "";
                var physDevName = "";
                
                                
                if(hba.physicalPortMapping)
                {                    
                    var mezz = hba.physicalPortMapping.mezz;
                    
                    // BLOMs aka Flexible LOMs should be displayed as LOM1:1-a not BLOM1:1-a to match the OA and VC GUIs                
                    if(mezz && mezz.indexOf('BLOM')==0)
                    {
                        mezz = mezz.substr(1);
                    }
                    
                    if(hba.physicalPortMapping.physFunc)
                    {                        
                        img = this.image("/static/img/network-diagram/flexnic.png", 0, 0, 16, 16);                    
                        var physFuncMap = {'VIRT-NIC1':'a', 'VIRT-NIC2':'b', 'VIRT-NIC3':'c', 'VIRT-NIC4':'d' };
                        var physFunc = hba.physicalPortMapping.physFunc;
                        if(hba.physicalPortMapping.physFunc in physFuncMap)
                        {
                            physFunc = physFuncMap[hba.physicalPortMapping.physFunc];
                        }
                        physDevName = mezz + ":" + hba.physicalPortMapping.port + '-' + physFunc;
                        portMapping = physDevName + " => Bay " + hba.physicalPortMapping.ioBay;
                    }
                    else
                    {
                        img = this.image("/static/img/network-diagram/pnic.png", 0, 0, 16, 16);                      
                        physDevName = mezz + ":" + hba.physicalPortMapping.port
                        portMapping = physDevName + " => Bay " + hba.physicalPortMapping.ioBay;
                    }
                }
                else
                {
                    img = this.image("/static/img/network-diagram/pnic.png", 0, 0, 16, 16);                                        
                }
                
                st.push(img);
                x=20;
                
                //FC adapters give speed in Gbps while FCoE adapters are in Kbps
                var speedUnit = 'Gb';
                var speed = hba.speedGb;                
                var ttip = 'HBA';
                if(hba.type == 'HostFibreChannelHba')
                {
                    ttip = Raphael.format("Device:  {0}<br>Physical Device: {1}<br>Model:  {2}<br>Port mapping: {3}<br>port WWN:  {4}<br>node WWN:  {5}<br>Driver:  {6}<br>PCI locator:  {7}<br>Speed:  {8} {9}<br>Path State: {10}",
                        hba.device,
                        physDevName,
                        hba.model,
                        portMapping,                    
                        hba.portWorldWideName,
                        hba.nodeWorldWideName,
                        hba.driver,
                        hba.pci,
                        speed,
                        speedUnit,
                        hba.pathState                        
                        );
                }
                else if(hba.type == 'HostInternetScsiHba')
                {
                    ttip = Raphael.format("Device:  {0}<br>Physical Device: {1}<br>Model:  {2}<br>Port mapping: {3}<br>MAC Address:  {4}<br>Driver:  {5}<br>PCI locator:  {6}<br>Speed:  {7} {8}<br>Path State: {9}",
                        hba.device,
                        physDevName,
                        hba.model,
                        portMapping,                    
                        hba.mac,                        
                        hba.driver,
                        hba.pci,
                        speed,
                        speedUnit,
                        hba.pathState                        
                        );
                }
                
                var name = this.hp.text(x, 0, Raphael.format("{0} {1} {2} {3}",
                            hba.device,
                            physDevName,
                            speed,
                            speedUnit), blackfont, ttip);
                
                st.push(name);  
                return st;
            },
            
            pnic: function(pnic) 
            {
                var st = this.set();
                var duplex = '';
                var img;
                var flexNIC = false;
                var portMapping = '';                
                var nicLabel = '';
                
                if(pnic.physicalPortMapping)
                {   
                    var mezz = pnic.physicalPortMapping.mezz;
                    
                    // BLOMs aka Flexible LOMs should be displayed as LOM1:1-a not BLOM1:1-a to match the OA and VC GUIs
                    if(mezz && mezz.indexOf('BLOM')==0)
                    {
                        mezz = mezz.substr(1);
                    }
                    // Convert port mapping data to the format displayed in the VC GUI
                    if(mezz == 'UNKNOWN')
                    {
                        portMapping = mezz.concat(' => Bay ', pnic.physicalPortMapping.ioBay);
                    }                    
                    else if(pnic.physicalPortMapping.portType == 'subport')
                    {
                        var physFuncMap = {'VIRT-NIC1':'a', 'VIRT-NIC2':'b', 'VIRT-NIC3':'c', 'VIRT-NIC4':'d' };                        
                        flexNIC = true;
                        nicLabel = mezz.concat(':',pnic.physicalPortMapping.port.replace('PORT',''));
                        if(pnic.physicalPortMapping.physFunc in physFuncMap)
                        {
                            nicLabel = nicLabel.concat('-', physFuncMap[pnic.physicalPortMapping.physFunc]);
                        }
                        else
                        {
                            portMapping = nicLabel.concat('-', pnic.physicalPortMapping.physFunc);
                        }
                        portMapping = nicLabel.concat(' => Bay ', pnic.physicalPortMapping.ioBay);
                    }
                    else
                    {                        
                        nicLabel = portMapping.concat(mezz,':',pnic.physicalPortMapping.port.replace('PORT',''));                                                         
                        portMapping = nicLabel.concat(' => Bay ', pnic.physicalPortMapping.ioBay);                                                         
                    }
                    
                }
                
                var speedUnit = 'Gb';
                var speed = pnic.speedGb;
                
                if (pnic.linkSpeed) 
                {                    
                    duplex = pnic.linkSpeed.duplex ? "Full" : "Half";
                }
                var mac = pnic.mac.toLowerCase().replace(/-/g, ':');
                var ttip = Raphael.format("Device:  {0}<br>Physical Device: {1}<br>Name:  {2}<br>Port mapping: {3}<br>MAC Address:  {4}<br>Driver:  {5}<br>PCI locator:  {6}<br>Speed:  {7} {10}<br>Duplex:  {8}<br>Wake-On-LAN supported:  {9}",
                    pnic.device,
                    nicLabel,
                    pnic.deviceName,
                    portMapping,                    
                    mac,
                    pnic.driver,
                    pnic.pci,
                    speed,
                    duplex,
                    pnic.wakeOnLanSupported ? "Yes" : "No",
                    speedUnit
                    );
                
                if (speed) {
                    if(flexNIC)img = this.image("/static/img/network-diagram/flexnic.png", 0, 0, 16, 16);
                    else img = this.image("/static/img/network-diagram/pnic.png", 0, 0, 16, 16);                                        
                    st.push(img);
                    x=20;
                } else {
                    if(flexNIC)img = this.image("/static/img/network-diagram/flexnic-down.png", 0, 0, 26, 16);
                    else img = this.image("/static/img/network-diagram/pnic-down.png", 0, 0, 26, 16);                                        
                    st.push(img);                
                    x=30;
                }
                st._pnic_img = img;                
                
                var name = this.hp.text(x, 0, Raphael.format("{0} {1} {2} {3}",
                            pnic.device,                            
                            nicLabel,
                            speed, speedUnit), blackfont, ttip);
                
                st.push(name);  
                st._pnic_name = name;
                
                pnic_macs[mac] = pnic;    //Save the pnics by mac to make sure our networks downlink to a drawn pnic
                return(st);
            },
            
            pnics: function(pnics) 
            {
                var nics = [];
                var obj = this.hp.bubble("Physical Adapters", {stroke: '#999', shadow: false}, titlefont, 0, function(paper) 
                {
                    var st = paper.set();
                    var i, x, y;
                    
                    if(!pnics || pnics.length==0)
                    {
                        st.push(paper.hp.text(0, 0, 'No adapters', blackfont));
                        return st;
                    }
                    pnics.sort();
                    for(i=y=0; i<pnics.length; i++) 
                    {
                        var pnic = pnics[i];
                        nics.push(pnic);
                        var nset = paper.hp.vmware.pnic(pnic);                    
                        nset.translate(0, y);
                        
                        b = nset.getBBox();
                        y += b.height;
                        b = st.binding(pnic.device, b.x+b.width, b.y+b.height/2);
                        // Put some extra info into the binding object so we can
                        // use it later to connect the nics
                        b.num = parseInt(pnic.device.substr(5));
                        b.pci = pnic.pci;
                        b.mac = pnic.mac.toLowerCase().replace(/-/g, ':');
                        b.text = nset._pnic_name;
                        b.image = nset._pnic_img;
                        b.speedGb = pnic.speedGb;
                        pnic.binding = b;
                        st.push(nset);
                    }                    
                    return st;
                });                
                
                //Adjust the nic bindings so that they all line up along the bubble edge.
                for(var i in pnics)
                {                    
                    var bb;
                    bb = obj.getBBox();
                    var x = bb.x + bb.width-5;  //No sure why we need the -5
                    pnics[i].binding.x = x;
                }
                
                return obj;
            },
     
            
            vswitch: function(vswitch) 
            {
                var pnics;
                var pg = [];
                var obj = this.hp.bubble(vswitch.name, {stroke: '#559'}, titlefont, 0, function(paper) 
                {
                    var st = paper.set();
                    var i, j, x, y, x0, y0;
                    var b, p;
                    
                    if(vswitch.port_groups.length == 0)
                    {
                       st.push(paper.hp.text(0, 0, "No associated port groups", blackfont));
                       st.push(paper.hp.text(0, 30, "xx", smallwhitefont));
                    }
                    
                    for(i=y=0; i<vswitch.port_groups.length; i++) {
                        p = paper.hp.vmware.portgroup(vswitch.port_groups[i]);                            
                        p.translate(0, y);
                        pg.push(p);
                        st.push(p);
                        b = p.getBBox();
                        y += b.height + 4;
                    }

                    b = st.getBBox();
                    x = b.width + 6;
                    st.push(paper.rect(x, 0, 32, b.height).attr({stroke: '#444', fill: '#999', 'stroke-width': 3}));
                    
                    for(j=0; j<pg.length; j++) {                        
                        for(i in pg[j].bindings) {                            
                            y = pg[j].bindings[i].y;
                            x0 = pg[j].bindings[i].x + 4;
                            var c = paper.circle(x+1, y, 4).attr({stroke: '#000', fill: '#0a0'});
                            st.push(c);
                            if (j==0 && i==0) {
                                st.push(paper.circle(x+31, y, 4).attr({stroke: '#000', fill: '#0a0'}));
                                st.push(paper.path(Raphael.format("M{0} {1}L{2} {3}", x0, y, x+40, y)).attr({'stroke-width': 2}).insertBefore(c));
                                y0 = y;
                            } else {
                                st.push(paper.path(Raphael.format("M{0} {1}L{2} {3}L{4} {5}", x0, y, x+16, y, x+16, y0)).attr({'stroke-width': 2}).insertBefore(c));
                            }
                        }
                    }
                                    
                    pnics = paper.hp.vmware.pnics(vswitch.pnics);
                    pnics.translate(x+39, 0);
                    st.push(pnics);
                    st.bindings = pnics.bindings;
                    
                    return st;
                });                
                obj.pnic = pnics;
                obj.portgroup = pg;
                return obj;
            },

            
            dvuplinkportgroup: function(dvp_up) 
            {                        
                var obj = this.hp.bubble(dvp_up.name, {stroke: '#559', shadow: false}, titlefont, 0, function(paper) 
                {
                    var st = paper.set();
                    var y,i,j,k;
                    // Loop through uplink ports
                    for(i=y=0; i < dvp_up.uplinks.length; i++) {
                        var pname = paper.hp.text(0, y, dvp_up.uplinks[i].name, blackfont);
                        st.push(pname);                        
                        b = pname.getBBox();
                        y += b.height;        
                        
                        // Loop through nics on the port
                        for(j=0; j < dvp_up.uplinks[i].pnics.length; j++)
                        {                                                
                            var pnic = dvp_up.uplinks[i].pnics[j];                            
                            //var t = paper.hp.text(10, y, pnic.device, blackfont);
                            var nset = paper.hp.vmware.pnic(pnic);
                            nset.translate(10, y);                        
                            b = nset.getBBox();
                            y += b.height;   
                            var bind = st.binding(pnic.device, b.x+b.width, b.y+b.height/2);                        
                            bind.mac = pnic.mac.toLowerCase().replace(/-/g, ':');         
                            // Put some extra info into the binding object so we can
                            // use it later to connect the nics
                            bind.num = parseInt(pnic.device.substr(5));
                            bind.pci = pnic.pci;
                            bind.mac = pnic.mac.toLowerCase().replace(/-/g, ':');
                            bind.text = nset._pnic_name;
                            bind.image = nset._pnic_img;
                            bind.speedGb = pnic.speedGb;
                            st.push(nset);                        
                        }
                        
                    }
                    return st;
                });
                return obj
            },
            
            dvdnlinkportgroup: function(dvp) 
            {
                           
                var obj = this.hp.bubble(dvp.name, {stroke: '#559', shadow: false}, titlefont, 0, function(paper) {
                    var y=0;
                    var b, i, j, n, vm_cnt;
                    var st = paper.set();
                    
                    vlanid = '--';                
                    if(dvp.vlanId!=0)vlanid = dvp.vlanId;                
                    var pname = paper.hp.text(0, y, Raphael.format("VLANID {0}", vlanid), blackfont);
                    st.push(pname);                        
                    b = pname.getBBox();
                    y += b.height;                   
                                    
                    var vms_label = paper.hp.text(0, y, Raphael.format("Virtual Machines ({0})", dvp.vms.length), greyfont);
                    st.push(vms_label);                        
                    b = vms_label.getBBox();
                    y += b.height;                   
                    
                    // List the VMs in this port group
                    for(i=0; i < dvp.vms.length; i++)
                    {                    
                        vms_label = paper.hp.text(0, y, dvp.vms[i].name, blackfont);
                        st.push(vms_label);                        
                        b = vms_label.getBBox();
                        y += b.height;   
                        for(n=0; n < dvp.vms[i].nics.length; n++)
                        {
                            var nic_label = paper.hp.text(10, y, dvp.vms[i].nics[n].macAddress, smallblackfont);
                            st.push(nic_label);                        
                            b = vms_label.getBBox();
                            y += b.height;
                        }
                    }
                    
                    return st;
                });
                return obj;
            },
            
            dvswitch: function(dvs) 
            {
                var obj = this.hp.bubble(dvs.name, {stroke: '#559'}, titlefont, 0, function(paper) 
                {
                    var st = paper.set();                
                    var i, x, y;
                    var b, bind;
                    var upb;
                    var dnb = new Array();
                    
                    x=0;
                    y=10;
                                    
                    for(i=0; i<dvs['downlink_port_groups'].length; i++)
                    {
                        var obj_dnlinkportgroup = paper.hp.vmware.dvdnlinkportgroup(dvs['downlink_port_groups'][i]);
                        b = obj_dnlinkportgroup.getBBox();
                        
                        if(b.width>x)
                        {
                            x=b.width; // save width of biggest for next column
                        }
                        
                        obj_dnlinkportgroup.translate(0,y);
                        dnb.push(obj_dnlinkportgroup.getBBox());
                        st.push(obj_dnlinkportgroup);
                        y = y+b.height+10;                                        
                        
                    }
                                    
                    var obj_uplinkportgroup = paper.hp.vmware.dvuplinkportgroup(dvs.uplink_port_groups[0]);
                    obj_uplinkportgroup.translate(x+10+40+10,10);
                    upb = obj_uplinkportgroup.getBBox();
                    st.push(obj_uplinkportgroup);
                    
                    b = st.getBBox();
                    st.bindings = obj_uplinkportgroup.bindings;
                    for(i in st.bindings) {
                        st.bindings[i].x = b.x+b.width-4;
                    }
                    st.push(paper.rect(x+10, 0, 40, b.height+20).attr({stroke: '#444', fill: '#999', 'stroke-width': 2}));
                    
                    st.push(paper.rect(x+40, 10, 10, upb.height).attr({stroke: '#000', fill: '#bbb', 'stroke-width': 2}));
                    for(i=y=0;i<dnb.length;i++)
                    {
                        st.push(paper.rect(x+10, 10+y, 10, dnb[i].height).attr({stroke: '#000', fill: '#bbb', 'stroke-width': 2}));
                        y+=dnb[i].height+10;
                    }
                    
                    if(dnb.length)
                    {
                        st.push(paper.path(Raphael.format("M{0} {1}L{2} {3}", x+40, 10+(upb.height/2), x+30, 10+(upb.height/2))).attr({stroke: '#000', 'stroke-width': 2}));
                        
                        for(i=y=0;i<dnb.length;i++)
                        {
                            st.push(paper.path(Raphael.format("M{0} {1}L{2} {3}", x+20, 10+y+(dnb[i].height/2), x+30, 10+y+(dnb[i].height/2))).attr({stroke: '#000', 'stroke-width': 2}));
                            st.push(paper.path(Raphael.format("M{0} {1}L{2} {3}", x+30, 10+y+(dnb[i].height/2),x+30, 10+(upb.height/2) )).attr({stroke: '#000', 'stroke-width': 2}));
                            y+=dnb[i].height+10;
                        }
                    }
                    return st;
                });
                
                return obj;
            },
   
            datastore: function(datastore, width)
            {
                var title = Raphael.format("Datastore - {0}", datastore.name);                    
                var ttip = Raphael.format("Name: {0}<br>Free Space: {1}<br>Max File Size: {2}",
                    datastore.name,
                    datastore.freeSpace,
                    datastore.maxFileSize);
                
                //Adjust for shadow size - keep the datastore from growing by shadow size
                width=width - 3;
                
                var obj = this.hp.bubble(title, {'stroke': '#559', 'width': width, 'shadow-size': 3}, titlefont, ttip, function(paper) 
                {
                    var st = paper.set();                
                    var i, x, y, d;
                    var b, bind;
                    var upb;
                    var dnb = new Array();
                    
                    x=0;
                    y=0;
                    
                    var vms_title = paper.hp.text(0, y, Raphael.format("Virtual Machines ({0})", datastore.vms.length), greyfont);                        
                    st.push(vms_title)
                    b = vms_title.getBBox();
                    y+= b.height;
                    for(i in datastore.vms )
                    {                        
                        var vm = paper.hp.text(0, y, datastore.vms[i].name, blackfont);
                        b = vm.getBBox();
                        y += b.height;
                        st.push(vm);
                        for(d in datastore.vms[i].hardware.disks)
                        {
                            var disk_tt = Raphael.format("Name: {0}<br>Summary: {1}", datastore.vms[i].hardware.disks[d].label, datastore.vms[i].hardware.disks[d].summary);
                            disk = paper.hp.text(20, y, Raphael.format("{0}", datastore.vms[i].hardware.disks[d].label), smallblackfont, disk_tt);
                            b = disk.getBBox();
                            y += b.height;
                            st.push(disk);
                        }
                    }

                    x=0;
                    y=0;
                    for(i=0; i<datastore.hbas.length; i++)
                    {
                        var nset = paper.hp.vmware.hba(datastore.hbas[i]);                    
                        b = nset.getBBox();
                        nset.translate(width-b.width-12, y);                        
                        b = nset.getBBox();
                        y += b.height;
                        var binding = nset.binding('right', b.x+b.width, b.y+b.height/2);                        
                        binding.speedGb = datastore.hbas[i].speedGb;
                        datastore.hbas[i].drawing = nset;
                        st.push(nset);                            
                    }
                    
                    return st;
                });
                return obj;
            }
            
        } // end of vmware namespace
    }; // end of hp namespace
})();

// vim: ts=4 sts=4 sw=4 expandtab:
