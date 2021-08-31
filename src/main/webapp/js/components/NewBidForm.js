export function NewBidForm(_container, _orchestrator) {
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;

    self.auctionID = 0;
    self.form = document.getElementById("addBid-form");

    function submitForm() {
        if (self.form.checkValidity()) {

            let finalForm = new FormData(self.form);
            finalForm.append("addBid-auctionId", self.auctionID);

            makeCall("POST", 'AddBid', finalForm,
                function (request) {
                    if (request.status === HttpResponseStatus.OK) {
                        self.orchestrator.showDetailsAndBids(self.auctionID);
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
        self.form.addEventListener("submit",(e) => {
            e.preventDefault();
            submitForm();
        });
    }

    this.reset = function() {
        self.container.style.display = "none";
        self.form.reset();
    }

    this.show = function(_auctionID){
        self.auctionID = _auctionID;
        self.container.style.display = "";
    }

    self.registerEvents();
}