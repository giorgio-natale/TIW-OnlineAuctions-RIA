export function BidCard(){
    let self = this;

    self.container = document.createElement("li");
    self.container.className += "list-group-item";

    self.createTemplate = function(){
        let outerDiv = document.createElement("div");

        let h5 = document.createElement("h5");
        h5.className += "card-title d-inline-block";

        //name of the bid owner
        self.userSpan = document.createElement("span");
        self.userSpan.className += "badge text-light fw-normal";

        let innerDiv = document.createElement("div");

        let outerBidPriceSpan = document.createElement("span");
        outerBidPriceSpan.className += "fw-bold";

        //offer amount
        self.priceBidSpan = document.createElement("span");

        let euroSpan = document.createElement("span");
        euroSpan.innerHTML = " &euro;"

        //bid date
        self.dateSpan = document.createElement("span");


        //assembling tags
        self.container.appendChild(outerDiv);
        outerDiv.appendChild(h5);
        h5.appendChild(self.userSpan);
        outerDiv.appendChild(innerDiv);
        innerDiv.appendChild(outerBidPriceSpan);
        outerBidPriceSpan.appendChild(self.priceBidSpan);
        outerBidPriceSpan.appendChild(euroSpan);
        innerDiv.appendChild(self.dateSpan);
    }

    self.show = function(bid){
        self.reset();
        self.createTemplate();
        self.update(bid);
    }

    self.update = function(bid) {
        if(localStorage.getItem("user_id") !== String(bid.user_id)){
            self.userSpan.className += " bg-primary";
            self.userSpan.textContent = bid.user_first_name + " " + bid.user_last_name;
        }else{
            self.userSpan.className += " bg-secondary";
            self.userSpan.textContent = bid.user_first_name + " " + bid.user_last_name + " (You)";
        }
        self.priceBidSpan.textContent = getPriceFormat(bid.price);
        self.dateSpan.textContent = ' - ' + secondsToDate(bid.bid_time);
    }

    self.reset = function() {
        while (self.container.firstChild) {
            self.container.firstChild.remove();
        }
    }

    self.cleanup = function(){
        self.container.parentNode.removeChild(self.container);
    }

}