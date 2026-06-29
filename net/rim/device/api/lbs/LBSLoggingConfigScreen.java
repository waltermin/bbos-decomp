package net.rim.device.api.lbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

final class LBSLoggingConfigScreen extends MainScreen {
   LBSLoggingConfigScreen() {
      this.add(new LabelField("LBS Logging Configuration"));
      LoggingFlags flags = Logger.getInstance()._loggableFlags;
      this.add(new LBSLoggingConfigScreen$FlagSettingCheckboxField(this, "Protocol", flags, 32));
      Ui.getUiEngine().pushScreen(this);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(new LBSLoggingConfigScreen$1(this, "Open Event Log Viewer", 0, 0));
      menu.add(new LBSLoggingConfigScreen$2(this, "Check All Types", 1, 0));
      menu.add(new LBSLoggingConfigScreen$3(this, "Uncheck All Types", 2, 0));
   }

   private final void setStateForAllCheckboxes(boolean checked) {
      Manager mgr = this.getDelegate().getManager();

      for (int i = 0; i < mgr.getFieldCount(); i++) {
         Field f = mgr.getField(i);
         if (f instanceof LBSLoggingConfigScreen$FlagSettingCheckboxField) {
            LBSLoggingConfigScreen$FlagSettingCheckboxField c = (LBSLoggingConfigScreen$FlagSettingCheckboxField)f;
            c.setChecked(checked);
         }
      }
   }

   @Override
   public final void save() {
      Logger.commit();
   }
}
