function refreshData() {
	new Ajax.Request('/graph?json=1', 
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
			var param = transport.responseText.evalJSON();
			
            for(var j=0; j < param.data.size(); j++)
            {
                var el = $("chart" + (j+1));
                var list = param.data[1];
                el.innerHTML = '';
            	var r = Raphael(el);
            	var y = [];
            	var x = [];
            	for(i=0; i < list.size(); i++ )
            	{
            		y.push(list[i].level);
            		x.push(new Date(list[i].date));
            	}
            	
                    // Creates a simple line chart at 10, 10
                    // width 300, height 220
                    // x-values: [1,2,3,4,5], y-values: [10,20,15,35,30]
                r.linechart(10,10,el.getWidth(),el.getHeight(),x,y);	
            }
            
			dataFailureCount = 0;
		}
	});
}