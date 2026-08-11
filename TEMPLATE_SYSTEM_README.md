# Sistema de Plantillas de Estructura de Proyectos

Este sistema permite definir estructuras de proyectos base y analizar/comparar proyectos existentes contra esas plantillas, generando propuestas de corrección automáticas.

## 📁 Archivos del Sistema

### 1. `project-template.yaml`
Archivo de configuración que define la estructura base del proyecto NeonPulse Ticketera. Contiene:

- **Stack tecnológico**: Java 17, Maven, Clean Architecture
- **Estructura de directorios**: Paquetes y carpetas requeridos
- **Configuración Maven**: Dependencias, plugins, configuración de build
- **Patrones de diseño**: Clean Architecture, DDD, etc.
- **Patrones de testing**: JUnit 5, Mockito, AssertJ
- **Reglas de validación**: Reglas que debe cumplir el proyecto
- **Variables personalizables**: Nombre del paquete, nombre del proyecto

### 2. `structure-analyzer.py`
Script en Python que analiza proyectos existentes y los compara contra la plantilla YAML.

**Características:**
- Análisis automático de estructura de directorios
- Detección de paquetes Java
- Verificación de configuración Maven
- Comparación contra plantilla
- Generación de propuestas de corrección
- Modo interactivo para aprobar cambios
- Exportación de reportes

## 🚀 Uso Básico

### Análisis de un Proyecto Existente

```bash
python structure-analyzer.py project-template.yaml /ruta/a/tu/proyecto
```

### Con Reporte de Salida

```bash
python structure-analyzer.py project-template.yaml /ruta/a/tu/proyecto --output reporte.txt
```

### Modo Interactivo

```bash
python structure-analyzer.py project-template.yaml /ruta/a/tu/proyecto --interactive
```

## 📋 Estructura del Archivo de Plantilla

El archivo YAML se organiza en secciones principales:

### 1. Información del Proyecto
```yaml
project:
  name: "Nombre del Proyecto"
  type: "tipo-de-proyecto"
  version: "1.0"
  description: "Descripción del proyecto"
```

### 2. Stack Tecnológico
```yaml
technology:
  language: "Java"
  version: "17"
  build_tool: "Maven"
  architecture: "Clean Architecture"
```

### 3. Estructura de Directorios
```yaml
directory_structure:
  root:
    - path: "src/main/java"
      purpose: "Código fuente"
      required: true
```

### 4. Estructura de Paquetes
```yaml
package_structure:
  base: "com.{base_package}"
  layers:
    - name: "domain"
      purpose: "Capa de dominio"
      subpackages:
        - name: "entity"
        - name: "valueobject"
```

### 5. Configuración Maven
```yaml
maven_config:
  pom_xml:
    group_id: "com.{base_package}"
    artifact_id: "{project_name}"
    dependencies: [...]
    plugins: [...]
```

### 6. Reglas de Validación
```yaml
validation_rules:
  - rule: "nombre_regla"
    description: "Descripción de la regla"
    severity: "error|warning"
```

## 🔧 Personalización para Otros Proyectos

### Paso 1: Copiar la Plantilla Base
```bash
cp project-template.yaml mi-plantilla.yaml
```

### Paso 2: Modificar Variables
Edita las variables en la sección `variables`:

```yaml
variables:
  - name: "base_package"
    default: "miempresa"
  - name: "project_name"
    default: "miproyecto"
```

### Paso 3: Ajustar Estructura
Modifica las secciones según tu proyecto:
- `directory_structure`: Agrega/elimina directorios
- `package_structure`: Modifica paquetes y subpaquetes
- `maven_config`: Ajusta dependencias y plugins
- `validation_rules`: Agrega reglas específicas

### Paso 4: Analizar Tu Proyecto
```bash
python structure-analyzer.py mi-plantilla.yaml /ruta/a/tu/proyecto
```

## 📊 Tipos de Validaciones

El sistema valida automáticamente:

### Estructurales
- ✅ Directorios requeridos existen
- ✅ Archivos de configuración presentes
- ✅ Estructura de paquetes correcta

### Configuración
- ✅ Configuración Maven válida
- ✅ Dependencias necesarias presentes
- ✅ Plugins de build configurados

### Calidad
- ✅ Herramientas de testing configuradas
- ✅ Coverage tools presentes
- ✅ Convenciones de código

