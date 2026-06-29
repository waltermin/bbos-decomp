package net.rim.wica.runtime.lifecycle.internal;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;

class RuntimeDialog extends Dialog {
   private Action[] _actions;

   RuntimeDialog(String message, Action[] actions, int[] values, int defaultAction, Bitmap bitmap, long style) {
      super(message, actions, values, defaultAction, bitmap, style);
      this._actions = actions;
   }

   Action getAction(int index) {
      return this._actions[index];
   }
}
