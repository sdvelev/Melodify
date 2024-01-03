function toggleSidebar() {
    const sidebar = document.getElementById("sidebar");
    const button = document.querySelector("#account span.fa-bookmark");
    if (sidebar.style.display === "none") {
        sidebar.style.display = "grid";
        button.classList.add("active")
    } else {
        sidebar.style.display = "none";
        button.classList.remove("active");
    }
}