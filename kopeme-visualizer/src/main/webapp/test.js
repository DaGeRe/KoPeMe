
$(function () {
	$('input[type="checkbox"]').change(function (e) {
		var checked = $(this).prop("checked"),
		container = $(this).parent(),
		siblings = container.siblings();

		container.find('input[type="checkbox"]').prop({
			indeterminate : false,
			checked : checked
		});

		// intermediate state 
		
		/* 
		function checkSiblings(el) {
			var parent = el.parent(),
			all = true;

			el.siblings().each(function () {
				return all = (Q(this).children('input[type="checkbox"]').prop("checked") === checked);
			});

			if (all && checked) {
				parent.children('input[type="checkbox"]').prop({

		checkSiblings(container);
		*/
	});
});

