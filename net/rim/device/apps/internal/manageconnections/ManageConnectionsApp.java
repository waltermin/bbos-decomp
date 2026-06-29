package net.rim.device.apps.internal.manageconnections;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.internal.system.RadioInternal;

final class ManageConnectionsApp extends UiApplication {
   boolean _acceptsForeground = false;
   protected static final long RESTORE_STATE_KEY = -8312528208360996369L;
   protected static final int RESTORE_POSSIBLE_BIT = 1;
   protected static final int RESTORE_WIRELESS_BIT = 2;
   protected static final int RESTORE_WIFI_BIT = 4;
   protected static final int RESTORE_BLUETOOTH_BIT = 8;
   protected static final int BAT_TOO_LOW_PROMPT_TIME = 8000;
   private static final int WORLD_PHONE_WAFS = 3;
   private static final boolean CDMA_GSM_WORLD_PHONE = (RadioInfo.getSupportedWAFs() & 3) == 3;
   static final int CELL_WAFS = RadioInfo.getSupportedWAFs() & -5;
   static final int CELL_RADIOS = RadioInternal.getSupportedRadios() & -5;

   public static final void main(String[] args) {
      if (args != null && args.length > 0 && args[0].equals("init")) {
         ApplicationEntryPoint.setPropertyOverride("net_rim_bb_manage_connections.Manage Connections", 7, Boolean.FALSE);
      } else {
         new ManageConnectionsApp().enterEventDispatcher();
      }
   }

   private ManageConnectionsApp() {
      this.pushGlobalScreen(new ConnectionsPopupScreen(), 100, 2);
   }

   @Override
   protected final boolean acceptsForeground() {
      return this._acceptsForeground;
   }

   protected static final void informUser(int resourceID) {
      ResourceBundleFamily rbf = ResourceBundle.getBundle(-348546850453906601L, "net.rim.device.apps.internal.manageconnections.ManageConnections");
      Dialog d = new Dialog(0, rbf.getString(resourceID), 0, null, 0);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_information"));
      Application app = Application.getApplication();
      if (app instanceof UiApplication) {
         UiApplication uiApp = (UiApplication)app;
         uiApp.pushGlobalScreen(d, 100, 5);
      }
   }

   static final int getEnabledRadios() {
      return CDMA_GSM_WORLD_PHONE ? RadioInternal.getEnabledRadios() : CELL_RADIOS;
   }

   static {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      Object o = applicationRegistry.get(1185883946270450222L);
      if (o == null) {
         ManageConnectionsApp$PhoneRestoreConnectionsRunnable rcr = new ManageConnectionsApp$PhoneRestoreConnectionsRunnable();
         applicationRegistry.put(1185883946270450222L, rcr);
      }
   }
}
