-- Schema SQL per il database Allenamento Manager
-- Database: SQLite

-- Tabella allenamenti
CREATE TABLE IF NOT EXISTS allenamenti (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    eta TEXT NOT NULL,
    tipo TEXT NOT NULL,
    obiettivo TEXT,
    descrizione TEXT,
    note TEXT,
    strumento TEXT,
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_modifica TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella per le immagini associate agli allenamenti
CREATE TABLE IF NOT EXISTS immagini (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    allenamento_id INTEGER NOT NULL,
    filename TEXT NOT NULL,
    path TEXT NOT NULL,
    data_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (allenamento_id) REFERENCES allenamenti(id) ON DELETE CASCADE
);

-- Indici per migliorare le performance di ricerca
CREATE INDEX IF NOT EXISTS idx_allenamenti_nome ON allenamenti(nome);
CREATE INDEX IF NOT EXISTS idx_allenamenti_eta ON allenamenti(eta);
CREATE INDEX IF NOT EXISTS idx_allenamenti_tipo ON allenamenti(tipo);
CREATE INDEX IF NOT EXISTS idx_immagini_allenamento_id ON immagini(allenamento_id);
