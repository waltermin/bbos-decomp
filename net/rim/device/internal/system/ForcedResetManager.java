package net.rim.device.internal.system;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.proxy.Proxy;

public final class ForcedResetManager implements GlobalEventListener {
   private int _currentFIPSLevel = ITPolicy.getInteger(24, 39, 1);
   private static final long GUID;
   private static final long RESET_SCHEDULE_GUID;
   private static final int NUM_RESET_WARNINGS;
   private static final long TIME_BETWEEN_RESET_WARNINGS;
   private static final long SIXTY_SECONDS;
   private static ForcedResetManager _resetManager;

   public final void scheduleDeviceResetNoTimeout(String message, int numResetWarnings, long timeBetweenResetWarnings, boolean delayedResetOption) {
      this.scheduleDeviceReset(message, numResetWarnings, timeBetweenResetWarnings, delayedResetOption, false);
   }

   public final void scheduleDeviceReset(String message) {
      this.scheduleDeviceReset(message, 5, 600000, false, true);
   }

   public final void scheduleDeviceResetNoTimeout(String message) {
      this.scheduleDeviceReset(message, 5, 600000, false, false);
   }

   public final void scheduleDeviceReset(String message, boolean delayedResetOption) {
      this.scheduleDeviceReset(message, 5, 600000, delayedResetOption, true);
   }

   public final void scheduleDeviceResetNoTimeout(String message, boolean delayedResetOption) {
      this.scheduleDeviceReset(message, 5, 600000, delayedResetOption, false);
   }

   public final void scheduleDeviceReset(String message, long timeBetweenResetWarnings, boolean delayedResetOption) {
      this.scheduleDeviceReset(message, 5, timeBetweenResetWarnings, delayedResetOption, true);
   }

   public final void scheduleDeviceResetNoTimeout(String message, long timeBetweenResetWarnings, boolean delayedResetOption) {
      this.scheduleDeviceReset(message, 5, timeBetweenResetWarnings, delayedResetOption, false);
   }

   public final void scheduleDeviceReset(String message, int numResetWarnings, long timeBetweenResetWarnings, boolean delayedResetOption) {
      this.scheduleDeviceReset(message, numResetWarnings, timeBetweenResetWarnings, delayedResetOption, true);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -594020114676189989L || guid == 8508406279413621091L) {
         if (CodeModuleManager.verifyApplicationControlModules() == 6) {
            this.scheduleDeviceReset(CommonResource.getString(10086));
            return;
         }

         int oldFIPSLevel = this._currentFIPSLevel;
         this._currentFIPSLevel = ITPolicy.getInteger(24, 39, 1);
         if (oldFIPSLevel != this._currentFIPSLevel && (oldFIPSLevel == 3 || this._currentFIPSLevel == 3)) {
            this.scheduleDeviceReset(CommonResource.getString(10088));
         }
      }
   }

   public static final ForcedResetManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         _resetManager = (ForcedResetManager)ar.get(-8068307003274200341L);
         if (_resetManager == null) {
            _resetManager = new ForcedResetManager();
            ar.put(-8068307003274200341L, _resetManager);
         }
      }

      return _resetManager;
   }

   public static final void initialize() {
      getInstance();
   }

   public ForcedResetManager() {
      Proxy.getInstance().addGlobalEventListenerInternal(this);
   }

   private final void scheduleDeviceReset(
      String message, int numResetWarnings, long timeBetweenResetWarnings, boolean delayedResetOption, boolean dialogShouldTimeout
   ) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ForcedResetManager$ResetRunnable deviceResetScheduled = null;
      synchronized (ar) {
         deviceResetScheduled = (ForcedResetManager$ResetRunnable)ar.get(150282248917516260L);
         if (deviceResetScheduled != null) {
            deviceResetScheduled.update(message, numResetWarnings, timeBetweenResetWarnings, delayedResetOption, dialogShouldTimeout);
            return;
         }

         deviceResetScheduled = new ForcedResetManager$ResetRunnable(
            message, numResetWarnings, timeBetweenResetWarnings, delayedResetOption, dialogShouldTimeout
         );
         ar.put(150282248917516260L, deviceResetScheduled);
      }

      Proxy.getInstance().invokeLater(deviceResetScheduled);
   }
}
