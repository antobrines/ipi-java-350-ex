package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.exception.EmployeException;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

class EmployeTest {

    @Test
    void testAugmenterSalaireXPourcent() throws EmployeException {
        //Given
        Employe employe = new Employe("Cena", "John", "T12345", LocalDate.now(), 1500d, 1,1.0);
        Double pourcentage = 5d;

        //When
        employe.augmenterSalaire(pourcentage);

        //Then
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1575d);
    }

    @Test
    void testAugmenterSalaireNull() throws EmployeException{
        //Given
        Employe employe = new Employe("Cena", "John", "T12345", LocalDate.now(), null, 1,1.0);
        Double pourcentage = 10d;

        //When / Then
        Assertions.assertThatThrownBy(() -> {
            employe.augmenterSalaire(pourcentage);
            }).isInstanceOf(EmployeException.class).hasMessage("Le salaire doit être différent de null !");
    }

    @Test
    void testAugmenterSalaireNegatif() throws EmployeException {
        //Given
        Employe employe = new Employe("Cena", "John", "T12345", LocalDate.now(), 1500d, 1,1.0);
        Double pourcentage = -5d;

        //When / Then
        Assertions.assertThatThrownBy(() -> {
            employe.augmenterSalaire(pourcentage);
            }).isInstanceOf(EmployeException.class).hasMessage("Augmente le salaire, ne le diminue pas !");
    }

    @ParameterizedTest
    @CsvSource({
            "'2004-01-01', 1, 11",
            "'2019-01-01', 1, 8",
            "'2019-01-01', 0.5, 4",
            "'2020-01-01', 1, 10",
            "'2021-01-01', 1, 10",
            "'2022-01-01', 1, 10",
            "'2026-01-01', 1, 9",
            "'2032-01-01', 1, 11",
            "'2032-01-01', 0.25, 2",
            "'2044-01-01', 1, 9",
    })
    void testNbRTT(LocalDate date, Double tmpsActivité, Integer nbDeRTTAttendu) {
        //Given
        Employe employe = new Employe("Cena", "John", "M12345", LocalDate.now(), 1400d, 1, 1d);
        employe.setTempsPartiel(tmpsActivité);

        //When
        Integer nbRtt = employe.getNbRtt(date);

        //Then
        Assertions.assertThat(nbRtt).isEqualTo(nbDeRTTAttendu);
    }

    @Test
    void testNbAnneeAncienneteNow() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());

        //When
        Integer nbAnnees = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnnees).isZero();
    }

    @Test
    void testNbAnneeAncienneteNowMoins2() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(2));

        //When
        Integer nbAnnees = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnnees).isEqualTo(2);
    }

    @Test
    void testNbAnneeAncienneteNowPlus3() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(3));

        //When
        Integer nbAnnees = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnnees).isNull();
    }

    @Test
    void testNbAnneeAncienneteNull() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);

        //When
        Integer nbAnnees = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnnees).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "'C12345', 1.0, 0, 1, 1000.0",
            "'T12345', 1.0, 0, , 1000.0",
            "'M12345', 1.0, 0, 1, 1700.0",
            ", 1.0, 2, 2, 2500.0"
    })
    void testGetPrimeAnnuelle(String matricule, Double tempsPartiel, Integer nbAnneeAnciennete, Integer performance, Double primeCalculee) {
        //Given
        Employe employe = new Employe();
        employe.setMatricule(matricule);
        employe.setTempsPartiel(tempsPartiel);
        employe.setDateEmbauche(LocalDate.now().minusYears(nbAnneeAnciennete));
        employe.setPerformance(performance);

        //When
        Double prime = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(prime).isEqualTo(primeCalculee);
    }
}
