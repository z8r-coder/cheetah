#!/usr/bin/env bash
java -cp ../../cheetah-demo/target/dependency/*:cheetah-demo-1.0-SNAPSHOT.jar raft.RaftServerSingleNodeStartDemo "127.0.0.1:7070,127.0.0.1:8080,127.0.0.1:6060"