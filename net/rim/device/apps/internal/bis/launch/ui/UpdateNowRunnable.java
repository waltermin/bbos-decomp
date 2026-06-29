package net.rim.device.apps.internal.bis.launch.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ui.RunnableDialog;
import net.rim.device.apps.internal.bis.BISLaunch;
import net.rim.device.apps.internal.bis.launch.ota.ApplicationDownloadField;
import net.rim.device.apps.internal.bis.launch.resource.ApplicationResources;
import net.rim.device.internal.ui.Image;

public final class UpdateNowRunnable implements FieldChangeRunnable {
   private String[] _downloadUrls;
   private byte[][] _digests;
   private int _size;
   private boolean _mandatoryUpdate;
   private boolean _updateSuccess;
   private boolean _launchApplication;
   private boolean _rebootRequired;
   private boolean _rebootNow;
   private static final int LATER = 0;
   private static final int REBOOT = 1;
   private static final int OK = 0;
   private static final int RUN = 1;

   public UpdateNowRunnable(boolean mandatoryUpdate, String[] downloadUrls, byte[][] digests, int size) {
      this._mandatoryUpdate = mandatoryUpdate;
      this._downloadUrls = downloadUrls;
      this._digests = digests;
      this._size = size;
   }

   @Override
   public final void run(Field field, int context) {
      this.download();
      this.navigate();
   }

   protected final void download() {
      this._updateSuccess = false;
      ApplicationDownloadField downloadField = new ApplicationDownloadField(this._downloadUrls, this._digests, this._size);
      downloadField.doDownload();
      int downloadStatus = downloadField.getDownloadStatus();
      switch (downloadStatus) {
         case 0:
            Dialog.inform(ApplicationResources.getString(12));
            this._updateSuccess = false;
            return;
         case 1:
         default:
            this._updateSuccess = true;
            if (downloadField.isRebootRequired()) {
               this._rebootRequired = true;
               this._rebootNow = this.askUserToReboot();
               return;
            }

            this._rebootRequired = false;
            this._launchApplication = this.askUserToLaunch();
            return;
         case 2:
            Dialog.inform(ApplicationResources.getString(13));
            return;
         case 3:
            Dialog.inform(ApplicationResources.getString(11));
            this._updateSuccess = false;
      }
   }

   protected final void navigate() {
      BISLaunch app = (BISLaunch)UiApplication.getUiApplication();
      if (this._updateSuccess) {
         if (this._rebootRequired) {
            if (this._rebootNow) {
               app.reboot();
            } else {
               app.exit();
            }
         } else if (this._launchApplication) {
            app.launchBISClient();
         } else {
            app.exit();
         }
      } else if (!this._mandatoryUpdate) {
         app.launchBISClient();
      } else {
         app.exit();
      }
   }

   protected final boolean askUserToReboot() {
      Object[] choices = new Object[]{ApplicationResources.getString(8), ApplicationResources.getString(9)};
      int defaultChoice = 0;
      String label = ApplicationResources.getString(10);
      Image icon = ThemeManager.getThemeAwareImage("dialog_information");
      Dialog dialog = (Dialog)(new Object(label, choices, null, defaultChoice, null, 0));
      dialog.setIcon(icon);
      RunnableDialog rd = (RunnableDialog)(new Object(dialog));
      rd.run();
      int result = rd.getResult();
      return result == 1;
   }

   protected final boolean askUserToLaunch() {
      Object[] choices = new Object[]{ApplicationResources.getString(19), ApplicationResources.getString(20)};
      int defaultChoice = 0;
      String label = ApplicationResources.getString(18);
      Image icon = ThemeManager.getThemeAwareImage("dialog_information");
      Dialog dialog = (Dialog)(new Object(label, choices, null, defaultChoice, null, 0));
      dialog.setIcon(icon);
      RunnableDialog rd = (RunnableDialog)(new Object(dialog));
      rd.run();
      int result = rd.getResult();
      return result == 1;
   }
}
