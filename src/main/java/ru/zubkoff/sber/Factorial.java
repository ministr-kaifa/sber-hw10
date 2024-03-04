package ru.zubkoff.sber;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Factorial {

  public static BigInteger calculate(long of) {
    if (of < 0) {
      throw new IllegalArgumentException("Number argument cant be negative");
    }

    BigInteger result = BigInteger.ONE;

    for (BigInteger n = BigInteger.TWO; n.compareTo(BigInteger.valueOf(of)) <= 0; n = n.add(BigInteger.ONE)) {
      result = result.multiply(n);
    }

    return result;
  }

  public static List<BigInteger> calculateAll(Collection<Long> in) {
    return in.stream()
        .map(Factorial::calculate)
        .toList();
  }

  public static List<BigInteger> parallelCalculateAll(Collection<Long> in) throws InterruptedException {
    try (ExecutorService threadPool = Executors.newCachedThreadPool();) {
      Function<Long, Callable<BigInteger>> toCallableFactorial = number -> () -> calculate(number);

      return threadPool.invokeAll(in.stream()
          .map(toCallableFactorial)
          .toList()).stream()
          .map(future -> {
            try {
              return future.get();
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              throw new RuntimeException(e);
            } catch (ExecutionException e) {
              throw new RuntimeException(e);
            }
          })
          .toList();
    }
  }

  public static List<BigInteger> parallelCalculateAll(Path file) throws IOException, InterruptedException {
    return parallelCalculateAll(Pattern.compile("\\S++")
        .matcher(Files.readString(file))
        .results()
        .map(MatchResult::group)
        .map(Long::parseLong).toList());
  }

  public static List<BigInteger> calculateAll(Path file) throws IOException {
    return calculateAll(Pattern.compile("\\S++")
        .matcher(Files.readString(file))
        .results()
        .map(MatchResult::group)
        .map(Long::parseLong).toList());
  }
}
