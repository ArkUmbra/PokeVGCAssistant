package com.arkumbra.pokesearcher.neo4jnew;

import com.arkumbra.pokesearcher.model.PokemonWithSpriteRef;
import com.arkumbra.pokesearcher.model.RegulationEligiblePokemon;
import com.arkumbra.pokesearcher.scraper.BattleTimers;

import com.arkumbra.pokesearcher.scraper.Regulation;
import com.arkumbra.pokesearcher.scraper.RegulationRules;
import com.arkumbra.pokesearcher.scraper.Schedule;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.time.ZonedDateTime;
import java.util.*;

import static org.neo4j.driver.Values.parameters;

public class NewNeo4jClient implements AutoCloseable {

  private static final Set<Integer> STANDARD_RESTRICTED_DEX_NUMBERS = Set.of(
      150, // Mewtwo
      249, // Lugia
      250, // Ho-Oh
      382, // Kyogre
      383, // Groudon
      384, // Rayquaza
      483, // Dialga
      484, // Palkia
      487, // Giratina
      643, // Reshiram
      644, // Zekrom
      646, // Kyurem
      716, // Xerneas
      717, // Yveltal
      718, // Zygarde
      789, // Cosmog
      790, // Cosmoem
      791, // Solgaleo
      792, // Lunala
      800, // Necrozma
      888, // Zacian
      889, // Zamazenta
      890, // Eternatus
      898, // Calyrex
      1007, // Koraidon
      1008, // Miraidon
      1024  // Terapagos
  );

  private final Driver driver;

  public NewNeo4jClient(String uri, String user, String password) {
    this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
  }

  @Override
  public void close() throws RuntimeException {
    driver.close();
  }

