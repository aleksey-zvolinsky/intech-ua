/* This notice must be untouched at all times.

wz_tooltip.js	 v. 4.0

The latest version is available at
http://www.walterzorn.com
or http://www.devira.com
or http://www.walterzorn.de

Copyright (c) 2002-2007 Walter Zorn. All rights reserved.
Created 1.12.2002 by Walter Zorn (Web: http://www.walterzorn.com )
Last modified: 31.5.2007

Easy-to-use cross-browser tooltips.
Just include the script at the beginning of the <body> section, and invoke
Tip('Tooltip text') from within the desired HTML onmouseover eventhandlers.
No container DIV, no onmouseouts required.
By default, width of tooltips is automatically adapted to content.
Is even capable of dynamically converting arbitrary HTML elements to tooltips
by calling TagToTip('ID_of_HTML_element_to_be_converted') instead of Tip(),
which means you can put important, search-engine-relevant stuff into tooltips.
Appearance of tooltips can be individually configured
via commands passed to Tip() or TagToTip().

Tab Width: 4
LICENSE: LGPL

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License (LGPL) as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

For more details on the GNU Lesser General Public License,
see http://www.gnu.org/copyleft/lesser.html
*/

var config = new Object();


//===================  GLOBAL TOOPTIP CONFIGURATION  =========================//
var  Debug			 = true			// false or true - recommended: false once you release your page to the public
var  TagsToTip		 = true			// false or true - if true, the script is capable of converting HTML elements to tooltips

// For each of the following config variables there exists a command, which is
// just the variablename in uppercase, to be passed to Tip() or TagToTip() to
// configure tooltips individually. Individual commands override global
// configuration. Order of commands is arbitrary.
// Example: onmouseover="Tip('Tooltip text', LEFT, true, BGCOLOR, '#FF9900', FADEOUT, 400)"

config. Above			= false 	// false or true - tooltip above mousepointer?
config. BgColor 		= '#E0E4FF' // Background color
config. BgImg			= ''		// Path to background image, none if empty string ''
config. BorderColor 	= '#002299'
config. BorderStyle 	= 'solid'	// Any permitted CSS value, but I recommend 'solid', 'dotted' or 'dashed'
config. BorderWidth 	= 1
config. CenterMouse 	= false 	// false or true - center the tip horizontally below (or above) the mousepointer
config. ClickClose		= false 	// false or true - close tooltip if the user clicks somewhere
config. CloseBtn		= false 	// false or true - closebutton in titlebar
config. CloseBtnColors	= ['#990000', '#FFFFFF', '#DD3333', '#FFFFFF']	  // [Background, text, hovered background, hovered text] - use empty strings '' to inherit title colors
config. CloseBtnText	= 'Close'	// Close button text (may also be an image tag)
config. Delay			= 400		// Time span in ms until tooltip shows up
config. Duration		= 0 		// Time span in ms after which the tooltip disappears; 0 for infinite duration
config. FadeIn			= 0 		// Fade-in duration in ms, e.g. 400; 0 for no animation
config. FadeOut 		= 0
config. FadeInterval	= 30		// Duration of each fade step in ms (recommended: 30) - shorter is smoother but causes more CPU-load
config. Fix 			= null		// Fixated position - x- an y-oordinates in brackets, e.g. [200, 200], or null for no fixation
config. FollowMouse		= true		// false or true - tooltip follows the mouse
config. FontColor		= '#000044'
config. FontFace		= 'Verdana,Geneva,sans-serif'
config. FontSize		= '8pt' 	// E.g. '9pt' or '12px' - specifying the unit is mandatory
config. FontWeight		= 'normal'	// 'normal' or 'bold';
config. Left			= false 	// false or true - tooltip on the left of the mouse
config. OffsetX 		= 14		// Horizontal offset of left-top corner from mousepointer
config. OffsetY 		= 8 		// Vertical offset
config. Opacity 		= 100		// Integer between 0 and 100 - opacity of tooltip in percent
config. Padding 		= 3 		// Spacing between border and content
config. Shadow			= false 	// false or true
config. ShadowColor 	= '#C0C0C0'
config. ShadowWidth 	= 5
config. Sticky			= false 	// Do NOT hide tooltip on mouseout? false or true
config. TextAlign		= 'left'	// 'left', 'right' or 'justify'
config. Title			= ''		// Default title text applied to all tips (no default title: empty string '')
config. TitleAlign		= 'left'	// 'left' or 'right' - text alignment inside the title bar
config. TitleBgColor	= ''		// If empty string '', BorderColor will be used
config. TitleFontColor	= '#ffffff'	// Color of title text - if '', BgColor (of tooltip body) will be used
config. TitleFontFace	= ''		// If '' use FontFace (boldified)
config. TitleFontSize	= ''		// If '' use FontSize
config. Width			= 0 		// Tooltip width; 0 for automatic adaption to tooltip content
//=======  END OF TOOLTIP CONFIG, DO NOT CHANGE ANYTHING BELOW  ==============//




//======================  PUBLIC  ============================================//

function Tip()
{
	tt_Tip(arguments);
}

function TagToTip()
{
	var t_a = arguments, t_el;

	if(TagsToTip)
	{
		t_el = tt_GetElt(t_a[0]);
		if(t_el)
		{
			t_a[0] = t_el.innerHTML;
			tt_Tip(t_a);
		}
	}
}


//==================  PUBLIC EXTENSION API	==================================//

// Extension eventhandlers currently supported:
// OnConfig, OnShow, OnMouseMove, OnHideInit, OnHide, OnKill

