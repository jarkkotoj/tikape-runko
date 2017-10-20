/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.*;

public class RaakaAineDao implements Dao<RaakaAine, Integer> {

    private Database database;

    public RaakaAineDao(Database database) {
        this.database = database;
    }

    @Override
    public RaakaAine findOne(Integer key) throws SQLException {
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
    }

    @Override
    public List<RaakaAine> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM RaakaAine");

        ResultSet rs = stmt.executeQuery();
        List<RaakaAine> raakaAineet = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            raakaAineet.add(new RaakaAine(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return raakaAineet;
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
