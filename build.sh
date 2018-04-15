#!/usr/bin/env bash

mvn clean compile package -Dlib=dependency install
mvn dependency:copy-dependencies