package net.rim.device.api.ui;

import net.rim.device.api.system.ModalEventThread;

final class UiModalEventThread extends ModalEventThread {
   private Screen _screen;

   public UiModalEventThread(Screen screen) {
      this._screen = screen;
   }

   public final Screen getScreen() {
      return this._screen;
   }

   @Override
   protected final boolean shouldExit() {
      return this._screen.getUiEngine() == null;
   }
}
