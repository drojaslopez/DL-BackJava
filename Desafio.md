
El entregable formal consiste en la publicación de un repositorio estructurado en GitHub que
cumpla con los siguientes tres requerimientos:

1. Core de Entidades de Dominio Puro (3 Puntos): Estructura base del modelo de negocio de
   su temática autónoma escrita en Java puro, libre de acoplamientos a frameworks o bases
   de datos físicas. Las clases deben poseer nombres consistentes alineados a su glosario
   técnico interno.
2. Suite Automatizada con JUnit 5 y Mockito (3 Puntos): Enfoque exhaustivo de casos de
   prueba estructurados rigurosamente bajo el Patrón AAA (Arrange, Act, Assert). El sistema
   debe controlar de forma limpia las excepciones de negocio personalizadas mediante
   assertThrows e interceptar las dependencias utilizando dobles de prueba e inyección por
   constructor.
3. Cobertura Matemática del 100% en Métodos Críticos (4 Puntos): El repositorio de código
   debe respaldar, mediante la suite automatizada, una cobertura lógica verificable del 100%
   (Branch/Line Coverage) en todos sus métodos y flujos de negocio centrales. No se
   aceptarán líneas de lógica condicional desprotegidas
