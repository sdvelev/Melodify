const artistButton = document.querySelector("#search_filters button.artist-filter");
const artistSearchResults = document.querySelector("#artist_search_results");

const albumButton = document.querySelector("#search_filters button.album-filter");
const albumSearchResults = document.querySelector("#album_search_results");

const songButton = document.querySelector("#search_filters button.song-filter");
const songSearchResults = document.querySelector("#song_search_results");

artistButton.addEventListener('click', event => {
    artistButton.classList.toggle("active");
    artistSearchResults.classList.toggle("active");
});

albumButton.addEventListener('click', event => {
    albumButton.classList.toggle("active");
    albumSearchResults.classList.toggle("active");
});

songButton.addEventListener('click', event => {
    songButton.classList.toggle("active");
    songSearchResults.classList.toggle("active");
});


async function performSearch() {
    const searchBox = document.querySelector('#search input[type="search"]');
    const searchTerm = searchBox.value.trim();

    if (searchTerm !== '') {
        try {
            const artistResults = await search('/api/artists', searchTerm);
            const albumResults = await search('/api/albums', searchTerm);
            const songResults = await search('/api/songs', searchTerm);

            updateSearchResults('artist', artistResults);
            updateSearchResults('album', albumResults);
            updateSearchResults('song', songResults);
        } catch (error) {
            console.error('Error fetching search results:', error);
        }
    }
}

async function search(endpoint, term) {
    console.log(`searching at ${endpoint}?name=${term}`)
    const response = await fetch(`${endpoint}?name=${term}`);
    if (!response.ok) {
        throw new Error(`Failed to fetch ${endpoint}`);
    }
    return response.json();
}

function updateSearchResults(category, results) {
    const categoryElement = document.querySelector(`#${category}_search_results`);

    if (results.length > 0) {
        categoryElement.classList.add('active');
        const itemsElement = categoryElement.querySelector('.items');
        itemsElement.innerHTML = '';

        results.forEach(result => {
            const itemElement = document.createElement('div');
            itemElement.classList.add('item');

            // Customize based on category (artist, album, song)
            switch (category) {
                case 'artist':
                    itemElement.classList.add('artist');
                    itemElement.innerHTML = `
                        <img src="${result.image}">
                        <p>${result.name}</p>
                    `;
                    break;
                case 'album':
                    itemElement.innerHTML = `
                        <img src="${result.image}">
                        <div class="info">
                            <p class="name">${result.name}</p>
                            <p class="author">${result.artists.map(artist => artist.name).join(", ")}</p>
                        </div>
                    `;
                    break;
                case 'song':
                    itemElement.innerHTML = `
                        <img src="${result.album.image}">
                        <div class="info">
                            <p class="name">${result.name}</p>
                            <p class="author">${result.album.name}, ${result.artists.map(artist => artist.name).join(", ")}</p>
                        </div>
                    `;
                    break;
                default:
                    break;
            }

            itemsElement.appendChild(itemElement);
        });
    } else {
        categoryElement.classList.remove('active');
    }
}
