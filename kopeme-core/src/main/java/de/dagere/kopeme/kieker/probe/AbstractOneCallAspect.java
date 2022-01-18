/***************************************************************************
 * Copyright 2020 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/

package de.dagere.kopeme.kieker.probe;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import de.dagere.kopeme.kieker.record.OneCallRecord;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.probe.aspectj.AbstractAspectJProbe;
import kieker.monitoring.timer.ITimeSource;

/**
 * Allows to only record which methods are called (especially relevant for big traces)
 */
@Aspect
public abstract class AbstractOneCallAspect extends AbstractAspectJProbe {

   private static final IMonitoringController CTRLINST = MonitoringController.getInstance();
   private static final ITimeSource TIME = CTRLINST.getTimeSource();

   /**
    * The pointcut for the monitored operations. Inheriting classes should extend the pointcut in order to find the correct executions of the methods (e.g. all methods or only
    * methods with specific annotations).
    */
   @Pointcut
   public abstract void monitoredOperation();

   @Before("monitoredOperation() && notWithinKieker()")
   public void beforeOperation(final JoinPoint thisJoinPoint) throws Throwable { // NOCS
      if (!CTRLINST.isMonitoringEnabled()) {
         return;
      }
      final String signature = this.signatureToLongString(thisJoinPoint.getSignature());
      if (!CTRLINST.isProbeActivated(signature)) {
         return;
      }
      CTRLINST.newMonitoringRecord(new OneCallRecord(signature));
   }
}
