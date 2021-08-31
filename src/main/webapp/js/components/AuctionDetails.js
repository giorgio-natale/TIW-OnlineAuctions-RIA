//This is just a mock component that prints the id of the auction
export function AuctionDetails(_container, _orchestrator){
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;

    self.image = document.getElementById("auctionDetails-image");
    self.name = document.getElementById("auctionDetails-name");
    self.ownerName = document.getElementById("auctionDetails-ownerName");
    self.descrption = document.getElementById("auctionDetails-description");
    self.priceLabel = document.getElementById("auctionDetails-priceLabel");
    self.price = document.getElementById("auctionDetails-price");
    self.minimumRebid = document.getElementById("auctionDetails-minimumRebid");
    self.endDate = document.getElementById("auctionDetails-endDate");
    self.timeLeft = document.getElementById("auctionDetails-timeLeft");
    self.closeForm = document.getElementById("auctionDetails-closeForm");
    self.offersTitle = document.getElementById("auctionDetails-offersTitle");

    this.show = function (auctionID) {
        makeCall("GET", "GetAuctionDetails?auctionID=" + auctionID, null,
            function (request) {
                if (request.status === HttpResponseStatus.OK) {
                    let pair = JSON.parse(request.responseText)

                    self.update(pair.auction, pair.owner)
                }
                else {
                    // TODO: error handling
                    alert("Error " + request.status + ": " + request.responseText);
                }
            }
        );

        self.container.style.display = "";
    }

    this.update = function(auctionDetails, lightOwnerDetails) {
        self.image.src = "GetImage?auctionId=" + auctionDetails.auction_id;

        self.name.textContent = auctionDetails.name;
        self.descrption.textContent = auctionDetails.description;

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

        if(auctionDetails.expired === true || auctionDetails.closed === true || auctionDetails.end_date < localStorage.getItem("last_login"))
            self.priceLabel.textContent = "Final price:";
        else
            self.priceLabel.textContent = "Current price:";

        if(auctionDetails.winning_price === 0)
            if(auctionDetails.expired === true || auctionDetails.closed === true)
                self.price.innerHTML = getPriceFormat(auctionDetails.starting_price) + " &euro; <span class='fw-bold'>(No bids were placed)</span>";
            else
                self.price.innerHTML = getPriceFormat(auctionDetails.starting_price) + " &euro;";
        else
            self.price.innerHTML = getPriceFormat(auctionDetails.winning_price) + " &euro;";

        self.minimumRebid.textContent = getPriceFormat(auctionDetails.min_price_gap);
        self.endDate.textContent = secondsToDate(auctionDetails.end_date);

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
    }

    this.reset = function() {
        self.container.style.display = "none";
    }
}