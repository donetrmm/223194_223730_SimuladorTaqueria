package com.simulador.models;

public class Orden {
    private final int id;
    private final Comensal comensal;
    private String estado;
    private Mesero mesero;
    private Cocinero cocinero;


    public Orden(int id, Comensal comensal) {
        this.id = id;
        this.comensal = comensal;
        this.estado = "Tomada";
    }

    public int getId() { return id; }
    public Comensal getComensal() { return comensal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setMesero(Mesero mesero) { this.mesero = mesero; }
    public void setCocinero(Cocinero cocinero) { this.cocinero = cocinero; }
}
