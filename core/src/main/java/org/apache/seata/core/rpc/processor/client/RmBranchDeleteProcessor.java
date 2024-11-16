package org.apache.seata.core.rpc.processor.client;

import io.netty.channel.ChannelHandlerContext;
import org.apache.seata.common.util.NetUtil;
import org.apache.seata.core.protocol.RpcMessage;
import org.apache.seata.core.protocol.transaction.BranchDeleteRequest;
import org.apache.seata.core.protocol.transaction.BranchDeleteResponse;
import org.apache.seata.core.rpc.RemotingClient;
import org.apache.seata.core.rpc.TransactionMessageHandler;
import org.apache.seata.core.rpc.processor.RemotingProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Rm branch delete processor.
 */
public class RmBranchDeleteProcessor implements RemotingProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RmBranchDeleteProcessor.class);

    private TransactionMessageHandler handler;

    private RemotingClient remotingClient;

    public RmBranchDeleteProcessor(TransactionMessageHandler handler, RemotingClient remotingClient) {
        this.handler = handler;
        this.remotingClient = remotingClient;
    }

    @Override
    public void process(ChannelHandlerContext ctx, RpcMessage rpcMessage) throws Exception {
        String remoteAddress = NetUtil.toStringAddress(ctx.channel().remoteAddress());
        Object msg = rpcMessage.getBody();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("rm handle branch rollback process: {}", msg);
        }
        handleBranchDelete(rpcMessage, remoteAddress, (BranchDeleteRequest) msg);
    }

    private void handleBranchDelete(RpcMessage request, String serverAddress, BranchDeleteRequest branchDeleteRequest) {
        BranchDeleteResponse resultMessage;
        resultMessage = (BranchDeleteResponse) handler.onRequest(branchDeleteRequest, null);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("branch delete result: {}", resultMessage);
        }
        try {
            this.remotingClient.sendAsyncResponse(serverAddress, request, resultMessage);
        } catch (Throwable throwable) {
            LOGGER.error("branch delete error: {}", throwable.getMessage(), throwable);
        }
    }
}
