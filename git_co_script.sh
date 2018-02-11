#!/usr/bin/env bash

rm -rf D\:\\rpc\\client\\log
git add .
git commit -m "$1"
git push origin master