# TurboTax Microservices - Multi-Root Workspace Setup

This VS Code workspace is configured as a **multi-root workspace** to provide independent development environments for each microservice module.

## 🏗️ Workspace Structure

The workspace includes the following modules:

- **Root** - Main project directory with shared configurations
- **TurboTax Filing Metadata Service** - Java/Spring Boot service
- **TurboTax Refund Status Service** - Java/Spring Boot service
- **TurboTax Agent Service** - Python/FastAPI service

## 🚀 Getting Started

1. **Open the Workspace:**
   ```bash
   code turbotax-microservices.code-workspace
   ```

2. **Each module has its own `.vscode` folder** with:
   - Independent settings
   - Module-specific tasks
   - Language-specific configurations

## 📁 Module-Specific Configurations

### Python Module (TurboTax Agent Service)
- **Virtual Environment:** `./venv/`
- **Python Path:** Configured to use virtual environment
- **Tasks:** Create venv, install deps, run tests, format code
- **Settings:** Python linting, formatting, testing configured

### Java Modules (Filing Metadata & Refund Status Services)
- **Java Settings:** Automatic build configuration updates
- **Gradle Integration:** Wrapper checksums enabled
- **Tasks:** Build, test, run, clean operations
- **Formatting:** Java formatting with custom profile

## 🛠️ Available Tasks

### Global Tasks (Root Level)
- `Build All Services` - Builds entire project
- `Test All Services` - Runs all tests

### Module-Specific Tasks
Each module has its own tasks accessible when that module is active:

**Python Module:**
- Create Virtual Environment
- Install Python Dependencies
- Run Python Tests
- Run Agent Service
- Format Python Code
- Sort Python Imports

**Java Modules:**
- Build [Service Name]
- Test [Service Name]
- Run [Service Name]
- Clean Build

## 🔧 Development Workflow

1. **Switch between modules** using the workspace explorer
2. **Each module maintains its own settings** - changes in one don't affect others
3. **Tasks are context-aware** - run tasks specific to the active module
4. **Debugging configurations** are isolated per module

## 📋 Recommended Extensions

The workspace recommends these extensions:
- **Java Pack** - For Java development
- **Python** - For Python development
- **Gradle** - For Gradle integration
- **Thunder Client** - For API testing
- **REST Client** - Alternative API testing

## 🎯 Benefits

- **Isolated Environments:** Each module has its own settings and configurations
- **Language-Specific Setup:** Python and Java modules configured appropriately
- **Efficient Development:** Quick access to module-specific tasks
- **Clean Separation:** No cross-contamination between module configurations

## 🔄 Switching Between Modules

Use the **workspace explorer** to switch between modules. Each module will:
- Load its own `.vscode` settings
- Show its specific tasks
- Use its configured environment

This setup provides the best of both worlds - unified project management with isolated development environments! 🎉