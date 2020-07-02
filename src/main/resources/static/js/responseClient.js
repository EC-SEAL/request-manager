$(document).ready(function()
{
    sendRequestLogic();

});

function sendRequestLogic()
{
    
    $('#send-request-button').click(function()
    {
    	updateConsentAttributesList();
    	$('#ap-form').submit();
    });

}

function updateConsentAttributesList()
{
    var consentList = "";

    $('.update-consent-attr').each(function() {
        var index = $('.update-consent-attr').index($(this));
        var indexes = new Array();
        var consentId = null;

        $($('.attributes-consent-selection')[index]).find('li input[type=checkbox]').each(function()
        {
            if ($(this).is(':checked'))
            {
                indexes.push($(this).val());
                consentId = $(this)[0].name;
            }
        });

        if (consentId != null)
        {
            consentList = consentList + "#" + consentId + ':' + indexes.join();
        }
    });

    if (consentList != '') consentList = consentList.substr(1);
    $('#attr-consent-list').val(consentList);
    
//    window.alert("attr-consent-list");
//    alert(consentList);
}