  public void storeRegulation(Regulation regulation) {
    if (regulation == null || regulation.regulationRules() == null) {
      throw new IllegalArgumentException("Regulation and its rules must not be null");
    }

    RegulationRules rules = regulation.regulationRules();
    String id = rules.getId();
    if (id == null || id.isBlank()) {
      id = (rules.getRegulationName() != null ? rules.getRegulationName() : "regulation")
          .toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-" + UUID.randomUUID().toString().substring(0, 8);
      rules.setId(id);
    }

    String regName = rules.getRegulationName() != null ? rules.getRegulationName() : id;
    String regNameLower = regName.toLowerCase();
    String eligibleUrl = rules.getEligiblePokemonUrl();
    int numOfRestricted = rules.getNumberOfRestricted();
    int setLevel = rules.getSetLevel() != null ? rules.getSetLevel() : 50;
    boolean itemsAllowed = rules.isItemsAllowed();
    boolean duplicateItemsAllowed = rules.isDuplicateItemsAllowed();

    ZonedDateTime startDate = rules.getSchedule() != null ? rules.getSchedule().getStartTime() : null;
    ZonedDateTime endDate = rules.getSchedule() != null ? rules.getSchedule().getEndTime() : null;

    long totalTime = rules.getBattleTimers() != null ? rules.getBattleTimers().getTotalTime() : 0;
    long playerTime = rules.getBattleTimers() != null ? rules.getBattleTimers().getPlayerTime() : 0;
    long previewTime = rules.getBattleTimers() != null ? rules.getBattleTimers().getPreviewTime() : 0;
    long turnTime = rules.getBattleTimers() != null ? rules.getBattleTimers().getTurnTime() : 0;

    if (rules.getBattleTimers() != null) {
      if (totalTime == 0 && rules.getBattleTimers().getTotalTimeDuration() != null) {
        totalTime = rules.getBattleTimers().getTotalTimeDuration().toSeconds();
      }
      if (playerTime == 0 && rules.getBattleTimers().getPlayerTimeDuration() != null) {
        playerTime = rules.getBattleTimers().getPlayerTimeDuration().toSeconds();
      }
      if (previewTime == 0 && rules.getBattleTimers().getPreviewTimeDuration() != null) {
        previewTime = rules.getBattleTimers().getPreviewTimeDuration().toSeconds();
      }
      if (turnTime == 0 && rules.getBattleTimers().getTurnTimeDuration() != null) {
        turnTime = rules.getBattleTimers().getTurnTimeDuration().toSeconds();
      }
    }

    String spriteSheetUrl = (regulation.eligiblePokemon() != null) ? regulation.eligiblePokemon().spriteSheetUrl() : null;

    final String finalId = id;
    final long finalTotalTime = totalTime;
    final long finalPlayerTime = playerTime;
    final long finalPreviewTime = previewTime;
    final long finalTurnTime = turnTime;

    try (var session = driver.session()) {
      session.executeWrite(tx -> {
        // 1. Upsert Regulation node and linked Name node
        String createRegQuery =
            """
            MERGE (r:Regulation {id: $id})
            MERGE (n:Name {english: $regulationName, englishLower: $regulationNameLower})
            MERGE (r)-[:NAMED]->(n)
            SET r.regulationName = $regulationName,
                r.numberOfRestricted = $numOfRestricted,
                r.setLevel = $setLevel,
                r.itemsAllowed = $itemsAllowed,
                r.duplicateItemsAllowed = $duplicateItemsAllowed,
                r.startDate = $startDate,
                r.endDate = $endDate,
                r.totalTime = $totalTime,
                r.playerTime = $playerTime,
                r.previewTime = $previewTime,
                r.turnTime = $turnTime,
                r.eligiblePokemonUrl = $eligiblePokemonUrl,
                r.spriteSheetUrl = $spriteSheetUrl,
                r.representativePokemonId = $representativePokemonId,
                r.representativeSpriteUrl = $representativeSpriteUrl,
                r.representativeSpriteBgSize = $representativeSpriteBgSize,
                r.representativeSpritePosition = $representativeSpritePosition
            """;

        var createRegParams = parameters(
            "id", finalId,
            "regulationName", regName,
            "regulationNameLower", regNameLower,
            "eligiblePokemonUrl", eligibleUrl != null ? eligibleUrl : "",
            "numOfRestricted", numOfRestricted,
            "setLevel", setLevel,
            "itemsAllowed", itemsAllowed,
            "duplicateItemsAllowed", duplicateItemsAllowed,
            "startDate", startDate,
            "endDate", endDate,
            "totalTime", finalTotalTime,
            "playerTime", finalPlayerTime,
            "previewTime", finalPreviewTime,
            "turnTime", finalTurnTime,
            "spriteSheetUrl", spriteSheetUrl != null ? spriteSheetUrl : "",
            "representativePokemonId", rules.getRepresentativePokemonId() != null ? rules.getRepresentativePokemonId() : "",
            "representativeSpriteUrl", (rules.getRepresentativeSpriteUrl() != null && !rules.getRepresentativeSpriteUrl().isBlank())
                ? rules.getRepresentativeSpriteUrl()
                : (spriteSheetUrl != null ? spriteSheetUrl : ""),
            "representativeSpriteBgSize", rules.getRepresentativeSpriteBgSize() != null ? rules.getRepresentativeSpriteBgSize() : "",
            "representativeSpritePosition", rules.getRepresentativeSpritePosition() != null ? rules.getRepresentativeSpritePosition() : ""
        );
        tx.run(createRegQuery, createRegParams);

        // 2. Clear existing PERMITTED relationships to ensure clean update
        String clearExistingPermittedQuery =
            """
            MATCH (r:Regulation {id: $regulationId})-[rel:PERMITTED]->()
            DELETE rel
            """;
        tx.run(clearExistingPermittedQuery, parameters("regulationId", finalId));

        // 3. Link eligible Pokemon from scraped RegulationEligiblePokemon (if available)
        if (regulation.eligiblePokemon() != null && regulation.eligiblePokemon().mons() != null
            && !regulation.eligiblePokemon().mons().isEmpty()) {
          storeScrapedEligiblePokemon(tx, finalId, regulation.eligiblePokemon(), rules);
        }

        return null;
      });
    }
  }

