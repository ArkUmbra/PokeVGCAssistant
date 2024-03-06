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

import static com.arkumbra.pokesearcher.scraper.Util.selectDataRowTdsByPartOfTrHeading;

// TODO Refactor this to handle dex by generation
public class AttackDexScraper {
  private static final String NOT_PRESENT_IN_DEX = "-";

  public Set<String> getAllAttackDexUrls() throws IOException {
    return Util.getUrlsForAllSelectURLObjectsOnPage(Constants.serebiiAttackDex, "attackdex");
  }

  public Move extractMoveInfo(String url) throws IOException {
    Move move = new Move();
    Document doc = getPageWithDelay(url);

    String idName = url.replaceAll(Constants.serebiiAttackDex, "").split(".shtml")[0];
    move.setIdName(idName);

    // ------- First data row -----------
    // 0 - Name
    // 1 - Battle Type
    // 2 - Category
    Elements tdsFirstDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Attack Name");

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
    Elements tdsSecondDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Power Points");
    int pp = Integer.parseInt(tdsSecondDataRow.get(0).text());
    int power = Integer.parseInt(tdsSecondDataRow.get(1).text());
    int accuracy = Integer.parseInt(tdsSecondDataRow.get(2).text());

    move.setPp(pp);
    move.setPower(power);
    move.setAccuracy(accuracy);

    // ------- Third data row -----------
    // 0 - Battle Effect
    Elements tdsThirdDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Battle Effect");
    String ingameDescription = tdsThirdDataRow.get(0).text();
    boolean unusableLegacyMove = ingameDescription.startsWith("This move can't be used.");
    move.setIngameDescription(ingameDescription);


    // ------- Fourth data row -----------
    // 0 - Secondary Effect
    // 1 - Effect Rate
    Elements tdsFourthDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Secondary Effect");
    String effectRateUnparsed = tdsFourthDataRow.get(1).text().split("%")[0].trim();

    EffectChance chance = (effectRateUnparsed.equals("--")) ? EffectChance.NONE : new EffectChance(Integer.parseInt(effectRateUnparsed));
    move.setEffectChance(chance);


    // ------- Fifth data row -----------
    // 0 - Base Critical Hit Rate
    // 1 - Speed Priority
    // 2 - Pokemon hit in battle
    Elements tdsFifthDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Base Critical Hit Rate");
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

    Elements tdsSixthDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Sound-Type - Details");
    if (SerebiiTextParser.yesOrNo(tdsSixthDataRow.get(0).text())) metaData.add(AttackMetaData.contact);
    if (SerebiiTextParser.yesOrNo(tdsSixthDataRow.get(1).text())) metaData.add(AttackMetaData.sound);
    if (SerebiiTextParser.yesOrNo(tdsSixthDataRow.get(2).text())) metaData.add(AttackMetaData.punching);
    if (SerebiiTextParser.yesOrNo(tdsSixthDataRow.get(3).text())) metaData.add(AttackMetaData.biting);
    if (SerebiiTextParser.yesOrNo(tdsSixthDataRow.get(4).text())) metaData.add(AttackMetaData.snatchable);

    Elements tdsSeventhDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Slicing Move");
    if (SerebiiTextParser.yesOrNo(tdsSeventhDataRow.get(0).text())) metaData.add(AttackMetaData.slicing);
    if (SerebiiTextParser.yesOrNo(tdsSeventhDataRow.get(1).text())) metaData.add(AttackMetaData.bullet);
    if (SerebiiTextParser.yesOrNo(tdsSeventhDataRow.get(2).text())) metaData.add(AttackMetaData.wind);
    if (SerebiiTextParser.yesOrNo(tdsSeventhDataRow.get(3).text())) metaData.add(AttackMetaData.powder);
    if (SerebiiTextParser.yesOrNo(tdsSeventhDataRow.get(4).text())) metaData.add(AttackMetaData.metronomeCallable);

    Elements tdsEighthDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Affected by Gravity");
    if (SerebiiTextParser.yesOrNo(tdsEighthDataRow.get(0).text())) metaData.add(AttackMetaData.affectedByGravity);
    if (SerebiiTextParser.yesOrNo(tdsEighthDataRow.get(1).text())) metaData.add(AttackMetaData.defrostsOnUse);
    if (SerebiiTextParser.yesOrNo(tdsEighthDataRow.get(2).text())) metaData.add(AttackMetaData.reflectedByMagicBounceCoat);
    if (SerebiiTextParser.yesOrNo(tdsEighthDataRow.get(3).text())) metaData.add(AttackMetaData.blockedByProtect);
    if (SerebiiTextParser.yesOrNo(tdsEighthDataRow.get(4).text())) metaData.add(AttackMetaData.mirrorMoveCopyable);

    if (unusableLegacyMove) metaData.add(AttackMetaData.unusableShouldBeForgotten);

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

  private MoveTarget parseMoveTarget(String input) {
    switch (input) {
      case "Selected Target":
      case "Target and Self":
      case "Adjacent target":
      case "Adjacent Pokémon":
      case "Opponent": return MoveTarget.SingleSelected;
      case "Self": return MoveTarget.Self;
      case "Team": return MoveTarget.Team;
      case "Adjacent Ally":
      case "Ally": return MoveTarget.Ally;
      case "Field": return MoveTarget.Field;
      case "All Adjacent Opponents":
      case "All opponents":
      case "All Adjacent Foes": return MoveTarget.BothOpponents;
      case "All Adjacent Pokémon": return MoveTarget.BothOpponentsAndAlly;
      case "Random Target": return MoveTarget.Random;
      case "All": return MoveTarget.All;
      case "Allies":
      case "All allies": return MoveTarget.BothAllies;
      case "Self or Ally": return MoveTarget.SelfOrALly;
      case "Previous opponent": return MoveTarget.PreviousOpponent;
      case "Opponent's Side":
      case "Enemy side":
      case "Enemy Side": return MoveTarget.OpponentsSideOfField;
      case "Special": return MoveTarget.Unspecified;
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
      Thread.sleep(50);
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
