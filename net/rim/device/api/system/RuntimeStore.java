package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.vm.TraceBack;

public final class RuntimeStore {
   private ApplicationRegistry _ar;
   private static final long GUID = -4040261540098774066L;

   private RuntimeStore(ApplicationRegistry ar) {
      this._ar = ar;
   }

   public final Object get(long id) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      return this._ar.get(caller, id, false, null, null);
   }

   public final Object get(long id, CodeSigningKey readAndReplaceKey) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      return this._ar.get(caller, id, false, readAndReplaceKey, readAndReplaceKey);
   }

   public final Object get(long id, CodeSigningKey readKey, CodeSigningKey replaceKey) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      return this._ar.get(caller, id, false, readKey, replaceKey);
   }

   public final ControlledAccess getControlledAccess(long id) {
      assertPermission();
      return this._ar.getControlledAccess(id, false);
   }

   public final void put(long id, Object value) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      this._ar.put(caller, id, false, value, false);
   }

   public final Object replace(long id, Object value) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      return this._ar.put(caller, id, false, value, true);
   }

   public final Object remove(long id) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      return this._ar.remove(caller, id, false, null, null);
   }

   public final Object remove(long id, CodeSigningKey readAndReplaceKey) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      return this._ar.remove(caller, id, false, readAndReplaceKey, readAndReplaceKey);
   }

   public final Object remove(long id, CodeSigningKey readKey, CodeSigningKey replaceKey) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      return this._ar.remove(caller, id, false, readKey, replaceKey);
   }

   public final Object waitFor(long id) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      return this._ar.waitFor(caller, id, false, null, null, false);
   }

   public final Object waitFor(long id, CodeSigningKey readAndReplaceKey) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      return this._ar.waitFor(caller, id, false, readAndReplaceKey, readAndReplaceKey, false);
   }

   public final Object waitFor(long id, CodeSigningKey readKey, CodeSigningKey replaceKey) {
      assertPermission();
      int caller = TraceBack.getCallingModule(0);
      return this._ar.waitFor(caller, id, false, readKey, replaceKey, false);
   }

   private static final void assertPermission() {
      ApplicationControl.assertIPCAllowed(true);
   }

   public static final RuntimeStore getRuntimeStore() {
      assertPermission();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      RuntimeStore store = (RuntimeStore)ar.getOrWaitFor(-4040261540098774066L);
      if (store == null) {
         store = new RuntimeStore(ar);
         ar.put(-4040261540098774066L, store);
      }

      return store;
   }
}
