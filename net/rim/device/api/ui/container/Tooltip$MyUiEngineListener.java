package net.rim.device.api.ui.container;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiEngineListener;

class Tooltip$MyUiEngineListener extends UiEngineListener implements FocusChangeListener {
   private Screen _current;

   @Override
   public void focusChanged(Field field, int eventType) {
      if (eventType == 1 && field instanceof Tooltip$TooltipProvider) {
         Tooltip$TooltipPollingThread.reset();
      }
   }

   @Override
   public void onFocus(Screen previous, Screen screen) {
      if (this._current != null) {
         this._current.removeFocusChangeListener(this);
         this._current = null;
      }

      if (screen != null) {
         screen.addFocusChangeListener(this);
         this._current = screen;
      }
   }
}