var tt_aElt = new Array(10), // Container DIV, outer title and body DIVs, inner TITLE and body SPAN, closebutton SPAN in title, shadow DIVs, and IFRAME to cover windowed elements in IE
tt_aV = new Array(),	// Caches and enumerates config data for currently active tooltip
tt_x, tt_y, tt_w, tt_h; // Position, width and height of currently displayed tooltip

function tt_Extension()
{
	tt_ExtCmdEnum();
	tt_aExt[tt_aExt.length] = this;
	return this;
}

function tt_SetTipPos(t_x, t_y)
{
	var t_css = tt_aElt[0].style;

	tt_x = t_x;
	tt_y = t_y;
	t_css.left = t_x + "px";
	t_css.top = t_y + "px";
	if(tt_ie56)
	{
		var t_ifrm = tt_aElt[tt_aElt.length - 1];
		if(t_ifrm)
		{
			t_ifrm.style.left = t_css.left;
			t_ifrm.style.top = t_css.top;
		}
	}
}

function tt_HideTip()
{
	if(tt_db)
	{
		if(tt_aElt[0].style.visibility == "visible")
			tt_ExtCallFunc(0, "OnHide");
		tt_tShow.EndTimer();
		tt_tHide.EndTimer();
		tt_tDurt.EndTimer();
		tt_tFade.EndTimer();
		if(!tt_op && !tt_ie)
		{
			tt_tWaitMov.EndTimer();
			tt_bWait = false;
		}
		if(tt_aV[CLICKCLOSE])
			tt_RemEvtFnc(document, "mouseup", tt_HideInit);
		tt_AddRemEvtFncEx(1, false);
		tt_AddRemEvtFncEx(0, false);
		tt_ExtCallFunc(0, "OnKill");
		tt_b = false;
		tt_Move.over = null;
		tt_ResetMainDiv();
		if(tt_aElt[tt_aElt.length - 1])
			tt_aElt[tt_aElt.length - 1].style.display = "none";
	}
}

function tt_GetDivW(t_el)
{
	if(t_el)
		return(t_el.offsetWidth || t_el.style.pixelWidth || 0);
	return 0;
}

function tt_GetDivH(t_el)
{
	if(t_el)
		return(t_el.offsetHeight || t_el.style.pixelHeight || 0);
	return 0;
}

function tt_GetScrollX()
{
	return((typeof(window.pageXOffset) != tt_u) ? window.pageXOffset
		   : tt_db ? (tt_db.scrollLeft || 0)
		   : 0);
}

function tt_GetScrollY()
{
	return((typeof(window.pageYOffset) != tt_u) ? window.pageYOffset
		   : tt_db ? (tt_db.scrollTop || 0)
		   : 0);
}

function tt_GetClientW()
{
	return(document.body && (typeof(document.body.clientWidth) != tt_u) ? document.body.clientWidth
		   : (typeof(window.innerWidth) != tt_u) ? window.innerWidth
		   : tt_db ? (tt_db.clientWidth || 0)
		   : 0);
}

function tt_GetClientH()
{
	// Exactly this order seems to yield correct values in all major browsers
	return(document.body && (typeof(document.body.clientHeight) != tt_u) ? document.body.clientHeight
		   : (typeof(window.innerHeight) != tt_u) ? window.innerHeight
		   : tt_db ? (tt_db.clientHeight || 0)
		   : 0);
}

function tt_AddEvtFnc(t_el, t_sEvt, t_PFnc)
{
	if(t_el)
	{
		if(t_el.addEventListener)
			t_el.addEventListener(t_sEvt, t_PFnc, false);
		else
			t_el.attachEvent("on" + t_sEvt, t_PFnc);
	}
}

function tt_RemEvtFnc(t_el, t_sEvt, t_PFnc)
{
	if(t_el)
	{
		if(t_el.removeEventListener)
			t_el.removeEventListener(t_sEvt, t_PFnc, false);
		else
			t_el.detachEvent("on" + t_sEvt, t_PFnc);
	}
}


//======================  PRIVATE  ===========================================//
var tt_aExt = new Array(),	 // Array of extension objects

tt_db, tt_op, tt_op78, tt_ie, tt_ie56, tt_bBoxOld,	// Browser flags
tt_body,
tt_flagOpa, 			// Opacity support: 1=IE, 2=Khtml, 3=KHTML, 4=Moz, 5=W3C
tt_scrlX, tt_scrlY,
tt_maxPosX, tt_maxPosY,
tt_b,					// Tooltip active
tt_opa, 				// Currently applied opacity
tt_bJmpVert,			// tip above mouse (or ABOVE tip below mouse)

tt_elDeHref,			  // The tag from which Opera has removed the href attribute
// Timer
tt_tShow = new Number(0), tt_tHide = new Number(0), tt_tDurt = new Number(0),
tt_tFade = new Number(0), tt_tWaitMov = new Number(0),
tt_bMovFnc, tt_bOutFnc,
tt_bWait = false,
tt_u = "undefined";


function tt_Init()
{
	tt_MkCmdEnum();
	// Send old browsers instantly to hell
	if(!tt_Browser() || !tt_MkMainDiv())
		return;
	tt_IsW3cBox();
	tt_OpaSupport();
	// In Debug mode we search for TagToTip() calls in order to notify
	// the user if they've forgotten to set the TagsToTip config flag
	if(TagsToTip || Debug)
		tt_SetOnloadFnc();
	tt_AddEvtFnc(window, "scroll", tt_HideOnScrl);
	// Ensure the tip be hidden when the page unloads
	tt_AddEvtFnc(window, "unload", tt_HideTip);
	tt_HideTip();
}

