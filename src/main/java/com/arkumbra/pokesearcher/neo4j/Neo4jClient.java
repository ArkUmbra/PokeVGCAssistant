package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.scraper.*;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.ClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

public class Neo4jClient implements AutoCloseable {

  private final Driver driver;

  public Neo4jClient(String uri, String user, String password) {
    driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
  }

  @Override
  public void close() throws RuntimeException {
    driver.close();
  }

  public void storeMove(Move move) {
    try (var session = driver.session()) {
      String createMoveDetails =
          "MATCH (tv:TypeValue {label:$typeValue}) \n" +
          "MATCH (ac:AttackCategory {label:$attackCategory}) \n" +
          "MATCH (pr:Priority {label:$priority}) \n" +
          "MATCH (mt:MoveTarget {label:$moveTarget}) \n" +
          "MERGE (n:Name {english:$english, japanese:$japanese}) \n" +
          "MERGE (m:Move {id:$moveId, description:$description, pp:$pp, power:$power, critRatio:$critRatio, effectChance:$effectChance}) \n" +
          "MERGE (m)-[:NAMED]->(n) \n" +
          "MERGE (m)-[:TARGETS]->(mt) \n" +
          "MERGE (m)-[:WITH_PRIORITY]->(pr) \n" +
          "MERGE (m)-[:OF_CATEGORY]->(ac) \n" +
          "MERGE (m)-[:OF_TYPE]->(tv) \n";
      var createMoveDetailsParams = parameters(
          "typeValue", move.getType().name(),
          "attackCategory", move.getCategory().name(),
          "priority", move.getPriority().name(),
          "moveTarget", move.getMoveTarget().name(),
          "english", move.getName().getEnglishName(),
          "japanese", nullToBlank(move.getName().getJapanese()),
          "moveId", nullToBlank(move.getIdName()),
          "pp", move.getPp(),
          "power", move.getPower(),
          "critRatio", move.getCritRatio().toString(),
          "effectChance", move.getEffectChance().getChance(),
          "description", move.getIngameDescription()
      );

      if (move.getAttackMetaData().isEmpty()) {
        executeQuery(session, createMoveDetails, createMoveDetailsParams);
        return;
      }

      // Find existing metadata node and link ability to that
      String linkMoveToMetaData =
          "UNWIND $metadataList AS metadata\n" +
              "MATCH (a:Move {id: $moveId}) \n" +
              "MATCH (m:AttackMetaData {label: metadata}) \n" +
              "CREATE (a)-[:WITH_BEHAVIOUR]->(m)";
      var linkMoveToMetaDataParams = parameters(
          "moveId", move.getIdName(),
          "metadataList", move.getAttackMetaData().stream().map(Enum::name).toList()
      );


      executeQuery(session,
          createMoveDetails, createMoveDetailsParams,
          linkMoveToMetaData, linkMoveToMetaDataParams);

    }
  }

  public void storeAbility(Ability ability) {
    try (var session = driver.session()) {

      String createAbilityLinkName =
          "MERGE (n:Name {english:$english, japanese:$japanese}) \n" +
          "MERGE (a:Ability {idString: $abilityId, description: $description}) \n" +
          "MERGE (a)-[:NAMED]->(n)";
      var createAbilityLinkNameParams = parameters(
          "english", ability.getName().getEnglishName(),
          "japanese", nullToBlank(ability.getName().getJapanese()),
          "abilityId", ability.getIdName(),
          "description", ability.getIngameDescription()
      );


      if (ability.getMetaData().isEmpty()) {
        executeQuery(session, createAbilityLinkName, createAbilityLinkNameParams);
        return;
      }

      // Find existing metadata node and link ability to that
      String linkAbilityToMetaData =
          "UNWIND $metadataList AS metadata\n" +
              "MATCH (a:Ability {idString: $abilityId}) \n" +
              "MATCH (m:AbilityMetaData {label: metadata}) \n" +
              "CREATE (a)-[:WITH_BEHAVIOUR]->(m)";
      var linkAbilityToMetaDataParams = parameters(
          "abilityId", ability.getIdName(),
          "metadataList", ability.getMetaData().stream().map(a -> a.name()).toList()
      );


      executeQuery(session,
          createAbilityLinkName, createAbilityLinkNameParams,
          linkAbilityToMetaData, linkAbilityToMetaDataParams);
    }
  }

  public void writeJapaneseName(Map<String, String> englishNameToJapaneseName) {
    try (var session = driver.session()) {

      Object[] queryAndParams = new Object[englishNameToJapaneseName.size() * 2];

      int i = 0;
      for (Map.Entry<String, String> englishJapaneseEntry : englishNameToJapaneseName.entrySet()) {
        queryAndParams[i] =
            "MATCH (a:Name {english: $english}) \n" +
            "SET a.japanese = $japanese";

        queryAndParams[i + 1] = parameters(
            "english", englishJapaneseEntry.getKey(),
            "japanese", englishJapaneseEntry.getValue()
        );
        i += 2;
      }

      executeQuery(session, queryAndParams);
    }
  }

  private String nullToBlank(String value) {
    return value == null ? "" : value;
  }

  private void executeQuery(Session session, Object... queryStringAndParameterPairs) {
    if (queryStringAndParameterPairs.length % 2 != 0)
      throw new IllegalArgumentException("Must have proper pairings of query string and params");

    session.executeWrite(tx -> {
      for (int i = 0; i < queryStringAndParameterPairs.length; i += 2) {
        String queryString = (String)queryStringAndParameterPairs[i];
        Value parameters = (Value)queryStringAndParameterPairs[i + 1];

        try {
          var query = new Query(queryString, parameters);
          tx.run(query);
        } catch (ClientException e) {
          System.err.println("Failed to execute the following query " + queryString + ", " + parameters);
          throw e;
        }
      }
      return null;
    });
  }

  private Value toParameters(Name name) {
    return parameters(
        "english", name.getEnglishName(),
        "japanese", name.getJapanese()
    );
  }

  public int getNumberOfAbilities() {
    try (var session = driver.session()) {
      var allAbilities = session.executeWrite(tx -> {
        var query = new Query("MATCH (a:Ability) RETURN a");
        var result = tx.run(query);
        return result.list();
      });
      return allAbilities.size();
    }
  }

  public void initializeConstantEnumDataToDatabase() {
    storeSimpleEnum(AbilityMetaData.values(), "AbilityMetaData");
    storeSimpleEnum(TypeValue.values(), "TypeValue");
    storeSimpleEnum(AttackCategory.values(), "AttackCategory");
    storeSimpleEnum(Priority.values(), "Priority");
    storeSimpleEnum(MoveTarget.values(), "MoveTarget");
    storeSimpleEnum(AttackMetaData.values(), "AttackMetaData");
  }

  private void storeSimpleEnum(Enum<?>[] values, String graphObjectName) {
    try (var session = driver.session()) {
      for (Enum<?> value : values) {
        session.executeWrite(tx -> {
          var query = new Query("MERGE (v:" + graphObjectName + "{label:$label})",
              parameters("label", value.name()));
          tx.run(query);
          return null;
        });
      }
    }
  }

}
