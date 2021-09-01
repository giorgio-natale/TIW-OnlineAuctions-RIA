export function NewAuctionForm(_container, _orchestrator) {
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;

    self.form = document.getElementById("newAuction-form");

    function submitForm() {
        makeCall("POST", 'AddAuction', new FormData(self.form),
            function (request) {
                if (request.status === HttpResponseStatus.OK) {
                    self.orchestrator.showSellPage();
                    self.form.reset();
                }
                else {
                    self.orchestrator.showAlertMessage(request.responseText);
                }
            },
            true
        );
    }

    this.registerEvents = function() {
        self.form.addEventListener("submit", function (event) {
            event.preventDefault();

            self.form.classList.add("was-validated");

            if (!self.form.checkValidity()) {
                event.stopPropagation();
            }
            else {
                submitForm();
                self.form.classList.remove("was-validated");
            }
        },
        false);

        let dateInput = document.getElementById("newAuction-auctionEndDate");
        dateInput.min = new Date().addDays(1).toISOString().slice(0, 10);
        dateInput.max = new Date().addDays(366).toISOString().slice(0, 10);
    }

    this.reset = function(){
        self.container.style.display = "none";
    }

    this.show = function(){
        self.container.style.display = "";
    }

    this.registerEvents();
}