#!/usr/bin/env bash

osascript -e 'tell app "Terminal"
    do script "cd '/Users/ruanxin/IdeaProjects/cheetah/script/raft'; ./raft_start_client.sh"
    do script "cd '/Users/ruanxin/IdeaProjects/cheetah/script/raft'; ./raft_start_mutil_process_6060.sh"
    do script "cd '/Users/ruanxin/IdeaProjects/cheetah/script/raft'; ./raft_start_mutil_process_7070.sh"
    do script "cd '/Users/ruanxin/IdeaProjects/cheetah/script/raft'; ./raft_start_mutil_process_8080.sh"
end tell'