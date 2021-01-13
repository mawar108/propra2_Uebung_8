package covidtracer.controller;

import covidtracer.kontaktliste.Index;
import covidtracer.kontaktliste.KontaktListe;
import covidtracer.kontaktliste.Kontaktperson;
import covidtracer.persistence.KontaktListeRepository;
import covidtracer.service.KontaktListen;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class Webpage {

  @Autowired
  KontaktListen listen;

  @Autowired
  KontaktListeRepository repo;

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("listen", listen.alle());
    return "index";
  }

  @GetMapping("/liste/{id}")
  public String details(@PathVariable("id") long id, Model model) {
    model.addAttribute("liste", listen.finde(id));
    return "details";
  }

  @PostMapping("/")
  public String erzeugeListe(String nachname, String vorname) {
    KontaktListe liste = new KontaktListe();
    liste.setIndex(new Index(nachname, vorname, LocalDate.now()));
    repo.save(liste);
    return "redirect:/";
  }

  @PostMapping("/liste/{id}")
  public String kontaktpersonHinzufuegen(@PathVariable("id") long id,
                                         Kontaktperson kontaktperson) {
    KontaktListe liste = repo.findById(id).orElseThrow(() ->
        new HttpClientErrorException(HttpStatus.NOT_FOUND,
            "Keine Liste mit id " + id + " vorhanden."));
    liste.addKontakt(kontaktperson);
    repo.save(liste);
    return "redirect:/liste/" + id;
  }

  @PostMapping("/remove/from/{id}")
  public String kontaktpersonEntfernen(@PathVariable("id") long id,
                                       Kontaktperson kontaktperson) {
    KontaktListe liste = repo.findById(id).orElseThrow(() ->
        new HttpClientErrorException(HttpStatus.NOT_FOUND,
            "Keine Liste mit id " + id + " vorhanden."));
    liste.removeKontakt(kontaktperson);
    repo.save(liste);
    return "redirect:/liste/" + id;
  }

  @GetMapping("/report")
  public String report(Model model) {
    SortedMap<Integer, Long> sortedByAge = listen.kontakteNachZeitpunktSortiert();
    model.addAttribute("alter", sortedByAge);
    return "report";
  }




}
