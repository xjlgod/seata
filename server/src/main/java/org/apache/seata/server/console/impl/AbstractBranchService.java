package org.apache.seata.server.console.impl;

import org.apache.seata.common.result.SingleResult;
import org.apache.seata.core.model.BranchStatus;
import org.apache.seata.core.model.GlobalStatus;
import org.apache.seata.server.console.exception.ConsoleException;
import org.apache.seata.server.console.service.BranchSessionService;
import org.apache.seata.server.session.BranchSession;
import org.apache.seata.server.session.GlobalSession;

public abstract class AbstractBranchService extends AbstractService implements BranchSessionService {
    @Override
    public SingleResult<Void> stopBranchRetry(String xid, String branchId) {
        CheckResult checkResult = commonCheckAndGetGlobalStatus(xid, branchId);
        GlobalSession globalSession = checkResult.getGlobalSession();
        // saga is not support to operate
        if (globalSession.isSaga()) {
            throw new IllegalArgumentException("saga can not operate branch transactions because it have no determinative role");
        }
        BranchSession branchSession = checkResult.getBranchSession();
        BranchStatus branchStatus = branchSession.getStatus();
        if (branchStatus.getCode() == BranchStatus.STOP_RETRY.getCode()) {
            throw new IllegalArgumentException("current branch session is already stop");
        }
        // For BranchStatus.PhaseOne_Done is finished, will remove soon, thus no support
        if (branchStatus != BranchStatus.Unknown && branchStatus != BranchStatus.Registered &&
                branchStatus != BranchStatus.PhaseOne_Done) {
            throw new IllegalArgumentException("current branch session is not support to stop");
        }
        GlobalStatus status = globalSession.getStatus();
        BranchStatus newStatus = RETRY_STATUS.contains(status) || GlobalStatus.Rollbacking.equals(status) ||
                GlobalStatus.Committing.equals(status) || GlobalStatus.StopRollbackRetry.equals(status) ||
                GlobalStatus.StopCommitRetry.equals(status) ? BranchStatus.STOP_RETRY : null;
        if (newStatus == null) {
            throw new IllegalArgumentException("wrong status for global status");
        }
        branchSession.setStatus(newStatus);
        try {
            globalSession.changeBranchStatus(branchSession, newStatus);
        } catch (Exception e) {
            throw new ConsoleException(e, String.format("stop branch session retry fail, xid:%s, branchId:%s", xid, branchId));
        }
        return SingleResult.success();
    }

    @Override
    public SingleResult<Void> startBranchRetry(String xid, String branchId) {
        CheckResult checkResult = commonCheckAndGetGlobalStatus(xid, branchId);
        GlobalSession globalSession = checkResult.getGlobalSession();
        // saga is not support to operate
        if (globalSession.isSaga()) {
            throw new IllegalArgumentException("saga can not operate branch transactions because it have no determinative role");
        }
        BranchSession branchSession = checkResult.getBranchSession();
        BranchStatus branchStatus = branchSession.getStatus();
        if (!BranchStatus.STOP_RETRY.equals(branchStatus)) {
            throw new IllegalArgumentException("current branch transactions status is not support to start retry");
        }
        // BranchStatus.PhaseOne_Done and BranchStatus.Registered will become BranchStatus.Registered
        BranchStatus newStatus = BranchStatus.Registered;
        branchSession.setStatus(newStatus);
        try {
            globalSession.changeBranchStatus(branchSession, newStatus);
        } catch (Exception e) {
            throw new ConsoleException(e, String.format("start branch session retry fail, xid:%s, branchId:%s", xid, branchId));
        }
        return SingleResult.success();
    }

    @Override
    public SingleResult<Void> deleteBranchSession(String xid, String branchId) {
        CheckResult checkResult = commonCheckAndGetGlobalStatus(xid, branchId);
        GlobalSession globalSession = checkResult.getGlobalSession();
        // saga is not support to operate
        if (globalSession.isSaga()) {
            throw new IllegalArgumentException("saga can not operate branch transactions because it have no determinative role");
        }
        GlobalStatus globalStatus = globalSession.getStatus();
        BranchSession branchSession = checkResult.getBranchSession();
        if (FAIL_STATUS.contains(globalStatus) || RETRY_STATUS.contains(globalStatus)
                || FINISH_STATUS.contains(globalStatus) || GlobalStatus.StopRollbackRetry == globalStatus
                || GlobalStatus.StopCommitRetry == globalStatus || GlobalStatus.Deleting == globalStatus) {
            try {
                boolean deleted = doDeleteBranch(globalSession, branchSession);
                return deleted ? SingleResult.success() :
                        SingleResult.failure("delete branch fail, please retry again later");
            } catch (Exception e) {
                throw new ConsoleException(e, String.format("delete branch session fail, xid:%s, branchId:%s", xid, branchId));
            }
        }
        throw new IllegalArgumentException("current global transaction is not support delete branch transaction");
    }
}
