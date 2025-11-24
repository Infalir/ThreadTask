package main.com.gavruseva.task3;

import main.com.gavruseva.task3.entity.Port;
import main.com.gavruseva.task3.entity.Ship;
import main.com.gavruseva.task3.entity.ShipTarget;
import main.com.gavruseva.task3.exception.MultiThreadException;
import main.com.gavruseva.task3.parser.DataParser;
import main.com.gavruseva.task3.reader.DataReader;
import main.com.gavruseva.task3.state.impl.ShipArrivingState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Main {
  private static final Logger logger = LogManager.getLogger();
  private final static String FILE_PATH = "resources/data/data.txt";
  public static void main(String[] args) {
    DataParser parser = new DataParser();
    List<Ship> ships = new ArrayList<>();

    List<String> lines;
    try {
      lines = DataReader.fileReading(FILE_PATH);
    } catch (MultiThreadException e) {
      logger.fatal("Could not read file {}", FILE_PATH, e);
      return;
    }

    for (String line : lines) {
      if (line.isBlank()) continue;
      try {
        List<Integer> parsed = parser.parse(line);
        int capacity = parsed.get(0);
        int occupied = parsed.get(1);
        int targetCode = parsed.get(2);

        ShipTarget target = ShipTarget.fromInt(targetCode);
        Ship ship = new Ship(capacity, occupied, target);
        ship.setShipState(new ShipArrivingState());

        ships.add(ship);
      } catch (MultiThreadException e) {
        logger.error("Could not parse line {}", line, e);
      }
    }

    Port port = Port.getInstance();
    port.getOccupiedPlaces().set(25);

    List<Thread> threads = new ArrayList<>();
    for (Ship ship : ships) {
      Thread t = new Thread(ship);
      threads.add(t);
      t.start();
    }

    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error("Interrupted while waiting for thread", e);
      }
    }

    logger.info("Final port state {}", port);
    logger.info("Simulation complete.");
  }
}
