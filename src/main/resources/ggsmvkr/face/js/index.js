var alarm_audio = new Howl({
	urls: ['/alarm.mp3'],
	loop: true,
	autoplay: true
});
alarm_audio.mute();

var failureCount = 0;

function refreshStatus(pe) {
	new Ajax.Request('/param2?json=1', 
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
			var packet = param.last_packet;
			if(packet.flowmeterState1)
			{
				$$("#column1 .error").invoke('addClassName','on');
			}
			else
			{
				$$("#column1 .error").invoke('removeClassName','on');
			}
			
			if(packet.flowmeterState2)
			{
				$$("#column2 .error").invoke('addClassName','on');
			}
			else
			{
				$$("#column2 .error").invoke('removeClassName','on');
			}	
			
			if(packet.flowmeterState3)
			{
				$$("#column3 .error").invoke('addClassName','on');
			}
			else
			{
				$$("#column3 .error").invoke('removeClassName','on');
			}
			
			$("value1").update(packet.level1);
			$("value2").update(packet.level2);
			$("value3").update(packet.level3);
			
			$w($("image1").className).each(function(a){if(a != "image")$("image1").removeClassName(a);});
			$("image1").addClassName("l"+Math.round(packet.level1/10)*10);
			
			$w($("image2").className).each(function(a){if(a != "image")$("image2").removeClassName(a);});
			$("image2").addClassName("l"+Math.round(packet.level2/10)*10);
			//;
			
			/*
			{
				last_packet: {
					id: 0,
					power: false,
					sensorPower: false,
					flowmeterState1: false,
					flowmeterState2: false,
					flowmeterState3: false,
					alert: false,
					reserve1: false,
					reserve2: false,
					level1: 33,
					level2: 55,
					level3: 66
				}
			} */
			
//			if ( failure && param.settings.alarm_enabled )
//			{
//				alarm_audio.unmute();
//			}
//			else
//			{
//				alarm_audio.mute();
//			}
			
			failureCount = 0;
		}
	});
}
function refreshStatusFirst(pe) 
{
	refreshStatus(pe);
	pe.stop();
	new PeriodicalExecuter( refreshStatus, 5 );
}
new PeriodicalExecuter( refreshStatusFirst, 0.1 );