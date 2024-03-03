package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.scraper.Ability;
import com.arkumbra.pokesearcher.scraper.AbilityMetaData;
import com.arkumbra.pokesearcher.scraper.Name;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.ClientException;

import java.util.ArrayList;
import java.util.List;

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

  public void storeAbility(Ability ability) {
    try (var session = driver.session()) {

      String createAbilityLinkName =
          "MERGE (n:Name {english:$english, japanese:$japanese}) \n" +
          "MERGE (a:Ability {idString: $abilityId, description: $description}) \n" +
          "MERGE (a)-[:NAMED]->(n)";
      var createAbilityLinkNameParams = parameters(
          "english", ability.getName().getEnglishName(),
          "japanese", ability.getName().getJapanese(),
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
      return;
    }
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

  public void printGreeting(final String message) {
    try (var session = driver.session()) {
      var greeting = session.executeWrite(tx -> {
        var query = new Query("CREATE (a:Greeting) SET a.message = $message RETURN a.message + ', from node ' + id(a)", parameters("message", message));
        var result = tx.run(query);
        return result.single().get(0).asString();
      });
      System.out.println(greeting);
    }
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

  public void initializeAbilityMetadata() {
    try (var session = driver.session()) {
      for (AbilityMetaData abilityMetaData : AbilityMetaData.values()) {
        session.executeWrite(tx -> {
          var query = new Query("MERGE (a:AbilityMetaData{label:$label})",
              parameters("label", abilityMetaData.name()));
          tx.run(query);
          return null;
        });
      }
    }
  }

}
