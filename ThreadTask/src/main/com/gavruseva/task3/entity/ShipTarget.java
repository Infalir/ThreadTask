package main.com.gavruseva.task3.entity;

public enum ShipTarget {
  LOADING(1),
  UNLOADING(2),
  LOADING_UNLOADING(0);
  private final int code;

  ShipTarget(int code) {
    this.code = code;
  }

  public static ShipTarget fromInt(int code) {
    for (ShipTarget st : values()) {
      if (st.code == code) return st;
    }
    throw new IllegalArgumentException("Invalid ShipTarget code: " + code);
  }
}
