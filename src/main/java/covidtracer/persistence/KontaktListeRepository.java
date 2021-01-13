package covidtracer.persistence;

import covidtracer.kontaktliste.KontaktListe;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface KontaktListeRepository extends CrudRepository<KontaktListe, Long> {
  public List<KontaktListe> findAll();
}
