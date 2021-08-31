//This component creates a card (root element is cardContainerDiv)
export function AuctionCard(_orchestrator) {
    let self = this;
    self.orchestrator = _orchestrator;

    //top level div
    self.cardContainerDiv = document.createElement("div");
    self.cardContainerDiv.className += "row px-5 py-2";

    this.createTemplate = function() {
        let cardDiv = document.createElement("div");
        cardDiv.className += "card";

        let cardBodyDiv = document.createElement("div");
        cardBodyDiv.className += "card-body";

        let titleDiv = document.createElement("div");
        titleDiv.className += "row p-0 justify-content-between";

        let innerTitleDiv = document.createElement("div");
        innerTitleDiv.className += "col-11";

        //title tag
        self.titleH4 = document.createElement("h4");
        self.titleH4.className += "col-11";

        let idDiv = document.createElement("div");
        idDiv.className += "col-1 p-0";

        //id tag
        self.idSpan = document.createElement("span");
        self.idSpan.className = "fw-lighter float-end";

        //description tag
        self.descriptionP = document.createElement("p");
        self.descriptionP.className += "card-text";

        let priceP = document.createElement("p");
        priceP.className += "card-text";

        //price label tag
        self.priceB = document.createElement("b");

        //price number tag
        self.priceSpan = document.createElement("span");

        self.euroSpan = document.createElement("span");
        self.euroSpan.innerHTML = " &euro;";

        self.statusP = document.createElement("p");
        self.statusP.className += "card-text";

        //end date or closed/expired label
        self.statusB = document.createElement("b");

        //status tag
        self.statusSpan = document.createElement("span");

        //end date tag
        self.endDateSpan = document.createElement("span");

        //time left tag
        self.timeLeftSpan = document.createElement("span");

        //link tag
        self.buttonA = document.createElement("a");
        self.buttonA.className += "btn btn-primary";

        //assembling tags
        self.cardContainerDiv.appendChild(cardDiv);
        cardDiv.appendChild(cardBodyDiv);
        cardBodyDiv.appendChild(titleDiv);
        titleDiv.appendChild(innerTitleDiv);
        innerTitleDiv.appendChild(self.titleH4);
        titleDiv.appendChild(idDiv);
        idDiv.appendChild(self.idSpan);
        cardBodyDiv.appendChild(self.descriptionP);
        cardBodyDiv.appendChild(priceP);
        priceP.appendChild(self.priceB);
        priceP.appendChild(self.priceSpan);
        priceP.appendChild(self.euroSpan);
        cardBodyDiv.appendChild(self.statusP);
        self.statusP.appendChild(self.statusB);
        self.statusP.appendChild(self.statusSpan);
        self.statusP.appendChild(self.endDateSpan);
        self.statusP.appendChild(self.timeLeftSpan);
        cardBodyDiv.appendChild(self.buttonA);
    }

    this.show = function(auction) {
        self.reset();
        self.createTemplate();
        self.update(auction);
        self.registerEvents(auction);
    }

    this.reset = function() {
        while (self.cardContainerDiv.firstChild) {
            self.cardContainerDiv.firstChild.remove();
        }
    }

    //There are some details to adjust (see TODOs).
    //The card knows what kind of message to show (e.g. 'Starting price' vs 'Current price') and the correct
    //interaction to trigger (e.g. Show Offers vs Show details) by looking at the auction bean passed by parameter.
    //Same considerations hold for the registerEvents function
    this.update = function(auction) {
        self.titleH4.textContent = auction.name;
        self.idSpan.textContent = "#" + auction.auction_id;
        self.descriptionP.innerHTML = auction.description;

        if(auction.closed || auction.expired) {
            self.priceB.textContent = "Final Price: ";
            if(auction.winning_price === 0) {
                self.priceSpan.textContent = "No bids were placed";
                self.euroSpan.style.display = "none";
            }else{
                self.priceSpan.textContent = getPriceFormat(auction.winning_price);
            }

            self.statusB.textContent = "Status: ";
            if(auction.closed)
                self.statusSpan.textContent = "CLOSED";
            else
                self.statusSpan.textContent = "EXPIRED";

            self.endDateSpan.style.display = "none";
            self.timeLeftSpan.style.display = "none";

            self.buttonA.textContent = "Show Details";

        } else {
            self.priceB.textContent = (auction.winning_price === 0) ? "Starting Price: " : "Current Price: ";
            self.priceSpan.textContent = (auction.winning_price === 0) ? getPriceFormat(auction.starting_price) : getPriceFormat(auction.winning_price);
            self.statusB.textContent = "End Date: ";
            self.endDateSpan.textContent = parseDate(auction.end_date);
            self.timeLeftSpan.textContent = " (" + getTimeLeft(auction.end_date, localStorage.getItem("last_login")) + " left)";

            self.statusSpan.style.display = "none";

            if(localStorage.getItem("user_id") === auction.user_id)
                self.buttonA.textContent = "Show Offers";
            else
                self.buttonA.textContent = "Make an offer";
        }

    }

    //see the comment for the update function
    this.registerEvents = function(auction) {
        self.buttonA.addEventListener("click", () => self.orchestrator.showDetailsAndBids(auction.auction_id));
    }

    //remove all the element
    this.cleanup = function() {
        self.cardContainerDiv.parentNode.removeChild(self.cardContainerDiv);
    }
}