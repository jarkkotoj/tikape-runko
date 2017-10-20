/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.dao;

import tikape.runko.dao.Dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.database.Database;
import tikape.runko.domain.*;

public class RaakaAineDao implements Dao<RaakaAine, Integer> {

    private Database database;

    public RaakaAineDao(Database database) {
        this.database = database;
    }

    @Override
    public RaakaAine findOne(Integer key) throws SQLException {
        return findAll().stream().filter(u -> u.getId().equals(key)).findFirst().get();
        /*
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM RaakaAine WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        RaakaAine o = new RaakaAine(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return o;
        */
    }

    @Override  
    public List<RaakaAine> findAll() throws SQLException {
        List<RaakaAine> aineet = new ArrayList<>();

        try (Connection conn = database.getConnection();
            ResultSet result = conn.prepareStatement("SELECT id, nimi FROM RaakaAine").executeQuery()) {

            while (result.next()) {
                aineet.add(new RaakaAine(result.getInt("id"), result.getString("nimi")));
            }
        }

        return aineet;
    }

    public List<Resepti> findResepti(Integer raakaAine_key) throws SQLException {
        Connection conn = database.getConnection();
        List<Resepti> reseptit = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT Resepti.id, Resepti.nimi FROM Resepti, ReseptiRaakaAine \n" 
                + "WHERE ReseptiRaakaAine.resepti_id = Resepti.id AND ReseptiRaakaAine.raaka_aine_id = ? ");
        stmt.setInt(1, raakaAine_key);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            reseptit.add(new Resepti(rs.getInt("id"), rs.getString("nimi")));
        }
        return reseptit;
    }
    
    public List<RaakaAine> findRaakaAineet(Integer resepti_key) throws SQLException {
        String query = "SELECT RaakaAine.id, RaakaAine.nimi FROM RaakaAine, ReseptiRaakaAine\n"
                + "              WHERE RaakaAine.id = ReseptiRaakaAine.raaka_aine_id "
                + "                  AND ReseptiRaakaAine.resepti_id = ?;";

        List<RaakaAine> aineet = new ArrayList<>();

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, resepti_key);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                aineet.add(new RaakaAine(result.getInt("id"), result.getString("nimi")));
            }
        }

        return aineet;
    }
    
    private RaakaAine findByNameRaakaAine(String name) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nimi FROM RaakaAine WHERE nimi = ?");
            stmt.setString(1, name);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }
            

            return new RaakaAine(result.getInt("id"), result.getString("nimi"));
        }
    }

    @Override
    public RaakaAine saveOrUpdate(RaakaAine object) throws SQLException {
        // simply support saving -- disallow saving if task with 
        // same name exists
        RaakaAine byName = findByNameRaakaAine(object.getNimi());

        if (byName != null) {
            return byName;
        }

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO RaakaAine (nimi) VALUES (?)");
            stmt.setString(1, object.getNimi());
            stmt.executeUpdate();
            
        }

        return findByNameRaakaAine(object.getNimi());

    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM RaakaAine WHERE id = ?");
        stmt.setInt(1,key);
        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
    }

}
