redirectToLoginIfInvalidCredentials();
const userId = getUserId();

function clickArtistButton(){
    const artistButton = document.querySelector("#search_filters button.artist-filter");
    const artistSearchResults = document.querySelector("#user_artists");
    artistButton.classList.toggle("active");
    artistSearchResults.classList.toggle("active");
}

function clickAlbumButton(){
    const albumButton = document.querySelector("#search_filters button.album-filter");
    const albumSearchResults = document.querySelector("#user_albums");

    albumButton.classList.toggle("active");
    albumSearchResults.classList.toggle("active");
}

function clickPlaylistButton(){
    const songButton = document.querySelector("#search_filters button.song-filter");
    const songSearchResults = document.querySelector("#user_playlists");
    songButton.classList.toggle("active");
    songSearchResults.classList.toggle("active");
}
function load() {
    fetch(`/api/users/${userId}`)
        .then(response => response.json())
        .then(user => {
            const playlists = user.playlists;

            populateUserPlaylists(playlists);

            const albumIds = playlists.reduce((acc, playlist) => {
                playlist.songs.forEach(song => {
                    acc.push(song.album_id);
                });
                return acc;
            }, []);

            const albumIdCount = albumIds.reduce((acc, albumId) => {
                if (!acc[albumId]) {
                    acc[albumId] = 0;
                }
                acc[albumId]++;
                return acc;
            }, {});

            const sortedAlbumIds = Object.keys(albumIdCount).sort((a, b) => albumIdCount[b] - albumIdCount[a]);
            populateUserAlbums(sortedAlbumIds);
            populateUserArtists(playlists);

        })
        .catch(error => {
            console.log(error.message);
        })
}

function populateUserPlaylists(playlists) {
    const userPlaylistsElement = document.querySelector('#user_playlists');
    const userPlaylistsItemsElement = document.querySelector('#user_playlists .items');
    playlists.forEach(playlist => {
        const playlistItem = document.createElement('div');
        playlistItem.classList.add('item', 'album');
        playlistItem.innerHTML = `
            <img src="${playlist.image}">
            <div class="info">
                <button class="a name" onclick="navigate('./playlist.html?id=${playlist.id}')">${playlist.name}</button>
            </div>
        `;
        userPlaylistsItemsElement.appendChild(playlistItem);
    });
    userPlaylistsElement.appendChild(userPlaylistsItemsElement);
}


async function fetchAlbumData(albumId) {
    try {
        const albumResponse = await fetch(`/api/albums/${albumId}`);
        if (!albumResponse.ok) {
            throw new Error(`Failed to fetch album with ID ${albumId}`);
        }
        return await albumResponse.json();
    } catch (error) {
        console.error(`Error fetching album with ID ${albumId}:`, error);
        return null;
    }
}
async function populateUserAlbums(sortedAlbumIds) {
    const userAlbumsElement = document.querySelector('#user_albums');
    const userAlbumsItemsElement = document.querySelector('#user_albums .items');

    for (const albumId of sortedAlbumIds) {
        const albumData = await fetchAlbumData(albumId);
        if (albumData) {
            const albumItem = document.createElement('div');
            albumItem.classList.add('item', 'album');

            const artistsHtml = albumData.artists.map(artist => `<button class="a name" onclick="navigate('./artist.html?id=${artist.id}')">${artist.name}</button>`).join(', ');

            albumItem.innerHTML = `
                <img src="${albumData.image}">
                <div class="info">
                    <button class="a name" onclick="navigate('./album.html?id=${albumData.id}')">${albumData.name}</button>
                    <p class="author">${artistsHtml}</p>
                </div>
            `;

            userAlbumsItemsElement.appendChild(albumItem);
        }
    }
    userAlbumsElement.appendChild(userAlbumsItemsElement);
}
async function populateUserArtists(playlists) {
    const userArtistsElement = document.querySelector('#user_artists');
    const userArtistsItemsElement = document.querySelector('#user_artists .items');

    const allArtists = playlists.reduce((acc, playlist) => {
        playlist.songs.forEach(song => {
            song.artists.forEach(artist => {
                const foundArtist = acc.find(a => a.id === artist.id);
                if (foundArtist) {
                    foundArtist.count++;
                } else {
                    acc.push({id: artist.id, name: artist.name, image: artist.image, count: 1});
                }
            });
        });
        return acc;
    }, []);

    const sortedArtists = allArtists.sort((a, b) => b.count - a.count);

    sortedArtists.forEach(artist => {
        const artistItem = document.createElement('div');
        artistItem.classList.add('item', 'artist');

        artistItem.innerHTML = `
            <img src="${artist.image}">
            <div class="info">
                <button class="a name" onclick="navigate('./artist.html?id=${artist.id}')">${artist.name}</button>
            </div>
        `;

        userArtistsItemsElement.appendChild(artistItem);
    });
    userArtistsElement.appendChild(userArtistsItemsElement);
}
