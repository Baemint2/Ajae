const joke = {
    init: function() {
        setupJoke();
        bookMark();
        openAnswer();
    },

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

const openAnswer = () => {
    const openAnswer = document.getElementById("openAnswer");
    openAnswer.addEventListener("click", () => {
        document.getElementById("answer").style.display = "block";
        openAnswer.style.display = "none";
    });
}

const bookMark = () => {
    document.querySelector(".bookmark-wrap img").addEventListener("click", () => {
        const joke = JSON.parse(localStorage.getItem("dailyJoke"));
        console.log(JSON.stringify(joke));

        fetch("/api/v1/bookmark", {
            method: "POST",
            headers: {
                "Content-Type": "application/json" // 서버가 JSON 형식을 기대하는 경우
            },
            body: JSON.stringify(joke) // joke를 JSON 형식으로 변환
        })
        .then(response => {
            console.log(response);
            return response.json()
        })
        .then(data => {
            if(data.message) {
                alert(data.message);
                const emptyStar = document.querySelector(".empty_star");
                emptyStar.style.display = "none";
                const yellowStar = document.createElement("img");
                yellowStar.src = "/img/yellow_star.png";
                yellowStar.alt = "노란별";
                document.querySelector(".bookmark-wrap").appendChild(yellowStar);
            }
        })
        .catch(error => {
        })
    });
}




document.addEventListener("DOMContentLoaded", () => {
    joke.init();
})


