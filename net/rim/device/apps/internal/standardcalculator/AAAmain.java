package net.rim.device.apps.internal.standardcalculator;

import net.rim.device.api.ui.UiApplication;

public final class AAAmain extends UiApplication {
   AAAmain() {
      this.enableKeyUpEvents(true);
      CalculatorScreen screen = new CalculatorScreen();
      this.pushScreen(screen);
   }

   public static final void main(String[] args) {
      new AAAmain().enterEventDispatcher();
   }
}
