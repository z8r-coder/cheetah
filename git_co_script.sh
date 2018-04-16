#!/usr/bin/env bash

rm -rf /Users/ruanxin/IdeaProjects/cheetah/script/raft/D\:\\rpc\\client\\log
rm -rf D\:\\rpc\\client\\log
git add .
git commit -m "$1"
git push origin master