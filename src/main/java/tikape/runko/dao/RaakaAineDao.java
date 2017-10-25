/*
 * RaakaAine-taulun muokkamiseen ja SQL-komentojen tekemiseen tarkoitettu
 * luokka.
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
    
    //Etsii tietokannasta avaimen perusteella RaakaAine-olion
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
    
    //Etsii kaikki oliot tietokannasta ja palauttaa ne aakkosjärjestyksessä.
    @Override  
    public List<RaakaAine> findAll() throws SQLException {
        List<RaakaAine> aineet = new ArrayList<>();

        try (Connection conn = database.getConnection();
            ResultSet result = conn.prepareStatement("SELECT id, nimi FROM RaakaAine ORDER BY nimi").executeQuery()) {

            while (result.next()) {
                aineet.add(new RaakaAine(result.getInt("id"), result.getString("nimi")));
            }
            
            result.close();
            conn.close();
        }
        
        return aineet;
    }
    
    //Etsii Reseptit, joissa esiintyy tietty raaka-aine
    public List<Resepti> findResepti(Integer raakaAine_key) throws SQLException {
        Connection conn = database.getConnection();
        List<Resepti> reseptit = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT Resepti.id, Resepti.nimi, Resepti.selitys FROM Resepti, ReseptiRaakaAine \n" 
                + "WHERE ReseptiRaakaAine.resepti_id = Resepti.id AND ReseptiRaakaAine.raaka_aine_id = ? "
                + "GROUP BY Resepti.nimi ORDER BY Resepti.nimi");
        stmt.setInt(1, raakaAine_key);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            reseptit.add(new Resepti(rs.getInt("id"), rs.getString("nimi"), rs.getString("selitys")));
        }
        return reseptit;
    }
    
    //Etsii reseptiin liittyvät raaka-aineet ja järjestää ne järjestysluvun mukaan
    public List<RaakaAine> findRaakaAineet(Integer resepti_key) throws SQLException {
        String query = "SELECT RaakaAine.id, RaakaAine.nimi FROM RaakaAine, ReseptiRaakaAine\n"
                + "              WHERE RaakaAine.id = ReseptiRaakaAine.raaka_aine_id "
                + "                  AND ReseptiRaakaAine.resepti_id = ? GROUP BY RaakaAine.id ORDER BY ReseptiRaakaAine.jarjestys ASC";

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
    
    //Etsii raaka-aineen nimen perusteella
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
    /*Tallentaa tai päivittää raaka-aineen tietokantaan ja palauttaa
     raaka-aine olion oikealla id:llä
    */
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
            stmt.close();
            conn.close();
        }

        return findByNameRaakaAine(object.getNimi());

    }
    
    //Poistaa raaka-aineen tietokannasta
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
