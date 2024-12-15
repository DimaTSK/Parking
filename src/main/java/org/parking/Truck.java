package org.parking;

import java.util.List;

public class Truck {
    private final TruckCapacity capacity;
    private final TruckGrid grid;

    public Truck(TruckCapacity capacity) {
        this.capacity = capacity;
        this.grid = new TruckGrid(capacity.getWidth(), capacity.getHeight());
    }

    public void packPackages(List<Parcel> parcels) {
        parcels.forEach(pkg -> {
            boolean placed = false;

            for (int i = 0; i <= capacity.getHeight() - pkg.getHeight(); i++) {
                if (placed) break;
                for (int j = 0; j <= capacity.getWidth() - pkg.getWidth(); j++) {
                    if (grid.canPlacePackage(pkg, i, j)) {
                        grid.placePackage(pkg, i, j);
                        placed = true;
                        break;
                    }
                }
            }

            if (!placed) {
                System.out.println("Не удалось разместить посылку:\n" + String.join("\n", pkg.getLines()));
            }
        });
    }

    public void print() {
        grid.print();
    }
}