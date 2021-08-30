import {Orchestrator} from "./components/Orchestrator.js";

(function() {
    window.addEventListener("load", () =>
        makeCall("GET", "CheckAlreadyLoggedIn", null,
            function () {
                let orchestrator = new Orchestrator();
                orchestrator.init();
            }
        )
    );
})();