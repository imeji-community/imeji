/**
 * JQuery shiftcheckbox plugin
 *
 * shiftcheckbox provides a simpler and faster way to select/unselect multiple checkboxes within a given range with just two clicks.
 * Inspired from GMail checkbox functionality
 *
 * Just call jQuery('.<class-name>').shiftcheckbox() in jQuery(document).ready
 *
 * @name shiftcheckbox
 * @type jquery
 * @cat Plugin/Form
 * @return JQuery
 *
 * @URL http://www.sanisoft.com/blog/2009/07/02/jquery-shiftcheckbox-plugin
 *
 * Copyright (c) 2009 Aditya Mooley <adityamooley@sanisoft.com>
 * Dual licensed under the MIT (MIT-LICENSE.txt) and GPL (GPL-LICENSE.txt) licenses
 */
if (typeof jQuery != 'undefined') {
(function (jQuery) {
    jQuery.fn.shiftcheckbox = function()
    {
        var prevChecked = null;

        selectorStr = this;

        jQuery(selectorStr).bind("click", handleClick);
    };

    function handleClick(event)
    {
        var val = this.id;
        var checkStatus = this.checked;
        //get the checkbox number which the user has checked

        //check whether user has pressed shift
        if (event.shiftKey) {
            if (prevChecked != 'null') {
                //get the current checkbox number
                var ind = 0, found = 0, currentChecked;
                currentChecked = getSelected(val);

                ind = 0;
                if (currentChecked < prevChecked) {
                    jQuery(selectorStr).each(function(i) {
                        if (ind >= currentChecked && ind <= prevChecked) {
                            if(this.checked !=checkStatus)
                            {
                            	this.click();
                            }
//                        	this.checked = checkStatus;
                        }
                        ind++;
                    });
                } else {
                    jQuery(selectorStr).each(function(i) {
                        if (ind >= prevChecked && ind <= currentChecked) {
                        	 if(this.checked !=checkStatus)
                             {
                             	this.click();
                             }
//                        	this.checked = checkStatus;
                        }
                        ind++;
                    });
                }

                prevChecked = currentChecked;
            }
        } else {
            if (checkStatus) {
                prevChecked = getSelected(val);
            }
        }
    };

    function getSelected(id)
    {
        var ind = 0, found = 0, checkedIndex;

        jQuery(selectorStr).each(function(i) {
            if (id == this.id && found != 1) {
                checkedIndex = ind;
                found = 1;
            }
            ind++;
        });

        return checkedIndex;
    };
})(jQuery);
}