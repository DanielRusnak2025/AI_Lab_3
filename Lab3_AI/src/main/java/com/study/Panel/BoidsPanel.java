package com.study.Panel;

import com.study.Boid;
import com.study.Vector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoidsPanel extends JPanel {

    public List<Boid> boids = new ArrayList<>();
    private final Random rnd = new Random();
    public final int width = 900;
    public final int height = 1000;
    public int step = 0;

    private static final float MAX_SPEED = 3f;
    private static final int SEPARATION_RADIUS = 20;
    private static final float ALIGNMENT_COHESION_RADIUS = 40f;


    private static final float W_COHESION   = 0.01f;
    private static final float W_ALIGNMENT  = 0.05f;
    private static final float W_SEPARATION = 0.1f;

    public BoidsPanel(){

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);


        for (int i = 0; i<100; i++){
            Vector pos = new Vector(rnd.nextFloat() * width, rnd.nextFloat() * height);
            Vector vel = new Vector(rnd.nextFloat() * 4 - 2, rnd.nextFloat() * 4 -2 );
            boids.add(new Boid(pos, vel));
        }
    }


    public void update(){
        step ++;
        for(Boid b : boids){

            Vector sep = separation(b);
            Vector ali = alignment(b);
            Vector coh = cohesion(b);


            sep.multiplyVector(W_SEPARATION);
            ali.multiplyVector(W_ALIGNMENT);
            coh.multiplyVector(W_COHESION);

            /*sep.multiplyVector(rnd.nextFloat() * 1 * W_SEPARATION);
            ali.multiplyVector(rnd.nextFloat() * 1 * W_ALIGNMENT);
            coh.multiplyVector(rnd.nextFloat() * 1 * W_COHESION);*/

            b.velocity.addVector(sep);
            b.velocity.addVector(ali);
            b.velocity.addVector(coh);
            b.velocity.limit(MAX_SPEED);
            b.position.addVector(b.velocity);

            b.position.x = ((b.position.x % width) + width) % width;
            b.position.y = ((b.position.y % height) + height) % height;
        }
    }


    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g); // стираем прошлый кадр

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(255,20,20));
        int r = 6;
        for(Boid b : boids){
            double angle = Math.atan2(b.velocity.y, b.velocity.x);

            Graphics2D g3 = (Graphics2D) g2.create();
            g3.translate(b.position.x, b.position.y);
            g3.rotate(angle);

            int[] xs = { 8, -4, -4 };
            int[] ys = { 0, -4,  4 };
            g3.fillPolygon(xs, ys, 3);

            g3.dispose();  // обязательно освободить копию
        }
        g2.setColor(Color.BLACK);
        g2.drawString("Шаг: " + step, 10, 20);

    }






    public static void main(String[] args) {
        // Весь UI-код Swing принято запускать в специальном потоке событий
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Boids — стая птиц");
            BoidsPanel panel = new BoidsPanel();

            frame.add(panel);
            frame.pack(); // окно подгоняется под preferredSize панели
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null); // по центру экрана
            frame.setVisible(true);

            // "Сердцебиение": каждые 16 мс (~60 кадров/сек) делаем шаг и перерисовываем
            Timer timer = new Timer(16, e -> {
                panel.update();
                panel.repaint();
            });
            timer.start();
        });
    }




    // отталкивание от соседей (накапривает вектор собирая положения всех слишком близких соседей)
    private Vector separation(Boid b){
        Vector steer = new Vector(0,0);
        for(Boid near : boids){
            if(near == b){
                continue;
            }
            if(b.position.lengthVector(near.position) < SEPARATION_RADIUS){
                steer.addVector(b.position.subVector(near.position));
            }
        }
        return steer;
    }

    //логика Alignmент
    // подстраиваем скорость под скорости соседей
    private Vector alignment(Boid b){
        Vector sum = new Vector(0,0);
        int count = 0;
        for(Boid bb :boids){
            if(bb == b){
                continue;
            }
            if(b.position.lengthVector(bb.position) < ALIGNMENT_COHESION_RADIUS){
                sum.addVector(bb.velocity);
                count++;
            }
        }
        if(count > 0 ){
            sum.multiplyVector((1f/count));
        }

        return sum;
    }


    // логика кохесион
    private Vector cohesion(Boid b){
        Vector positionSum = new Vector(0,0);
        int count = 0;
        for(Boid bb : boids){
            if(bb == b) continue;
            if(b.position.lengthVector(bb.position) < ALIGNMENT_COHESION_RADIUS){
                positionSum.addVector(bb.position);
                count++;
            }
        }
        if(count > 0){
            positionSum.multiplyVector(1f/count);
            return positionSum.subVector(b.position);
        }

        return new Vector(0,0);
    }
}
