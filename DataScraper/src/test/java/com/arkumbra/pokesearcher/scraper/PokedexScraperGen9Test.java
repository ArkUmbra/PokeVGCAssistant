package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static com.arkumbra.pokesearcher.scraper.TestUtils.assertOptionalEmpty;
import static org.junit.jupiter.api.Assertions.*;

public class PokedexScraperGen9Test {

  private PokedexScraper scraper;

  @BeforeEach
  public void setUp() {
    this.scraper = new PokedexScraper();
  }


  @Test
  public void testOnlyPokemonUrlsAreExtracted() throws IOException {
    Set<String> allPokemonDexUrls = scraper.getAllPokemonDexUrls();
    System.out.println(allPokemonDexUrls);
    // The select boxes on the top level Pokedex page contained these links which we don't want, so make sure they are
    // excluded
    assertFalse(allPokemonDexUrls.contains("https://www.serebii.net/pokedex-sv/egg/amorphous.shtml"));
    assertFalse(allPokemonDexUrls.contains("https://www.serebii.net/pokedex-sv/egg/noeggs.shtml"));
    assertFalse(allPokemonDexUrls.contains("https://www.serebii.net/pokedex-sv/egg/monster.shtml"));
    assertFalse(allPokemonDexUrls.contains("https://www.serebii.net/pokedex-sv//"));
  }

  @Test
  public void testPagesWhichCausedErrors() throws IOException {
    // These Pokemon have all failed to parse correctly at one point, usually due to having multiple forms

    List<String> urls = List.of(
        "https://www.serebii.net/pokedex-sv/weezing/",
        "https://www.serebii.net/pokedex-sv/basculegion/",
        "https://www.serebii.net/pokedex-sv/sawsbuck/",
        "https://www.serebii.net/pokedex-sv/tatsugiri/",
        "https://www.serebii.net/pokedex-sv/arceus/",
        "https://www.serebii.net/pokedex-sv/shellos/",
        "https://www.serebii.net/pokedex-sv/dudunsparce/",
        "https://www.serebii.net/pokedex-sv/tauros/",
        "https://www.serebii.net/pokedex-sv/meowstic/",
        "https://www.serebii.net/pokedex-sv/smeargle/",
        "https://www.serebii.net/pokedex-sv/cosmoem/",
        "https://www.serebii.net/pokedex-sv/minior/",
        "https://www.serebii.net/pokedex-sv/magikarp/",
        "https://www.serebii.net/pokedex-sv/oricorio/",
        "https://www.serebii.net/pokedex-sv/basculin/",
        "https://www.serebii.net/pokedex-sv/ogerpon/",
        "https://www.serebii.net/pokedex-sv/indeedee/"
    );

    for (String url : urls) {
      System.out.println(url);
      Pokemon pokemon = scraper.extractPokemonInfo(url);
      System.out.println(pokemon);
    }
  }

