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
package org.apache.seata.server.limit;

import org.apache.seata.core.protocol.transaction.AbstractTransactionRequestToTC;
import org.apache.seata.core.protocol.transaction.AbstractTransactionResponse;
import org.apache.seata.core.rpc.RpcContext;

/**
 * TransactionRequestLimitHandler
 */
public abstract class TransactionRequestLimitHandler {

    /**
     * limit handler
     */
    protected TransactionRequestLimitHandler transactionRequestLimitHandler;

    public TransactionRequestLimitHandler() {
    }

    /**
     * next handler handle
     * @param context
     * @return
     */
    protected AbstractTransactionResponse next(AbstractTransactionRequestToTC originRequest, RpcContext context) {
        if (transactionRequestLimitHandler != null) {
            return transactionRequestLimitHandler.next(originRequest, context);
        }
        return originRequest.handle(context);
    }

    public abstract AbstractTransactionResponse handle(AbstractTransactionRequestToTC originRequest, RpcContext context);

    public void setTransactionRequestLimitHandler(TransactionRequestLimitHandler transactionRequestLimitHandler) {
        this.transactionRequestLimitHandler = transactionRequestLimitHandler;
    }
}
