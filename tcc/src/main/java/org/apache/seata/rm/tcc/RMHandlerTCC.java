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
package org.apache.seata.rm.tcc;

import org.apache.seata.core.model.BranchStatus;
import org.apache.seata.core.model.BranchType;
import org.apache.seata.core.model.ResourceManager;
import org.apache.seata.core.protocol.ResultCode;
import org.apache.seata.core.protocol.transaction.BranchDeleteRequest;
import org.apache.seata.core.protocol.transaction.BranchDeleteResponse;
import org.apache.seata.core.protocol.transaction.UndoLogDeleteRequest;
import org.apache.seata.integration.tx.api.fence.DefaultCommonFenceHandler;
import org.apache.seata.rm.AbstractRMHandler;
import org.apache.seata.rm.DefaultResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Rm handler tcc.
 *
 */
public class RMHandlerTCC extends AbstractRMHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RMHandlerTCC.class);

    @Override
    public void handle(UndoLogDeleteRequest request) {
        //DO nothing
    }

    @Override
    public BranchDeleteResponse handle(BranchDeleteRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start tcc delete branch fence, xid:{}, branchId:{}",
                    request.getXid(), request.getBranchId());
        }
        BranchDeleteResponse branchDeleteResponse = new BranchDeleteResponse();
        try {
            boolean result = DefaultCommonFenceHandler.get().
                    deleteFenceByXidAndBranchId(request.getXid(), request.getBranchId());
            ResultCode code = result ? ResultCode.Success : ResultCode.Failed;
            branchDeleteResponse.setResultCode(code);
        } catch (Exception e) {
            LOGGER.error("Delete tcc fence fail, xid:{}, branchId:{}", request.getXid(), request.getBranchId(), e);
            branchDeleteResponse.setResultCode(ResultCode.Failed);
        }
        branchDeleteResponse.setXid(request.getXid());
        branchDeleteResponse.setBranchId(request.getBranchId());
        // this branch status is no importance
        branchDeleteResponse.setBranchStatus(BranchStatus.Unknown);
        return branchDeleteResponse;
    }

    @Override
    protected ResourceManager getResourceManager() {
        return DefaultResourceManager.get().getResourceManager(BranchType.TCC);
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.TCC;
    }

}
