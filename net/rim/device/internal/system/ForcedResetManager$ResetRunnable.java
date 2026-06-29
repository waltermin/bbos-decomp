package net.rim.device.internal.system;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

class ForcedResetManager$ResetRunnable implements Runnable {
   int _warningNumber = 1;
   String _message;
   int _numResetWarnings;
   long _timeBetweenResetWarnings;
   boolean _delayedResetOption;
   boolean _dialogShouldTimeout;
   boolean _dialogCurrentlyDisplayed;
   int _id;

   ForcedResetManager$ResetRunnable(
      String message, int numResetWarnings, long timeBetweenResetWarnings, boolean delayedResetOption, boolean dialogShouldTimeout
   ) {
      this._message = message;
      this._numResetWarnings = numResetWarnings;
      this._timeBetweenResetWarnings = timeBetweenResetWarnings;
      this._delayedResetOption = delayedResetOption;
      this._dialogShouldTimeout = dialogShouldTimeout;
   }

   public void update(String message, int numResetWarnings, long timeBetweenResetWarnings, boolean delayedResetOption, boolean dialogShouldTimeout) {
      if (numResetWarnings < this._numResetWarnings) {
         if (numResetWarnings <= this._warningNumber) {
            this._numResetWarnings = this._warningNumber;
         } else {
            this._numResetWarnings = numResetWarnings;
         }
      }

      if (timeBetweenResetWarnings < this._timeBetweenResetWarnings) {
         this._timeBetweenResetWarnings = timeBetweenResetWarnings;
         this._message = message;
      }

      this._delayedResetOption &= delayedResetOption;
      this._dialogShouldTimeout |= dialogShouldTimeout;
      if (dialogShouldTimeout) {
         this.enableDialogTimeout();
      }
   }

   public void enableDialogTimeout() {
      if (this._id == 0 && this._dialogCurrentlyDisplayed) {
         this._id = Proxy.getInstance().invokeLaterInternal(new ForcedResetManager$ResetRunnable$DelayedResetRunnable(this, null, 1), 600000, false);
      }
   }

   @Override
   public void run() {
      StringBuffer buffer = new StringBuffer(this._message);
      buffer.append(CommonResource.getString(10087));
      String finalMessage = MessageFormat.format(buffer.toString(), new String[]{String.valueOf(this._warningNumber), String.valueOf(this._numResetWarnings)});
      String[] allChoices = CommonResource.getStringArray(this._delayedResetOption && this._warningNumber > 1 ? 10100 : 10085);
      int initialChoice = 1;
      String[] actualChoices;
      byte var7;
      if (this._warningNumber == this._numResetWarnings) {
         if (this._delayedResetOption) {
            actualChoices = new String[]{allChoices[0], allChoices[1]};
            var7 = 1;
         } else {
            actualChoices = new String[]{allChoices[0]};
            var7 = 0;
         }
      } else {
         actualChoices = allChoices;
         var7 = 1;
      }

      SimpleChoiceDialog dialog = new SimpleChoiceDialog(finalMessage, actualChoices, var7, null, 134217728);
      this._id = 0;
      if (this._dialogShouldTimeout) {
         this._id = Proxy.getInstance().invokeLaterInternal(new ForcedResetManager$ResetRunnable$DelayedResetRunnable(this, this._message, 2), 600000, false);
      }

      this._dialogCurrentlyDisplayed = true;
      BackgroundDialog.show(dialog);
      this._dialogCurrentlyDisplayed = false;
      if (this._dialogShouldTimeout && this._id != 0) {
         Proxy.getInstance().cancelInvokeLater(this._id);
         this._id = 0;
      }

      System.out.println("Reset dialog dismissed.");
      if ((dialog.getCloseReason() == -1 || dialog.getSelectedIndex() == allChoices.length - 1) && this._warningNumber < this._numResetWarnings) {
         this._warningNumber++;
         Proxy.getInstance().invokeLater(this, this._timeBetweenResetWarnings, false);
      } else if (this._delayedResetOption && dialog.getSelectedIndex() == 1) {
         Proxy.getInstance().invokeLater(new ForcedResetManager$ResetRunnable$DelayedResetRunnable(this, this._message, 3), 60000, false);
      } else {
         InternalServices.initiateReset("FRM SCHa");
      }
   }
}
