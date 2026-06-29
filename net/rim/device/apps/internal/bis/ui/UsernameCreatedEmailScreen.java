package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class UsernameCreatedEmailScreen extends UserSettingsScreen {
   public UsernameCreatedEmailScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(313));
      ClientSessionState session = ClientSessionState.getInstance();
      String confirmation = MessageFormat.format(ApplicationResources.getString(312), new Object[]{session.getIntegrationEmail()});
      FormattedTextField confirmLabel = new FormattedTextField(confirmation);
      this.addContentField(confirmLabel);
      this.addContentLineBreak();
      LabelField orLabel = (LabelField)(new Object(ApplicationResources.getString(237)));
      this.addContentField(orLabel);
      this.addContentLineBreak();
      FormattedTextField nextLabel = new FormattedTextField(ApplicationResources.getString(244));
      this.addContentField(nextLabel);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, nextButton}, false, 1);
      this.attachEventToField(closeButton, new CloseEvent());
      LinkEvent nextEvent = new LinkEvent(17, 7);
      this.attachEventToField(nextButton, nextEvent);
      this.setDefaultEvent(nextEvent);
      this.setHelp("index.wml");
   }
}
