
function fetchPlaylists(){
    redirectToLoginIfInvalidCredentials();
    const token = getToken();
    const userId = getUserId();

    fetch(`/api/users/${userId}`, {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(user => {
            console.log(user);
            document.querySelector("#account img").src = user.image;

            const playlists = user.playlists;
            const libraryItems = document.querySelector('#library .items');

            libraryItems.innerHTML = "";

            if(playlists.length === 0){
                libraryItems.innerHTML = "You have no playlists"
            }

            playlists.forEach(playlist => {
                const item = document.createElement('div');
                item.classList.add('item');

                const itemImageContainer = document.createElement('div');
                itemImageContainer.classList.add("image-container");

                const overflowDiv = document.createElement('div');
                overflowDiv.onclick = function (){
                    playPlaylist(playlist.id);
                }

                const span =document.createElement('span');
                span.classList.add('fa');
                span.classList.add('fa-circle-play');

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
                    navigate(playlist.uri, false);
                };

                const author = document.createElement('p');
                author.classList.add('author');
                author.textContent = user.name;

                info.appendChild(name);
                info.appendChild(author);
                overflowDiv.appendChild(span);
                itemImageContainer.appendChild(img)
                itemImageContainer.appendChild(overflowDiv)
                item.appendChild(itemImageContainer);
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
    fetch(`http://localhost:8080/api/queues`, {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(queues => queues[0])
        .then(queue => {

            const queueItems = document.querySelector('#queue .items');

            queueItems.innerHTML = "";

            if (queue.songs.length === queue.currentSongIndex){
                document.querySelector("#track_info .name").textContent = "";
                document.querySelector("#track_info .author").textContent = "";
                document.querySelector("#track_info img").src = "";
                return;
            }

            document.querySelector("#track_info .name").textContent = queue.songs[queue.currentSongIndex].name;
            document.querySelector("#track_info .author").textContent = queue.songs[queue.currentSongIndex].album_name;
            document.querySelector("#track_info img").src = queue.songs[queue.currentSongIndex].album_image;


            queue.songs.slice(queue.currentSongIndex).forEach(song => {
                const item = document.createElement('div');
                item.classList.add('item');

                const img = document.createElement('img');
                img.src = song.album_image;

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
            libraryItems.innerHTML = "We couldn't fetch your queue. "
        });
}

fetchPlaylists();
fetchQueue();