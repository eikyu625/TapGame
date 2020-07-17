/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mapleworld.game.tapgame.main;

import com.mapleworld.game.tapgame.gui.GameMainGui;
import com.mapleworld.game.tapgame.gui.GameOverGUI;
import com.mapleworld.game.tapgame.gui.NewGameGui;
import com.mapleworld.game.tapgame.timer.DropTimer;
import com.mapleworld.game.tapgame.timer.MainTimer;
import com.mapleworld.game.tapgame.tools.SQLiteJDBCUtils;
import com.mapleworld.game.tapgame.tools.translate.TransApi;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 *
 * @author Administrator
 */
public class TapGame {
    DropTimer dropTimer;
    //static GameMainGui gameMainGui = gameMainGui.getJPMain();
    static GameMainGui gameMainGui;
    static TransApi api = new TransApi("", "");//you app id and key
    private static int score = 0;
    private static  int life = 3;
    private static int finshCount = 0;
    static boolean isPaused = false;
    static double avgSpeed = 0.0;
    private static int mod1 = 0;
    private static int mod2 = 0;
    private static int min = 0;
    private static int sec = 0;
    private static int tapCount = 0;
    private static String CurrentWordChinese = "...";
    private static SQLiteJDBCUtils.PlayerData playerData;
    public static void main(String[] args) {
        NewGameGui.main(args);
    }

    public static String getCurrentWordChinese() {
        return CurrentWordChinese;
    }

    public static SQLiteJDBCUtils.PlayerData getPlayerData() {
        if(playerData != null){
            return playerData;
        }
        System.err.println("错误！playerData为null。");
        return null;

    }

    public static void setPlayerData(SQLiteJDBCUtils.PlayerData playerDataN){
        playerData = playerDataN;
    }


    public static void newGame(int mod1, int mod2, int diff){
        //mod1 = 0 单人模式
        //  mod2 = 0 无尽模式
        //mod1 = 1 双人模式
        //  mod2 = 0 抢单词 mod2 = 1 对抗
        //mod1 = 2 多人模式（大概不会做）
        gameMainGui.main();
        gameMainGui = GameMainGui.getGameMainGui();
        life = 3;
        score = 0;
        finshCount = 0;
        min = 0;
        sec = 0;
        avgSpeed = 0;
        tapCount = 0;
        pause(false);
        CurrentWordChinese = "...";
        gameMainGui.UI_setMod(mod1,mod2,diff);
        gameMainGui.UI_setLife(life);
        gameMainGui.UI_setPlayerName(getPlayerData().getPlayerName());
        gameMainGui.UI_setCurrentWord("...");
        gameMainGui.UI_setCurrentWordChinese("...");
        gameMainGui.UI_setFinshCount(finshCount);

        MainTimer mainTimer = new MainTimer();
        //mainTimer.setMainGui(gameMainGui);
        mainTimer.setMod(mod1,mod2,diff);
        mainTimer.start();
    }

    public static String getWordRandom(){
        String word;
        try {
            word = new SQLiteJDBCUtils().selectRand().get(0).getWord();
            return word;
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
        }

    }

    public static void setTapCount(int count){
        tapCount = count;
    }

    public static void dropOneWord(JPanel jpWord,int speed){
        if(jpWord == null){
            jpWord = GameMainGui.createNewWord(TapGame.getWordRandom());
        }
        createDropTimer(jpWord,GameMainGui.getJPMain(),speed);
    }
    public static void createDropTimer(JPanel jpWord,JPanel jpMain,int speed){
        if(speed > 980){
            speed =980;
        }
        speed = (int) Math.floor(Math.random() * 10 + speed - 5);
        System.out.println("创建掉落线程，速度："+speed);
        DropTimer dropTimer = new DropTimer();
        //dropTimer.setLabel(lb);
        dropTimer.setWordPanel(jpWord);
        dropTimer.setMainPanel(jpMain);
        dropTimer.setSpeed(speed);
        dropTimer.start();
        //dropTimer.setNo(0);
    }

    public static void addFinshCount(){
        finshCount++;
        gameMainGui.UI_setFinshCount(finshCount);
    }

    public static void setAvgTapSpeed(double speed){
        avgSpeed = speed;
        gameMainGui.UI_setAvgSpeed(speed);
    }
    public static void setCurrentWord(String word){
        gameMainGui.UI_setCurrentWord(word);
        new Thread(new Runnable() {
            @Override
            public void run() {
                CurrentWordChinese =  api.getTransResult(word,"en","zh");
                //gameMainGui.UI_setCurrentWordChinese();
            }
        });

        ExecutorService threadPool = Executors.newCachedThreadPool();

        //System.out.println("====提交异步任务");
        Future<String> future = threadPool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String TC =  api.getTransResult(word,"en","zh");
                return TC;
            }

        });
        try {
            CurrentWordChinese = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public static void setCurrentWordChineseUI(String word){
        if(!word.equals(gameMainGui.UI_getCurrentWordChinese())){
            gameMainGui.UI_setCurrentWordChinese(word);
        }

    }


    public static void addScore(int scoreC){
        score+= scoreC;
        if(gameMainGui == null){
            System.err.println("gui为空。");
        }else{
            gameMainGui.UI_setScore(score);
            //gameMainGui.UI_setScoreTip(score);
        }


    }
    public static int getScore(){
        return score;
    }
    public static void costLife(){
        life--;
        if(gameMainGui == null){
            System.err.println("gui为空。");
        }else{
            gameMainGui.UI_setLife(life);
        }
        if(life <= 0){

        }
    }

    public static int getLife(){
        return life;
    }
    public static void setLife(int lifeC){
        life = lifeC;

    }


    public static void gameOver(){
        GameOverGUI oGui = new GameOverGUI();
        oGui.UI_GameOver_setInfo(mod1,mod2,playerData.getPlayerName(),finshCount,tapCount,min*60+sec,avgSpeed);
        oGui.setVisible(true);
        DropTimer.over();
        MainTimer.over();
        //new SQLiteJDBCUtils().savePlayerInfo(playerData.getId(),playerData.getPlayerName(),playerData.getPlayTime() + min * 60 + sec,(playerData.getAvgTapSpeed() + avgSpeed)/2)
        try {
            new SQLiteJDBCUtils().savePlayerInfo(playerData.getId(),playerData.getPlayerName(),min * 60 + sec,avgSpeed,tapCount,score,finshCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static int getCapitalization(){
        return playerData.getCapitalization();
    }

    public static void pause(boolean pause){
        isPaused = pause;
    }

    public static boolean isPause(){
        return isPaused;
    }

    public static void setUpTime(int minC,int secC){
        min = minC;
        sec = secC;
        gameMainGui.setUpTime(min,sec);
    }

}
