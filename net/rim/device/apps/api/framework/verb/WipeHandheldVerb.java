package net.rim.device.apps.api.framework.verb;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.PopupDialogClosedListener;
import net.rim.device.internal.ui.component.SimpleInputDialog;

public class WipeHandheldVerb extends Verb implements DialogClosedListener, PopupDialogClosedListener {
   private boolean _statusDialog;
   private boolean _open;
   private long _dialogStyle;
   private int _result;
   private Object _dialogSignal;
   private Object _completionSignal;
   private static String _handheldWipeConfirmString = "blackberry";

   public void waitForCompletion() {
      if (this._statusDialog && this._open) {
         synchronized (this._completionSignal) {
            try {
               this._completionSignal.wait();
            } finally {
               return;
            }
         }
      }
   }

   @Override
   public void dialogClosed(PopupDialog d, int choice) {
      this._result = choice;
      synchronized (this._dialogSignal) {
         this._dialogSignal.notify();
      }
   }

   @Override
   public void dialogClosed(Dialog d, int choice) {
      this._result = choice;
      synchronized (this._dialogSignal) {
         this._dialogSignal.notify();
      }
   }

   @Override
   public Object invoke(Object parameter) {
      ResourceBundleFamily srb = ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security");
      this._open = true;
      Dialog promptDialog = new Dialog(
         srb.getString(701), srb.getStringArray(702), new int[]{0, 1, -804650998, 0, 1, 2, 3, 4}, 1, Bitmap.getPredefinedBitmap(2), this._dialogStyle
      );
      CheckboxField wipeThirdPartyAppsCheckbox = new CheckboxField(srb.getString(755), false, 1073741824);
      promptDialog.add(wipeThirdPartyAppsCheckbox);
      promptDialog.setEscapeEnabled(true);
      if (this._statusDialog) {
         UiApplication.getUiApplication().pushGlobalScreen(promptDialog, -1073741823, 2);
         promptDialog.setDialogClosedListener(this);
         synchronized (this._dialogSignal) {
            label208:
            try {
               this._dialogSignal.wait();
            } finally {
               break label208;
            }
         }
      } else {
         this._result = promptDialog.doModal();
      }

      if (this._result == 0) {
         String prompt = MessageFormat.format(srb.getString(703), new Object[]{_handheldWipeConfirmString});
         SimpleInputDialog inputDialog = new SimpleInputDialog(10, prompt, 0, 1000000, this._dialogStyle);
         inputDialog.setAllowUnicodeInput(false);
         if (this._statusDialog) {
            inputDialog.setModal(false);
            UiApplication.getUiApplication().pushGlobalScreen(inputDialog, -1073741823, 2);
            inputDialog.setPopupDialogClosedListener(this);
            synchronized (this._dialogSignal) {
               label198:
               try {
                  this._dialogSignal.wait();
               } finally {
                  break label198;
               }
            }
         } else {
            inputDialog.show();
            this._result = inputDialog.getCloseReason() != -1 ? 0 : 1;
         }

         if (this._result == 0) {
            if (StringUtilities.compareToIgnoreCase(_handheldWipeConfirmString, inputDialog.getText()) == 0) {
               Dialog dialog = new Dialog(srb.getString(704), null, null, 0, Bitmap.getPredefinedBitmap(2));
               if (this._statusDialog) {
                  UiApplication.getUiApplication().pushGlobalScreen(dialog, -1073741823, 2);
               } else {
                  dialog.show();
               }

               Application app = Application.getApplication();
               app.invokeLater(new WipeHandheldVerb$WipeHandheldRunnable(wipeThirdPartyAppsCheckbox.getChecked()));
            } else if (this._statusDialog) {
               Dialog d = new Dialog(srb.getString(705), CommonResource.getStringArray(10004), null, 0, null, 0);
               d.setIcon(ThemeManager.getThemeAwareImage("dialog_information"));
               UiApplication.getUiApplication().pushGlobalScreen(d, -1073741823, 2);
               d.setDialogClosedListener(this);
               synchronized (this._dialogSignal) {
                  label189:
                  try {
                     this._dialogSignal.wait();
                  } finally {
                     break label189;
                  }
               }
            } else {
               Dialog.inform(srb.getString(705));
            }
         }
      }

      synchronized (this._completionSignal) {
         this._open = false;
         this._completionSignal.notify();
         return null;
      }
   }

   public WipeHandheldVerb(long dialogStyle) {
      super(200965, ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security"), 700);
      this._dialogStyle = dialogStyle;
      if ((dialogStyle & 33554432) != 0) {
         this._statusDialog = true;
      }

      this._dialogSignal = new Object();
      this._completionSignal = new Object();
      this._open = false;
   }

   public static String getDisplayString() {
      ResourceBundleFamily srb = ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security");
      return srb.getString(700);
   }
}
