package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.LED;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;

final class TestLEDScreen extends TestScreenBase {
   TestLEDScreen() {
      super._title = new LabelField(DeviceSelfTestResources.getString(17), 1152921504606846976L);
      super._input = new LabelField(DeviceSelfTestResources.getString(16), 1152921504606846976L);
      super._output = new LabelField(DeviceSelfTestResources.getString(26), 1152921504606846976L);
      this.setTitle(super._title);
      this.add(super._input);
      this.add(new SeparatorField());
      this.add(super._output);
      this.setupMenuItems();
   }

   @Override
   protected final void doNext() {
      Application.getApplication().invokeLater(new TestLEDScreen$1(this));
   }

   @Override
   protected final void cleanup() {
      LED.setState(0, 0);
      super.app.removeKeyListener(super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }
}
