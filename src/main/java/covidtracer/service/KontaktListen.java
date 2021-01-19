package covidtracer.service;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;


import covidtracer.kontaktliste.KontaktListe;
import covidtracer.persistence.KontaktListeRepository;
import covidtracer.stereotypes.ClassOnly;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class KontaktListen {

  @Autowired
  final KontaktListeRepository repo;

  public KontaktListen(KontaktListeRepository repo) {
    this.repo = repo;
  }

  public SortedMap<Integer, Long> kontakteNachZeitpunktSortiert() {
    List<KontaktListe> listen = repo.findAll();
    Map<Integer, Long> alter =
        listen.stream().collect(groupingBy(KontaktListen::alter, counting()));
    SortedMap<Integer, Long> sortedByAge = new TreeMap<>();
    sortedByAge.putAll(alter);
    return sortedByAge;
  }

  @ClassOnly
  public static int alter(KontaktListe liste) {
    LocalDate erstbefund = liste.getIndex().getErstbefund();
    LocalDate now = LocalDate.now();
    return Period.between(erstbefund, now).getDays();
  }

  public List<KontaktListe> alle() {
    return repo.findAll();
  }

  public KontaktListe finde(long id) {
    return repo.findById(id).orElseThrow(() ->
        new HttpClientErrorException(HttpStatus.NOT_FOUND,
            "Keine Liste mit id " + id + " vorhanden."));
  }
}
