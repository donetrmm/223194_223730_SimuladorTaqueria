package com.simulador.views;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.simulador.services.RestauranteService;
import com.simulador.models.*;
import com.simulador.views.models.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameApp extends GameApplication {
    private RestauranteService restauranteService;
    private List<Entity> mesas;
    private List<MeseroUI> meseros;
    private List<CocineroUI> cocineros;
    private Text detallesText;
    private Map<Integer, ComensalUI> comensales;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1200);
        settings.setHeight(800);
        settings.setTitle("Taquería El Mike");
        settings.setVersion("1.0");
    }

    @Override
    protected void initGame() {
        restauranteService = new RestauranteService();
        mesas = new ArrayList<>();
        meseros = new ArrayList<>();
        cocineros = new ArrayList<>();
        comensales = new HashMap<>();

        for (int i = 0; i < 20; i++) {
            Entity mesa = FXGL.entityBuilder()
                    .at(100 + (i % 5) * 150, 100 + (i / 5) * 150)
                    .view(new Rectangle(40, 40, Color.BLACK))
                    .buildAndAttach();
            mesas.add(mesa);
        }

        for (int i = 0; i < (int)(20 * 0.10); i++) {
            MeseroUI meseroUI = new MeseroUI(
                    new Mesero(i, restauranteService.getRestaurante()),
                    800,
                    100 + i * 100
            );
            meseros.add(meseroUI);
            FXGL.getGameWorld().addEntity(meseroUI);
        }

        for (int i = 0; i < (int)(20 * 0.15); i++) {
            CocineroUI cocineroUI = new CocineroUI(
                    new Cocinero(i, restauranteService.getRestaurante()),
                    1000,
                    100 + i * 100
            );
            cocineros.add(cocineroUI);
            FXGL.getGameWorld().addEntity(cocineroUI);
        }

        detallesText = new Text();
        detallesText.setTranslateX(50);
        detallesText.setTranslateY(700);
        FXGL.getGameScene().addUINode(detallesText);

        restauranteService.iniciar();

        FXGL.run(() -> actualizarDetalles(), Duration.seconds(1));

        FXGL.run(() -> actualizarComensales(), Duration.seconds(0.2));


        Stage stage = FXGL.getPrimaryStage();
        stage.setOnCloseRequest(event -> {
            restauranteService.detener();
            System.exit(0);
        });
    }

    private void actualizarDetalles() {
        String stats = String.format("""
                Clientes en espera: %d
                Órdenes esperando mesero: %d
                Órdenes en cola de cocina: %d
                Órdenes siendo cocinadas: %d
                Órdenes listas: %d
                Total clientes recibidos: %d
                """,
                restauranteService.getColaDeEntrada(),
                restauranteService.getBufferPedidos(),
                restauranteService.getBufferCocina(),
                restauranteService.getPedidosCocinandose(),
                restauranteService.getPedidosPreparados(),
                restauranteService.getComensalesRecibidos());

        detallesText.setText(stats);
    }

    private void actualizarComensales() {
        List<Comensal> comensalesActuales = restauranteService.getComensalesActuales();

        Iterator<Map.Entry<Integer, ComensalUI>> it = comensales.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ComensalUI> entry = it.next();
            if (!comensalesActuales.contains(entry.getValue().getComensal())) {
                FXGL.getGameWorld().removeEntity(entry.getValue());
                it.remove();
                System.out.println("Removida visualización del comensal " + entry.getKey());
            }
        }

        for (Comensal comensal : comensalesActuales) {
            if (!comensales.containsKey(comensal.getId())) {
                int mesaLibre = encontrarMesaLibre();
                if (mesaLibre >= 0) {
                    double x = 100 + (mesaLibre % 5) * 150;
                    double y = 100 + (mesaLibre / 5) * 150;

                    ComensalUI comensalUI = new ComensalUI(comensal, x, y);
                    comensales.put(comensal.getId(), comensalUI);
                    FXGL.getGameWorld().addEntity(comensalUI);
                }
            }
        }
    }

    private int encontrarMesaLibre() {
        boolean[] mesasOcupadas = new boolean[20];

        for (ComensalUI entity : comensales.values()) {
            int mesaIndex = calcularMesaIndex(entity);
            if (mesaIndex >= 0 && mesaIndex < mesasOcupadas.length) {
                mesasOcupadas[mesaIndex] = true;
            }
        }

        for (int i = 0; i < mesasOcupadas.length; i++) {
            if (!mesasOcupadas[i]) {
                return i;
            }
        }

        return -1;
    }

    private int calcularMesaIndex(ComensalUI entity) {
        int mesaIndex = (int)((entity.getX() - 100) / 150) + ((int)((entity.getY() - 100) / 150) * 5);
        return mesaIndex;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
