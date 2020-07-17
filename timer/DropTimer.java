/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mapleworld.game.tapgame.timer;

import com.mapleworld.game.tapgame.main.TapGame;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;


/**
 *
 * @author Administrator
 */
public class DropTimer extends  Thread{
    private int x;
    private int y;
    //private JLabel lb;
    private JPanel jpWord;
    private JPanel jpMain;
    private int speed;
    private boolean cleared = false;
    private static boolean over = false;
            

    @Override
    public void run(){
        over = false;
        y = 0;
        while(y < jpMain.getHeight()){
            if(over){
                cleared = true;
                break;
            }
            //x++;
            y++;
            jpWord.setBounds(jpWord.getX(), y, jpWord.getWidth(), jpWord.getHeight());
            /*if(lb.getForeground() == Color.RED){
                TapGame.addScore();
                jpMain.remove(lb);
                jpMain.repaint();
                cleared = true;
                break;
            }*/
            TapGame.setCurrentWordChineseUI(TapGame.getCurrentWordChinese());
            int letterCount = 0;
            int finshCount = 0;
            for(Component letterComponent:jpWord.getComponents()){
                letterCount++;
                JLabel letterLB = (JLabel) letterComponent;
                if(letterLB.getForeground() == Color.RED){
                    finshCount++;
                }

            }
            if(finshCount >= letterCount){
                cleared = true;
                double h = jpMain.getHeight();
                double t = (h - y)/h;
                int sc = (int)Math.ceil(t * 10);
                TapGame.addScore(sc);
                TapGame.addFinshCount();
                jpMain.remove(jpWord);
                jpMain.repaint();
                break;
            }

            try {
                DropTimer.sleep(1000-speed);
            } catch (InterruptedException ex) {
                Logger.getLogger(DropTimer.class.getName()).log(Level.SEVERE, null, ex);
            }
            while (TapGame.isPause()){
                try {
                    DropTimer.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DropTimer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if(!cleared){
            jpMain.remove(jpWord);
            jpMain.repaint();
            if(!over)
            TapGame.costLife();
        }

    }
    
    public void setLabel(JLabel lb){
        //this.lb = lb;
    }
    public void setMainPanel(JPanel jpMain){
        this.jpMain = jpMain;
    }
    public void setWordPanel(JPanel jpMain){
        this.jpWord = jpMain;
    }
    public void setSpeed(int speed){
        if(speed >= 1000){
            speed = 1000;
        }
        this.speed = speed;
    }

    public static void over(){
        over = true;
    }
}

