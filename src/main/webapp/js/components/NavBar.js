export function NavBar(_container, _orchestrator) {
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;
    self.buyButton = document.getElementById("navbar-buy-link");
    self.sellButton = document.getElementById("navbar-sell-link");
    self.logoutButton = document.getElementById("navbar-logout-link")
    self.nameLabel = document.getElementById("navbar-name-text");

    this.registerEvents = function() {
        self.buyButton.addEventListener("click", (event) => {
            event.preventDefault();
            self.orchestrator.showBuyPage();
        });

        self.sellButton.addEventListener("click", (event) => {
            event.preventDefault();
            self.orchestrator.showSellPage();
        });

        self.logoutButton.addEventListener("click", (event) => {
            event.preventDefault();
            makeCall("GET", "Logout", null, () => {});
        });
    }

    this.reset = function() {
        self.container.style.display = "none";
    }

    this.show = function() {
        self.container.style.display = "";

        self.nameLabel.textContent = localStorage.getItem("first_name") + " " + localStorage.getItem("last_name");
    }

    this.activateBuy = function () {
        self.buyButton.classList.add("active");
        self.sellButton.classList.remove("active");
    }

    this.activateSell = function () {
        self.buyButton.classList.remove("active");
        self.sellButton.classList.add("active");
    }

    this.deactivateAll = function () {
        self.buyButton.classList.remove("active");
        self.sellButton.classList.remove("active");
    }

    this.registerEvents();
}