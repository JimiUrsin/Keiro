## Testing document
IO testing is focused around reading and writing being consistent. In the case of errors in IO, it would be hard to pin down what's going wrong so rigorous testing of this area is important. 

A\* and JPS are tested to see if they return one of the best possible paths. The grid tested is 10x10 either with obstacles or without. JPS's horizontal, vertical and diagonal searches are tested to see if they return all JumpPoints they're supposed to. 

My performance testing used all PNG files from the moving AI labs (except terrain maps). Unit testing doesn't need any input.



The program can be tested with
```
mvn test
```

Performance tests are run separately with
```
mvn test -Dtest=PerformanceTest
```
Performance testing will run all maps under resources/benchmark/