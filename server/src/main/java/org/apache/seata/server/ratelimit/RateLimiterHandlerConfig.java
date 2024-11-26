package org.apache.seata.server.ratelimit;

/**
 * RateLimiterHandlerConfig
 */
public class RateLimiterHandlerConfig {
    /**
     * whether enable server rate limit
     */
    private boolean enable;

    /**
     * limit token number of bucket per second
     */
    private int bucketTokenNumPerSecond;

    /**
     * limit token max number of bucket
     */
    private int bucketTokenMaxNum;

    /**
     * limit token initial number of bucket
     */
    private int bucketTokenInitialNum;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getBucketTokenNumPerSecond() {
        return bucketTokenNumPerSecond;
    }

    public void setBucketTokenNumPerSecond(int bucketTokenNumPerSecond) {
        this.bucketTokenNumPerSecond = bucketTokenNumPerSecond;
    }

    public int getBucketTokenMaxNum() {
        return bucketTokenMaxNum;
    }

    public void setBucketTokenMaxNum(int bucketTokenMaxNum) {
        this.bucketTokenMaxNum = bucketTokenMaxNum;
    }

    public int getBucketTokenInitialNum() {
        return bucketTokenInitialNum;
    }

    public void setBucketTokenInitialNum(int bucketTokenInitialNum) {
        this.bucketTokenInitialNum = bucketTokenInitialNum;
    }
}
