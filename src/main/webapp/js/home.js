(function(){
    const auction1 = {
        title: "Titolo di prova 1",
        id: 1,
        description: "Asta di prova",
        price: 34.5,
        endDate: "27/05/2022"
    };
    const auction2 = {
        title: "Titolo di prova 2",
        id: 2,
        description: "Asta di prova",
        price: 34.5,
        endDate: "27/05/2022"
    };
    const auction3 = {
        title: "Titolo di prova 3",
        id: 3,
        description: "Asta di prova",
        price: 34.5,
        endDate: "27/05/2022"
    };

    const mockAuctions = [auction1, auction2, auction3];


    window.addEventListener("load", (e) => {
        let orchestrator = new Orchestrator();
        orchestrator.showSearchResults();
    });


    function FoundAuctionsList(_container, _orchestrator){
        this.container = _container;
        this.orchestrator = _orchestrator;

        //fetch all found auctions from database (maybe the parameter of this function will be the search string)
        this.show = function(){
            //TODO: use ajax instead
            setTimeout(() => {
                let auctionsToShow = mockAuctions;
                this.update(auctionsToShow);
            }, 10);
        }

        this.update = function(_auctionsToShow){
            this.container.style.visibility = "visible";
            this.container.innerHTML = "";
            _auctionsToShow.forEach((auction) => {
                let card = new AuctionCard();
                card.update(auction);
                card.registerEvents(this.orchestrator);
                this.container.appendChild(card.cardContainerDiv);
            });
        }

        this.reset = function(){
            this.container.style.visibility = "hidden";
            this.container.innerHTML = "";
        }

    }




    function AuctionCard(){

        this.createTemplate = function(){
            //top level div
            this.cardContainerDiv = document.createElement("div");
            this.cardContainerDiv.className += "row px-5 py-2";

            let cardDiv = document.createElement("div");
            cardDiv.className += "card";

            let cardBodyDiv = document.createElement("div");
            cardBodyDiv.className += "card-body";

            let titleDiv = document.createElement("div");
            titleDiv.className += "row p-0 justify-content-between";

            let innerTitleDiv = document.createElement("div");
            innerTitleDiv.className += "col-11";

            //title tag
            this.titleH4 = document.createElement("h4");
            this.titleH4.className += "col-11";

            let idDiv = document.createElement("div");
            idDiv.className += "col-1 p-0";

            //id tag
            this.idSpan = document.createElement("span");
            this.idSpan.className = "fw-lighter float-end";

            //description tag
            this.descriptionP = document.createElement("p");
            this.descriptionP.className += "card-text";

            let priceP = document.createElement("p");
            priceP.className += "card-text";

            //price label tag
            this.priceB = document.createElement("b");

            //price number tag
            this.priceSpan = document.createElement("span");

            let euroSpan = document.createElement("span");
            euroSpan.innerHTML = "&euro;";

            let statusP = document.createElement("p");
            statusP.className += "card-text";

            let endDateB = document.createElement("b");
            endDateB.innerHTML = "End Date: ";

            //end date tag
            this.endDateSpan = document.createElement("span");

            //link tag
            this.buttonA = document.createElement("a");
            this.buttonA.className += "btn btn-primary";

            //assembling tags
            this.cardContainerDiv.appendChild(cardDiv);
            cardDiv.appendChild(cardBodyDiv);
            cardBodyDiv.appendChild(titleDiv);
            titleDiv.appendChild(innerTitleDiv);
            innerTitleDiv.appendChild(this.titleH4);
            titleDiv.appendChild(idDiv);
            idDiv.appendChild(this.idSpan);
            cardBodyDiv.appendChild(this.descriptionP);
            cardBodyDiv.appendChild(priceP);
            priceP.appendChild(this.priceB);
            priceP.appendChild(this.priceSpan);
            priceP.appendChild(euroSpan);
            cardBodyDiv.appendChild(statusP);
            statusP.appendChild(endDateB);
            statusP.appendChild(this.endDateSpan);
            cardBodyDiv.appendChild(this.buttonA);
        }

        this.reset = function(){
            this.cardContainerDiv.style.visibility = "hidden";
        }

        this.update = function(auction){
            this.titleH4.textContent = auction.title;
            this.idSpan.textContent = auction.id;
            this.descriptionP.textContent = auction.description;
            this.priceB.textContent = "Starting Price: ";
            this.priceSpan.textContent = auction.price;
            this.endDateSpan.textContent = auction.endDate;
            this.buttonA.textContent = "Show";
        }

        this.registerEvents = function(orchestrator){
            this.buttonA.addEventListener("click", (evt) => {
                orchestrator.showDetails(this.idSpan.textContent);
            });
        }


        this.createTemplate();
    }


    function AuctionDetails(_container){
        this.container = _container;

        this.show = function (_id){
            //TODO: use ajax instead
            setTimeout(() => {
                this.update(_id);
            }, 10);
        }

        this.update = function(_auctionDetails){
            this.container.style.visibility = "visible";
            this.container.innerHTML  = "";
            let p = document.createElement("p");
            p.textContent = _auctionDetails;
            this.container.appendChild(p);
        }

        this.reset = function(){
            this.container.style.visibility = "hidden";
            this.container.innerHTML = "";
        }
    }


    function Orchestrator(){
        this.foundAuctionsList = new FoundAuctionsList(document.getElementById("section-found-cards"), this);
        this.auctionDetails = new AuctionDetails(document.getElementById("section-details"));

        this.showSearchResults = function(){
            this.auctionDetails.reset();
            this.foundAuctionsList.show();
        }

        this.showDetails = function(_id){
            this.auctionDetails.show(_id);
            this.foundAuctionsList.reset();
        }
    }
})();