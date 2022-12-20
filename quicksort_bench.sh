git pull --rebase
mvn clean install
sudo nice -n 20 java -Xmx10g -Xms10g "$@" -jar quicksort-benchmark/target/quicksort-benchmarks.jar 2>&1 | tee out.log
