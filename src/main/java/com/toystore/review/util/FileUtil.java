package com.toystore.review.util;

import com.toystore.util.DataPaths;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static final String DELIMITER = "||";

    private static String reviewsFile() {
        return DataPaths.get("reviews.txt");
    }

    public static void saveReview(String reviewData) throws IOException {
        ensureFileExists();
        try (FileWriter fw = new FileWriter(reviewsFile(), true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(reviewData);
        }
    }

    public static List<String> readAllReviews() throws IOException {
        ensureFileExists();
        List<String> reviews = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(reviewsFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    reviews.add(line);
                }
            }
        }
        return reviews;
    }

    public static void updateReview(int id, String newReviewData) throws IOException {
        ensureFileExists();
        List<String> reviews = readAllReviews();
        List<String> updatedReviews = new ArrayList<>();

        for (String review : reviews) {
            String[] parts = parseReviewData(review);
            if (parts.length > 0 && Integer.parseInt(parts[0]) == id) {
                updatedReviews.add(newReviewData);
            } else {
                updatedReviews.add(review);
            }
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(reviewsFile()))) {
            for (String review : updatedReviews) {
                out.println(review);
            }
        }
    }

    public static void deleteReview(int id) throws IOException {
        ensureFileExists();
        List<String> reviews = readAllReviews();
        List<String> remainingReviews = new ArrayList<>();

        for (String review : reviews) {
            String[] parts = parseReviewData(review);
            if (parts.length > 0 && Integer.parseInt(parts[0]) != id) {
                remainingReviews.add(review);
            }
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(reviewsFile()))) {
            for (String review : remainingReviews) {
                out.println(review);
            }
        }
    }

    public static String formatReviewData(int id, String productId, String userId, int rating,
                                        String comment, String createdAt, String updatedAt) {
        return String.join(DELIMITER,
            String.valueOf(id),
            productId,
            userId,
            String.valueOf(rating),
            comment,
            createdAt,
            updatedAt
        );
    }

    public static String[] parseReviewData(String reviewData) {
        String[] parts = reviewData.split("\\|\\|");
        if (parts.length >= 7) {
            return parts;
        }
        parts = reviewData.split("\\|");
        if (parts.length >= 7) {
            return parts;
        }
        return new String[0];
    }

    private static void ensureFileExists() throws IOException {
        File file = new File(reviewsFile());
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.createNewFile()) {
                throw new IOException("Could not create reviews file");
            }
        }
    }
}
