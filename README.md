### Quicksort - sequential and parallel versions comparsion

There are two sort implementations in /quicksort module

Requirements for benchmark: maven, java 17, kotlin 1.7.22

run with `mvn clean install` and `java -jar quicksort-benchmark/target/benchmarks.jar`

#### Sample results:

```
Benchmark                                           Mode  Cnt      Score     Error  Units

QuicksortBenchmark.parallelQuicksort              sample   50   6845.943 ± 184.182  ms/op
QuicksortBenchmark.parallelQuicksort·p0.00        sample        5972.689            ms/op
QuicksortBenchmark.parallelQuicksort·p0.50        sample        6928.990            ms/op
QuicksortBenchmark.parallelQuicksort·p0.90        sample        7238.530            ms/op
QuicksortBenchmark.parallelQuicksort·p0.95        sample        7251.532            ms/op
QuicksortBenchmark.parallelQuicksort·p0.99        sample        7566.524            ms/op
QuicksortBenchmark.parallelQuicksort·p0.999       sample        7566.524            ms/op
QuicksortBenchmark.parallelQuicksort·p0.9999      sample        7566.524            ms/op
QuicksortBenchmark.parallelQuicksort·p1.00        sample        7566.524            ms/op

QuicksortBenchmark.sequentialQuicksort            sample   25  20536.655 ± 149.315  ms/op
QuicksortBenchmark.sequentialQuicksort·p0.00      sample       20166.214            ms/op
QuicksortBenchmark.sequentialQuicksort·p0.50      sample       20501.758            ms/op
QuicksortBenchmark.sequentialQuicksort·p0.90      sample       20770.193            ms/op
QuicksortBenchmark.sequentialQuicksort·p0.95      sample       20864.146            ms/op
QuicksortBenchmark.sequentialQuicksort·p0.99      sample       20904.411            ms/op
QuicksortBenchmark.sequentialQuicksort·p0.999     sample       20904.411            ms/op
QuicksortBenchmark.sequentialQuicksort·p0.9999    sample       20904.411            ms/op
QuicksortBenchmark.sequentialQuicksort·p1.00      sample       20904.411            ms/op
```
