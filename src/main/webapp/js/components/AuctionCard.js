//TODO: delete this (it will be retrieved from the session)
let mockUserId = 1;


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

        let euroSpan = document.createElement("span");
        euroSpan.innerHTML = "&euro;";

        let statusP = document.createElement("p");
        statusP.className += "card-text";

        let endDateB = document.createElement("b");
        endDateB.innerHTML = "End Date: ";

        //end date tag
        self.endDateSpan = document.createElement("span");

        //link tag
        self.buttonA = document.createElement("a");
        self.buttonA.className += "btn btn-primary";

        //assembling tags
        self.cardContainerDiv.appendChild(cardDiv);
        cardDiv.appendChild(cardBodyDiv);
        cardBodyDiv.appendChild(titleDiv);
        titleDiv.appendChild(innerTitleDiv);
        innerTitleDiv.appendChild(this.titleH4);
        titleDiv.appendChild(idDiv);
        idDiv.appendChild(this.idSpan);
        cardBodyDiv.appendChild(this.descriptionP);
        cardBodyDiv.appendChild(priceP);
        priceP.appendChild(this.priceB);
        priceP.appendChild(this.priceSpan);
        priceP.appendChild(euroSpan);
        cardBodyDiv.appendChild(statusP);
        statusP.appendChild(endDateB);
        statusP.appendChild(this.endDateSpan);
        cardBodyDiv.appendChild(this.buttonA);
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
        self.descriptionP.textContent = auction.description;
        self.priceB.textContent = (auction.winning_price === 0) ? "Starting Price: " : "Current Price: ";
        self.priceSpan.textContent = (auction.winning_price === 0) ? auction.starting_price : auction.winning_price;
        self.endDateSpan.textContent = secondsToDate(auction.end_date);

        //TODO: add remaining time if open


        if(auction.closed) //TODO: add the expired case (auction not closed but expired)
            self.buttonA.textContent = "Show Details";
        else {
            if(mockUserId === auction.userId) //TODO: if the userId is not defined (local session expired), load the login page
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
        self.cardContainerDiv.parentNode.removeChild(this.cardContainerDiv);
    }
}