(function () {

    let form = document.getElementById("form");

    window.addEventListener("load", () => {
            makeCall("GET", "CheckAlreadyLoggedIn", null,
                function (request) {
                    if (request.status === HttpResponseStatus.OK)
                        window.location.href = "home.html";
                },
                false
            );

            form.addEventListener("submit", (e) => {
                e.preventDefault();
                form.classList.add("was-validated");

                if (!self.form.checkValidity()) {
                    e.stopPropagation();
                }
                else {
                    checkLogin();
                    form.classList.remove("was-validated");
                }
            },
                false
            );
        }
    );

    function checkLogin() {

        if (form.checkValidity()) {
            makeCall("POST", 'CheckLogin', new FormData(form),

                function (request) {
                        const message = request.responseText;

                        switch (request.status) {
                            case HttpResponseStatus.OK:

                                let lightUser = JSON.parse(message);

                                console.log(message);

                                for (const [key, value] of Object.entries(lightUser)) {
                                    localStorage.setItem(key, String(value));
                                }

                                window.location.href = "home.html";
                                document.getElementById("errorMsg").style.visibility = "hidden";
                                break;

                            case HttpResponseStatus.BAD_REQUEST:
                            case HttpResponseStatus.UNAUTHORIZED:
                            case HttpResponseStatus.INTERNAL_SERVER_ERROR:
                            case HttpResponseStatus.BAD_GATEWAY:
                            default:
                                localStorage.clear();
                                document.getElementById("errorMsg").textContent = message;
                                document.getElementById("errorMsg").style.visibility = "visible";
                                break;
                    }
                },
                false
            );
        }
        else {
            form.reportValidity();
        }
    }

})();