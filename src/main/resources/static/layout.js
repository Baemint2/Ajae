let userInfoFetched = false;

const layout = {
    init: function () {
        this.setupEvent();
        loginCheck();
    },

    setupEvent: function () {

        document.querySelector(".logo").addEventListener("click", () => location.href = "/");

        // 요소 선택 및 함수 선언
        const sidebar = document.getElementById("sidebar");
        const openButton = document.querySelector(".menu-wrap img");
        const closeButton = document.getElementById("close-button");

        // 사이드바 열기 함수
        const openSidebar = () => {
            if (!userInfoFetched) {
                getUserInfo();
                userInfoFetched = true;
            }

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
    }
}

const getUserInfo = () => {
    fetch("/api/v1/userInfo", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    }).then(response => {
        if (!response.ok) return;
        else return response.json();
    })
    .then(data => {
        if (!data) return;

        const userWrap = document.querySelector(".user-wrap")

        const img = document.createElement("img");
        img.src = data.profile;
        img.alt = "User Profile";
        img.className = "user-img";

        userWrap.appendChild(img);
    })
    .catch(error => {

    })
}

const loginCheck = () => {
    const login = document.getElementById("login");
    const logout = document.getElementById("logout");
    fetch("/api/v1/loginCheck", {
        method: "GET",
    }).then(response => response.json())
        .then(data => {
            console.log("로그인체크");
            if(data === true) {
                login.style.display = "none";
                logout.style.display = "block";
            } else {
                logout.style.display = "none";
                login.style.display = "block";
            }
        })
}

document.addEventListener("DOMContentLoaded", () => {
    layout.init();
});
