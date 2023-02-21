package com.dmasso.multidwh.metadata;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@NoArgsConstructor(access = AccessLevel.NONE)
public class MetadataUtility {

    public static Metadata readMetadata() {
        Yaml yaml = new Yaml(new Constructor(Metadata.class));
        try {
            var is = new FileInputStream("processor/src/main/resources/metadata-v2.yaml");
            return yaml.load(is);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Metadata metadata = readMetadata();
    }
}