// Creates command names by translating config variable names to upper case
function tt_MkCmdEnum()
{
	var t_n = 0;
	for(var i in config)
		eval("window." + i.toString().toUpperCase() + " = " + t_n++);
	tt_aV.length = t_n;
}

function tt_Browser()
{
	var t_n, t_nv, t_n6, t_w3c;

	t_n = navigator.userAgent.toLowerCase(),
	t_nv = navigator.appVersion;
	tt_op = (document.defaultView && typeof(eval("w" + "indow" + "." + "o" + "p" + "er" + "a")) != tt_u);
	tt_op78 = (tt_op && !window.getSelection);
	tt_ie = t_n.indexOf("msie") != -1 && document.all && !tt_op;
	if(tt_ie)
	{
		var t_ieOld = (!document.compatMode || document.compatMode == "BackCompat");
		tt_db = !t_ieOld ? document.documentElement : (document.body || null);
		if(tt_db)
			tt_ie56 = parseFloat(t_nv.substring(t_nv.indexOf("MSIE") + 5)) >= 5.5
					  && typeof document.body.style.maxHeight == tt_u;
	}
	else
	{
		tt_db = document.documentElement || document.body ||
				(document.getElementsByTagName ? document.getElementsByTagName("body")[0]
				: null);
		if(!tt_op)
		{
			t_n6 = document.defaultView && typeof document.defaultView.getComputedStyle != tt_u;
			t_w3c = !t_n6 && document.getElementById;
		}
	}
	tt_body = (document.getElementsByTagName ? document.getElementsByTagName("body")[0]
			   : (document.body || null));
	if(tt_ie || t_n6 || tt_op || t_w3c)
	{
		if(tt_body && tt_db)
		{
			if(document.attachEvent || document.addEventListener)
				return true;
		}
		else
			tt_Err("wz_tooltip.js must be included INSIDE the body section,"
				   + " immediately after the opening <body> tag.");
	}
	tt_db = null;
	return false;
}

function tt_MkMainDiv()
{
	// Create the tooltip DIV
	if(tt_body.insertAdjacentHTML)
		tt_body.insertAdjacentHTML("afterBegin", tt_MkMainDivHtm());
	else if(typeof tt_body.innerHTML != tt_u && document.createElement && tt_body.appendChild)
		tt_body.appendChild(tt_MkMainDivDom());
	// FireFox Alzheimer bug
	if(window.tt_GetMainDivRefs && tt_GetMainDivRefs())
		return true;
	tt_db = null;
	return false;
}

function tt_MkMainDivHtm()
{
	return('<div id="WzTtDiV"></div>' +
		   (tt_ie56 ? ('<iframe id="WzTtIfRm" src="javascript:false" scrolling="no" frameborder="0" style="filter:Alpha(opacity=0);position:absolute;top:0px;left:0px;display:none;"></iframe>')
		   : ''));
}

function tt_MkMainDivDom()
{
	var t_el;

	t_el = document.createElement("div");
	if(t_el)
		t_el.id = "WzTtDiV";
	return t_el;
}

function tt_GetMainDivRefs()
{
	tt_aElt[0] = tt_GetElt("WzTtDiV");
	if(tt_ie56 && tt_aElt[0])
	{
		tt_aElt[tt_aElt.length - 1] = tt_GetElt("WzTtIfRm");
		if(!tt_aElt[tt_aElt.length - 1])
			tt_aElt[0] = null;
	}
	if(tt_aElt[0])
	{
		var t_css = tt_aElt[0].style;

		t_css.position = "absolute";
		t_css.overflow = "hidden";
		return true;
	}
	return false;
}

function tt_ResetMainDiv()
{
	var t_css = tt_aElt[0].style, t_w = screen.width;

	t_css.visibility = "hidden";
	t_css.left = -t_w + "px";
	tt_aElt[0].innerHTML = "";
	t_css.width = (t_w - 1) + "px";
}

function tt_IsW3cBox()
{
	var t_css = tt_aElt[0].style;

	t_css.padding = "10px";
	t_css.width = "40px";
	tt_bBoxOld = (tt_GetDivW(tt_aElt[0]) == 40);
	t_css.padding = "0px";
	tt_ResetMainDiv();
}

function tt_OpaSupport()
{
	var t_css = tt_body.style;

	tt_flagOpa = (typeof(t_css.filter) != tt_u) ? 1
			   : (typeof(t_css.KhtmlOpacity) != tt_u) ? 2
			   : (typeof(t_css.KHTMLOpacity) != tt_u) ? 3
			   : (typeof(t_css.MozOpacity) != tt_u) ? 4
			   : (typeof(t_css.opacity) != tt_u) ? 5
			   : 0;
}

// Ported from http://dean.edwards.name/weblog/2006/06/again/  (Dean Edwards)
function tt_SetOnloadFnc()
{
	tt_AddEvtFnc(document, "DOMContentLoaded", tt_HideSrcTags);
	tt_AddEvtFnc(window, "load", tt_HideSrcTags);
	// Conditional comment below is IE code - DON'T REMOVE!!!
	/*@cc_on
	if(document.attachEvent)
		document.attachEvent("onreadystatechange",
			function(){ if(document.readyState == "complete") tt_HideSrcTags(); });
	document.write('<scr' + 'ipt id="TT_ie_onload" defer src="'
				   + ((location.protocol == "https:") ? '//0' : 'javascript:void(0)')
				   + '"><\/scr' + 'ipt>');
	var script = document.getElementById("TT_ie_onload");
	script.onreadystatechange = function() {
		if(this.readyState == "complete")
			tt_HideSrcTags();
	};
	@*/
	if(/WebKit|KHTML/i.test(navigator.userAgent))
	{
		var t_t = setInterval(function() {
					if(/loaded|complete/.test(document.readyState))
					{
						clearInterval(t_t);
						tt_HideSrcTags();
					}
				}, 10);
	}
}

