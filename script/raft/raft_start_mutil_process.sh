#!/usr/bin/env bash
java -cp ../../cheetah-demo/target/dependency/*:cheetah-demo-1.0-SNAPSHOT.jar raft.RaftServerMutilProcessStartDemo "127.0.0.1:6060"
java -cp ../../cheetah-demo/target/dependency/*:cheetah-demo-1.0-SNAPSHOT.jar raft.RaftServerMutilProcessStartDemo "127.0.0.1:7070"
java -cp ../../cheetah-demo/target/dependency/*:cheetah-demo-1.0-SNAPSHOT.jar raft.RaftServerMutilProcessStartDemo "127.0.0.1:8080"