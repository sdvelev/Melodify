function storeToken(jwt, userId) {
    const expiryDate = new Date();
    expiryDate.setTime(expiryDate.getTime() + (24 * 60 * 60 * 1000)); // 24hs

    sessionStorage.setItem('jwt', jwt);
    sessionStorage.setItem('userId', userId);
    sessionStorage.setItem('expiryDate', expiryDate.toISOString());
}

function getToken() {
    const jwt = sessionStorage.getItem('jwt');
    const expiryDate = sessionStorage.getItem('expiryDate');

    if (jwt && expiryDate) {
        const currentTimestamp = new Date().getTime();
        const tokenExpiryTimestamp = new Date(expiryDate).getTime();

        if (currentTimestamp < tokenExpiryTimestamp) {
            return jwt;
        }
        sessionStorage.removeItem('jwt');
        sessionStorage.removeItem('userId');
        sessionStorage.removeItem('expiryDate');
        return null;
    }
    return null;
}

function getUserId() {
    const userId = sessionStorage.getItem('userId');
    const expiryDate = sessionStorage.getItem('expiryDate');

    if (userId && expiryDate) {
        const currentTimestamp = new Date().getTime();
        const tokenExpiryTimestamp = new Date(expiryDate).getTime();

        if (currentTimestamp < tokenExpiryTimestamp) {
            return userId;
        }
        sessionStorage.removeItem('jwt');
        sessionStorage.removeItem('userId');
        sessionStorage.removeItem('expiryDate');
        return null;
    }
    return null;
}
