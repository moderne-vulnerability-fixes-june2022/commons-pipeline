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
 *
 * Created on July 22, 2005, 4:10 PM
 *
 * $Log: SimpleStageDriverTest.java,v $
 * Revision 1.3  2005/07/25 22:19:17  kjn
 * Updated licenses, documentation.
 *
 */

package org.apache.commons.pipeline.driver;

import junit.framework.*;
import org.apache.commons.pipeline.BaseStage;
import org.apache.commons.pipeline.Pipeline;
import org.apache.commons.pipeline.Stage;
import org.apache.commons.pipeline.StageDriver;
import org.apache.commons.pipeline.StageException;


/**
 * Test cases for SimpleStageDriver.
 *
 * @author <a href="mailto:Kris.Nuttycombe@noaa.gov">Kris Nuttycombe</a>, National Geophysical Data Center, NOAA
 */
public class SimpleStageDriverTest extends TestCase {
    
    private Pipeline pipeline;
    private volatile int[] processedObjectCount;
    
    public SimpleStageDriverTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        this.pipeline = new Pipeline();
        this.processedObjectCount = new int[3];
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(SimpleStageDriverTest.class);
        
        return suite;
    }
    
    /**
     * Integration test for org.apache.commons.pipeline.driver.SimpleStageDriver.
     */
    public void testRunPipeline() {
        StageDriver driver = new SimpleStageDriver();
        Stage stage0 = new TestStage(0);
        this.pipeline.addStage(stage0, driver);
        Stage stage1 = new TestStage(1);
        this.pipeline.addStage(stage1, driver);
        Stage stage2 = new TestStage(2);
        this.pipeline.addStage(stage2, driver);
       
        this.pipeline.enqueue(new Object());
        this.pipeline.enqueue(new Object());
        this.pipeline.enqueue(new Object());
        this.pipeline.enqueue(new Object());
        this.pipeline.run();
        
        Assert.assertEquals(4, processedObjectCount[0]);
        Assert.assertEquals(4, processedObjectCount[1]);
        Assert.assertEquals(4, processedObjectCount[2]);
        Assert.assertEquals(0, stage0.getMonitor().getErrors().size());
        Assert.assertEquals(0, stage1.getMonitor().getErrors().size());
        Assert.assertEquals(0, stage2.getMonitor().getErrors().size());
    }
    
    public void testFaultingPipeline() {
        StageDriver driver = new SimpleStageDriver();
        Stage stage0 = new TestStage(0);
        this.pipeline.addStage(stage0, driver);
        Stage stage1 = new FaultingTestStage(1);
        this.pipeline.addStage(stage1, driver);
        Stage stage2 = new TestStage(2);
        this.pipeline.addStage(stage2, driver);
        
        this.pipeline.enqueue(new Object());
        this.pipeline.enqueue(new Object());
        this.pipeline.enqueue(new Object());
        this.pipeline.enqueue(new Object());
        this.pipeline.run();
        
        Assert.assertEquals(4, processedObjectCount[0]);
        Assert.assertEquals(2, processedObjectCount[1]);
        Assert.assertEquals(2, processedObjectCount[2]);
        Assert.assertEquals(0, stage0.getMonitor().getErrors().size());
        Assert.assertEquals(2, stage1.getMonitor().getErrors().size());
        Assert.assertEquals(0, stage2.getMonitor().getErrors().size());
    }
    
    private class TestStage extends BaseStage {
        private int index;
        
        public TestStage(int index) {
            this.index = index;
        }
        
        public void process(Object obj) throws org.apache.commons.pipeline.StageException {
            processedObjectCount[index]++;
            super.process(obj);
        }
    }
    
    private class FaultingTestStage extends BaseStage {
        private int index;
        private int counter;
        
        public FaultingTestStage(int index) {
            this.index = index;
        }
        
        public void process(Object obj) throws org.apache.commons.pipeline.StageException {
            if (++counter % 2 == 0) throw new StageException("Planned fault.");
            
            processedObjectCount[index]++;
            super.process(obj);
        }
    }
}
