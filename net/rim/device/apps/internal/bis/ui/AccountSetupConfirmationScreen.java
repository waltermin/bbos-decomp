package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISClient;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class AccountSetupConfirmationScreen extends UserSettingsScreen {
   public AccountSetupConfirmationScreen() {
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
      int clientUiMode = ((BISClient)Application.getApplication()).getUiMode();
      if (ClientSessionState.getInstance().getSynchMailboxEnabled()) {
         this.addContentField(new FormattedTextField(ApplicationResources.getString(316)));
         Button closeButton = new Button(ApplicationResources.getString(15));
         Button nextButton = new Button(ApplicationResources.getString(17));
         this.addButtonBarButtons(new Button[]{closeButton, nextButton}, false, 1);
         CloseEvent closeEvent = new CloseEvent();
         this.attachEventToField(closeButton, closeEvent);
         LinkEvent nextEvent = new LinkEvent(17, 55);
         this.attachEventToField(nextButton, nextEvent);
         this.setDefaultEvent(nextEvent);
      } else if (clientUiMode == 1) {
         this.addContentField(new FormattedTextField(ApplicationResources.getString(64)));
         Button closeButton = new Button(ApplicationResources.getString(15));
         Button nextButton = new Button(ApplicationResources.getString(17));
         this.addButtonBarButtons(new Button[]{closeButton, nextButton}, false, 1);
         CloseEvent closeEvent = new CloseEvent();
         this.attachEventToField(closeButton, closeEvent);
         LinkEvent nextEvent = new LinkEvent(17, 7);
         this.attachEventToField(nextButton, nextEvent);
         this.setDefaultEvent(nextEvent);
      } else {
         this.addContentField(new LabelField(ApplicationResources.getString(63)));
         Button okButton = new Button(ApplicationResources.getString(39));
         this.addButtonBarButtons(new Button[]{okButton}, false);
         LinkEvent okEvent = new LinkEvent(39, 7);
         this.attachEventToField(okButton, okEvent);
         this.setCloseEvent(okEvent);
         this.setDefaultEvent(okEvent);
      }

      this.setHelp("index.wml");
   }
}
