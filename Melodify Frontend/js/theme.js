function setTheme(themeName) {
    localStorage.setItem('theme', themeName);
    document.documentElement.className = themeName;
    const iframe = document.querySelector("iframe");
    if(iframe){
        const iframeDocument = iframe.contentDocument || iframe.contentWindow.document;
        iframeDocument.body.className = themeName;
    }

}
function toggleTheme() {
    if (localStorage.getItem('theme') === 'theme-dark'){
        setTheme('theme-light');
    } else {
        setTheme('theme-dark');
    }
}
(function () {
    if (localStorage.getItem('theme') === 'theme-dark') {
        setTheme('theme-dark');
    } else {
        setTheme('theme-light');
    }
})();