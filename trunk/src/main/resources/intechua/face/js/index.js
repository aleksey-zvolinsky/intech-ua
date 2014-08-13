var alarm_audio = new Howl({
	urls: ['/alarm.mp3'],
	loop: true,
	autoplay: true
});
alarm_audio.mute();

var failureCount = 0;
var dataFailureCount = 0;

function onClickJournal(counter)
{
	var newtab = window.open( '/journal?counter'+counter+'=checked', 'intechua-tab-2' )
}

function onClickExpand(counter)
{
	var newtab = window.open( '/graph?counter='+counter+'&fixedPeriod=hour', 'intechua-tab-2' )
}

function refreshStatus(pe) {
	new Ajax.Request('/indexstatus?json=1', 
	{
		method:'get',
		onException: function()
		{
			if(failureCount++ > 10)
			{
				window.location.reload();
			}
		},
		onSuccess: function(transport)
		{
			var param = transport.responseText.evalJSON();
			var packet = param.last_packet.records[0];
			
			packet.failure = false;
//			param.volume = true;
			
			packet.date = new Date(packet[1]);
			packet.level1 = packet[3];
			packet.level2 = packet[4];
			packet.level3 = packet[5];
			
			packet.rawlevel1 = packet[6];
			packet.rawlevel2 = packet[7];
			packet.rawlevel3 = packet[8];
			
			packet.power = packet[11];
			packet.flowmeterState1 = (packet.power == 1);
			packet.flowmeterState2 = (packet.power == 1);
			packet.flowmeterState3 = (packet.power == 1);
			
			packet.state = packet[9];
			packet.connection_level = packet[10];
			
			
			if(param.volume.toString() == "true")
			{
				$$(".all .voice").invoke('addClassName','on');
			}
			else
			{
				$$(".all .voice").invoke('removeClassName','on');
			}
			
			
			
			var diff = new Date() - packet.date;
			var diffminutes = Math.floor((diff/1000)/60);
			
			$$(".all .general_alert").invoke('removeClassName','off');
			$$(".all .general_alert").invoke('removeClassName','on');
			$$(".all .general_alert").invoke('removeClassName','woff');
			if(packet.state == 200)
			{
				$$(".all .general_alert").invoke('addClassName','woff');
				packet.failure = true;
			}
			else if(diffminutes > 5)
			{
				$$(".all .general_alert").invoke('addClassName','off');
			}
			else
			{
				$$(".all .general_alert").invoke('addClassName','on');
			}
			
			if(packet.power == 1)
			{
				$$(".all .power").invoke('addClassName','on');
			}
			else
			{
				$$(".all .power").invoke('removeClassName','on');
			}
			
			if(packet.rawlevel1 > 200)
			{
				$$("#column1 .power").invoke('addClassName','on');
			}
			else
			{
				$$("#column1 .power").invoke('removeClassName','on');
			}
			
			if(packet.rawlevel2 > 200)
			{
				$$("#column2 .power").invoke('addClassName','on');
			}
			else
			{
				$$("#column2 .power").invoke('removeClassName','on');
			}
			
			if(packet.rawlevel3 > 200)
			{
				$$("#column3 .power").invoke('addClassName','on');
			}
			else
			{
				$$("#column3 .power").invoke('removeClassName','on');
			}
			
			if(packet.flowmeterState1)
			{
				$$("#column1 .error").invoke('addClassName','off');
			}
			else
			{
				$$("#column1 .error").invoke('removeClassName','off');
			}
			
			if(packet.flowmeterState2)
			{
				$$("#column2 .error").invoke('addClassName','off');
			}
			else
			{
				$$("#column2 .error").invoke('removeClassName','off');
			}	
			
			if(packet.flowmeterState3)
			{
				$$("#column3 .error").invoke('addClassName','off');
			}
			else
			{
				$$("#column3 .error").invoke('removeClassName','off');
			}
			
			$("value1").update(packet.level1);
			$("value2").update(packet.level2);
			$("value3").update(packet.level3);
			
			$w($("image1").className).each(function(a){if(a != "image")$("image1").removeClassName(a);});
			$("image1").addClassName("l"+Math.round(packet.level1/10)*10);
			
			$w($("image2").className).each(function(a){if(a != "image")$("image2").removeClassName(a);});
			$("image2").addClassName("l"+Math.round(packet.level2/10)*10);
			
			
			if ( packet.failure && (param.volume.toString() == "true") )
			{
				alarm_audio.unmute();
			}
			else
			{
				alarm_audio.mute();
			}
			
			failureCount = 0;
		}
	});
}
function refreshStatusFirst(pe) 
{
	refreshStatus(pe);
	pe.stop();
	new PeriodicalExecuter( refreshStatus, 10 );
}
new PeriodicalExecuter( refreshStatusFirst, 0.1 );


function refreshData(pe) {
	new Ajax.Request('/indexdata?json=1', 
	{
		method:'get',
		onException: function()
		{
			if(dataFailureCount++ > 10)
			{
				window.location.reload();
			}
		},
		onSuccess: function(transport)
		{
			var LEVEL = 3;
			var DATE = 1;
			var param = transport.responseText.evalJSON();
			var max = [110,110,90];
            for(var j=0; j < param.data.size(); j++)
            {
                var el = $("chart" + (j+1));
                var list = param.data[j].records;
                el.innerHTML = '';
            	var r = Raphael(el);
            	var y = [];
            	var x = [];
            	for(i=0; i < list.size(); i++ )
            	{
            		y.push(list[i][LEVEL]);
            		x.push(new Date(list[i][DATE]));
            	}
            	
            	
            	var dateEnd = new Date();
            	dateEnd.setMinutes(0,0,0);
            	dateEnd.setHours(dateEnd.getHours()+1);
            	
            	var dateBegin = new Date(dateEnd.toString());
            	dateBegin.setHours(dateBegin.getHours()-6);

            	xstep = 6;
            	
                    // Creates a simple line chart at 10, 10
                    // width 300, height 220
                    // x-values: [1,2,3,4,5], y-values: [10,20,15,35,30]
            	lines = r.linechart(30,30,el.getWidth()-40,el.getHeight()-100,
            			[x, [dateBegin, dateEnd]],
            			[y, [0, max[j]]], 
            			{
            				axis:"0 0 1 1", 
            				axisystep: max[j]/10, 
            				axisxstep: xstep,
            				colors: [
            				         "#555599",       // the second line is blue
            				         "transparent"    // the third line is invisible
            				         ]});
                

            	
            	
                lines.axis[1].text.attr({font:"16px Arial"});
                lines.axis[0].text.attr({font:"16px Arial"});
                lines.axis[0].text.items.each( 
                	function ( label, index ) 
                	{
	                    //Get the timestamp you saved
	                    var originalText = label.attr('text');
	                    var originalY = label.attr('y');
	                    var date = new Date(parseInt(originalText))
	                    if(date.toString() != "Invalid Date")
	                    {
	                    	//label.rotate(20);
		                    label.attr({'text': date.getHours(), 'y': originalY+20 });	
	                    }
	                    
                    });
            }
            
			dataFailureCount = 0;
		}
	});
}

function refreshDataFirst(pe) 
{
	refreshData(pe);
	pe.stop();
	new PeriodicalExecuter( refreshData, 10 );
}

new PeriodicalExecuter( refreshDataFirst, 0.1 );