package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

final class LocationServicesOptionsItem$AgpsConfigScreen extends MainScreen {
   private String suplServerUri;
   private PersistentObject _po;
   private Object[] _agpsConfig;
   private final long AGPS_CONFIG_KEY;
   private LabelField _title;
   private EditField _suplSvr;
   private MenuItem _saveMenuItem;
   private final LocationServicesOptionsItem this$0;

   LocationServicesOptionsItem$AgpsConfigScreen(LocationServicesOptionsItem _1) {
      this.this$0 = _1;
      this.AGPS_CONFIG_KEY = -5700456430655177747L;
      this.setupMenuItems();
      this._po = PersistentStore.getPersistentObject(-5700456430655177747L);
      this._agpsConfig = (Object[])this._po.getContents();
      if (this._agpsConfig == null) {
         this.suplServerUri = "";
      } else {
         this.suplServerUri = PersistentContent.decodeString(this._agpsConfig[0]);
      }

      this._title = (LabelField)(new Object("AGPS Configuration Screen", 1152921504606846976L));
      this.setTitle(this._title);
      this._suplSvr = (EditField)(new Object("SUPL Server URI : ", this.suplServerUri));
      this.add(this._suplSvr);
   }

   private final void setupMenuItems() {
      this._saveMenuItem = new LocationServicesOptionsItem$AgpsConfigScreen$1(this, "Save", 0, 0);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._saveMenuItem);
   }

   @Override
   public final boolean onSave() {
      try {
         this.suplServerUri = this._suplSvr.getText().trim();
         this._suplSvr.setDirty(false);
         if (this._agpsConfig == null) {
            this._agpsConfig = new Object[1];
         }

         this._agpsConfig[0] = PersistentContent.encode(this.suplServerUri, false, true);
         synchronized (this._po) {
            this._po.setContents(this._agpsConfig, 51, false);
            this._po.forceCommit();
         }
      } finally {
         return true;
      }

      return true;
   }

   private final boolean changesMade() {
      return this._suplSvr.isDirty();
   }

   @Override
   public final boolean onClose() {
      if (this.changesMade()) {
         int response = Dialog.ask(1);
         if (response == 1) {
            this.onSave();
         } else if (response == -1) {
            return false;
         }
      }

      UiApplication.getUiApplication().popScreen(this);
      return true;
   }
}
