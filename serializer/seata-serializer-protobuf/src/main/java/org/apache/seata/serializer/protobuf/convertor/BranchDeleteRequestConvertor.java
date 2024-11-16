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

