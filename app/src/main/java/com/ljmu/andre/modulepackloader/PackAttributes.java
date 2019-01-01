package com.ljmu.andre.modulepackloader;

import java.util.jar.Attributes;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 * <hr>
 * Allows reading important information from the jar File and linking it to the ModulePack Instance.
 * Some Examples:
 * <ul>
 *     <li>Pack Version</li>
 *     <li>Target App Version</li>
 *     <li>Basic or Premium Pack (if you want to have a paid version)</li>
 *     <li>Flavour (Beta or Release)</li>
 * </ul>
 */
public interface PackAttributes {

    /**
     * Listener called when the Pack is instantiated
     *
     * @param attributes Attributes read from the jar
     * @param <T> Type of your own PackAttributes Implementation
     * @return The Built instance of {@code T} with any information you need
     */
    <T extends PackAttributes> T onBuild(Attributes attributes);

    class TestPackAttributes implements PackAttributes {
        private String type;
        private String packVersion;
        private String scVersion;

        @Override
        public <T extends PackAttributes> T onBuild(Attributes attributes) {
            this.type = attributes.getValue("Type");
            this.packVersion = attributes.getValue("PackVersion");
            this.scVersion = attributes.getValue("SCVersion");

            return (T) this;
        }

        @Override
        public String toString() {
            return "TestPackAttributes{" +
                    "type='" + type + '\'' +
                    ", packVersion='" + packVersion + '\'' +
                    ", scVersion='" + scVersion + '\'' +
                    '}';
        }
    }
}
