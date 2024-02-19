package com.arkumbra.pokesearcher.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Util {

  public static Document getPageWithDelay(String url) {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace(); // TODO logger
    }

    try {
      return Jsoup.connect(url).get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Set<String> getUrlsForAllSelectURLObjectsOnPage(String serebiiPageLink) throws IOException {
    // Some pages have duplicate values, so just using a set to simplify things
    Set<String> urls = new HashSet<>();

    // SelectURL
    Document doc = getPageWithDelay(serebiiPageLink);

    Elements pokedexSelectBoxes = doc.select("select[name=SelectURL] > option");

    for (Element element : pokedexSelectBoxes) {
      String shortUrl = element.attr("value");
      if (shortUrl != null && !shortUrl.equals("#")) {
        String combinedUrl = Constants.serebiiHomePage + shortUrl;
        urls.add(combinedUrl);
      }
    }

    return urls;
  }

  public static Elements selectDataRowTdsByPartOfTrHeading(Document document, String heading) {
    Element dataRowTr = document.select("tr:contains(" + heading + ")").first().nextElementSibling();

    return dataRowTr.select("> td");
  }

}
