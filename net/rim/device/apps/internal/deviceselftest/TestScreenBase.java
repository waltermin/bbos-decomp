package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

class TestScreenBase extends MainScreen {
   protected LabelField _title;
   protected LabelField _input;
   protected LabelField _output;
   protected MenuItem _nextMenuItem;
   protected MenuItem _skipMenuItem;
   protected MenuItem _skipAllMenuItem;
   protected DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
   protected boolean isMenuOn = false;

   protected void setupMenuItems() {
      this._nextMenuItem = new TestScreenBase$1(this, DeviceSelfTestResources.getString(13), 0, 0);
      this._skipMenuItem = new TestScreenBase$2(this, DeviceSelfTestResources.getString(14), 20000, 20000);
      this._skipAllMenuItem = new TestScreenBase$3(this, DeviceSelfTestResources.getString(15), 0, 0);
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._nextMenuItem);
      menu.add(this._skipMenuItem);
      menu.add(this._skipAllMenuItem);
      this.isMenuOn = true;
   }

   protected void doNext() {
   }

   protected void cleanup() {
   }

   protected void doSkip() {
      Application.getApplication().invokeLater(new TestScreenBase$4(this));
   }

   protected void doSkipAll() {
      Application.getApplication().invokeLater(new TestScreenBase$5(this));
   }

   @Override
   public boolean onClose() {
      this.doSkipAll();
      return true;
   }
}
