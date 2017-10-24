/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.database.Database;
import tikape.runko.domain.*;

public class ReseptiDao implements Dao<Resepti, Integer> {

    private Database database;

    public ReseptiDao(Database database) throws ClassNotFoundException {
        this.database = database;
    }

    @Override
    public Resepti findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Resepti WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");
        String selitys = rs.getString("selitys");

        Resepti o = new Resepti(id, nimi, selitys);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }

    @Override
    public List<Resepti> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Resepti ORDER BY nimi");

        ResultSet rs = stmt.executeQuery();
        List<Resepti> reseptit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            String selitys = rs.getString("selitys");

            reseptit.add(new Resepti(id, nimi, selitys));
        }

        rs.close();
        stmt.close();
        connection.close();

        return reseptit;
    }
    
    public List<RaakaAine> findIngredient(Integer resepti_key) throws SQLException {
        Connection conn = database.getConnection();
        List<RaakaAine> raakaAineet = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT RaakaAine.id, RaakaAine.nimi FROM RaakaAine, ReseptiRaakaAine \n" 
                + "WHERE ReseptiRaakaAine.resepti_id = ? AND ReseptiRaakaAine.raaka_aine_id = RaakaAine.id ");
        stmt.setInt(1, resepti_key);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            raakaAineet.add(new RaakaAine(rs.getInt("id"), rs.getString("nimi")));
        }
        return raakaAineet;
    }
    
    private Resepti findByNameResepti(String name) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Resepti WHERE nimi = ?");
            stmt.setString(1, name);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Resepti(result.getInt("id"), result.getString("nimi"), result.getString("selitys"));
        }
    }
    
    @Override
    public Resepti saveOrUpdate(Resepti object) throws SQLException {
        // simply support saving -- disallow saving if task with 
        // same name exists
        Resepti byName = findByNameResepti(object.getNimi());

        if (byName != null) {
            return byName;
        }

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Resepti (nimi, selitys) VALUES (?, ?)");
            stmt.setString(1, object.getNimi());
            stmt.setString(2, object.getSelitys());
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        }

        return findByNameResepti(object.getNimi());

    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Resepti WHERE id = ?");
        stmt.setInt(1,key);
        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
    }

}
