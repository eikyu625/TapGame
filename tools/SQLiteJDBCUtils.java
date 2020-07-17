package com.mapleworld.game.tapgame.tools;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

public class SQLiteJDBCUtils {
    Connection c;
    Statement stmt;
    static PlayerData cachePlayerData;

    public class Result {

        private int id;
        private String word;
        private String wordtype;
        private String definition;
        public void setId(int id){
            this.id = id;
        }
        public int getId(){
            return this.id;
        }
        public void setWord(String word){
            this.word = word;
        }
        public String getWord(){
            return this.word;
        }
        public void setWordType(String wordType){
            this.wordtype = wordType;
        }
        public String getWordType(){
            return this.wordtype;
        }
        public void setDefinition(String definition){
            this.definition = definition;
        }
        public String getDefinition(){
            return this.definition;
        }

    }

    public class PlayerData {

        private int id;
        private String playerName;
        private int playTime;
        private double avgTapSpeed;
        private int totalTapCount;
        private long totalScore;
        private int totalClearWord;
        private int difficult;
        private int capitalization;
        public void setId(int id){
            this.id = id;
        }
        public int getId(){
            return this.id;
        }
        public void setPlayerName(String name){
            this.playerName = name;
        }
        public String getPlayerName(){
            return this.playerName;
        }
        public void setPlayTime(int playTime){
            this.playTime = playTime;
        }
        public int getPlayTime(){
            return this.playTime;
        }
        public void setAvgTapSpeed(double avgTapSpeed){
            this.avgTapSpeed = avgTapSpeed;
        }
        public double getAvgTapSpeed(){
            return this.avgTapSpeed;
        }
        public void setTotalTapCount(int totalTapCount){
            this.totalTapCount = totalTapCount;
        }
        public int getTotalTapCount(){
            return this.totalTapCount;
        }

        public void setTotalScore(long totalScore) {
            this.totalScore = totalScore;
        }

        public long getTotalScore() {
            return totalScore;
        }

        public int getTotalClearWord() {
            return totalClearWord;
        }

        public void setTotalClearWord(int totalClearWord) {
            this.totalClearWord = totalClearWord;
        }

        public int getDifficult() {
            return this.difficult;
        }

        public void setDifficult(int difficult) {
            this.difficult = difficult;
        }

        public int getCapitalization() {
            return capitalization;
        }

        public void setCapitalization(int capitalization) {
            this.capitalization = capitalization;
        }
    }

    /**
     * 连接到一个现有的数据库。如果数据库不存在， 那么它就会被创建，最后将返回一个数据库对象。
     */

    @Before
    public void before() {
        try {
            Class.forName("org.sqlite.JDBC");//检测是否有sqllite JDBC库（类）
            c = DriverManager.getConnection("jdbc:sqlite:EnglishDictionary.db");//D:\Java Project\com.mapleworld.game.tapgame\
            System.out.println("Opened database successfully");
            stmt = c.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() {
        try {
            stmt.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public List<Result> selectOne(int i) throws SQLException {
        before();
        ResultSet rs = stmt.executeQuery("SELECT * FROM words WHERE id = " + i + ";");
        List<Result> resultList = new ArrayList<Result>();
        rs.next();
        //while (rs.next()) {
        Result result = new Result();
        result.setId(i);
        result.setWord(rs.getString("word"));
        result.setWordType(rs.getString("wordtype"));
        result.setDefinition(rs.getString("definition"));
        resultList.add(result);
        //}
        rs.close();
        after();
        return resultList;
    }

    @Test
    public List<Result> selectRand() throws SQLException {
        before();
        int i = (int)(Math.random() * 176023) + 1;
        ResultSet rs = stmt.executeQuery("SELECT * FROM words WHERE id = " + i + ";");
        List<Result> resultList = new ArrayList<Result>();//结果list，以Result类为类型
        rs.next();
        Result result = new Result();//结果构造类
        //result.setId(i);
        result.setWord(rs.getString("word"));
        result.setWordType(rs.getString("wordtype"));
        result.setDefinition(rs.getString("definition"));
        resultList.add(result);
        rs.close();
        after();
        return resultList;
    }

    @Test
    public List<PlayerData> selectPlayerInfo(int id) throws SQLException {
        before();
        ResultSet rs = stmt.executeQuery("SELECT * FROM playerData WHERE id = " + id + ";");
        List<PlayerData> playerDataList = new ArrayList<PlayerData>();
        while (rs.next()) {
            PlayerData data = new PlayerData();
            //result.setId(i);
            data.setId(id);
            data.setPlayerName(rs.getString("playerName"));
            data.setPlayTime(rs.getInt("playTime"));
            data.setAvgTapSpeed(rs.getDouble("avgTapSpeed"));
            data.setTotalTapCount(rs.getInt("totalTapCount"));
            data.setTotalScore(rs.getInt("totalScore"));
            data.setTotalClearWord(rs.getInt("totalClearWord"));
            data.setDifficult(rs.getInt("difficult"));
            data.setCapitalization(rs.getInt("capitalization"));
            playerDataList.add(data);
        }
        rs.close();
        after();
        return playerDataList;
    }

    public PlayerData selectPlayerInfo(int id,boolean usecache){
        if(usecache){
            if(cachePlayerData != null){
                return cachePlayerData;
            }else{
                try{
                    cachePlayerData = selectPlayerInfo(id).get(0);
                }catch (SQLException e){
                    System.err.println("缓存角色数据错误。"+e);
                }
                return cachePlayerData;
            }
        }else{
            try{
                return selectPlayerInfo(id).get(0);
            }catch (SQLException e){
                System.err.println("读取角色数据错误。"+e);
                return null;
            }
        }
    }

    public boolean savePlayerInfo(int id,String name,int playtime,double avgTapSpeed,int totalTapCount,int totalScore,int totalClearWord) throws SQLException {

        if(selectPlayerInfo(id).size() <=0){
            before();
            stmt.executeQuery("INSERT INTO playerData (id,playerName,playTime,avgTapSpeed,totalTapCount,totalScore,totalClearWord) VALUES ("
                    + id + ",'" + name + "'," + playtime + "," + avgTapSpeed + "," + totalTapCount + "," + totalScore +"," + totalClearWord +");");
            after();
            return true;
        }else{
            before();
            stmt.executeUpdate("UPDATE playerData SET playTime = playTime + " + playtime + ",avgTapSpeed = (avgTapSpeed + " + avgTapSpeed + ")/2,totalTapCount = totalTapCount + " + totalTapCount + ",totalScore = totalScore +" + totalScore +",totalClearWord = totalClearWord + " + totalClearWord + " WHERE id = " + id + ";");
            after();
            return true;
        }

    }

    public boolean savePlayerSetting(int id,String name,int diff,int capitalization) throws SQLException {


            before();
            stmt.executeUpdate("UPDATE playerData SET playerName = '" + name + "',difficult =  " + diff + ",capitalization = " + capitalization + " WHERE id = " + id + ";");
            after();
            return true;


    }
}
