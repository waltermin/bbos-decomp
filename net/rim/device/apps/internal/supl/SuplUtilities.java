package net.rim.device.apps.internal.supl;

import java.util.Timer;
import net.rim.device.api.gps.GPSLocationExtended;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;

public final class SuplUtilities implements DialogClosedListener {
   private Dialog dialog;
   private Timer timer;
   private int dialogResponse;
   private int defaultChoice;
   private static final int VERIFICATION_RESPONSE_TIME_MS;
   static ResourceBundle _rb = ResourceBundle.getBundle(-8422383104449549495L, "net.rim.device.apps.internal.supl.Supl");

   public final boolean cachedPositionAcceptable(GPSLocationExtended position, QOP desiredQOP) {
      boolean result = false;
      if (position == null) {
         System.out.println("No previous position");
      } else {
         double horUncertainty = position.getHorizontalUncertainty();
         double specifiedHorAcc = desiredQOP.getHorizontalAccuracy();
         System.out
            .println(((StringBuffer)(new Object("HorAcc Specified: "))).append(specifiedHorAcc).append(" HorUncert: ").append(horUncertainty).toString());
         result = horUncertainty < specifiedHorAcc;
         if ((desiredQOP.optionals & 4) == 4) {
            double verUncertainty = position.getVerticalUncertainity();
            double specifiedVerAcc = desiredQOP.getVerticalAccuracy();
            System.out
               .println(((StringBuffer)(new Object("VerAcc Specified: "))).append(specifiedVerAcc).append(" VerUncert: ").append(verUncertainty).toString());
            result = result && verUncertainty < specifiedVerAcc;
         }

         if ((desiredQOP.optionals & 2) == 2) {
            long positionTimestamp = position.getUTCTime();
            long currentTime = System.currentTimeMillis();
            System.out
               .println(
                  ((StringBuffer)(new Object("MaxLocAge Specified: ")))
                     .append(desiredQOP.maxLocAge)
                     .append(" Position Age: ")
                     .append(currentTime - positionTimestamp)
                     .toString()
               );
            result = result && currentTime - positionTimestamp < desiredQOP.maxLocAge;
         }

         if ((desiredQOP.optionals & 1) == 1) {
            System.out.println(((StringBuffer)(new Object("Specified Delay: "))).append(desiredQOP.delay).toString());
            return result;
         }
      }

      return result;
   }

   final synchronized boolean notifyAndVerify(byte notificationType) {
      this.defaultChoice = -1;
      int dialogType = 3;
      boolean retVal = false;
      String resourceString;
      switch (notificationType) {
         case 0:
            return true;
         case 1:
            dialogType = 0;
            this.defaultChoice = 0;
            resourceString = _rb.getString(2);
            break;
         case 2:
         default:
            this.defaultChoice = 4;
            resourceString = _rb.getString(3);
            break;
         case 3:
            this.defaultChoice = -1;
            resourceString = _rb.getString(3);
      }

      this.dialog = (Dialog)(new Object(dialogType, resourceString, this.defaultChoice, null, 0));
      this.dialog.setDialogClosedListener(this);
      this.timer = (Timer)(new Object());
      this.timer.schedule(new SuplUtilities$VerificationTimerTask(this, null), 30000);
      UiApplication.getUiApplication().invokeAndWait(new SuplUtilities$1(this));

      label38:
      try {
         this.wait();
      } finally {
         break label38;
      }

      boolean verificationAllowed = false;
      switch (this.dialogResponse) {
         case -1:
            verificationAllowed = false;
            break;
         case 0:
         case 4:
            verificationAllowed = true;
      }

      return verificationAllowed;
   }

   @Override
   public final synchronized void dialogClosed(Dialog dialog, int choice) {
      this.timer.cancel();
      this.dialogResponse = choice;
      this.notifyAll();
   }
}
