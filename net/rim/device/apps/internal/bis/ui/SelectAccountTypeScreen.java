package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class SelectAccountTypeScreen extends UserSettingsScreen {
   private RadioButtonGroup _defaultGroup;
   private RadioButtonField _personalEmailField;
   private RadioButtonField _workEmailField;
   private LinkEvent _accountTypeLinkEvent;

   public SelectAccountTypeScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(48));
      String sessionSimpleEmail = ClientSessionState.getInstance().getIntegrationEmail();
      this._defaultGroup = new RadioButtonGroup();
      this._personalEmailField = new RadioButtonField(ApplicationResources.getString(87), this._defaultGroup, true);
      this._workEmailField = new RadioButtonField(ApplicationResources.getString(88), this._defaultGroup, false);
      String unableToSetupText = MessageFormat.format(ApplicationResources.getString(68), new String[]{sessionSimpleEmail});
      this.addContentField(new LabelField(unableToSetupText));
      this.addContentLineBreak();
      this.addContentField(this._personalEmailField);
      this.addContentLineBreak();
      this.addContentField(this._workEmailField);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button backButton = new Button(ApplicationResources.getString(16));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, backButton, nextButton}, false, 2);
      this._accountTypeLinkEvent = new LinkEvent(17, 19);
      this.attachEventToField(nextButton, this._accountTypeLinkEvent);
      this.setDefaultEvent(this._accountTypeLinkEvent);
      this._defaultGroup.setChangeListener(new SelectAccountTypeScreen$AccountTypeChoiceFieldListener(this, null));
      this.attachEventToField(closeButton, new CloseEvent());
      this.attachEventToField(backButton, new BackEvent());
      this.setHelp("221273.wml");
   }
}
