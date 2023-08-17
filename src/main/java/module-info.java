module eu.hansolo.toolboxfx {
    // Java
    requires java.base;

    // Java-FX
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.swing;

    // 3rd Party
    requires transitive eu.hansolo.toolbox;

    opens eu.hansolo.toolboxfx to eu.hansolo.toolbox;
    opens eu.hansolo.toolboxfx.evt.type to eu.hansolo.toolbox;
    opens eu.hansolo.toolboxfx.font to eu.hansolo.toolbox;
    opens eu.hansolo.toolboxfx.geom to eu.hansolo.toolbox;

    exports eu.hansolo.toolboxfx;
    exports eu.hansolo.toolboxfx.evt.type;
    exports eu.hansolo.toolboxfx.font;
    exports eu.hansolo.toolboxfx.geom;

}
