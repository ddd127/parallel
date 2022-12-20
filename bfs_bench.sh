git pull --rebase
mvn clean install
sudo nice -n 20 java -Xmx12g -Xms12g -XX:+UseG1GC "$@" -jar bfs-benchmark/target/bfs-benchmarks.jar 2>&1 | tee out.log
