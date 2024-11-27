package com.simulador.views.models;

import com.almasb.fxgl.entity.Entity;
import com.simulador.models.Cocinero;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CocineroUI extends Entity {
    private Cocinero cocinero;

    public CocineroUI(Cocinero cocinero, double x, double y) {
        this.cocinero = cocinero;
        this.setPosition(x, y);
        this.getViewComponent().addChild(new Rectangle(30, 30, Color.RED));
    }
}
