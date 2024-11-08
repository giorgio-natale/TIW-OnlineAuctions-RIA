import {NavBar} from "./NavBar.js";
import {AlertMessage} from "./AlertMessage.js";

import {SearchBar} from "./SearchBar.js";
import {FoundAuctionsList} from "./FoundAuctionsList.js";
import {WonAuctionsList} from "./WonAuctionsList.js";

import {NewAuctionForm} from "./NewAuctionForm.js";
import {OpenAuctionsList} from "./OpenAuctionsList.js";
import {ClosedAuctionsList} from "./ClosedAuctionsList.js";

import {AuctionPage} from "./AuctionPage.js";


//It creates all the components. The init function shows (for now) only the search form. This "class" contains a function
//for each possible interaction (almost equivalent to all the 'GoTo...' servlets of the pure html project).
//Each function is responsible to show and hide components in order to compose the correct layout.
//This component is passed to all other subcomponents so that they can invoke the correct interaction function when needed
export function Orchestrator() {
    this.navBar = new NavBar(document.getElementById("section-navbar"), this);
    this.alertMessage = new AlertMessage(document.getElementById('section-alert'), this);

    this.searchBar = new SearchBar(document.getElementById("section-search"), this);
    this.foundAuctionsList = new FoundAuctionsList(document.getElementById("section-found"), this);
    this.wonAuctionsList = new WonAuctionsList(document.getElementById("section-won"), this);

    this.newAuctionForm = new NewAuctionForm(document.getElementById("section-newAuction"), this);
    this.openAuctionsList = new OpenAuctionsList(document.getElementById("section-open"), this);
    this.closedAuctionsList = new ClosedAuctionsList(document.getElementById("section-closed"), this);

    this.auctionPage = new AuctionPage(this);

    let self = this;

    this.init = function() {
        if(localStorage.getItem("new_user") === String(true))
            self.showBuyPage(true);
        else if(getCookie("lastAction." + localStorage.getItem("user_id")) === "create")
            self.showSellPage();
        else
            self.showBuyPage(true);
    }

    this.showSearchResults = function(searchString) {
        self.navBar.show();

        self.searchBar.show();
        self.foundAuctionsList.show(searchString);
        self.wonAuctionsList.show();

        self.newAuctionForm.reset();
        self.openAuctionsList.reset();
        self.closedAuctionsList.reset();

        self.auctionPage.reset();

        self.navBar.activateBuy();
    }

    this.showBuyPage = function(showHistory = true) {
        self.navBar.show();

        self.searchBar.show();

        if(showHistory === true)
            self.foundAuctionsList.showHistory();
        else
            self.foundAuctionsList.reset();

        self.wonAuctionsList.show();

        self.newAuctionForm.reset();
        self.openAuctionsList.reset();
        self.closedAuctionsList.reset();

        self.auctionPage.reset();

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

        self.auctionPage.reset();

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

        self.auctionPage.show(auctionID);

        self.navBar.deactivateAll();
    }

    this.showAlertMessage = function (message) {
        self.alertMessage.show(message);
    }
}