  private void storeScrapedEligiblePokemon(
      TransactionContext tx,
      String regulationId,
      RegulationEligiblePokemon eligiblePokemon,
      RegulationRules rules) {

    // Initialize empty set for rule-based restricted dex numbers (no ValidPokemon support)
    Set<Integer> ruleRestrictedDexNos = new HashSet<>();

    int numOfRestricted = rules.getNumberOfRestricted();
    Set<String> seenKeys = new HashSet<>();
    List<Map<String, Object>> monParamsList = new ArrayList<>();

    for (PokemonWithSpriteRef mon : eligiblePokemon.mons()) {
      String variant = mon.variant() != null ? mon.variant() : "";
      String dedupeKey = mon.dexNumber() + "_" + variant;
      if (!seenKeys.add(dedupeKey)) {
        continue;
      }

      Map<String, Object> item = new HashMap<>();
      item.put("dexNumber", mon.dexNumber());
      item.put("name", mon.name());
      item.put("nameLower", mon.name().toLowerCase());
      String cleanId = mon.name().toLowerCase().replaceAll("[^a-z0-9]", "");
      item.put("idName", cleanId);
      item.put("variant", variant);
      item.put("spriteBgSize", mon.spriteBgSize() != null ? mon.spriteBgSize() : "");
      item.put("spritePosition", mon.spritePosition() != null ? mon.spritePosition() : "");
      item.put("imageUrl", mon.imageUrl() != null ? mon.imageUrl() : "");

      boolean isRestricted = false;
      if (!ruleRestrictedDexNos.isEmpty()) {
        isRestricted = ruleRestrictedDexNos.contains(mon.dexNumber());
      } else if (numOfRestricted > 0) {
        isRestricted = STANDARD_RESTRICTED_DEX_NUMBERS.contains(mon.dexNumber());
      }
      item.put("permittedLabel", isRestricted ? "restricted" : "nonRestricted");
      monParamsList.add(item);
    }

    String linkMatchedQuery =
        """
        MATCH (r:Regulation {id: $regulationId})
        UNWIND $pokemonList AS item
        OPTIONAL MERGE (p1:Pokemon)-[d:IN_DEX]->(:PokedexRegion {label: 'National'})
          WHERE d.num = item.dexNumber
        OPTIONAL MERGE (p2:Pokemon)-[:NAMED]->(n:Name)
          WHERE n.englishLower = item.nameLower
        WITH r, item, coalesce(p1, p2) AS p
        WHERE p IS NOT NULL
        MERGE (r)-[rel:PERMITTED {variant: item.variant}]->(p)
        SET rel.label = item.permittedLabel,
            rel.dexNumber = item.dexNumber,
            rel.spriteBgSize = item.spriteBgSize,
            rel.spritePosition = item.spritePosition,
            rel.imageUrl = item.imageUrl
        RETURN collect(distinct item.dexNumber) AS linkedDexNumbers
        """;

    String createAndLinkQuery =
        """
        MATCH (r:Regulation {id: $regulationId})
        UNWIND $pokemonList AS item
        MERGE (n:Name {english: item.name, englishLower: item.nameLower})
        MERGE (p:Pokemon {id: item.idName})
        MERGE (p)-[:NAMED]->(n)
        MERGE (pNat:PokedexRegion {label: 'National'})
        MERGE (p)-[:IN_DEX {num: item.dexNumber}]->(pNat)
        MERGE (r)-[rel:PERMITTED {variant: item.variant}]->(p)
        SET rel.label = item.permittedLabel,
            rel.dexNumber = item.dexNumber,
            rel.spriteBgSize = item.spriteBgSize,
            rel.spritePosition = item.spritePosition,
            rel.imageUrl = item.imageUrl
        """;

    int batchSize = 200;
    for (int i = 0; i < monParamsList.size(); i += batchSize) {
      List<Map<String, Object>> batch = monParamsList.subList(i, Math.min(i + batchSize, monParamsList.size()));

      var batchParams = parameters("regulationId", regulationId, "pokemonList", batch);
      var result = tx.run(linkMatchedQuery, batchParams);

      Set<Integer> linkedDexNumbers = new HashSet<>();
      if (result.hasNext()) {
        Record record = result.next();
        List<Integer> list = record.get("linkedDexNumbers").asList(Value::asInt);
        linkedDexNumbers.addAll(list);
      }

      // Check if any items in this batch could not be matched to an existing Pokemon
      List<Map<String, Object>> unmatched = new ArrayList<>();
      for (Map<String, Object> item : batch) {
        int dexNum = (Integer) item.get("dexNumber");
        if (!linkedDexNumbers.contains(dexNum)) {
          unmatched.add(item);
        }
      }

      if (!unmatched.isEmpty()) {
        tx.run(createAndLinkQuery, parameters("regulationId", regulationId, "pokemonList", unmatched));
      }
    }
  }

