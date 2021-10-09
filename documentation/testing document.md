## Testing document
IO testing is focused around reading and writing being consistent. In the case of errors in IO, it would be hard to pin down what's going wrong so rigorous testing of this area is important. 

A\* and JPS are tested to see if they return one of the best possible paths. The grid tested is 10x10 either with obstacles or without.

The program can be tested with
```
mvn test
```