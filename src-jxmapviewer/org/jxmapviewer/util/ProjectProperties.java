package org.jxmapviewer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project properties.
 *
 * @author Primoz K.
 */
public enum ProjectProperties {

    /**
     * The only instance of this class
     */
    INSTANCE;

    private static final String PROPERTIES_FILE = "project.properties";

    private static final String PROP_VERSION = "version";
    private static final String PROP_NAME = "name";

    private final Logger log = Logger.getLogger( ProjectProperties.class.getName() );
    private final Properties props = new Properties();

    private ProjectProperties() {
        log.fine("Loading project properties...");

        InputStream is = null;
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            is = classloader.getResourceAsStream(PROPERTIES_FILE);
            if (is != null) {
                props.load(is);
                log.fine("Properties successfully loaded.");
            } else {
                log.warning("Project properties file not found. Set default values.");
                props.put(PROP_NAME, "JxMapViewer");
                props.put(PROP_VERSION, "1.0");
            }
        }
        catch (IOException e) {
            log.log( Level.WARNING, "Unable to read project properties.", e);
            props.put(PROP_NAME, "JxMapViewer");
            props.put(PROP_VERSION, "1.0");
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException e) {
                log.log( Level.WARNING, "Unable to close stream.", e);
            }
        }
    }

    /***************************************************************
     ********************* PROPERTIES GETTERS **********************
     ***************************************************************/

    /**
     * @return Project version.
     */
    public String getVersion() {
        return props.getProperty(PROP_VERSION);
    }

    /**
     * @return Project name.
     */
    public String getName() {
        return props.getProperty(PROP_NAME);
    }

}
