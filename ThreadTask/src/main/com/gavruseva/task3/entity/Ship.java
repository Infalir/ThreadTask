package main.com.gavruseva.task3.entity;

import main.com.gavruseva.task3.exception.MultiThreadException;
import main.com.gavruseva.task3.state.ShipState;
import main.com.gavruseva.task3.state.impl.ShipDepartingState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class Ship implements Runnable {
  private static final Logger logger = LogManager.getLogger();
  private final String shipId;
  private final int capacity;
  private int occupiedPlaces;
  private ShipTarget shipTarget;
  private ShipState shipState;
  private Optional<Pier> pier;

  public Ship(int capacity, int occupiedPlaces, ShipTarget shipTarget) {
    shipId = UUID.randomUUID().toString().replace("-","");
    this.capacity = capacity;
    this.occupiedPlaces = occupiedPlaces;
    this.shipTarget = shipTarget;
  }

  public String getShipId() {
    return shipId;
  }

  public int getCapacity() {
    return capacity;
  }

  public int getOccupiedPlaces() {
    return occupiedPlaces;
  }

  public void setOccupiedPlaces(int occupiedPlaces) {
    this.occupiedPlaces = occupiedPlaces;
  }

  public ShipTarget getShipTarget() {
    return shipTarget;
  }

  public void setShipTarget(ShipTarget shipTarget) {
    this.shipTarget = shipTarget;
  }

  public ShipState getShipState() {
    return shipState;
  }

  public void setShipState(ShipState shipState) {
    this.shipState = shipState;
  }

  public Optional<Pier> getPier() {
    return pier;
  }

  public void setPier(Optional<Pier> pier) {
    this.pier = pier;
  }

  public void addContainer() {
    occupiedPlaces++;
  }

  public void removeContainer() {
    occupiedPlaces--;
  }

  public boolean isFull(){
    return occupiedPlaces == capacity;
  }

  public boolean isEmpty(){
    return occupiedPlaces == 0;
  }

  @Override
  public void run() {
    boolean departed = false;
    while (!departed) {
      try {
        shipState.doAction(this);
        if (shipState instanceof ShipDepartingState) {
          try {
            shipState.doAction(this);
          } catch (MultiThreadException e) {
            logger.error("Cannot proceed to completing an action on ship {}", shipId, e);
          }
          departed = true;
        }
      } catch (MultiThreadException e) {
        logger.error("Cannot do action", e);
        break;
      }
    }
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((shipId == null) ? 0 : shipId.hashCode());
    result = prime * result + capacity;
    result = prime * result + occupiedPlaces;
    result = prime * result + ((shipTarget == null) ? 0 : shipTarget.hashCode());
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
    Ship other = (Ship) obj;
    if (shipId == null) {
      if (other.shipId != null) {
        return false;
      }
    } else if (!shipId.equals(other.shipId)) {
      return false;
    }
    if (capacity != other.capacity) {
      return false;
    }
    if (occupiedPlaces != other.occupiedPlaces) {
      return false;
    }
    if (shipTarget == null) {
      if (other.shipTarget != null) {
        return false;
      }
    } else if (!shipTarget.equals(other.shipTarget)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Ship ");
    sb.append(shipId).append(":");
    sb.append("\nCapacity: ").append(capacity);
    sb.append("\nOccupied places: ").append(occupiedPlaces);
    sb.append("\nShip target: ").append(shipTarget);
    return sb.toString();
  }

}
