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

const openAnswer = document.getElementById("openAnswer");
openAnswer.addEventListener("click", () => {
    document.getElementById("answer").style.display = "block";
    openAnswer.style.display = "none";

});

document.addEventListener("DOMContentLoaded", () => {
    const savedJoke = JSON.parse(localStorage.getItem("dailyJoke"));
    const today = new Date().toLocaleDateString();

    if (!savedJoke || savedJoke.date !== today) {
        getJoke(); // 오늘 개그가 없거나 날짜가 다르면 새로운 개그 요청
    } else {
        document.getElementById("question").textContent = "Q. " + savedJoke.question;
        document.getElementById("answer").textContent = "A. " + savedJoke.answer;
    }
});

document.querySelector(".logo").addEventListener("click", () => location.href = "/");

// 요소 선택 및 함수 선언
const sidebar = document.getElementById("sidebar");
const openButton = document.querySelector(".menu-wrap img");
const closeButton = document.getElementById("close-button");

// 사이드바 열기 함수
const openSidebar = () => {
    sidebar.classList.remove('w-0');
    sidebar.classList.add('w-64'); // 사이드바 너비 변경
};

// 사이드바 닫기 함수
const closeSidebar = () => {
    sidebar.classList.remove('w-64');
    sidebar.classList.add('w-0');
};

// 열기 버튼 클릭 시 사이드바 열기
openButton.addEventListener("click", openSidebar);

// 닫기 버튼 클릭 시 사이드바 닫기
closeButton.addEventListener("click", closeSidebar);

// 페이지 외부 클릭 시 사이드바 닫기
document.addEventListener("click", (e) => {
    if (!sidebar.contains(e.target) && !openButton.contains(e.target)) {
        closeSidebar();
    }
});
