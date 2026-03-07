package org.example.talentmatch_ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.talentmatch_ai.model.Candidate;
import org.example.talentmatch_ai.model.JobOffer;
import org.example.talentmatch_ai.repository.CandidateRepo;
import org.example.talentmatch_ai.repository.JobOfferRepo;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {


    private final CandidateRepo candidateRepo;
    private final JobOfferRepo jobOfferRepo;

    @Override
    public void run(String @NonNull ... args) {
        if (candidateRepo.count() > 0) {
            log.info("Data already seeded, skipping.");
            return;
        }

        // ─── Candidates ──────────────────────────────────────────
        Candidate candidate1 = Candidate.builder()
                .firstName("Amina")
                .lastName("Diallo")
                .email("amina.diallo@example.com")
                .githubUsername("aminaDev")
                .skills(List.of("JavaScript", "React", "CSS", "HTML", "TypeScript"))
                .yearsOfExperience(1)
                .bio("Développeuse frontend junior passionnée par le design et l'UX. "
                        + "Diplômée en informatique, stage de 6 mois sur une application React en production.")
                .createdAt(LocalDateTime.now())
                .build();

        Candidate candidate2 = Candidate.builder()
                .firstName("Marc")
                .lastName("Dupont")
                .email("marc.dupont@example.com")
                .githubUsername("marcdup")
                .skills(List.of("Java", "Spring Boot", "Angular", "Kubernetes", "AWS", "PostgreSQL"))
                .yearsOfExperience(10)
                .bio("Architecte fullstack senior avec 10 ans d'expérience. "
                        + "Expert cloud AWS, CI/CD, et leadership technique d'équipes de 8+ développeurs.")
                .createdAt(LocalDateTime.now())
                .build();

        Candidate candidate3 = Candidate.builder()
                .firstName("Kevin")
                .lastName("Durant")
                .email("kevin@example.com")
                .githubUsername("KD")
                .skills(List.of("Java", "Spring Boot", "Hibernate", "Docker", "PostgreSQL"))
                .yearsOfExperience(5)
                .bio("Développeur Java/Spring Boot avec 5 ans d'expérience en microservices et APIs REST. "
                        + "Contributeur open source, habitué aux environnements CI/CD et aux revues de code.")
                .createdAt(LocalDateTime.now())
                .build();

        candidateRepo.saveAll(List.of(candidate1, candidate2, candidate3));
        log.info("3 candidates seeded");

        // ─── Job Offers ──────────────────────────────────────────
        JobOffer offer1 = JobOffer.builder()
                .title("Développeur Java/Spring Boot")
                .company("TechCorp")
                .requiredSkills(List.of("Java", "Spring Boot", "PostgreSQL", "Docker", "REST API"))
                .description("""
                        Contexte :
                        TechCorp est une fintech en forte croissance (150 collaborateurs) spécialisée dans le paiement \
                        en ligne. L'équipe backend est composée de 8 développeurs organisés en 2 squads agiles (Scrum, \
                        sprints de 2 semaines). Le projet porte sur une plateforme de paiement traitant plus de \
                        2 millions de transactions par jour.
                        
                        Responsabilités :
                        - Concevoir et développer des APIs REST performantes avec Spring Boot
                        - Implémenter la logique métier dans une architecture microservices (12 services)
                        - Écrire des tests unitaires (JUnit 5, Mockito) et d'intégration (Testcontainers)
                        - Participer activement aux code reviews et aux daily stand-ups
                        - Optimiser les requêtes SQL et les performances applicatives
                        - Contribuer à la mise en place du pipeline CI/CD (GitLab CI, Docker, Kubernetes)
                        
                        Stack technique : Java 21, Spring Boot 3, PostgreSQL 16, Docker, Kubernetes, Kafka, \
                        GitLab CI, SonarQube.
                        
                        Méthodologie : Scrum, pair programming, TDD encouragé.
                        """)
                .location("Paris, France")
                .salaryRange("45k-60k")
                .postedAt(LocalDateTime.now().minusDays(5))
                .build();

        JobOffer offer2 = JobOffer.builder()
                .title("Développeur Frontend React")
                .company("StartupFlow")
                .requiredSkills(List.of("React", "TypeScript", "CSS", "REST API", "Git"))
                .description("""
                        Contexte :
                        StartupFlow est une startup SaaS B2B (30 personnes) qui développe un outil de gestion de \
                        workflow collaboratif. L'équipe produit est composée de 4 développeurs frontend, 3 backend \
                        et 1 designer UX. Le produit est en phase de scale-up avec un objectif de x3 sur la base \
                        utilisateurs d'ici fin d'année.
                        
                        Responsabilités :
                        - Développer des composants React réutilisables au sein d'un design system interne
                        - Intégrer les APIs REST et gérer le state management (Redux Toolkit)
                        - Assurer la qualité du code avec des tests Jest et Cypress (couverture cible : 80%)
                        - Collaborer quotidiennement avec le designer UX pour implémenter les maquettes Figma
                        - Participer aux sprints (Kanban) et aux rétrospectives
                        - Optimiser les performances web (Core Web Vitals, lazy loading, code splitting)
                        
                        Stack technique : React 18, TypeScript 5, Vite, TailwindCSS, Redux Toolkit, Jest, \
                        Cypress, GitHub Actions.
                        
                        Méthodologie : Kanban, feature flags, déploiement continu.
                        """)
                .location("Lyon, France")
                .salaryRange("32k-42k")
                .postedAt(LocalDateTime.now().minusDays(2))
                .build();

        JobOffer offer3 = JobOffer.builder()
                .title("Architecte Logiciel Senior")
                .company("BankSecure")
                .requiredSkills(List.of("Java", "Spring Boot", "Kubernetes", "AWS", "Kafka", "MongoDB"))
                .description("""
                        Contexte :
                        BankSecure est un groupe bancaire européen (5 000 collaborateurs) en pleine transformation \
                        digitale. Le département IT compte 200 ingénieurs répartis en 25 squads. Vous intégrerez \
                        la direction architecture pour piloter la modernisation du SI core banking, un programme \
                        stratégique sur 3 ans avec un budget de 15M€.
                        
                        Responsabilités :
                        - Définir l'architecture cible microservices et event-driven (Kafka)
                        - Encadrer techniquement 3 squads de développement (15 développeurs)
                        - Piloter la migration du monolithe vers le cloud AWS (EKS, RDS, S3)
                        - Rédiger les ADR (Architecture Decision Records) et les standards techniques
                        - Garantir la sécurité applicative (OWASP Top 10) et la conformité réglementaire (DSP2, RGPD)
                        - Animer la communauté technique : tech talks, guildes, veille techno
                        - Participer au recrutement et au mentorat des développeurs seniors
                        
                        Stack technique : Java 21, Spring Boot 3, Kubernetes (EKS), AWS, Kafka, MongoDB, \
                        PostgreSQL, Terraform, ArgoCD, Datadog.
                        
                        Méthodologie : SAFe, Architecture as Code, RFC process.
                        """)
                .location("Paris, France")
                .salaryRange("75k-95k")
                .postedAt(LocalDateTime.now().minusDays(10))
                .build();

        jobOfferRepo.saveAll(List.of(offer1, offer2, offer3));
        log.info(" 3 job offers seeded");
    }
}
