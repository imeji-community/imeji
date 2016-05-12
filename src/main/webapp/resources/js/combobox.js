$(function() {
	var availableTags = [ "Publication", "Project Homepage" ];
	$(".combobox_js").autocomplete({
		source : availableTags,
		minLength : 0,
	}).bind('focus', function() {
		$(this).autocomplete("search");
	});
});