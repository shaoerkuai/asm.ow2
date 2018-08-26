import java.util.ArrayList;
import java.util.List;

public class Benchmark {
  private final Type type;
  private final String name;
  private final List<Element> elements = new ArrayList<>();
  private int scale;

  enum Type {
    THROUGHPUT,
    MEMORY;
  }

  public Benchmark(Type type, String name) {
    this.type = type;
    this.name = name;
  }

  public Type getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getCaption() {
    return type == Type.THROUGHPUT ? scale + " ops/s" : scale + " MB";
  }

  public List<Element> getElements() {
    return elements;
  }

  public float getMaxValue() {
    float maxValue = 0.0f;
    for (Element element : elements) {
      maxValue = Math.max(maxValue, element.getMaxValue());
    }
    return maxValue;
  }

  public void addElement(Element element) {
    elements.add(element);
  }

  public void setScale(int scale) {
    this.scale = scale;
    for (Element element : elements) {
      element.scale(100.0f / scale);
    }
  }
}
