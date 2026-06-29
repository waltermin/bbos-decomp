package net.rim.wica.runtime.lifecycle.internal;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.util.LongHashtable;

final class LifecycleManager {
   private LongHashtable _applications = (LongHashtable)(new Object(32));

   final synchronized WicletImpl getApplication(long id) {
      return (WicletImpl)this._applications.get(id);
   }

   final synchronized WicletImpl getApplication(String uri) {
      return this.getApplication(new LifecycleManager$1(this, uri));
   }

   final synchronized void addApplication(WicletImpl application) {
      this._applications.put(application.getId(), application);
   }

   final synchronized void removeApplication(long id) {
      this._applications.remove(id);
   }

   final synchronized Vector getApplications() {
      return this.getApplications(new LifecycleManager$2(this));
   }

   final synchronized WicletImpl[] getApplicationsAsArray(Vector applications) {
      Enumeration e = applications.elements();
      WicletImpl[] applicationArray = new WicletImpl[applications.size()];
      int i = 0;

      while (e.hasMoreElements()) {
         applicationArray[i++] = (WicletImpl)e.nextElement();
      }

      return applicationArray;
   }

   final synchronized Vector getApplicationsByServer(long serverId) {
      return this.getApplications(new LifecycleManager$3(this, serverId));
   }

   final synchronized Vector getDisabledWiclets() {
      return this.getApplications(new LifecycleManager$4(this));
   }

   final synchronized Vector getRunningApplications() {
      return this.getApplications(new LifecycleManager$5(this));
   }

   final synchronized Vector getSystemApplications() {
      return this.getApplications(new LifecycleManager$6(this));
   }

   final synchronized Vector getUserApplications() {
      return this.getApplications(new LifecycleManager$7(this));
   }

   final synchronized Vector getVisibleApplications() {
      return this.getApplications(new LifecycleManager$8(this));
   }

   private final synchronized WicletImpl getApplication(LifecycleManager$ApplicationBooleanExpression expression) {
      WicletImpl application = null;
      Enumeration e = this._applications.elements();

      while (e.hasMoreElements()) {
         application = (WicletImpl)e.nextElement();
         if (expression.evaluate(application)) {
            return application;
         }
      }

      return null;
   }

   private final synchronized Vector getApplications(LifecycleManager$ApplicationBooleanExpression expression) {
      Vector applications = (Vector)(new Object());
      Enumeration e = this._applications.elements();
      WicletImpl application = null;

      while (e.hasMoreElements()) {
         application = (WicletImpl)e.nextElement();
         if (expression.evaluate(application)) {
            applications.addElement(application);
         }
      }

      return applications;
   }
}
