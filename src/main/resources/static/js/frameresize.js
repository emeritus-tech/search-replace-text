function postResizeMessage() {
	window.top.postMessage(
		{
			'subject': 'lti.resizeFrame',
			'hide': false,
			'subsrc': 'course-dashboard'
		},
		'*');
}
$(document).ready(function() {
	postResizeMessage();
});