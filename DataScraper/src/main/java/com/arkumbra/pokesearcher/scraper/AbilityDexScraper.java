package com.arkumbra.pokesearcher.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.arkumbra.pokesearcher.scraper.Util.getPageWithDelay;
import static com.arkumbra.pokesearcher.scraper.Util.selectDataRowTdsByPartOfTrHeading;

// TODO Refactor this to handle dex by generation
public class AbilityDexScraper {

  public Set<String> getAllAbilityDexUrls() throws IOException {
    return Util.getUrlsForAllSelectURLObjectsOnPage(Constants.serebiiAbilityDex, "abilitydex");
  }

  public Ability extractAbilityInfo(String url) throws IOException {
    Ability ability = new Ability();
    Document doc = getPageWithDelay(url);

    String idName = url.replaceAll(Constants.serebiiAbilityDex, "").split(".shtml")[0];
    ability.setIdName(idName);

    // ------- First data row -----------
    // 0 - Name
    // 1 - Jp. Name
    Elements tdsFirstDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Jp. Name");

    // Name
    Name name = new Name();
    name.englishName = tdsFirstDataRow.get(0).text().trim();

    // NB: /sometimes/ the td with the Japanese ability name also contains an English translation, which is kind of
    // annoying. Therefore, we want code that handles both situations
    // This text box is always formatted as -> English name <br> Japanese name
    // This is the case even when there is no actual text provided for English name. Therefore, we just split on the br,
    // and take the last option
    try {
      name.japanese = Parser.unescapeEntities(tdsFirstDataRow.get(1).html().split("<br>")[1].trim(), true);
    } catch (Exception e) {
      System.err.println("No Japanese name found for ability!! " + url);
      e.printStackTrace();
    }

    ability.setName(name);


    // ------- Second data row -----------
    // 0 - Game's text

    // NB: wanted to search by Game's text, but I guess the ' is treated as a special character or something
    Elements tdsSecondDataRow = selectDataRowTdsByPartOfTrHeading(doc, "s text");
    ability.setIngameDescription(tdsSecondDataRow.get(0).text());


    // ------- Third data row -----------
    // 0 - Blocks Role Play & Doodle
    // 1 - Blocks Receive & Power of Alchemy
    // 2 - Blocks Entrainment
    // 3 - Blocks Trace

    Set<AbilityMetaData> metaData = new HashSet<>();
    ability.setMetaData(metaData);

    Elements tdsThirdDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Blocks Role Play & Doodle");
    if (SerebiiTextParser.yesOrNo(tdsThirdDataRow.get(0).text())) metaData.add(AbilityMetaData.blocksRolePlayAndDoodle);
    if (SerebiiTextParser.yesOrNo(tdsThirdDataRow.get(1).text())) metaData.add(AbilityMetaData.blocksReceiveAndPowerOfAlchemy);
    if (SerebiiTextParser.yesOrNo(tdsThirdDataRow.get(2).text())) metaData.add(AbilityMetaData.blocksEntrainment);
    if (SerebiiTextParser.yesOrNo(tdsThirdDataRow.get(3).text())) metaData.add(AbilityMetaData.blocksTrace);

    // ------- Fourth data row -----------
    // 0 - Blocks Skill Swap & Wandering Spirit
    // 1 - Blocks Gastro Acid & Neutralizing Gas
    // 2 - Removed by Mold Breaker / Turboblaze / Teravolt
    // 3 - Not used if user is Transfoemed

    Elements tdsFourthDataRow = selectDataRowTdsByPartOfTrHeading(doc, "Blocks Role Play & Doodle");
    if (SerebiiTextParser.yesOrNo(tdsFourthDataRow.get(0).text())) metaData.add(AbilityMetaData.blocksSkillSwapAndWanderingSpirit);
    if (SerebiiTextParser.yesOrNo(tdsFourthDataRow.get(1).text())) metaData.add(AbilityMetaData.blocksGastroAcidAndNeutralizingAcid);
    if (SerebiiTextParser.yesOrNo(tdsFourthDataRow.get(2).text())) metaData.add(AbilityMetaData.removedByMoldBreakerTurboblazeTeravolt);
    if (SerebiiTextParser.yesOrNo(tdsFourthDataRow.get(3).text())) metaData.add(AbilityMetaData.notUsedIfUserIsTransformed);

    return ability;
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


}
