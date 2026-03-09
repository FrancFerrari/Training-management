package com.allenamento.dao;

import com.allenamento.model.Immagine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing images in the database.
 * Provides CRUD methods for operations on images associated with trainings.
 */
public class ImmagineDAO {
    private static final Logger logger = LoggerFactory.getLogger(ImmagineDAO.class);
    private final Connection connection;

    /**
     * Constructor that gets the connection from DatabaseConnection Singleton.
     */
    public ImmagineDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Inserts a new image into the database.
     *
     * @param immagine the image to insert
     * @return the ID of the inserted image
     */
    public int insert(Immagine immagine) {
        String sql = "INSERT INTO immagini (allenamento_id, filename, path) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, immagine.getAllenamentoId());
            pstmt.setString(2, immagine.getFilename());
            pstmt.setString(3, immagine.getPath());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Use SQLite's last_insert_rowid() to get the ID
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        logger.info("Image inserted with ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting image", e);
        }
        return -1;
    }

    /**
     * Deletes an image from the database.
     *
     * @param id the ID of the image to delete
     * @return true if the deletion succeeded
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM immagini WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Image deleted: ID " + id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting image", e);
        }
        return false;
    }

    /**
     * Retrieves an image by ID.
     *
     * @param id the image ID
     * @return the found image, null if not found
     */
    public Immagine getById(int id) {
        String sql = "SELECT * FROM immagini WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToImmagine(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving image", e);
        }
        return null;
    }

    /**
     * Retrieves all images associated with a training.
     *
     * @param allenamentoId the training ID
     * @return list of training images
     */
    public List<Immagine> getByAllenamentoId(int allenamentoId) {
        List<Immagine> immagini = new ArrayList<>();
        String sql = "SELECT * FROM immagini WHERE allenamento_id=? ORDER BY data_upload DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, allenamentoId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                immagini.add(mapResultSetToImmagine(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving images", e);
        }
        return immagini;
    }

    /**
     * Retrieves all images from the database.
     *
     * @return list of all images
     */
    public List<Immagine> getAll() {
        List<Immagine> immagini = new ArrayList<>();
        String sql = "SELECT * FROM immagini ORDER BY data_upload DESC";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                immagini.add(mapResultSetToImmagine(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all images", e);
        }
        return immagini;
    }

    /**
     * Maps a ResultSet to an Immagine object.
     *
     * @param rs the ResultSet to map
     * @return an Immagine object
     */
    private Immagine mapResultSetToImmagine(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Integer allenamentoId = rs.getInt("allenamento_id");
        String filename = rs.getString("filename");
        String path = rs.getString("path");
        LocalDateTime dataUpload = rs.getTimestamp("data_upload") != null ?
                rs.getTimestamp("data_upload").toLocalDateTime() : null;

        return new Immagine(id, allenamentoId, filename, path, dataUpload);
    }
}
