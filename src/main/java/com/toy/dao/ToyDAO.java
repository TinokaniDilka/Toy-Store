package com.toy.dao;

import com.toy.model.Toy;
import com.toystore.util.DataPaths;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToyDAO {
    private static final Logger LOGGER = Logger.getLogger(ToyDAO.class.getName());

    private File getFile() {
        return new File(DataPaths.get("toys.txt"));
    }

    private void ensureFileExists() {
        File file = getFile();
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error initializing toys data file", e);
        }
    }

    public void addToy(Toy toy) {
        ensureFileExists();
        try (FileWriter fw = new FileWriter(getFile(), true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(toy.toString());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error adding toy", e);
        }
    }

    public List<Toy> getAllToys() {
        ensureFileExists();
        List<Toy> toys = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(getFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    try {
                        toys.add(new Toy(parts[0], parts[1], parts[2], Double.parseDouble(parts[3])));
                    } catch (NumberFormatException e) {
                        LOGGER.warning("Invalid price format: " + parts[3]);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading toys", e);
        }
        return toys;
    }

    public Toy getToyByImageName(String imageName) {
        for (Toy toy : getAllToys()) {
            if (toy.getImageName().equals(imageName)) {
                return toy;
            }
        }
        return null;
    }

    public void updateToy(Toy updatedToy) {
        List<Toy> toys = getAllToys();
        for (int i = 0; i < toys.size(); i++) {
            if (toys.get(i).getImageName().equals(updatedToy.getImageName())) {
                toys.set(i, updatedToy);
                break;
            }
        }
        saveAllToys(toys);
    }

    public void deleteToy(String imageName) {
        List<Toy> toys = getAllToys();
        toys.removeIf(toy -> toy.getImageName().equals(imageName));
        saveAllToys(toys);
    }

    private void saveAllToys(List<Toy> toys) {
        ensureFileExists();
        try (PrintWriter out = new PrintWriter(new FileWriter(getFile()))) {
            for (Toy toy : toys) {
                out.println(toy.toString());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving toys", e);
        }
    }

    public List<Toy> searchToys(String query) {
        List<Toy> results = new LinkedList<>();
        query = query.toLowerCase();
        for (Toy toy : getAllToys()) {
            if (toy.getName().toLowerCase().contains(query)
                    || toy.getDescription().toLowerCase().contains(query)) {
                results.add(toy);
            }
        }
        return results;
    }
}
