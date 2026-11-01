package com.arkumbra.pokesearcher.pokebase;

public record Move(
    String name,
    String effect,
    String power,
    String pp,
    String accuracy,
    String priority
) {
}
