package com.arkumbra.pokesearcher.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.arkumbra.pokesearcher.scraper.Util.getPageWithDelay;

public class PokemonDbSpriteScraper {

  private static final String url = "https://pokemondb.net/sprites";
  private static final String spriteUrlBase = "https://pokemondb.net";
  private static final String spriteDownloadUrlFormat = "https://img.pokemondb.net/sprites/scarlet-violet/normal/%s.png";

  public List<Pair<String, String>> getAllSpritePageUrls() {
    Document document = getPageWithDelay(url);
    Elements infocards = document.select("a.infocard");
    System.out.println(infocards.size());


    return infocards.stream().map(infocard -> {
      String englishName = infocard.text();
      String urlPart = infocard.select("a").attr("href");
      return new Pair<>(englishName, spriteUrlBase + urlPart);
    }).collect(Collectors.toList());
  }

  public String getSpriteDownloadUrl(String spritePageUrl) {
    String pokemonName = spritePageUrl.replace(url + "/", "");
    return String.format(spriteDownloadUrlFormat, pokemonName);
  }
//  public String getSpriteDownloadUrl(String spritePageUrl) {
//    Document document = getPageWithDelay(url);
//
////    Elements overviewHeading = document.select("h2:contains(Overview)")
//    Elements tableTds = document.select("table.sprites-history-table > tbody > tr > td");
//    System.out.println(tableTds);
//    System.out.println(tableTds.size());
//    String downloadUrl = tableTds.last().select("a").attr("href");
//    System.out.println(downloadUrl);
//    return downloadUrl;
//  }

  public void findAllSpritesAndDownload(String localSaveDirectory) throws IOException {
    List<Pair<String, String>> spritePageUrls = getAllSpritePageUrls();

    for (Pair<String, String> spritePageUrl : spritePageUrls) {
      String name = spritePageUrl.left;
      String pageUrl = spritePageUrl.right;

      String actualDownloadUrl = getSpriteDownloadUrl(pageUrl);

      try {
        downloadImage(actualDownloadUrl, localSaveDirectory, name);
        System.out.println("Downloaded " + name);
      } catch (Exception e) {
        if (e.getMessage().contains("Can't get input stream from URL!")) {
          System.err.println("Not available in Pokemon Scarlet / Violet " + name);
        } else {
          e.printStackTrace();
        }
      }
    }

  }

  private void downloadImage(String downloadFrom, String downloadTo, String pokemonName) throws IOException {
    URL url = new URL(downloadFrom);
    BufferedImage img = ImageIO.read(url);
    File file = new File(downloadTo + "/" + pokemonName + ".png");
    ImageIO.write(img, "png", file);
  }
}
