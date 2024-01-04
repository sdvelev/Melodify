function handleLogin(event) {
    document.querySelector("form button").display = "none";

    event.preventDefault();

    const email = document.querySelector('input[type="email"]').value;
    const password = document.querySelector('input[type="password"]').value;

    // Assuming you're using fetch to make the API call
    fetch('http://localhost:8080/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({email, password}),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Invalid credentials. Please try again.');
            }
            return response.json();
        })
        .then(data => {
            storeToken(data.token, data.user_id);
            window.location.href = './../user/index.html';
        })
        .catch(error => {
            const errorBox = document.getElementById('error-box');
            errorBox.textContent = error.message;
            errorBox.style.display = 'block';
        });
}