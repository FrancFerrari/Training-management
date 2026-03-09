package com.allenamento.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for managing SQLite database connection.
 * Implements the Singleton pattern to ensure a single connection instance.
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_FILENAME = "allenamenti.db";
    private final String dbUrl;

    /**
     * Private constructor for Singleton pattern.
     * Determines the app's base directory to position the DB.
     */
    private DatabaseConnection() {
        String dbPath = resolveDbPath();
        this.dbUrl = "jdbc:sqlite:" + dbPath;
        logger.info("Database path: " + dbPath);
        
        try {
            // Register SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(dbUrl);
            logger.info("SQLite database connection established");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            logger.error("SQLite driver not found", e);
            throw new RuntimeException("Driver SQLite non trovato", e);
        } catch (SQLException e) {
            logger.error("Error connecting to database", e);
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    /**
     * Gets the singleton instance of the database connection.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Gets the active connection.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(dbUrl);
            }
        } catch (SQLException e) {
            logger.error("Error connecting to database", e);
        }
        return connection;
    }

    /**
     * Determines the absolute path of the database file.
     * Tries to position the DB in the project directory.
     */
    private static String resolveDbPath() {
        try {
            java.net.URI codeSourceUri = DatabaseConnection.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI();
            Path codePath = Paths.get(codeSourceUri);

            Path baseDir;
            if (codePath.toString().endsWith(".jar")) {
                // Execution from JAR in target/
                Path jarDir = codePath.getParent();
                if (jarDir != null && jarDir.getFileName() != null
                        && jarDir.getFileName().toString().equals("target")) {
                    baseDir = jarDir.getParent().toAbsolutePath();
                } else {
                    baseDir = jarDir.toAbsolutePath();
                }
            } else {
                // Execution from classes (IDE / mvn exec)
                Path classesDir = codePath;
                if (classesDir.getFileName() != null
                        && classesDir.getFileName().toString().equals("classes")) {
                    Path targetDir = classesDir.getParent();
                    if (targetDir != null && targetDir.getFileName() != null
                            && targetDir.getFileName().toString().equals("target")) {
                        baseDir = targetDir.getParent().toAbsolutePath();
                    } else {
                        baseDir = classesDir.toAbsolutePath();
                    }
                } else {
                    baseDir = classesDir.toAbsolutePath();
                }
            }
            
            return baseDir.resolve(DB_FILENAME).toString();
        } catch (Exception e) {
            logger.warn("Cannot determine base directory, using CWD for DB", e);
        }
        return DB_FILENAME;
    }

    /**
     * Initializes the database by creating tables if they don't exist.
     */
    private void initializeDatabase() {
        String[] sqlStatements = {
            "CREATE TABLE IF NOT EXISTS allenamenti (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    nome TEXT NOT NULL," +
            "    eta TEXT NOT NULL," +
            "    tipo TEXT NOT NULL," +
            "    obiettivo TEXT," +
            "    descrizione TEXT," +
            "    note TEXT," +
            "    strumento TEXT," +
            "    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    data_modifica TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");",

            "CREATE TABLE IF NOT EXISTS immagini (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    allenamento_id INTEGER NOT NULL," +
            "    filename TEXT NOT NULL," +
            "    path TEXT NOT NULL," +
            "    data_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    FOREIGN KEY (allenamento_id) REFERENCES allenamenti(id) ON DELETE CASCADE" +
            ");",

            "CREATE INDEX IF NOT EXISTS idx_allenamenti_nome ON allenamenti(nome);",
            "CREATE INDEX IF NOT EXISTS idx_allenamenti_eta ON allenamenti(eta);",
            "CREATE INDEX IF NOT EXISTS idx_allenamenti_tipo ON allenamenti(tipo);",
            "CREATE INDEX IF NOT EXISTS idx_immagini_allenamento_id ON immagini(allenamento_id);"
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }

            ensureAllenamentiSchema();
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
        }
    }

    /**
     * Performs small schema migrations to maintain compatibility with existing DBs.
     */
    private void ensureAllenamentiSchema() throws SQLException {
        if (!columnExists("allenamenti", "obiettivo")) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("ALTER TABLE allenamenti ADD COLUMN obiettivo TEXT");
                logger.info("Column 'obiettivo' added to allenamenti table");
            }
        }

        if (!columnExists("allenamenti", "strumento")) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("ALTER TABLE allenamenti ADD COLUMN strumento TEXT");
                logger.info("Column 'strumento' added to allenamenti table");
            }
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        String sql = "PRAGMA table_info(" + tableName + ")";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String existingColumn = rs.getString("name");
                if (columnName.equalsIgnoreCase(existingColumn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed");
            }
        } catch (SQLException e) {
            logger.error("Error closing connection", e);
        }
    }
}
