let userInfoFetched = false;

const layout = {
    init: function () {
        this.setupEvent();
    },

    setupEvent: function () {
        const openAnswer = document.getElementById("openAnswer");
        openAnswer.addEventListener("click", () => {
            document.getElementById("answer").style.display = "block";
            openAnswer.style.display = "none";

        });

        document.querySelector(".logo").addEventListener("click", () => location.href = "/");

        // 요소 선택 및 함수 선언
        const sidebar = document.getElementById("sidebar");
        const openButton = document.querySelector(".menu-wrap img");
        const closeButton = document.getElementById("close-button");

        // 사이드바 열기 함수
        const openSidebar = () => {
            if(!userInfoFetched) {
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
    }).then(response => response.json())
        .then(data => {
            const userWrap = document.querySelector(".user-wrap")

            const img = document.createElement("img");
            img.src = data.profile;
            img.alt = "User Profile";
            img.className = "user-img";

            userWrap.appendChild(img);
        })
}

document.addEventListener("DOMContentLoaded", () => {
    layout.init();
});
