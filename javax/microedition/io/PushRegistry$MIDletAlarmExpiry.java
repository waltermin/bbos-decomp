package javax.microedition.io;

import java.util.Date;
import net.rim.device.internal.system.MIDletSecurity;

final class PushRegistry$MIDletAlarmExpiry implements Runnable {
   private String _midletClassName;
   private Date _expiry;

   public final long getExpiry() {
      return this._expiry.getTime();
   }

   @Override
   public final void run() {
      try {
         MIDletSecurity.checkPermission(8);
         PushRegistry.launchMidlet(this._midletClassName, new String[]{this._midletClassName}, true);
      } catch (SecurityException var2) {
      }
   }

   public PushRegistry$MIDletAlarmExpiry(String classname, Date expiry) {
      this._midletClassName = classname;
      this._expiry = expiry;
   }
}
