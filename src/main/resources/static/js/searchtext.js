        // Function to highlight the search term on the page
        function highlightText(searchTerm) {
            if (!searchTerm) return;

            // Using regular expression to find and replace search term with a highlighted span
            document.body.innerHTML = document.body.innerHTML.replace(
                new RegExp(searchTerm, 'gi'), // 'gi' makes it case-insensitive
                (match) => `<span class="highlight">${match}</span>`
            );
        }

        // Function to get the search term from the URL
        function getSearchTerm() {
            const params = new URLSearchParams(window.location.search);
            return params.get('searchTerm');
        }

        // When the page loads, highlight the text
        window.onload = function() {

            const searchTerm = getSearchTerm();
            console.log('searchTerm' + searchTerm);
            if (searchTerm) {
                console.log('inside if');
                highlightText(searchTerm);
            }
        };
 
    function findAndReplace() {
		var replaceTextModal = bootstrap.Modal.getInstance(document.getElementById('replaceTextModal'));
        replaceTextModal.hide();
		// Show loader
        document.getElementById('loader').style.display = 'block';
        $(".invalid-feedback").hide();
        // Show the horizontal loader before making the request
        //document.getElementById('horizontal-loader').style.display = 'block';
        
        var courseIds = document.getElementById('course_id').value;
        var textToFind = document.getElementById('targetText').value;
        var replaceWith = document.getElementById('replaceWith').value;

        // Convert the comma-separated string into an array (trim to remove extra spaces)
        let courseIdList = courseIds.split(',').map(id => id.trim());

        $.ajax({
            type: "POST",
            url: appContext + 'api/v1/search-replace',
            contentType: "application/json",
            data: JSON.stringify({
                "courseIds": courseIdList,
                "sourceText": textToFind,
                "textToBeReplace": replaceWith
            }),
            success: function(data) {
                // Hide the loader when request is successful
                //document.getElementById('horizontal-loader').style.display = 'none';
                alert('Text replaced successfully!');
               	location.reload();
            },
            error: function(jqXHR, textStatus, errorThrown) {
                // Hide the loader when an error occurs
                document.getElementById('horizontal-loader').style.display = 'none';
                console.log("Error while replacing text: " + errorThrown);
            }
        });
    }
    
function findResult(){
		// Show loader
        document.getElementById('loader').style.display = 'block';
        $(".invalid-feedback").hide();
        
         $("#searchResult").html("");
        
       var courseIds = document.getElementById('course_id').value;
		if(isNullOrBlank(courseIds)) {
			$("#invalid-course_id").show();
			return false;
		}
        var textToFind = document.getElementById('targetText').value;
      	if(isNullOrBlank(textToFind)) {
			$("#invalid-targetText").show();
			return false;
		}

        // Convert the comma-separated string into an array (trim to remove extra spaces)
        let courseIdList = courseIds.split(',').map(id => id.trim());

        $.ajax({
            type: "GET",
            url: appContext + 'ui/v1/find?courseIds='+courseIdList+"&textToFind="+textToFind+"&textToReplace=",
            contentType: "application/json",
            success: function(data) {
                $("#searchResult").html(data);
                // hide loader
       			 document.getElementById('loader').style.display = 'none';
       			 $("#resetButton").removeClass("d-none");
            },
            error: function(jqXHR, textStatus, errorThrown) {
                // Hide the loader when an error occurs
                document.getElementById('horizontal-loader').style.display = 'none';
                console.log("Error while replacing text: " + errorThrown);
            }
        });
    }

// Function to populate result or redirect to another page with parameters
function populateResult(courseIds, textToFind, replaceWith) {     
     $.ajax({
            type: "GET",
            url: appContext + 'ui/v1/search-result?courseIds=' + courseIds +
    '&textToFind=' + textToFind + '&textToReplace=' + replaceWith,
            contentType: "application/json",
            success: function(data) {
                $("#searchResult").html(data);
                // hide loader
       			 document.getElementById('loader').style.display = 'none';
       			 $("#resetButton").removeClass("d-none");
            },
            error: function(jqXHR, textStatus, errorThrown) {
                // Hide the loader when an error occurs
                document.getElementById('horizontal-loader').style.display = 'none';
                // hide loader
       			document.getElementById('loader').style.display = 'none';
                console.log("Error while replacing text: " + errorThrown);
            }
        });
}

function showReplaceContentModal() {
	
	var confirmationModal = new bootstrap.Modal(document.getElementById('replaceTextModal'));
    confirmationModal.show();
}

