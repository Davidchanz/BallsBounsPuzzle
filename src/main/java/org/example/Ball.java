package org.example;

import Engine2D.Circle;
import Engine2D.ShapeObject;
import UnityMath.Vector2;

import java.awt.*;

public class Ball extends ShapeObject {
    public Vector2 dir;
    public boolean fly;
    public Ball(String name, int id, Circle circle) {
        super(name, id);
        super.add(circle);
        this.fly = false;
        this.dir = new Vector2();
    }
    public Vector2 getPosition(){
        return this.position;
    }
}