  @Test
  public void testAllRegionalForms() throws IOException {
    // These Pokemon have all failed to parse correctly at one point, usually due to having multiple forms

    List<String> urls = List.of(
        "https://www.serebii.net/pokedex-sv/raichu/",
        "https://www.serebii.net/pokedex-sv/sandshrew/",
        "https://www.serebii.net/pokedex-sv/sandslash/",
        "https://www.serebii.net/pokedex-sv/vulpix/",
        "https://www.serebii.net/pokedex-sv/ninetales/",
        "https://www.serebii.net/pokedex-sv/diglett/",
        "https://www.serebii.net/pokedex-sv/dugtrio/",
        "https://www.serebii.net/pokedex-sv/meowth/",
        "https://www.serebii.net/pokedex-sv/persian/",
        "https://www.serebii.net/pokedex-sv/geodude/",
        "https://www.serebii.net/pokedex-sv/graveler/",
        "https://www.serebii.net/pokedex-sv/golem/",
        "https://www.serebii.net/pokedex-sv/grimer/",
        "https://www.serebii.net/pokedex-sv/muk/",
        "https://www.serebii.net/pokedex-sv/exeggutor/",
        "https://www.serebii.net/pokedex-sv/slowpoke/",
        "https://www.serebii.net/pokedex-sv/slowbro/",
        "https://www.serebii.net/pokedex-sv/slowking/",
        "https://www.serebii.net/pokedex-sv/weezing/",
        "https://www.serebii.net/pokedex-sv/articuno/",
        "https://www.serebii.net/pokedex-sv/zapdos/",
        "https://www.serebii.net/pokedex-sv/moltres/",
        "https://www.serebii.net/pokedex-sv/growlithe/",
        "https://www.serebii.net/pokedex-sv/arcanine/",
        "https://www.serebii.net/pokedex-sv/voltorb/",
        "https://www.serebii.net/pokedex-sv/typhlosion/",
        "https://www.serebii.net/pokedex-sv/qwilfish/",
        "https://www.serebii.net/pokedex-sv/sneasel/",
        "https://www.serebii.net/pokedex-sv/samurott/",
        "https://www.serebii.net/pokedex-sv/lilligant/",
        "https://www.serebii.net/pokedex-sv/zorua/",
        "https://www.serebii.net/pokedex-sv/zoroark/",
        "https://www.serebii.net/pokedex-sv/braviary/",
        "https://www.serebii.net/pokedex-sv/sliggoo/",
        "https://www.serebii.net/pokedex-sv/goodra/",
        "https://www.serebii.net/pokedex-sv/avalugg/",
        "https://www.serebii.net/pokedex-sv/decidueye/",
        "https://www.serebii.net/pokedex-sv/wooper/"
    );

    for (String url : urls) {
      System.out.println(url);
      try {
        Pokemon pokemon = scraper.extractPokemonInfo(url);
      } catch(Exception e) {
        e.printStackTrace();
      }
//      System.out.println(pokemon);
    }
  }

  @Test
  public void testScrapePikachu() throws IOException {
    Pokemon pokemon = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/pikachu/");
    System.out.println(pokemon);

    assertTrue(pokemon.getDetails().getMoveset().getLevelUpMoves().contains(new Pair<>(new MoveAcquire(MoveAcquireType.Distribution), "fly")));
    assertTrue(pokemon.getDetails().getMoveset().getLevelUpMoves().contains(new Pair<>(new MoveAcquire(MoveAcquireType.Distribution), "celebrate")));
  }

  @Test
  public void testDifferentlyFormattedPokemonPages() throws IOException {
    // New SV Poke
    Pokemon tinkaton = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/tinkaton/");
    System.out.println(tinkaton);

    assertFalse(tinkaton.hasAlternateForms());
    assertNames(new Name("Tinkaton"), tinkaton.getName());
    assertPokedex(
        mapOfNullable(
            PokedexRegion.Paldea, 281,
            PokedexRegion.National, 959,
            PokedexRegion.Blueberry, null,
            PokedexRegion.Kitakami, null
        ), tinkaton.getPokedexNumber());
    assertEquals(new Abilities(List.of("moldbreaker", "owntempo"), "pickpocket"),
        tinkaton.getDetails().getAbilities());
    assertEquals(new BaseStats(85, 75, 77,70,105,94),
        tinkaton.getDetails().getBaseStats());

    // Pokemon from previous generation that is available in SV
    Pokemon rillaboom = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/rillaboom/");
    System.out.println(rillaboom);

    assertFalse(rillaboom.hasAlternateForms());
    assertNames(new Name("Rillaboom"), rillaboom.getName());
    assertPokedex(
        mapOfNullable(
            PokedexRegion.Paldea, null,
            PokedexRegion.National, 812,
            PokedexRegion.Blueberry, 229,
            PokedexRegion.Kitakami, null
        ), rillaboom.getPokedexNumber());
    assertEquals(new Abilities(List.of("overgrow"), "grassysurge"),
        rillaboom.getDetails().getAbilities());
    assertEquals(new BaseStats(100,125, 90, 60, 70, 85),
        rillaboom.getDetails().getBaseStats());


    // No Egg moves
    Pokemon hooh = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/ho-oh/");
    System.out.println(hooh);
    assertNames(new Name("Ho-Oh"), hooh.getName());
    assertPokedex(
        mapOfNullable(
            PokedexRegion.Paldea, null,
            PokedexRegion.National, 250,
            PokedexRegion.Blueberry, null,
            PokedexRegion.Kitakami, null
        ), hooh.getPokedexNumber());
    assertFalse(hooh.hasAlternateForms());
    assertEquals(new Abilities(List.of("pressure"), "regenerator"),
        hooh.getDetails().getAbilities());
    assertEquals(new BaseStats(106, 130, 90, 110, 154, 90),
        hooh.getDetails().getBaseStats());
  }