function tt_HideSrcTags()
{
	if(!window.tt_HideSrcTags || window.tt_HideSrcTags.done)
		return;
	window.tt_HideSrcTags.done = true;
	if(!tt_HideSrcTagsRecurs(tt_body))
		tt_Err("To enable the capability to convert HTML elements to tooltips,"
			   + " you must set TagsToTip in the global tooltip configuration"
			   + " to true.");
}

function tt_HideSrcTagsRecurs(t_dad)
{
	var t_a, t_ovr, t_asT2t;

	// Walk the DOM tree for tags that have an onmouseover attribute
	// containing a TagToTip('...') call.
	// (.childNodes first since .children is bugous in Safari)
	t_a = t_dad.childNodes || t_dad.children || null;
	for(var i = t_a ? t_a.length : 0; i;)
	{--i;
		if(!tt_HideSrcTagsRecurs(t_a[i]))
			return false;
		t_ovr = t_a[i].getAttribute ? t_a[i].getAttribute("onmouseover")
				: (typeof t_a[i].onmouseover == "function") ? t_a[i].onmouseover
				: null;
		if(t_ovr)
		{
			t_asT2t = t_ovr.toString().match(/TagToTip\s*\(\s*'[^'.]+'\s*[\),]/);
			if(t_asT2t && t_asT2t.length)
			{
				if(!tt_HideSrcTag(t_asT2t[0]))
					return false;
			}
		}
	}
	return true;
}

function tt_HideSrcTag(t_sT2t)
{
	var t_id, t_el;

	// The ID passed to the found TagToTip() call identifies an HTML element
	// to be converted to a tooltip, so hide that element
	t_id = t_sT2t.replace(/.+'([^'.]+)'.+/, "$1");
	t_el = tt_GetElt(t_id);
	if(t_el)
	{
		if(Debug && !TagsToTip)
			return false;
		else
			t_el.style.display = "none";
	}
	else
		tt_Err("Invalid ID\n'" + t_id + "'\npassed to TagToTip()."
			   + " There exists no HTML element with that ID.");
	return true;
}

function tt_Tip(t_arg)
{
	if(!tt_db)
		return;
	if(tt_b)
		tt_HideTip();
	if(!tt_ReadCmds(t_arg))
		return;
	tt_b = true;
	tt_AdaptConfig1();
	tt_MkTipSubDivs(t_arg[0]);
	tt_FormatTip();
	tt_AddRemEvtFncEx(0, true);
	tt_bJmpVert = false;
	tt_scrlX = tt_GetScrollX();
	tt_scrlY = tt_GetScrollY();
	tt_maxPosX = tt_GetClientW() + tt_scrlX - tt_w - 1;
	tt_maxPosY = tt_GetClientH() + tt_scrlY - tt_h - 1;
	tt_AdaptConfig2();
	// IE and Op won't fire a mousemove accompanying the mouseover, so we
	// must ourselves fake that first mousemove in order to ensure the tip
	// be immediately shown and positioned
	if(window.event)
		tt_Move(window.event);
}

function tt_ReadCmds(t_a)
{
	var i;

	// First load the global config values, to initialize also values
	// for which no command has been passed
	i = 0;
	for(var j in config)
		tt_aV[i++] = config[j];
	// Then replace each cached config value for which a command has been
	// passed; ensure the # of command args plus value args be even
	if(t_a.length & 1)
	{
		for(i = t_a.length - 1; i > 0; i -= 2)
			tt_aV[t_a[i - 1]] = t_a[i];
		return true;
	}
	tt_Err("Incorrect call of Tip() or TagToTip().\n"
		   + "Each command must be followed by a value.");
	return false;
}

function tt_AdaptConfig1()
{
	tt_ExtCallFunc(0, "OnConfig");
	// Inherit unspecified title formattings from body
	if(!tt_aV[TITLEBGCOLOR].length)
		tt_aV[TITLEBGCOLOR] = tt_aV[BORDERCOLOR];
	if(!tt_aV[TITLEFONTCOLOR].length)
		tt_aV[TITLEFONTCOLOR] = tt_aV[BGCOLOR];
	if(!tt_aV[TITLEFONTFACE].length)
		tt_aV[TITLEFONTFACE] = tt_aV[FONTFACE];
	if(!tt_aV[TITLEFONTSIZE].length)
		tt_aV[TITLEFONTSIZE] = tt_aV[FONTSIZE];
	if(tt_aV[CLOSEBTN])
	{
		// Use title colors for non-specified closebutton colors
		if(!tt_aV[CLOSEBTNCOLORS])
			tt_aV[CLOSEBTNCOLORS] = new Array("", "", "", "");
		for(var i = 4; i;)
		{--i;
			if(!tt_aV[CLOSEBTNCOLORS][i].length)
				tt_aV[CLOSEBTNCOLORS][i] = (i & 1) ? tt_aV[TITLEFONTCOLOR] : tt_aV[TITLEBGCOLOR];
		}
		// Enforce titlebar be shown
		if(!tt_aV[TITLE].length)
			tt_aV[TITLE] = " ";
	}
	// Circumvents broken display of images and fade-in flicker in old Geckos.
	// TODO: Execute only in Geckos old enough to suffer from these bugs
	if(tt_aV[OPACITY] == 100 && typeof tt_aElt[0].style.MozOpacity != tt_u)
		tt_aV[OPACITY] = 99;
	// Smartly shorten the delay for fade-in tooltips
	if(tt_aV[FADEIN] && tt_flagOpa && tt_aV[DELAY] > 100)
		tt_aV[DELAY] = Math.max(tt_aV[DELAY] - tt_aV[FADEIN], 100)
}

