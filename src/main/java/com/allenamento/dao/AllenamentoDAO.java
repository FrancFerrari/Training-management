package com.allenamento.dao;

import com.allenamento.model.Allenamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing trainings in the database.
 * Provides CRUD methods for operations on trainings.
 */
public class AllenamentoDAO {
    private static final Logger logger = LoggerFactory.getLogger(AllenamentoDAO.class);

    /**
     * Constructor that gets the connection from DatabaseConnection Singleton.
     */
    public AllenamentoDAO() {
        // Connection is obtained dynamically for each operation
    }

    /**
     * Gets the active database connection.
     */
    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Inserts a new training into the database.
     *
     * @param allenamento the training to insert
     * @return the ID of the inserted training
     */
    public int insert(Allenamento allenamento) {
        String sql = "INSERT INTO allenamenti (nome, eta, tipo, obiettivo, descrizione, note, strumento) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = getConnection();
        if (conn == null) {
            logger.error("Database connection is null!");
            return -1;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, allenamento.getNome());
            pstmt.setString(2, allenamento.getEta());
            pstmt.setString(3, allenamento.getTipo());
            pstmt.setString(4, allenamento.getObiettivo());
            pstmt.setString(5, allenamento.getDescrizione());
            pstmt.setString(6, allenamento.getNote());
            pstmt.setString(7, allenamento.getStrumento());

            logger.info("Executing INSERT for: " + allenamento.getNome());
            int affectedRows = pstmt.executeUpdate();
            logger.info("Rows inserted: " + affectedRows);

            if (affectedRows > 0) {
                // Use SQLite's last_insert_rowid() to get the ID
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        logger.info("Training inserted with ID: " + id);
                        return id;
                    }
                }
                // Fallback: return 1 if insert succeeded
                logger.warn("last_insert_rowid not available, returning 1");
                return 1;
            } else {
                logger.warn("No rows inserted - affectedRows = 0");
            }
        } catch (SQLException e) {
            logger.error("SQLException during insert: " + e.getMessage(), e);
            logger.error("SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
        } catch (Exception e) {
            logger.error("Generic exception during insert: " + e.getClass().getName() + " - " + e.getMessage(), e);
        }
        logger.error("Dati: nome=" + allenamento.getNome() + ", eta=" + allenamento.getEta() + ", tipo=" + allenamento.getTipo());
        return -1;
    }

    /**
     * Updates an existing training in the database.
     *
     * @param allenamento the training to update
     * @return true if the update succeeded
     */
    public boolean update(Allenamento allenamento) {
        String sql = "UPDATE allenamenti SET nome=?, eta=?, tipo=?, obiettivo=?, descrizione=?, note=?, strumento=?, " +
                 "data_modifica=CURRENT_TIMESTAMP WHERE id=?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, allenamento.getNome());
            pstmt.setString(2, allenamento.getEta());
            pstmt.setString(3, allenamento.getTipo());
            pstmt.setString(4, allenamento.getObiettivo());
            pstmt.setString(5, allenamento.getDescrizione());
            pstmt.setString(6, allenamento.getNote());
            pstmt.setString(7, allenamento.getStrumento());
            pstmt.setInt(8, allenamento.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Training updated: ID " + allenamento.getId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating training", e);
        }
        return false;
    }

    /**
     * Deletes a training from the database.
     *
     * @param id the ID of the training to delete
     * @return true if the deletion succeeded
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM allenamenti WHERE id=?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Training deleted: ID " + id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting training", e);
        }
        return false;
    }

    /**
     * Retrieves a training by ID.
     *
     * @param id the training ID
     * @return the found training, null if not found
     */
    public Allenamento getById(int id) {
        String sql = "SELECT * FROM allenamenti WHERE id=?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAllenamento(rs);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving training", e);
        }
        return null;
    }

    /**
     * Retrieves all trainings from the database.
     *
     * @return list of all trainings
     */
    public List<Allenamento> getAll() {
        List<Allenamento> allenamenti = new ArrayList<>();
        String sql = "SELECT * FROM allenamenti ORDER BY data_modifica DESC";

        try (Statement stmt = getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                allenamenti.add(mapResultSetToAllenamento(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all trainings", e);
        }
        return allenamenti;
    }

    /**
     * Searches trainings based on provided criteria.
     *
     * @param nome name filter (null to exclude)
     * @param eta age filter (null to exclude)
     * @param tipo type filter (null to exclude)
     * @param description description filter (null to exclude)
     * @return list of trainings matching the criteria
     */
    public List<Allenamento> search(String tipo, String nome, String obiettivo, String eta, String faseAllenamento, String strumento, String descrizione) {
        List<Allenamento> allenamenti = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM allenamenti WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (tipo != null && !tipo.isEmpty()) {
            sql.append(" AND tipo LIKE ?");
            params.add("%" + tipo + "%");
        }
        if (nome != null && !nome.isEmpty()) {
            sql.append(" AND nome LIKE ?");
            params.add("%" + nome + "%");
        }
        if (obiettivo != null && !obiettivo.isEmpty()) {
            sql.append(" AND obiettivo LIKE ?");
            params.add("%" + obiettivo + "%");
        }
        if (eta != null && !eta.isEmpty()) {
            sql.append(" AND eta LIKE ?");
            params.add("%" + eta + "%");
        }
        if (faseAllenamento != null && !faseAllenamento.isEmpty()) {
            sql.append(" AND note LIKE ?");
            params.add("%" + faseAllenamento + "%");
        }
        if (strumento != null && !strumento.isEmpty()) {
            sql.append(" AND strumento LIKE ?");
            params.add("%" + strumento + "%");
        }
        if (descrizione != null && !descrizione.isEmpty()) {
            sql.append(" AND descrizione LIKE ?");
            params.add("%" + descrizione + "%");
        }

        sql.append(" ORDER BY data_modifica DESC");

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                allenamenti.add(mapResultSetToAllenamento(rs));
            }
        } catch (SQLException e) {
            logger.error("Error searching trainings", e);
        }
        return allenamenti;
    }

    /**
     * Maps a ResultSet to an Allenamento object.
     *
     * @param rs the ResultSet to map
     * @return an Allenamento object
     */
    private Allenamento mapResultSetToAllenamento(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String nome = rs.getString("nome");
        String eta = rs.getString("eta");
        String tipo = rs.getString("tipo");
        String obiettivo = rs.getString("obiettivo");
        String descrizione = rs.getString("descrizione");
        String note = rs.getString("note");
        String strumento = rs.getString("strumento");
        LocalDateTime dataCreazione = rs.getTimestamp("data_creazione") != null ?
                rs.getTimestamp("data_creazione").toLocalDateTime() : null;
        LocalDateTime dataModifica = rs.getTimestamp("data_modifica") != null ?
                rs.getTimestamp("data_modifica").toLocalDateTime() : null;

        return new Allenamento(id, nome, eta, tipo, obiettivo, descrizione, note, strumento, dataCreazione, dataModifica);
    }
}
