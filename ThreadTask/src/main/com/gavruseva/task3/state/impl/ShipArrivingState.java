package main.com.gavruseva.task3.state.impl;

import main.com.gavruseva.task3.entity.Pier;
import main.com.gavruseva.task3.entity.Port;
import main.com.gavruseva.task3.entity.Ship;
import main.com.gavruseva.task3.exception.MultiThreadException;
import main.com.gavruseva.task3.state.ShipState;

import java.util.Optional;

public class ShipArrivingState implements ShipState {
  @Override
  public void doAction(Ship ship) throws MultiThreadException {
    Port port = Port.getInstance();
    Pier pier = port.getPier();
    ship.setPier(Optional.of(pier));
    switch (ship.getShipTarget()){
      case LOADING -> ship.setShipState(new ShipLoadingState());
      case UNLOADING, LOADING_UNLOADING -> ship.setShipState(new ShipUnloadingState());
    }
  }
}
