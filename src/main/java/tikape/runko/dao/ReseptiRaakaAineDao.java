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

public class ReseptiRaakaAineDao implements Dao<ReseptiRaakaAine, Integer> {

    private Database database;

    public ReseptiRaakaAineDao(Database database) {
        this.database = database;
    }

    @Override
    public ReseptiRaakaAine findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ReseptiRaakaAine WHERE id = ?");
        stmt.setInt(1, key);
        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        Integer raakaAineId = rs.getInt("raaka_aine_id");
        Integer reseptiId = rs.getInt("resepti_id");
        Integer jarjestys = rs.getInt("jarjestys");        
        String maara = rs.getString("maara");
        String ohje = rs.getString("ohje");

        ReseptiRaakaAine o = new ReseptiRaakaAine(id, raakaAineId, reseptiId, jarjestys, maara, ohje);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }

    @Override
    public List<ReseptiRaakaAine> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ReseptiRaakaAine");

        ResultSet rs = stmt.executeQuery();
        List<ReseptiRaakaAine> lista = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer raakaAineId = rs.getInt("raaka_aine_id");
            Integer reseptiId = rs.getInt("resepti_id");
            Integer jarjestys = rs.getInt("jarjestys");            
            String maara = rs.getString("maara");
            String ohje = rs.getString("ohje");            

            lista.add(new ReseptiRaakaAine(id, raakaAineId, reseptiId, jarjestys, maara, ohje));
        }

        rs.close();
        stmt.close();
        connection.close();

        return lista;
    }

    public ReseptiRaakaAine saveOrUpdate(ReseptiRaakaAine raa) throws SQLException {
        int id=-1;
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ReseptiRaakaAine WHERE raaka_aine_id = ? AND resepti_id = ? AND jarjestys = ?");
        stmt.setInt(1,raa.getRaakaAineId());
        stmt.setInt(2,raa.getReseptiId());
        stmt.setInt(3,raa.getJarjestys());
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
            PreparedStatement stmt1 = conn.prepareStatement("UPDATE ReseptiRaakaAine SET maara = ?, ohje = ? WHERE id = ?");
            stmt1.setInt(3, raa.getId());
            stmt1.setString(1,raa.getMaara());
            stmt1.setString(2, raa.getOhje());
            stmt1.executeUpdate();
            stmt1.close();
            
        } else {
            PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO ReseptiRaakaAine (raaka_aine_id,resepti_id,jarjestys,maara,ohje) VALUES (?,?,?,?,?)");
            stmt1.setInt(1, raa.getRaakaAineId());
            stmt1.setInt(2,  raa.getReseptiId());
            stmt1.setInt(3, raa.getJarjestys());
            stmt1.setString(4, raa.getMaara());
            stmt1.setString(5, raa.getOhje());
            stmt1.executeUpdate();
            stmt1.close();
            rs.close();
            
            rs =stmt.executeQuery();
            id = rs.getInt("id");
            
        }
        
        rs.close();
        stmt.close();
        conn.close();
        ReseptiRaakaAine raa1 = new ReseptiRaakaAine(raa);
        raa1.setId(id);
        return raa1;
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM ReseptiRaakaAine WHERE id = ?");
        stmt.setInt(1,key);
        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
    }

}
