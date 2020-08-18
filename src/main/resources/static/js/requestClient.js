$(document).ready(function()
{
	$(".data-query-list a").click(function()
	{
		let id = $(this).attr('id')
		$("#attr-request-list").val(id);
		if(id == "pds-selector") {
			$("#list-pds").css('visibility', 'visible');
		}
		else {
			$("#list-pds").css('visibility', 'hidden');
		}
	});
	
	$("#list-pds a").click(function()
	{
		let id = $(this).attr('id')
		$("#pds-request-list").val(id);
	
	});

});