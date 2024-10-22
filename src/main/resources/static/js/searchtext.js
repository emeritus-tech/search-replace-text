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
 
    function findAndReplace(courseIds, textToFind, replaceWith) {
        // Show the horizontal loader before making the request
        document.getElementById('horizontal-loader').style.display = 'block';

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
                document.getElementById('horizontal-loader').style.display = 'none';
                console.log('Text replaced successfully!');
                populateResult(courseIds, textToFind, replaceWith);
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
  window.location.href = appContext + 'ui/v1/search-result?courseIds=' + courseIds +
    '&textToFind=' + textToFind + '&textToReplace=' + replaceWith;
}

