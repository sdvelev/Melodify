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
        body: JSON.stringify({ email, password }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Invalid credentials. Please try again.');
            }
            return response.json(); // Parse the JSON response
        })
        .then(data => {
            localStorage.setItem('jwt', data.jwt);
            localStorage.setItem('userId', data.userId);
            // Redirect or perform actions upon successful login
            // For example, redirect to a dashboard page:
            window.location.href = '/index.html';
        })
        .catch(error => {
            const errorBox = document.getElementById('error-box');
            errorBox.textContent = error.message;
            errorBox.style.display = 'block'; // Show the error box
        });
}