function tt_AdaptConfig2()
{
	if(tt_aV[CENTERMOUSE])
		tt_aV[OFFSETX] = -((tt_w - (tt_aV[SHADOW] ? tt_aV[SHADOWWIDTH] : 0)) >> 1);
}

function tt_MkTipSubDivs(t_htm)
{
	var t_sTbTr = ' cellpadding="0" cellspacing="0" border="0" style="position:relative;margin:0px;border-width:0px;"><tr>',
	t_sTdCss = 'position:relative;margin:0px;padding:0px;border-width:0px;';

	tt_aElt[0].innerHTML =
		(''
		 + (tt_aV[TITLE].length ?
			('<div id="WzTiTl" style="position:relative;z-index:1;">'
			+ '<table id="WzTiTlTb"' + t_sTbTr
			+ '<td id="WzTiTlI" style="' + t_sTdCss + '">'
			+ tt_aV[TITLE]
			+ '</td>'
			+ (tt_aV[CLOSEBTN] ?
			  ('<td align="right" style="' + t_sTdCss
			  + ';text-align:right;">'
			  + '<span id="WzClOsE" style="padding-left:2px;padding-right:2px;'
			  + 'cursor:' + (tt_ie ? 'hand' : 'pointer')
			  + ';" onmouseover="tt_OnCloseBtnOver(1)" onmouseout="tt_OnCloseBtnOver(0)" onclick="tt_HideInit()">'
			  + tt_aV[CLOSEBTNTEXT]
			  + '</span></td>')
			  : '')
			+ '</tr></table></div>')
		   : '')
		 + '<div id="WzBoDy" style="position:relative;z-index:0;">'
		 + '<table' + t_sTbTr + '<td id="WzBoDyI" style="' + t_sTdCss + '">'
		 + t_htm
		 + '</td></tr></table>'
		 + '</div>'
		 + (tt_aV[SHADOW]
		   ? ('<div id="WzTtShDwR" style="position:absolute;overflow:hidden;"></div>'
			  + '<div id="WzTtShDwB" style="position:relative;overflow:hidden;"></div>')
		   : '')
		);
	tt_GetSubDivRefs();
}

function tt_GetSubDivRefs()
{
	var t_aId = new Array("WzTiTl", "WzTiTlTb", "WzTiTlI", "WzClOsE", "WzBoDy", "WzBoDyI", "WzTtShDwB", "WzTtShDwR");

	for(var i = t_aId.length; i; --i)
		tt_aElt[i] = tt_GetElt(t_aId[i - 1]);
	return true;
}

function tt_FormatTip()
{
	var t_css, t_top, t_w;

	//--------- Title DIV ----------
	if(tt_aV[TITLE].length)
	{
		t_css = tt_aElt[1].style;
		t_css.background = tt_aV[TITLEBGCOLOR];
		t_css.paddingTop = (tt_aV[CLOSEBTN] ? 2 : 0) + "px";
		t_css.paddingBottom = "1px";
		t_css.paddingLeft = t_css.paddingRight = tt_aV[PADDING] + "px";
		t_css = tt_aElt[3].style;
		t_css.color = tt_aV[TITLEFONTCOLOR];
		t_css.fontFamily = tt_aV[TITLEFONTFACE];
		t_css.fontSize = tt_aV[TITLEFONTSIZE];
		t_css.fontWeight = "bold";
		t_css.textAlign = tt_aV[TITLEALIGN];
		// Close button DIV
		if(tt_aElt[4])
		{
			t_css.paddingRight = (tt_aV[PADDING] << 1) + "px";
			t_css = tt_aElt[4].style;
			t_css.background = tt_aV[CLOSEBTNCOLORS][0];
			t_css.color = tt_aV[CLOSEBTNCOLORS][1];
			t_css.fontFamily = tt_aV[TITLEFONTFACE];
			t_css.fontSize = tt_aV[TITLEFONTSIZE];
			t_css.fontWeight = "bold";
		}
		if(tt_aV[WIDTH] > 0)
			tt_w = tt_aV[WIDTH] + ((tt_aV[PADDING] + tt_aV[BORDERWIDTH]) << 1);
		else
		{
			tt_w = tt_GetDivW(tt_aElt[3]) + tt_GetDivW(tt_aElt[4]);
			// Some spacing between title DIV and closebutton
			if(tt_aElt[4])
				tt_w += tt_aV[PADDING];
		}
		// Ensure the top border of the body DIV be covered by the title DIV
		t_top = -tt_aV[BORDERWIDTH];
	}
	else
	{
		tt_w = 0;
		t_top = 0;
	}

	//-------- Body DIV ------------
	t_css = tt_aElt[5].style;
	t_css.top = t_top + "px";
	if(tt_aV[BORDERWIDTH])
	{
		t_css.borderColor = tt_aV[BORDERCOLOR];
		t_css.borderStyle = tt_aV[BORDERSTYLE];
		t_css.borderWidth = tt_aV[BORDERWIDTH] + "px";
	}
	if(tt_aV[BGCOLOR].length)
		t_css.background = tt_aV[BGCOLOR];
	if(tt_aV[BGIMG].length)
		t_css.backgroundImage = "url(" + tt_aV[BGIMG] + ")";
	t_css.padding = tt_aV[PADDING] + "px";
	t_css.textAlign = tt_aV[TEXTALIGN];
	// TD inside body DIV
	t_css = tt_aElt[6].style;
	t_css.color = tt_aV[FONTCOLOR];
	t_css.fontFamily = tt_aV[FONTFACE];
	t_css.fontSize = tt_aV[FONTSIZE];
	t_css.fontWeight = tt_aV[FONTWEIGHT];
	t_css.background = "";
	t_css.textAlign = tt_aV[TEXTALIGN];
	if(tt_aV[WIDTH] > 0)
		t_w = tt_aV[WIDTH] + ((tt_aV[PADDING] + tt_aV[BORDERWIDTH]) << 1);
	else
		// We measure the width of the body's inner TD, because some browsers
		// expand the width of the container and outer body DIV to 100%
		t_w = tt_GetDivW(tt_aElt[6]) + ((tt_aV[PADDING] + tt_aV[BORDERWIDTH]) << 1);
	if(t_w > tt_w)
		tt_w = t_w;

	//--------- Shadow DIVs ------------
	if(tt_aV[SHADOW])
	{
		var t_off;

		tt_w += tt_aV[SHADOWWIDTH];
		t_off = tt_CalcShadowOffset();
		// Bottom shadow
		t_css = tt_aElt[7].style;
		t_css.top = t_top + "px";
		t_css.left = t_off + "px";
		t_css.width = (tt_w - t_off - tt_aV[SHADOWWIDTH]) + "px";
		t_css.height = tt_aV[SHADOWWIDTH] + "px";
		t_css.background = tt_aV[SHADOWCOLOR];
		// Right shadow
		t_css = tt_aElt[8].style;
		t_css.top = t_off + "px";
		t_css.left = (tt_w - tt_aV[SHADOWWIDTH]) + "px";
		t_css.width = tt_aV[SHADOWWIDTH] + "px";
		t_css.background = tt_aV[SHADOWCOLOR];
	}

	//-------- Container DIV -------
	tt_SetTipOpa(tt_aV[FADEIN] ? 0 : tt_aV[OPACITY]);
	tt_FixSize(t_top);
}