function replaceContent() {
	if($("#selectAllCourses").is(':checked')) {
		findAndReplace()
	} else {
		replaceItems();
	}
}

function replaceItems() {
	var replaceTextModal = bootstrap.Modal.getInstance(document.getElementById('replaceTextModal'));
    replaceTextModal.hide();
	// Show loader
	document.getElementById('loader').style.display = 'block';
	$(".invalid-feedback").hide();
	// Show the horizontal loader before making the request
	//document.getElementById('horizontal-loader').style.display = 'block';

	var courseItems = getSelectedCourseItemIds();
	var textToFind = document.getElementById('targetText').value;
	var replaceWith = document.getElementById('replaceWith').value;

	// Convert the comma-separated string into an array (trim to remove extra spaces)
	//let courseIdList = courseIds.split(',').map(id => id.trim());

	$.ajax({
		type: "POST",
		url: appContext + 'api/v1/items-replace',
		contentType: "application/json",
		data: JSON.stringify({
			"sourceText": textToFind,
			"textToBeReplace": replaceWith,
			"courseItems": courseItems
		}),
		success: function(data) {
			// Hide the loader when request is successful
			//document.getElementById('horizontal-loader').style.display = 'none';
			alert('Text replaced successfully!');
			location.reload();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			// Hide the loader when an error occurs
			document.getElementById('horizontal-loader').style.display = 'none';
			console.log("Error while replacing text: " + errorThrown);
		}
	});
	
}

function getSelectedCourseItemIds() {
    const courseItems = [];
    document.querySelectorAll('.accordion-item').forEach(accordionItem => {
        const courseId = accordionItem.id;
        const itemIdsMap = {};
        $('.selectCourse-' + courseId).each(function() {
            if (this.checked) {
                const itemIds = $(this).attr("id").split("-");
                const itemId = parseInt(itemIds[2]);
                const type = itemIds[1];
                itemIdsMap[itemId] = type;
            }
        });
        // Check if itemIdsMap has any entries
        if (Object.keys(itemIdsMap).length > 0) {
            courseItems.push({
                courseId: courseId,
                itemIdsMap: itemIdsMap
            });
        }
    });
 
    console.log(courseItems);
    return courseItems; // Return the constructed courseItems array
}

function replaceSingleItem(pageType, pageId) {
	$("#pageId-"+pageType+"-"+pageId).prop("checked",true);
	showReplaceContentModal();
}

$(document).on('click', '.accordion-button', function(e) {
	var target = $(this).attr('data-bs-target');
	console.log($("#"+target).html());
	if($("#"+target).hasClass('show')) {
		$("#"+target).removeClass('show');
		$(this).addClass("collapsed");
		$(this).attr("aria-expanded","false");
	} else {
		$("#"+target).addClass('show');
		$(this).removeClass("collapsed");
		$(this).attr("aria-expanded","true");
	}
	
});

// Reset form and hide loader on reset
$(document).on('click', '#resetButton', function(e) {
	document.getElementById('loader').style.display = 'none';
	document.getElementById('searchResult').innerHTML = '';
	$("#resetButton").addClass("d-none");
	$(".invalid-feedback").hide();
});

/*$(document).on('click', '#submitButton', function(e) {
		$(".invalid-feedback").hide();
		var courseIds = document.getElementById('course_id').value;
		if(isNullOrBlank(courseIds)) {
			$("#invalid-course_id").show();
			return false;
		}
        var targetText = document.getElementById('targetText').value;
      	if(isNullOrBlank(targetText)) {
			$("#invalid-targetText").show();
			return false;
		}
        var updatedText = document.getElementById('updatedText').value;
        
        var confirmationModal = new bootstrap.Modal(document.getElementById('confirmationModal'));
        confirmationModal.show();
});*/

// check if string null or blank or undefined
function isNullOrBlank(string){
    if(string == '' || string == null || string == undefined)
        return true;
    return false;
}
$(document).on('click', '#selectAllCourses', function(e) {
        let chk_status = this.checked;

        // Iterate all listed checkbox items
        $('.common-checkbox').each(function(){
            this.checked = chk_status;
        });
});

// select all course modules
$(document).on('click', '.course-checkbox', function(e) {
	
	let chk_status = this.checked;
	let courseSelector = $(this).attr("id");

    // Iterate all listed checkbox items
    $('.'+courseSelector).each(function(){
        this.checked = chk_status;
    });
    
    if(!chk_status) {
		$('#selectAllCourses').prop('checked', false);
	}
});

