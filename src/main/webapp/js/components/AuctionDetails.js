export function AuctionDetails(_container, _orchestrator) {
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;
    self.auctionID = 0;

    self.image = document.getElementById("auctionDetails-image");
    self.name = document.getElementById("auctionDetails-name");
    self.ownerName = document.getElementById("auctionDetails-ownerName");
    self.description = document.getElementById("auctionDetails-description");
    self.startingPrice = document.getElementById("auctionDetails-startingPrice");
    self.priceLabel = document.getElementById("auctionDetails-priceLabel");
    self.price = document.getElementById("auctionDetails-price");
    self.minimumRebid = document.getElementById("auctionDetails-minimumRebid");
    self.endDate = document.getElementById("auctionDetails-endDate");
    self.timeLeft = document.getElementById("auctionDetails-timeLeft");
    self.closeForm = document.getElementById("auctionDetails-closeForm");
    self.closeDiv = document.getElementById("auctionDetails-closeDiv");
    self.offersTitle = document.getElementById("auctionDetails-offersTitle");

    this.show = function (auctionDetails, lightOwnerDetails) {
        self.auctionID = auctionDetails.auction_id;
        self.update(auctionDetails, lightOwnerDetails);
        self.container.style.display = "";
    }

    this.update = function(auctionDetails, lightOwnerDetails) {
        self.image.src = "GetImage?auctionId=" + auctionDetails.auction_id;

        self.name.textContent = auctionDetails.name;
        self.description.textContent = auctionDetails.description;

        self.ownerName.textContent = lightOwnerDetails.first_name + " " + lightOwnerDetails.last_name;

        if(String(auctionDetails.user_id) === localStorage.getItem("user_id")) {
            self.ownerName.classList.remove("bg-primary");
            self.ownerName.classList.add("bg-secondary");
            self.ownerName.textContent += " (You)";
        }
        else {
            self.ownerName.classList.remove("bg-secondary");
            self.ownerName.classList.add("bg-primary");
        }

        self.startingPrice.innerHTML = getPriceFormat(auctionDetails.starting_price);
        self.minimumRebid.textContent = getPriceFormat(auctionDetails.min_price_gap);

        if (auctionDetails.expired === true || auctionDetails.closed === true || Date.parse(auctionDetails.end_date) < Date.parse(localStorage.getItem("last_login"))) {
            self.priceLabel.textContent = "Final price:";
            self.priceLabel.style.display = "";
            self.price.style.display = "";
        } else if (auctionDetails.winning_price !== 0) {
            self.priceLabel.textContent = "Current price:";
            self.priceLabel.style.display = "";
            self.price.style.display = "";
        } else {
            self.priceLabel.style.display = "none";
            self.price.style.display = "none";
        }

        if(auctionDetails.winning_price === 0)
            if(auctionDetails.expired === true || auctionDetails.closed === true)
                self.price.innerHTML = "<span>No bids were placed</span>";
            else
                self.price.innerHTML = getPriceFormat(auctionDetails.starting_price) + " &euro;";
        else
            self.price.innerHTML = getPriceFormat(auctionDetails.winning_price) + " &euro;";

        self.endDate.textContent = parseDate(auctionDetails.end_date);

        if(auctionDetails.closed === true)
            self.timeLeft.textContent = "(CLOSED)";
        else if(auctionDetails.expired === true)
            self.timeLeft.textContent = "(EXPIRED)";
        else
            self.timeLeft.textContent = "(" + getTimeLeft(auctionDetails.end_date, localStorage.getItem("last_login")) + " left)";

        if(auctionDetails.closed === true || auctionDetails.expired === true)
            self.offersTitle.style.display = "none";
        else
            self.offersTitle.style.display = "";

        if(auctionDetails.closed === false && auctionDetails.expired === true && String(auctionDetails.user_id) === localStorage.getItem("user_id"))
            self.closeDiv.style.display = "";
        else
            self.closeDiv.style.display = "none";
    }

    this.reset = function() {
        self.container.style.display = "none";
    }

    function submitForm() {
        if (self.closeForm.checkValidity()) {

            let finalForm = new FormData(self.closeForm);
            finalForm.append("auctionID", self.auctionID);

            makeCall("POST", 'CloseAuction', finalForm,
                function (request) {
                    if (request.status === HttpResponseStatus.OK) {
                        self.orchestrator.showDetailsAndBids(self.auctionID);
                        self.closeForm.reset();
                    }
                    else {
                        self.orchestrator.showAlertMessage(request.responseText);
                    }
                },
                true
            );
        }
        else {
            self.closeForm.reportValidity();
        }
    }

    this.registerEvents = function() {
        self.closeForm.addEventListener("submit", (event) => {
            event.preventDefault();
            submitForm();
        });
    }

    this.registerEvents();
}