package net.rim.device.apps.internal.browser.wml;

import com.fourthpass.wmls.IDialog;
import net.rim.device.api.system.Application;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.RunnableDialog;
import net.rim.device.apps.internal.browser.ui.DialogEnterString;

final class WMLScriptDialog implements IDialog {
   @Override
   public final String alert(String message) {
      RunnableDialog dialog = new RunnableDialog(message, 0);
      Application.getApplication().invokeAndWait(dialog);
      return "";
   }

   @Override
   public final String confirm(String cancel, String ok, String message) {
      if (ok.length() == 0) {
         ok = CommonResources.getString(117);
      }

      if (cancel.length() == 0) {
         cancel = CommonResources.getString(9042);
      }

      Object[] choices = new Object[]{ok, cancel};
      RunnableDialog dialog = new RunnableDialog(0, message, choices, null, 0);
      Application.getApplication().invokeAndWait(dialog);
      return dialog.getResult() == 0 ? "true" : "false";
   }

   @Override
   public final String prompt(String defaultInput, String message) {
      DialogEnterString des = new DialogEnterString(message, defaultInput, CommonResources.getString(117));
      RunnableDialog dialog = new RunnableDialog(des);
      Application.getApplication().invokeAndWait(dialog);
      return des.getResult();
   }
}
