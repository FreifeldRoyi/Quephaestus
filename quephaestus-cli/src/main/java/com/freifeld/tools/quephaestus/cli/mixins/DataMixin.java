package com.freifeld.tools.quephaestus.cli.mixins;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freifeld.tools.quephaestus.core.exceptions.PathDoesNotExistException;
import com.freifeld.tools.quephaestus.core.exceptions.UnhandledQuephaestusException;
import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class DataMixin {
    private DataMixin.Exclusive exclusive;
    private Map<String, String> mappings;

    @ArgGroup(exclusive = true, multiplicity = "0..1")
    public void setExclusive(DataMixin.Exclusive exclusive) {
        this.exclusive = exclusive;
        this.exclusive.mixin = this;
    }

    public Map<String, String> mappings() {
        return this.mappings != null ? this.mappings : Collections.emptyMap();
    }

    public static class Exclusive {
        DataMixin mixin;

        private void parseContent(String content) throws IOException {
            final var mapper = new ObjectMapper();
            try (final var parser = mapper.createParser(content)) {
                this.mixin.mappings = parser.readValueAs(new TypeReference<Map<String, String>>() {
                });
            }
        }

        @Option(names = {"-d", "--data"},
                description = "'key=value' formatted tuples. (repeated)",
                arity = "*")
        public void setData(Map<String, String> data) {
            this.mixin.mappings = new HashMap<>(data);
        }

        @Option(names = {"-p", "--payload"}, description = "Data file containing mappings", arity = "0..1")
        public void setPayload(String filePath) {
            final var path = Path.of(filePath);
            if (!Files.exists(path)) {
                throw new PathDoesNotExistException(path);
            }

            try {
                final var fileContent = Files.readString(path);
                this.parseContent(fileContent);
            } catch (IOException e) {
                throw new UnhandledQuephaestusException("Failed to parse the provided payload", e);
            }
        }
    }
}