// Nail the size so it can't dynamically change while the tooltip is moving.
function tt_FixSize(t_offTop)
{
	var t_wIn, t_wOut, t_i;

	tt_aElt[0].style.width = tt_w + "px";
	tt_aElt[0].style.pixelWidth = tt_w;
	tt_h = tt_GetDivH(tt_aElt[0]) + t_offTop;
	t_wOut = tt_w - ((tt_aV[SHADOW]) ? tt_aV[SHADOWWIDTH] : 0);
	// Body
	if(tt_bBoxOld)
		t_wIn = t_wOut;
	else
		t_wIn = t_wOut - ((tt_aV[PADDING] + tt_aV[BORDERWIDTH]) << 1);
	tt_aElt[5].style.width = t_wIn + "px";
	// Title
	if(tt_aElt[1])
	{
		if(tt_bBoxOld)
			t_wIn = t_wOut - (tt_aV[PADDING] << 1);
		else
		{
			t_wOut -= (tt_aV[PADDING] << 1);
			t_wIn = t_wOut;
		}
		tt_aElt[1].style.width = t_wOut + "px";
		tt_aElt[2].style.width = t_wIn + "px";
	}
	// Right shadow
	if(tt_aElt[8])
		tt_aElt[8].style.height = (tt_h - tt_CalcShadowOffset()) + "px";
	t_i = tt_aElt.length - 1;
	if(tt_aElt[t_i])
	{
		tt_aElt[t_i].style.width = tt_w + "px";
		tt_aElt[t_i].style.height = tt_h + "px";
	}
}

function tt_CalcShadowOffset()
{
	return(Math.floor((tt_aV[SHADOWWIDTH] * 4) / 3));
}

function tt_StartMov()
{
	tt_DeAlt(tt_Move.over);
	tt_OpDeHref(tt_Move.over);
	tt_tShow.Timer("tt_ShowTip()", tt_aV[DELAY], true);
	tt_AddRemEvtFncEx(1, true);
	if(tt_aV[CLICKCLOSE])
		tt_AddEvtFnc(document, "mouseup", tt_HideInit);
}

function tt_DeAlt(t_el)
{
	var t_aKid;

	if(t_el.alt)
		t_el.alt = "";
	if(t_el.title)
		t_el.title = "";
	t_aKid = t_el.childNodes || t_el.children || null;
	if(t_aKid)
	{
		for(var i = t_aKid.length; i;)
			tt_DeAlt(t_aKid[--i]);
	}
}

function tt_OpDeHref(t_el)
{
	// I hope those annoying native tooltips over links never revive in Opera
	if(!tt_op78)
		return;
	if(tt_elDeHref)
		tt_OpReHref();
	while(t_el)
	{
		if(t_el.hasAttribute("href"))
		{
			t_el.t_href = t_el.getAttribute("href");
			t_el.t_stats = window.status;
			t_el.removeAttribute("href");
			t_el.style.cursor = "hand";
			tt_AddEvtFnc(t_el, "mousedown", tt_OpReHref);
			window.status = t_el.t_href;
			tt_elDeHref = t_el;
			break;
		}
		t_el = t_el.parentElement;
	}
}

