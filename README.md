# Quephaestus

> **Forge your own path. Build smarter. Deliver faster.**

## Overview

Quephaestus is a powerful yet flexible scaffolding tool designed to boost developer productivity. Unlike traditional generators that enforce rigid structures, Quephaestus allows you to define **your own templates, folder structures, and DSLs**. Whether you're creating a backend module, a set of configuration files, or any standardized project setup, Quephaestus lets you tailor the process to fit **your** workflow.

## Why "Quephaestus"?

The name "Quephaestus" is a fusion of **"Qu"** (a nod to Quarkus, though the tool is not Quarkus-specific) and **"Hephaestus"**, the Greek god of blacksmithing and craftsmanship. Just as Hephaestus forged legendary tools for the gods, Quephaestus helps developers forge their own tools, modules, and project structures.

## Installation

**Installation is currently TBD.** For now, you can download the latest release from [GitHub Releases](#) (Mac, Linux, Windows executables available).

## Usage

Quephaestus provides two primary commands for generating files:

### **Forge a Single File**

```sh
quephaestus forge -f path/to/my/config.yaml -m my-module-name -w path/to/working-directory -b path/to/base-directory my-element-name
```

### **Forge a Full Blueprint (Multiple Files)**

```sh
quephaestus forge-blueprint -f path/to/my/config.yaml -m my-module-name -w path/to/working-directory -b path/to/base-directory my-blueprint-name
```

### **Command Breakdown**

- `-f` → Path to the configuration file.
- `-m` → Module name (used as a contextual grouping for the generated files).
- `-w` → **Working directory**: The root directory where all operations are performed (typically the project's main directory).
- `-b` → **Base directory**: A subdirectory inside the working directory where the forged files should be placed.
- `<element-name>` or `<blueprint-name>` → The specific element or blueprint to generate.

### **Example Directory Structure**

For a Java project:

- **Working directory**: The root of your project (e.g., where `pom.xml` or `build.gradle` resides).
- **Base directory**: A common subdirectory for source files (e.g., `src/main/java`).

Generated files will be placed under:

```
[working-directory]/[base-directory]/[module]/[element path]/[filename]
```

Where:

- `[element path]` is defined in the `path` field of the configuration file.
- `[filename]` follows the pattern in the `namePattern` field of the configuration file.

If `-w` and `-b` are omitted, the current directory is used for both.

## Configuration

Quephaestus relies on a YAML-based configuration file to define **templates and blueprints**.

Example:

```yaml
project: "testproject"
namespace: "com.myproject"
templatesFolder: "./templates"
elements:
  resource:
    path: "boundary/api/{version}"
    namePattern: "{name.toPascalCase}Resource.java"
  orchestrator:
    path: "control"
    namePattern: "{name.toPascalCase}Orchestrator.java"
blueprints:
  full-module:
    elements: [resource, orchestrator]
    mappings:
      name: "{module}"
      version: "v1"
```

### **How It Works**

- **elements**: Define reusable components in your DSL.
- **blueprints**: Combine multiple elements to generate entire structures.
- **mappings**: Static values to fill in template placeholders.
- **Predefined interpolation points**:
    - `{project}` → Defined by `project` in config.
    - `{module}` → Provided via `-m` in CLI.
    - `{namespace}` → Defined by `namespace` in config.

## Templates (`.qphs` Files)

Templates are stored in the folder specified by `templatesFolder` and should match element names. For example, if `resource` is an element, its corresponding template file should be `resource.qphs`.

Quephaestus templates follow the **Qute** templating engine syntax. For more details, check out the [Qute documentation](https://quarkus.io/guides/qute).

## Glossary

- **Element** → A DSL component used in your project. Example: A backend developer might define `controller`, `repository`, or `orchestrator` as elements.
- **Template** → A `.qphs` file that serves as a skeleton for a generated file. It can include Qute expressions and placeholders.
- **Module** → A contextual grouping for generated files. Can represent a domain (DDD) or a related set of components.
- **Blueprint** → A combination of multiple elements, allowing multiple files to be forged together.
- **Working Directory** → The root directory where all operations are performed (usually the main project directory).
- **Base Directory** → A subdirectory inside the working directory where forged files should be placed (e.g., `src/main/java` in a Java project).

## Contribution

We welcome contributions! Here’s how you can help:

- **Submit Issues**: Found a bug? Have an idea? [Open an issue](#)!
- **Feature Requests**: Have an idea for a new subcommand? Let’s discuss!

## Future Enhancements

- **Logging & Debugging** (`--verbose`, `--debug`)
- **Additional Subcommands** for more automation
- **Template Sharing** via a public repository


##  Known issues
* Due to a bug with picocli, the `-d` & `-p` options, appear twice in the usage printout - https://github.com/remkop/picocli/issues/2309