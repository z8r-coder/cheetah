#!/usr/bin/env bash

mvn clean compile package install
mvn dependency:copy-dependencies