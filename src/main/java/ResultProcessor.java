import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class ResultProcessor {

  private static final String BENCHMARK_COLUMN_NAME = "Benchmark";
  private static final String SCORE_COLUMN_NAME = "Score";
  private static final String ERROR_COLUMN_NAME = "Score Error (99.9%)";
  private static final String NAME_PREFIX = "org.objectweb.asm.benchmarks.";
  private static final String MEMORY_BENCHMARK_PREFIX = "MemoryBenchmark";
  private static final String TYPE_BENCHMARK_PREFIX = "TypeBenchmark";
  private static final String MEMORY_USE_SUFFIX = ":+memory.used";
  private static final String TEMPLATE_NAME = "performance.ftl";

  private static final int MEGA_BYTE = 1024 * 1024;

  public static void main(String[] args) throws Exception {
    processResults(normalize(readResults(args[0])), args[1]);
  }

  private static List<Benchmark> readResults(String inputFileName) throws Exception {
    List<Benchmark> result = new ArrayList<>();
    Map<String, Benchmark> benchmarks = new HashMap<>();
    for (CSVRecord record : CSVFormat.DEFAULT.withHeader().parse(new FileReader(inputFileName))) {
      String fullName = stripPrefix(record.get(BENCHMARK_COLUMN_NAME), NAME_PREFIX);
      if (fullName.startsWith(TYPE_BENCHMARK_PREFIX)
          || fullName.endsWith("memory.used") != fullName.contains(MEMORY_BENCHMARK_PREFIX)) {
        continue;
      }

      String benchmarkName = getBenchmarkName(fullName);
      Benchmark.Type benchmarkType =
          benchmarkName.startsWith(MEMORY_BENCHMARK_PREFIX)
              ? Benchmark.Type.MEMORY
              : Benchmark.Type.THROUGHPUT;
      String elementName = getElementName(fullName);

      Benchmark benchmark = benchmarks.get(benchmarkName);
      if (benchmark == null) {
        benchmark = new Benchmark(benchmarkType, benchmarkName);
        benchmarks.put(benchmarkName, benchmark);
        result.add(benchmark);
      }
      float value = Float.parseFloat(record.get(SCORE_COLUMN_NAME));
      float error = Float.parseFloat(record.get(ERROR_COLUMN_NAME));
      if (benchmarkType == Benchmark.Type.MEMORY) {
        value /= MEGA_BYTE;
        error /= MEGA_BYTE;
      }
      benchmark.addElement(new Element(elementName, value, error));
    }
    return result;
  }

  private static List<Benchmark> normalize(List<Benchmark> result) {
    for (Benchmark benchmark : result) {
      float maxValue = benchmark.getMaxValue();
      int maxValue10 = (int) Math.pow(10.0, Math.ceil(Math.log10(maxValue)));
      int maxValue5 = 5 * (int) Math.pow(10.0, Math.ceil(Math.log10(maxValue / 5.0)));
      int maxValue2 = 2 * (int) Math.pow(10.0, Math.ceil(Math.log10(maxValue / 2.0)));
      benchmark.setScale(Math.min(Math.min(maxValue10, maxValue5), maxValue2));
    }
    return result;
  }

  private static void processResults(List<Benchmark> results, String outputFileName)
      throws Exception {
    Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
    configuration.setClassForTemplateLoading(ResultProcessor.class, "");
    configuration.setLocale(Locale.ENGLISH);
    configuration.setDefaultEncoding("UTF-8");
    configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

    Map<String, Object> input = new HashMap<String, Object>();
    input.put("benchmarks", results);

    Writer fileWriter = new FileWriter(new File(outputFileName));
    try {
      configuration.getTemplate(TEMPLATE_NAME).process(input, fileWriter);
    } finally {
      fileWriter.close();
    }
  }

  private static String stripPrefix(String str, String prefix) {
    if (str.startsWith(prefix)) {
      return str.substring(prefix.length());
    }
    return str;
  }

  private static String getBenchmarkName(String fullName) {
    return fullName.substring(0, getNameSeparatorIndex(fullName));
  }

  private static String getElementName(String fullName) {
    int endIndex = fullName.lastIndexOf(MEMORY_USE_SUFFIX);
    if (endIndex == -1) {
      return fullName.substring(getNameSeparatorIndex(fullName) + 1);
    } else {
      return fullName.substring(getNameSeparatorIndex(fullName) + 1, endIndex);
    }
  }

  private static int getNameSeparatorIndex(String fullName) {
    if (fullName.startsWith("GeneratorBenchmark")) {
      return fullName.indexOf('.');
    } else {
      return fullName.indexOf('_');
    }
  }
}
