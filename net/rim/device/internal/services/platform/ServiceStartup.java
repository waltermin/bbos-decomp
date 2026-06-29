package net.rim.device.internal.services.platform;

import javax.microedition.content.Registry;
import net.rim.device.api.crypto.Crypto10Initialization;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.deviceoptions.DeviceOptions;
import net.rim.device.internal.i18n.Initialization;
import net.rim.device.internal.i18n.ResourceBundleFetcher;
import net.rim.device.internal.io.file.FileSystemPropertyProvider;
import net.rim.device.internal.lowMemory.Registration;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.CodeModuleDeletionListener;
import net.rim.device.internal.system.CodeModuleGroupPropertiesCollection;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.DataServices;
import net.rim.device.internal.system.ForcedResetManager;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.RIMProcessLauncherMain;
import net.rim.device.internal.system.SystemPropertyManager;
import net.rim.device.internal.system.USBPasswordRedirectManager;
import net.rim.device.internal.ui.IMSwitcherOption;
import net.rim.vm.Process;

final class ServiceStartup {
   public static final void main(String[] args) {
      if (args.length != 0) {
         if (args[0].equals("protocoldaemon")) {
            ProtocolDaemon.start();
         } else {
            switch (Integer.parseInt(args[0])) {
               case 0:
                  tier0();
                  return;
               case 2:
                  tier2();
            }
         }
      }
   }

   private static final void tier0() {
      Process p = Process.currentProcess();
      p.setThreadLimit(64);
      p.haltDeviceIfThisProcessDies(true);
      Application proxy = Proxy.ProxyMain();
      RIMProcessLauncherMain.initialize();
      ResourceBundleFetcher.initialize();
      CodeStore.generateDependencyList();
      new SHA1Digest();

      label85:
      try {
         Crypto10Initialization.initialize();
      } finally {
         break label85;
      }

      DeviceOptions.DeviceOptionsMain();
      Registration.LowMemoryMain();
      net.rim.device.internal.ui.autotext.Registration.AutoTextMain();
      DataServices.getInstance();
      net.rim.device.internal.firewall.Registration.FirewallMain();
      net.rim.device.internal.gps.Registration.GPSFirewallMain();
      net.rim.device.internal.gps.Registration.GPSBluetoothSimulateMain();
      Initialization.I18nMain();
      net.rim.device.internal.ui.Initialization.UIMain();
      USBPasswordRedirectManager.initialize();
      ForcedResetManager.initialize();
      ITPolicyInternal.initialize();
      CodeModuleDeletionListener.CodeModuleDeletionListenerMain();
      CodeModuleGroupPropertiesCollection.CodeModuleGroupPropertiesCollectionMain();

      label81:
      try {
         Registry.getServer("net.rim.device.internal.services.platform");
      } finally {
         break label81;
      }

      proxy.addSystemListener(new FastResetListener());

      while (true) {
         try {
            proxy.enterEventDispatcher();
         } finally {
            continue;
         }
      }
   }

   private static final void tier2() {
      ThemeManager.activateTheme();
      IMSwitcherOption.getInstance();
      SystemPropertyManager sysPropManager = SystemPropertyManager.getInstance();
      if (sysPropManager != null) {
         sysPropManager.addProvider(new FileSystemPropertyProvider());
      }
   }
}
