package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.ui.AppsMainScreen;

class BasicWizardPage$WizardMainScreen extends AppsMainScreen {
   private final BasicWizardPage this$0;

   BasicWizardPage$WizardMainScreen(BasicWizardPage _1, long style) {
      super(style);
      this.this$0 = _1;
      this.setDefaultClose(false);
   }

   @Override
   public void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      this.this$0.makeWizardMenu(menu, instance);
   }

   @Override
   protected boolean invokeAction(int action) {
      boolean handled = this.this$0.invokeAction(action);
      if (!handled) {
         handled = super.invokeAction(action);
      }

      return handled;
   }

   @Override
   protected boolean handleSendKey() {
      return false;
   }

   @Override
   protected boolean handleEndKey() {
      return WizardKeyEventManager.processKeyEvent(513, Keypad.keycode('\u0012', 0), 0, this.this$0._warnOnCloseOrHotKey) ? true : super.handleEndKey();
   }
}
