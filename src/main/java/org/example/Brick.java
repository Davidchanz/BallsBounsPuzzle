package org.example;

import Engine2D.AbstractShape;
import Engine2D.Circle;
import Engine2D.Rectangle;
import Engine2D.ShapeObject;
import Engine2D.Text;

import java.awt.*;

public class Brick extends ShapeObject {
    private int score;
    public Brick(String name, int id, Rectangle rectangle) {
        super(name, id);
        super.add(rectangle);
        this.score = 20;
        body.add(new Text(String.valueOf(score), rectangle.position, Color.BLACK));
    }
    public Brick(String name, int id, Rectangle rectangle, int score) {
        super(name, id);
        super.add(rectangle);
        this.score = score;
        body.add(new Text(String.valueOf(score), rectangle.position, Color.BLACK));
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
}
