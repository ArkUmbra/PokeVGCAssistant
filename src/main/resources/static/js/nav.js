function navBarDoSearch() {
  const txtBox = document.querySelector("#input-nav-search");
  window.location.href = "/search/" + txtBox.value;
}

//window.onload = function() {
//
//  let formNavSearch = document.querySelector("#form-nav-search");
//  formNavSearch.addEventListener("submit", navBarDoSearch(), false);
//
//}

//  let txtBox = document.querySelector("#input-nav-search")

//  txtBox.addEventListener("keyup", function(event) {
//    if (event.keyCode === 13) {
//      navBarDoSearch()
//    }
//  });


