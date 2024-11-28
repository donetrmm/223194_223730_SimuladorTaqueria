
package com.simulador.views;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.simulador.models.Cocinero;
import com.simulador.models.Comensal;
import com.simulador.models.Mesero;
import com.simulador.services.RestauranteService;
import com.simulador.views.models.CocineroUI;
import com.simulador.views.models.ComensalUI;
import com.simulador.views.models.MeseroUI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameApp extends GameApplication {
    private RestauranteService restauranteService;
    private List<Entity> mesas;
    private List<MeseroUI> meseros;
    private List<CocineroUI> cocineros;
    private Text detallesText;
    private Map<Integer, ComensalUI> comensales;

    public GameApp() {
    }

    protected void initSettings(GameSettings settings) {
        settings.setWidth(1200);
        settings.setHeight(800);
        settings.setTitle("Taquería Endcity");
        settings.setVersion("1.0");
    }

    protected void initGame() {
        this.restauranteService = new RestauranteService();
        this.mesas = new ArrayList();
        this.meseros = new ArrayList();
        this.cocineros = new ArrayList();
        this.comensales = new HashMap();

        int i;
        TransformComponent cocineroComponent;
        for(i = 0; i < 20; ++i) {
            Entity mesa = FXGL.entityBuilder().at((double)(-80 + i % 5 * 150), (double)(-110 + i / 5 * 150)).viewWithBBox("mesa.png").buildAndAttach();
            this.mesas.add(mesa);
            cocineroComponent = mesa.getTransformComponent();
            cocineroComponent.setScaleY(0.2);
            cocineroComponent.setScaleX(0.2);
        }

        for(i = 0; i < 2; ++i) {
            MeseroUI meseroUI = new MeseroUI(new Mesero(i, this.restauranteService.getRestaurante()), 850.0, (double)(260 + i * 100));
            this.meseros.add(meseroUI);
            cocineroComponent = meseroUI.getTransformComponent();
            cocineroComponent.setScaleY(0.2);
            cocineroComponent.setScaleX(0.2);
            FXGL.getGameWorld().addEntity(meseroUI);
        }

        for(i = 0; i < 3; ++i) {
            CocineroUI cocineroUI = new CocineroUI(new Cocinero(i, this.restauranteService.getRestaurante()), 1000.0, (double)(160 + i * 100));
            this.cocineros.add(cocineroUI);
            cocineroComponent = cocineroUI.getTransformComponent();
            cocineroComponent.setScaleY(0.4);
            cocineroComponent.setScaleX(0.4);
            FXGL.getGameWorld().addEntity(cocineroUI);
        }

        this.detallesText = new Text();
        this.detallesText.setTranslateX(900.0);
        this.detallesText.setTranslateY(50.0);
        FXGL.getGameScene().addUINode(this.detallesText);
        this.restauranteService.iniciar();
        FXGL.run(() -> {
            this.actualizarDetalles();
        }, Duration.seconds(1.0));
        FXGL.run(() -> {
            this.actualizarComensales();
        }, Duration.seconds(0.2));
        Stage stage = FXGL.getPrimaryStage();
        stage.setOnCloseRequest((event) -> {
            this.restauranteService.detener();
            System.exit(0);
        });
    }

    private void actualizarDetalles() {
        String stats = String.format("Clientes en espera: %d\nÓrdenes esperando mesero: %d\nÓrdenes en cola de cocina: %d\nÓrdenes siendo cocinadas: %d\nÓrdenes listas: %d\nTotal clientes recibidos: %d\n", this.restauranteService.getColaDeEntrada(), this.restauranteService.getBufferPedidos(), this.restauranteService.getBufferCocina(), this.restauranteService.getPedidosCocinandose(), this.restauranteService.getPedidosPreparados(), this.restauranteService.getComensalesRecibidos());
        this.detallesText.setText(stats);
    }
    private void actualizarComensales() {
        List<Comensal> comensalesActuales = this.restauranteService.getAllComensales();

        Iterator<Map.Entry<Integer, ComensalUI>> it = new ArrayList<>(this.comensales.entrySet()).iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ComensalUI> entry = it.next();
            Comensal comensalAsociado = entry.getValue().getComensal();

            if (comensalAsociado.getEstado().equals("Terminado")) {
                FXGL.getGameWorld().removeEntity(entry.getValue());
                this.comensales.remove(entry.getKey());
                System.out.println("Comensal terminado eliminado visualmente: " + comensalAsociado.getId());
            }
        }

        for (Comensal comensal : comensalesActuales) {
            if (!this.comensales.containsKey(comensal.getId())) {
                if (!comensal.getEstado().equals("Terminado")) {
                    int mesaLibre = this.encontrarMesaLibre();
                    if (mesaLibre >= 0) {
                        double x = (100 + mesaLibre % 5 * 150);
                        double y = (100 + mesaLibre / 5 * 150);
                        ComensalUI comensalUI = new ComensalUI(comensal, x, y);
                        this.comensales.put(comensal.getId(), comensalUI);
                        FXGL.getGameWorld().addEntity(comensalUI);
                    }
                }
            } else {
                ComensalUI comensalUI = this.comensales.get(comensal.getId());
                comensalUI.actualizarImagen();
            }
        }
    }



    private int encontrarMesaLibre() {
        boolean[] mesasOcupadas = new boolean[20];
        Iterator var2 = this.comensales.values().iterator();

        while(var2.hasNext()) {
            ComensalUI entity = (ComensalUI)var2.next();
            int mesaIndex = this.calcularMesaIndex(entity);
            if (mesaIndex >= 0 && mesaIndex < mesasOcupadas.length) {
                mesasOcupadas[mesaIndex] = true;
            }
        }

        for(int i = 0; i < mesasOcupadas.length; ++i) {
            if (!mesasOcupadas[i]) {
                return i;
            }
        }

        return -1;
    }

    private int calcularMesaIndex(ComensalUI entity) {
        int mesaIndex = (int)((entity.getX() - 100.0) / 150.0) + (int)((entity.getY() - 100.0) / 150.0) * 5;
        return mesaIndex;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
