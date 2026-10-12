package com.arkumbra.pokesearcher.scraper;

import com.arkumbra.pokesearcher.model.RegulationEligiblePokemon;

public record Regulation(
    RegulationRules regulationRules,
    RegulationEligiblePokemon eligiblePokemon
) { }
