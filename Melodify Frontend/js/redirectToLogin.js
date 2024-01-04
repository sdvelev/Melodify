function redirectToLoginIfInvalidCredentials(){
    const token = getToken();
    const userId = getUserId();

    if (token === null || userId === null) {
        window.location.href = "./../user/login.html";
    }
}

redirectToLoginIfInvalidCredentials();

