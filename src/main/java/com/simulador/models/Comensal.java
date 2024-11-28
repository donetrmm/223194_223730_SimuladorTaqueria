package com.simulador.models;

public class Comensal implements Runnable {

    private static final String ESTADO_ESPERANDO_RECEPCIONISTA = "EsperandoRecepcionista";
    private static final String ESTADO_COMIENDO = "Comiendo";
    private static final String ESTADO_TERMINADO = "Terminado";

    private final int id;
    private String estado;
    private Orden orden;
    private final Restaurante restaurante;

    private static final long TIEMPO_ESPERA_MS = 500L;
    private static final long TIEMPO_COMER_MS = 10 * 1000L;

    public Comensal(int id) {
        this(id, null);
    }

    public Comensal(int id, Restaurante restaurante) {
        this.id = id;
        this.restaurante = restaurante;
        this.estado = ESTADO_ESPERANDO_RECEPCIONISTA;
    }


    @Override
    public void run() {
        try {
            esperarOrden();
            comer();
            finalizar();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Comensal " + id + "] interrumpido.");
        }
    }

    private void esperarOrden() throws InterruptedException {
        while (orden == null || !orden.getEstado().equals("Entregada")) {
            Thread.sleep(TIEMPO_ESPERA_MS);
        }
        log("recibió la orden y está listo para comer");
    }

    private void comer() throws InterruptedException {
        estado = ESTADO_COMIENDO;
        restaurante.getAndSetComensalComiendo(this);

        log("está comiendo");
        Thread.sleep(TIEMPO_COMER_MS);
    }

    private void finalizar() {
        estado = ESTADO_TERMINADO;
        restaurante.getAndSetComensalTerminado(this);
        if (orden != null) {
            orden.setEstado("Terminada");
        }
        if (restaurante != null) {
            restaurante.salirDelRestaurante(this);
        }
        log("ha terminado y se retira del restaurante");
    }

    public String getEstado() {
        return estado;
    }

    public int getId() {
        return id;
    }
    private void log(String mensaje) {
        System.out.println("[Comensal " + id + "] " + mensaje);
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

}
