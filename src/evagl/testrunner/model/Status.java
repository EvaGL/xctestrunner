package evagl.testrunner.model;

import evagl.testrunner.ui.Icons;

import javax.swing.*;

/**
 * Represents status of particular test case/suite or whole testing process
 */
public enum Status {
    RUNNING(Icons.LOADING),
    FAILED(Icons.FAIL),
    PASSED(Icons.OK),
    CANCELED(Icons.CANCELED);

    private final Icon icon;

    Status(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }
}
