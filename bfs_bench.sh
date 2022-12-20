git pull --rebase
mvn clean install
sudo nice -n 20 java -Xmx4g -Xms4g -XX:+UseZGC "$@" -jar bfs-benchmark/target/bfs-benchmarks.jar 2>&1 | tee out.log
