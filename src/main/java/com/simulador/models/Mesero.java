package com.simulador.models;

public class Mesero implements Runnable {
    private static final String ESTADO_DISPONIBLE = "Disponible";
    private static final String ESTADO_TOMANDO_PEDIDO = "TomandoPedido";
    private static final String ESTADO_LLEVANDO_PEDIDO_COCINERO = "LlevandoPedidoCocinero";
    private static final String ESTADO_ESPERANDO_PEDIDO = "EsperandoPedido";
    private static final String ESTADO_SIRVIENDO = "Sirviendo";

    private final int id;
    private String estado;
    private Orden orden;
    private final Restaurante restaurante;

    private static final long TIEMPO_ESPERA_MS = 3000L;
    private static final long TIEMPO_TOMAR_PEDIDO_MS = 1500L;
    private static final long TIEMPO_SERVICIO_MS = 1500L;

    public Mesero(int id, Restaurante restaurante) {
        this.id = id;
        this.restaurante = restaurante;
        this.estado = ESTADO_DISPONIBLE;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ejecutarEstado();
                Thread.sleep(TIEMPO_ESPERA_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Mesero " + id + "] interrumpido.");
            }
        }
    }

    private void ejecutarEstado() {
        switch (estado) {
            case ESTADO_DISPONIBLE -> esperarPedidos();
            case ESTADO_TOMANDO_PEDIDO -> tomarPedido();
            case ESTADO_LLEVANDO_PEDIDO_COCINERO -> llevarPedidoAlCocinero();
            case ESTADO_ESPERANDO_PEDIDO -> esperarPedidoTerminado();
            case ESTADO_SIRVIENDO -> servir();
            default -> throw new IllegalStateException("Estado desconocido: " + estado);
        }
    }

    private void esperarPedidos() {
        try {
            orden = restaurante.getNuevoPedido();
            if (orden != null) {
                orden.setMesero(this);
                estado = ESTADO_TOMANDO_PEDIDO;
                log("atendiendo orden " + orden.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void tomarPedido() {
        try {
            Thread.sleep(TIEMPO_TOMAR_PEDIDO_MS);
            estado = ESTADO_LLEVANDO_PEDIDO_COCINERO;
            log("tomó orden " + orden.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void llevarPedidoAlCocinero() {
        restaurante.entregarPedidoACocina(orden);
        estado = ESTADO_ESPERANDO_PEDIDO;
        log("llevó orden " + orden.getId() + " a cocina");
    }

    private void esperarPedidoTerminado() {
        if (orden.getEstado().equals("Lista")) {
            estado = ESTADO_SIRVIENDO;
            log("recogiendo orden " + orden.getId());
        }
    }

    private void servir() {
        try {
            Thread.sleep(TIEMPO_SERVICIO_MS);
            restaurante.entregarPedidoAComensal(orden);
            estado = ESTADO_DISPONIBLE;
            log("entregó orden " + orden.getId());
            orden = null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void log(String mensaje) {
        System.out.println("[Mesero " + id + "] " + mensaje);
    }
}
