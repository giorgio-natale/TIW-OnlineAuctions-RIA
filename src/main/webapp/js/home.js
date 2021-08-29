import {Orchestrator} from "./components/Orchestrator.js";

(function(){
    window.addEventListener("load", () => {
        let orchestrator = new Orchestrator();
        orchestrator.init();
    });
})();