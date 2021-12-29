package eu.hansolo.toolboxfx.heatmap;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Dimension2D;

import java.util.HashMap;


public class HeatMapBuilder<B extends HeatMapBuilder<B>> {
    private final HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected HeatMapBuilder() { }


    // ******************** Methods *******************************************
    public final static HeatMapBuilder create() {
        return new HeatMapBuilder();
    }

    public final B prefSize(final double width, final double height) {
        return prefSize(new Dimension2D(width, height));
    }

    public final B prefSize(final Dimension2D prefSize) {
        properties.put("prefSize", new SimpleObjectProperty<>(prefSize));
        return (B)this;
    }

    public final B width(final double width) {
        properties.put("width", new SimpleDoubleProperty(width));
        return (B)this;
    }

    public final B height(final double height) {
        properties.put("height", new SimpleDoubleProperty(height));
        return (B)this;
    }

    public final B colorMapping(final Mapping colorMapping) {
        properties.put("colorMapping", new SimpleObjectProperty<>(colorMapping));
        return (B)this;
    }

    public final B spotRadius(final double spotRadius) {
        properties.put("spotRadius", new SimpleDoubleProperty(spotRadius));
        return (B)this;
    }

    public final B fadeColors(final boolean fadeColors) {
        properties.put("fadeColors", new SimpleBooleanProperty(fadeColors));
        return (B)this;
    }

    public final B heatMapOpacity(final double heatMapOpacity) {
        properties.put("heatMapOpacity", new SimpleDoubleProperty(heatMapOpacity));
        return (B)this;
    }

    public final B opacityDistribution(final OpacityDistribution opacityDistribution) {
        properties.put("opacityDistribution", new SimpleObjectProperty<>(opacityDistribution));
        return (B)this;
    }


    public final HeatMap build() {
        double              width               = 400;
        double              height              = 400;
        Mapping             colorMapping        = ColorMapping.LIME_YELLOW_RED;
        double              spotRadius          = 15.5;
        boolean             fadeColors          = false;
        double              heatMapOpacity      = 0.5;
        OpacityDistribution opacityDistribution = OpacityDistribution.CUSTOM;

        for (String key : properties.keySet()) {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                width  = dim.getWidth();
                height = dim.getHeight();
            } else if ("width".equals(key)) {
                width = ((DoubleProperty) properties.get(key)).get();
            } else if ("height".equals(key)) {
                height = ((DoubleProperty) properties.get(key)).get();
            } else if ("colorMapping".equals(key)) {
                colorMapping = ((ObjectProperty<Mapping>) properties.get(key)).get();
            } else if ("spotRadius".equals(key)) {
                spotRadius = ((DoubleProperty) properties.get(key)).get();
            } else if ("fadeColors".equals(key)) {
                fadeColors = ((BooleanProperty) properties.get(key)).get();
            } else if ("heatMapOpacity".equals(key)) {
                heatMapOpacity = ((DoubleProperty) properties.get(key)).get();
            } else if ("opacityDistribution".equals(key)) {
                opacityDistribution = ((ObjectProperty<OpacityDistribution>) properties.get(key)).get();
            }
        }
        return new HeatMap(width,  height, colorMapping, spotRadius, fadeColors, heatMapOpacity, opacityDistribution);
    }
}
