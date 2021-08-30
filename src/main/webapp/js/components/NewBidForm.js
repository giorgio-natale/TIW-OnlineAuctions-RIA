export function NewBidForm(_container, _orchestrator) {
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;

    this.registerEvents = function(auctionID) {

        function submitForm(event) {
            const form = document.getElementById("addBid-form");

            event.preventDefault();
            if (form.checkValidity()) {

                let finalForm = new FormData(form);
                finalForm.append("addBid-auctionId", auctionID);

                makeCall("POST", 'AddBid', finalForm,
                    function (request) {
                        if (request.status === HttpResponseStatus.OK) {
                            self.orchestrator.showDetailsAndBids(auctionID);
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
                form.reportValidity();
            }
        }

        document.getElementById("addBid-button").addEventListener(
            "click",
            (e) => submitForm(e)
        );
        document.addEventListener(
            "keypress",
            (e) => {
                if(e.key === "Enter" && e.altKey === false &&  e.ctrlKey === false)
                    submitForm(e);
            }
        );

    }

    this.reset = function(){
        self.container.style.display = "none";
    }

    this.show = function(auctionID){
        this.registerEvents(auctionID);
        self.container.style.display = "";
    }

}