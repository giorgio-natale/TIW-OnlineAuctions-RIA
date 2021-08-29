import {FoundAuctionsList} from "./FoundAuctionsList.js";
import {AuctionDetails} from "./AuctionDetails.js";
import {SearchBar} from "./SearchBar.js"


//It creates all the components. The init function shows (for now) only the search form. This "class" contains a function
//for each possible interaction (almost equivalent to all the 'GoTo...' servlets of the pure html project).
//Each function is responsible to show and hide components in order to compose the correct layout.
//This component is passed to all other subcomponents so that they can invoke the correct interaction function when needed
export function Orchestrator(){
    this.foundAuctionsList = new FoundAuctionsList(document.getElementById("section-found"), this);
    this.auctionDetails = new AuctionDetails(document.getElementById("section-details"));
    this.searchBar = new SearchBar(document.getElementById("section-search"), this);

    let self = this;

    this.init = function(){
        self.auctionDetails.reset();
        self.foundAuctionsList.reset();
        self.searchBar.show();
    }

    this.showSearchResults = function(searchString){
        self.auctionDetails.reset();
        self.foundAuctionsList.show(searchString);
        self.searchBar.show();
    }

    this.showDetails = function(_details){
        self.auctionDetails.show(_details);
        self.foundAuctionsList.reset();
        self.searchBar.reset();
    }

    this.showOffers = function(_id){
        //TODO: implement
        alert("Showing the offers for auction #" + _id);
    }

    this.makeOffer = function(_id){
        //TODO: implement
        alert("Showing the layout to make an offer for auction #" + _id);
    }
}