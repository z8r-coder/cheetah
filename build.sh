#!/usr/bin/env bash

mvn clean compile package install
mvn dependency:copy-dependenciescheetah-admin-1.0-SNAPSHOT.jar