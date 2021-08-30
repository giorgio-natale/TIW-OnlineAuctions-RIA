import {NavBar} from "./NavBar.js";

import {SearchBar} from "./SearchBar.js";
import {FoundAuctionsList} from "./FoundAuctionsList.js";
import {WonAuctionsList} from "./WonAuctionsList.js";

import {NewAuctionForm} from "./NewAuctionForm.js";
import {OpenAuctionsList} from "./OpenAuctionsList.js";
import {ClosedAuctionsList} from "./ClosedAuctionsList.js";

import {AuctionDetails} from "./AuctionDetails.js";
import {WinnerDetails} from "./WinnerDetails.js";
// NewBidForm
// BidsList


//It creates all the components. The init function shows (for now) only the search form. This "class" contains a function
//for each possible interaction (almost equivalent to all the 'GoTo...' servlets of the pure html project).
//Each function is responsible to show and hide components in order to compose the correct layout.
//This component is passed to all other subcomponents so that they can invoke the correct interaction function when needed
export function Orchestrator() {
    this.navBar = new NavBar(document.getElementById("section-navbar"), this);

    this.searchBar = new SearchBar(document.getElementById("section-search"), this);
    this.foundAuctionsList = new FoundAuctionsList(document.getElementById("section-found"), this);
    this.wonAuctionsList = new WonAuctionsList(document.getElementById("section-won"), this);

    this.newAuctionForm = new NewAuctionForm(document.getElementById("section-newAuction"), this);
    this.openAuctionsList = new OpenAuctionsList(document.getElementById("section-open"), this);
    this.closedAuctionsList = new ClosedAuctionsList(document.getElementById("section-closed"), this);

    this.auctionDetails = new AuctionDetails(document.getElementById("section-auctionDetails"), this);
    this.winnerDetails = new WinnerDetails(document.getElementById("section-auctionWinner"), this);
    // this.newBidForm = new NewBidForm(document.getElementById("section-addBid"), this);
    // this.bidsList = new BidsList(document.getElementById("section-bids"), this);

    let self = this;

    this.init = function() {
        self.showBuyPage();
    }

    this.showSearchResults = function(searchString) {
        self.navBar.show();

        self.searchBar.show();
        self.foundAuctionsList.show(searchString);
        self.wonAuctionsList.show();

        self.newAuctionForm.reset();
        self.openAuctionsList.reset();
        self.closedAuctionsList.reset();

        self.auctionDetails.reset();
        self.winnerDetails.reset();
        // self.newBidForm.reset();
        // self.bidsList.reset();

        self.navBar.activateBuy();
    }

    this.showBuyPage = function() {
        self.navBar.show();

        self.searchBar.show();
        self.foundAuctionsList.reset();
        self.wonAuctionsList.show();

        self.newAuctionForm.reset();
        self.openAuctionsList.reset();
        self.closedAuctionsList.reset();

        self.auctionDetails.reset();
        self.winnerDetails.reset();
        // self.newBidForm.reset();
        // self.bidsList.reset();

        self.navBar.activateBuy();
    }

    this.showSellPage = function () {
        self.navBar.show();

        self.searchBar.reset();
        self.foundAuctionsList.reset();
        self.wonAuctionsList.reset();

        self.newAuctionForm.show();
        self.openAuctionsList.show();
        self.closedAuctionsList.show();

        self.auctionDetails.reset();
        self.winnerDetails.reset();
        // self.newBidForm.reset();
        // self.bidsList.reset();

        self.navBar.activateSell();
    }

    this.showDetailsAndBids = function(auctionID) {
        self.navBar.show();

        self.searchBar.reset();
        self.foundAuctionsList.reset();
        self.wonAuctionsList.reset();

        self.newAuctionForm.reset();
        self.openAuctionsList.reset();
        self.closedAuctionsList.reset();

        self.auctionDetails.show(auctionID);
        self.winnerDetails.reset();     // TODO: to be shown
        // self.newBidForm.show();
        // self.bidsList.show();

        self.navBar.deactivateAll();
    }
}