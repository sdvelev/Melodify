function playAlbum(albumId, songIndex = 0) {
    window.parent.postMessage({action: 'playAlbum', albumId: albumId, songIndex: songIndex}, '*');
}

function playPlaylist(playlistId, songIndex = 0) {
    window.parent.postMessage({action: 'playPlaylist', playlistId: playlistId, songIndex: songIndex}, '*');
}

function playSong(songId, dropQueue = true) {
    window.parent.postMessage({action: 'playSong', songId: songId, dropQueue: dropQueue}, '*');
}