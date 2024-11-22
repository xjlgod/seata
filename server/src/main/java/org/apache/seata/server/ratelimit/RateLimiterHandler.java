/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.seata.server.ratelimit;

import org.apache.seata.common.XID;
import org.apache.seata.common.loader.EnhancedServiceLoader;
import org.apache.seata.common.util.NumberUtils;
import org.apache.seata.config.CachedConfigurationChangeListener;
import org.apache.seata.config.Configuration;
import org.apache.seata.config.ConfigurationChangeEvent;
import org.apache.seata.config.ConfigurationFactory;
import org.apache.seata.common.ConfigurationKeys;
import org.apache.seata.core.exception.TransactionExceptionCode;
import org.apache.seata.core.protocol.AbstractMessage;
import org.apache.seata.core.protocol.AbstractResultMessage;
import org.apache.seata.core.protocol.ResultCode;
import org.apache.seata.core.protocol.transaction.GlobalBeginRequest;
import org.apache.seata.core.protocol.transaction.GlobalBeginResponse;
import org.apache.seata.core.rpc.RpcContext;
import org.apache.seata.server.metrics.MetricsPublisher;

/**
 * RateLimiterHandler
 */
public class RateLimiterHandler {

    private static volatile RateLimiterHandler instance;

    private final RateLimiter rateLimiter;

    private static final RateLimiterHandlerConfig LISTENER = new RateLimiterHandlerConfig();
    private static final Configuration CONFIG = ConfigurationFactory.getInstance();

    static {
        CONFIG.addConfigListener(ConfigurationKeys.RATE_LIMIT_PREFIX, LISTENER);
        CONFIG.addConfigListener(ConfigurationKeys.RATE_LIMIT_BUCKET_TOKEN_NUM_PER_SECOND, LISTENER);
        CONFIG.addConfigListener(ConfigurationKeys.RATE_LIMIT_BUCKET_TOKEN_MAX_NUM, LISTENER);
        CONFIG.addConfigListener(ConfigurationKeys.RATE_LIMIT_BUCKET_TOKEN_INITIAL_NUM, LISTENER);
    }

    public RateLimiterHandler(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    private RateLimiterHandler() {
        rateLimiter = EnhancedServiceLoader.load(RateLimiter.class);
    }

    public static RateLimiterHandler getInstance() {
        if (instance == null) {
            synchronized (RateLimiterHandler.class) {
                if (instance == null) {
                    instance = new RateLimiterHandler();
                }
            }
        }
        return instance;
    }

    public AbstractResultMessage handle(AbstractMessage request, RpcContext rpcContext) {
        if (!LISTENER.isEnable()) {
            return null;
        }

        if (request instanceof GlobalBeginRequest) {
            if (!rateLimiter.canPass()) {
                GlobalBeginResponse response = new GlobalBeginResponse();
                response.setTransactionExceptionCode(TransactionExceptionCode.BeginFailedRateLimited);
                response.setResultCode(ResultCode.RateLimited);
                RateLimitInfo rateLimitInfo = RateLimitInfo.generateRateLimitInfo(rpcContext.getApplicationId(),
                        RateLimitInfo.GLOBAL_BEGIN_FAILED, rpcContext.getClientId(), XID.getIpAddressAndPort());
                MetricsPublisher.postRateLimitEvent(rateLimitInfo);
                response.setMsg(String.format("TransactionException[rate limit exception, rate limit info: %s]", rateLimitInfo));
                return response;
            }
        }
        return null;
    }

    /**
     * RateLimiterHandlerConfig
     */
     static class RateLimiterHandlerConfig implements CachedConfigurationChangeListener {
        /**
         * whether enable server rate limit
         */
        private volatile boolean enable;

        /**
         * limit token number of bucket per second
         */
        private volatile int bucketTokenNumPerSecond;

        /**
         * limit token max number of bucket
         */
        private volatile int bucketTokenMaxNum;

        /**
         * limit token initial number of bucket
         */
        private volatile int bucketTokenInitialNum;

        private final int DEFAULT_BUCKET_TOKEN_NUM_PER_SECOND = Integer.MAX_VALUE;
        private final int DEFAULT_BUCKET_TOKEN_MAX_NUM = Integer.MAX_VALUE;
        private final int DEFAULT_BUCKET_TOKEN_INITIAL_NUM = Integer.MAX_VALUE;

        private static final Configuration CONFIG = ConfigurationFactory.getInstance();

        public RateLimiterHandlerConfig() {
            enable = CONFIG.getBoolean(ConfigurationKeys.RATE_LIMIT_ENABLE);
            bucketTokenNumPerSecond = CONFIG.getInt(ConfigurationKeys.RATE_LIMIT_BUCKET_TOKEN_NUM_PER_SECOND);
            bucketTokenMaxNum = CONFIG.getInt(ConfigurationKeys.RATE_LIMIT_BUCKET_TOKEN_MAX_NUM);
            bucketTokenInitialNum = CONFIG.getInt(ConfigurationKeys.RATE_LIMIT_BUCKET_TOKEN_INITIAL_NUM);
        }

        @Override
        public void onChangeEvent(ConfigurationChangeEvent event) {
            String dataId = event.getDataId();
            String newValue = event.getNewValue();
            if (ConfigurationKeys.RATE_LIMIT_ENABLE.equals(dataId)) {
                enable = Boolean.parseBoolean(newValue);
            }
            if (ConfigurationKeys.RATE_LIMIT_BUCKET_TOKEN_NUM_PER_SECOND.equals(dataId)) {
                bucketTokenNumPerSecond = NumberUtils.toInt(newValue, DEFAULT_BUCKET_TOKEN_NUM_PER_SECOND);
            }
            if (ConfigurationKeys.RATE_LIMIT_BUCKET_TOKEN_MAX_NUM.equals(dataId)) {
                bucketTokenMaxNum = NumberUtils.toInt(newValue, DEFAULT_BUCKET_TOKEN_MAX_NUM);
            }
            if (ConfigurationKeys.RATE_LIMIT_BUCKET_TOKEN_INITIAL_NUM.equals(dataId)) {
                bucketTokenInitialNum = NumberUtils.toInt(newValue, DEFAULT_BUCKET_TOKEN_INITIAL_NUM);
            }
        }

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
}
