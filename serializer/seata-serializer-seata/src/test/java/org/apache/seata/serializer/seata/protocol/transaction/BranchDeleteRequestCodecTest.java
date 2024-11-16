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
package org.apache.seata.serializer.seata.protocol.transaction;

import org.apache.seata.core.model.BranchType;
import org.apache.seata.core.protocol.ProtocolConstants;
import org.apache.seata.core.protocol.transaction.BranchDeleteRequest;
import org.apache.seata.serializer.seata.SeataSerializer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The type Branch delete request codec test.
 */
public class BranchDeleteRequestCodecTest {

    /**
     * The Seata codec.
     */
    SeataSerializer seataSerializer = new SeataSerializer(ProtocolConstants.VERSION);

    /**
     * Test codec.
     */
    @Test
    public void test_codec(){
        BranchDeleteRequest branchDeleteRequest = new BranchDeleteRequest();
        branchDeleteRequest.setBranchId(112232);
        branchDeleteRequest.setBranchType(BranchType.TCC);
        branchDeleteRequest.setResourceId("343");
        branchDeleteRequest.setXid("123");

        byte[] bytes = seataSerializer.serialize(branchDeleteRequest);

        BranchDeleteRequest branchDeleteRequest2 = seataSerializer.deserialize(bytes);

        assertThat(branchDeleteRequest2.getBranchId()).isEqualTo(branchDeleteRequest.getBranchId());
        assertThat(branchDeleteRequest2.getBranchType()).isEqualTo(branchDeleteRequest.getBranchType());
        assertThat(branchDeleteRequest2.getResourceId()).isEqualTo(branchDeleteRequest.getResourceId());
        assertThat(branchDeleteRequest2.getXid()).isEqualTo(branchDeleteRequest.getXid());
    }
}
