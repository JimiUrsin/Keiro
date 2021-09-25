## Testing document
IO testing is focused around reading and writing being consistent. In the case of errors in IO, it would be hard to pin down what's going wrong so rigorous testing of this area is important. 

Pathing testing is currently only testing whether or not the A star algorithm returns the correct path length in various situations. Its input is a 10x10 grid either with obstacles or without.

The program can be tested with
```
mvn test
```