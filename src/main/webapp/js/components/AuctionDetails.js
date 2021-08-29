//This is just a mock component that prints the id of the auction
export function AuctionDetails(_container, _orchestrator){
    let self = this;

    this.container = _container;

    //get the auction bean from the servlet using an ajax call
    this.show = function (_id){
        //TODO: use ajax instead
        setTimeout(() => {
            this.update(_id); //to test we only print the id, but we will show all details instead
        }, 1);
    }

    this.update = function(_auctionDetails){
        self.container.style.display = "";
        self.container.innerHTML  = "";
        let p = document.createElement("p");
        p.textContent = "Details: " + _auctionDetails;
        self.container.appendChild(p);
    }

    this.reset = function(){
        self.container.style.display = "none";
        self.container.innerHTML = "";
    }
}