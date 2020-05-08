package sample;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    static int speed = 5;
    static int foodcolor = 0;
    static int width = 20;
    static int height = 20;
    static int foodX = 0;
    static int foodY = 0;
    static int cornersize = 25;
    static int bonus = 3;
    static int countBonus = 5;
    static List<Corner> snake = new ArrayList<>();
    static Dir direction = Dir.left;
    static boolean gameOver = false;
    static int count = 0;
    static Random random = new Random();
    public enum Dir{
        left,right,up,down;
    }

    public static class Corner{
        int x,y;
        public Corner(int x,int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        newFood();
        VBox root = new VBox();
        Canvas c = new Canvas(width*cornersize,height*cornersize);
        GraphicsContext gc = c.getGraphicsContext2D();
        root.getChildren().add(c);

        new AnimationTimer(){
            long lastTick = 0;
            public void handle(long now) {
                if (lastTick == 0){
                    lastTick = now;
                    tick(gc);
                    return;
                }
                if(now - lastTick > 1_000_000_000/speed){
                    lastTick = now;
                    tick(gc);
                }

            }
        }.start();


        Scene scene = new Scene(root, width*cornersize, height*cornersize);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();
        scene.addEventFilter(KeyEvent.KEY_PRESSED,key->{
            if(key.getCode() == KeyCode.W){
                direction = Dir.up;
            }
            if (key.getCode() == KeyCode.A) {
                direction = Dir.left;
            }
            if (key.getCode() == KeyCode.X) {
                direction = Dir.down;
            }
            if (key.getCode() == KeyCode.D) {
                direction = Dir.right;
            }
        });

        snake.add(new Corner(width/2,height/2));
        snake.add(new Corner(width/2,height/2));
        snake.add(new Corner(width/2,height/2));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void newFood(){
        start :while (true){
            foodX = random.nextInt(width);
            foodY = random.nextInt(height);

            for(Corner c :snake){
                if(c.x ==foodX && c.y == foodY){
                    continue start;
                }
            }
            foodcolor = random.nextInt(5);
            break;
        }
    }

    public static void tick(GraphicsContext gc){
        if(gameOver){
            gc.setFill(Color.RED);
            gc.setFont(new Font("",50));
            gc.fillText("GAME OVER",100,250);
            return;
        }
        for(int i = snake.size()-1;i>=1;i--){
            snake.get(i).x = snake.get(i-1).x;
            snake.get(i).y = snake.get(i-1).y;
        }

        switch (direction){
            case up:{
                snake.get(0).y--;
                if(snake.get(0).y<0){
                    gameOver = true;
                }
                break;
            }
            case down:{
                snake.get(0).y++;
                if(snake.get(0).y>height){
                    gameOver = true;
                }
                break;
            }
            case left:{
                snake.get(0).x--;
                if(snake.get(0).x<0){
                    gameOver = true;
                }
                break;
            }
            case right:{
                snake.get(0).x++;
                if(snake.get(0).x>width){
                    gameOver = true;
                }
                break;
            }
        }
        //eat food
        if(foodX == snake.get(0).x && foodY == snake.get(0).y){
            if(count == countBonus){
                snake.add(new Corner(-1,-1));
                snake.add(new Corner(-1,-1));
                count+=bonus;
                countBonus+=5;
            }
            else
            {
                snake.add(new Corner(-1,-1));
                count++;
            }
            newFood();
        }

        //self destroy
        for(int i = 1;i<snake.size();i++)
        {
            if(snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y){
                gameOver = true;
            }
        }

        //background
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,width*cornersize,height*cornersize);

        gc.setFill(Color.YELLOW);
        gc.setFont(new Font("",30));
        gc.fillText("Score "+(count),10,30);

        // random foodcolor
        Color cc = Color.WHITE;

        switch (foodcolor) {
            case 0:
                cc = Color.PURPLE;
                break;
            case 1:
                cc = Color.LIGHTBLUE;
                break;
            case 2:
                cc = Color.YELLOW;
                break;
            case 3:
                cc = Color.PINK;
                break;
            case 4:
                cc = Color.ORANGE;
                break;
        }
        if(count == countBonus){
            gc.setFill(cc);
            gc.fillRect(foodX*cornersize,foodY*cornersize,cornersize,cornersize);
            gc.setFill(Color.GOLD);
            gc.fillOval(foodX*cornersize,foodY*cornersize,cornersize,cornersize);
        }
        else{
            gc.setFill(cc);
            gc.fillOval(foodX*cornersize,foodY*cornersize,cornersize,cornersize);
        }

        for(Corner c :snake){
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(c.x*cornersize,c.y*cornersize,cornersize-1,cornersize-1);
            gc.setFill(Color.RED);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
