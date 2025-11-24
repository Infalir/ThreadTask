package main.com.gavruseva.task3.entity;

import main.com.gavruseva.task3.exception.MultiThreadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
  private static final Logger logger = LogManager.getLogger();
  private static final int AMOUNT_OF_PIERS = 4;
  private static final int CAPACITY = 50;
  private static Port instance;
  private static Lock lock = new ReentrantLock();
  private AtomicInteger occupiedPlaces;
  private List<Pier> busyPiers = new ArrayList<>();
  private List<Pier> freePiers = new ArrayList<>();
  private Lock pierLock = new ReentrantLock();
  private Lock shipLock = new ReentrantLock();
  private Condition pierCondition = pierLock.newCondition();

  private Port() {
    occupiedPlaces = new AtomicInteger();
    for (int i = 0; i < AMOUNT_OF_PIERS; i++) {
      Pier pier = new Pier();
      freePiers.add(pier);
    }
  }

  public AtomicInteger getOccupiedPlaces() {
    return occupiedPlaces;
  }

  public void setOccupiedPlaces(AtomicInteger occupiedPlaces) {
    this.occupiedPlaces = occupiedPlaces;
  }

  public List<Pier> getBusyPiers() {
    return Collections.unmodifiableList(busyPiers);
  }

  public List<Pier> getFreePiers() {
    return Collections.unmodifiableList(freePiers);
  }

  public static Port getInstance() {
    if (instance == null) {
      try {
        lock.lock();
        if (instance == null) {
          instance = new Port();
        }

      } finally {
        lock.unlock();
      }
    }
    return instance;
  }

  public Pier getPier(Ship ship) {
    try {
      pierLock.lock();
      while (freePiers.isEmpty()) {
        try {
          pierCondition.await();
        } catch (InterruptedException e) {
          logger.error("Thread was interrupted while waiting for free piers", e);
          Thread.currentThread().interrupt();
        }
      }
      Pier pier = freePiers.getFirst();
      freePiers.remove(pier);
      busyPiers.add(pier);
      logger.info("Ship {} has arrived. Ship task {}, ship current load {}, ship capacity {}. Pier {} is now occupied",
              ship.getShipId(), ship.getShipTarget(), ship.getOccupiedPlaces(),
              ship.getCapacity(), pier.getPierId());
      return pier;
    } finally {
      pierLock.unlock();
    }
  }

  public void releasePier(Pier pier, Ship ship) {
    try {
      pierLock.lock();
      freePiers.add(pier);
      busyPiers.remove(pier);
      logger.info("Ship {} has left. Ship task {}, ship current load {}, ship capacity {} Pier {} is now free",
              ship.getShipId(), ship.getShipTarget(), ship.getOccupiedPlaces(),
              ship.getCapacity(), pier.getPierId());
    } finally {
      pierCondition.signal();
      pierLock.unlock();
    }
  }

  public void loadShip(Ship ship) {
    try {
      shipLock.lock();
      while(!ship.isFull()) {
        if (occupiedPlaces.get() > 0) {
          logger.info("Ship {} has been loaded. Cargo on the ship {}. Cargo left in the port {}",
                  ship.getShipId(), ship.getOccupiedPlaces(), occupiedPlaces.get());
          ship.addContainer();
          occupiedPlaces.decrementAndGet();
        }
        if (occupiedPlaces.get() == 0) {
          logger.info("Port is empty");
          break;
        }
      }
    } finally {
      shipLock.unlock();
    }
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      logger.error("Thread was interrupted while loading ship {}", ship.getShipId(), e);
      Thread.currentThread().interrupt();
    }
  }

  public void unloadShip(Ship ship) {
    try{
      shipLock.lock();
      while(!ship.isEmpty()){
        if(occupiedPlaces.get() < CAPACITY){
          logger.info("Ship {} has been unloaded. Cargo on the ship {}. Cargo left in the port {}",
                  ship.getShipId(), ship.getOccupiedPlaces(), occupiedPlaces.get());
          ship.removeContainer();
          occupiedPlaces.incrementAndGet();
        }
        if(occupiedPlaces.get() == CAPACITY){
          logger.info("Port is full");
          break;
        }
      }
    } finally {
      shipLock.unlock();
    }
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      logger.error("Thread was interrupted while unloading ship {}", ship.getShipId(), e);
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + occupiedPlaces.get();
    result = prime * result + ((busyPiers == null) ? 0 : busyPiers.hashCode());
    result = prime * result + ((freePiers == null) ? 0 : freePiers.hashCode());
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
    Port other = (Port) obj;
    if (occupiedPlaces.get() != other.occupiedPlaces.get()) {
      return false;
    }
    if (busyPiers == null) {
      if (other.busyPiers != null) {
        return false;
      }
    } else if (!busyPiers.equals(other.busyPiers)) {
      return false;
    }
    if (freePiers == null) {
      if (other.freePiers != null) {
        return false;
      }
    } else if (!freePiers.equals(other.freePiers)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Port");
    sb.append("\nOccupied places: ").append(occupiedPlaces);
    sb.append("\nBusy piers: ").append(busyPiers);
    sb.append("\nFree piers: ").append(freePiers);
    return sb.toString();
  }
}
