package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.api.ui.LinkField;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class AccountSetupInstructionScreen extends UserSettingsScreen {
   public AccountSetupInstructionScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(49));
      ClientSessionState session = ClientSessionState.getInstance();
      String or = ApplicationResources.getString(237);
      String config = MessageFormat.format(ApplicationResources.getString(236), new Object[]{session.getIntegrationEmail()});
      this.addContentField((Field)(new Object(config)));
      this.addContentLineBreak();
      if (!session.isAutoAuth()) {
         LabelField follow = (LabelField)(new Object(ApplicationResources.getString(238)));
         this.addContentField(follow);
         this.addContentLineBreak();
         LabelField orLabel = (LabelField)(new Object(ApplicationResources.getString(237)));
         this.addContentField(orLabel);
         this.addContentLineBreak();
      }

      LabelField toAdd = (LabelField)(new Object(MessageFormat.format(ApplicationResources.getString(239), new Object[]{session.getIntegrationEmail()})));
      LinkField provideSettings = new LinkField(ApplicationResources.getString(60));
      this.addContentField(provideSettings);
      this.addContentField(toAdd);
      LinkField createLink = null;
      if (session.isAutoAuth()) {
         this.addContentLineBreak();
         LabelField orLabel = (LabelField)(new Object(ApplicationResources.getString(237)));
         this.addContentField(orLabel);
         this.addContentLineBreak();
         String toConfig = MessageFormat.format(ApplicationResources.getString(240), new Object[]{session.getIntegrationEmail()});
         this.addContentField((Field)(new Object(null, toConfig, toConfig.length(), 9007199254740992L)));
         this.addContentLineBreak();
         createLink = new LinkField(ApplicationResources.getString(241));
         this.addContentField(createLink);
         String emailSent = MessageFormat.format(ApplicationResources.getString(243), new Object[]{session.getIntegrationEmail()});
         this.addContentField((Field)(new Object(null, emailSent, emailSent.length(), 9007199254740992L)));
      }

      this.addContentLineBreak();
      LabelField orLabel = (LabelField)(new Object(ApplicationResources.getString(237)));
      this.addContentField(orLabel);
      this.addContentLineBreak();
      FormattedTextField nextLabel = new FormattedTextField(ApplicationResources.getString(244));
      this.addContentField(nextLabel);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, nextButton}, false, 1);
      this.attachEventToField(provideSettings, new LinkEvent(60, 23));
      if (createLink != null) {
         this.attachEventToField(createLink, new LinkEvent(242, 49));
      }

      this.attachEventToField(closeButton, new CloseEvent());
      LinkEvent nextEvent = new LinkEvent(17, 7);
      this.attachEventToField(nextButton, nextEvent);
      this.setDefaultEvent(nextEvent);
      BackEvent backEvent = new BackEvent(16);
      this.setCloseEvent(backEvent);
   }
}