function tt_ShowTip()
{
	var t_css = tt_aElt[0].style;

	// Override the z-index of the topmost wz_dragdrop.js D&D item
	t_css.zIndex = Math.max((window.dd && dd.z) ? (dd.z + 2) : 0, 1010);
	if(tt_aV[STICKY] || !tt_aV[FOLLOWMOUSE])
		tt_AddRemEvtFncEx(0, false);
	if(tt_aV[DURATION] > 0)
		tt_tDurt.Timer("tt_HideInit()", tt_aV[DURATION], true);
	tt_ExtCallFunc(0, "OnShow")
	t_css.visibility = "visible";
	if(tt_aV[FADEIN])
		tt_Fade(0, 0, tt_aV[OPACITY], Math.round(tt_aV[FADEIN] / tt_aV[FADEINTERVAL]));
	tt_ShowIfrm();
}

function tt_ShowIfrm()
{
	if(tt_ie56)
	{
		var t_ifrm = tt_aElt[tt_aElt.length - 1];
		if(t_ifrm)
		{
			var t_css = t_ifrm.style;
			t_css.zIndex = tt_aElt[0].style.zIndex - 1;
			t_css.width = tt_w + "px";
			t_css.height = tt_h + "px";
			t_css.display = "block";
		}
	}
}

function tt_Move(t_e)
{
	t_e = t_e || window.event || null;
	// Protect some browsers against jam of mousemove events
	if(!tt_op && !tt_ie)
	{
		if(tt_bWait)
			return;
		tt_bWait = true;
		tt_tWaitMov.Timer("tt_bWait = false;", 1, true);
	}
	if(tt_aV[FIX])
	{
		tt_AddRemEvtFncEx(0, false);
		tt_SetTipPos(tt_aV[FIX][0], tt_aV[FIX][1]);
	}
	else if(!tt_ExtCallFunc(t_e, "OnMouseMove"))
		tt_SetTipPos(tt_PosX(t_e), tt_PosY(t_e));
	// The first onmousemove when the HTML element has just been hovered
	if(!tt_Move.over)
	{
		tt_Move.over = t_e.target || t_e.srcElement;
		if(tt_Move.over)
			tt_StartMov();
	}
}

function tt_PosX(t_e)
{
	var t_x;

	t_x = (typeof(t_e.pageX) != tt_u) ? t_e.pageX : (t_e.clientX + tt_scrlX);
	if(tt_aV[LEFT])
		t_x -= tt_w + tt_aV[OFFSETX] - (tt_aV[SHADOW] ? tt_aV[SHADOWWIDTH] : 0);
	else
		t_x += tt_aV[OFFSETX];
	// Prevent tip from extending past right/left clientarea boundary
	if(t_x > tt_maxPosX)
		t_x = tt_maxPosX;
	return((t_x < tt_scrlX) ? tt_scrlX : t_x);
}

function tt_PosY(t_e)
{
	var t_yMus, t_y;

	t_yMus = (typeof(t_e.pageY) != tt_u) ? t_e.pageY : (t_e.clientY + tt_scrlY);
	// The following logic applys some hysteresis when the tip has snapped
	// to the other side of the mouse. In doubt (insufficient space above
	// and beneath the mouse) the tip is positioned beneath.
	if(tt_aV[ABOVE] && (!tt_bJmpVert || tt_CalcPosYAbove(t_yMus) >= tt_scrlY + 16))
		t_y = tt_DoPosYAbove(t_yMus);
	else if(!tt_aV[ABOVE] && tt_bJmpVert && tt_CalcPosYBeneath(t_yMus) > tt_maxPosY - 16)
		t_y = tt_DoPosYAbove(t_yMus);
	else
		t_y = tt_DoPosYBeneath(t_yMus);
	// Must the tip snap to the other side of the mouse in order to
	// not extend past the window boundary?
	if(t_y > tt_maxPosY)
		t_y = tt_DoPosYAbove(t_yMus);
	if(t_y < tt_scrlY)
		t_y = tt_DoPosYBeneath(t_yMus);
	return t_y;
}

function tt_DoPosYBeneath(t_yMus)
{
	tt_bJmpVert = tt_aV[ABOVE];
	return tt_CalcPosYBeneath(t_yMus);
}

function tt_DoPosYAbove(t_yMus)
{
	tt_bJmpVert = !tt_aV[ABOVE];
	return tt_CalcPosYAbove(t_yMus);
}

function tt_CalcPosYBeneath(t_yMus)
{
	return(t_yMus + tt_aV[OFFSETY]);
}

function tt_CalcPosYAbove(t_yMus)
{
	var t_dy = tt_aV[OFFSETY] - (tt_aV[SHADOW] ? tt_aV[SHADOWWIDTH] : 0);
	if(tt_aV[OFFSETY] > 0 && t_dy <= 0)
		t_dy = 1;
	return(t_yMus - tt_h - t_dy);
}

function tt_OnOut()
{
	tt_AddRemEvtFncEx(1, false);
	if(!(tt_aV[STICKY] && tt_aElt[0].style.visibility == "visible"))
		tt_HideInit();
}

// Most browsers don't fire a mouseout if the mouse leaves an element due to
// the window being scrolled.
function tt_HideOnScrl()
{
	if(tt_b && !(tt_aV[STICKY] && tt_aElt[0].style.visibility == "visible"))
		tt_HideInit();
}

function tt_HideInit()
{
	tt_ExtCallFunc(0, "OnHideInit");
	tt_AddRemEvtFncEx(0, false);
	if(tt_flagOpa && tt_aV[FADEOUT])
	{
		tt_tFade.EndTimer();
		if(tt_opa)
		{
			var t_n = Math.round(tt_aV[FADEOUT] / (tt_aV[FADEINTERVAL] * (tt_aV[OPACITY] / tt_opa)));
			tt_Fade(tt_opa, tt_opa, 0, t_n);
			return;
		}
	}
	tt_tHide.Timer("tt_HideTip();", 1, false);
}

