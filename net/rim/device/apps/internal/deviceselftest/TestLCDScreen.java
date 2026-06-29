package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;

final class TestLCDScreen extends TestScreenBase {
   TestLCDScreen() {
      super._title = new LabelField(DeviceSelfTestResources.getString(71), 1152921504606846976L);
      super._input = new LabelField(DeviceSelfTestResources.getString(69), 1152921504606846976L);
      super._output = new LabelField(DeviceSelfTestResources.getString(70), 1152921504606846976L);
      this.setTitle(super._title);
      this.add(super._input);
      this.add(new SeparatorField());
      this.add(super._output);
      this.setupMenuItems();
   }

   @Override
   protected final void doNext() {
      Application.getApplication().invokeLater(new TestLCDScreen$1(this));
   }

   @Override
   protected final void cleanup() {
      super.app.removeKeyListener(super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }
}
