let audio = document.querySelector('#player_controls audio');
let playButton = document.querySelector('#player_controls .fa-circle-play').parentNode;
let forwardButton = document.querySelector('#player_controls .fa-forward').parentNode;
let backwardButton = document.querySelector('#player_controls .fa-backward').parentNode;
let trackRange = document.querySelector('#player .track_slider input');
let currentTimeDisplay = document.querySelector('#current_time');
let durationDisplay = document.querySelector('#duration');
let volumeRange = document.querySelector('#account input[type="range"]');



function nextSong() {
    return fetch('/api/queues/next', {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.status === 404) {
                audio.removeAttribute('src');
                forwardButton.disabled = true;
                return Promise.resolve();
            }
            else if (!response.ok){
                throw new Error('No next song');
            }
            return response.blob();
        })
        .then(song => {
            if(song){
                audio.setAttribute('src', URL.createObjectURL(song));
                play();
            }
        })
        .catch(error => console.error(error.message));
}

function previousSong() {
    return fetch('/api/queues/previous', {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.status === 404) {
                audio.removeAttribute('src');
                backwardButton.disabled = true;
                return Promise.resolve();
            }
            else if (!response.ok){
                throw new Error('No previous song');
            }
            return response.blob();
        })
        .then(song => {
            if(song){
                audio.setAttribute('src', URL.createObjectURL(song));
                play();
            }
        })
        .catch(error => console.error(error.message));
}

function currentSong(startPlaying = true) {
    return fetch('/api/queues/play', {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.status === 404) {
                audio.removeAttribute('src');
                // forwardButton.disabled = true;
                // backwardButton.disabled = true;
                playButton.disabled = true;
                return Promise.resolve();
            }
            else if (!response.ok){
                throw new Error('No next song');
            }
            return response.blob();
        })
        .then(song => {
            if(song){
                audio.setAttribute('src', URL.createObjectURL(song));
                if(startPlaying){
                    play();
                }
            }
        })
        .catch(error => console.error(error.message));
}

function play() {
    if (audio.paused) {
        audio.play();
    } else {
        audio.pause();
    }
    togglePlayButton();
}

playButton.addEventListener('click', () => {
    play();
});

forwardButton.addEventListener('click', () => {
    nextSong()
        .finally(() => {
        fetchQueue();
    })
        .catch(error => console.error(error.message));
});

backwardButton.addEventListener('click', () => {
    previousSong()
        .finally(() => {
            fetchQueue();
        })
        .catch(error => console.error(error.message));
});

trackRange.addEventListener('input', () => {
    audio.currentTime = trackRange.value;
});

volumeRange.addEventListener('input', () => {
    audio.volume = volumeRange.value;
});

audio.addEventListener('timeupdate', () => {
    trackRange.value = audio.currentTime;
    currentTimeDisplay.textContent = formatTime(audio.currentTime);
});

audio.addEventListener('loadedmetadata', () => {
    trackRange.min = 0;
    trackRange.max = Math.floor(audio.duration);
    durationDisplay.textContent = formatTime(audio.duration);
});

audio.addEventListener('ended', () => {
    trackRange.value = trackRange.max;
    togglePlayButton();
    nextSong()
        .finally(() => {
            fetchQueue();
        })
        .catch(error => console.error(error.message));
});

function formatTime(time) {
    const minutes = Math.floor(time / 60);
    const seconds = Math.floor(time % 60);
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
}

function togglePlayButton() {
    if (audio.paused) {
        playButton.querySelector("span").classList.replace("fa-circle-pause", "fa-circle-play")

    } else {
        playButton.querySelector("span").classList.replace("fa-circle-play", "fa-circle-pause")
    }
}