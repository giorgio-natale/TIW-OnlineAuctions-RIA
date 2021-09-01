import {AuctionDetails} from "./AuctionDetails.js";
import {WinnerDetails} from "./WinnerDetails.js";
import {NewBidForm} from "./NewBidForm.js";
import {BidsList} from "./BidsList.js";

export function AuctionPage(_orchestrator) {
    let self = this;

    self.orchestrator = _orchestrator;

    self.auctionDetails = new AuctionDetails(document.getElementById("section-auctionDetails"), self.orchestrator);
    self.winnerDetails = new WinnerDetails(document.getElementById("section-auctionWinner"), self.orchestrator);
    self.newBidForm = new NewBidForm(document.getElementById("section-addBid"), self.orchestrator);
    self.bidsList = new BidsList(document.getElementById("section-bids"), self.orchestrator);

    this.show = function (auctionID) {
        makeCall("GET", "GetAuctionDetails?auctionID=" + auctionID, null,
            function (request) {
                if (request.status === HttpResponseStatus.OK) {
                    let pair = JSON.parse(request.responseText)
                    self.update(pair.auction, pair.owner);
                }
                else {
                    self.orchestrator.showAlertMessage(request.responseText);
                }
            }
        );
    }

    this.update = function(auctionDetails, lightOwnerDetails) {
        self.auctionDetails.show(auctionDetails, lightOwnerDetails);

        if(auctionDetails.expired === true || auctionDetails.closed === true) {
            self.winnerDetails.show(auctionDetails.auction_id);
            self.newBidForm.reset();
            self.bidsList.reset();
        }
        else {
            self.winnerDetails.reset();
            self.bidsList.show(auctionDetails.auction_id);

            if(String(auctionDetails.user_id) === localStorage.getItem("user_id"))
                self.newBidForm.reset();
            else
                self.newBidForm.show(auctionDetails.auction_id);
        }
    }

    this.reset = function() {
        self.auctionDetails.reset();
        self.winnerDetails.reset();
        self.newBidForm.reset();
        self.bidsList.reset();
    }
}