function handleRegister(event) {
    document.getElementById('error-box').style.display = "none";
    event.preventDefault();
    const name = document.querySelector('input[placeholder="Enter name"]').value;
    const surname = document.querySelector('input[placeholder="Enter surname"]').value;
    const email = document.querySelector('input[type="email"]').value;
    const password = document.querySelector('input[type="password"]').value;

    const userData = {
        name: name,
        surname: surname,
        email: email,
        password: password
    };

    fetch('http://localhost:8080/api/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Invalid credentials. Please try again.');
            }
            window.location.href = '/login.html';
        })
        .catch(error => {
            const errorBox = document.getElementById('error-box');
            errorBox.textContent = error.message;
            errorBox.style.display = 'block'
        });
}