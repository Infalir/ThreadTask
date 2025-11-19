package main.com.gavruseva.task3.state;

import main.com.gavruseva.task3.entity.Ship;
import main.com.gavruseva.task3.exception.MultiThreadException;

public interface ShipState {
  void doAction(Ship ship) throws MultiThreadException;
}
