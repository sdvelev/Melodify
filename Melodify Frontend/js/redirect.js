const contentContainer = document.querySelector('main #content');

function loadContent(url) {
    fetch(url, {
    })
        .then(response => response.text())
        .then(data => {
            if(!data){
                console.error(`Data at ${url} is null`);
            }
            const parser = new DOMParser();
            const htmlDocument = parser.parseFromString(data, 'text/html');
            if(!htmlDocument){
                console.error(`Html at ${url} is null`);
            }
            contentContainer.innerHTML = htmlDocument.querySelector('#content').innerHTML;
        })
        .catch(error => console.error(`Error fetching content from ${url}:`, error));
}

function navigate(url) {
    history.pushState(null, null, url);
    loadContent(url);
}

