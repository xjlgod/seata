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

import org.apache.seata.core.exception.TransactionExceptionCode;
import org.apache.seata.core.model.BranchStatus;
import org.apache.seata.core.protocol.ResultCode;
import org.apache.seata.core.protocol.transaction.BranchDeleteResponse;
import org.apache.seata.serializer.protobuf.generated.BranchDeleteResponseProto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The type Branch delete response convertor test.
 */
public class BranchDeleteResponseConvertorTest {
    @Test
    public void convert2Proto() {
        BranchDeleteResponse branchDeleteResponse = new BranchDeleteResponse();
        branchDeleteResponse.setMsg("msg");
        branchDeleteResponse.setResultCode(ResultCode.Failed);
        branchDeleteResponse.setTransactionExceptionCode(TransactionExceptionCode.GlobalTransactionNotExist);
        branchDeleteResponse.setXid("xid");
        branchDeleteResponse.setBranchStatus(BranchStatus.PhaseTwo_Rollbacked);
        branchDeleteResponse.setBranchId(123);

        BranchDeleteResponseConvertor convertor = new BranchDeleteResponseConvertor();
        BranchDeleteResponseProto proto = convertor.convert2Proto(branchDeleteResponse);
        BranchDeleteResponse real = convertor.convert2Model(proto);
        assertThat((real.getTypeCode())).isEqualTo(branchDeleteResponse.getTypeCode());
        assertThat((real.getMsg())).isEqualTo(branchDeleteResponse.getMsg());
        assertThat((real.getResultCode())).isEqualTo(branchDeleteResponse.getResultCode());
        assertThat((real.getTransactionExceptionCode())).isEqualTo(branchDeleteResponse.getTransactionExceptionCode());
        assertThat((real.getBranchId())).isEqualTo(branchDeleteResponse.getBranchId());
        assertThat((real.getXid())).isEqualTo(branchDeleteResponse.getXid());
        assertThat((real.getBranchStatus())).isEqualTo(branchDeleteResponse.getBranchStatus());
    }
}
