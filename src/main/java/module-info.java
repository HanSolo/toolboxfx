module eu.hansolo.toolboxfx {
    // Java
    requires java.base;

    // Java-FX
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.swing;

    // 3rd Party
    requires transitive eu.hansolo.toolbox;

    exports eu.hansolo.toolboxfx.font;
    exports eu.hansolo.toolboxfx.evt.type;
    exports eu.hansolo.toolboxfx.geom;
    exports eu.hansolo.toolboxfx;
}