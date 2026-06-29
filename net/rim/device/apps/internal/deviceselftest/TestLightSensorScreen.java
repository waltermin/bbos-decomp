package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;

final class TestLightSensorScreen extends TestScreenBase {
   EditField _reading;

   TestLightSensorScreen() {
      super._title = (LabelField)(new Object(DeviceSelfTestResources.getString(114), 1152921504606846976L));
      super._input = (LabelField)(new Object(DeviceSelfTestResources.getString(115), 1152921504606846976L));
      super._output = (LabelField)(new Object(DeviceSelfTestResources.getString(116), 1152921504606846976L));
      this._reading = (EditField)(new Object(DeviceSelfTestResources.getString(117), ""));
      this._reading.setEditable(false);
      this.setTitle(super._title);
      this.add(super._input);
      this.add((Field)(new Object()));
      this.add(super._output);
      this.add((Field)(new Object()));
      this.add(this._reading);
      this.setupMenuItems();
   }

   @Override
   protected final void doNext() {
      Application.getApplication().invokeLater(new TestLightSensorScreen$1(this));
   }

   @Override
   protected final void cleanup() {
      super.app.removeKeyListener(super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }
}
