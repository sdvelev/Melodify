const contentContainer = document.querySelector('main #content');

function loadContent(url) {
    fetch(url, {
    })
        .then(response => response.text())
        .then(data => {
            contentContainer.innerHTML = data;
        })
        .catch(error => console.error('Error fetching content:', error));
}

function navigate(url) {
    history.pushState(null, null, url);
    loadContent(url);
}

document.addEventListener('DOMContentLoaded', () => {
    loadContent('../user/home.html');

    const links = document.querySelectorAll('a');
    links.forEach(link => {
        link.addEventListener('click', event => {
            event.preventDefault();
            navigate(event.target.href);
        });
    });

    window.onpopstate = () => {
        loadContent(window.location.pathname);
    };
});
