$(function() {
    function split( val ) {
      return val.split( /,\s*/ );
    }
    function extractLast( term ) {
      return split( term ).pop();
    }
 //@Ye: Selector for adding auto-complete ability, currently add to all input text with class "xHuge_txtInput"
    $( ".xHuge_txtInput" )
      // don't navigate away from the field on tab when selecting an item
      .bind( "keydown", function( event ) {
        if ( event.keyCode === $.ui.keyCode.TAB &&
            $( this ).data( "autocomplete" ).menu.active ) {
          event.preventDefault();
        }
      })
      .autocomplete({
        source: function( request, response ) {
          $.getJSON( "http://localhost:8888/imeji/autocompleter", {
            term: extractLast( request.term )
          }, response );
        },
        search: function() {       
          // custom minLength, currently start query after entering 2 characters,
          var term = extractLast( this.value );
          if ( term.length < 2 ) {
            return false;
          }
        },
        focus: function() {
          // prevent value inserted on focus
          return false;
        },
        select: function( event, ui ) {
           return;        
        }
      });
  });