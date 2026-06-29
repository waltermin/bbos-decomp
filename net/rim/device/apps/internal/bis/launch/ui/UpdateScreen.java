package net.rim.device.apps.internal.bis.launch.ui;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.internal.bis.BISLaunch;
import net.rim.device.apps.internal.bis.launch.resource.ApplicationResources;

public final class UpdateScreen extends BaseScreen {
   boolean mandatory;
   String[] downloadUrls;
   byte[][] digests;
   int size;

   public UpdateScreen(boolean mandatory, String[] downloadUrls, byte[][] digests, int size) {
      super(ApplicationResources.getString(2));
      this.mandatory = mandatory;
      this.downloadUrls = downloadUrls;
      this.digests = digests;
      this.size = size;
      String acceptLabel = ApplicationResources.getString(7);
      MenuItem declineMenuItem = null;
      LabelField instructionsLabelField;
      FieldChangeRunnable declineRunnable;
      String declineLabel;
      if (mandatory) {
         instructionsLabelField = this.createLabelField(3);
         declineLabel = ApplicationResources.getString(15);
         declineRunnable = new UpdateScreen$1(this);
      } else {
         instructionsLabelField = this.createLabelField(4);
         declineLabel = ApplicationResources.getString(6);
         declineRunnable = new UpdateScreen$2(this);
         declineMenuItem = new UpdateScreen$3(this, declineLabel, 1, 1);
      }

      HorizontalFieldManager buttonsFieldManager = (HorizontalFieldManager)(new Object(12884901888L));
      ButtonField declineButtonField = this.createButton(declineLabel, null);
      ButtonField updateNowButtonField = this.createButton(acceptLabel, null);
      buttonsFieldManager.add(declineButtonField);
      buttonsFieldManager.add(updateNowButtonField);
      UpdateNowRunnable updateNowRunnable = new UpdateNowRunnable(mandatory, downloadUrls, digests, size);
      this.addFieldChangeRunnable(updateNowButtonField, updateNowRunnable);
      this.addFieldChangeRunnable(declineButtonField, declineRunnable);
      MenuItem acceptMenuItem = new UpdateScreen$4(this, acceptLabel, 1, 1);
      super._defaultMenuItem = acceptMenuItem;
      if (declineMenuItem != null) {
         super._menuItems = new Object[]{declineMenuItem, acceptMenuItem};
      } else {
         super._menuItems = new Object[]{acceptMenuItem};
      }

      this.add(instructionsLabelField);
      this.add(this.createFormattedTextField(5));
      this.add(buttonsFieldManager);
      updateNowButtonField.setFocus();
   }

   protected final void exit() {
      BISLaunch app = (BISLaunch)UiApplication.getUiApplication();
      app.exit();
   }

   protected final void launch() {
      BISLaunch app = (BISLaunch)UiApplication.getUiApplication();
      app.launchBISClient();
   }

   protected final void update() {
      UpdateNowRunnable updateNowRunnable = new UpdateNowRunnable(this.mandatory, this.downloadUrls, this.digests, this.size);
      updateNowRunnable.run(null, 0);
   }
}
