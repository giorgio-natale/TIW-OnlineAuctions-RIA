export function NewAuctionForm(_container, _orchestrator) {
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;

    this.registerEvents = function() {

        function submitForm(event) {
            const form = document.getElementById("newAuction-form");

            event.preventDefault();
            if (form.checkValidity()) {
                makeCall("POST", 'AddAuction', new FormData(form),
                    function (request) {
                        if (request.status === HttpResponseStatus.OK) {
                            self.orchestrator.showSellPage();
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

        const formContainers = [
            "newAuction-auctionName",
            "newAuction-auctionStartingPrice",
            "newAuction-auctionPriceGap",
            "newAuction-auctionEndDate",
            "newAuction-auctionEndHour",
            "newAuction-auctionEndMinute"
        ];

        document.getElementById("button").addEventListener("click", (e) => submitForm(e));
        formContainers.forEach(element => {
            document.getElementById(element).addEventListener(
                "keypress",
                (e) => {
                    if(e.key === "Enter" && e.altKey === false &&  e.ctrlKey === false)
                        submitForm(e);
                }
            );
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