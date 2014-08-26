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
	var maxY = 110;
	var ystep = 11;
	if(counter == "3")
	{
		maxY = 90;
		ystep = 9;
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

    }
    else if(fixedPeriod == "day")
    {
    	var dateEnd = new Date();
    	dateEnd.setMinutes(0,0,0);
    	dateEnd.setHours(dateEnd.getHours()+1);
    	
    	var dateBegin = new Date(dateEnd.toString());
    	dateBegin.setHours(dateBegin.getHours()-24);

    	xstep = 24;
    	
    }
    else if(fixedPeriod == "week")
    {
    	var dateEnd = new Date();
    	dateEnd.setHours(0,0,0,0);
    	dateEnd.setDate(dateEnd.getDate()+1);
    	
    	var dateBegin = new Date(dateEnd.toString());
    	dateBegin.setDate(dateBegin.getDate()-7);

    	xstep = 7;
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
    }
    
    console.log("dateBegin=" + dateBegin);
    console.log("dateEnd=" + dateEnd);
    
    
    var xTransp = [dateBegin, dateBegin, dateEnd, dateEnd];
	var yTransp = [0, maxY, maxY, 0];
	
	var xLines = [];
	var yLines = [];
	var lineColors = [];
	
	if(x.length < 4)
	{
		xLines = [xTransp, x];
		yLines = [yTransp, y];
    	lineColors = [
    	              	"transparent",    // the first line is invisible
				         "#555599"       // the second line is blue
			         ];
	}
	else
	{
		xLines = [x, xTransp];
		yLines = [y, yTransp];
    	lineColors = [
    	              	"#555599",       // the first line is blue
    	              	"transparent"    // the second line is invisible
			         ];
	}
    
    lines = r.linechart(30,30,el.getWidth()-70,el.getHeight()-100,
    		xLines,
    		yLines, 
    		{
    			axis:"0 0 1 1", 
    			axisystep: ystep, 
    			axisxstep: xstep,
    			colors: lineColors
    		});
    lines.axis[1].text.attr({font:"16pt Arial"});
    lines.axis[0].text.attr({font:"16pt Arial"});
    
    var yShift = 40;
    
    if(fixedPeriod == "hour")
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText));
	        console.log(date);
	        label.attr({'text': date.getHours(), 'y': originalY+yShift });
	      });
//    	10 августа 2014
//    	(Текущая дата в таком формате)
    	setChartLabel(r, dateEnd.getDate() + " " + ofMounth[dateEnd.getMonth()] + " " + dateEnd.getFullYear(), lines);
    }
    else if(fixedPeriod == "day")
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText));
	        console.log(date);
	        label.attr({'text': date.getHours(), 'y': originalY+yShift });
	      });
//    	10 августа 2014
//    	(Текущая дата в таком формате)
    	setChartLabel(r, dateEnd.getDate() + " " + ofMounth[dateEnd.getMonth()] + " " + dateEnd.getFullYear(), lines);
    }
    else if(fixedPeriod == "week")
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText));
	        console.log(date);
	        label.attr({'text': date.getDate(), 'y': originalY+yShift });
	      });
    	//17 февраля - 16 марта 2014
    	setChartLabel(r, dateBegin.getDate() + " " + ofMounth[dateBegin.getMonth()] + " " + dateBegin.getFullYear() + " - " + dateEnd.getDate() + " " + ofMounth[dateEnd.getMonth()] + " " + dateEnd.getFullYear(), lines);
    }
    else if(fixedPeriod == "month")
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText));
	        console.log(date);
	        label.attr({'text': date.getDate(), 'y': originalY+yShift });
	      });

	    //17 февраля - 16 марта 2014
    	setChartLabel(r, dateBegin.getDate() + " " + ofMounth[dateBegin.getMonth()] + " " + dateBegin.getFullYear() + " - " + dateEnd.getDate() + " " + ofMounth[dateEnd.getMonth()] + " " + dateEnd.getFullYear(), lines);
    }
    else if(fixedPeriod == "year")
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText));
	        console.log(date);
	        label.attr({'text': monthNames[date.getMonth()], 'y': originalY+yShift });
	      });
	    
    	//август 2013 - июль 2014 
    	setChartLabel(r, monthNames[dateBegin.getMonth()] + " " + dateBegin.getFullYear() + " - " + monthNames[dateEnd.getMonth()] + " " + dateEnd.getFullYear(), lines);
    }
    else
    {
	    lines.axis[0].text.items.each( function ( label, index ) {
	        //Get the timestamp you saved
	        var originalText = label.attr('text');
	        var originalY = label.attr('y');
	        var date = new Date(parseInt(originalText))
	        label.rotate(20);
	        label.attr({'text': date.toLocaleString(), 'y': originalY+yShift });
	      });
    }

};

function setChartLabel(raphael, /*String*/ text, /*chart*/ chart)
{
	//$("chartlabel").update(text);
    var el = $("chart");

   	raphael.text(el.getWidth()/2, el.getHeight()-60, text).attr({ font: "16pt Arial" });
}