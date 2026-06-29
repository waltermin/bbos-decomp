package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.component.Dialog;

public class RunnableDialog implements Runnable {
   private int _whichDialog;
   private Dialog _dialog;
   private int _type;
   private String _message;
   private Object[] _choices;
   private int[] _values;
   private int _defaultChoice = Integer.MIN_VALUE;
   private int _result = -2;
   public static final int ALERT_DIALOG;
   public static final int INFORM_DIALOG;
   public static final int ASK_DIALOG;

   public int getResult() {
      return this._result;
   }

   @Override
   public void run() {
      switch (this._whichDialog) {
         case -1:
         default:
            this._result = this._dialog.doModal();
            return;
         case 0:
            Dialog.alert(this._message);
            return;
         case 1:
            Dialog.inform(this._message);
            return;
         case 2:
            if (this._message == null || this._message.length() == 0) {
               this._result = Dialog.ask(this._type);
               return;
            } else if (this._choices == null) {
               this._result = this._defaultChoice == Integer.MIN_VALUE
                  ? Dialog.ask(this._type, this._message)
                  : Dialog.ask(this._type, this._message, this._defaultChoice);
               return;
            } else if (this._values == null) {
               this._result = Dialog.ask(this._message, this._choices, this._defaultChoice);
               return;
            } else {
               this._result = Dialog.ask(this._message, this._choices, this._values, this._defaultChoice);
            }
         case -2:
      }
   }

   public RunnableDialog(int type, String message, Object[] choices, int[] values, int defaultChoice) {
      this(2, type, message, choices, values, defaultChoice);
   }

   public RunnableDialog(int runnableDialogType, int type, String message, Object[] choices, int[] values, int defaultChoice) {
      this._whichDialog = runnableDialogType;
      this._type = type;
      this._message = message;
      this._choices = choices;
      this._values = values;
      this._defaultChoice = defaultChoice;
   }

   public RunnableDialog(String message, int dialogType) {
      this._message = message;
      this._whichDialog = dialogType;
   }

   public RunnableDialog(Dialog dialog) {
      this._dialog = dialog;
      this._whichDialog = -1;
   }
}
