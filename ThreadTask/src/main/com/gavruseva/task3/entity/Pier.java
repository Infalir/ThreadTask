package main.com.gavruseva.task3.entity;

import java.util.UUID;

public class Pier {
  private String pierId;

  public Pier(){
    pierId = UUID.randomUUID().toString().replace("-","");
  }

  public String getPierId() {
    return pierId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((pierId == null) ? 0 : pierId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Pier other = (Pier) obj;
    if (pierId == null) {
      if (other.pierId != null) {
        return false;
      }
    } else if (!pierId.equals(other.pierId)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Pier ");
    sb.append(pierId);
    return sb.toString();
  }
}