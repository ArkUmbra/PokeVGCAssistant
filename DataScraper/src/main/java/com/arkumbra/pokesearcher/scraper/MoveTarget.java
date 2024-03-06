package com.arkumbra.pokesearcher.scraper;

/*
1体選択
自分か味方
味方1体
自分以外
相手全体
味方全体
自分
全体
ランダム1体
全体の場
相手の場
味方の場
不定

 */

public enum MoveTarget {
  SingleSelected,
  SelfOrALly,
  Ally,
  BothOpponentsAndAlly,
  BothOpponents,
  BothAllies,
  Self,
  All,
  Random,
  Team, // Needed??
  Field,
  OpponentsSideOfField,
  AlliesSideOfField,
  Unspecified, // Metal Sound https://www.serebii.net/attackdex-sv/metalburst.shtml https://www.serebii.net/attackdex-sv/counter.shtml

  PreviousOpponent,
}