### Arquitectura
- ✅ Separación de capas
- ✅ Patrones de diseño implementados
- ✅ Principios SOLID seguidos

## 💡 Propuestas de Corrección

El sistema genera propuestas automáticamente para:

### Directorios Faltantes
```bash
mkdir -p "src/main/java/com/miempresa/domain/entity"
mkdir -p "src/main/java/com/miempresa/domain/valueobject"
```

### Archivos Faltantes
Lista de archivos que deben crearse con su propósito.

### Paquetes Faltantes
Estructura de paquetes Java que deben crearse.

### Issues de Validación
Problemas detectados con sugerencias de corrección:
- ❌ **ERROR**: Issues críticos que deben corregirse
- ⚠️ **WARNING**: Issues recomendados pero no bloqueantes

## 🎯 Flujo de Trabajo Recomendado

### Para el Equipo de Desarrollo

1. **Definir Plantilla Base**
   - Crear `project-template.yaml` con la estructura estándar
   - Documentar convenciones y patrones

2. **Analizar Proyectos Existentes**
   ```bash
   python structure-analyzer.py project-template.yaml ./proyecto-a
   python structure-analyzer.py project-template.yaml ./proyecto-b
   ```

3. **Revisar Propuestas**
   - Analizar el reporte generado
   - Discutir cambios propuestos con el equipo

4. **Aplicar Correcciones**
   - Crear directorios faltantes
   - Agregar archivos de configuración
   - Ajustar estructura de paquetes

5. **Validar**
   - Ejecutar análisis nuevamente
   - Verificar que todas las validaciones pasen

### Para Nuevos Proyectos

1. **Crear desde Plantilla**
   - Usar la plantilla como guía
   - Crear estructura inicial

2. **Validar Progresivamente**
   - Ejecutar análisis durante desarrollo
   - Corregir desviaciones temprano

3. **Mantener Consistencia**
   - Actualizar plantilla cuando evolucione el estándar
   - Re-analizar proyectos existentes periódicamente

## 🔍 Ejemplo de Salida

```
📋 Cargando plantilla: project-template.yaml
🔍 Analizando proyecto: ./mi-proyecto
⚖️  Comparando estructuras...

============================================================
RESULTADO DEL ANÁLISIS
============================================================
❌ La estructura del proyecto NO coincide con la plantilla

📁 Directorios faltantes: 3
📄 Archivos faltantes: 1
📦 Paquetes faltantes: 2
⚠️  Issues de validación: 1

============================================================
PROPUESTAS DE CORRECCIÓN
============================================================

## Directorios Faltantes

Crear los siguientes directorios:

- `src/main/java/com/miempresa/domain/valueobject`
- `src/main/java/com/miempresa/application/port`
- `src/test/java/com/miempresa`

Comandos para crear:
```bash
mkdir -p "src/main/java/com/miempresa/domain/valueobject"
mkdir -p "src/main/java/com/miempresa/application/port"
mkdir -p "src/test/java/com/miempresa"
```

## Issues de Validación

❌ **test_coverage**: Coverage debe ser 100% en líneas y branches
   💡 Agregar JaCoCo plugin en pom.xml
```

## 🛠️ Requisitos del Sistema

### Python
- Python 3.7+
- PyYAML: `pip install pyyaml`

### Java
- Java 17+ (para los proyectos analizados)
- Maven (para proyectos Java)

## 📝 Extensiones Posibles

El sistema puede extenderse para:

1. **Corrección Automática**: Aplicar cambios automáticamente
2. **Múltiples Plantillas**: Soportar diferentes tipos de proyectos
3. **Integración CI/CD**: Integrar en pipelines de build
4. **Generación de Código**: Crear archivos boilerplate automáticamente
5. **Validación de Código**: Análisis estático adicional
6. **Reportes HTML**: Generar reportes visuales

## 🤝 Contribuciones

Para agregar nuevas reglas de validación:

1. Agregar la regla en `validation_rules` en el YAML
2. Implementar la lógica de validación en `StructureComparator._validate_rules()`
3. Definir propuestas de corrección en `ProposalGenerator`

## 📞 Soporte

Para problemas o preguntas:
- Revisar la documentación del proyecto original
- Validar que la plantilla YAML esté bien formada
- Verificar que el proyecto a analizar sea accesible

## 📄 Licencia

Este sistema es parte del proyecto NeonPulse Ticketera y sigue la misma licencia.