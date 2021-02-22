package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.*;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeServiceIntegrationTest {

    @Autowired
    EmployeService employeService;

    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        employeRepository.deleteAll();
    }

    @Test
    void testIntegrationEmbaucheEmploye() throws EmployeException {
        //Given
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        Employe employe = employeRepository.findByMatricule("T12346");
        Assertions.assertNotNull(employe);
        Assertions.assertEquals(nom, employe.getNom());
        Assertions.assertEquals(prenom, employe.getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employe.getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T12346", employe.getMatricule());
        Assertions.assertEquals(1.0, employe.getTempsPartiel().doubleValue());
        Assertions.assertEquals(1825.46, employe.getSalaire().doubleValue());
    }

    @Test
    void testIntegrationCalculPerformanceCommercial() throws EmployeException {
        //Given
        String nom = "Cena";
        String prenom = "John";
        String matricule = "C12345";
        LocalDate dateEmbauche = LocalDate.now();
        Double salaire = 2300d;
        Integer performance = 2;
        Double tempsPartiel = 1d;
        employeRepository.save(new Employe(nom, prenom, matricule, dateEmbauche, salaire, performance, tempsPartiel));

        //When
        employeService.calculPerformanceCommercial(matricule, 2000L, 10000L);

        //Then
        Employe employe = employeRepository.findByMatricule(matricule);
        Assertions.assertEquals(1, employe.getPerformance());
    }

    @Test
    void testCalculPerformanceCommercialWithAvgInf() throws EmployeException {
        //Given
        employeService.embaucheEmploye("Mysterio","Rey", Poste.COMMERCIAL, NiveauEtude.LICENCE,1.0);
        employeService.embaucheEmploye("Cena","John", Poste.COMMERCIAL, NiveauEtude.LICENCE,1.0);

        //When
        employeService.calculPerformanceCommercial("C00001",1201l,1000L);
        employeService.calculPerformanceCommercial("C00001",1201l,1000L);
        employeService.calculPerformanceCommercial("C00002",1201l,1000L);
        List<Employe> employes = employeRepository.findAll();
        Employe employe =  employes.get(employes.size() - 1);

        //Then
        Assertions.assertEquals(5, employe.getPerformance());
    }

}