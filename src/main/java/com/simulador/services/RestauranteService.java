package com.simulador.services;

import com.simulador.models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestauranteService {
    private final Restaurante restaurante;
    private final Recepcionista recepcionista;
    private final List<Mesero> meseros;
    private final List<Cocinero> cocineros;
    private final ExecutorService pool;
    private boolean estado;

    public RestauranteService() {
        this.restaurante = new Restaurante(
                20,
                (int)(20 * 0.10),
                (int)(20 * 0.15)
        );
        this.recepcionista = new Recepcionista(restaurante);
        this.meseros = new ArrayList<>();
        this.cocineros = new ArrayList<>();
        this.pool = Executors.newCachedThreadPool();
        this.estado = false;
    }

    public void iniciar() {
        if (estado) return;
        estado = true;

        // lanzamos los hilos
        pool.submit(recepcionista);

        for (int i = 0; i < (int)(20 * 0.10); i++) {
            Mesero mesero = new Mesero(i, restaurante);
            meseros.add(mesero);
            pool.submit(mesero);
        }

        for (int i = 0; i < (int)(20 * 0.15); i++) {
            Cocinero cocinero = new Cocinero(i, restaurante);
            cocineros.add(cocinero);
            pool.submit(cocinero);
        }
    }

    public void detener() {
        if (!estado) return;
        estado = false;

        recepcionista.detener();

        pool.shutdownNow();
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public int getBufferPedidos() {
        return restaurante.getBufferPedidos();
    }

    public int getBufferCocina() {
        return restaurante.getBufferCocina();
    }

    public int getPedidosPreparados() {
        return restaurante.getBufferPedidoPreparado();
    }

    public int getComensalesRecibidos() {
        return recepcionista.getComensalesRecibidos();
    }

    public int getColaDeEntrada() {
        return restaurante.getComensalesEnEspera();
    }

    public List<Comensal> getAllComensales() {
        return restaurante.getAllComensales();
    }


    public int getPedidosCocinandose() {
        return restaurante.getPedidosCocinandose();
    }
}
