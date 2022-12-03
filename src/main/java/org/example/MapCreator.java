package org.example;

import Engine2D.Alphabet.B;
import Engine2D.Alphabet.H;
import Engine2D.Alphabet.V;
import Engine2D.Circle;
import Engine2D.Rectangle;
import Engine2D.Scene;
import Engine2D.ShapeObject;
import UnityMath.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MapCreator extends JFrame {
    private Scene scene;
    private int WIDTH;
    private int HEIGHT;
    private Input input;
    private int ball_radius;
    private Ball ball;
    private boolean game;
    private int delay;
    private Brick[][] map;
    private int row;
    private int col;
    private int brick_size;
    private Vector2 start;
    private JTextField textField;
    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    private void start(){
        game();
    }
    private void game(){
        int mouseX = Input.getMouseX();
        int mouseY = Input.getMouseY()+23;
        System.out.println(mouseX+" / "+mouseY);
        if(Input.isClicked()){
            for(int i = 0; i < row; i++){
                for(int j = 0; j < col; j++) {
                    if(mouseX > map[i][j].body.get(0).position.x - brick_size &&
                       mouseX < map[i][j].body.get(0).position.x + brick_size &&
                       mouseY > map[i][j].body.get(0).position.y - brick_size &&
                       mouseY < map[i][j].body.get(0).position.y + brick_size){
                        map[i][j].body.get(0).setColor(Color.RED);
                        map[i][j].setScore(Integer.parseInt(textField.getText()));
                    }
                }
            }
            scene.repaint();
        }
    }
    private void ini(){
        delay = 1;
        game = true;
        WIDTH = 800;
        HEIGHT = 600;
        scene = new Scene(WIDTH, HEIGHT);
        scene = new Scene(WIDTH, HEIGHT);
        scene.setCenterVisible(true);
        scene.setCoordVisible(true);
        scene.setBorderVisible(true);
        int border_size = 1;
        scene.setBorder(border_size, Color.BLACK);
        input = new Input();
        ball_radius = 10;
        ball = new Ball("ball", 1, new Circle(ball_radius, new Vector2(0,-HEIGHT/2 + 50), Color.WHITE));
        scene.add(ball);
        row = 10;
        col = 21;
        brick_size = 15;
        start = new Vector2(-col*brick_size,0);
        map = new Brick[row][col];
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                map[i][j] = new Brick("brick", 1, new Rectangle(brick_size, brick_size, new Vector2(start.x + 2 * brick_size * j, start.y + 2 * brick_size * i), null), 0);
                scene.add(map[i][j]);
            }
        }



        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("A Menu");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

        menuItem = new JMenuItem("Save");
        menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Save");
                try {
                    File myObj = new File("src/main/resources/save.txt");
                    if (myObj.createNewFile()) {
                        System.out.println("File created: " + myObj.getName());
                    } else {
                        System.out.println("File already exists.");
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

                try {
                    FileWriter myWriter = new FileWriter("src/main/resources/save.txt");
                    StringBuilder res = new StringBuilder();
                    for(int i = row-1; i >= 0; i--){
                        for(int j = col-1; j >= 0; j--){
                            if(map[i][j].body.get(0).color != null){
                                res.append(String.valueOf(map[i][j].getScore()));
                            }else {
                                res.append(String.valueOf(0));
                            }
                            res.append(',');
                        }
                        if(res.lastIndexOf(",") != -1)
                            res.deleteCharAt(res.lastIndexOf(","));
                        res.append("\n");
                    }
                    res.deleteCharAt(res.length()-1);
                    myWriter.write(res.toString());

                    //myWriter.write("Files in Java might be tricky, but it is fun enough!");
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        });
        menu.add(menuItem);
    }

    MapCreator(){
        ini();
        this.setSize(new Dimension(WIDTH+10, HEIGHT+45));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.add(scene, BorderLayout.CENTER);
        this.addMouseMotionListener(input.getMouseAdapter());
        this.addMouseListener(input.getMouseAdapter());
        this.addKeyListener(input.getKeyAdapter());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                game();
            }
        });
        textField = new JTextField("0");
        this.add(textField, BorderLayout.SOUTH);
        JButton button = new JButton("save");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Save");
            }
        });
        this.setJMenuBar(menuBar);
        this.setVisible(true);
    }
    public static void main(String[] args) {
        new MapCreator().start();
    }
}
