package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class AccountSetupConfirmationMainScreen extends UserSettingsScreen {
   public AccountSetupConfirmationMainScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(1));
      String email = ClientSessionState.getInstance().getIntegrationEmail();
      this.addContentField(new LabelField(ApplicationResources.getString(18)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(email));
      this.addContentLineBreak();
      this.addContentField(new LabelField(ApplicationResources.getString(63)));
      Button okButton = new Button(ApplicationResources.getString(39));
      this.addButtonBarButtons(new Button[]{okButton}, false);
      LinkEvent okEvent = new LinkEvent(39, 7);
      this.attachEventToField(okButton, okEvent);
      this.setDefaultEvent(okEvent);
      this.setHelp("index.wml");
   }
}
