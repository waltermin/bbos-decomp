package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class SetupPersonalEmailScreen extends UserSettingsScreen {
   RadioButtonField _outlookRadioButton;
   RadioButtonField _provideSettingsRadioButton;
   private LinkEvent _emailSetupLinkEvent;
   private static final String setupExistingMailScreenLink;
   private static final String setupBBMailScreenLink;

   public SetupPersonalEmailScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(156));
      this.addContentField((Field)(new Object(ApplicationResources.getString(157))));
      RadioButtonGroup personalAccountTypeGroup = (RadioButtonGroup)(new Object());
      boolean importControlEnabled = ClientSessionState.getInstance().getBrandingInfo().isImportControlEnabled();
      this._provideSettingsRadioButton = (RadioButtonField)(new Object(ApplicationResources.getString(159), personalAccountTypeGroup, true));
      this.addContentField(this._provideSettingsRadioButton);
      if (importControlEnabled) {
         this._outlookRadioButton = (RadioButtonField)(new Object(ApplicationResources.getString(158), personalAccountTypeGroup, false));
         this.addContentField(this._outlookRadioButton);
      }

      this._emailSetupLinkEvent = new LinkEvent(17, 24);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button backButton = new Button(ApplicationResources.getString(16));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, backButton, nextButton}, false, 2);
      this.attachEventToField(nextButton, this._emailSetupLinkEvent);
      this.setDefaultEvent(this._emailSetupLinkEvent);
      this.attachEventToField(closeButton, new CloseEvent());
      this.attachEventToField(backButton, new BackEvent());
      personalAccountTypeGroup.setChangeListener(new SetupPersonalEmailScreen$EmailTypeChoiceFieldListener(this, null));
      this.setHelp("221683.wml");
   }
}
