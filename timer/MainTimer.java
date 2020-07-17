package com.mapleworld.game.tapgame.timer;

import com.mapleworld.game.tapgame.gui.GameMainGui;
import com.mapleworld.game.tapgame.gui.NewGameGui;
import com.mapleworld.game.tapgame.main.TapGame;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainTimer extends  Thread{
    private double avgTapSpeed;
    private GameMainGui gui;
    private boolean fristRun = true;
    private int mod1 = 0,mod2 = 0,diff = 1;
    private static int keyCount = 0;
    private static boolean over = false;

    @Override
    public void run(){
        over = false;
        if(fristRun){
            keyCount = 0;
            int x = 5;
            while (x >= 0){
                x--;
                try {
                    MainTimer.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DropTimer.class.getName()).log(Level.SEVERE, null, ex);
                }
                TapGame.setUpTime(0,x);
            }
        }
        int sec = 0;
        int min = 0;
        int speedIncrease = 945 + (diff * 2);
        int lengthIncrease = 0;
        int frequencyIncreaseCount = 10;
        int frequencyIncrease = 4 + diff;
        int frequencyCount = 10;
        int FdropCount = 0;
        int dropCount = 0;
        if(mod1 == 0){
            while (TapGame.getLife() > 0){
                if(over){
                    break;
                }
                sec++;
                if(sec == 60){
                    min++;
                    sec = 0;
                }
                TapGame.setUpTime(min,sec);
                frequencyCount++;

                if(frequencyCount >= frequencyIncreaseCount - frequencyIncrease){
                    frequencyCount = 0;
                    TapGame.dropOneWord(null,speedIncrease);
                    speedIncrease += 2 + diff * 2;
                    FdropCount ++;
                    dropCount++;
                }
                if(FdropCount >= 6){
                    if(frequencyIncrease < 10){
                        frequencyIncrease++;
                    }
                    FdropCount = 0;
                }
                TapGame.setTapCount(keyCount);
                avgTapSpeed = 60/Math.floor(min * 60 + sec) * keyCount;//
                TapGame.setAvgTapSpeed(avgTapSpeed);
                try {
                    MainTimer.sleep(1000);
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



            if(!over){
                TapGame.gameOver();
            }

        }

    }

    public void setMod(int mod1,int mod2,int diff){
        this.mod1 = mod1;
        this.mod2 = mod2;
        this.diff = diff;
    }

    public static void typeKey(){
        keyCount++;
    }
    public static void over(){
        over = true;
    }
}
