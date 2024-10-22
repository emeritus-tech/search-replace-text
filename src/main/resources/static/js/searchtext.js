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
 
 // Function to handle the AJAX request
// Function to handle the AJAX request
function findAndReplace(courseIds, textToFind, replaceWith) {
  // Convert the comma-separated string into an array (trim to remove extra spaces)
  let courseIdList = courseIds.split(',').map(id => id.trim());

  $.ajax({
    type: "POST",
    url: appContext + 'api/v1/search-replace',
    contentType: "application/json",
    data: JSON.stringify({
      "courseIds": courseIdList, // Send the array of course IDs
      "sourceText": textToFind,
      "textToBeReplace": replaceWith
    }),
    success: function(data) {
      // Handle success response (optional UI refresh)
      console.log('Text replaced successfully!');
      // Optionally call populateResult function to refresh the UI or load new data
      populateResult(courseIds, textToFind, replaceWith);
    },
    error: function(jqXHR, textStatus, errorThrown) {
      console.log("Error while replacing text: " + errorThrown);
    }
  });
}

// Function to populate result or redirect to another page with parameters
function populateResult(courseIds, textToFind, replaceWith) {
  window.location.href = appContext + 'ui/v1/search-result?courseIds=' + courseIds +
    '&textToFind=' + textToFind + '&textToReplace=' + replaceWith;
}

