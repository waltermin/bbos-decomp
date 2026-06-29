package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;

final class TestMicrophoneScreen extends TestScreenBase {
   TestMicrophone handler;
   int microphoneType;
   EditField _state;

   TestMicrophoneScreen(TestMicrophone _handler, int _microphoneType) {
      this.handler = _handler;
      this.microphoneType = _microphoneType;
      if (this.microphoneType == 0) {
         super._title = new LabelField(DeviceSelfTestResources.getString(89), 1152921504606846976L);
         super._input = new LabelField(DeviceSelfTestResources.getString(92), 1152921504606846976L);
         super._output = new LabelField(DeviceSelfTestResources.getString(95), 1152921504606846976L);
      } else if (this.microphoneType == 1) {
         super._title = new LabelField(DeviceSelfTestResources.getString(90), 1152921504606846976L);
         super._input = new LabelField(DeviceSelfTestResources.getString(93), 1152921504606846976L);
         super._output = new LabelField(DeviceSelfTestResources.getString(96), 1152921504606846976L);
      } else if (this.microphoneType == 2) {
         super._title = new LabelField(DeviceSelfTestResources.getString(91), 1152921504606846976L);
         super._input = new LabelField(DeviceSelfTestResources.getString(94), 1152921504606846976L);
         super._output = new LabelField(DeviceSelfTestResources.getString(97), 1152921504606846976L);
      }

      this._state = new EditField(DeviceSelfTestResources.getString(138), DeviceSelfTestResources.getString(134));
      this._state.setEditable(false);
      this.setTitle(super._title);
      this.add(super._input);
      this.add(new SeparatorField());
      this.add(super._output);
      this.add(new SeparatorField());
      this.add(this._state);
      this.setupMenuItems();
   }

   final void updateState(String event) {
      synchronized (Application.getEventLock()) {
         this._state.setText(event);
      }
   }

   @Override
   protected final void doNext() {
      Application.getApplication().invokeLater(new TestMicrophoneScreen$1(this));
   }

   @Override
   protected final void cleanup() {
      TestMicrophone.isSkipped = true;
      super.app.removeKeyListener(super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }
}
