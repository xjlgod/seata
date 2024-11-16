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
package org.apache.seata.rm;

import org.apache.seata.core.model.BranchStatus;
import org.apache.seata.core.model.BranchType;
import org.apache.seata.core.model.ResourceManager;
import org.apache.seata.core.protocol.ResultCode;
import org.apache.seata.core.protocol.transaction.BranchDeleteRequest;
import org.apache.seata.core.protocol.transaction.BranchDeleteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type RM handler XA.
 *
 */
public class RMHandlerXA extends AbstractRMHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RMHandlerXA.class);

    @Override
    protected ResourceManager getResourceManager() {
        return DefaultResourceManager.get().getResourceManager(BranchType.XA);
    }

    @Override
    public BranchDeleteResponse handle(BranchDeleteRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start xa delete branch, xid:{}, branchId:{}", request.getXid(), request.getBranchId());
        }
        BranchDeleteResponse branchDeleteResponse = new BranchDeleteResponse();
        try {
            BranchStatus branchStatus = getResourceManager().branchRollback(request.getBranchType(), request.getXid(),
                    request.getBranchId(), request.getResourceId(), "");
            ResultCode code = branchStatus == BranchStatus.PhaseTwo_Rollbacked ? ResultCode.Success : ResultCode.Failed;
            branchDeleteResponse.setResultCode(code);
            branchDeleteResponse.setBranchStatus(branchStatus);
        } catch (Exception e) {
            branchDeleteResponse.setResultCode(ResultCode.Failed);
            LOGGER.error("XA branch delete fail, reason: {}", e.getMessage(), e);
        }
        branchDeleteResponse.setXid(request.getXid());
        branchDeleteResponse.setBranchId(request.getBranchId());
        return branchDeleteResponse;
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.XA;
    }

}
