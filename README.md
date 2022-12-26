### Quicksort - sequential and parallel versions comparsion

There are two sort implementations in /bfs module

Requirements for benchmark: maven, java 19, kotlin 1.7.22

run with `mvn clean install` and `java -jar bfs-benchmark/target/bfs-benchmarks.jar`

#### Sample results:

```
# JMH version: 1.36
# VM version: JDK 19.0.1, OpenJDK 64-Bit Server VM, 19.0.1+10-21
# VM options: -XX:+UseZGC -XX:SoftMaxHeapSize=1g -Xmx4g -Xms4g -Dbfs.benchmark.graph.size=700 -Dfile.encoding=UTF-8
# Blackhole mode: compiler (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)

Benchmark                                           Mode  Cnt            Score              Error   Units

BfsBenchmark.parallelBfs                          sample    5        26917.365 ±         4250.471   ms/op
BfsBenchmark.parallelBfs:parallelBfs·p0.00        sample             26105.348                      ms/op
BfsBenchmark.parallelBfs:parallelBfs·p0.50        sample             26239.566                      ms/op
BfsBenchmark.parallelBfs:parallelBfs·p0.90        sample             28588.376                      ms/op
BfsBenchmark.parallelBfs:parallelBfs·p0.95        sample             28588.376                      ms/op
BfsBenchmark.parallelBfs:parallelBfs·p0.99        sample             28588.376                      ms/op
BfsBenchmark.parallelBfs:parallelBfs·p0.999       sample             28588.376                      ms/op
BfsBenchmark.parallelBfs:parallelBfs·p0.9999      sample             28588.376                      ms/op
BfsBenchmark.parallelBfs:parallelBfs·p1.00        sample             28588.376                      ms/op
BfsBenchmark.parallelBfs:·gc.alloc.rate           sample    5         1787.374 ±         3872.503  MB/sec
BfsBenchmark.parallelBfs:·gc.alloc.rate.norm      sample    5  50766849776.000 ± 109276460509.239    B/op
BfsBenchmark.parallelBfs:·gc.count                sample    5         5456.000                     counts
BfsBenchmark.parallelBfs:·gc.time                 sample    5        15130.000                         ms
BfsBenchmark.parallelBfs:·jfr                     sample                   NaN                        ---

BfsBenchmark.sequentialBfs                        sample    5        66303.558 ±        29980.843   ms/op
BfsBenchmark.sequentialBfs:sequentialBfs·p0.00    sample             53351.547                      ms/op
BfsBenchmark.sequentialBfs:sequentialBfs·p0.50    sample             67175.973                      ms/op
BfsBenchmark.sequentialBfs:sequentialBfs·p0.90    sample             73819.750                      ms/op
BfsBenchmark.sequentialBfs:sequentialBfs·p0.95    sample             73819.750                      ms/op
BfsBenchmark.sequentialBfs:sequentialBfs·p0.99    sample             73819.750                      ms/op
BfsBenchmark.sequentialBfs:sequentialBfs·p0.999   sample             73819.750                      ms/op
BfsBenchmark.sequentialBfs:sequentialBfs·p0.9999  sample             73819.750                      ms/op
BfsBenchmark.sequentialBfs:sequentialBfs·p1.00    sample             73819.750                      ms/op
BfsBenchmark.sequentialBfs:·gc.alloc.rate         sample    5          589.209 ±         1064.934  MB/sec
BfsBenchmark.sequentialBfs:·gc.alloc.rate.norm    sample    5  42788172540.800 ±  80197395209.586    B/op
BfsBenchmark.sequentialBfs:·gc.count              sample    5        13324.000                     counts
BfsBenchmark.sequentialBfs:·gc.time               sample    5        95428.000                         ms
BfsBenchmark.sequentialBfs:·jfr                   sample                   NaN                        ---
```


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
