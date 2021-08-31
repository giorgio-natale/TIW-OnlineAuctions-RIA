export function NewAuctionForm(_container, _orchestrator) {
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;

    self.form = document.getElementById("newAuction-form");

    function submitForm() {
        if (self.form.checkValidity()) {
            makeCall("POST", 'AddAuction', new FormData(self.form),
                function (request) {
                    if (request.status === HttpResponseStatus.OK) {
                        self.orchestrator.showSellPage();
                        self.form.reset();
                    }
                    else {
                        // TODO: error handling
                        alert("Error " + request.status + ": " + request.responseText);
                    }
                },
                true
            );
        }
        else {
            self.form.reportValidity();
        }
    }

    this.registerEvents = function() {
        self.form.addEventListener("submit", (e) => {
            e.preventDefault();
            submitForm();
        });
    }

    this.reset = function(){
        self.container.style.display = "none";
    }

    this.show = function(){
        self.container.style.display = "";
    }

    this.registerEvents();
}