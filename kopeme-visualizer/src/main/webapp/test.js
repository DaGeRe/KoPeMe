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
					indeterminate : false,
					checked : checked
				});
				checkSiblings(parent);
			} else if (all && !checked) {
				parent.children('input[type="checkbox"]').prop("checked", checked);
				parent.children('input[type="checkbox"]').prop("indeterminate", (parent.find('input[type="checkbox"]:checked').length > 0));
				checkSiblings(parent);
			} else {
				el.parents('.testcases').children('input[type="checkbox"]').prop({
					indeterminate : true,
					checked : false
				});
			}
		}
		checkSiblings(container);
		*/
	});
});

