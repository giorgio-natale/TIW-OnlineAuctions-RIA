import {AuctionCard} from "./AuctionCard.js";

const auction1 = {
    title: "Titolo di prova 1",
    id: 1,
    userId: 1,
    description: "Asta di prova",
    price: 34.5,
    endDate: "27/05/2022",
    startingPrice: 50,
    winningPrice: 100,
    closed: false
};
const auction2 = {
    title: "Titolo di prova 2",
    id: 2,
    userId: 2,
    description: "Asta di prova",
    price: 34.5,
    endDate: "27/05/2022",
    startingPrice: 70,
    winningPrice: 150,
    closed: true
};
const auction3 = {
    title: "Titolo di prova 3",
    id: 3,
    userId: 3,
    description: "Asta di prova",
    price: 34.5,
    endDate: "27/05/2022",
    startingPrice: 100,
    winningPrice: 0,
    closed: false
};

const mockAuctions = [auction1, auction2, auction3];

//This component takes care of the auctions found after the search interaction.
export function FoundAuctionsList(_container, _orchestrator){
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;
    self.listContainer = document.getElementById("list-found");
    self.emptyMessageContainer = document.getElementById("found-noResults");

    self.cards = [];

    //fetch all found auctions from database (use the 'searchString' parameter to make the ajax request)
    this.show = function(searchString){
        //TODO: use ajax instead
        setTimeout(() => {
            let auctionsToShow = mockAuctions;
            self.update(auctionsToShow);
        }, 10);
    }

    this.update = function(_auctionsToShow){
        self.reset();
        self.container.style.display = "";

        //if there is no auction found, a message is shown
        self.emptyMessageContainer.style.display = (_auctionsToShow.length > 0) ? "none" : "";

        //for each auction, a card is created. They are stored in a list so that on update it is possible to clean resources
        _auctionsToShow.forEach((auctionBean) => {
            let card = new AuctionCard(this.orchestrator);
            self.cards.push(card);

            self.listContainer.appendChild(card.cardContainerDiv);
            card.show(auctionBean);
        });
    }

    this.reset = function(){
        self.container.style.display = "none";
        self.cards.forEach((c) => {
            c.cleanup();
        })
        self.cards = []; //allow to garbage collector to free memory
    }

}