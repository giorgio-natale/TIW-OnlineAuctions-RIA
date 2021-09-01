export function AlertMessage(_container, _orchestrator) {
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;

    self.message = document.getElementById("alert-message");
    self.button = document.getElementById("alert-closeBtn");

    self.show = function (message) {
        self.message.textContent = message;
        self.container.style.display = "";
    }

    self.reset = function () {
        self.message.textContent = "";
        self.container.style.display = "none";
    }

    self.registerEvents = function() {
        self.button.addEventListener("click", () => self.reset());
    }

    self.registerEvents();
    self.reset();
}