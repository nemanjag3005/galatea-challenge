# Developer Setup

## Overview

Setup steps for a new development environment:

1. Clone repository
1. [Install tools](#install-tools)
1. [Setup an empty local database](#setup-an-empty-local-database)
1. [Setup Maven runtime settings for local environment](#setup-maven-local-environment-settings)
1. [First Time Setup](#first-time-setup)
1. [Run the App](#running-the-project)

## Install Tools

- Postgresql 14
- JDK 17
- IntelliJ IDE
- SSH
- Vaadin 23
- pgAdmin or some other tool to visualize the database (optional)

## Setup an Empty Local Database

Start a local Postgresql database server and create an empty database such as `demo`.

## Configure Properties for Local Environment

The app uses certain runtime properties, some of which are dependent on the local environment. Of chief concern is the
database to connect to, particularly the password. App properties are loaded in the following order, with the
last loaded value taking precedence:

1. `properties/app.properties`
   - Used to set default properties common to most environments
2. `properties/app-local.properties`
   - Used to set properties for local development environments
   - File is optional - loaded if it exists
   - Local settings file is ignored by version control with `.gitignore`
   - Use `properties/template.app-local.properties` as a base

New developers will want to set up option 2.

## First Time Setup

- Maven -> demo -> Lifecycle -> clean  (`mvn clean`)
- Maven -> demo -> data -> Lifecycle -> package  (`mvn package`)
- Edit configurations for demo-data (or DataApp) and add `initOrMigrateAndGenerateJooq --profile=local-defaults` as an argument
- Run `demo-data` configuration
   - this generates the database source code for the webapp
- Reload all maven projects
   - This will set `webapp/target/generated-sources/jooq` as a source folder
  ![](ReloadMavenProjects.png?raw=true)
- Maven -> demo -> webapp -> Lifecycle -> package  (`mvn package`) 
---

## Running the Project

The project uses an embedded Jetty server.

1. Run the provided `demo-webapp` configuration in intellij
   - otherwise, run `data/src/main/java/DataApp` anytime there is a structural change in the database
   - then run `webapp/src/main/java/App` to run the webapp
2. Wait for the application to start
3. Open http://localhost:8080/ to view the application

First run can take up to about 10 minutes to build the frontend

Default login credentials can be found
in: [`src/main/java/com/quickstep/misc/DbBootStrapper.java`](quickstep/webapp/src/main/java/com/quickstep/misc/DbBootStrapper.java)

### Select Profile (optional)

To select a profile the app takes command line flags formatted as `--profile=profile_name`. The current profiles
are `production`, `demo`, and `staging`.
<br/>In intelliJ these can be set in `Run` > `Edit Configurations` then setting the `Program arguments` field.

---
## Vaadin Business App Starter

This project is based on the Vaadin [Business App Starter](https://vaadin.com/docs/business-app/overview.html).