  private List<Integer> nullToEmptyList(List<Integer> list) {
    return list != null ? list : Collections.emptyList();
  }

  /**
   * Returns a full Regulation (rules + eligible pokemon) by its regulation ID.
   * Returns null if no regulation with that ID exists.
   */
  public Regulation getRegulationById(String id) {
    String rulesQuery =
        """
        MATCH (r:Regulation {id: $id})
        MATCH (r)-[:NAMED]->(n:Name)
        OPTIONAL MATCH (r)-[repRel:PERMITTED]->(rep:Pokemon)-[:NAMED]->(repName:Name)
        WHERE (
          (r.representativePokemonId CONTAINS '-'
           AND repName.english = split(r.representativePokemonId, '-')[0]
           AND repRel.variant = split(r.representativePokemonId, '-')[1])
          OR
          (NOT r.representativePokemonId CONTAINS '-'
           AND repName.english = r.representativePokemonId
           AND (repRel.variant = '' OR repRel.variant IS NULL))
        )
        RETURN r.id, n.english, r.startDate, r.endDate,
               r.numberOfRestricted, r.setLevel, r.itemsAllowed, r.duplicateItemsAllowed,
               r.totalTime, r.playerTime, r.previewTime, r.turnTime,
               r.eligiblePokemonUrl, coalesce(r.spriteSheetUrl, ''),
               r.representativePokemonId,
               coalesce(r.representativeSpriteUrl, r.spriteSheetUrl, '') AS representativeSpriteUrl,
               coalesce(repRel.spriteBgSize, r.representativeSpriteBgSize, '') AS repSpriteBgSize,
               coalesce(repRel.spritePosition, r.representativeSpritePosition, '') AS repSpritePosition
        """;

    String pokemonQuery =
        """
        MATCH (r:Regulation {id: $id})-[rel:PERMITTED]->(p:Pokemon)
        MATCH (p)-[:NAMED]->(n:Name)
        OPTIONAL MATCH (p)-[d:IN_DEX]->(:PokedexRegion {label: 'National'})
        RETURN p.id, n.english, coalesce(rel.dexNumber, d.num, -1) AS dexNumber,
               rel.label, rel.variant, rel.spriteBgSize, rel.spritePosition, rel.imageUrl
        ORDER BY dexNumber, n.english, rel.variant
        """;

    var params = parameters("id", id);

    try (var session = driver.session()) {
      return session.executeRead(tx -> {
        var rulesResult = tx.run(rulesQuery, params);
        if (!rulesResult.hasNext()) {
          return null;
        }

        Record rulesRecord = rulesResult.next();

        String regId         = rulesRecord.get(0).asString();
        String regName       = rulesRecord.get(1).asString();
        ZonedDateTime start  = rulesRecord.get(2).isNull() ? null : rulesRecord.get(2).asZonedDateTime();
        ZonedDateTime end    = rulesRecord.get(3).isNull() ? null : rulesRecord.get(3).asZonedDateTime();
        int numRestricted    = rulesRecord.get(4).asInt(0);
        int setLevel         = rulesRecord.get(5).asInt(50);
        boolean itemsAllowed = rulesRecord.get(6).asBoolean(true);
        boolean dupItems     = rulesRecord.get(7).asBoolean(true);
        long totalTime       = rulesRecord.get(8).asLong(0);
        long playerTime      = rulesRecord.get(9).asLong(0);
        long previewTime     = rulesRecord.get(10).asLong(0);
        long turnTime        = rulesRecord.get(11).asLong(0);
        String eligibleUrl   = rulesRecord.get(12).asString("");
        String spriteSheetUrl = rulesRecord.get(13).asString("");

        Schedule schedule =
            (start != null || end != null) ? new Schedule(start, end) : null;
        BattleTimers battleTimers =
            new BattleTimers(totalTime, playerTime, previewTime, turnTime);

        RegulationRules rules = new RegulationRules(
            regId, regName, eligibleUrl, schedule,
            numRestricted, setLevel, itemsAllowed, dupItems, battleTimers
        );

        String repPokemonId = rulesRecord.get(14).isNull() ? "" : rulesRecord.get(14).asString("");
        String repSpriteUrl = rulesRecord.get(15).isNull() ? "" : rulesRecord.get(15).asString("");
        String repBgSize    = rulesRecord.get(16).isNull() ? "" : rulesRecord.get(16).asString("");
        String repPosition  = rulesRecord.get(17).isNull() ? "" : rulesRecord.get(17).asString("");

        rules.setRepresentativePokemonId(repPokemonId);
        rules.setRepresentativeSpriteUrl(repSpriteUrl);
        rules.setRepresentativeSpriteBgSize(repBgSize);
        rules.setRepresentativeSpritePosition(repPosition);

        // Build eligible pokemon list from PERMITTED relationships
        List<PokemonWithSpriteRef> mons = new ArrayList<>();
        var pokemonResult = tx.run(pokemonQuery, params);
        while (pokemonResult.hasNext()) {
          Record r = pokemonResult.next();
          String pokemonId    = r.get(0).isNull() ? "" : r.get(0).asString("");
          String name         = r.get(1).asString("");
          int dexNumber       = r.get(2).asInt(-1);
          String label        = r.get(3).isNull() ? "" : r.get(3).asString("");
          String variant      = r.get(4).isNull() ? "" : r.get(4).asString("");
          String spriteBgSize = r.get(5).isNull() ? "" : r.get(5).asString("");
          String spritePos    = r.get(6).isNull() ? "" : r.get(6).asString("");
          String imageUrl     = r.get(7).isNull() ? "" : r.get(7).asString("");

          boolean isRestricted = "restricted".equalsIgnoreCase(label);
          String monImageUrl = (imageUrl != null && !imageUrl.isBlank()) ? imageUrl : spriteSheetUrl;

          mons.add(new PokemonWithSpriteRef(
              dexNumber, name, variant, spriteBgSize, spritePos, monImageUrl, pokemonId, isRestricted
          ));
        }

        // Fallback to resolve representative sprite from mons if not fully populated
        if ((rules.getRepresentativeSpriteBgSize() == null || rules.getRepresentativeSpriteBgSize().isBlank()
            || rules.getRepresentativeSpritePosition() == null || rules.getRepresentativeSpritePosition().isBlank())
            && rules.getRepresentativePokemonId() != null && !rules.getRepresentativePokemonId().isBlank()) {
          for (PokemonWithSpriteRef mon : mons) {
            String monKey = (mon.variant() != null && !mon.variant().isBlank())
                ? mon.name() + "-" + mon.variant()
                : mon.name();
            if (monKey.equalsIgnoreCase(rules.getRepresentativePokemonId()) || mon.name().equalsIgnoreCase(rules.getRepresentativePokemonId())) {
              if (rules.getRepresentativeSpriteBgSize() == null || rules.getRepresentativeSpriteBgSize().isBlank()) {
                rules.setRepresentativeSpriteBgSize(mon.spriteBgSize());
              }
              if (rules.getRepresentativeSpritePosition() == null || rules.getRepresentativeSpritePosition().isBlank()) {
                rules.setRepresentativeSpritePosition(mon.spritePosition());
              }
              if (rules.getRepresentativeSpriteUrl() == null || rules.getRepresentativeSpriteUrl().isBlank()) {
                rules.setRepresentativeSpriteUrl(spriteSheetUrl);
              }
              break;
            }
          }
        }

        RegulationEligiblePokemon eligiblePokemon =
            new RegulationEligiblePokemon(mons, spriteSheetUrl);

        return new Regulation(rules, eligiblePokemon);
      });
    }
  }

