/*
 * Copyright 2014, The Sporting Exchange Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.betfair.cougar.netutil.nio.message;

import com.betfair.cougar.netutil.nio.CougarProtocol;
import com.betfair.cougar.netutil.nio.CougarProtocolEncoder;
import org.apache.mina.common.ByteBuffer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMessage implements ProtocolMessage {

    @Override
    public ByteBuffer getSerialisedForm(byte protocolVersion) {
        return CougarProtocolEncoder.encode(this, protocolVersion);
    }
}