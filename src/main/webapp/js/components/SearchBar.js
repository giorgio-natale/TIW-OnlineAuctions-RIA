export function SearchBar(_container, _orchestrator){
    let self = this;

    self.container = _container;
    self.orchestrator = _orchestrator;
    self.formTag = document.getElementById("search-form");
    self.searchTag = document.getElementById("search-input");

    this.registerEvents = function() {
        self.formTag.addEventListener("submit", (evt) => {
            //prevent the default behavior of this event (send a get request)
            evt.preventDefault();
            if (!self.searchTag.checkValidity()) {
                self.searchTag.reportValidity();
                return;
            }

            self.orchestrator.showSearchResults(self.searchTag.value);
        });
    }

    this.reset = function(){
        self.container.style.display = "none";
        self.formTag.value = self.formTag.placeholder;
    }

    this.show = function(){
        self.container.style.display = "";
    }

    this.registerEvents();
}