# Quephaestus

[![Tests](https://github.com/FreifeldRoyi/Quephaestus/actions/workflows/build-and-test.yaml/badge.svg)](https://github.com/FreifeldRoyi/Quephaestus/actions/workflows/build-and-test.yaml)

## Overview
> **Forge your own path. Build smarter. Deliver faster.**

Quephaestus is a powerful yet flexible scaffolding tool designed to boost developer productivity. Unlike traditional
generators that enforce rigid structures, Quephaestus allows you to define **your own templates, folder structures, and
DSLs**. Whether you're creating a backend module, a set of configuration files, or any standardized project setup,
Quephaestus lets you tailor the process to fit **your** workflow.

## Why "Quephaestus"?

The name "Quephaestus" is a fusion of **"Qu"** (a nod to Quarkus, though the tool is not Quarkus-specific) and **"
Hephaestus"**, the Greek god of blacksmithing and craftsmanship. Just as Hephaestus forged legendary tools for the gods,
Quephaestus helps developers forge their own tools, modules, and project structures.

## Installation

**Installation is currently TBD.** For now, you can download the latest release from [GitHub Releases](#) (Mac, Linux,
Windows executables available).

## Quick start

Quephaestus provides two primary commands for generating files:

```sh
# Forge a Single File
quephaestus forge -f path/to/my/config.yaml -m my-module-name -w path/to/working-directory -b path/to/base-directory my-element-name

# Forge a Blueprint (Multiple Files)
quephaestus forge-blueprint -f path/to/my/config.yaml -m my-module-name -w path/to/working-directory -b path/to/base-directory my-blueprint-name

# -f --> Path to the configuration file  
# -m --> Module name (used as a contextual grouping for the generated files)  
# -w --> Working directory. The root directory where all operations are performed (typically the project's main directory)  
# -b --> Base directory. A subdirectory inside the working directory where the forged files should be placed  
# -d --> Inline json data For example: -d "{\"name\":"123", \"hello\": \"world\"}"
# -p --> Payload. A path to a json file containing mappings for your data   
# --pre-forge/--post-forge --> a script/command to execute before/after the forge completion
# <element-name>` or `<blueprint-name>` --> The specific element or blueprint to generate.  
```

Forged files will be placed under:

```
[working-directory]/[base-directory]/[module]/[element path]/[filename]
```

Where:

- `[Working directory]` - The root of your project
- `[Base directory]` - A common subdirectory for source files
- `[element path]` - is defined in the `path` field of the configuration file
- `[filename]` - follows the pattern in the `namePattern` field of the configuration file

> 📘 **Note**     
> If `-w` and `-b` are omitted, the current directory is used for both.

For example:  

In a java project named `awesome-project` all files are stored in a folder named `awesome-project`.  
The source files are, generally speaking, stored under `src/main/java/com/awesomeProject/`.
Good candidates for working and base directories are `-w awesome-project -b src/main/java/com/awesomeProject`

## Configuration

```yaml
# [Optional]
# General definitions of project name and namespace. 
# Both will be added automatically as interpolation data
project: "testproject"            # optional
namespace: "com.myproject"        # optional

# [Required]
# An absolute/relative path to the templates' directory.
# If relative, will be relative to the path of this configuration file
templatesFolder: "./templates"

# [Optional]
# Can be a command, or an absolute/relative path to script
# If relative, will be relative to the WORKING directory only
preForgeScript: "git commit"      
postForgeScript: "./build.sh"     

# [Required]
# Elements definition. Must define at least 1 element
# An element's name can be anything. Suggestion: make it part of the DSL you use on a daily basis
# path [Mandatory] - is the relative path in the project's folder, in which this element should be created
#        NOTE 1: It is relative to the working + base dir + module name (`-m`) --> workingDir/baseDir/moduleName/path
#        NOTE 2: You can use interpolation slots
# namePattern [Mandatory] - filename to use
#        NOTE 1: You can use interpolation slots
elements:
  resource:
    path: "boundary/api/{version}"
    namePattern: "{name.toPascalCase}Resource.go"
  orchestrator:
    path: "control"
    namePattern: "{name.toPascalCase}Orchestrator.java"

# [Optional] (unless you are using blueprints)
# Blueprints definition. Must define at least 1 element if defined
# A blueprint name can be anything. Suggestion: make it part of the DSL you use on a daily basis
# elements [Mandatory] - A list of elements that this blueprint will forge. 
#        NOTE 1: Each element, must be defined in the `elements` section
# mappings [Optional] - Predefined mappings
#        NOTE 1: You can use interpolation slots
blueprints:
  full-module:
    elements: [ resource, orchestrator ]
    mappings:
      name: "{module}"
      version: "v1"
```
## Templates (`.qphs` Files)

Templates are stored in the folder specified by `templatesFolder` and should match element names. For example, if
`resource` is an element, its corresponding template file should be `resource.qphs`.

Quephaestus templates follow the **Qute** templating engine syntax. For more details, check out [Qute documentation](https://quarkus.io/guides/qute).  

> 📘 **Note**     
> There are several capabilities that are not supported at the moment:
> * namespaced expressions
> * Objects in expressions (e.g `{item}` or `{item.name}` where `item` is an object)

## Glossary

- **Element** → A reusable components in your DSL, used in your project. Example: `controller`, `repository`, or
  `orchestrator`
- **Template** → A `.qphs` file that serves as a skeleton for a generated file. It can include Qute expressions and
  placeholders
- **Module** → A contextual grouping for generated files. Can represent a domain (DDD) or a related set of components
- **Blueprint** → A combination of multiple elements, allowing multiple files to be forged together
- **Working Directory** → The root directory where all operations are performed (usually the main project directory)
- **Base Directory** → A subdirectory inside the working directory where forged files should be placed
- **Mapping**: Static values to fill in template placeholders

## Contribution

We welcome contributions! Here’s how you can help:

- **Submit Issues**: Found a bug? Have an
  idea? [Open an issue](https://github.com/FreifeldRoyi/Quephaestus/issues/new/choose)!
- **Feature Requests**: Have an idea for a new subcommand? Let’s discuss!

## Known issues

* Due to a [bug in picocli lib](https://github.com/remkop/picocli/issues/2309), the `-d` & `-p` options, appear twice in
  the usage printout
* Several qute capabilities are not fully supported. See [templates](#templates-qphs-files)