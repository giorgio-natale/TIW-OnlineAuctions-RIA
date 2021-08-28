import {Orchestrator} from "./components/Orchestrator.js";

(function(){
    window.addEventListener("load", (e) => {
        let orchestrator = new Orchestrator();
        orchestrator.init();
    });
})();