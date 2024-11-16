package org.apache.seata.server.console.impl;

import org.apache.seata.common.util.StringUtils;
import org.apache.seata.common.result.SingleResult;
import org.apache.seata.server.console.param.GlobalLockParam;
import org.apache.seata.server.console.service.GlobalLockService;

public abstract class AbstractLockService extends AbstractService implements GlobalLockService {

    @Override
    public SingleResult<Boolean> check(String xid, String branchId) {
        try {
            commonCheckAndGetGlobalStatus(xid, branchId);
        } catch (IllegalArgumentException e) {
            return SingleResult.success(Boolean.FALSE);
        }
        return SingleResult.success(Boolean.TRUE);
    }

    protected void checkDeleteLock(GlobalLockParam param) {
        commonCheck(param.getXid(), param.getBranchId());
        if (StringUtils.isBlank(param.getTableName()) || StringUtils.isBlank(param.getPk())
                || StringUtils.isBlank(param.getResourceId())) {
            throw new IllegalArgumentException("tableName or resourceId or pk can not be empty");
        }
    }
}

