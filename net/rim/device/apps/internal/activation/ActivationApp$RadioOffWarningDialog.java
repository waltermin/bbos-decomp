package net.rim.device.apps.internal.activation;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;

final class ActivationApp$RadioOffWarningDialog extends Dialog {
   ActivationApp$RadioOffWarningDialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap, long style) {
      super(message, choices, values, defaultChoice, bitmap, style);
   }

   @Override
   public final void inHolster() {
   }
}
