package eu.hansolo.toolboxfx.heatmap;

import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;


public interface Mapping {
    Stop[] getStops();

    LinearGradient getGradient();
}
