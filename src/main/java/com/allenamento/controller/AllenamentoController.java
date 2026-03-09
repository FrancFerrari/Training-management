package com.allenamento.controller;

import com.allenamento.dao.AllenamentoDAO;
import com.allenamento.dao.ImmagineDAO;
import com.allenamento.model.Allenamento;
import com.allenamento.model.Immagine;
import com.allenamento.util.ImageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Controller that manages application logic.
 * Acts as an intermediary between View (JavaFX) and Model (DAO).
 */
public class AllenamentoController {
    private static final Logger logger = LoggerFactory.getLogger(AllenamentoController.class);

    private final AllenamentoDAO allenamentoDAO;
    private final ImmagineDAO immagineDAO;
    private Allenamento allenamentoCorrente;

    /**
     * Constructor that initializes the DAOs.
     */
    public AllenamentoController() {
        this.allenamentoDAO = new AllenamentoDAO();
        this.immagineDAO = new ImmagineDAO();
    }

    /**
     * Creates a new training.
     *
     * @param nome training name
     * @param eta age or age range
     * @param tipo training type
     * @param obiettivo training objective
     * @param descrizione textual description
     * @param note training phase
     * @param strumento equipment used
     * @return the ID of the new training
     */
    public int createAllenamento(String nome, String eta, String tipo, String obiettivo, String descrizione, String note, String strumento) {
        Allenamento allenamento = new Allenamento(nome, eta, tipo, obiettivo, descrizione, note, strumento);
        return allenamentoDAO.insert(allenamento);
    }

    /**
     * Updates an existing training.
     *
     * @param allenamento the training to update
     * @return true if the update succeeded
     */
    public boolean updateAllenamento(Allenamento allenamento) {
        return allenamentoDAO.update(allenamento);
    }

    /**
     * Deletes a training.
     *
     * @param id the ID of the training to delete
     * @return true if the deletion succeeded
     */
    public boolean deleteAllenamento(int id) {
        Allenamento allenamento = allenamentoDAO.getById(id);
        if (allenamento != null) {
            // Also delete all associated images
            List<Immagine> immagini = immagineDAO.getByAllenamentoId(id);
            for (Immagine immagine : immagini) {
                ImageManager.deleteImage(immagine.getPath());
                immagineDAO.delete(immagine.getId());
            }
        }
        return allenamentoDAO.delete(id);
    }

    /**
     * Retrieves a training by ID.
     *
     * @param id the training ID
     * @return the found training
     */
    public Allenamento getAllenamento(int id) {
        Allenamento allenamento = allenamentoDAO.getById(id);
        if (allenamento != null) {
            List<Immagine> immagini = immagineDAO.getByAllenamentoId(id);
            allenamento.setImmagini(immagini);
            this.allenamentoCorrente = allenamento;
        }
        return allenamento;
    }

    /**
     * Retrieves all trainings.
     *
     * @return list of all trainings
     */
    public List<Allenamento> getAllAllenamenti() {
        List<Allenamento> allenamenti = allenamentoDAO.getAll();
        for (Allenamento allenamento : allenamenti) {
            List<Immagine> immagini = immagineDAO.getByAllenamentoId(allenamento.getId());
            allenamento.setImmagini(immagini);
        }
        return allenamenti;
    }

    /**
     * Searches trainings based on provided criteria.
     *
     * @param tipo type filter
     * @param nome name filter
     * @param obiettivo objective filter
     * @param eta age filter
     * @param faseAllenamento training phase filter
     * @param strumento equipment filter
     * @param descrizione description filter
     * @return list of trainings matching the criteria
     */
    public List<Allenamento> searchAllenamenti(String tipo, String nome, String obiettivo, String eta, String faseAllenamento, String strumento, String descrizione) {
        List<Allenamento> allenamenti = allenamentoDAO.search(tipo, nome, obiettivo, eta, faseAllenamento, strumento, descrizione);
        for (Allenamento allenamento : allenamenti) {
            List<Immagine> immagini = immagineDAO.getByAllenamentoId(allenamento.getId());
            allenamento.setImmagini(immagini);
        }
        return allenamenti;
    }

    /**
     * Adds an image to a training.
     *
     * @param allenamentoId the training ID
     * @param imageFile the image file to add
     * @return true if the addition succeeded
     */
    public boolean addImage(int allenamentoId, File imageFile) {
        try {
            String savedPath = ImageManager.saveImage(imageFile);
            if (savedPath != null) {
                Immagine immagine = new Immagine(allenamentoId, imageFile.getName(), savedPath);
                int id = immagineDAO.insert(immagine);
                return id > 0;
            }
        } catch (Exception e) {
            logger.error("Error adding image", e);
        }
        return false;
    }

    /**
     * Deletes an image from a training.
     *
     * @param immagineId the ID of the image to delete
     * @return true if the deletion succeeded
     */
    public boolean deleteImage(int immagineId) {
        Immagine immagine = immagineDAO.getById(immagineId);
        if (immagine != null) {
            ImageManager.deleteImage(immagine.getPath());
            return immagineDAO.delete(immagineId);
        }
        return false;
    }

    /**
     * Retrieves all images of a training.
     *
     * @param allenamentoId the training ID
     * @return list of images
     */
    public List<Immagine> getImagesByAllenamento(int allenamentoId) {
        return immagineDAO.getByAllenamentoId(allenamentoId);
    }

    /**
     * Sets the current training.
     *
     * @param allenamento the training to set as current
     */
    public void setAllenamentoCorrente(Allenamento allenamento) {
        this.allenamentoCorrente = allenamento;
    }

    /**
     * Gets the current training.
     *
     * @return the current training
     */
    public Allenamento getAllenamentoCorrente() {
        return allenamentoCorrente;
    }
}
