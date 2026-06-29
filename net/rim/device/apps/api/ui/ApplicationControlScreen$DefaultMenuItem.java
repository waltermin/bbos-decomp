package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.firewall.Firewall;

class ApplicationControlScreen$DefaultMenuItem extends MenuItem {
   int _item;
   private final ApplicationControlScreen this$0;

   public ApplicationControlScreen$DefaultMenuItem(ApplicationControlScreen _1, int item) {
      super(ApplicationControlScreen.DEFAULT_MENU_ITEMS[item], 300210, 100);
      this.this$0 = _1;
      this._item = item;
   }

   @Override
   public void run() {
      switch (this._item) {
         case 0:
         default:
            this.this$0.save();
            this.this$0.close();
            return;
         case 1:
            this.this$0.commitSettings(0);
            return;
         case 2:
            if (!this.this$0._policySettingPresent) {
               this.this$0.updateSelections(0);
               this.this$0.save();
            }

            for (int i = 0; i < this.this$0._moduleHandles.length; i++) {
               ApplicationControl.removeUserSetting(this.this$0._moduleHandles[i]);
            }

            this.this$0.getScreen().deleteAll();
            this.this$0._useDefaults = true;
            this.this$0.populate();
            this.this$0.setDirty(false);
            return;
         case 3:
            if (this.this$0._moduleHandle == 0) {
               this.this$0.save();
            }

            ApplicationControl.removeUserSettings(false);
            this.this$0.close();
            return;
         case 4:
            this.this$0.editDefaults();
            return;
         case 5:
            Firewall f = Firewall.getInstance();

            for (int i = 0; i < this.this$0._moduleHandles.length; i++) {
               f.reset(this.this$0._moduleHandles[i]);
               ApplicationControl.resetPrompts(this.this$0._moduleHandles[i]);
            }

            this.this$0.collectSettings();
            this.this$0.updateSelections();
            return;
         case 6:
            ApplicationControl.resetAllPrompts();
            Firewall.getInstance().reset();
            return;
         case 7:
            this.this$0._showingAll = true;
            this.this$0.getScreen().deleteAll();
            this.this$0.removeMenuItem(this.this$0.SHOW_ALL_MI);
            this.this$0.populate();
         case -1:
      }
   }
}
