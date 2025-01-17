package ra.analysis.rushing.data

trait RushRange {
  def range: YardRange
  def csvString: String
}

case class AnalyzedRushRange(
  range:       YardRange,
  averageRush: Double,
  numRushes:   Int,
  numTds:      Int
) extends RushRange {
  override def toString: String = s"{" +
    s"range: ${range.rangeString}" +
    s", averageRush: $averageRush" +
    s", rushes: $numRushes" +
    s", touchdowns: $numTds" +
    s"}"
  def csvString: String = s"${range.rangeString},$averageRush,$numRushes,$numTds"
}

case class NormalizedRushRange(
  range:               YardRange,
  numRushes:           Int,
  avgRush:             Double,
  numTds:              Int,
  normalizedAvgRush:   Double,
  normalizedNumRushes: Double,
  normalizedTdRate:    Double,
  normalizedNumTds:    Double
) extends RushRange {
  override def toString: String = s"{" +
    s"range: ${range.rangeString}" +
    s", nAverageRush: $normalizedAvgRush" +
    s", nRushes: $normalizedNumRushes" +
    s", nTouchdownRate: $normalizedTdRate" +
    s", nTouchdowns: $normalizedNumTds" +
    s"}"
  def csvString: String = s"${range.rangeString},$numRushes,$avgRush,$numTds,$normalizedAvgRush,$normalizedNumRushes,$normalizedTdRate,$normalizedNumTds"
}

object AnalyzedRushRange {

  def normalizeMetricToAverage(
    playerStat: Double,
    avgStat:    Double,
    offset:     Double
  ): Double = {
    // Offset is used here to translate the absence of a player stat to 0.
    offset + ( playerStat - avgStat ) / avgStat
  }

  def produceComparisonToAverage(
    numPlayers: Int,
    offset:     Double
  )(
    playerRange:  AnalyzedRushRange,
    averageRange: AnalyzedRushRange
  ): NormalizedRushRange = {

    require( playerRange.range == averageRange.range )

    val avgAverageRush = averageRange.averageRush
    val avgTouchdownRate = averageRange.numTds.toDouble / averageRange.numRushes

    val avgNumRushes = averageRange.numRushes.toDouble / numPlayers
    val avgNumTouchdowns = averageRange.numTds.toDouble / numPlayers

    val playerAverageRush = playerRange.averageRush
    val playerTouchdownRate = playerRange.numTds.toDouble / playerRange.numRushes

    val playerNumRushes = playerRange.numRushes.toDouble
    val playerNumTouchdowns = playerRange.numTds.toDouble

    NormalizedRushRange(
      playerRange.range,
      playerRange.numRushes,
      playerRange.averageRush,
      playerRange.numTds,
      normalizeMetricToAverage( playerAverageRush, avgAverageRush, offset ),
      normalizeMetricToAverage( playerNumRushes, avgNumRushes, offset ),
      normalizeMetricToAverage( playerTouchdownRate, avgTouchdownRate, offset ),
      normalizeMetricToAverage( playerNumTouchdowns, avgNumTouchdowns, offset )
    )
  }

}