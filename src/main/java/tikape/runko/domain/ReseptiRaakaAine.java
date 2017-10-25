package tikape.runko.domain;

/*Tietokannassa olevan ReseptiRaakaAine-liitostaulun käsittelyyn tarvittava
 luokka.*/

public class ReseptiRaakaAine {

    private Integer id;
    private Integer raakaAine_id;    
    private Integer resepti_id;
    private String maara;
    private Integer jarjestys;
    private String ohje;

    public ReseptiRaakaAine(Integer id, Integer raakaAine_id, Integer resepti_id, Integer jarjestys, String maara, String ohje) {
        this.id = id;
        this.resepti_id = resepti_id;
        this.raakaAine_id = raakaAine_id;
        this.jarjestys = jarjestys;
        this.maara = maara;
        this.ohje = ohje;
    }
    
    public ReseptiRaakaAine(ReseptiRaakaAine obj1) {
        this.id = obj1.id;
        this.resepti_id = obj1.resepti_id;
        this.raakaAine_id = obj1.raakaAine_id;
        this.jarjestys = obj1.jarjestys;
        this.maara = obj1.maara;
        this.ohje = obj1.ohje;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getReseptiId() {
        return resepti_id;
    }
    public Integer getRaakaAineId() {
        return raakaAine_id;
    }
    public void setReseptiId(Integer resepti_id) {
        this.resepti_id = resepti_id;
    }
    public void setRaakaAineId(Integer raakaAine_id) {
        this.raakaAine_id = raakaAine_id;
    }    
    public String getOhje() {
        return ohje;
    }
    public String getMaara() {
        return maara;
    }
    public Integer getJarjestys() {
        return jarjestys;
    }

    public void setOhje(String ohje) {
        this.ohje = ohje;
    }
    public void setMaara(String maara) {
        this.maara = maara;
    }
    public void setJarjestys(Integer jarjestys) {
        this.jarjestys = jarjestys;
    }
    
}
