package com.aprendizaje.gastoexagonal;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Comprobación didáctica de imports, sin añadir una dependencia de arquitectura. */
class ArchitectureTest {

    private static final Path ROOT = Path.of("src/main/java/com/aprendizaje/gastoexagonal");

    @Test
    void dominioYAplicacionNoImportanFrameworksNiInfraestructura() throws IOException {
        for (String layer : List.of("domain", "application")) {
            for (Path file : javaFiles(ROOT.resolve(layer))) {
                for (String line : Files.readAllLines(file)) {
                    if (line.startsWith("import ")) {
                        assertTrue(line.startsWith("import java.")
                                        || line.startsWith("import com.aprendizaje.gastoexagonal.domain.")
                                        || (layer.equals("application")
                                        && line.startsWith("import com.aprendizaje.gastoexagonal.application.")),
                                () -> file + ": dependencia no permitida: " + line);
                    }
                }
            }
        }
    }

    @Test
    void controladoresSoloAccedenAlNucleoMediantePuertosDeEntrada() throws IOException {
        for (Path file : javaFiles(ROOT.resolve("infrastructure/adapter/in/web"))) {
            String source = Files.readString(file);
            assertFalse(source.contains("import com.aprendizaje.gastoexagonal.application.service."), file.toString());
            assertFalse(source.contains("import com.aprendizaje.gastoexagonal.application.port.out."), file.toString());
            assertFalse(source.contains("import com.aprendizaje.gastoexagonal.infrastructure.adapter.out."), file.toString());
        }
    }

    private List<Path> javaFiles(Path directory) throws IOException {
        try (var files = Files.walk(directory)) {
            return files.filter(file -> file.toString().endsWith(".java")).toList();
        }
    }
}
