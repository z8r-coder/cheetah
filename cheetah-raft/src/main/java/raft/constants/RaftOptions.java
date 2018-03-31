package raft.constants;

/**
 * @author ruanxin
 * @create 2018-03-01
 * @desc
 */
public class RaftOptions {

    //election timer random(1500, 2000)
    private int electionTimeOutMilliSec = 1500;
    private int electionTimeOutRandomMilliSec = 500;
    //heart beat 500
    private int heartbeatPeriodMilliseconds = 500;
    //message to other node thread num
    private int raftConsensusThreadNum = 20;
    //get result by future obj(sec)
    private int raftFutureTimeOut = 5;
    //100M per log file
    private int maxLogSizePerFile = 1024 * 1024 * 100;

    public int getElectionTimeOutMilliSec() {
        return electionTimeOutMilliSec;
    }

    public void setElectionTimeOutMilliSec(int electionTimeOutMilliSec) {
        this.electionTimeOutMilliSec = electionTimeOutMilliSec;
    }

    public int getElectionTimeOutRandomMilliSec() {
        return electionTimeOutRandomMilliSec;
    }

    public void setElectionTimeOutRandomMilliSec(int electionTimeOutRandomMilliSec) {
        this.electionTimeOutRandomMilliSec = electionTimeOutRandomMilliSec;
    }

    public int getHeartbeatPeriodMilliseconds() {
        return heartbeatPeriodMilliseconds;
    }

    public void setHeartbeatPeriodMilliseconds(int heartbeatPeriodMilliseconds) {
        this.heartbeatPeriodMilliseconds = heartbeatPeriodMilliseconds;
    }

    public int getRaftFutureTimeOut() {
        return raftFutureTimeOut;
    }

    public void setRaftFutureTimeOut(int raftFutureTimeOut) {
        this.raftFutureTimeOut = raftFutureTimeOut;
    }

    public int getRaftConsensusThreadNum() {
        return raftConsensusThreadNum;
    }

    public void setRaftConsensusThreadNum(int raftConsensusThreadNum) {
        this.raftConsensusThreadNum = raftConsensusThreadNum;
    }
}
