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
        // TODO: get real image
        self.image.src = "C:/TIW_images/default.png";

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
                self.price.innerHTML = auctionDetails.starting_price + "&euro; (No bids were placed)";
            else
                self.price.innerHTML = auctionDetails.starting_price + "&euro;";
        else
            self.price.innerHTML = auctionDetails.winning_price + "&euro;";

        self.minimumRebid.textContent = auctionDetails.min_price_gap;
        self.endDate.textContent = secondsToDate(auctionDetails.end_date);
    }

    this.reset = function() {
        self.container.style.display = "none";
    }
}