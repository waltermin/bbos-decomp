package net.rim.device.apps.internal.elt;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.internal.proxy.Proxy;

final class ITPolicyListener implements GlobalEventListener {
   private boolean _hasITPolicyChanged;
   public static final int IT_POLICY_GROUP = 48;
   public static final int DISABLE_BLACKBERRY_MAPS = 1;
   public static final int ENABLE_ENTERPRISE_TRACKING = 2;
   public static final int ELT_ITPOLICY_USER_MESSAGE = 3;
   public static final int REPORTING_INTERVAL = 4;
   private static final long GUID = -4382460237972221749L;
   private static ITPolicyListener INSTANCE;

   private ITPolicyListener() {
      ELTState data = ETManager.getELTData();
      data.setEnabledByITPolicy(ITPolicy.getBoolean(48, 2, false));
      int interval = ITPolicy.getInteger(48, 4, 60);
      data.setGPSInterval(interval);
   }

   static final ITPolicyListener getInstance() {
      if (INSTANCE == null) {
         INSTANCE = (ITPolicyListener)ApplicationRegistry.getApplicationRegistry().getOrWaitFor(-4382460237972221749L);
      }

      if (INSTANCE == null) {
         INSTANCE = new ITPolicyListener();
         ApplicationRegistry.getApplicationRegistry().put(-4382460237972221749L, INSTANCE);
         Proxy.getInstance().addGlobalEventListener(INSTANCE);
      }

      return INSTANCE;
   }

   static final void registerOnStartup() {
      getInstance();
   }

   final boolean hasITPolicyChanged() {
      return this._hasITPolicyChanged;
   }

   final void setITPolicyChanged(boolean state) {
      this._hasITPolicyChanged = state;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         boolean enabled = ITPolicy.getBoolean(48, 2, false);
         ETManager manager = ETApplication.getETManager();
         ELTState data = ETManager.getELTData();
         this._hasITPolicyChanged = enabled != data.isEnabledByITPolicy();
         Logger.logEvent(
            this,
            "IT_POLICY_CHANGED, ENABLE_ENTERPRISE_TRACKING: "
               + enabled
               + ", enabledByITPolicy="
               + data.isEnabledByITPolicy()
               + ", IT Changed: "
               + this._hasITPolicyChanged,
            true
         );
         if (this._hasITPolicyChanged) {
            data.setEnabledByITPolicy(enabled);
            if (!enabled) {
               data.setEnabledByUser(false);
            }

            manager.commit();

            try {
               RibbonLauncher.getInstance().launch("net_rim_bb_options_app?net.rim.device.apps.internal.options.items.LocationServicesOptionsItem");
               return;
            } catch (Throwable var12) {
               Logger.logError(this, "options app launch error: " + amex);
               return;
            }
         }
      }
   }
}
