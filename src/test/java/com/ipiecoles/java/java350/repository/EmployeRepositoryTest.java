package com.ipiecoles.java.java350.repository;


import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

@DataJpaTest
class EmployeRepositoryTest {

    @Autowired
    EmployeRepository employeRepository;

    @BeforeEach
        //@AfterEach
    void setUp(){
        employeRepository.deleteAll();
    }


    @Test
    void findLastMatricule0Employe() {
        //Given

        //When
        String lastMatricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertThat(lastMatricule).isNull();
    }

    @Test
    void testFindLastMatricule1Employe(){
        //Given
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), 2000d, 1, 1.0));

        //When
        String lastMatricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertThat(lastMatricule).isEqualTo("12345");
    }

    @Test
    void findLastMatricule3Employes() {
        //Given
        Employe e1 = new Employe("Doe", "John", "T12345", LocalDate.now(), 2000d, 1,1.0);
        Employe e2 = new Employe("Doe", "Jane", "C67890", LocalDate.now(), 2000d, 1,1.0);
        Employe e3 = new Employe("Doe", "John", "M45678", LocalDate.now(), 2000d, 1,1.0);

        employeRepository.save(e1);
        employeRepository.save(e2);
        employeRepository.save(e3);

        //When
        String lastMatricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertThat(lastMatricule).isEqualTo("67890");
    }

    @Test
    void testFindLastMatriculeMatriculeIfMatriculeIsNull(){
        //Given
        Employe employe = new Employe("Doe", "John", null, LocalDate.now(), 1400d, 1, 1d);
        employeRepository.save(employe);

        //When
        String employeRep = employeRepository.findLastMatricule();

        //Then
        Assertions.assertThat(employeRep).isNull();
    }

    @Test
    void testAvgPerformanceWhereMatriculeStartsWith() {
        //Given
        Employe e0 = new Employe("Cena", "John", "C12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 4, 1.0);
        Employe e1 = new Employe("Mysterio", "Rey", "C23456", LocalDate.now(), Entreprise.SALAIRE_BASE, 2, 1.0);
        Employe e2 = new Employe("Orton", "Randy", "C34567", LocalDate.now(), Entreprise.SALAIRE_BASE, 6, 1.0);
        Employe e3 = new Employe("Johnson", "Dwayne", "M12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 6, 1.0);
        Employe e4 = new Employe("Triple", "H", "C45678", LocalDate.now(), Entreprise.SALAIRE_BASE, null, 1.0);
        employeRepository.save(e0);
        employeRepository.save(e1);
        employeRepository.save(e2);
        employeRepository.save(e3);
        employeRepository.save(e4);

        //When
        Double avgPerformance = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        //Then
        Assertions.assertThat(avgPerformance).isEqualTo(4d);
    }

    @Test
    void testAvgPerformanceWhereNoAvg() {
        //Given
        Employe e1 = new Employe("Dwayne", "Johnson", "M12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 6, 1.0);
        employeRepository.save(e1);

        //When
        Double avgPerformance = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        //Then
        Assertions.assertThat(avgPerformance).isNull();
    }

    @Test
    void testAvgPerformanceWhith0(){
        //Given
        Employe e0 = new Employe("Cena", "John", "C12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 0, 1.0);
        Employe e1 = new Employe("Mysterio", "Rey", "C23456", LocalDate.now(), Entreprise.SALAIRE_BASE, 0, 1.0);
        Employe e2 = new Employe("Orton", "Randy", "C34567", LocalDate.now(), Entreprise.SALAIRE_BASE, 0, 1.0);
        employeRepository.save(e0);
        employeRepository.save(e1);
        employeRepository.save(e2);

        //When
        Double moyenne = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        //Then
        Assertions.assertThat(moyenne).isZero();
    }

}