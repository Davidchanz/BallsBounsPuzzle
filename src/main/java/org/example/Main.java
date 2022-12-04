package org.example;

import Engine2D.Alphabet.S;
import Engine2D.Circle;
import Engine2D.Rectangle;
import Engine2D.Scene;
import Engine2D.Triangle;
import UnityMath.Vector2;

import javax.swing.*;
import java.awt.*;
import java.io.File;
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
    private JFileChooser fileChooser;
    private long timeStart;
    private long timeEnd;
    private long timeFly;

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
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/main/resources"));
        String mapPath = "src/main/resources/save.txt";
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // user selects a file
            File selectedFile = fileChooser.getSelectedFile();
            mapPath = selectedFile.getPath();
        }
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
        map = loadMap(mapPath);
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
            timeFly++;
            if(timeFly - timeStart > 1000 && acceleration <= 10.0f){
                timeStart = timeFly;
                acceleration += 0.5;
                for(int i = 0; i < ball_count; i++){
                    if(balls[i].fly) {
                        balls[i].dir.mul(acceleration);
                    }
                }
                System.out.println(acceleration);
            }
            int j = 0;
            int count = 0;
            while(fl) {
                timeFly++;
                if(timeFly - timeStart > 1000 && acceleration <= 10.0f){
                    timeStart = timeFly;
                    acceleration += 0.5;
                    for(int i = 0; i < ball_count; i++){
                        if(balls[i].fly) {
                            balls[i].dir.mul(acceleration);
                        }
                    }
                }
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
                if(count >= (ball_radius * 2) / acceleration){
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
                timeEnd = System.currentTimeMillis();
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
                timeStart = System.currentTimeMillis();
                timeFly = timeStart;
                acceleration = 1.0f;
            }
        }
    }
    private boolean isCollision(Ball origBall){
        Vector2 ballPosition = new Vector2(origBall.getPosition());
        ballPosition.add(origBall.dir);
        if(isErrorCollision(origBall)){
            origBall.fly = false;
            countFlyBalls--;
            return countFlyBalls <= 0;
        }
        else if(ballPosition.x < borderV.x) {
            float ang = origBall.dir.angleDeg();
            origBall.dir.rotateDeg(-2 * (ang - 90));
            return false;
        }
        else if(ballPosition.x > borderV.y){
            float ang = origBall.dir.angleDeg();
            origBall.dir.rotateDeg(2 * (90 - ang));
            return false;
        }
        else if(ballPosition.y < borderH.x){
            float ang = origBall.dir.angleDeg();
            origBall.dir.rotateDeg(2 * -ang);
            origBall.fly = false;
            countFlyBalls--;
            return countFlyBalls <= 0;
        }
        else if(ballPosition.y > borderH.y){
            float ang = origBall.dir.angleDeg();
            origBall.dir.rotateDeg(-2 * ang);
            return false;
        }/*else if(ball.getPosition().x <= cart.getPosition().x + cart_width &&
                ball.getPosition().x >= cart.getPosition().x - cart_width &&
                ball.getPosition().y >= cart.getPosition().y - cart_height &&
                ball.getPosition().y <= cart.getPosition().y + cart_height){
            float ang = dir.angleDeg();
            dir.rotateDeg(2 * -ang);
            return false;
        }*/
        BrickCollision(origBall);
        return false;
    }
    public boolean isErrorCollision(Ball origBall){
        Vector2 ballPosition = new Vector2(origBall.getPosition());
        ballPosition.add(origBall.dir);
        if(ballPosition.x < borderV.x) {
            if(ballPosition.x < -(WIDTH + ball_radius * 4))
                return true;
        }
        else if(ballPosition.x > borderV.y){
            if(ballPosition.x > WIDTH + ball_radius * 4)
                return true;
        }
        else if(ballPosition.y < borderH.x){
            if(ballPosition.y < -(HEIGHT + ball_radius * 4))
                return true;
        }
        else if(ballPosition.y > borderH.y){
            if(ballPosition.y > HEIGHT + ball_radius * 4)
                return true;
        }
        return false;
    }
    public ArrayList<Integer> Interpolate(float i0, float d0, float i1, float d1) {
        ArrayList<Integer> values = new ArrayList<>();
        if (i0 == i1) {
            values.add((int)d0);
            return values;
        } else {
            float a = (d1 - d0) / (i1 - i0);
            float d = d0;

            for(int i = (int)i0; i <= (int)i1; ++i) {
                values.add((int)d);
                d += a;
            }
            return values;
        }
    }
    public ArrayList<Vector2> Brezenheim(Vector2 v1, Vector2 v2) {
        ArrayList<Vector2> checkList = new ArrayList<>();
        float dx = v2.x - v1.x;
        float dy = v2.y - v1.y;
        Vector2 tmp;
        int scanlineY;
        ArrayList<Integer> ys;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (v1.x > v2.x) {
                tmp = v1;
                v1 = v2;
                v2 = tmp;
            }

            ys = this.Interpolate(v1.x, v1.y, v2.x, v2.y);

            for(scanlineY = (int)v1.x; (float)scanlineY <= v2.x; ++scanlineY) {
                checkList.add(new Vector2(scanlineY, ys.get(scanlineY - (int)v1.x)));
            }
        } else {
            if (v1.y > v2.y) {
                tmp = v1;
                v1 = v2;
                v2 = tmp;
            }

            ys = this.Interpolate(v1.y, v1.x, v2.y, v2.x);

            for(scanlineY = (int)v1.y; (float)scanlineY <= v2.y; ++scanlineY) {
                checkList.add(new Vector2(ys.get(scanlineY - (int)v1.y), scanlineY));
            }
        }
        return checkList;
    }
    public boolean sideCollision(ArrayList<Vector2> checkList, Ball origBall) {
        Vector2 ballPosition = new Vector2(origBall.getPosition());
        ballPosition.add(origBall.dir);
        for (var side : checkList) {
            float x = side.x;
            float y = side.y;
            float range = (float) Math.sqrt((Math.abs(x - ballPosition.x) * Math.abs(x - ballPosition.x)) + (Math.abs(y - ballPosition.y) * Math.abs(y - ballPosition.y)));
            if (range <= ball_radius) {
                return true;
            }
        }
        return false;
    }
    public void BrickCollision(Ball ball){
        var bricks_old = bricks.toArray(new Brick[0]);
        int brick_count_old = brick_count;
        boolean collision = false;
        for(int i = 0; i < brick_count_old; i++){
            Brick brick = bricks_old[i];
            switch (brick.getType()){
                case 1 -> {
                    Rectangle rectangle = (Rectangle) brick.body.get(0);
                    if(sideCollision(Brezenheim(new Vector2(rectangle.Bot.P0).add(brick.position), new Vector2(rectangle.Bot.P1).add(brick.position)), ball)){//right
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(2 * (90 - ang));
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(rectangle.Bot.P1).add(brick.position), new Vector2(rectangle.Bot.P2).add(brick.position)), ball)) {//top
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(-2 * ang);
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(rectangle.Top.P0).add(brick.position), new Vector2(rectangle.Top.P1).add(brick.position)), ball)) {//bot
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(2 * -ang);
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(rectangle.Top.P1).add(brick.position), new Vector2(rectangle.Top.P2).add(brick.position)), ball)) {//left
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(-2 * (ang - 90));
                        collision = true;
                    }
                }
                case 2 -> {
                    Triangle triangle = (Triangle) brick.body.get(0);
                    if(sideCollision(Brezenheim(new Vector2(triangle.P0).add(brick.position), new Vector2(triangle.P1).add(brick.position)), ball)){//bot
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(2 * -ang);
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(triangle.P1).add(brick.position), new Vector2(triangle.P2).add(brick.position)), ball)) {//right
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(2 * (90 - ang));
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(triangle.P0).add(brick.position), new Vector2(triangle.P2).add(brick.position)), ball)) {//diag
                        Vector2 perp = new Vector2(1,1).rotate90(1);
                        float ang = ball.dir.angleDeg(perp);
                        ball.dir.rotateDeg(2 * (90 - ang));
                        collision = true;
                    }
                }
                case 3 -> {
                    Triangle triangle = (Triangle) brick.body.get(0);
                    if(sideCollision(Brezenheim(new Vector2(triangle.P0).add(brick.position), new Vector2(triangle.P1).add(brick.position)), ball)){//right
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(2 * (90 - ang));
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(triangle.P1).add(brick.position), new Vector2(triangle.P2).add(brick.position)), ball)) {//top
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(-2 * ang);
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(triangle.P0).add(brick.position), new Vector2(triangle.P2).add(brick.position)), ball)) {//diag
                        Vector2 perp = new Vector2(-1,1).rotate90(1);
                        float ang = ball.dir.angleDeg(perp);
                        ball.dir.rotateDeg(2 * (90 - ang));
                        collision = true;
                    }
                }
                case 4 -> {
                    Triangle triangle = (Triangle) brick.body.get(0);
                    if(sideCollision(Brezenheim(new Vector2(triangle.P0).add(brick.position), new Vector2(triangle.P1).add(brick.position)), ball)){//top
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(-2 * ang);
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(triangle.P1).add(brick.position), new Vector2(triangle.P2).add(brick.position)), ball)) {//left
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(-2 * (ang - 90));
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(triangle.P0).add(brick.position), new Vector2(triangle.P2).add(brick.position)), ball)) {//diag
                        Vector2 perp = new Vector2(1,1).rotate90(-1);
                        float ang = ball.dir.angleDeg(perp);
                        ball.dir.rotateDeg(2 * (90 - ang));
                        collision = true;
                    }
                }
                case 5 -> {
                    Triangle triangle = (Triangle) brick.body.get(0);
                    if(sideCollision(Brezenheim(new Vector2(triangle.P0).add(brick.position), new Vector2(triangle.P1).add(brick.position)), ball)){//left
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(-2 * (ang - 90));
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(triangle.P1).add(brick.position), new Vector2(triangle.P2).add(brick.position)), ball)) {//bot
                        float ang = ball.dir.angleDeg();
                        ball.dir.rotateDeg(-2 * ang);
                        collision = true;
                    }else if (sideCollision(Brezenheim(new Vector2(triangle.P0).add(brick.position), new Vector2(triangle.P2).add(brick.position)), ball)) {//diag
                        Vector2 perp = new Vector2(-1,1).rotate90(-1);
                        float ang = ball.dir.angleDeg(perp);
                        ball.dir.rotateDeg(2 * (90 - ang));
                        collision = true;
                    }
                }
                default -> {
                    System.err.println("Unknown brick type!");
                }
            }
            if(collision) {
                score++;
                brick.hit();
                if (brick.getScore() <= 0) {
                    brick_count -= 1;
                    scene.remove(brick);
                    bricks.remove(brick);
                }
                Vector2 tmp = new Vector2(brick.position).sub(ball.getPosition()).nor().mul(-1);
                ball.move(tmp);
                return;
            }
        }
    }
    public static void main(String[] args) {
        System.out.println("Hello world!");
        new Main().start();
    }
}