package com.simulador;

import com.simulador.services.RestauranteService;

public class Main {
    public static void main(String[] args) {
        RestauranteService restauranteService = new RestauranteService();
        restauranteService.iniciar();
    }
}