import {BidCard} from "./BidCard.js";

export function BidsList(_container, _orchestrator){
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;

    self.bidsContainerDiv = document.getElementById("bids-shown");
    self.listContainer = document.getElementById("list-bids");
    self.emptyContainer = document.getElementById("bids-empty");
    self.bidCards = [];

    self.show = function(auctionId){
        makeCall("GET", "GetBids?auctionID=" + auctionId, null,
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

    self.update = function(_bids){
        self.reset();
        self.container.style.display = "";

        if(_bids.length > 0){
            self.emptyContainer.style.display = "none";
            self.bidsContainerDiv.style.display = "";
        }else{
            self.emptyContainer.style.display = "";
            self.bidsContainerDiv.style.display = "none";
        }

        _bids.forEach((bid) => {
            let bidCard = new BidCard();
            self.bidCards.push(bidCard);

            self.listContainer.appendChild(bidCard.container);
            bidCard.show(bid);
        });


    }

    self.reset = function(){
        self.container.style.display = "none";
        self.bidCards.forEach((c) => {
            c.cleanup();
        });
        self.bidCards = [];
    }
}