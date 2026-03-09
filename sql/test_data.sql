-- Script di dati di test per popolare il database
-- Esegui questo script dopo il primo avvio per avere dati di esempio

-- Dati di test per la tabella allenamenti
INSERT INTO allenamenti (nome, eta, tipo, descrizione, note) VALUES 
(
    'Allenamento Gambe Completo',
    '20-30',
    'Forza',
    'Allenamento completo per gli arti inferiori. Include esercizi di quadricipiti, femorali e polpacci. Durata: 60 minuti.',
    'Riposa 48-72 ore prima di ripetere. Mantieni una buona postura durante gli esercizi.'
),
(
    'HIIT Cardio 30 minuti',
    '25-35',
    'Cardio',
    'Allenamento ad alta intensità a intervalli. Combina sprinti ad intensità massima con recupero attivo. Perfetto per bruciare calorie in poco tempo.',
    'Assicurati di essere in buona salute cardiovascolare. Inizia con sessioni più corte se principiante.'
),
(
    'Yoga per la Mobilità',
    'Tutti',
    'Mobilità',
    'Sessione di yoga focalizzata su migliorare la mobilità articolare. Include pose di riscaldamento, stretching dinamico e pose di rilassamento.',
    'Perfetto da fare al mattino. Può essere combinato con altri allenamenti come cool-down.'
),
(
    'Core Stability',
    '18-50',
    'Resistenza',
    'Allenamento mirato per rafforzare i muscoli del core. Include plank, dead bugs, pallof press e rotations. Durata: 40 minuti.',
    'Il core stabile è essenziale per qualsiasi atleta. Esegui lentamente, focalizzandoti sulla forma.'
),
(
    'Stretching Avanzato',
    '30-60',
    'Flessibilità',
    'Sessione completa di stretching statico e PNF stretching. Lavora su tutti i principali gruppi muscolari. Durata: 50 minuti.',
    'Perfetto dopo un allenamento intenso. Non saltare i muscoli stretti.'
),
(
    'Balance Training per Anziani',
    '60+',
    'Equilibrio',
    'Allenamento appositamente progettato per anziani, focalizzato su migliorare l''equilibrio e prevenire cadute. Include esercizi propriocettivi e di mobilità.',
    'Esegui sempre vicino a un supporto. Se necessario, usar bastoni o barre parallele.'
);

-- Nota: I dati sulle immagini devono essere aggiunti manualmente caricando immagini dall'applicazione
-- Questo perché le immagini devono essere salvate fisicamente nel filesystem in data/images/

-- Se vuoi aggiungere immagini manualmente al database (sono già presenti nel filesystem):
-- INSERT INTO immagini (allenamento_id, filename, path) VALUES 
-- (1, 'gambe-1.jpg', 'data/images/uuid-1.jpg'),
-- (2, 'cardio-1.png', 'data/images/uuid-2.png');

-- Seleziona per verificare i dati inseriti
SELECT * FROM allenamenti;