  @Test
  public void testMultiFormPokemonScrapeCorrectly() throws IOException {

    // ------
    // Different types across forms
    Pokemon weezing = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/weezing/");
    System.out.println(weezing);

    assertNames(new Name("Weezing"), weezing.getName());
    assertPokedex(
        mapOfNullable(
            PokedexRegion.Paldea, null,
            PokedexRegion.National, 110,
            PokedexRegion.Blueberry, null,
            PokedexRegion.Kitakami, 136
        ), weezing.getPokedexNumber());
    assertTrue(weezing.hasAlternateForms());
    assertEquals(1, weezing.getAlternateForms().size());

    PokemonDetails kantoWeezing = weezing.getDetails();
    assertEquals("Kantonian Form", kantoWeezing.getFormName());
    assertEquals(linkedHashSet(TypeValue.poison), kantoWeezing.getTypes());

    PokemonDetails galarWeezing = weezing.getAlternateForms().get(0);
    assertEquals("Galarian Form", galarWeezing.getFormName());
    assertEquals(linkedHashSet(TypeValue.poison, TypeValue.fairy), galarWeezing.getTypes());

    assertTrue(galarWeezing.getMoveset().getLevelUpMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.Relearner), "aromaticmist")));
    assertFalse(kantoWeezing.getMoveset().getLevelUpMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.Relearner), "aromaticmist")));

    assertTrue(galarWeezing.getMoveset().getTmMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.TM, 79), "dazzlinggleam")));
    assertFalse(kantoWeezing.getMoveset().getTmMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.TM, 79), "dazzlinggleam")));

    assertTrue(galarWeezing.getMoveset().getTmMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.TM, 91), "toxicspikes")));
    assertTrue(kantoWeezing.getMoveset().getTmMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.TM, 91), "toxicspikes")));

    // Apparently Galar Weezing gets all the moves of normal Weezing along with some extra
    kantoWeezing.getMoveset().getLevelUpMoves().forEach(levelUpMove -> {
      assertTrue(galarWeezing.getMoveset().getLevelUpMoves().stream().map(Pair::getRight).toList()
          .contains(levelUpMove.getRight()), "Galar Weezing doesn't learn " + levelUpMove);
    });

    kantoWeezing.getMoveset().getTmMoves().forEach(levelUpMove -> {
      assertTrue(galarWeezing.getMoveset().getTmMoves().stream().map(Pair::getRight).toList()
          .contains(levelUpMove.getRight()), "Galar Weezing doesn't learn " + levelUpMove);
    });

    assertEquals(kantoWeezing.getMoveset().getEggMoves(), galarWeezing.getMoveset().getEggMoves());
    assertEquals(List.of(
            "curse",
            "spite",
            "painsplit",
            "stockpile",
            "spitup",
            "swallow"
        ),
        kantoWeezing.getMoveset().getEggMoves());

    assertEquals(galarWeezing.getBaseStats(), kantoWeezing.getBaseStats());
    assertEquals(new BaseStats(65, 90, 120, 85, 70, 60),
        galarWeezing.getBaseStats());

    assertEquals(2, kantoWeezing.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(kantoWeezing.getAbilities().getStandardAbilityNameIds().contains("levitate"));
    assertTrue(kantoWeezing.getAbilities().getStandardAbilityNameIds().contains("neutralizinggas"));
    assertEquals("stench", kantoWeezing.getAbilities().getHiddenAbilityNameId().get());

    assertEquals(2, galarWeezing.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(galarWeezing.getAbilities().getStandardAbilityNameIds().contains("levitate"));
    assertTrue(galarWeezing.getAbilities().getStandardAbilityNameIds().contains("neutralizinggas"));
    assertEquals("mistysurge", galarWeezing.getAbilities().getHiddenAbilityNameId().get());

    // ------
    // Alola
    Pokemon sandshrew = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/sandshrew/");
    System.out.println(sandshrew);

    assertNames(new Name("Sandshrew"), sandshrew.getName());
    assertTrue(sandshrew.hasAlternateForms());
    assertEquals(1, sandshrew.getAlternateForms().size());

    PokemonDetails kantoSandshrew = sandshrew.getDetails();
    assertEquals("Kantonian Form", kantoSandshrew.getFormName());
    assertEquals(linkedHashSet(TypeValue.ground), kantoSandshrew.getTypes());
    assertFalse(kantoSandshrew.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(kantoSandshrew.getMoveset().getTmMoves().isEmpty());
    assertFalse(kantoSandshrew.getMoveset().getEggMoves().isEmpty());

    PokemonDetails alolaSandshrew = sandshrew.getAlternateForms().get(0);
    assertEquals("Alola Form", alolaSandshrew.getFormName());
    assertEquals(linkedHashSet(TypeValue.ice, TypeValue.steel), alolaSandshrew.getTypes());
    assertFalse(alolaSandshrew.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(alolaSandshrew.getMoveset().getTmMoves().isEmpty());
    assertFalse(alolaSandshrew.getMoveset().getEggMoves().isEmpty());

    assertEquals(1, kantoSandshrew.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(kantoSandshrew.getAbilities().getStandardAbilityNameIds().contains("sandveil"));
    assertEquals("sandrush", kantoSandshrew.getAbilities().getHiddenAbilityNameId().get());

    assertEquals(1, alolaSandshrew.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(alolaSandshrew.getAbilities().getStandardAbilityNameIds().contains("snowcloak"));
    assertEquals("slushrush", alolaSandshrew.getAbilities().getHiddenAbilityNameId().get());


    // -----
    // Use of 'forme'
    // Same types across formes
    Pokemon landorus = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/landorus/");
    System.out.println(landorus);

    assertNames(new Name("Landorus"), landorus.getName());
    assertTrue(landorus.hasAlternateForms());
    assertEquals(1, landorus.getAlternateForms().size());

    PokemonDetails incarnateLandorus = landorus.getDetails();
    assertEquals("Incarnate Forme", incarnateLandorus.getFormName());
    assertEquals(linkedHashSet(TypeValue.ground, TypeValue.flying), incarnateLandorus.getTypes());

    assertFalse(incarnateLandorus.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(incarnateLandorus.getMoveset().getTmMoves().isEmpty());
    assertTrue(incarnateLandorus.getMoveset().getEggMoves().isEmpty(),
        "Contained egg moves!! " + landorus.getDetails().getMoveset().getEggMoves());

    PokemonDetails therianLandorus = landorus.getAlternateForms().get(0);
    assertEquals("Therian Forme", therianLandorus.getFormName());
    assertEquals(linkedHashSet(TypeValue.ground, TypeValue.flying), therianLandorus.getTypes());
    assertEquals(incarnateLandorus.getMoveset(), therianLandorus.getMoveset());


    assertEquals(1, incarnateLandorus.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(incarnateLandorus.getAbilities().getStandardAbilityNameIds().contains("sandforce"));
    assertEquals("sheerforce", incarnateLandorus.getAbilities().getHiddenAbilityNameId().get());

    assertEquals(1, therianLandorus.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(therianLandorus.getAbilities().getStandardAbilityNameIds().contains("intimidate"));
    assertOptionalEmpty(therianLandorus.getAbilities().getHiddenAbilityNameId());

    // -----
    // Extra form due to ability activation
    Pokemon palafin = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/palafin/");
    System.out.println(palafin);

    assertNames(new Name("Palafin"), palafin.getName());
    assertTrue(palafin.hasAlternateForms());
    assertEquals(1, palafin.getAlternateForms().size());

    PokemonDetails zeroPalafin = palafin.getDetails();
    assertEquals("Zero Form", zeroPalafin.getFormName());
    assertEquals(linkedHashSet(TypeValue.water), zeroPalafin.getTypes());

    PokemonDetails heroPalafin = palafin.getAlternateForms().get(0);
    assertEquals("Hero Form", heroPalafin.getFormName());
    assertEquals(linkedHashSet(TypeValue.water), heroPalafin.getTypes());

    // movesets should be the same between forms
    assertEquals(zeroPalafin.getMoveset(), heroPalafin.getMoveset());
    assertFalse(zeroPalafin.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(zeroPalafin.getMoveset().getTmMoves().isEmpty());
    assertFalse(zeroPalafin.getMoveset().getEggMoves().isEmpty());

    assertEquals(heroPalafin.getAbilities(), zeroPalafin.getAbilities());
    assertEquals(1, heroPalafin.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(heroPalafin.getAbilities().getStandardAbilityNameIds().contains("zerotohero"));
    assertOptionalEmpty(heroPalafin.getAbilities().getHiddenAbilityNameId());

    // -----
    // More than 2 forms
    Pokemon deoxys = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/deoxys/");
    System.out.println(deoxys);

    assertNames(new Name("Deoxys"), deoxys.getName());
    assertTrue(deoxys.hasAlternateForms());
    assertEquals(3, deoxys.getAlternateForms().size());

    PokemonDetails normalDeoxys = deoxys.getDetails();
    assertEquals("Normal Forme", normalDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), normalDeoxys.getTypes());
    assertFalse(normalDeoxys.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(normalDeoxys.getMoveset().getTmMoves().isEmpty());
    assertTrue(normalDeoxys.getMoveset().getEggMoves().isEmpty());
    assertEquals(new BaseStats(50, 150, 50, 150, 50, 150),
        normalDeoxys.getBaseStats());

    PokemonDetails attackDeoxys = deoxys.getAlternateForms().get(0);
    assertEquals("Attack Forme", attackDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), attackDeoxys.getTypes());
    assertFalse(attackDeoxys.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(attackDeoxys.getMoveset().getTmMoves().isEmpty());
    assertTrue(attackDeoxys.getMoveset().getEggMoves().isEmpty());
    assertEquals(new BaseStats(50, 180, 20, 180, 20, 150),
        attackDeoxys.getBaseStats());

    PokemonDetails defenseDeoxys = deoxys.getAlternateForms().get(1);
    assertEquals("Defense Forme", defenseDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), defenseDeoxys.getTypes());
    assertFalse(defenseDeoxys.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(defenseDeoxys.getMoveset().getTmMoves().isEmpty());
    assertTrue(defenseDeoxys.getMoveset().getEggMoves().isEmpty());
    assertEquals(new BaseStats(50, 70, 160, 70, 160, 90),
        defenseDeoxys.getBaseStats());

    PokemonDetails speedDeoxys = deoxys.getAlternateForms().get(2);
    assertEquals("Speed Forme", speedDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), speedDeoxys.getTypes());
    assertFalse(speedDeoxys.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(speedDeoxys.getMoveset().getTmMoves().isEmpty());
    assertTrue(speedDeoxys.getMoveset().getEggMoves().isEmpty());
    assertEquals(new BaseStats(50, 95, 90, 95, 90, 180),
        speedDeoxys.getBaseStats());

    // All forms get thunderbolt
    Pair<MoveAcquire, String> tmThunderbolt = new Pair<>(new MoveAcquire(MoveAcquireType.TM, 126), "thunderbolt");
    assertTrue(deoxys.getDetails().getMoveset().getTmMoves().contains(tmThunderbolt));
    deoxys.getAlternateForms().forEach(form -> assertTrue(form.getMoveset().getTmMoves().contains(tmThunderbolt)));

    // Only defense and speed form get amnesia
    Pair<MoveAcquire, String> tmAmnesia = new Pair<>(new MoveAcquire(MoveAcquireType.TM, 128), "amnesia");
    assertFalse(deoxys.getDetails().getMoveset().getTmMoves().contains(tmAmnesia));
    deoxys.getAlternateForms().forEach(form -> {
      if (form.getFormName().equals("Defense Forme") || form.getFormName().equals("Speed Forme")) {
        assertTrue(form.getMoveset().getTmMoves().contains(tmAmnesia), "Expected " + form.getFormName() + " to learn Amnesia");
      } else {
        assertFalse(form.getMoveset().getTmMoves().contains(tmAmnesia), "Expected " + form.getFormName() + " to not learn Amnesia");
      }

    });


    assertEquals(normalDeoxys.getAbilities(), attackDeoxys.getAbilities());
    assertEquals(normalDeoxys.getAbilities(), defenseDeoxys.getAbilities());
    assertEquals(normalDeoxys.getAbilities(), speedDeoxys.getAbilities());
    assertEquals(1, normalDeoxys.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(normalDeoxys.getAbilities().getStandardAbilityNameIds().contains("pressure"));
    assertOptionalEmpty(normalDeoxys.getAbilities().getHiddenAbilityNameId());

    // -----
    // Initially failing Pokemon - Palkia
    // Wasn't able to parse correctly due to extra 'img' elements in the 'Alternate Forms' section
    Pokemon palkia = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/palkia/");
    System.out.println(palkia);

    assertNames(new Name("Palkia"), palkia.getName());
    assertTrue(palkia.hasAlternateForms());
    assertEquals(1, palkia.getAlternateForms().size());

    PokemonDetails basePalkia = palkia.getDetails();
    assertEquals("", basePalkia.getFormName());
    assertEquals(linkedHashSet(TypeValue.dragon, TypeValue.water), basePalkia.getTypes());

    assertFalse(basePalkia.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(basePalkia.getMoveset().getTmMoves().isEmpty());
    assertTrue(basePalkia.getMoveset().getEggMoves().isEmpty(),
        "Contained egg moves!! " + palkia.getDetails().getMoveset().getEggMoves());

    assertEquals(new BaseStats(90, 120, 100, 150, 120, 100),
        basePalkia.getBaseStats());

    PokemonDetails originPalkia = palkia.getAlternateForms().get(0);
    assertEquals("Origin Forme", originPalkia.getFormName());
    assertEquals(linkedHashSet(TypeValue.dragon, TypeValue.water), originPalkia.getTypes());
    assertEquals(basePalkia.getMoveset(), originPalkia.getMoveset());

    assertEquals(1, basePalkia.getAbilities().getStandardAbilityNameIds().size(), "Abilities: " + basePalkia.getAbilities());
    assertTrue(basePalkia.getAbilities().getStandardAbilityNameIds().contains("pressure"));
    assertEquals("telepathy", basePalkia.getAbilities().getHiddenAbilityNameId().get());

    assertEquals(basePalkia.getAbilities(), originPalkia.getAbilities());

    assertEquals(new BaseStats(90, 100, 100, 150, 120, 120),
        originPalkia.getBaseStats());

    // TODO Ogerpon
  }

  @Test
  public void testRotom() throws Exception {
// -----
    // Initially failing Pokemon - Rotom
    Pokemon rotom = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/rotom/");
    System.out.println(rotom);

    assertNames(new Name("Rotom"), rotom.getName());
    assertTrue(rotom.hasAlternateForms());
    assertEquals(5, rotom.getAlternateForms().size());

    PokemonDetails baseRotom = rotom.getDetails();
    assertEquals("", baseRotom.getFormName());
    assertEquals(linkedHashSet(TypeValue.electric, TypeValue.ghost), baseRotom.getTypes());

    PokemonDetails fanRotom = rotom.getAlternateForms().get(0);
    assertEquals("Fan Rotom", fanRotom.getFormName());
    assertEquals(linkedHashSet(TypeValue.electric, TypeValue.flying), fanRotom.getTypes());

    PokemonDetails frostRotom = rotom.getAlternateForms().get(1);
    assertEquals("Frost Rotom", frostRotom.getFormName());
    assertEquals(linkedHashSet(TypeValue.electric, TypeValue.ice), frostRotom.getTypes());

    PokemonDetails heatRotom = rotom.getAlternateForms().get(2);
    assertEquals("Heat Rotom", heatRotom.getFormName());
    assertEquals(linkedHashSet(TypeValue.electric, TypeValue.fire), heatRotom.getTypes());

    PokemonDetails mowRotom = rotom.getAlternateForms().get(3);
    assertEquals("Mow Rotom", mowRotom.getFormName());
    assertEquals(linkedHashSet(TypeValue.electric, TypeValue.grass), mowRotom.getTypes());

    PokemonDetails washRotom = rotom.getAlternateForms().get(4);
    assertEquals("Wash Rotom", washRotom.getFormName());
    assertEquals(linkedHashSet(TypeValue.electric, TypeValue.water), washRotom.getTypes());

    assertEquals(new BaseStats(50, 50, 77, 95, 77, 91),
        baseRotom.getBaseStats());
  }

  @Test
  public void testRegionalForms() throws IOException {
    Pokemon raichu = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/raichu/");
    System.out.println(raichu);

    PokemonDetails kantonianRaichu = raichu.getDetails();
    assertEquals(linkedHashSet(TypeValue.electric), kantonianRaichu.getTypes());
    assertEquals(kantonianRaichu.getMoveset().getLevelUpMoves().get(0).right, "thunderpunch");
    assertTrue(kantonianRaichu.getMoveset().getTmMoves().stream().map(tm -> tm.right).toList()
        .contains("disarmingvoice"));
    assertFalse(kantonianRaichu.getMoveset().getTmMoves().stream().map(tm -> tm.right).toList()
        .contains("storedpower"));

    assertEquals(1, raichu.getAlternateForms().size());
    PokemonDetails alolanRaichu = raichu.getAlternateForms().get(0);
    assertEquals(linkedHashSet(TypeValue.electric, TypeValue.psychic), alolanRaichu.getTypes());
    assertEquals(alolanRaichu.getMoveset().getLevelUpMoves().get(0).right, "psychic");
    assertFalse(alolanRaichu.getMoveset().getTmMoves().stream().map(tm -> tm.right).toList()
        .contains("disarmingvoice"));
    assertTrue(alolanRaichu.getMoveset().getTmMoves().stream().map(tm -> tm.right).toList()
        .contains("storedpower"));
  }

  @Test
  public void testSplitMultiFormAbilityStrings() {
    MultiFormPokedexScraper scraper = new MultiFormPokedexScraper();

    // Weezing
    String abilitiesRaw = "Abilities: Levitate - Neutralizing Gas - Stench (Hidden) (Normal Forme) - Levitate- Neutralizing Gas - Misty Surge (Hidden) (Galarian Form)";

    List<String> expected = List.of(
        "Levitate - Neutralizing Gas - Stench (Hidden)",
        "Levitate- Neutralizing Gas - Misty Surge (Hidden)"
    );

    List<String> result = scraper.splitUpAbilitiesHeaderStringForMultiFormPokemon(abilitiesRaw);
    assertEquals(expected, result);

    // Ogerpon
    abilitiesRaw = "Abilities: Defiant (Teal Mask) - Water Absorb (Wellspring Mask) - Mold Breaker (Hearthflame Mask) - Sturdy (Cornerstone Mask)";

    expected = List.of(
        "Defiant",
        "Water Absorb",
        "Mold Breaker",
        "Sturdy"
    );

    result = scraper.splitUpAbilitiesHeaderStringForMultiFormPokemon(abilitiesRaw);
    assertEquals(expected, result);
  }

  @Test
  public void testAbilityIdMatchesIdStringInAbilityDex() throws IOException {
    // There's a hyphen in the ability name for Well-Baked Body, so test to make sure an sanitization we are doing
    // is the same across Pokemon and Ability scrapers
    // This can be problematic because we are splitting strings based on hyphen at the time of writing

    Pokemon dachsbun = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/dachsbun/");
    System.out.println(dachsbun.getDetails().getAbilities());

    Ability wellBakedBody = new AbilityDexScraper().extractAbilityInfo("https://www.serebii.net/abilitydex/well-bakedbody.shtml");
    System.out.println(wellBakedBody);

    assertEquals(wellBakedBody.getIdName(), dachsbun.getDetails().getAbilities().getStandardAbilityNameIds().get(0));


    // Test ability with hyphen
    Pokemon ursaluna = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/ursaluna/");
    System.out.println(ursaluna.getDetails().getAbilities());

    Ability mindsEye = new AbilityDexScraper().extractAbilityInfo("https://www.serebii.net/abilitydex/mind'seye.shtml");
    System.out.println(mindsEye);

    assertEquals(mindsEye.getIdName(), ursaluna.getAlternateForms().get(0).getAbilities().getStandardAbilityNameIds().get(0));
  }

  private <A> LinkedHashSet<A> linkedHashSet(A... objects) {
    return new LinkedHashSet<>(Arrays.asList(objects));
  }

  private Map<PokedexRegion, Integer> mapOfNullable(Object... keyAndValues) {
    Map<PokedexRegion, Integer> map = new HashMap<>();

    for (int i = 0; i < keyAndValues.length; i += 2) {
      map.put((PokedexRegion)keyAndValues[i], (Integer)keyAndValues[i + 1]);
    }

    return map;
  }

  private void assertNames(Name expected, Name actual) {
    assertEquals(expected.english, actual.english);
  }

  private void assertPokedex(Map<PokedexRegion, Integer> expectedDex, PokedexNumber pokedexNumber) {
    assertEquals(expectedDex.size(), pokedexNumber.dexNumbers.size(),
        "Pokedex dex entries size did not match. " + expectedDex + ", " + pokedexNumber);

    assertEquals(expectedDex.get(PokedexRegion.National), pokedexNumber.dexNumbers.get(PokedexRegion.National), "National dex mismatch");
    assertEquals(expectedDex.get(PokedexRegion.Blueberry), pokedexNumber.dexNumbers.get(PokedexRegion.Blueberry), "Blueberry dex mismatch");
    assertEquals(expectedDex.get(PokedexRegion.Paldea), pokedexNumber.dexNumbers.get(PokedexRegion.Paldea), "Paldea dex mismatch");
    assertEquals(expectedDex.get(PokedexRegion.Kitakami), pokedexNumber.dexNumbers.get(PokedexRegion.Kitakami), "Kitakami dex mismatch");
  }

}
