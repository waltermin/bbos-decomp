package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.apps.api.ribbon.Action;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.internal.system.LockEventLogger;
import net.rim.device.internal.system.Security;
import net.rim.vm.Memory;

final class LockAction extends Action implements GlobalEventListener {
   private Security _security = Security.getInstance();
   private Bitmap _lockIcon;
   private Bitmap _keyLockIcon;
   private ResourceBundleFamily _rbf;
   private static String LOCK_BITMAP_NAME = "net_rim_LockSystem.gif";
   private static String KEYLOCK_BITMAP_NAME = "net_rim_KeyLockSystem.gif";
   private static String LOCK_BITMAP_MODULE = "net_rim_bb_ribbon_lib";
   private static String LOCK_RIBBON_ENTRY_NAME = "net_rim_LockSystem";

   LockAction(ApplicationDescriptor applicationDescriptor) {
      super(applicationDescriptor, LOCK_RIBBON_ENTRY_NAME, 220);
      this._lockIcon = Bitmap.getBitmapResource(LOCK_BITMAP_MODULE, LOCK_BITMAP_NAME);
      this._keyLockIcon = Bitmap.getBitmapResource(LOCK_BITMAP_MODULE, KEYLOCK_BITMAP_NAME);
      Application.getApplication().addGlobalEventListener(this);
      this._rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -1789715216180579536L) {
         RibbonLauncher rl = RibbonLauncher.getInstance();
         rl.updateRegisteredAction(this.get(1, (String)((Object)null)));
      }
   }

   @Override
   protected final String getDescription() {
      return !this._security.isPasswordEnabled() ? this._rbf.getString(5) : this._rbf.getString(4);
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      return super.get(propID, defaultReturned);
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      if (propID == 5) {
         return !this._security.isPasswordEnabled() ? this._keyLockIcon : this._lockIcon;
      } else {
         return defaultReturned;
      }
   }

   @Override
   public final void run() {
      LockEventLogger.logLockEvent(1281454192);
      super.run();
      Memory.scheduleIdleGC();
   }
}
