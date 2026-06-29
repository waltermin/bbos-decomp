package net.rim.device.apps.api.sync;

public interface Reconcilable {
   long ROOT_OBJECT;
   long OTR_ROOT_OBJECT;

   void reconcile(Object var1, Object var2);
}
