var monthNames = [ "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Окрябрь", "Ноябрь", "Декабрь" ];

var ofMounth = monthNames.map(function(mounth) {
    return (mounth + 'а').replace(/[ьй]а$/, 'я');
}); 

function onClick(fixedPeriod)
{
	$("fixedPeriod").value = fixedPeriod;
	$("submit").click();
}


function drawChart(list, fixedPeriod, counter)
{
	var maxY = 120;
	if(counter == "3")
	{
		maxY = 90;
	}
	var LEVEL = 3;
	var DATE = 1;

    var el = $("chart");
    el.innerHTML = '';
   	var r = Raphael(el);
   	var y = [];
   	var x = [];

   	for(i=0; i < list.records.size(); i++ )
   	{
   		y.push(list.records[i][LEVEL]);
   		x.push(new Date(list.records[i][DATE]));
   	}
    // Creates a simple line chart at 10, 10
    // width 300, height 220
    // x-values: [1,2,3,4,5], y-values: [10,20,15,35,30]
    var xstep = null; 
   
    if(fixedPeriod == "hour")
    {
    	var dateEnd = new Date();
    	dateEnd.setMinutes(0,0,0);
    	dateEnd.setHours(dateEnd.getHours()+1);
    	
    	var dateBegin = new Date(dateEnd.toString());
    	dateBegin.setHours(dateBegin.getHours()-6);

    	xstep = 6;

//    	10 августа 2014
//    	(Текущая дата в таком формате)
    	setChartLabel(dateEnd.getDate() + " " + ofMounth[dateEnd.getMonth()] + " " + dateEnd.getFullYear());
    }
    else if(fixedPeriod == "day")
    {
    	var dateEnd = new Date();
    	dateEnd.setMinutes(0,0,0);
    	dateEnd.setHours(dateEnd.getHours()+1);
    	
    	var dateBegin = new Date(dateEnd.toString());
    	dateBegin.setHours(dateBegin.getHours()-24);

    	xstep = 24;
    	
//    	10 августа 2014
//    	(Текущая дата в таком формате)
    	setChartLabel(dateEnd.getDate() + " " + ofMounth[dateEnd.getMonth()] + " " + dateEnd.getFullYear());
    }
    else if(fixedPeriod == "week")
    {
    	var dateEnd = new Date();
    	dateEnd.setHours(0,0,0,0);
    	dateEnd.setDate(dateEnd.getDate()+1);
    	
    	var dateBegin = new Date(dateEnd.toString());
    	dateBegin.setDate(dateBegin.getDate()-7);

    	xstep = 7;
    	
    	//17 февраля - 16 марта 2014
    	setChartLabel(dateBegin.getDate() + " " + ofMounth[dateBegin.getMonth()] + " " + dateBegin.getFullYear() + " - " + dateEnd.getDate() + " " + ofMounth[dateEnd.getMonth()] + " " + dateEnd.getFullYear());
    }
    else if(fixedPeriod == "month")
    {
    	var dateEnd = new Date();
    	dateEnd.setHours(0,0,0,0);
    	dateEnd.setDate(dateEnd.getDate()+1);
    	
    	var dateBegin = new Date(dateEnd.toString());
    	dateBegin.setMonth(dateBegin.getMonth()-1);
    	//dateBegin.setDate(dateBegin.getDate()-31);
    	
    	var timeDiff = Math.abs(dateBegin - dateEnd);
    	var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24));

    	xstep = diffDays;
    	//17 февраля - 16 марта 2014
    	setChartLabel(dateBegin.getDate() + " " + ofMounth[dateBegin.getMonth()] + " " + dateBegin.getFullYear() + " - " + dateEnd.getDate() + " " + ofMounth[dateEnd.getMonth()] + " " + dateEnd.getFullYear());
    }
    else if(fixedPeriod == "year")
    {
    	var dateEnd = new Date();
    	dateEnd.setHours(0,0,0,0);
    	dateEnd.setDate(1);
    	dateEnd.setMonth(dateEnd.getMonth()+1);
    	
    	var dateBegin = new Date(dateEnd.toString());
    	dateBegin.setMonth(dateBegin.getMonth()-12);

    	xstep = 12;
    	//август 2013 - июль 2014 
    	setChartLabel(ofMounth[dateBegin.getMonth()] + " " + dateBegin.getFullYear() + " - " + ofMounth[dateEnd.getMonth()] + " " + dateEnd.getFullYear());
    }
    
    console.log("dateBegin=" + dateBegin);
    console.log("dateEnd=" + dateEnd);
    
    lines = r.linechart(30,30,el.getWidth()-70,el.getHeight()-100,
    		[x, [dateBegin, dateEnd]],
    		[y, [0, maxY]], 
    		{
    			axis:"0 0 1 1", 
    			axisystep: 20, 
    			axisxstep: xstep,
    			colors: [
    			          "#555599",       // the second line is blue
    			          "transparent"    // the third line is invisible
    			        ]
    		});
    lines.axis[1].text.attr({font:"16pt Arial"});
    lines.axis[0].text.attr({font:"16pt Arial"});
    
    if(fixedPeriod == "hour")
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText));
	        console.log(date);
	        label.attr({'text': date.getHours(), 'y': originalY+30 });
	      });
    }
    else if(fixedPeriod == "day")
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText));
	        console.log(date);
	        label.attr({'text': date.getHours(), 'y': originalY+30 });
	      });
    }
    else if(fixedPeriod == "week")
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText));
	        console.log(date);
	        label.attr({'text': date.getDate(), 'y': originalY+30 });
	      });
    }
    else if(fixedPeriod == "month")
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText));
	        console.log(date);
	        label.attr({'text': date.getDate(), 'y': originalY+30 });
	      });
    }
    else if(fixedPeriod == "year")
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText));
	        console.log(date);
	        label.attr({'text': monthNames[date.getMonth()], 'y': originalY+30 });
	      });
    }
    else
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText))
	        label.rotate(20);
	        label.attr({'text': date.toLocaleString(), 'y': originalY+30 });
	      });
    }

};

function setChartLabel(/*String*/ text)
{
	$("chartlabel").update(text);
}