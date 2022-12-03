package org.example;

import Engine2D.Rectangle;
import Engine2D.ShapeObject;
import UnityMath.Vector2;

public class Cart extends ShapeObject {
    private float speed;
    public Cart(String name, int id, Rectangle rectangle) {
        super(name, id);
        super.add(rectangle);
        this.speed = 1;
    }
    public void move(){
        if(Input.isLeftMove()){
            this.move(new Vector2(-1, 0).mul(speed));
        }else if(Input.isRightMove()){
            this.move(new Vector2(1, 0).mul(speed));
        }
    }
    public Vector2 getPosition(){
        return this.position;
    }
}
