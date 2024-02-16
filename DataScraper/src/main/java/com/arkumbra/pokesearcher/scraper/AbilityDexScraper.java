package com.arkumbra.pokesearcher.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

// TODO Refactor this to handle dex by generation
public class AbilityDexScraper {
  private static final String dexUrlBase = "https://www.serebii.net";
  private static final String NOT_PRESENT_IN_DEX = "-";

  public Set<String> getAllAbilityDexUrls() throws IOException {
    Set<String> urls = new HashSet<>();

    // SelectURL
    Document doc = getPageWithDelay("https://www.serebii.net/abilitydex/");
    log(doc.title());

    Elements abilitiesSelectBoxes = doc.select("select[name=SelectURL] > option");

    for (Element element : abilitiesSelectBoxes) {
      String shortUrl = element.attr("value");
      if (shortUrl != null && !shortUrl.equals("#")) {
        String combinedUrl = dexUrlBase + shortUrl;
        urls.add(combinedUrl);
      }
    }

    return urls;
  }

  public Ability extractAbilityInfo(String url) throws IOException {
    Ability ability = new Ability();
    Document doc = getPageWithDelay(url);

    String idName = url.replaceAll("https://www.serebii.net/abilitydex/", "").split(".shtml")[0];
    ability.setIdName(idName);

    // ------- First data row -----------
    // 0 - Name
    // 1 - Jp. Name
    Elements tdsFirstDataRow = selectDataRowTdsByFirstTrHeading(doc, "Jp. Name");

    // Name
    Name name = new Name();
    name.englishName = tdsFirstDataRow.get(0).text().trim();

    // NB: /sometimes/ the td with the Japanese ability name also contains an English translation, which is kind of
    // annoying. Therefore, we want code that handles both situations
    // This text box is always formatted as -> English name <br> Japanese name
    // This is the case even when there is no actual text provided for English name. Therefore, we just split on the br,
    // and take the last option
    name.japanese = Parser.unescapeEntities(tdsFirstDataRow.get(1).html().split("<br>")[1].trim(), true);
    ability.setName(name);


    // ------- Second data row -----------
    // 0 - Game's text

    // NB: wanted to search by Game's text, but I guess the ' is treated as a special character or something
    Elements tdsSecondDataRow = selectDataRowTdsByFirstTrHeading(doc, "s text");
    ability.setIngameDescription(tdsSecondDataRow.get(0).text());


    // ------- Third data row -----------
    // 0 - Blocks Role Play & Doodle
    // 1 - Blocks Receive & Power of Alchemy
    // 2 - Blocks Entrainment
    // 3 - Blocks Trace

    Set<AbilityMetaData> metaData = new HashSet<>();
    ability.setMetaData(metaData);

    Elements tdsThirdDataRow = selectDataRowTdsByFirstTrHeading(doc, "Blocks Role Play & Doodle");
    if (SerebiiTextParser.yesOrNo(tdsThirdDataRow.get(0).text())) metaData.add(AbilityMetaData.blocksRolePlayAndDoodle);
    if (SerebiiTextParser.yesOrNo(tdsThirdDataRow.get(1).text())) metaData.add(AbilityMetaData.blocksReceiveAndPowerOfAlchemy);
    if (SerebiiTextParser.yesOrNo(tdsThirdDataRow.get(2).text())) metaData.add(AbilityMetaData.blocksEntrainment);
    if (SerebiiTextParser.yesOrNo(tdsThirdDataRow.get(3).text())) metaData.add(AbilityMetaData.blocksTrace);

    // ------- Fourth data row -----------
    // 0 - Blocks Skill Swap & Wandering Spirit
    // 1 - Blocks Gastro Acid & Neutralizing Gas
    // 2 - Removed by Mold Breaker / Turboblaze / Teravolt
    // 3 - Not used if user is Transfoemed

    Elements tdsFourthDataRow = selectDataRowTdsByFirstTrHeading(doc, "Blocks Role Play & Doodle");
    if (SerebiiTextParser.yesOrNo(tdsFourthDataRow.get(0).text())) metaData.add(AbilityMetaData.blocksSkillSwapAndWanderingSpirit);
    if (SerebiiTextParser.yesOrNo(tdsFourthDataRow.get(1).text())) metaData.add(AbilityMetaData.blocksGastroAcidAndNeutralizingAcid);
    if (SerebiiTextParser.yesOrNo(tdsFourthDataRow.get(2).text())) metaData.add(AbilityMetaData.removedByMoldBreakerTurboblazeTeravolt);
    if (SerebiiTextParser.yesOrNo(tdsFourthDataRow.get(3).text())) metaData.add(AbilityMetaData.notUsedIfUserIsTransformed);

    return ability;
  }

  private boolean parseYesNoString(String boolValue) {
    return boolValue.equals("Yes");
  }

  private BigDecimal parseCriticalHitRatio(String criticalRatioUnparsed) {
    criticalRatioUnparsed = criticalRatioUnparsed.trim();

    if (criticalRatioUnparsed.equals("None")) {
      return new BigDecimal(-1);
    }

    // Expected formatting is -> 4.17%
    // So we need to remove the percentage sign
    return new BigDecimal(criticalRatioUnparsed.split("%")[0]);
  }

  private Elements selectDataRowTdsByFirstTrHeading(Document document, String heading) {
    Element dataRowTr = document.select("tr:contains(" + heading + ")").first().nextElementSibling();

    return dataRowTr.select("> td");
  }

  private MoveTarget parseMoveTarget(String input) {
    switch (input) {
      case "Selected Target": return MoveTarget.SingleSelected;
      default: throw new RuntimeException("Move Target type parse not implemented [" + input + "]");
    }
  }

  private TypeValue extractTypes(Element moveTypeTd) {
    Attribute href = moveTypeTd.selectFirst("a").attribute("href");
    String[] splits = href.getValue().split("/");
    String type = splits[splits.length - 1].replaceAll(".shtml", ""); // remove the .shtml part of the type
    return TypeValue.valueOf(type);
  }

  private AttackCategory extractAttackCategory(Element moveCategoryTd) {
    Attribute href = moveCategoryTd.selectFirst("a").attribute("href");
    String[] splits = href.getValue().split("/");
    String category = splits[splits.length - 1].replaceAll(".shtml", ""); // remove the .shtml part of the type
    switch (category) {
      case "other": return AttackCategory.status;
      case "physical": return AttackCategory.physical;
      case "special": return AttackCategory.special;
      default: throw new RuntimeException("Unhandled type category [" + category + "]");
    }
  }

  private Document getPageWithDelay(String url) {
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

  private void log(String... msg) {
    System.out.println("log -> " + System.lineSeparator() + String.join(System.lineSeparator(), msg));
  }

}
