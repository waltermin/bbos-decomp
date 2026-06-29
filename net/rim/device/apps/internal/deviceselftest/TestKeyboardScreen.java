package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;

final class TestKeyboardScreen extends TestScreenBase {
   EditField _keyString;

   TestKeyboardScreen() {
      super._title = (LabelField)(new Object(DeviceSelfTestResources.getString(98), 1152921504606846976L));
      super._input = (LabelField)(new Object(DeviceSelfTestResources.getString(99), 1152921504606846976L));
      super._output = (LabelField)(new Object(DeviceSelfTestResources.getString(100), 1152921504606846976L));
      this.setTitle(super._title);
      this.add(super._input);
      this.add((Field)(new Object()));
      this.add(super._output);
      this.add((Field)(new Object()));
      this._keyString = (EditField)(new Object("Pressed Keys : ", null));
      this._keyString.setEditable(false);
      this.add(this._keyString);
      this.setupMenuItems();
   }

   final void updateKeyString(StringBuffer keyString) {
      synchronized (Application.getEventLock()) {
         this._keyString.setText(keyString.toString());
      }
   }

   @Override
   protected final void doNext() {
      Application.getApplication().invokeLater(new TestKeyboardScreen$1(this));
   }

   @Override
   protected final void cleanup() {
      super.app.removeKeyListener(super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }
}
