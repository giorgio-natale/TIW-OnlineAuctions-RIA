import {AuctionCard} from "./AuctionCard.js";

export function ClosedAuctionsList(_container, _orchestrator) {
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;
    self.listContainer = document.getElementById("list-closed");

    self.cards = [];

    this.show = function() {
        makeCall("GET", "GetAuctionsList?type=closed", null,
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

        if(_auctionsToShow.length > 0)
            self.container.style.display = "";
        else
            self.container.style.display = "none";

        _auctionsToShow.forEach((auctionBean) => {
            let card = new AuctionCard(this.orchestrator);
            self.cards.push(card);

            self.listContainer.appendChild(card.cardContainerDiv);
            card.show(auctionBean);
        });
    }

    this.reset = function() {
        self.container.style.display = "none";
        self.cards.forEach((card) => {
            card.cleanup();
        })

        self.cards = [];
    }
}