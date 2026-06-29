package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.internal.ui.component.PopupDialog;

final class NetworkOrStkMsgDlg extends PopupDialog implements Runnable {
   private int[] _closeReasons;
   private String[] _options;
   int _type;
   private boolean _closeOnAnyKey;
   private String _msg2;
   static final int CALL_SETUP_DIALOG = 0;
   static final int USSD_DISPLAY_DIALOG = 1;
   static final int SS_NOTIFICATION_DIALOG = 2;

   NetworkOrStkMsgDlg(String msg1, String msg2, int type, String[] options, int[] closeReasons) {
      this(msg1, msg2, type, options, closeReasons, false);
   }

   NetworkOrStkMsgDlg(String msg1, String msg2, int type, String[] options, int[] closeReasons, boolean closeOnAnyKey) {
      super(new DialogFieldManager(), 33554432);
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setMessage(new RichTextField(msg1, 36028797018963968L));
      this._closeReasons = closeReasons;
      this._type = type;
      this._options = options;
      this._closeOnAnyKey = closeOnAnyKey;
      this._msg2 = msg2;
      if (options != null && options.length > 0) {
         for (int i = 0; i < options.length; i++) {
            dfm.addCustomField(new ButtonField(options[i], 0));
         }
      }
   }

   final String getMsg2() {
      return this._msg2;
   }

   @Override
   public final void run() {
      this.close(0);
   }

   private final void acceptInput() {
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof ButtonField) {
         int index = field.getIndex();
         if (this._closeReasons != null && this._closeReasons.length > 0) {
            this.close(this._closeReasons[index]);
            return;
         }

         this.close(0);
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      this.acceptInput();
      return true;
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (this._options == null) {
         this.close(0);
         return true;
      } else if (c == '\n') {
         this.acceptInput();
         return true;
      } else if (this._closeOnAnyKey) {
         this.close(0);
         return true;
      } else {
         return super.keyChar(c, status, time);
      }
   }
}
