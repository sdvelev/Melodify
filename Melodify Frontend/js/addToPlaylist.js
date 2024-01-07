function removeFromPlaylist(playlistId, songId) {
    fetch(`/api/playlists/${playlistId}/remove?song_id=${songId}`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .finally(() => {
            window.location.reload();
            // closeDialog();
        })
        .catch(error => {
            console.error('Error removing song from playlist:', error);
        });
}

function fetchPlaylists() {
    return fetch('/api/users', {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(users => users[0].playlists)
        .catch(error => {
            console.error('Error fetching user playlists:', error);
            return [];
        });
}

function createAddToPlaylistDialog(songId) {
    const dialog = document.createElement('div');
    dialog.className = 'add-to-playlist-dialog';
    dialog.innerHTML = `
            <div class="dialog-content">
                <h2>Add Song to Playlist</h2>
                <label for="playlistSelect">Select Playlist:</label>
                <select id="playlistSelect">
                </select>
                <div class="dialog-buttons">
                    <button onclick="closeDialog()">Cancel</button>
                    <button onclick="addToPlaylist(${songId})">Add</button>
                </div>
            </div>
        `;
    document.body.appendChild(dialog);

    fetchPlaylists().then(playlists => {
        const playlistSelect = document.getElementById('playlistSelect');
        playlists.forEach(playlist => {
            const option = document.createElement('option');
            option.value = playlist.id;
            option.textContent = playlist.name;
            playlistSelect.appendChild(option);
        });
    });
}

function closeDialog() {
    const dialog = document.querySelector('.add-to-playlist-dialog');
    if (dialog) {
        dialog.remove();
    }
}

function addToPlaylist(songId) {
    const playlistSelect = document.getElementById('playlistSelect');
    const selectedPlaylistId = playlistSelect.value;

    fetch(`/api/playlists/${selectedPlaylistId}/add?song_id=${songId}`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .finally(() => {
            window.location.reload();
            // closeDialog();
        })
        .catch(error => {
            console.error('Error adding song to playlist:', error);
        });
}
