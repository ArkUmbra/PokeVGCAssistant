package com.arkumbra.pokesearcher.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

// TODO Refactor this to handle dex by generation
public class AttackDexScraper {
  private static final String pokedexUrlBase = "https://www.serebii.net";
  private static final String NOT_PRESENT_IN_DEX = "-";

  public Set<String> getAllAttackDexUrls() throws IOException {
    Set<String> urls = new HashSet<>();

    // SelectURL
    Document doc = getPageWithDelay("https://www.serebii.net/attackdex-sv/");
    log(doc.title());

    Elements pokedexSelectBoxes = doc.select("select[name=SelectURL] > option");

    for (Element element : pokedexSelectBoxes) {
      String shortUrl = element.attr("value");
      if (shortUrl != null && !shortUrl.equals("#")) {
        String combinedUrl = pokedexUrlBase + shortUrl;
        urls.add(combinedUrl);
      }
    }

    return urls;
  }

  public Move extractMoveInfo(String url) throws IOException {
    Move move = new Move();
    Document doc = getPageWithDelay(url);

    // ------- First data row -----------
    // 0 - Name
    // 1 - Battle Type
    // 2 - Category
    Elements tdsFirstDataRow = selectDataRowTdsByFirstTrHeading(doc, "Attack Name");

    // Name
    String[] nameSplits = tdsFirstDataRow.get(0).html().split("<br>");
    Name name = new Name();
    name.englishName = nameSplits[0];
    name.japanese = Parser.unescapeEntities(nameSplits[1].split("<!--")[0].trim(), true);
    move.setName(name);

    // Type
    TypeValue type = extractTypes(tdsFirstDataRow.get(1));
    move.setType(type);

    // Category
    AttackCategory category = extractAttackCategory(tdsFirstDataRow.get(2));
    move.setCategory(category);

    // ------- Second data row -----------
    // 0 - PP
    // 1 - Base Power
    // 2 - Accuracy
    Elements tdsSecondDataRow = selectDataRowTdsByFirstTrHeading(doc, "Power Points");
    int pp = Integer.parseInt(tdsSecondDataRow.get(0).text());
    int power = Integer.parseInt(tdsSecondDataRow.get(1).text());
    int accuracy = Integer.parseInt(tdsSecondDataRow.get(2).text());

    move.setPp(pp);
    move.setPower(power);
    move.setAccuracy(accuracy);

    // ------- Third data row -----------
    // 0 - Battle Effect
    Elements tdsThirdDataRow = selectDataRowTdsByFirstTrHeading(doc, "Battle Effect");
    boolean unusableLegacyMove = tdsThirdDataRow.get(0).text().startsWith("This move can't be used.");

    // ------- Fourth data row -----------
    // 0 - Secondary Effect
    // 1 - Effect Rate
    Elements tdsFourthDataRow = selectDataRowTdsByFirstTrHeading(doc, "Secondary Effect");
    String effectRateUnparsed = tdsFourthDataRow.get(1).text().split("%")[0].trim();

    EffectChance chance = (effectRateUnparsed.equals("--")) ? EffectChance.NONE : new EffectChance(Integer.parseInt(effectRateUnparsed));
    move.setEffectChance(chance);


    // ------- Fifth data row -----------
    // 0 - Base Critical Hit Rate
    // 1 - Speed Priority
    // 2 - Pokemon hit in battle
    Elements tdsFifthDataRow = selectDataRowTdsByFirstTrHeading(doc, "Base Critical Hit Rate");
    String criticalRatioUnparsed = tdsFifthDataRow.get(0).text();
    String priorityUnparsed = tdsFifthDataRow.get(1).text();
    String targetHitUnparsed = tdsFifthDataRow.get(2).text();

    BigDecimal criticalHitRatio = parseCriticalHitRatio(criticalRatioUnparsed);
    Priority priority = Priority.ofNumber(Integer.parseInt(priorityUnparsed));
    MoveTarget moveTarget = parseMoveTarget(targetHitUnparsed);

    move.setCritRatio(criticalHitRatio);
    move.setPriority(priority);
    move.setMoveTarget(moveTarget);

    // ------- Sixth data row -----------
    // 0 - Physical Contact
    // 1 - Sound-Type
    // 2 - Punch Move
    // 3 - Biting Move
    // 4 - Snatchable

    Set<AttackMetaData> metaData = new HashSet<>();
    move.setAttackMetaData(metaData);

    Elements tdsSixthDataRow = selectDataRowTdsByFirstTrHeading(doc, "Physical Contact");
    if (SerebiiTextParser.yesOrNo(tdsSixthDataRow.get(0).text())) metaData.add(AttackMetaData.contact);
    if (SerebiiTextParser.yesOrNo(tdsSixthDataRow.get(1).text())) metaData.add(AttackMetaData.sound);
    if (SerebiiTextParser.yesOrNo(tdsSixthDataRow.get(2).text())) metaData.add(AttackMetaData.punching);
    if (SerebiiTextParser.yesOrNo(tdsSixthDataRow.get(3).text())) metaData.add(AttackMetaData.biting);
    if (SerebiiTextParser.yesOrNo(tdsSixthDataRow.get(4).text())) metaData.add(AttackMetaData.snatchable);

    Elements tdsSeventhDataRow = selectDataRowTdsByFirstTrHeading(doc, "Slicing Move");
    if (SerebiiTextParser.yesOrNo(tdsSeventhDataRow.get(0).text())) metaData.add(AttackMetaData.slicing);
    if (SerebiiTextParser.yesOrNo(tdsSeventhDataRow.get(1).text())) metaData.add(AttackMetaData.bullet);
    if (SerebiiTextParser.yesOrNo(tdsSeventhDataRow.get(2).text())) metaData.add(AttackMetaData.wind);
    if (SerebiiTextParser.yesOrNo(tdsSeventhDataRow.get(3).text())) metaData.add(AttackMetaData.powder);
    if (SerebiiTextParser.yesOrNo(tdsSeventhDataRow.get(4).text())) metaData.add(AttackMetaData.metronomeCallable);

    Elements tdsEighthDataRow = selectDataRowTdsByFirstTrHeading(doc, "Affected by Gravity");
    if (SerebiiTextParser.yesOrNo(tdsEighthDataRow.get(0).text())) metaData.add(AttackMetaData.affectedByGravity);
    if (SerebiiTextParser.yesOrNo(tdsEighthDataRow.get(1).text())) metaData.add(AttackMetaData.defrostsOnUse);
    if (SerebiiTextParser.yesOrNo(tdsEighthDataRow.get(2).text())) metaData.add(AttackMetaData.reflectedByMagicBounceCoat);
    if (SerebiiTextParser.yesOrNo(tdsEighthDataRow.get(3).text())) metaData.add(AttackMetaData.blockedByProtect);
    if (SerebiiTextParser.yesOrNo(tdsEighthDataRow.get(4).text())) metaData.add(AttackMetaData.mirrorMoveCopyable);

    return move;
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
