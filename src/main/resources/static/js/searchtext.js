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