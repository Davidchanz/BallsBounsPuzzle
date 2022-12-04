package org.example;

import Engine2D.*;
import Engine2D.Rectangle;
import UnityMath.Vector3;

import java.awt.*;

public class Brick extends ShapeObject {
    private int score;
    private int type;
    private Color color;
    public Brick(String name, int id, Rectangle rectangle, int score) {
        super(name, id);
        super.add(rectangle);
        this.score = score;
        body.add(new Text(String.valueOf(score), rectangle.position, Color.BLACK));
        this.type = 1;
        setColor(rectangle.color);
    }
    public Brick(String name, int id, Triangle triangle, int score, int type) {
        super(name, id);
        super.add(triangle);
        this.score = score;
        body.add(new Text(String.valueOf(score), triangle.position, Color.BLACK));
        this.type = type;
        setColor(triangle.color);
    }
    public void hit(){
        this.score--;
        Text text = (Text) (body.get(1));
        text.text = String.valueOf(score);

    }
    public int getScore() {
        return score;
    }
    public void setScore(int score){
        this.score = score;
        Text text = (Text) (body.get(1));
        text.text = String.valueOf(score);
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getColor() {
        return color != null? color.getRGB() : -1;
    }
}
