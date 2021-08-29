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

const mockAuctions = [auction1];

export function OpenAuctionsList(_container, _orchestrator){
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;
    self.listContainer = document.getElementById("list-open");

    self.cards = [];

    this.show = function() {
        //TODO: use ajax instead
        setTimeout(() => {
            self.update(mockAuctions);
        }, 10);
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