/*
 * Copyright 2005 The Apache Software Foundation
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

package org.apache.commons.pipeline.testFramework;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pipeline.*;
import org.apache.commons.pipeline.validation.ConsumedTypes;
import org.apache.commons.pipeline.validation.ProducesConsumed;

@ConsumedTypes(Object.class)
@ProducesConsumed
public class TestStage implements Stage {
    private Log log = LogFactory.getLog(TestStage.class);
    private int index;
    private StageContext context;

    public List<Object> processedObjects = new ArrayList<Object>();
    public boolean initialized = false;
    public boolean preprocessed = false;
    public boolean postprocessed = false;
    public boolean released = false;
    
    public TestStage(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return this.index;
    }

    public void init(StageContext context) {        
        this.context = context;
        this.initialized = true;
    }
    
    public void preprocess() throws StageException {
        this.preprocessed = true;
    }

    public void process(Object obj) throws StageException {
        log.info(this + " is processing object " + obj);
        this.processedObjects.add(obj);
        this.context.getDownstreamFeeder(this).feed(obj);
    }
    
    public void postprocess() throws StageException {
        this.postprocessed = true;
    }

    public void release() {
        this.released = true;
    }

    public String toString() {
        return "TEST STAGE (" + this.index + ")";
    }
}