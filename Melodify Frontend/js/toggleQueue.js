function toggleQueue() {
    const queue = document.getElementById("queue");
    const button = document.querySelector("#account span.fa-list");
    if (queue.style.display === "none") {
        queue.style.display = "grid";
        button.classList.add("active")
    } else {
        queue.style.display = "none";
        button.classList.remove("active");
    }
}