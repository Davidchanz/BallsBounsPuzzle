package org.example;

import Engine2D.Alphabet.S;
import Engine2D.Circle;
import Engine2D.Rectangle;
import Engine2D.Scene;
import UnityMath.Vector2;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends JFrame {
    private Scene scene;
    private int WIDTH;
    private int HEIGHT;
    private Ball[] balls;
    private int ball_count;
    private int brick_size;
    private int ball_radius;
    private ArrayList<Brick> bricks;
    private int brick_count;
    private Cart cart;
    private int cart_width;
    private int cart_height;
    private Input input;
    private boolean game;
    private boolean fly;
    private Vector2 borderV;
    private Vector2 borderH;
    private int lvl;
    private int score;
    private boolean fl;
    private int countFlyBalls;
    private int delay;
    private float acceleration;
    private Map map;
    private Vector2 startPoint;
    Main(){
        ini();
        this.setSize(new Dimension(WIDTH+10, HEIGHT+45));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(scene);
        this.addMouseMotionListener(input.getMouseAdapter());
        this.addMouseListener(input.getMouseAdapter());
        this.addKeyListener(input.getKeyAdapter());
        this.setVisible(true);
    }
    private void ini(){
        WIDTH = 800;
        HEIGHT = 600;
        startPoint = new Vector2(0,-HEIGHT/2+50);
        acceleration = 1.0f;
        fl = true;
        score = 0;
        lvl = 0;
        delay = 2 + (lvl * 10);
        fly = false;
        game = true;
        input = new Input();
        scene = new Scene(WIDTH, HEIGHT);
        scene.setCenterVisible(true);
        scene.setCoordVisible(true);
        scene.setBorderVisible(true);
        ball_count = 50;
        countFlyBalls = 0;
        balls = new Ball[ball_count];
        ball_radius = 10;
        int border_size = 1;
        for(int i = 0; i < ball_count; i++)
            balls[i] = new Ball("ball", 1, new Circle(ball_radius, new Vector2(startPoint), Color.WHITE));
        for (Ball ball : balls)
            scene.add(ball);
        brick_size = 15;
        map = loadMap("src/main/resources/save.txt");
        scene.setBorder(border_size, Color.BLACK);
        int shift = border_size*3 + brick_size/2;
        borderV = new Vector2(-WIDTH/2 + shift, WIDTH/2 - shift);
        borderH = new Vector2(-HEIGHT/2 + shift, HEIGHT/2 - shift);
        brick_count = map.objects.size();
        /*int row = 5;
        int col = 20;*/
        bricks = new ArrayList<>();
        /*int count = 0;
        int shiftV = 0;
        int shiftH = 0;*/
        for(int i = 0; i < brick_count; i++){
            /*shiftH += brick_size * 2;
            bricks.add(new Brick("brick", 2, new Rectangle(brick_size, brick_size, new Vector2(-WIDTH/2 + brick_count + shiftH,HEIGHT/2-50 - shiftV), Color.RED)));
            if(++count == brick_count / row){
                count = 0;
                shiftV += brick_size * 2;
                shiftH = 0;
            }*/
            bricks.add(map.objects.get(i));
        }
        for (Brick brick : bricks){
            scene.add(brick);
        }
        /*cart_width = 40;
        cart_height = 2;
        cart = new Cart("cart", 1, new Rectangle(cart_height, cart_width, new Vector2(0, -250 - ball_radius-cart_height), Color.RED));
        scene.add(cart);*/
    }
    private Map loadMap(String path){
        try{
           return Map.loadMap(path, brick_size);
        }catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
    private void start(){
        Toolkit tk = Toolkit.getDefaultToolkit();
        long summ_time = 0;
        long end_time = 0;
        long start_time = 0;

        while (game) {
            end_time = System.currentTimeMillis();
            summ_time += end_time - start_time;
            //delay = 1 + (lvl * 10);TODO lvl UP
            if (delay <= 1) delay = 1;
            if (summ_time >= delay) {
                tk.sync();

                //cart.move();
                game();

                summ_time = 0L;
                scene.repaint();
            }
            start_time = end_time;
        }
    }
    public void game(){
        if(fly){
            int j = 0;
            int count = 0;
            while(fl) {
                for (int i = 0; i < j; i++) {
                    if(balls[i].fly) {
                        balls[i].move(new Vector2(balls[i].dir));
                        if (isCollision(balls[i])) {
                            fly = false;
                            fl = true;
                        }
                    }
                }
                scene.repaint();
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                count++;
                if(count >= ball_radius * 2){
                    j++;
                    count = 0;
                }
                if(j > ball_count)
                    fl = false;
            }

            for(int i = 0; i < ball_count; i++){
                if(balls[i].fly) {
                    balls[i].move(balls[i].dir);
                    if (isCollision(balls[i])) {
                        fly = false;
                        fl = true;
                    }
                }
            }
            if(!fly){
                for (var ball: balls) {
                    Vector2 offset = new Vector2(startPoint).sub(ball.position);
                    ball.move(offset);
                }
            }
        }else {
            if(Input.isClicked()){
                countFlyBalls = ball_count;
                int mouseX = Input.getMouseX();
                int mouseY = Input.getMouseY();
                Vector2 cursor = new Vector2(mouseX, mouseY);
                for (var ball: balls) {
                    ball.dir = new Vector2(cursor).sub(ball.getPosition()).nor().mul(acceleration);
                    ball.fly = true;
                }
                fly = true;
            }
        }
    }
    private boolean isCollision(Ball ball){
        if(ball.getPosition().x < borderV.x) {
            float ang = ball.dir.angleDeg();
            ball.dir.rotateDeg(-2 * (ang - 90));
            return false;
        }
        else if(ball.getPosition().x > borderV.y){
            float ang = ball.dir.angleDeg();
            ball.dir.rotateDeg(2 * (90 - ang));
            return false;
        }
        else if(ball.getPosition().y < borderH.x){
            float ang = ball.dir.angleDeg();
            ball.dir.rotateDeg(2 * -ang);
            ball.fly = false;
            countFlyBalls--;
            return countFlyBalls <= 0;
        }
        else if(ball.getPosition().y > borderH.y){
            float ang = ball.dir.angleDeg();
            ball.dir.rotateDeg(-2 * ang);
            return false;
        }/*else if(ball.getPosition().x <= cart.getPosition().x + cart_width &&
                ball.getPosition().x >= cart.getPosition().x - cart_width &&
                ball.getPosition().y >= cart.getPosition().y - cart_height &&
                ball.getPosition().y <= cart.getPosition().y + cart_height){
            float ang = dir.angleDeg();
            dir.rotateDeg(2 * -ang);
            return false;
        }*/
        BrickCollision(ball);
        return false;
    }
    public void BrickCollision(Ball ball){
        var bricks_old = bricks.toArray(new Brick[0]);
        int brick_count_old = brick_count;
        for(int i = 0; i < brick_count_old; i++){
            Brick brick = bricks_old[i];
            float x = brick.position.x;
            float y = brick.position.y;
            float range = (float)Math.sqrt((Math.abs(x - ball.getPosition().x)*Math.abs(x - ball.getPosition().x))+(Math.abs(y - ball.getPosition().y)*Math.abs(y - ball.getPosition().y)));
            if(range <= ball_radius + brick_size){
                double dx = Math.abs(x - ball.getPosition().x);
                double dy = Math.abs(y - ball.getPosition().y);
                if(ball.getPosition().x < brick.position.x && dx > dy) {
                    float ang = ball.dir.angleDeg();
                    ball.dir.rotateDeg(-2 * (ang - 90));
                }
                else if(ball.getPosition().x > brick.position.x && dx > dy){
                    float ang = ball.dir.angleDeg();
                    ball.dir.rotateDeg(2 * (90 - ang));
                }
                else if(ball.getPosition().y < brick.position.y && dx < dy){
                    float ang = ball.dir.angleDeg();
                    ball.dir.rotateDeg(2 * -ang);
                }
                else if(ball.getPosition().y > brick.position.y && dx < dy){
                    float ang = ball.dir.angleDeg();
                    ball.dir.rotateDeg(-2 * ang);
                }
                else if(dx == dy){
                    System.out.println("Shot in angle!");//TODO shot in angle
                }
                else {
                    System.out.println("Error!");
                }
                score++;
                brick.hit();
                if(brick.getScore() <= 0) {
                    brick_count -= 1;
                    scene.remove(brick);
                    bricks.remove(brick);
                    return;
                }

                ////////////////

                Vector2 tmp = new Vector2(brick.position).sub(ball.getPosition()).nor().mul(-1);
                ball.move(tmp);

                ///////////////

                return;
            }
        }
    }
    public static void main(String[] args) {
        System.out.println("Hello world!");
        new Main().start();
    }
}