function doUpload(event){
    const file = event.target.files[0];
    if (file) {
        uploadImage(file);
    }
}


function openFileDialog() {
    document.querySelector('#account input[type="file"]').click();
}

function uploadImage(file) {
    const formData = new FormData();
    formData.append('file', file);

    fetch('/images/users', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${getToken()}`
        },
        body: formData
    })
        .then(response => response.text())
        .then(data => {
            updateProfileImage(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function updateProfileImage(imageUrl) {
    fetch('/api/users/settings', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify({image: imageUrl})
    })
        .then(() =>
            document.querySelector("#account img").setAttribute('src', imageUrl)
        )
        .catch(error => {
            console.error('Error:', error);
        });
}