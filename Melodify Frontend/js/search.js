function clickArtistButton(){
    const artistButton = document.querySelector("#search_filters button.artist-filter");
    const artistSearchResults = document.querySelector("#artist_search_results");
    artistButton.classList.toggle("active");
    artistSearchResults.classList.toggle("active");
}

function clickAlbumButton(){
    const albumButton = document.querySelector("#search_filters button.album-filter");
    const albumSearchResults = document.querySelector("#album_search_results");

    albumButton.classList.toggle("active");
    albumSearchResults.classList.toggle("active");
}

function clickSongButton(){
    const songButton = document.querySelector("#search_filters button.song-filter");
    const songSearchResults = document.querySelector("#song_search_results");
    songButton.classList.toggle("active");
    songSearchResults.classList.toggle("active");
}

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

            switch (category) {
                case 'artist':
                    itemElement.classList.add('artist');
                    itemElement.innerHTML = `
                        <img src="${result.image}">
                        <button class="a name" onclick="navigate('./artist.html?id=${result.id}')">${result.name}</button>
                    `;
                    break;
                case 'album':
                    itemElement.innerHTML = `
                        <img src="${result.image}">
                        <div class="info">
                            <button class="a name" onclick="navigate('./album.html?id=${result.id}')">${result.name}</button>
                            <p class="author">${result.artists.map(artist => "<button class='a' onclick='navigate(\"./artist.html?id="+artist.id+"\")'>"+artist.name+"</button>").join(", ")}</p>
                        </div>
                    `;
                    break;
                case 'song':
                    itemElement.innerHTML = `
                        <img src="${result.album_image}">
                        <div class="info">
                            <button class="a name" onclick="navigate('./album.html?id=${result.album_id}')">${result.name}</button>
                            <p class="author"><button class="a" onclick="navigate('./album.html?id=${result.album_id}')">${result.album_name}</button> • ${result.artists.map(artist => "<button class='a' onclick='navigate(\"./artist.html?id=" + artist.id +"\")'>"+artist.name+"</button>").join(", ")}</p>
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
