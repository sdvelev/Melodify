
function editPlaylist(playlistId){
    const newName = document.querySelector("dialog input").value;
    fetch(`/api/playlists/${playlistId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({'name': newName})
    })
        .finally(() => {
            window.location.reload();
            window.parent.fetchPlaylists();
        })
        .catch(error => console.error(error));
}

function createEditPlaylistDialog(playlistId) {
    const dialog = document.createElement('dialog');
    dialog.className = 'edit-playlist-dialog';
    dialog.innerHTML = `
            <div class="dialog-content">
                <h2>Edit Playlist</h2>
                <input id="newPlaylistName" type="text" placeholder="Enter new playlist name">
                <div class="dialog-buttons">
                    <button onclick="closeDialog()">Cancel</button>
                    <button onclick="editPlaylist(${playlistId})">Edit</button>
                </div>
            </div>
        `;
    document.body.appendChild(dialog);
    dialog.showModal();
}

function createDeletePlaylistDialog(playlistId) {
    const dialog = document.createElement('dialog');
    dialog.className = 'delete-playlist-dialog';
    dialog.innerHTML = `
            <div class="dialog-content">
                <h2>Delete Playlist</h2>
                <p>Are you sure you want to delete this playlist?</p>
                <div class="dialog-buttons">
                    <button onclick="closeDialog()">Cancel</button>
                    <button onclick="deletePlaylist(${playlistId})">Delete</button>
                </div>
            </div>
        `;
    document.body.appendChild(dialog);
    dialog.showModal();
}

function deletePlaylist(playlistId){
    fetch(`/api/playlists?playlist_id=${playlistId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(() => {
            navigate('./home.html')
            window.parent.fetchPlaylists();
        })
        .catch(error => console.error(error));
}
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
    const dialog = document.createElement('dialog');
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
    dialog.showModal();
}

function closeDialog() {
    const dialog = document.querySelector('dialog');
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