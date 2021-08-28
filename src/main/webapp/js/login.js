(function () {

    function checkLogin() {
        const form = document.getElementById("form");

        if (form.checkValidity()) {
            makeCall("POST", 'CheckLogin', form,

                function (request) {
                        const message = request.responseText;

                        switch (request.status) {
                            case HttpResponseStatus.OK:
                                sessionStorage.setItem('username', message);
                                window.location.href = "home.html";
                                document.getElementById("errorMsg").style.visibility = "hidden";
                                break;

                            case HttpResponseStatus.BAD_REQUEST:
                            case HttpResponseStatus.UNAUTHORIZED:
                            case HttpResponseStatus.INTERNAL_SERVER_ERROR:
                            case HttpResponseStatus.BAD_GATEWAY:
                            default:
                                document.getElementById("errorMsg").textContent = message;
                                document.getElementById("errorMsg").style.visibility = "visible";
                                break;
                    }
                }
            );
        }
        else {
            form.reportValidity();
        }
    }

    document.getElementById("button").addEventListener("click", () => checkLogin());
    document.addEventListener("keypress", (e) => {
        if(e.key === "Enter" && e.altKey === false &&  e.ctrlKey === false)
            checkLogin();
    });

})();