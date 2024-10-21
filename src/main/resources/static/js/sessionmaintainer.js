/** This JS maintains LTI session by calling ping endpoint using setInterval  */
var ajax_call = function() {
	try {
		$.ajax({
			type: 'get',
			url: appContext + 'api/v1/ping',
			data: { },
			success: function(data) {
				if(data.data !== null && data.data.userId !== null){
					// ping success
				} else {
					// if userId is null, canvas session time out
					// then reload
					isReload = true;
					reload();
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				// network error
			}
		});
	} catch (e) {
		console.log(e);
	}
};

$(document).ready(function() {
	// where X is your every X minutes
	var interval = 1000 * 60 * 10; 
	setInterval(ajax_call, interval);
});