package net.rim.device.api.system;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.vm.TraceBack;

public final class PersistentStore implements Persistable {
   private PersistentStore() {
   }

   private static final void assertPermission() {
      ApplicationControl.assertIPCAllowed(true);
   }

   public static final PersistentObject getPersistentObject(long key) {
      if (!ITPolicy.getBoolean(24, 17, true) && !ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)) {
         throw new SecurityException("Access to the Persistent Store is not permitted by your IT Policy.");
      }

      assertPermission();
      return RIMPersistentStore.getPersistentObject(key);
   }

   public static final void destroyPersistentObject(long key) {
      if (!ITPolicy.getBoolean(24, 17, true) && !ControlledAccess.verifyCodeModuleSignature(0, 51)) {
         throw new SecurityException("Access to the Persistent Store is not permitted by your IT Policy.");
      }

      assertPermission();
      RIMPersistentStore.destroyPersistentObject(key, TraceBack.getCallingModule(0));
   }

   public static final Object getSynchObject() {
      if (!ITPolicy.getBoolean(24, 17, true) && !ControlledAccess.verifyCodeModuleSignature(0, 51)) {
         throw new SecurityException("Access to the Persistent Store is not permitted by your IT Policy.");
      }

      assertPermission();
      return RIMPersistentStore.getSynchObject();
   }
}
