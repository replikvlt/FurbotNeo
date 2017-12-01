package com.kuborros.FurBotNeo.utils.config;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.kuborros.FurBotNeo.BotMain;
import com.kuborros.FurBotNeo.utils.msg.ChannelFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;

public class Database {

    static final Logger LOG = LoggerFactory.getLogger(Database.class);

    static final String DRIVER = "org.sqlite.JDBC";
    static final String DB = "jdbc:sqlite:database.db";

    private Connection conn;
    private Statement stat;
    private ResultSet rs;


    public Database() {
        try {
            Class.forName(Database.DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.error("No JDBC driver!");
            e.printStackTrace();
        }

        try {
            conn = DriverManager.getConnection(DB);
            stat = conn.createStatement();
        } catch (SQLException e) {
            LOG.error("Database connection error!");
            e.printStackTrace();
        }
    }
    
    public void close(){
        try{
        conn.close();
        stat.close();
        }catch (SQLException e){}
    }
    
    public void createTables() {

      try {
          stat = conn.createStatement();

          String bans = "CREATE TABLE IF NOT EXISTS BotBans " +

                  "(id INTEGER PRIMARY KEY AUTOINCREMENT," +

                  " user_id TEXT NOT NULL, " +

                  " guild_id TEXT NOT NULL, " +
                  
                  " reason TEXT NOT NULL, " +

                  " time_end TEXT NOT NULL, " +                  

                  " time_start TEXT DEFAULT CURRENT_TIMESTAMP) ";

          stat.executeUpdate(bans);

          String guild = "CREATE TABLE IF NOT EXISTS Guilds " +

                  "(guild_id TEXT UNIQUE PRIMARY KEY NOT NULL, " +

                  " music_id TEXT NOT NULL, " +

                  " name TEXT NOT NULL, " +
                  
                  " members INTEGER)";

          stat.executeUpdate(guild);
          
          String game = "CREATE TABLE IF NOT EXISTS Games " +

                  "(id INTEGER PRIMARY KEY AUTOINCREMENT," +

                  " guild_id TEXT NOT NULL, " +

                  " game_id TEXT NOT NULL, " +

                  " priority INTEGER) ";

          stat.executeUpdate(game);
          
          String count = "CREATE TABLE IF NOT EXISTS CommandStats " +

                  "(user_id TEXT UNIQUE PRIMARY KEY NOT NULL) ";

          stat.executeUpdate(count);          
          
      } catch (SQLException e){
          LOG.error(e.getMessage());
        }
    }
    
    public void setGuilds(JDA jda) {
        
        List<Guild> guilds = jda.getGuilds();    
        if(guilds.isEmpty()) return;        
        try {
            stat = conn.createStatement();
            
            stat.executeUpdate("DELETE FROM Guilds");
            
            for (Guild guild : guilds){
                String sql = "INSERT OR IGNORE INTO Guilds(guild_id,music_id,name,members) VALUES(?,?,?,?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, guild.getId());
                pstmt.setString(2, new ChannelFinder(guild).FindBotChat().getId());
                pstmt.setString(3, guild.getName());
                pstmt.setInt(4, guild.getMembers().size());
                pstmt.executeUpdate();
                
            } 
        } catch (SQLException e){
            LOG.error(e.getLocalizedMessage());
        }
    }
     
    public void updateGuildMembers(GuildMemberJoinEvent event) {
        updateGuildMembers(event.getGuild());        
    }
    
    public void updateGuildMembers(GuildMemberLeaveEvent event) {
        updateGuildMembers(event.getGuild());
    }

    private void updateGuildMembers(Guild guild) {
    int members = guild.getMembers().size();
    try {    
            stat = conn.createStatement();
            stat.executeUpdate("UPDATE Guilds SET members=" + members + " WHERE guild_id=" + guild.getId());
        } catch (SQLException e){
            LOG.error(e.getLocalizedMessage());
        }
    }
    
    public void setCommandStats(JDA jda) {
        List<User> users = jda.getUsers();
        try {    
            stat = conn.createStatement();
            for (User user : users) {
                stat.addBatch("INSERT OR IGNORE INTO CommandStats (user_id) VALUES (" + user.getId() + ")");
            }
            stat.executeBatch();
        } catch (SQLException e) {}                
    }
    
    public void registerCommand(String command) {
        try {    
            stat = conn.createStatement();
            stat.executeUpdate("ALTER TABLE CommandStats ADD COLUMN " + command + " INTEGER DEFAULT 0");            
        } catch (SQLException e) {}
    }
    
    public void updateCommandStats(String memberID, String command) {
       try {    
            stat = conn.createStatement();
            stat.executeUpdate("UPDATE CommandStats SET " + command + "=" + command + " + 1 WHERE user_id=" + memberID);            
        } catch (SQLException e) {}
    }        
    
    public Map<String, String> getCommandStats(String memberID) throws SQLException{
  
            Map<String, String> map = new HashMap<>();
            int counter;
            stat = conn.createStatement();
            rs = stat.executeQuery("SELECT * FROM CommandStats WHERE user_id=" + memberID);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();  
            List<String> names = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++ ) {
                names.add(rsmd.getColumnName(i));
            }
            names.remove(0);
            while (rs.next()) {
                for (String name : names) {
                    counter = rs.getInt(name);
                    map.put(name, Integer.toString(counter));
                }
            }
            return map;
        
    }
}