package com.allenamento.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model class representing a Training in the database.
 * Contains all information related to a training.
 */
public class Allenamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String nome;
    private String eta;
    private String tipo;
    private String obiettivo;
    private String descrizione;
    private String note;
    private String strumento;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataModifica;
    private List<Immagine> immagini;

    /**
     * Empty constructor for creating new trainings.
     */
    public Allenamento() {
        this.immagini = new ArrayList<>();
    }

    /**
     * Constructor with parameters for creating a complete training.
     */
    public Allenamento(String nome, String eta, String tipo, String obiettivo, String descrizione, String note, String strumento) {
        this();
        this.nome = nome;
        this.eta = eta;
        this.tipo = tipo;
        this.obiettivo = obiettivo;
        this.descrizione = descrizione;
        this.note = note;
        this.strumento = strumento;
    }

    /**
     * Complete constructor with all parameters including ID and dates.
     */
    public Allenamento(Integer id, String nome, String eta, String tipo,
                      String obiettivo, String descrizione, String note, String strumento, LocalDateTime dataCreazione,
                      LocalDateTime dataModifica) {
        this();
        this.id = id;
        this.nome = nome;
        this.eta = eta;
        this.tipo = tipo;
        this.obiettivo = obiettivo;
        this.descrizione = descrizione;
        this.note = note;
        this.strumento = strumento;
        this.dataCreazione = dataCreazione;
        this.dataModifica = dataModifica;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getObiettivo() {
        return obiettivo;
    }

    public void setObiettivo(String obiettivo) {
        this.obiettivo = obiettivo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStrumento() {
        return strumento;
    }

    public void setStrumento(String strumento) {
        this.strumento = strumento;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public LocalDateTime getDataModifica() {
        return dataModifica;
    }

    public void setDataModifica(LocalDateTime dataModifica) {
        this.dataModifica = dataModifica;
    }

    public List<Immagine> getImmagini() {
        return immagini;
    }

    public void setImmagini(List<Immagine> immagini) {
        this.immagini = immagini != null ? immagini : new ArrayList<>();
    }

    public void addImmagine(Immagine immagine) {
        this.immagini.add(immagine);
    }

    public void removeImmagine(Immagine immagine) {
        this.immagini.remove(immagine);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Allenamento that = (Allenamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Allenamento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", eta='" + eta + '\'' +
                ", tipo='" + tipo + '\'' +
                ", obiettivo='" + obiettivo + '\'' +
                ", strumento='" + strumento + '\'' +
                '}';
    }
}
