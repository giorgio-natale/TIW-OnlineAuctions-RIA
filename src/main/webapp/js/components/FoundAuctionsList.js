import {AuctionCard} from "./AuctionCard.js";

export function FoundAuctionsList(_container, _orchestrator){
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;
    self.listContainer = document.getElementById("list-found");
    self.emptyMessage = document.getElementById("found-noResults");
    self.recentMessage = document.getElementById("found-recent");

    self.cards = [];

    this.show = function(searchString) {
        makeCall("GET", "GetFoundAuctionsList?search=" + encodeURIComponent(searchString), null,
            function (request) {
                if (request.status === HttpResponseStatus.OK) {
                    self.update(JSON.parse(request.responseText), false);
                }
                else {
                    self.orchestrator.showAlertMessage(request.responseText);
                }
            }
        );
    }

    this.showHistory = function() {
        makeCall("GET", "GetAuctionsFromHistory", null,
            function (request) {
                if (request.status === HttpResponseStatus.OK) {
                    self.update(JSON.parse(request.responseText), true);
                }
                else {
                    self.orchestrator.showAlertMessage(request.responseText);
                }
            }
        );
    }

    this.update = function(_auctionsToShow, showHistory = true) {
        self.reset();
        self.container.style.display = "";

        if (showHistory === true)
            if(_auctionsToShow.length > 0) {
                self.emptyMessage.style.display = "none";
                self.recentMessage.style.display = "";
                self.listContainer.style.display = "";
            }
            else {
                self.emptyMessage.style.display = "none";
                self.recentMessage.style.display = "none";
                self.listContainer.style.display = "none";
            }
        else
            if(_auctionsToShow.length > 0) {
                self.emptyMessage.style.display = "none";
                self.recentMessage.style.display = "none";
                self.listContainer.style.display = "";
            }
            else {
                self.emptyMessage.style.display = "";
                self.recentMessage.style.display = "none";
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