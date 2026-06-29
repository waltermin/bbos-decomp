package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;

public final class EmailSetupScreen extends UserSettingsScreen {
   private RadioButtonField _setupExistingEmailChoice;
   private RadioButtonField _setupBBMailChoice;
   private LinkEvent _emailSetupLinkEvent;

   public EmailSetupScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(6));
      this.addContentField((Field)(new Object(ApplicationResources.getString(33))));
      this.addContentField((Field)(new Object()));
      RadioButtonGroup emailSetupMethodGroup = (RadioButtonGroup)(new Object());
      this._setupExistingEmailChoice = (RadioButtonField)(new Object(ApplicationResources.getString(34), emailSetupMethodGroup, true));
      this.addContentField(this._setupExistingEmailChoice);
      this._setupBBMailChoice = (RadioButtonField)(new Object(ApplicationResources.getString(35), emailSetupMethodGroup, false));
      this.addContentField(this._setupBBMailChoice);
      this._emailSetupLinkEvent = new LinkEvent(17, 4);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, nextButton}, false, 1);
      this.attachEventToField(nextButton, this._emailSetupLinkEvent);
      this.setDefaultEvent(this._emailSetupLinkEvent);
      this.attachEventToField(closeButton, new CloseEvent());
      emailSetupMethodGroup.setChangeListener(new EmailSetupScreen$EmailTypeChoiceFieldListener(this, null));
      this.setHelp("index.wml");
   }
}
