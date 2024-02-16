package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AttackDexScraperGen9Test {

  private AttackDexScraper scraper;

  @BeforeEach
  public void setUp() {
    this.scraper = new AttackDexScraper();
  }


  @Test
  public void testGetAllAttackDexLinks() throws IOException {
    Set<String> urls = scraper.getAllAttackDexUrls();
    System.out.println(urls);

    assertTrue(urls.contains("https://www.serebii.net/attackdex-sv/absorb.shtml"));
    assertTrue(urls.contains("https://www.serebii.net/attackdex-sv/gyroball.shtml"));
    assertTrue(urls.contains("https://www.serebii.net/attackdex-sv/hail.shtml"));
    assertTrue(urls.contains("https://www.serebii.net/attackdex-sv/ruination.shtml"));
    assertTrue(urls.contains("https://www.serebii.net/attackdex-sv/sacredfire.shtml"));
    assertTrue(urls.contains("https://www.serebii.net/attackdex-sv/zippyzap.shtml"));
  }

  @Test
  public void testExtractVariousMoves() throws IOException {
    Move odorSleuth = scraper.extractMoveInfo("https://www.serebii.net/attackdex-sv/odorsleuth.shtml");
    System.out.println(odorSleuth.getName());

    assertEquals("Odor Sleuth", odorSleuth.getName().englishName);
    assertEquals("かぎわける", odorSleuth.getName().japanese);
    assertEquals(TypeValue.normal, odorSleuth.getType());
    assertEquals(AttackCategory.status, odorSleuth.getCategory());

    assertEquals(40, odorSleuth.getPp());
    assertEquals(0, odorSleuth.getPower());
    assertEquals(101, odorSleuth.getAccuracy());

    assertEquals(EffectChance.NONE, odorSleuth.getEffectChance());

    assertEquals(new BigDecimal(-1), odorSleuth.getCritRatio());
    assertEquals(Priority.Standard, odorSleuth.getPriority());
    assertEquals(MoveTarget.SingleSelected, odorSleuth.getMoveTarget());

    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.contact));
    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.sound));
    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.punching));
    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.biting));
    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.snatchable));

    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.slicing));
    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.bullet));
    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.wind));
    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.powder));
    assertTrue(odorSleuth.getAttackMetaData().contains(AttackMetaData.metronomeCallable));

    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.affectedByGravity));
    assertFalse(odorSleuth.getAttackMetaData().contains(AttackMetaData.defrostsOnUse));
    assertTrue(odorSleuth.getAttackMetaData().contains(AttackMetaData.reflectedByMagicBounceCoat));
    assertTrue(odorSleuth.getAttackMetaData().contains(AttackMetaData.blockedByProtect));
    assertTrue(odorSleuth.getAttackMetaData().contains(AttackMetaData.mirrorMoveCopyable));


    Move heatCrash = scraper.extractMoveInfo("https://www.serebii.net/attackdex-sv/heatcrash.shtml");
    System.out.println(heatCrash.getName());

    assertEquals("Heat Crash", heatCrash.getName().englishName);
    assertEquals("ヒートスタンプ", heatCrash.getName().japanese);
    assertEquals(TypeValue.fire, heatCrash.getType());
    assertEquals(AttackCategory.physical, heatCrash.getCategory());

    assertEquals(10, heatCrash.getPp());
    assertEquals(1, heatCrash.getPower());
    assertEquals(100, heatCrash.getAccuracy());

    assertEquals(EffectChance.NONE, heatCrash.getEffectChance());

    assertEquals(new BigDecimal("4.17"), heatCrash.getCritRatio());
    assertEquals(Priority.Standard, heatCrash.getPriority());
    assertEquals(MoveTarget.SingleSelected, heatCrash.getMoveTarget());

    assertTrue(heatCrash.getAttackMetaData().contains(AttackMetaData.contact));
    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.sound));
    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.punching));
    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.biting));
    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.snatchable));

    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.slicing));
    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.bullet));
    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.wind));
    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.powder));
    assertTrue(heatCrash.getAttackMetaData().contains(AttackMetaData.metronomeCallable));

    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.affectedByGravity));
    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.defrostsOnUse));
    assertFalse(heatCrash.getAttackMetaData().contains(AttackMetaData.reflectedByMagicBounceCoat));
    assertTrue(heatCrash.getAttackMetaData().contains(AttackMetaData.blockedByProtect));
    assertTrue(heatCrash.getAttackMetaData().contains(AttackMetaData.mirrorMoveCopyable));
  }

}
