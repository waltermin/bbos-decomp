package net.rim.device.apps.internal.bis;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.invoke.BISClientInvoke;
import net.rim.device.apps.internal.bis.launch.controller.Controller;
import net.rim.device.apps.internal.bis.launch.ui.InitializationScreen;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Process;

public final class BISLaunch extends UiApplication {
   private int _uiMode;
   protected boolean _udpatesCheckSuccessful;
   protected boolean _updatesRequired;
   protected boolean _updatesAvailable;
   protected boolean _updatesCheckSuccessful;
   protected String[] _urls;
   byte[][][] _digests;
   int _size;
   public static final int RIBBON_UI_MODE = 0;
   public static final int WIZARD_UI_MODE = 1;
   private static final String WAP_CID = "WAP";
   private static final int AUTOSTART_TIME_DELAY = 5000;
   static final long APP_GUID = -5149637025418560721L;
   static final String BIS_LAUNCH_MODULE_NAME = "net_rim_bis_launch";
   static final String BIS_CLIENT_MODULE_NAME = "net_rim_bis_client";
   static String _version;
   public static BISClientConfigRecord _configRecord;
   private static ApplicationDescriptor _wizardDescriptor = (ApplicationDescriptor)(new Object(
      BISLaunch._baseDescriptor, "net_rim_bis_launch", new String[]{"WIZARD"}
   ));
   private static ApplicationDescriptor _ribbonDescriptor = (ApplicationDescriptor)(new Object(
      BISLaunch._baseDescriptor, "net_rim_bis_launch", new String[]{"RIBBON"}
   ));
   private static ApplicationDescriptor _baseDescriptor = CodeModuleManager.getApplicationDescriptors(CodeModuleManager.getModuleHandle("net_rim_bis_client"))[0];

   protected static final ApplicationDescriptor getApplicationDescriptor(boolean isSetupWizard) {
      ApplicationDescriptor applicationDescriptor = _ribbonDescriptor;
      if (isSetupWizard) {
         applicationDescriptor = _wizardDescriptor;
      }

      return applicationDescriptor;
   }

   protected static final void destroyExistingApp(ApplicationDescriptor applicationDescriptor) {
      int processId = ApplicationManager.getApplicationManager().getProcessId(applicationDescriptor);
      if (processId != -1) {
         Process process = Process.getProcess(processId);
         process.destroy();
      }
   }

   public static final void main(String[] args) {
      System.out.println("STARTING BISLaunch...");
      destroyExistingApp(_wizardDescriptor);
      destroyExistingApp(_ribbonDescriptor);
      if (args != null && args.length != 0) {
         BISLaunch bisLaunch = new BISLaunch();
         if ("WIZARD".equals(args[0])) {
            bisLaunch._uiMode = 1;
         } else {
            bisLaunch._uiMode = 0;
         }

         bisLaunch.initialize();
         bisLaunch.enterEventDispatcher();
      } else {
         processServiceBook();
         Proxy.getInstance().addGlobalEventListener(new BISLaunch$BISClientSBListener(null));
      }

      System.out.println("STOPPING BISUpdate...");
   }

   private final void initialize() {
      _configRecord = BISClientConfigRecord.getBISClientConfigRecord();
      if (_configRecord == null) {
         throw new Object("No service record");
      }

      Controller.initialize();
      this.pushScreen(new InitializationScreen());
   }

   public final void checkUpdates() {
      Runnable checkUpdates = new BISLaunch$1(this);
      Runnable navigation = new BISLaunch$2(this);
      String message = net.rim.device.apps.internal.bis.launch.resource.ApplicationResources.getString(16);
      Controller.run(checkUpdates, navigation, message);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void launchBISClient() {
      ApplicationDescriptor applicationDescriptor = getApplicationDescriptor(this._uiMode == 1);

      try {
         ApplicationManager.getApplicationManager().runApplication(applicationDescriptor);
      } catch (Throwable var4) {
         System.out.println(e.getMessage());
         return;
      }
   }

   public final void reboot() {
      InternalServices.initiateReset("BIS Update");
   }

   public final void exit() {
      System.exit(-1);
   }

   static final void autoLaunch() {
      if (RibbonLauncher.getInstance().getRegisteredAction("net_rim_bis_launch") == null) {
         try {
            ApplicationDescriptor ribbonModeDescriptor = BISClientInvoke.getRibbonInvokeDescriptor();
            ApplicationManager.getApplicationManager().scheduleApplication(ribbonModeDescriptor, System.currentTimeMillis() + 5000, true);
         } finally {
            return;
         }
      }
   }

   static final void addRibbonIcon() {
      if (RibbonLauncher.getInstance().getRegisteredAction("net_rim_bis_launch") == null) {
         ApplicationDescriptor appDescriptor = BISClientInvoke.getRibbonInvokeDescriptor();
         ApplicationEntryPoint entryPoint = (ApplicationEntryPoint)(new Object(appDescriptor));
         entryPoint.set(3, getDescription());
         entryPoint.set(4, getRibbonIcon());
         RibbonLauncher.getInstance().registerAction("net_rim_bis_launch", entryPoint);
         ApplicationRegistry.getApplicationRegistry().put(-5149637025418560721L, appDescriptor);
      }
   }

   static final void removeRibbonIcon() {
      if (RibbonLauncher.getInstance().getRegisteredAction("net_rim_bis_launch") != null) {
         RibbonLauncher.getInstance().unregisterAction("net_rim_bis_launch");
         ApplicationRegistry.getApplicationRegistry().remove(-5149637025418560721L);
      }
   }

   static final String getDescription() {
      Locale locale = Locale.getDefault();
      return BISClientConfigRecord.getBISClientConfigRecord().getRibbonTitle(locale.getLanguage(), locale.getCountry());
   }

   static final Bitmap getRibbonIcon() {
      Bitmap ribbonIcon = null;
      int brandingIcon = BISClientConfigRecord.getBISClientConfigRecord().getBrandingIconIndex();
      if (brandingIcon != -1) {
         byte[] iconFromNVRam = Branding.getData(16640 + brandingIcon);
         if (iconFromNVRam != null) {
            EncodedImage image = EncodedImage.createEncodedImage(iconFromNVRam, 0, iconFromNVRam.length, "image/png");
            return image != null ? image.getBitmap() : null;
         }
      } else {
         ribbonIcon = ThemeManager.getActiveTheme().getImage("net_rim_bb_browser_daemon.prov.bwc").getBitmap();
      }

      return ribbonIcon;
   }

   protected static final void processServiceBook() {
      if (BISClientInvoke.canBeInvoked()) {
         BISClientConfigRecord configRecord = BISClientConfigRecord.getBISClientConfigRecord();
         if (configRecord.getShowOnRibbon()) {
            addRibbonIcon();
         } else {
            removeRibbonIcon();
         }

         if (configRecord.getAutoStart()) {
            autoLaunch();
            return;
         }
      } else {
         removeRibbonIcon();
      }
   }
}
