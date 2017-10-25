package tikape.runko.domain;

/*
 * Luokka ja oliot Resepti-taulun muokkaamiseen SQL-tietokannassa.
 */

public class Resepti {

    private Integer id;
    private String nimi;
    private String selitys;

    public Resepti(Integer id, String nimi, String selitys) {
        this.id = id;
        this.nimi = nimi;
        this.selitys = selitys;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public String getSelitys() {
        return selitys;
    }

    public void setSelitys(String selitys) {
        this.selitys = selitys;
    }
    
    

}
