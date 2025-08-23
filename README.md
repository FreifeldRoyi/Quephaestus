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

## Quick start

Quephaestus provides two primary commands for generating files:

```sh
# Forge a Single File
quephaestus forge -f path/to/my/config.yaml -m my-module-name -w path/to/working-directory -b path/to/base-directory my-element-name

# Forge an Element group (Multiple Files)
quephaestus forge-element-group -f path/to/my/config.yaml -m my-module-name -w path/to/working-directory -b path/to/base-directory my-group-name

# -f --> Path to the configuration file  
# -m --> Module name (used as a contextual grouping for the generated files)  
# -w --> Working directory. The root directory where all operations are performed (typically the project's main directory)  
# -b --> Base directory. A subdirectory inside the working directory where the forged files should be placed  
# -d --> Inline json data For example: -d "{\"name\":"123", \"hello\": \"world\"}"
# -p --> Payload. A path to a json file containing mappings for your data
# -i --> Interactive. Will ask for any missing data.   
# --pre-forge/--post-forge --> a script/command to execute before/after the forge completion
# <element-name>` or `<group-name>` --> The specific element or group to generate.  
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

# [Optional] (unless you are using an element group)
# Element groups definition. Must define at least 1 element if defined
# A group name can be anything. Suggestion: make it part of the DSL you use on a daily basis
# elements [Mandatory] - A list of elements that this group will forge. 
#        NOTE 1: Each element, must be defined in the `elements` section
# mappings [Optional] - Predefined mappings
#        NOTE 1: You can use interpolation slots
elementGroups:
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

## Installation

### Build prerequisites 

To build locally you would need to install GraalVM and native image locally.
I suggest you use `sdkman` for this task. See this [guide](https://www.graalvm.org/latest/getting-started/#installing) for OS specific details.
Make sure that you are actually running GraalVM and not another JVM.
`mvn --version` should help figuring it out.  

### Building a native executable locally

Once everything is installed and setup, run the following command to compile everything and create your executable:

```shell
# The `clean` step is optional as well as -DskipTests
./mvnw clean package -DskipTests -Pnative
```

This will generate an executable with `-runner` suffix in the `target` folder, that looks somewhat like this 
`quephaestus-{version}-{arch}-runner` (On windows it will be an `exe` file).  
You can copy it to `/usr/local/bin` to make it globally available.

### Building an uber jar locally

If you are a Java savvy, and know your way around maven and jars, you can run the following command to compile everything into a single jar.  
This process is substantially faster than building the native image.

```shell
# The `clean` step is optional as well as -DskipTests
./mvnw clean package -DskipTests -Dquarkus.package.jar.type=uber-jar
```

This will generate an uber-jar with `-runner` suffix in the `target` folder, that looks somewhat like this
`quephaestus-{version}-{arch}-runner`.
You can copy to your desired location

### Formal release [TBD]

At the moment, I generate the executables in a [github pipeline](https://github.com/FreifeldRoyi/Quephaestus/actions/workflows/generate-executables.yaml) every once in a while.  
For simplicity reasons, the executable is uploaded as an artifact and not to the release page, and without a proper version.  In future releases it will be fixed.  

## Glossary

- **Element** → A reusable components in your DSL, used in your project. Example: `controller`, `repository`, or
  `orchestrator`
- **Template** → A `.qphs` file that serves as a skeleton for a generated file. It can include Qute expressions and
  placeholders
- **Module** → A contextual grouping for generated files. Can represent a domain (DDD) or a related set of components
- **Element group** → A collection of elements, allowing multiple files to be forged together
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