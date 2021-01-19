package covidtracer.kontaktliste;

import covidtracer.stereotypes.ClassOnly;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class Index {

  private final String nachname;
  private final String vorname;
  private final LocalDate erstbefund;

  public Index(String nachname, String vorname, LocalDate erstbefund) {
    this.nachname = nachname;
    this.vorname = vorname;
    this.erstbefund = erstbefund;
  }

  public long getTage() {
    return Period.between(erstbefund, LocalDate.now()).getDays();
  }

  public LocalDate getErstbefund() {
    return erstbefund;
  }

  public String getNachname() {
    return nachname;
  }

  public String getVorname() {
    return vorname;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    Index index = (Index) o;
    return Objects.equals(nachname, index.nachname) &&
        Objects.equals(vorname, index.vorname) &&
        Objects.equals(erstbefund, index.erstbefund);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nachname, vorname, erstbefund);
  }
}
