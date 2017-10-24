package tikape.runko;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.Comparator;
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
    
    public static final String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static Random rand = new Random();
    
    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:smoothiet.db");
        ReseptiDao reseptit = new ReseptiDao(database);
        RaakaAineDao raakaAineet = new RaakaAineDao(database);
        ReseptiRaakaAineDao reseptiRaakaAineet = new ReseptiRaakaAineDao(database);
        
        HashMap<String,Resepti> resepteja = new HashMap<String,Resepti>();
        HashMap<Resepti, List<ReseptiRaakaAine> > raat = new HashMap<Resepti, List<ReseptiRaakaAine>>();
        
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
        
        Spark.get("/raakaAineet/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Integer aineId = Integer.parseInt(req.params(":id"));
            map.put("raakaAine", raakaAineet.findOne(aineId));
            map.put("resepti", raakaAineet.findResepti(aineId));
            
            return new ModelAndView(map, "raakaAineReseptit");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("/reseptit", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("reseptit", reseptit.findAll());

            return new ModelAndView(map, "reseptit");
        }, new ThymeleafTemplateEngine());
        
        Spark.post("/reseptit", (req, res) -> {
            Resepti resepti = new Resepti(-1,req.queryParams("nimi"), "");
            String id = randomStringGenerator(20);
            resepteja.put(id, resepti);
            raat.put(resepti, new ArrayList<ReseptiRaakaAine>());
            res.redirect("/reseptit2/" + id);
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
        
        Spark.get("/reseptit2/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            String id = req.params(":id");
            
            Resepti resepti = resepteja.get(id);
            List<ReseptiRaakaAine> raa= raat.get(resepti);
            sortListOfReseptiRaakaAine(raa);
            Set<RaakaAine> raakaainelista = new HashSet<>();
            List<RaakaAine> raakaainelista2 = raakaAineet.findAll();
            
            for (ReseptiRaakaAine e : raa) {
                raakaainelista.add(raakaAineet.findOne(e.getRaakaAineId()));
            }
            
            map.put("avain",id);
            map.put("resepti",resepti);
            map.put("raakaaineet",raakaainelista);
            map.put("raakaaineet2",raakaainelista2);
            map.put("reseptiRaakaAineet", raa);
            
            return new ModelAndView(map, "resepti2");
        }, new ThymeleafTemplateEngine());
        
        Spark.post("/reseptit2/:id", (req,res)-> {
           String id = req.params(":id");
           Resepti resepti = resepteja.get(id);
           Integer raaId = Integer.parseInt(req.queryParams("raakaaineid"));
           String maara = req.queryParams("maara");
           String ohje = req.queryParams("ohje");
           List<ReseptiRaakaAine> raa = raat.get(resepti);
           
           raa.add(new ReseptiRaakaAine(-1,raaId,resepti.getId(),raa.size()+1,maara,ohje));
           res.redirect("/reseptit2/" + id);
           return "";
        });
        
        Spark.post("/reseptit3/:id", (req,res)-> {
           String id = req.params(":id");
           Resepti resepti = resepteja.get(id);
          
           Integer raaJarjestys = Integer.parseInt(req.queryParams("reseptiraakaainejarjestys"));
           
           List<ReseptiRaakaAine> raa = raat.get(resepti);
           ListIterator<ReseptiRaakaAine> litr=raa.listIterator();
           int ind=-1;
           while (litr.hasNext()) {
               ind = litr.nextIndex();
               if (litr.next().getJarjestys()==raaJarjestys) {
                   break;
               }
               ind=-1;
           }
           
           raa.remove(ind);
           res.redirect("/reseptit2/" + id);
           return "";
        });
        
        Spark.post("/reseptit4/:id", (req,res)-> {
           String id = req.params(":id");
           Resepti resepti = resepteja.get(id);
           
           String selitys = req.queryParams("selitys");
           resepti.setSelitys(selitys);
           
           Resepti sqlresepti = reseptit.saveOrUpdate(resepti);
           for (ReseptiRaakaAine resraa : raat.get(resepti) ) {
               reseptiRaakaAineet.saveOrUpdate(new ReseptiRaakaAine(-1,resraa.getRaakaAineId(), sqlresepti.getId(), resraa.getJarjestys(), resraa.getMaara(), resraa.getOhje()));
           }
           raat.remove(resepti);
           resepteja.remove(req.params(":id"));
           
           res.redirect("/reseptit");
           return "";
        });
    }
    
    public static String randomStringGenerator(int length) {
        String randkey = "";
        int ll = alphabet.length();
        for (int i = 0; i<length; i++) {
            randkey = randkey+alphabet.charAt(rand.nextInt(ll));
        }
        return randkey;
    }
    public static void sortListOfReseptiRaakaAine(List<ReseptiRaakaAine> lista) {
        Collections.sort(lista, new Comparator<ReseptiRaakaAine>(){
            public int compare(ReseptiRaakaAine r1, ReseptiRaakaAine r2) {
                return r1.getJarjestys()-r2.getJarjestys();
            }
        });
    }
     
}
