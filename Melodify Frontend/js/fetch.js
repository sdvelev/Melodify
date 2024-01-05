
function fetchPlaylists(){
    redirectToLoginIfInvalidCredentials();
    const token = getToken();
    const userId = getUserId();

    fetch(`http://localhost:8080/api/users/${userId}`, {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(user => {
            console.log(user);
            const playlists = user.playlists;
            const libraryItems = document.querySelector('#library .items');

            libraryItems.innerHTML = "";

            if(playlists.length === 0){
                libraryItems.innerHTML = "You have no playlists"
            }

            playlists.forEach(playlist => {
                const item = document.createElement('div');
                item.classList.add('item');

                const img = document.createElement('img');
                img.src = playlist.image;

                const info = document.createElement('div');
                info.classList.add('info');

                const name = document.createElement('button');
                name.classList.add('name');
                name.classList.add('a');
                name.textContent = playlist.name;
                // name.href = playlist.uri;
                name.onclick = function() {
                    navigate(playlist.uri);
                };

                const author = document.createElement('p');
                author.classList.add('author');
                author.textContent = user.name;

                info.appendChild(name);
                info.appendChild(author);
                item.appendChild(img);
                item.appendChild(info);
                libraryItems.appendChild(item);
            });
        })
        .catch(error => {
            const libraryItems = document.querySelector('#library .items');
            libraryItems.innerHTML = "We couldn't fetch your playlists. "
            console.log(error.message);
        });
}

function fetchQueue(){
    const token = getToken();
    const userId = getUserId();
    fetch(`http://localhost:8080/api/users/${userId}`, {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then (user => user.queue)
        .then(queue => {
            const queueItems = document.querySelector('#queue .items');

            queueItems.innerHTML = "";

            queue.forEach(song => {
                const item = document.createElement('div');
                item.classList.add('item');

                const img = document.createElement('img');
                img.src = song.image;

                const info = document.createElement('div');
                info.classList.add('info');

                const name = document.createElement('p');
                name.classList.add('name');
                name.textContent = song.name;

                const author = document.createElement('p');
                author.classList.add('author');
                author.textContent = song.album;

                // Appending elements to create the structure
                info.appendChild(name);
                info.appendChild(author);
                item.appendChild(img);
                item.appendChild(info);
                queueItems.appendChild(item);
            });
        })
        .catch(error => {
            console.log(error.message);
            const libraryItems = document.querySelector('#queue .items');
            libraryItems.innerHTML = "We couldn't fetch your playlists. "
        });
}

fetchPlaylists();