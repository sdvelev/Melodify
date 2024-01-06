
function clearQueue(){
    fetch("/api/queues/clear", {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(() => {
        })
        .catch(error => console.error(error.message));
}
function playAlbum(albumId, songIndex = 0, shuffle = false) {
    fetch(`/api/albums/${albumId}`, {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(album => album.songs)
        .then(
            songs => songs.map(song => song.id)
        ).then(songIds => {
        let ids;
        if (shuffle) {
            let temp = songIds.filter(id => id !== songIds[songIndex]).sort(() => Math.random() - 0.5);
            temp.unshift(songIds[songIndex]);
            ids = temp;
        } else {
            let temp = songIds.slice(songIndex + 1);
            temp.unshift(songIds[songIndex]);
            ids = temp;
        }

        clearQueue();
        fetch(`/api/queues/add?song_ids=${ids.join(",")}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`,
                'Content-Type': 'application/json'
            },
            method: 'PATCH'
        })
            .then(response => response.json())
            .then(() => {
                    fetchQueue();
                    currentSong();
                }
            )
            .catch(error => console.error(error.message));
    });
}

function playPlaylist(playlistId, songIndex = 0, shuffle = false) {
    fetch(`/api/playlists/${playlistId}`, {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(playlist => playlist.songs)
        .then(
            songs => songs.map(song => song.id)
        ).then(songIds => {
        let ids;
        if (shuffle) {
            let temp = songIds.filter(id => id !== songIds[songIndex]).sort(() => Math.random() - 0.5);
            temp.unshift(songIds[songIndex]);
            ids = temp;
        } else {
            let temp = songIds.slice(songIndex + 1);
            temp.unshift(songIds[songIndex]);
            ids = temp;
        }

        clearQueue();
        fetch(`/api/queues/add?song_ids=${ids.join(",")}`, {
            headers: {
                'Authorization': `Bearer ${getToken()}`,
                'Content-Type': 'application/json'
            },
            method: 'PATCH'
        })
            .then(response => response.json())
            .then(() => {
                    fetchQueue();
                    currentSong();
                }
            )
            .catch(error => console.error(error.message));
    });
}

function playSong(songId) {
    clearQueue();
    fetch(`/api/queues/add?song_ids=${songId}`, {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        },
        method: 'PATCH'
    })
        .then(response => response.json())
        .then(() => {
                fetchQueue();
                currentSong();
            }
        )
        .catch(error => console.error(error.message));
}