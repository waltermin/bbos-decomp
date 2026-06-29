package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;

final class TestVibratorScreen extends TestScreenBase {
   TestVibratorScreen() {
      super._title = (LabelField)(new Object(DeviceSelfTestResources.getString(52), 1152921504606846976L));
      super._input = (LabelField)(new Object(DeviceSelfTestResources.getString(53), 1152921504606846976L));
      super._output = (LabelField)(new Object(DeviceSelfTestResources.getString(54), 1152921504606846976L));
      this.setTitle(super._title);
      this.add(super._input);
      this.add((Field)(new Object()));
      this.add(super._output);
      this.setupMenuItems();
   }

   @Override
   protected final void doNext() {
      Application.getApplication().invokeLater(new TestVibratorScreen$1(this));
   }

   @Override
   protected final void cleanup() {
      super.app.removeKeyListener(super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }
}
