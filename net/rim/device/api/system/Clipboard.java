package net.rim.device.api.system;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.TraceBack;

public final class Clipboard {
   private Object _object;
   private long _pastePriorityTimeout = 60000;
   private long _pastePriorityExpiration;
   private boolean _notYetPasted = false;
   private static final long REGISTRY_NAME;
   private static final long PASTE_PRIORITY_TIMEOUT;
   private static Clipboard _instance;

   private Clipboard() {
   }

   public final Object get() {
      return this._object != null && !this.isClipboardDisabled() && !this.isClipboardAccessRestricted(this._object) ? this._object : null;
   }

   public static final synchronized Clipboard getClipboard() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (Clipboard)ar.getOrWaitFor(-2401061171848633112L);
         if (_instance == null) {
            _instance = new Clipboard();
            ar.put(-2401061171848633112L, _instance);
         }
      }

      return _instance;
   }

   private final boolean isClipboardAccessRestricted(Object o) {
      if (!ApplicationControl.isIPCAllowed(true)) {
         return true;
      }

      if (o != null && !(o instanceof String) && !(o instanceof StringBuffer)) {
         int callingModule = TraceBack.getCallingModule(2);
         if (!ControlledAccess.verifyCodeModuleSignature(callingModule, 51)) {
            return true;
         }
      }

      return false;
   }

   private final boolean isClipboardDisabled() {
      return ITPolicy.getBoolean(24, 36, false);
   }

   public final boolean isNotYetPasted() {
      return this._notYetPasted;
   }

   public final boolean isTimeForPasteAsDefaultNotPassed() {
      return InternalServices.getUptime() < this._pastePriorityExpiration;
   }

   public final Object put(Object o) {
      this._pastePriorityExpiration = InternalServices.getUptime();
      if (this.isClipboardDisabled()) {
         this._object = null;
         return null;
      }

      if (this.isClipboardAccessRestricted(o)) {
         throw new SecurityException();
      }

      Object temp = this._object;
      this._object = o;
      if (o != null) {
         this._pastePriorityExpiration = this._pastePriorityExpiration + this._pastePriorityTimeout;
      }

      return temp;
   }

   public final void setNotYetPasted(boolean notYetPasted) {
      this._notYetPasted = notYetPasted;
   }

   public final void setPastePriorityTimeout(long pastePriorityTimeout) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (pastePriorityTimeout > 0) {
         this._pastePriorityTimeout = pastePriorityTimeout;
      } else {
         this._pastePriorityTimeout = 60000;
      }
   }

   @Override
   public final String toString() {
      return this._object != null && !this.isClipboardDisabled() && !this.isClipboardAccessRestricted(this._object) ? this._object.toString() : "";
   }
}
