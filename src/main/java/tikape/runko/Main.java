package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import spark.Spark;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.*;
import tikape.runko.dao.RaakaAineDao;
import tikape.runko.dao.ReseptiRaakaAineDao;
import tikape.runko.dao.ReseptiDao;
import tikape.runko.domain.RaakaAine;
import tikape.runko.domain.ReseptiRaakaAine;
import tikape.runko.domain.Resepti;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:smoothiet.db");

        ReseptiDao reseptit = new ReseptiDao(database);
        RaakaAineDao raakaAineet = new RaakaAineDao(database);
        ReseptiRaakaAineDao reseptiRaakaAineet = new ReseptiRaakaAineDao(database);
        
        Spark.get("/", (req, res) -> {
            HashMap map = new HashMap<>();

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("/raakaAineet", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("raakaAineet", raakaAineet.findAll());
            map.put("reseptit", reseptit.findAll());

            return new ModelAndView(map, "raakaAineet");
        }, new ThymeleafTemplateEngine());
        
        Spark.post("/raakaAineet", (req, res) -> {
            RaakaAine aine = new RaakaAine(-1, req.queryParams("nimi"));
            raakaAineet.saveOrUpdate(aine);

            res.redirect("/raakaAineet");
            return "";
        });
        
        Spark.get("/reseptit", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("reseptit", reseptit.findAll());

            return new ModelAndView(map, "reseptit");
        }, new ThymeleafTemplateEngine());
        
        Spark.post("/reseptit", (req, res) -> {
            Resepti resepti = new Resepti(-1,req.queryParams("nimi"));
            int id =reseptit.saveOrUpdate(resepti).getId();
            
            res.redirect("/reseptit/" + id);
            return "";
        });
        
        Spark.get("/reseptit/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Integer reseptiId = Integer.parseInt(req.params(":id"));
            map.put("resepti", reseptit.findOne(reseptiId));
            map.put("raakaAineet", raakaAineet.findRaakaAineet(reseptiId));
            map.put("reseptiRaakaAineet", reseptiRaakaAineet.findForResepti(reseptiId));
            
            return new ModelAndView(map, "resepti");
        }, new ThymeleafTemplateEngine());
        /*
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/opiskelijat", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelijat", reseptit.findAll());

            return new ModelAndView(map, "opiskelijat");
        }, new ThymeleafTemplateEngine());

        get("/opiskelijat/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelija", reseptit.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "opiskelija");
        }, new ThymeleafTemplateEngine());
        */
    }
    
}
