package com.ipiecoles.java.java350.service;


import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {

    @InjectMocks
    EmployeService employeService;

    @Mock
    EmployeRepository employeRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    void testEmbaucheEmploye0Employe() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1064.85);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(0.5);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getMatricule()).isEqualTo("M00001");
    }

    @Test
    void testEmbaucheEmployeXEmployes() throws EmployeException {
        //Given
        when(employeRepository.findLastMatricule()).thenReturn("45678");
        when(employeRepository.findByMatricule(Mockito.anyString())).thenReturn(null);
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.LICENCE;


        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, null);

        //Then 
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        //On vérifie que la méthode save a bien été appelée sur employeRepository, et on capture le paramètre
        Mockito.verify(employeRepository).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getMatricule()).isEqualTo("M45679");
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(null);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        //1521.22 * 1.2 * 1.0 = 1825.46
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.46);
    }

    @Test
    void testEmbaucheEmployeManagerMiTempsMaster99999(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("99999");
        try{
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail("Aurait du lancer une exception");
        }catch (EmployeException e){
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("Limite des 100000 matricules atteinte !");
        }

    }
    
    @Test
    void testEmbaucheEmployeExistDeja(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(new Employe());
        try {
            //When
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail("Aurait du lancer une exception");
        } catch (Exception e){
            //Then
            Assertions.assertThat(e).isInstanceOf(EntityExistsException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("L'employé de matricule M00001 existe déjà en BDD");
        }
    }

    @ParameterizedTest
    @CsvSource({
            " 0 , 1 ",
            " 850 , 1 ",
            " 1050 , 1 ",
            " 1051 , 3 ",
            " 1201 , 6 ",
    })
    void testCalculPerformanceCommercial(Long chiffreAffaire, Integer performanceAttendu) throws EmployeException {
        //Given
        String matricule = "C12345";
        Long objectif = 1000L;
        Employe employe = new Employe("Cena","John",matricule,LocalDate.now(), Entreprise.SALAIRE_BASE,1,1d);
        when(employeRepository.findByMatricule(matricule)).thenReturn(employe);
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(1d);
        when(employeRepository.save(Mockito.any(Employe.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        //When
        employeService.calculPerformanceCommercial(matricule,chiffreAffaire,objectif);

        //Then
        ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeCaptor.capture());
        Employe employeWithNewPerformance = employeCaptor.getValue();
        Assertions.assertThat(employeWithNewPerformance.getPerformance()).isEqualTo(performanceAttendu);
    }

    @ParameterizedTest
    @CsvSource({
            "C12345, , 88000, Le chiffre d'affaire traité ne peut être négatif ou null !",
            "C12345, -50000, 88000, Le chiffre d'affaire traité ne peut être négatif ou null !",
            "C12345, 50000, , L'objectif de chiffre d'affaire ne peut être négatif ou null !",
            "C12345, 50000, -88000, L'objectif de chiffre d'affaire ne peut être négatif ou null !",
            "M12345, 50000, 88000, Le matricule ne peut être null et doit commencer par un C !",
            ", 50000, 88000, Le matricule ne peut être null et doit commencer par un C !",
            "C12345, 50000, 88000, Le matricule C12345 n'existe pas !",
    })
    void calculPerformanceCommercialExceptions(String matricule, Long caTraite, Long objectifCa, String exceptionMessage) {
        //Given
        try {
            //When
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
            Assertions.fail("Aurait du lancer une exception");
        } catch (Exception e) {
            //Then
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo(exceptionMessage);
        }
    }

}