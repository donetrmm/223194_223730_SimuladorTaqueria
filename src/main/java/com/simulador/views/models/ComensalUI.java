package com.simulador.views.models;

import com.almasb.fxgl.entity.Entity;
import com.simulador.models.Comensal;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ComensalUI extends Entity {
    private Comensal comensal;

    public ComensalUI(Comensal comensal, double x, double y) {
        this.comensal = comensal;
        this.setPosition(x, y);
        this.getViewComponent().addChild(new Circle(15, Color.GREEN));
    }

    public Comensal getComensal() {
        return comensal;
    }
}
