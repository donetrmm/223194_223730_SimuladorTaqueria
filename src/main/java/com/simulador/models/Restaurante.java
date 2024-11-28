package com.simulador.models;

import java.util.concurrent.*;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class Restaurante {
    private final int capacidad;
    private int clientes;
    private int clientesEnEspera;
    private final Queue<Orden> bufferPedidos;
    private final Queue<Orden> bufferCocina;
    private final Queue<Orden> bufferPedidoPreparado;
    private final Queue<Orden> bufferPedidoListo;
    private final ArrayList<Comensal> allComensales;

    private final Semaphore mesasDisponibles;
    private final Semaphore meserosDisponibles;
    private final Semaphore cocinerosDisponibles;

    public Restaurante(int capacidad, int numMeseros, int numCocineros) {
        this.capacidad = capacidad;
        this.clientes = 0;
        this.clientesEnEspera = 0;
        this.bufferPedidos = new LinkedList<>();
        this.bufferCocina = new LinkedList<>();
        this.bufferPedidoPreparado = new LinkedList<>();
        this.bufferPedidoListo = new LinkedList<>();
        this.allComensales = new ArrayList<>();
        this.mesasDisponibles = new Semaphore(capacidad, true);
        this.meserosDisponibles = new Semaphore(numMeseros, true);
        this.cocinerosDisponibles = new Semaphore(numCocineros, true);
    }

    public synchronized boolean intentarIngresarComensal(Comensal comensal) throws InterruptedException {
        if (clientes >= capacidad) {
            clientesEnEspera++;
            System.out.println("[Comensal " + comensal.getId() +
                    "] en espera. Restaurante lleno (" + clientes + "/" + capacidad + ")");
            return false;
        }
        mesasDisponibles.acquire();
        clientes++;
        allComensales.add(comensal);
        System.out.println("[Comensal " + comensal.getId() +
                "] ingresó al restaurante (" + clientes + "/" + capacidad + ")");
        return true;
    }


    public synchronized int getComensalesEnEspera() {
        return clientesEnEspera;
    }


    //meseros
    public synchronized Orden getNuevoPedido() throws InterruptedException {
        while (bufferPedidos.isEmpty()) {
            wait();
        }
        meserosDisponibles.acquire();
        return bufferPedidos.poll();
    }

    public synchronized void entregarPedidoACocina(Orden orden) {
        orden.setEstado("EnProceso");
        bufferCocina.offer(orden);
        System.out.println("[Orden " + orden.getId() + "] agregada a la cola de cocina. Total ordenes por cocinar: " + bufferCocina.size());
        notifyAll();
    }

    public synchronized void entregarPedidoAComensal(Orden orden) {
        orden.setEstado("Entregada");
        meserosDisponibles.release();
        notifyAll();
    }

    // Cocineros
    public synchronized Orden getPedidoParaCocinar() throws InterruptedException {
        while (bufferCocina.isEmpty()) {
            wait();
        }
        cocinerosDisponibles.acquire();
        Orden orden = bufferCocina.poll();
        System.out.println("Cocinero tomó orden " + orden.getId() + " para cocinar. Quedan por cocinar: " + bufferCocina.size());
        return orden;
    }

    public synchronized void marcarOrdenComoLista(Orden orden) {
        orden.setEstado("Lista");
        bufferPedidoPreparado.offer(orden);
        cocinerosDisponibles.release();
        System.out.println("[Orden " + orden.getId() + "] marcada como lista");
        notifyAll();
    }


    // Comensales
    public synchronized void agregarNuevaOrden(Orden orden) {
        bufferPedidos.offer(orden);
        notifyAll();
    }

    public synchronized void salirDelRestaurante(Comensal comensal) {
        clientes--;
        mesasDisponibles.release();
        System.out.println("[Comensal " + comensal.getId() + "] ha salido del restaurante. " +
                "Clientes actuales: " + clientes);
    }

    public synchronized int getComensales() {
        return clientes;
    }

    public synchronized int getBufferPedidos() {
        return bufferPedidos.size();
    }

    public synchronized int getBufferCocina() {
        int ordenesSiendoCocinadas = (int)(20 * 0.15) - cocinerosDisponibles.availablePermits();
        return bufferCocina.size() + ordenesSiendoCocinadas;
    }

    public synchronized int getBufferPedidoPreparado() {
        return bufferPedidoPreparado.size();
    }

    public synchronized int getPedidosCocinandose() {
        return (int)(20 * 0.15) - cocinerosDisponibles.availablePermits();
    }

    public synchronized List<Comensal> getComensalesActuales() {
        List<Comensal> comensales = new ArrayList<>();

        for (Orden orden : bufferPedidos) {
            comensales.add(orden.getComensal());
        }
        for (Orden orden : bufferCocina) {
            comensales.add(orden.getComensal());
        }
        for (Orden orden : bufferPedidoPreparado) {
            Comensal comensal = orden.getComensal();
            comensales.add(comensal);
        }
        return comensales;
    }

    public synchronized void getAndSetComensalComiendo(Comensal comensal){
        for (Comensal comensals : allComensales){
            if (comensals.getId() == comensal.getId()){
                comensals.setEstado("Comiendo");
                break;
            }
        }
    }

    public synchronized void getAndSetComensalTerminado(Comensal comensal){
        for (Comensal comensals : allComensales){
            if (comensals.getId() == comensal.getId()){
                comensals.setEstado("Terminado");
                break;
            }
        }
    }

    public synchronized ArrayList<Comensal> getAllComensales(){
        return allComensales;
    }
}
