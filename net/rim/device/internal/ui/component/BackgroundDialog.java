package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Bitmap;

public class BackgroundDialog {
   private BackgroundDialog() {
   }

   public static String getInput(String label, int type) {
      return getInput(label, 0, 1000000, type);
   }

   public static String getInput(String label, int minLength, int maxLength, int type) throws BackgroundDialogCancelException {
      BackgroundDialog$GetInputDialogDisplayRunnable dialogDisplayRunnable = new BackgroundDialog$GetInputDialogDisplayRunnable(
         label, minLength, maxLength, type
      );
      SimpleOKCancelInputDialog dialog = (SimpleOKCancelInputDialog)dialogDisplayRunnable.runInCorrectProcess();
      if (dialog.getCloseReason() == -1) {
         throw new BackgroundDialogCancelException();
      } else {
         return dialog.getText();
      }
   }

   public static int getChoice(String label, Object[] choices, int defaultChoice) {
      return getChoice(label, choices, defaultChoice, 50);
   }

   public static int getChoice(String label, Object[] choices, int defaultChoice, int priority) {
      return getChoice(label, choices, defaultChoice, null, priority);
   }

   public static int getChoice(String label, Object[] choices, int defaultChoice, Bitmap bitmap, int priority) {
      BackgroundDialog$GetChoiceDialogDisplayRunnable dialogDisplayRunnable = new BackgroundDialog$GetChoiceDialogDisplayRunnable(
         label, choices, defaultChoice, bitmap, priority
      );
      SimpleChoiceDialog dialog = (SimpleChoiceDialog)dialogDisplayRunnable.runInCorrectProcess();
      return dialog.getCloseReason() == -1 ? -1 : dialog.getSelectedIndex();
   }

   public static void showMessage(String label) {
      showMessage(label, 50, false);
   }

   public static void showMessageOnProxy(String label) {
      showMessage(label, 50, true);
   }

   public static void showMessage(String label, int priority) {
      showMessage(label, priority, false);
   }

   private static void showMessage(String label, int priority, boolean forceRunInProxy) {
      BackgroundDialog$ShowMessageDialogDisplayRunnable dialogDisplayRunnable = new BackgroundDialog$ShowMessageDialogDisplayRunnable(label, priority);
      if (forceRunInProxy) {
         dialogDisplayRunnable.forceRunInProxy();
      }

      dialogDisplayRunnable.runInCorrectProcess();
   }

   public static void show(PopupDialog dialog) {
      BackgroundDialog$ShowDialogDisplayRunnable dialogDisplayRunnable = new BackgroundDialog$ShowDialogDisplayRunnable(dialog);
      dialogDisplayRunnable.runInCorrectProcess();
   }

   public static void showOnProxy(PopupDialog dialog) {
      BackgroundDialog$ShowDialogDisplayRunnable dialogDisplayRunnable = new BackgroundDialog$ShowDialogDisplayRunnable(dialog);
      dialogDisplayRunnable.forceRunInProxy();
      dialogDisplayRunnable.runInCorrectProcess();
   }
}
