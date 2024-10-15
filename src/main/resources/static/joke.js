const joke = {
    init: function() {
        setupJoke();
    }
}


const getJoke = () => {
    fetch("/api/v1/joke", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    }).then(response => response.json())
        .then(data => {
            console.log(data);
            const today = new Date().toLocaleDateString(); // 오늘 날짜
            localStorage.setItem("dailyJoke", JSON.stringify({
                question: data.question,
                answer: data.answer,
                date: today
            }));
            document.getElementById("question").textContent = data.question;
            document.getElementById("answer").textContent = data.answer;
        })
}

const setupJoke = () => {
    const savedJoke = JSON.parse(localStorage.getItem("dailyJoke"));
    const today = new Date().toLocaleDateString();

    if (!savedJoke || savedJoke.date !== today) {
        getJoke(); // 오늘 개그가 없거나 날짜가 다르면 새로운 개그 요청
    } else {
        document.getElementById("question").textContent = "Q. " + savedJoke.question;
        document.getElementById("answer").textContent = "A. " + savedJoke.answer;
    }
}


document.addEventListener("DOMContentLoaded", () => {
    joke.init();
})


