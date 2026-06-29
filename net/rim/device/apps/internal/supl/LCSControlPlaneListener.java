package net.rim.device.apps.internal.supl;

import net.rim.device.api.gps.LCS;
import net.rim.device.api.gps.LCSListener;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;

public final class LCSControlPlaneListener implements LCSListener, DialogClosedListener {
   private Dialog dialog;
   private int defaultChoice;
   static ResourceBundle _rb = ResourceBundle.getBundle(-8422383104449549495L, "net.rim.device.apps.internal.supl.Supl");

   final synchronized void notifyAndVerify(int notificationType, String clientName) {
      this.defaultChoice = -1;
      int dialogType = 3;
      String resourceString;
      switch (notificationType) {
         case -1:
            return;
         case 0:
            dialogType = 0;
            this.defaultChoice = 0;
            resourceString = clientName + " " + _rb.getString(4);
            break;
         case 1:
         default:
            this.defaultChoice = 4;
            resourceString = clientName + " " + _rb.getString(5);
            break;
         case 2:
            this.defaultChoice = -1;
            resourceString = clientName + " " + _rb.getString(5);
      }

      this.dialog = new Dialog(dialogType, resourceString, this.defaultChoice, null, 0);
      this.dialog.setDialogClosedListener(this);
      Ui.getUiEngine().pushGlobalScreen(this.dialog, 50, 2);
   }

   final synchronized void notifyAndVerify(int notificationType) {
      this.notifyAndVerify(notificationType, _rb.getString(6));
   }

   @Override
   public final void notificationRequest(int type, int length) {
      if (length > 0) {
         byte[] clientName = new byte[length];
         if (LCS.getLCSClientName(clientName)) {
            this.notifyAndVerify(type, _rb.getString(7) + " \"" + new String(clientName) + "\"");
         } else {
            LCS.verificationResponse(false);
         }
      } else {
         this.notifyAndVerify(type);
      }
   }

   @Override
   public final void verificationTimerExpiry() {
      this.dialog.close();
   }

   @Override
   public final synchronized void dialogClosed(Dialog dialog, int choice) {
      if (choice == 4) {
         LCS.verificationResponse(true);
      } else {
         if (choice == -1) {
            LCS.verificationResponse(false);
         }
      }
   }

   @Override
   public final void RRLPPayloadIndicationEvent(int sessionID, int length) {
   }

   @Override
   public final void reqAssistDataEvent() {
   }
}
