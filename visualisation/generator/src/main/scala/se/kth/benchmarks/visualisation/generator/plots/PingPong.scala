package se.kth.benchmarks.visualisation.generator.plots

import se.kth.benchmarks.visualisation.generator.{
  BenchmarkData,
  ImplGroupedResult,
  JsRaw,
  JsString,
  PlotData,
  PlotGroup,
  Series
}
import se.kth.benchmarks.runner.{Benchmark, BenchmarkWithSpace, ParameterSpacePB}
import kompics.benchmarks.benchmarks._

object PingPong {

  type Params = PingPongRequest;

  def plot(data: BenchmarkData[String]): PlotGroup = {
    val bench = data.benchmark.asInstanceOf[BenchmarkWithSpace[Params]];
    val space = bench.space.asInstanceOf[ParameterSpacePB[Params]];
    val dataParams = data.mapParams(space.paramsFromCSV);
    val params = dataParams.paramSeries(_.numberOfMessages);
    val groupedSeries = dataParams.results.mapValues(_.map2D(_.numberOfMessages, params));
    val groupedErrorSeries = dataParams.results.mapValues(_.map2DErrorBars(_.numberOfMessages, params));
    val mergedSeries = (for (key <- groupedSeries.keys) yield {
      (key, groupedSeries(key), groupedErrorSeries(key))
    }).toList;
    val sortedSeries: List[Series] = mergedSeries.sortBy(_._1).map(t => List[Series](t._2, t._3)).flatten;
    val pimpedSeries: List[Series] = sortedSeries.map(
      _.addMeta(
        "tooltip" -> JsRaw("{valueDecimals: 2, valueSuffix: 'ms'}")
      )
    );
    val paramsS = params.map(_.toString);
    val plotid = s"${bench.symbol.toLowerCase()}-number-of-messages";
    val plot = PlotData(
      id = plotid,
      title = s"${bench.name} (Execution Time)",
      xAxisLabel = "total number of messages",
      xAxisCategories = paramsS,
      yAxisLabel = "total execution time (ms)",
      seriesData = pimpedSeries
    );
    PlotGroup.Single(plot)
  }
}
