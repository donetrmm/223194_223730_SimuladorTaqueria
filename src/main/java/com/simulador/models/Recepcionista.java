package com.simulador.models;

import com.simulador.utils.DistribucionPoison;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Recepcionista implements Runnable {

    private static final long TIEMPO_ESPERA_MS = 1000L;
    private static final double LARGO_INTERVALO_ARRIBO = 2.0;
    private static final double ANCHO_INTERVALO_ARRIBO = 2.0 * 2.5;

    private final Restaurante restaurante;
    private final DistribucionPoison distribucionPoison;
    private final BlockingQueue<Comensal> comensalesEspera;
    private int clientesRecibidos;
    private boolean estado;

    public Recepcionista(Restaurante restaurante) {
        this.restaurante = restaurante;
        this.distribucionPoison = new DistribucionPoison(1.0 / 2.0);
        this.comensalesEspera = new LinkedBlockingQueue<>();
        this.clientesRecibidos = 0;
        this.estado = true;
    }

    @Override
    public void run() {
        while (estado) {
            try {
                //Thread.sleep(TIEMPO_ESPERA_MS);
                atenderComensalesEnEspera();

                Comensal nuevoComensal = new Comensal(++clientesRecibidos);
                procesarNuevoComensal(nuevoComensal);

                double espera = distribucionPoison.nextBoundedInterArrivalTime(LARGO_INTERVALO_ARRIBO, ANCHO_INTERVALO_ARRIBO);
                Thread.sleep((long)(espera * 1000));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                detener();
            }
        }
    }

    private void atenderComensalesEnEspera() {
        while (!comensalesEspera.isEmpty()) {
            Comensal comensal = comensalesEspera.peek();
            try {
                if (entrarAlRestaurante(comensal)) {
                    comensalesEspera.poll();
                    System.out.println("Comensal " + comensal.getId() + " ingresó desde la cola de espera");
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void procesarNuevoComensal(Comensal nuevoComensal) throws InterruptedException {
        if (entrarAlRestaurante(nuevoComensal)) {
            System.out.println("Comensal " + nuevoComensal.getId() + " ha ingresado al restaurante");
        } else {
            System.out.println("Comensal " + nuevoComensal.getId() + " en cola de espera");
            comensalesEspera.offer(nuevoComensal);
        }
    }

    private boolean entrarAlRestaurante(Comensal comensal) throws InterruptedException {
        boolean acceso = restaurante.intentarIngresarComensal(comensal);

        if (acceso) {
            comensal.setEstado("EsperandoPedido");
            Orden nuevaOrden = new Orden(comensal.getId(), comensal);
            comensal.setOrden(nuevaOrden);

            restaurante.agregarNuevaOrden(nuevaOrden);

            Comensal comensalEnRestaurante = new Comensal(comensal.getId(), restaurante);
            comensalEnRestaurante.setOrden(nuevaOrden);
            comensalEnRestaurante.setEstado("EsperandoPedido");
            new Thread(comensalEnRestaurante).start();

            return true;
        }
        return false;
    }

    public void detener() {
        this.estado = false;
    }

    public int getComensalesRecibidos() {
        return clientesRecibidos;
    }
}
