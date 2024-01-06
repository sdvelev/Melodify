const audio = document.querySelector('#player_controls audio');
const playButton = document.querySelector('#player_controls .fa-circle-play');
const forwardButton = document.querySelector('#player_controls .fa-forward').parentNode;
const backwardButton = document.querySelector('#player_controls .fa-backward').parentNode;
const trackRange = document.querySelector('#player .track_slider input');
const currentTimeDisplay = document.querySelector('#current_time');
const durationDisplay = document.querySelector('#duration');
const volumeRange = document.querySelector('#account input[type="range"]');



function nextSong() {
    fetch('/api/queues/next', {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.status === 404) {
                document.querySelector("#player audio").src = "";
                forwardButton.disabled = true;
                return;
            }
            else if (!response.ok){
                throw new Error('No next song');
            }
            return response.blob();
        })
        .then(song => {
            document.querySelector("#player audio").src = URL.createObjectURL(song);
            play();
        })
        .catch(error => console.error(error.message));
}

function previousSong() {
    fetch('/api/queues/previous', {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.status === 404) {
                document.querySelector("#player audio").src = "";
                backwardButton.disabled = true;
                return;
            }
            else if (!response.ok){
                throw new Error('No next song');
            }
            return response.blob();
        })
        .then(song => {
            document.querySelector("#player audio").src = URL.createObjectURL(song);
            play();
        })
        .catch(error => console.error(error.message));
}

function currentSong() {
    fetch('/api/queues/play', {
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.status === 404) {
                document.querySelector("#player audio").src = "";
                forwardButton.disabled = true;
                backwardButton.disabled = true;
                playButton.parentNode.disabled = true;
                return;
            }
            else if (!response.ok){
                throw new Error('No next song');
            }
            return response.blob();
        })
        .then(song => {
            document.querySelector("#player audio").src = URL.createObjectURL(song);
            play();
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
    fetchQueue();
    nextSong();
});

backwardButton.addEventListener('click', () => {
    // audio.currentTime -= 10;
    // TODO
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
    fetchQueue();
    nextSong();
});

function formatTime(time) {
    const minutes = Math.floor(time / 60);
    const seconds = Math.floor(time % 60);
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
}

function togglePlayButton() {
    if (audio.paused) {
        playButton.classList.replace("fa-circle-pause", "fa-circle-play")

    } else {
        playButton.classList.replace("fa-circle-play", "fa-circle-pause")

    }
}