/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.jmx.blueprint.codec;

import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;

import org.apache.aries.jmx.blueprint.BlueprintMetadataMBean;
import org.osgi.service.blueprint.reflect.ReferenceMetadata;

public class BPReferenceMetadata extends BPServiceReferenceMetadata implements BPTarget {

    private long timeout;

    public BPReferenceMetadata(CompositeData reference) {
        super(reference);
        timeout = (Long) reference.get(BlueprintMetadataMBean.TIMEOUT);
    }

    public BPReferenceMetadata(ReferenceMetadata reference) {
        super(reference);
        timeout = reference.getTimeout();
    }

    protected Map<String, Object> getItemsMap() {
        Map<String, Object> items = super.getItemsMap();
        items.put(BlueprintMetadataMBean.TIMEOUT, timeout);

        return items;
    }

    public CompositeData asCompositeData() {
        try {
            return new CompositeDataSupport(BlueprintMetadataMBean.REFERENCE_METADATA_TYPE, getItemsMap());
        } catch (OpenDataException e) {
            throw new RuntimeException(e);
        }
    }

    public long getTimeout() {
        return timeout;
    }
}
