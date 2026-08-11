# DL-BackJava

Sistema de gestión de cuentas bancarias en **Java 17** con dominio puro (DDD), tests con **JUnit 5 + Mockito** y cobertura **JaCoCo ~98%**.

Bank account management system in **Java 17** with a pure domain (DDD), tests with **JUnit 5 + Mockito** and **JaCoCo ~98%** coverage.

## Estructura / Structure

```
src/
├── main/java/drl/desafio/
│   ├── dominio/            # Entities / exceptions (pure domain)
│   ├── excepciones/
│   └── servicio/
└── test/java/drl/desafio/
    ├── dominio/
    └── servicio/
```

## Documentación / Documentation

| Idioma | Language | Documento | Document |
|---|---|---|---|
| 🇪🇸 Español | Spanish | [README.es.md](README.es.md) | Análisis de estructura, propuestas de cambio y mejoras |
| 🇬🇧 English | English | [README.en.md](README.en.md) | Structure analysis, change proposals and improvements |

## Inicio rápido / Quick start

```bash
mvn clean verify
```

Reporte de cobertura: `target/site/jacoco/index.html`
