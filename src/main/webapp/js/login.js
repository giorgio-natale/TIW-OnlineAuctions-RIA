(function () {

    window.addEventListener("load", () =>
        makeCall("GET", "CheckAlreadyLoggedIn", null,
            function (request) {
                if (request.status === HttpResponseStatus.OK)
                    window.location.href = "home.html";
            },
            false
        )
    );

    function checkLogin() {
        const form = document.getElementById("form");

        if (form.checkValidity()) {
            makeCall("POST", 'CheckLogin', form,

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