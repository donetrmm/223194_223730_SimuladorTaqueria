package com.simulador.views.models;

import com.almasb.fxgl.entity.Entity;
import com.simulador.models.Mesero;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MeseroUI extends Entity {
    private Mesero mesero;

    public MeseroUI(Mesero mesero, double x, double y) {
        this.mesero = mesero;
        this.setPosition(x, y);
        this.getViewComponent().addChild(new Rectangle(30, 30, Color.BLUE));
    }

}
