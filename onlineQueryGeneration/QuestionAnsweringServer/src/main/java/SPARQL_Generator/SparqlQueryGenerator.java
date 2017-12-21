/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SPARQL_Generator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Florian
 */
public class SparqlQueryGenerator {

    public SparqlQueryGenerator() {
    }

    public String generate(String questionString, List<String> entityURIs) {
        // Extracting the labels of the classes and properties with their matching DBpedia URIs from the files
        // Storing them in a HashMap with the label as key and the URI as value
        // Extracting the parts (single words)
        // Storing them in a HashMap with the label as key and its single words as value
        
        Map<String, String> classes = new HashMap<>();
        Map<String, String> properties = new HashMap<>();
        Map<String, String[]> classesParts = new HashMap<>();
        Map<String, String[]> propertiesParts = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/Classes.txt")));
            String line, key, value;
            String[] parts;
            while ((line = reader.readLine()) != null) {
                parts = line.split("\"");
                key = parts[parts.length - 2];

                parts = line.split(" ");
                value = parts[0].replace("<", "").replace(">", "");

                classes.put(key, value);
                classesParts.put(key, key.split(" "));
            }
            reader.close();

            reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/Properties.txt")));

            while ((line = reader.readLine()) != null) {
                parts = line.split("\"");
                key = parts[parts.length - 2];

                parts = line.split(" ");
                value = parts[0].replace("<", "").replace(">", "");

                properties.put(key, value);
                propertiesParts.put(key, key.split(" "));
            }
            reader.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }

        // Extracting the classes and properties of the question
        List<String> classesURIs = new ArrayList<>();
        List<String> propertiesURIs = new ArrayList<>();

        Iterator<String> propertiesKeys;
        Iterator<String> classesKeys;

        String[] words = questionString.replace("?", " ?").split(" ");

        for (int i = 0; i < words.length; i++) {
            classesKeys = classes.keySet().iterator();
            while (classesKeys.hasNext()) {
                String key = classesKeys.next();
                String[] parts = classesParts.get(key);
                int length = parts.length;
                String[] focus = Arrays.copyOfRange(words, i, i + length);

                if (Arrays.equals(parts, focus)) {
                    classesURIs.add(classes.get(key));
                    while (classesKeys.hasNext()) {
                        classesKeys.next();
                    }
                }
            }
        }

        for (int i = 0; i < words.length; i++) {
            propertiesKeys = properties.keySet().iterator();
            while (propertiesKeys.hasNext()) {
                String key = propertiesKeys.next();
                String[] parts = propertiesParts.get(key);
                int length = parts.length;
                String[] focus = Arrays.copyOfRange(words, i, i + length);

                if (Arrays.equals(parts, focus)) {
                    propertiesURIs.add(properties.get(key));
                    while (propertiesKeys.hasNext()) {
                        propertiesKeys.next();
                    }
                }
            }
        }

        System.out.println(entityURIs.toString());
        System.out.println(classesURIs.toString());
        System.out.println(propertiesURIs.toString());
        
        // Generating the query using the Pattern SPARQL generator
        PatternSparqlGenerator generator = PatternSparqlGenerator.getInstance();
        String query = generator.generateQuery(classesURIs, propertiesURIs, entityURIs);
        return query;
    }
}
