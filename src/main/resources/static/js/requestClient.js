$(document).ready(function()
{
	initHandlers();
	sendRequestLogic();
	
});


function sendRequestLogic()
{
    //updateRequestAttributesList();
    //updateSendAttributesList();
    //updateConsentAttributesList();

    $('#send-request-button').click(function()
    {
        //updateConsentAttributesList();
    	retrieveRequestedAttributes();
        $('#ap-form').submit();
    });

    $('#send-request-cancel').click(function()
    {
        $('#send-request').hide();
    });
}

function initHandlers() 
{
	$(".data-query-list a").click(function()
		{
			let id = $(this).attr('id')
			$("#request-source").val(id);
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
		
	$('.attribute-request-checkbox').click(function(){
			retrieveRequestedAttributes();
	})
}


function retrieveRequestedAttributes()
{
	
	var selectedCheckbox = $('.attribute-request-checkbox:checked');
	var arrayResult = new Array();
	for (var i = 0; i< selectedCheckbox.length; i++){
		arrayResult.push($(selectedCheckbox[i]).val());
	}
	$('#attr-request-list').val(arrayResult);
	
}