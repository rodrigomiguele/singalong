function search(form, event){
    event.preventDefault();
    doSearch(form.elements.query.value);
}

function doSearch(query){
    var xmlhttp = new XMLHttpRequest();
    var url = "search?query="+query;

    xmlhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var response = JSON.parse(this.responseText);
            buildResults(response);
        }
    };
    xmlhttp.open("GET", url, true);
    xmlhttp.send();
}

function buildResults(response){
    if(response) {
        if(response.videos && response.videos.length > 0) {
            var results = document.querySelector("#results");
            var newResults = document.createElement("div");
            for(var i = 0; i < response.videos.length; i++){
                var div = document.createElement("div");
                div.setAttribute("id", "video-" + i);
                var link = document.createElement("a");
                link.setAttribute("href", response.videos[i].url);
                var thumb = document.createElement("img");
                thumb.setAttribute("src", response.videos[i].thumbnailUrl);
                link.appendChild(thumb);
                var title = document.createElement("span");
                title.innerText = response.videos[i].title;
                link.appendChild(title);
                div.appendChild(link);
                newResults.appendChild(div);
            }
            results.innerHTML = newResults.innerHTML;
        }
    }
}