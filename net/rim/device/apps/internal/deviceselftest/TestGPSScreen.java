package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;

final class TestGPSScreen extends TestScreenBase {
   MenuItem _stopFixMenuItem;
   MenuItem _startFixMenuItem;
   EditField _status;
   EditField _info;
   TestGPS handler;
   boolean isFixing;

   TestGPSScreen(TestGPS _handler) {
      this.handler = _handler;
      super._title = new LabelField(DeviceSelfTestResources.getString(106), 1152921504606846976L);
      super._input = new LabelField(DeviceSelfTestResources.getString(107), 1152921504606846976L);
      super._output = new LabelField(DeviceSelfTestResources.getString(108), 1152921504606846976L);
      this.setTitle(super._title);
      this.add(super._input);
      this.add(new SeparatorField());
      this.add(super._output);
      this._status = new EditField(DeviceSelfTestResources.getString(138), DeviceSelfTestResources.getString(142));
      this._status.setEditable(false);
      this.add(new SeparatorField());
      this.add(this._status);
      this.add(new SeparatorField());
      this._info = new EditField(DeviceSelfTestResources.getString(139), "");
      this._info.setEditable(false);
      this.add(this._info);
      this.setupMenuItems();
      this.isFixing = false;
   }

   @Override
   protected final void setupMenuItems() {
      super.setupMenuItems();
      this._startFixMenuItem = new TestGPSScreen$1(this, DeviceSelfTestResources.getString(109), 0, 0);
      this._stopFixMenuItem = new TestGPSScreen$2(this, DeviceSelfTestResources.getString(110), 0, 0);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      if (!this.isFixing) {
         menu.add(this._startFixMenuItem);
      }

      super.makeMenu(menu, instance);
      super.isMenuOn = true;
   }

   protected final void doStartFix() {
      this._status.setText(DeviceSelfTestResources.getString(140));
      new TestGPSScreen$GPSFixThread(this).start();
      super.isMenuOn = false;
      this.isFixing = true;
   }

   protected final void doStopFix() {
      this._status.setText(DeviceSelfTestResources.getString(140));
      this.isFixing = false;
      super.isMenuOn = false;
   }

   @Override
   protected final void doNext() {
      Application.getApplication().invokeLater(new TestGPSScreen$3(this));
   }

   @Override
   protected final void cleanup() {
      super.app.removeKeyListener(super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }
}
