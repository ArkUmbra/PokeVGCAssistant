package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.scraper.Ability;

import java.util.List;

public record AbilitiesDetail(List<Ability> standardAbilities, Ability hiddenAbility) {

}
