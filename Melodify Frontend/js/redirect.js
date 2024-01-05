const contentContainer = document.querySelector('main #content');


function preventRedirect(){
    const links = document.querySelectorAll("a");
    links.forEach(link => {
        link.addEventListener("click", event => {
            event.preventDefault();
            navigate(link.href);
        })
    })
}

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
            const links = contentContainer.querySelectorAll("a");
            links.forEach(link => {
                link.addEventListener("click", event => {
                    event.preventDefault();
                    navigate(link.href);
                })
            })
        })
        .catch(error => console.error(`Error fetching content from ${url}:`, error));
}

function navigate(url, redirect=true) {
    if(redirect){
        window.location.href=url;
    } else {
        history.pushState(null, null, url);
        document.querySelector("iframe").src = url;
        // loadContent(url);
    }
}

