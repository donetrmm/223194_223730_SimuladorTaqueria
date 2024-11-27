package com.simulador.models;

public class Cocinero implements Runnable {
    private static final String ESTADO_DISPONIBLE = "Disponible";
    private static final String ESTADO_COCINANDO = "Cocinando";
    private static final String ESTADO_ORDEN_TERMINADA = "OrdenTerminada";

    private final int id;
    private String estadoCocinero;
    private Orden orden;
    private final Restaurante restaurante;
    private static final long TIEMPO_ESPERA_MS = 6000L;

    public Cocinero(int id, Restaurante restaurante) {
        this.id = id;
        this.restaurante = restaurante;
        this.estadoCocinero = ESTADO_DISPONIBLE;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ejecutarEstado();
                Thread.sleep(TIEMPO_ESPERA_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Cocinero " + id + "] interrumpido.");
            }
        }
    }

    private void ejecutarEstado() {
        switch (estadoCocinero) {
            case ESTADO_DISPONIBLE -> esperarPedido();
            case ESTADO_COCINANDO -> cocinar();
            case ESTADO_ORDEN_TERMINADA -> notificarPedidoTerminado();
            default -> throw new IllegalStateException("Estado desconocido: " + estadoCocinero);
        }
    }

    private void esperarPedido() {
        try {
            orden = restaurante.getPedidoParaCocinar();
            if (orden != null) {
                orden.setCocinero(this);
                estadoCocinero = ESTADO_COCINANDO;
                log("comenzó a preparar la orden " + orden.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void cocinar() {
        try {
            log("cocinando la orden " + orden.getId());
            Thread.sleep(TIEMPO_ESPERA_MS);
            estadoCocinero = ESTADO_ORDEN_TERMINADA;
            log("terminó de preparar la orden " + orden.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void notificarPedidoTerminado() {
        if (orden == null) {
            log("intentó notificar una orden nula");
            return;
        }
        restaurante.marcarOrdenComoLista(orden);
        log("notificó que la orden " + orden.getId() + " está lista");
        estadoCocinero = ESTADO_DISPONIBLE;
        orden = null;
    }

    private void log(String mensaje) {
        System.out.println("[Cocinero " + id + "] " + mensaje);
    }
}
