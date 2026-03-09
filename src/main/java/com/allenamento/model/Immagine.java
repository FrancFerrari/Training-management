package com.allenamento.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model class representing an Image associated with a Training.
 */
public class Immagine implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer allenamentoId;
    private String filename;
    private String path;
    private LocalDateTime dataUpload;

    /**
     * Empty constructor.
     */
    public Immagine() {
    }

    /**
     * Constructor with main parameters.
     */
    public Immagine(Integer allenamentoId, String filename, String path) {
        this.allenamentoId = allenamentoId;
        this.filename = filename;
        this.path = path;
    }

    /**
     * Complete constructor.
     */
    public Immagine(Integer id, Integer allenamentoId, String filename,
                   String path, LocalDateTime dataUpload) {
        this.id = id;
        this.allenamentoId = allenamentoId;
        this.filename = filename;
        this.path = path;
        this.dataUpload = dataUpload;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAllenamentoId() {
        return allenamentoId;
    }

    public void setAllenamentoId(Integer allenamentoId) {
        this.allenamentoId = allenamentoId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Immagine immagine = (Immagine) o;
        return Objects.equals(id, immagine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Immagine{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                '}';
    }
}
