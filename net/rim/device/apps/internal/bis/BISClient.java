package net.rim.device.apps.internal.bis;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.bis.api.ui.ShutdownListener;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.config.UINavigationConfig;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.ui.InitializationScreen;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;
import net.rim.vm.Process;

public final class BISClient extends UiApplication {
   private Screen _startupScreen;
   private int _uiMode;
   private Vector _shutdownListeners = (Vector)(new Object());
   public static final int RIBBON_UI_MODE;
   public static final int WIZARD_UI_MODE;
   private static final String WAP_CID;
   private static final int AUTOSTART_TIME_DELAY;
   static final long APP_GUID;
   static final String BIS_CLIENT_MODULE_NAME;
   static String _version;

   public final void registerShutdownListeners(Vector shutdownListeners) {
      for (int i = 0; i < shutdownListeners.size(); i++) {
         ShutdownListener listener = (ShutdownListener)shutdownListeners.elementAt(i);
         this._shutdownListeners.addElement(listener);
      }
   }

   public final void shutdown() {
      for (int i = 0; i < this._shutdownListeners.size(); i++) {
         ShutdownListener listener = (ShutdownListener)this._shutdownListeners.elementAt(i);
         listener.shutdown();
      }

      ClientSessionState.shutdown();
   }

   private final void initialize() {
      BISEventLogger.register();
      BISClientConfigRecord configRecord = BISClientConfigRecord.getBISClientConfigRecord();
      if (configRecord == null) {
         throw new Object("No service record");
      }

      int pid = this.getProcessId();
      ApplicationProcess process = (ApplicationProcess)Process.getProcess(pid);
      Runnable shutdownRunnable = new BISClient$1(this);
      process.addCleanupRunnable(shutdownRunnable);
      ClientSessionState.initialize();
      ObjectRegistry.getInstance().register("configRecord", configRecord);
      boolean useWapGateway = "WAP".equalsIgnoreCase(configRecord.getTransportCID());
      ObjectRegistry.getInstance()
         .register("restClient", new RestClient(configRecord.getServerURL(), configRecord.getTransportUID(), configRecord.getServiceTimeout(), useWapGateway));
      ClientPersistentState.initialize();
      if (ClientPersistentState.getInstance().getLocale() == null) {
         Locale defaultInputLocale = Locale.getDefaultInputForSystem();
         String localeCode = Common.getLocaleCode(defaultInputLocale);
         if (!ApplicationResources.isAppLocalePresent(localeCode)) {
            defaultInputLocale = Common.getLocale("en");
         }

         ClientPersistentState.getInstance().setLocale(defaultInputLocale);
      }

      int handle = CodeModuleManager.getModuleHandle("net_rim_bis_client");
      _version = CodeModuleManager.getModuleVersion(handle);
   }

   private final void configureScreens() {
      Vector shutdownListeners = UINavigationConfig.loadStandardConfig(this._uiMode);
      this.registerShutdownListeners(shutdownListeners);
   }

   private final void initializeDisplay() {
      this._startupScreen = new InitializationScreen();
      this.pushScreen(this._startupScreen);
   }

   public final int getUiMode() {
      return this._uiMode;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void main(String[] args) {
      if (args != null && args.length != 0) {
         boolean ribbonLaunch = "RIBBON".equals(args[0]);
         BISClient client = new BISClient();
         client._uiMode = ribbonLaunch ? 0 : 1;

         try {
            client.initialize();
            client.configureScreens();
            client.initializeDisplay();
            client.enterEventDispatcher();
         } catch (Throwable var5) {
            System.err
               .println(((StringBuffer)(new Object("Shutting down due to "))).append(e.getClass().getName()).append(" : ").append(e.getMessage()).toString());
            System.exit(0);
            return;
         }
      } else {
         removeRibbonIcon();
      }
   }

   private static final void removeRibbonIcon() {
      if (RibbonLauncher.getInstance().getRegisteredAction("net_rim_bis_client") != null) {
         RibbonLauncher.getInstance().unregisterAction("net_rim_bis_client");
      }
   }
}
