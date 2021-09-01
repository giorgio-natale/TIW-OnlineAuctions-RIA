export function WinnerDetails(_container, _orchestrator) {
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;

    self.regularInfosShown = document.getElementById("auctionWinner-regularInfosShown");
    self.noWinnerShown = document.getElementById("auctionWinner-noWinnerShown");
    self.otherInfosShown = document.getElementById("auctionWinner-otherInfosShown");

    self.name = document.getElementById("auctionWinner-name");
    self.address = document.getElementById("auctionWinner-address");
    self.otherAddressInfos = document.getElementById("auctionWinner-other");

    this.show = function(auctionID) {
        makeCall("GET", "GetAuctionWinner?auctionID=" + auctionID, null,
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

    this.update = function(userBean) {
        if(Object.keys(userBean).length === 0) {
            self.regularInfosShown.style.display = "none";
            self.noWinnerShown.style.display = "";
        } else {
            self.regularInfosShown.style.display = "";
            self.noWinnerShown.style.display = "none";

            if(userBean.other_address_infos === undefined)
                self.otherInfosShown.style.display = "none";
            else {
                self.otherInfosShown.style.display = "";
                self.otherAddressInfos.textContent = userBean.other_address_infos;
            }

            self.name.textContent = userBean.first_name + ' ' + userBean.last_name;
            self.address.textContent = userBean.street + ' - ' + userBean.city + ' (' + userBean.province + ') ' + userBean.zip_code;
        }

        self.container.style.display = "";
    }

    this.reset = function () {
        self.name.textContent = "";
        self.address.textContent = "";
        self.otherAddressInfos.textContent = "";

        self.container.style.display = "none";
    }
}