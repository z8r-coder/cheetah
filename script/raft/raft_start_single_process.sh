#!/usr/bin/env bash
java -cp ../../cheetah-demo/target/*:cheetah-demo-1.0-SNAPSHOT.jar raft.RaftServerSingleNodeDemo "127.0.0.1:7070,127.0.0.1:8080,127.0.0.1:6060"