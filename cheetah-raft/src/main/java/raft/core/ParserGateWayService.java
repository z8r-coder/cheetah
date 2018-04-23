package raft.core;

import raft.protocol.request.CommandParseRequest;
import raft.protocol.response.CommandParseResponse;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc parse command
 */
public interface ParserGateWayService {

    /**
     * parse command
     */
    public CommandParseResponse parse(CommandParseRequest request);
}
