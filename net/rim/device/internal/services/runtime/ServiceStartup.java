package net.rim.device.internal.services.runtime;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.SystemListener;
import net.rim.device.internal.deviceoptions.synchronization.DeviceOptionsDaemon;
import net.rim.device.internal.deviceoptions.synchronization.SyncEventLogger;
import net.rim.device.internal.io.file.RootRegister;
import net.rim.device.internal.io.store.AAALibrary;
import net.rim.device.internal.provisioning.ProvisioningService;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.synchronization.Registration;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.CoverageInfoInternalImpl;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.timesync.TimeSyncImpl;

public final class ServiceStartup implements SystemListener {
   private int _registeredCriticalServices = 0;
   private static final long ID = -2381599947898288336L;
   public static final int SECURITY_APP = 1;
   public static final int IT_ADMIN_TRANSMISSION_SERVICE = 2;
   public static final int SECURITY_MONITOR_SERVICE = 4;
   public static final int NUM_CRITICAL_SERVICES = 3;
   public static final int ALL_CRITICAL_SERVICES = 7;

   public static final void libMain(String[] args) {
      if (!InternalServices.isFermion()) {
         Registration.SynchronizationMain();
         TimeSyncImpl.TimeSyncMain();
         net.rim.device.internal.deviceagent.Registration.DeviceAgentMain();
         SyncEventLogger.register();
         DeviceOptionsDaemon.DeviceOptionsDaemonMain();
         ProvisioningService.ProvisioningServiceMain(args);
         RootRegister.register();
         AAALibrary.register();
         CoverageInfoInternalImpl.register();
         ApplicationRegistry.getApplicationRegistry().put(-2381599947898288336L, new ServiceStartup());
      }
   }

   public static final ServiceStartup getInstance() {
      return (ServiceStartup)ApplicationRegistry.getApplicationRegistry().waitFor(-2381599947898288336L);
   }

   ServiceStartup() {
      if (InternalServices.isDeviceSecure()) {
         Proxy.getInstance().addSystemListener(this);
      }
   }

   public final void registerCriticalService(int criticalService) {
      this._registeredCriticalServices |= criticalService;
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerUp() {
      if (((ApplicationManagerInternal)ApplicationManager.getApplicationManager()).getSecurityManager() != null) {
         this.registerCriticalService(1);
      }

      if (this._registeredCriticalServices != 7) {
         InternalServices.catastrophicFailure(205, "Unregistered critical services: " + Integer.toHexString(~this._registeredCriticalServices & 7));
      }

      Proxy.getInstance().removeSystemListener(this);
   }
}
