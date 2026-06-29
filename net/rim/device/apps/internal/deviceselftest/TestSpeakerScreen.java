package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;

final class TestSpeakerScreen extends TestScreenBase {
   int speakerType;

   TestSpeakerScreen(int _speakerType) {
      this.speakerType = _speakerType;
      if (this.speakerType == 0) {
         super._title = (LabelField)(new Object(DeviceSelfTestResources.getString(77), 1152921504606846976L));
         super._input = (LabelField)(new Object(DeviceSelfTestResources.getString(79), 1152921504606846976L));
         super._output = (LabelField)(new Object(DeviceSelfTestResources.getString(78), 1152921504606846976L));
      } else if (this.speakerType == 1) {
         super._title = (LabelField)(new Object(DeviceSelfTestResources.getString(80), 1152921504606846976L));
         super._input = (LabelField)(new Object(DeviceSelfTestResources.getString(81), 1152921504606846976L));
         super._output = (LabelField)(new Object(DeviceSelfTestResources.getString(82), 1152921504606846976L));
      } else if (this.speakerType == 2) {
         super._title = (LabelField)(new Object(DeviceSelfTestResources.getString(83), 1152921504606846976L));
         super._input = (LabelField)(new Object(DeviceSelfTestResources.getString(84), 1152921504606846976L));
         super._output = (LabelField)(new Object(DeviceSelfTestResources.getString(85), 1152921504606846976L));
      } else if (this.speakerType == 3) {
         super._title = (LabelField)(new Object(DeviceSelfTestResources.getString(86), 1152921504606846976L));
         super._input = (LabelField)(new Object(DeviceSelfTestResources.getString(87), 1152921504606846976L));
         super._output = (LabelField)(new Object(DeviceSelfTestResources.getString(88), 1152921504606846976L));
      }

      this.setTitle(super._title);
      this.add(super._input);
      this.add((Field)(new Object()));
      this.add(super._output);
      this.setupMenuItems();
   }

   @Override
   protected final void doNext() {
      Application.getApplication().invokeLater(new TestSpeakerScreen$1(this));
   }

   @Override
   protected final void cleanup() {
      super.app.removeKeyListener(super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
      AudioRouter.getInstance().resetSink();
   }
}
