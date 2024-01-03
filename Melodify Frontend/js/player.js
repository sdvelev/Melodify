const audio = document.querySelector('#player_controls audio');
const playButton = document.querySelector('#player_controls .fa-circle-play');
const forwardButton = document.querySelector('#player_controls .fa-forward');
const backwardButton = document.querySelector('#player_controls .fa-backward');
const trackRange = document.querySelector('#player .track_slider input');
const currentTimeDisplay = document.querySelector('#current_time');
const durationDisplay = document.querySelector('#duration');
const volumeRange = document.querySelector('#account input[type="range"]');


playButton.addEventListener('click', () => {
    if (audio.paused) {
        audio.play();
        togglePlayButton();
    } else {
        audio.pause();
        playButton.classList.replace("fa-circle-pause", "fa-circle-play")
    }
});

forwardButton.addEventListener('click', () => {
// TODO
});

// Backward button (skip back by 10 seconds)
backwardButton.addEventListener('click', () => {
    // audio.currentTime -= 10;
    // TODO
});

// Track range
trackRange.addEventListener('input', () => {
    audio.currentTime = trackRange.value / 100.0;
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