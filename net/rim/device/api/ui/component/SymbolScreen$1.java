package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Field;

class SymbolScreen$1 implements Runnable {
   private final Field val$field;

   SymbolScreen$1(Field _1) {
      this.val$field = _1;
   }

   @Override
   public void run() {
      if (!SymbolScreen.getSymbolScreen().isDisplayed()) {
         SymbolScreen.getSymbolScreen().doModal(this.val$field);
      }
   }
}
