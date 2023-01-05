package softuni.exam.util;

import java.nio.file.Path;

public enum Paths {
    ;

    public static final Path MECHANICS_JSON_PATH =
            Path.of("src", "main", "resources", "files", "json", "mechanics.json");

    public static final Path PARTS_JSON_PATH =
            Path.of("src", "main", "resources", "files", "json", "parts.json");

    public static final Path CARS_XML_PATH =
            Path.of("src", "main", "resources", "files", "xml", "cars.xml");

    public static final Path TASKS_XML_PATH =
            Path.of("src", "main", "resources", "files", "xml", "tasks.xml");

}
