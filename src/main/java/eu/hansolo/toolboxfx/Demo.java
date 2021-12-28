package eu.hansolo.toolboxfx;

import eu.hansolo.toolboxfx.evt.type.LocationChangeEvt;
import eu.hansolo.toolboxfx.geom.Location;


public class Demo {

    public Demo() {
        locationDemo();
    }


    private void locationDemo() {
        Location home = new Location(7.38, 51.51);
        home.addLocationObserver(LocationChangeEvt.LOCATION_CHANGED, e -> System.out.println("Location observer: " + e.getOldLocation() + "\n---------\n" + e.getLocation()));
        home.addLocationObserver(LocationChangeEvt.ALTITUDE_CHANGED, e -> System.out.println("Altitude observer: " + e.getLocation().getAltitude()));
        home.setLatitude(8.0);
    }

    public static void main(String[] args) {
        new Demo();
    }
}
