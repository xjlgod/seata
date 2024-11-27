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
package org.apache.seata.serializer.protobuf.convertor;

import org.apache.seata.core.model.BranchType;
import org.apache.seata.core.protocol.transaction.BranchDeleteRequest;
import org.apache.seata.serializer.protobuf.generated.AbstractMessageProto;
import org.apache.seata.serializer.protobuf.generated.AbstractTransactionRequestProto;
import org.apache.seata.serializer.protobuf.generated.BranchDeleteRequestProto;
import org.apache.seata.serializer.protobuf.generated.BranchTypeProto;
import org.apache.seata.serializer.protobuf.generated.MessageTypeProto;

/**
 * BranchDeleteRequestConvertor
 */
public class BranchDeleteRequestConvertor implements PbConvertor<BranchDeleteRequest, BranchDeleteRequestProto> {
    @Override
    public BranchDeleteRequestProto convert2Proto(BranchDeleteRequest branchDeleteRequest) {
        final short typeCode = branchDeleteRequest.getTypeCode();

        final AbstractMessageProto abstractMessage = AbstractMessageProto.newBuilder().setMessageType(
                MessageTypeProto.forNumber(typeCode)).build();

        final AbstractTransactionRequestProto abstractTransactionRequestProto = AbstractTransactionRequestProto
                .newBuilder().setAbstractMessage(abstractMessage).build();

        final String resourceId = branchDeleteRequest.getResourceId();
        return BranchDeleteRequestProto.newBuilder().setAbstractTransactionRequest(
                abstractTransactionRequestProto).setXid(branchDeleteRequest.getXid()).setBranchId(
                branchDeleteRequest.getBranchId()).setBranchType(BranchTypeProto.valueOf(
                branchDeleteRequest.getBranchType().name())).setResourceId(resourceId == null ? "" : resourceId).build();
    }

    @Override
    public BranchDeleteRequest convert2Model(BranchDeleteRequestProto branchDeleteRequestProto) {
        BranchDeleteRequest branchDeleteRequest = new BranchDeleteRequest();
        branchDeleteRequest.setBranchId(branchDeleteRequestProto.getBranchId());
        branchDeleteRequest.setResourceId(branchDeleteRequestProto.getResourceId());
        branchDeleteRequest.setXid(branchDeleteRequestProto.getXid());
        branchDeleteRequest.setBranchType(
                BranchType.valueOf(branchDeleteRequestProto.getBranchType().name()));
        return branchDeleteRequest;
    }
}

