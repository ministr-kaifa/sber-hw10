package ru.zubkoff.sber;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class FactorialTest {
  
  @Test
  void givenCorrectCalculationResult_whenFactorialCalculate_thenResultsAreSame() {
    //given
    var expectedOuts = IntStream.of(new int[]{
        1, 1, 2, 6, 24, 120, 720, 5_040, 40_320,
        362_880, 3_628_800, 39_916_800,
        479_001_600})
      .mapToObj(n -> BigInteger.valueOf(n))
      .toArray();

    //when
    var results = 
      LongStream.range(0, expectedOuts.length)
        .mapToObj(Factorial::calculate)
        .toArray();
    
    //then
    assertArrayEquals(
      expectedOuts, 
      results
    );
  }
  
  @Test
  @RepeatedTest(1_000)
  void givenSingleThreadCalculationResult_whenParallelCalculationResult_thenResultsAreSame() throws InterruptedException {
    //given
    var input = LongStream.rangeClosed(1, 50).mapToObj(Long::valueOf).toList();
    
    //when
    var singleThreadResult = Factorial.calculateAll(input);
    var parallelResult = Factorial.parallelCalculateAll(input);
    
    //then
    assertArrayEquals(singleThreadResult.toArray(), parallelResult.toArray());
  }

  @Test
  @RepeatedTest(1_000)
  void givenSingleThreadCalculationResultFromFile_whenParallelCalculationResultFromFile_thenResultsAreSame() throws InterruptedException, URISyntaxException, IOException {
    //given
    var input = LongStream.rangeClosed(1, 50).mapToObj(String::valueOf).toList();
    var inputFile = Files.writeString(
      Path.of("input.txt"),
      String.join(" ", input),
      StandardOpenOption.CREATE);
    
      
    //when
    var singleThreadResult = Factorial.calculateAll(inputFile);
    var parallelResult = Factorial.parallelCalculateAll(inputFile);
    
    //then
    assertArrayEquals(singleThreadResult.toArray(), parallelResult.toArray());
  }
  

}