function tt_OpReHref()
{
	if(tt_elDeHref)
	{
		tt_elDeHref.setAttribute("href", tt_elDeHref.t_href);
		tt_RemEvtFnc(tt_elDeHref, "mousedown", tt_OpReHref);
		window.status = tt_elDeHref.t_stats;
		tt_elDeHref = null;
	}
}

function tt_Fade(t_a, t_now, t_z, t_n)
{
	if(t_n)
	{
		t_now += Math.round((t_z - t_now) / t_n);
		if((t_z > t_a) ? (t_now >= t_z) : (t_now <= t_z))
			t_now = t_z;
		else
			tt_tFade.Timer("tt_Fade("
						   + t_a + "," + t_now + "," + t_z + "," + (t_n - 1)
						   + ")",
						   tt_aV[FADEINTERVAL],
						   true);
	}
	if(!t_now)
		tt_HideTip();
	else
		tt_SetTipOpa(t_now);
}

// To circumvent the opacity nesting flaws of IE, we must set the opacity
// for each sub-DIV by its own, rather than for the container DIV.
function tt_SetTipOpa(t_opa)
{
	tt_SetOpa(tt_aElt[5].style, t_opa);
	if(tt_aElt[1])
		tt_SetOpa(tt_aElt[1].style, t_opa);
	if(tt_aV[SHADOW])
	{
		t_opa = Math.round(t_opa * 0.8);
		tt_SetOpa(tt_aElt[7].style, t_opa);
		tt_SetOpa(tt_aElt[8].style, t_opa);
	}
}

function tt_OnCloseBtnOver(t_iOver)
{
	var t_css = tt_aElt[4].style;

	t_iOver <<= 1;
	t_css.background = tt_aV[CLOSEBTNCOLORS][t_iOver];
	t_css.color = tt_aV[CLOSEBTNCOLORS][ t_iOver + 1];
}

function tt_Int(t_x)
{
	var t_y;

	return(isNaN(t_y = parseInt(t_x)) ? 0 : t_y);
}

function tt_GetElt(t_id)
{
	return(document.getElementById ? document.getElementById(t_id)
		   : document.all ? document.all[t_id]
		   : null);
}

// Adds or removes the document.mousemove or HoveredElem.mouseout handler
// conveniently. Keeps track of those handlers to prevent them from being
// set or removed redundantly.
function tt_AddRemEvtFncEx(t_typ, t_bAdd)
{
	var t_PSet = t_bAdd ? tt_AddEvtFnc : tt_RemEvtFnc;

	if(t_typ)
	{
		if(t_bAdd != tt_bOutFnc)
		{
			t_PSet(tt_Move.over, "mouseout", tt_OnOut);
			tt_bOutFnc = t_bAdd;
			if(!t_bAdd)
				tt_OpReHref();
		}
	}
	else
	{
		if(t_bAdd != tt_bMovFnc)
		{
			t_PSet(document, "mousemove", tt_Move);
			tt_bMovFnc = t_bAdd;
		}
	}
}

Number.prototype.Timer = function(t_s, t_t, t_bUrge)
{
	if(!this.value || t_bUrge)
		this.value = window.setTimeout(t_s, t_t);
}

Number.prototype.EndTimer = function()
{
	if(this.value)
	{
		window.clearTimeout(this.value);
		this.value = 0;
	}
}

function tt_SetOpa(t_css, t_opa)
{
	tt_opa = t_opa;
	if(tt_flagOpa == 1)
	{
		// Hack for bugs of IE:
		// A DIV cannot be made visible in a single step if an opacity < 100
		// has been applied while the DIV was hidden.
		// Moreover, in IE6, applying an opacity < 100 has no effect if the
		// concerned element has no layout (such as position, size, zoom, ...).
		if(t_opa < 100)
		{
			var t_bVis = t_css.visibility != "hidden";
			t_css.zoom = "100%";
			if(!t_bVis)
				t_css.visibility = "visible";
			t_css.filter = "alpha(opacity=" + t_opa + ")";
			if(!t_bVis)
				t_css.visibility = "hidden";
		}
		else
			t_css.filter = "";
	}
	else
	{
		t_opa /= 100.0;
		switch(tt_flagOpa)
		{
		case 2:
			t_css.KhtmlOpacity = t_opa; break;
		case 3:
			t_css.KHTMLOpacity = t_opa; break;
		case 4:
			t_css.MozOpacity = t_opa; break;
		case 5:
			t_css.opacity = t_opa; break;
		}
	}
}

function tt_Err(t_sErr)
{
	if(Debug)
		alert("Tooltip Script Error Message:\n\n" + t_sErr);
}

//===========  DEALING WITH EXTENSIONS	==============//

function tt_ExtCmdEnum()
{
	var t_s;

	// Add new command(s) to the commands enum
	for(var i in config)
	{
		t_s = "window." + i.toString().toUpperCase();
		if(eval("typeof(" + t_s + ") == tt_u"))
		{
			eval(t_s + " = " + tt_aV.length);
			tt_aV[tt_aV.length] = null;
		}
	}
}

function tt_ExtCallFunc(t_arg, t_sFnc)
{
	for(var i = tt_aExt.length; i;)
	{--i;
		var t_fnc = tt_aExt[i][t_sFnc];
		// Call the method the extension has defined for this event
		if(t_fnc && t_fnc(t_arg))
			// If the callback method returns true, we shouldn't handle this
			// event ourselves, so we bail out
			return true;
	}
	return false;
}

tt_Init();
