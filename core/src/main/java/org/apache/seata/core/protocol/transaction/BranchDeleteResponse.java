package org.apache.seata.core.protocol.transaction;

import org.apache.seata.core.protocol.MessageType;

/**
 * BranchDeleteResponse
 *
 *
 */
public class BranchDeleteResponse extends AbstractBranchEndResponse {
    @Override
    public short getTypeCode() {
        return MessageType.TYPE_BRANCH_DELETE_RESULT;
    }
}
