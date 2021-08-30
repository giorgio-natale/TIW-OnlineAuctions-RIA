import {AuctionCard} from "./AuctionCard.js";

//This component takes care of the auctions found after the search interaction.
export function FoundAuctionsList(_container, _orchestrator){
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;
    self.listContainer = document.getElementById("list-found");
    self.emptyMessageContainer = document.getElementById("found-noResults");

    self.cards = [];

    this.show = function(searchString) {
        makeCall("GET", "GetFoundAuctionsList?search=" + searchString, null,
            function (request) {
                if (request.status === HttpResponseStatus.OK) {
                    self.update(JSON.parse(request.responseText));
                }
                else {
                    // TODO: error handling
                    alert("Error " + request.status + ": " + request.responseText);
                }
            }
        );
    }

    this.update = function(_auctionsToShow) {
        self.reset();
        self.container.style.display = "";

        //if there is no auction found, a message is shown
        if(_auctionsToShow.length > 0) {
            self.emptyMessageContainer.style.display = "none";
            self.listContainer.style.display = "";
        }
        else {
            self.emptyMessageContainer.style.display = "";
            self.listContainer.style.display = "none";
        }

        //for each auction, a card is created. They are stored in a list so that on update it is possible to clean resources
        _auctionsToShow.forEach((auctionBean) => {
            let card = new AuctionCard(this.orchestrator);
            self.cards.push(card);

            self.listContainer.appendChild(card.cardContainerDiv);
            card.show(auctionBean);
        });
    }

    this.reset = function() {
        self.container.style.display = "none";
        self.cards.forEach((c) => {
            c.cleanup();
        })
        self.cards = []; //allow to garbage collector to free memory
    }

}