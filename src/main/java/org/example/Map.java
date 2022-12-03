package Test2D;

import Engine2D.Rectangle;
import Engine2D.ShapeObject;
import UnityMath.Vector2;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**Class for game map*/
public class Map {
    public ArrayList<ShapeObject> objects = new ArrayList<>();
    public int WIDTH;
    public int HEIGHT;
    public char[][] mat_array;
    public int size;
    public static Vector2 start = new Vector2(0,0);

    Map(int w, int h, int size, Vector2 st){
        this.WIDTH = w;
        this.HEIGHT = h;
        this.mat_array = new char[this.WIDTH][this.HEIGHT];
        this.size = size;
        start = st;
    }

    /**Parse element of map*/
    private static void parseMap(int i, int j, char elem, Map map){
        switch (elem){
            case '0' -> {//space
                ShapeObject space = new ShapeObject("Space ["+i+"]["+j+"]", 0);
                //space.add(new Engine2D.Rectangle(map.size, new Vector2(start.x + 2*map.size * j, start.y + 2*map.size * i), null));
                map.objects.add(space);
            }
            case '1' -> {//border
                ShapeObject border = new ShapeObject("Border ["+i+"]["+j+"]", 1);
                border.add(new Engine2D.Rectangle(map.size, new Vector2(start.x + 2 * map.size * j, start.y + 2 * map.size * i), Color.BLACK));
                map.objects.add(border);
            }
            case 'p' -> {//player
                ShapeObject player = new ShapeObject("Player ["+i+"]["+j+"]", 2);
                player.add(new Engine2D.Triangle(map.size, new Vector2(start.x + 2 * map.size * j, start.y + 2 * map.size * (i+1)), Color.BLUE));//head m
                player.add(new Engine2D.Rectangle(map.size, new Vector2(start.x + 2 * map.size * j, start.y + 2 * map.size * i), Color.BLUE));//body m
                player.add(new Engine2D.Rectangle(map.size, new Vector2(start.x + 2 * map.size * (j+1) , start.y + 2 * map.size * i), Color.BLUE));//body r
                player.add(new Engine2D.Rectangle(map.size, new Vector2(start.x + 2 * map.size * (j-1) , start.y + 2 * map.size * i), Color.BLUE));//body l
                player.add(new Engine2D.Circle(map.size, new Vector2(start.x + 2 * map.size * (j+1) , start.y + 2 * map.size * (i-1)), Color.BLUE));//leg r
                player.add(new Engine2D.Circle(map.size, new Vector2(start.x + 2 * map.size * (j-1) , start.y + 2 * map.size * (i-1)), Color.BLUE));//leg l
                map.objects.add(player);
            }
            case 'e' -> {//enemy
                ShapeObject enemy = new ShapeObject("Enemy ["+i+"]["+j+"]", 3);
                enemy.add(new Rectangle(map.size, new Vector2(start.x + 2 * map.size * j, start.y + 2 * map.size * i), Color.RED));
                map.objects.add(enemy);
            }
        }
    }

    /**Load map from file  format:
     *
     * 0,0,0,0
     * 1,0,0,1
     *
     * */
    public static Map loadMap(String path, int size) throws IOException{
        try(FileReader fr = new FileReader(path);FileReader f = new FileReader(path)){
            Scanner sc = new Scanner(fr);
            int ii = 0;
            int jj = 0;
            int h = 0;
            int w = 0;
            while(sc.hasNext()){
                String line = sc.nextLine();
                ii++;
                Scanner scl = new Scanner(line);
                scl.useDelimiter(",");
                while (scl.hasNext()){
                    String c = scl.next();
                    jj++;
                }
                h = jj;
                jj = 0;
            }
            w = ii;
            ii = 0;
            Vector2 st = new Vector2(-(size*(w-1)), -(size*(h-1)));
            Map map = new Map(w, h, size, st);
            sc = new Scanner(f);
            for(int i = 0; i < map.mat_array.length; ++i){
                String line = sc.nextLine();
                Scanner scl = new Scanner(line);
                scl.useDelimiter(",");
                for(int j = 0; j < map.mat_array[i].length; ++j){
                    var t = scl.next();
                    map.mat_array[i][j] = t.charAt(0);
                    parseMap(i, j, t.charAt(0), map);
                }
            }
            return map;
        }
    }
}
