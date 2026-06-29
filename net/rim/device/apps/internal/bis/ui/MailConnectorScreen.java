package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.api.ui.LinkField;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class MailConnectorScreen extends UserSettingsScreen {
   public MailConnectorScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(37));
      if (ClientSessionState.getInstance().getUserInfo().isAutoAuth()) {
         ClientSessionState session = ClientSessionState.getInstance();
         String config = MessageFormat.format(ApplicationResources.getString(267), new Object[]{session.getIntegrationEmail()});
         this.addContentField((Field)(new Object(null, config, config.length(), 9007199254740992L)));
         this.addContentLineBreak();
         LinkField createLink = new LinkField(ApplicationResources.getString(241));
         this.addContentField(createLink);
         this.attachEventToField(createLink, new LinkEvent(242, 49));
         String emailSent = MessageFormat.format(ApplicationResources.getString(268), new Object[]{session.getIntegrationEmail()});
         this.addContentField((Field)(new Object(null, emailSent, emailSent.length(), 9007199254740992L)));
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
         this.setCloseEvent(nextEvent);
      } else {
         String mailConnectorLabelText = ApplicationResources.getString(38);
         this.addContentField((Field)(new Object(mailConnectorLabelText)));
         Button closeButton = new Button(ApplicationResources.getString(15));
         Button okButton = new Button(ApplicationResources.getString(39));
         this.addButtonBarButtons(new Button[]{closeButton, okButton}, false, 1);
         this.attachEventToField(closeButton, new CloseEvent());
         LinkEvent okEvent = new LinkEvent(39, 7);
         this.attachEventToField(okButton, okEvent);
         this.setDefaultEvent(okEvent);
         this.setCloseEvent(okEvent);
      }

      this.setHelp("231283.wml");
   }
}