  /**
   * Returns a summary list of all stored regulations, ordered by start date descending
   * (most recent first). Each summary includes id, name, start/end date, and total pokemon count.
   */
  public List<RegulationSummary> getAllRegulationSummaries() {
    String query =
        """
        MATCH (r:Regulation)
        MATCH (r)-[:NAMED]->(n:Name)
        OPTIONAL MATCH (r)-[:PERMITTED]->(p:Pokemon)
        OPTIONAL MATCH (r)-[repRel:PERMITTED]->(rep:Pokemon)-[:NAMED]->(repName:Name)
        WHERE (
          (r.representativePokemonId CONTAINS '-'
           AND repName.english = split(r.representativePokemonId, '-')[0]
           AND repRel.variant = split(r.representativePokemonId, '-')[1])
          OR
          (NOT r.representativePokemonId CONTAINS '-'
           AND repName.english = r.representativePokemonId
           AND (repRel.variant = '' OR repRel.variant IS NULL))
        )
        RETURN r.id,
               n.english,
               r.startDate,
               r.endDate,
               count(distinct p) AS pokemonCount,
               r.representativePokemonId,
               coalesce(r.representativeSpriteUrl, r.spriteSheetUrl, '') AS representativeSpriteUrl,
               coalesce(repRel.spriteBgSize, r.representativeSpriteBgSize, '') AS repSpriteBgSize,
               coalesce(repRel.spritePosition, r.representativeSpritePosition, '') AS repSpritePosition
        ORDER BY r.startDate DESC
        """;

    try (var session = driver.session()) {
      return session.executeRead(tx -> {
        List<RegulationSummary> summaries = new ArrayList<>();
        var result = tx.run(query);
        while (result.hasNext()) {
          Record record = result.next();
          String id           = record.get(0).asString("");
          String name         = record.get(1).asString("");
          ZonedDateTime start = record.get(2).isNull() ? null : record.get(2).asZonedDateTime();
          ZonedDateTime end   = record.get(3).isNull() ? null : record.get(3).asZonedDateTime();
          int pokemonCount    = record.get(4).asInt(0);
          String representativePokemonId = record.get(5).isNull() ? "" : record.get(5).asString("");
          String representativeSpriteUrl = record.get(6).isNull() ? "" : record.get(6).asString("");
          String representativeSpriteBgSize = record.get(7).isNull() ? "" : record.get(7).asString("");
          String representativeSpritePosition = record.get(8).isNull() ? "" : record.get(8).asString("");
          summaries.add(new RegulationSummary(id, name, start, end, pokemonCount, representativePokemonId, representativeSpriteUrl, representativeSpriteBgSize, representativeSpritePosition));
        }
        return summaries;
      });
    }
  }

  /**
   * Deletes a regulation by its ID, removing the regulation node, its PERMITTED connections
   * to eligible Pokemon, its NAMED connection, and any orphaned Name node if it is not
   * referenced by any other entity. Other regulations and Pokemon nodes remain unaffected.
   *
   * @param id The unique regulation ID to delete
   * @return true if a regulation was deleted, false otherwise
   */
  public boolean deleteRegulation(String id) {
    if (id == null || id.isBlank()) {
      return false;
    }

    String query =
        """
        MATCH (r:Regulation {id: $id})
        OPTIONAL MATCH (r)-[:NAMED]->(n:Name)
        OPTIONAL MATCH (other)-[:NAMED]->(n) WHERE other <> r
        WITH r, n, count(other) AS otherCount
        DETACH DELETE r
        WITH n, otherCount
        WHERE n IS NOT NULL AND otherCount = 0
        DELETE n
        """;

    try (var session = driver.session()) {
      return session.executeWrite(tx -> {
        var result = tx.run(query, parameters("id", id));
        var summary = result.consume();
        return summary.counters().nodesDeleted() > 0;
      });
    }
  }

}
