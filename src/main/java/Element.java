public class Element {
  private final String name;
  private float value;
  private float error;

  public Element(String name, float value, float error) {
    this.name = name;
    this.value = value;
    this.error = error;
  }

  public String getName() {
    return name;
  }

  public float getValue() {
    return value;
  }

  public float getMinValue() {
    return value - error;
  }

  public float getMaxValue() {
    return value + error;
  }

  public void scale(float scale) {
    value = value * scale;
    error = error * scale;
  }
}
