document.getElementById("google-login").addEventListener("click", () => {
    location.href = "/oauth2/authorization/google";
})
document.getElementById("kakao-login").addEventListener("click", () => {
    location.href = "/oauth2/authorization/kakao";
})
document.getElementById("naver-login").addEventListener("click", () => {
    location.href = "/oauth2/authorization/naver";
})