package com.hp.asi.hpic4vc.ui.utils.networkDiagram
{
	import mx.graphics.SolidColor;
	import mx.graphics.SolidColorStroke;
	import mx.utils.HSBColor;
	
	import spark.primitives.Ellipse;
	import spark.primitives.Line;
	import spark.primitives.Rect;
	
	public class Utilities
	{
		public static var used_colors:Array = new Array();
		public static var _levels:Array = ['bytes', 'Kb', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
		
		public static function colorcode(id:String, s:Number=0, v:Number=0):uint 
		{
			var i:Number, sum:Number = 0;
			var num_colors:Number = 24;
			if (v == 0) 
				v = 0.61;
			if (s == 0) 
				s = 0.50;
			
			for(i=0; i<id.length; i++)
				sum += id.charCodeAt(i);
			var hue:Number = sum % num_colors;
			
			// If color has already been used then use one that hasn't
			// If there are no colors left then just use the original color
			if((hue in used_colors) && (used_colors[hue] != id))
			{
				var h:Number;
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
				}
			}
			else
			{
				used_colors[hue] = id;
			}		
			var tmpColor:Number = HSBColor.convertHSBtoRGB(hue/(num_colors/1.0) * 360, s, v );
			
			return tmpColor;
		}
		
		public static function bytesToString(bytes:Number):String
		{
			var index:uint = Math.floor(Math.log(bytes)/Math.log(1024));
			return (bytes/Math.pow(1024, index)).toFixed(2) + " " + _levels[index];
		}
		
		public static function drawLine(x0:Number,y0:Number,x1:Number,y1:Number):Line{
			var line:Line=new Line();
			line.xFrom=x0;
			line.yFrom=y0;
			line.xTo=x1;
			line.yTo=y1;
			var stroke:SolidColorStroke=new SolidColorStroke(0x000000,2);
			line.stroke=stroke;
			
			return line;
		}
		
		public static function drawCircle():Ellipse{
			var circle:Ellipse=new Ellipse();
			circle.width=8;
			circle.height=8;
			var stroke:SolidColorStroke=new SolidColorStroke(0x000000,1);
			circle.stroke=stroke;
			var fill:SolidColor=new SolidColor(0x00AA00);
			circle.fill=fill;
			return circle;
		}
		
		public static function drawRect():Rect{
			var rect:Rect=new Rect();
			rect.width=8;
			rect.height=20;
			var stroke:SolidColorStroke=new SolidColorStroke(0x000000,2);
			rect.stroke=stroke;
			var fill:SolidColor=new SolidColor(0xBBBBBB);
			rect.fill=fill;
			return rect
		}
	}
	
}