---
name: "myapp-cli-guide"
displayName: "MyApp CLI Guide"
description: "Complete guide for using myapp-cli to build, test, and deploy microservices with best practices and troubleshooting."
keywords: ["myapp", "cli", "deployment", "microservices", "devops"]
author: "Your Team"
---

# MyApp CLI Guide

## Overview

MyApp CLI is an internal deployment tool designed for managing microservices lifecycle. It provides a unified interface for building, testing, and deploying applications across different environments. This guide covers installation, common workflows, and troubleshooting to help you get productive quickly.

## Onboarding

### Installation

#### Via npm
```bash
npm install -g @yourcompany/myapp-cli
```

### Prerequisites
- Node.js 16+ 
- npm or yarn package manager
- Access to company deployment infrastructure

### Basic Configuration
```bash
# Configure your credentials
myapp-cli config set --api-key YOUR_API_KEY

# Verify installation
myapp-cli --version
```

### Verification
```bash
# Check if CLI is properly installed
myapp-cli status

# Expected output:
# ✓ CLI version: 2.1.0
# ✓ Configuration: Valid
# ✓ API connection: Connected
```

## Common Workflows

### Workflow: Build Application

**Goal:** Compile and package your application for deployment

**Commands:**
```bash
# Navigate to your project
cd /path/to/your/project

# Build the application
myapp-cli build --env production

# Verify build artifacts
myapp-cli build --verify
```

**Explanation:**
- `--env`: Specifies the target environment (development, staging, production)
- `--verify`: Validates build artifacts after compilation

**Complete Example:**
```bash
# Full build workflow
cd my-microservice
myapp-cli build --env production --optimize
myapp-cli build --verify
```

### Workflow: Test Application

**Goal:** Run automated tests before deployment

**Commands:**
```bash
# Run all tests
myapp-cli test

# Run specific test suite
myapp-cli test --suite integration

# Run tests with coverage
myapp-cli test --coverage
```

**Complete Example:**
```bash
# Complete testing workflow
myapp-cli test --suite unit
myapp-cli test --suite integration
myapp-cli test --coverage --report
```

### Workflow: Deploy Application

**Goal:** Deploy your application to target environment

**Commands:**
```bash
# Deploy to staging
myapp-cli deploy --env staging

# Deploy to production (requires approval)
myapp-cli deploy --env production --confirm

# Check deployment status
myapp-cli deploy --status
```

**Complete Example:**
```bash
# Full deployment workflow
myapp-cli build --env production
myapp-cli test --suite integration
myapp-cli deploy --env staging
# After verification in staging:
myapp-cli deploy --env production --confirm
```

## Command Reference

### myapp-cli build

**Purpose:** Build and package application

**Syntax:**
```bash
myapp-cli build [options]
```

**Common Options:**
| Flag | Description | Example |
|------|-------------|---------|
| `--env <environment>` | Target environment | `--env production` |
| `--optimize` | Enable optimizations | `--optimize` |
| `--verify` | Verify build artifacts | `--verify` |

### myapp-cli test

**Purpose:** Run automated tests

**Syntax:**
```bash
myapp-cli test [options]
```

**Common Options:**
| Flag | Description | Example |
|------|-------------|---------|
| `--suite <name>` | Run specific test suite | `--suite integration` |
| `--coverage` | Generate coverage report | `--coverage` |
| `--watch` | Watch mode for development | `--watch` |

### myapp-cli deploy

**Purpose:** Deploy application to environment

**Syntax:**
```bash
myapp-cli deploy [options]
```

**Common Options:**
| Flag | Description | Example |
|------|-------------|---------|
| `--env <environment>` | Target environment | `--env production` |
| `--confirm` | Skip confirmation prompt | `--confirm` |
| `--status` | Check deployment status | `--status` |

## Troubleshooting

### Error: "Command not found: myapp-cli"
**Cause:** CLI not installed or not in PATH
**Solution:**
1. Verify installation: `npm list -g @yourcompany/myapp-cli`
2. Reinstall if needed: `npm install -g @yourcompany/myapp-cli`
3. Restart your terminal
4. Check PATH includes npm global bin directory

### Error: "Authentication failed"
**Cause:** Invalid or missing API credentials
**Solution:**
1. Check your API key: `myapp-cli config get api-key`
2. Update credentials: `myapp-cli config set --api-key YOUR_NEW_KEY`
3. Verify connection: `myapp-cli status`
4. Contact admin if key is invalid

### Error: "Build failed: Missing dependencies"
**Cause:** Required dependencies not installed
**Solution:**
1. Install project dependencies: `npm install`
2. Verify package.json is present
3. Check for platform-specific dependencies
4. Try cleaning build cache: `myapp-cli build --clean`

### Installation Issues

**Problem:** CLI installation fails with permission errors
**Cause:** Insufficient permissions for global npm install
**Solution:**
1. Use sudo (macOS/Linux): `sudo npm install -g @yourcompany/myapp-cli`
2. Or configure npm to use user directory:
   ```bash
   mkdir ~/.npm-global
   npm config set prefix '~/.npm-global'
   export PATH=~/.npm-global/bin:$PATH
   ```
3. Then retry installation without sudo

## Best Practices

- Always run tests before deploying to production
- Use `--verify` flag after builds to catch issues early
- Deploy to staging environment first for validation
- Keep your CLI updated: `npm update -g @yourcompany/myapp-cli`
- Use environment-specific configuration files
- Review deployment logs after each deployment
- Set up CI/CD pipelines for automated deployments

## Additional Resources

- Internal Documentation: https://docs.yourcompany.com/myapp-cli
- Support Channel: #devops-support on Slack
- Issue Tracker: https://github.com/yourcompany/myapp-cli/issues

---

**CLI Tool:** `myapp-cli`
**Installation:** `npm install -g @yourcompany/myapp-cli`
