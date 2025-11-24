package main.com.gavruseva.task3.state.impl;

import main.com.gavruseva.task3.entity.Pier;
import main.com.gavruseva.task3.entity.Port;
import main.com.gavruseva.task3.entity.Ship;
import main.com.gavruseva.task3.exception.MultiThreadException;
import main.com.gavruseva.task3.state.ShipState;

import java.util.Optional;

public class ShipDepartingState implements ShipState {
  @Override
  public void doAction(Ship ship) throws MultiThreadException {
    Port port = Port.getInstance();
    Optional<Pier> pier = ship.getPier();
    if (pier.isPresent()) {
      port.releasePier(pier.get(), ship);
      ship.setPier(Optional.empty());
    }
    ship.setShipState(new ShipArrivingState());
  }
}
