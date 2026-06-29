package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;

public final class SetupWorkEmailScreen extends UserSettingsScreen {
   private LinkEvent _emailSetupLinkEvent;
   private RadioButtonField _ispOutlook;
   private RadioButtonField _ispProvideSettings;
   private RadioButtonField _owa;
   private RadioButtonField _outlook;
   private RadioButtonField _notes;

   public SetupWorkEmailScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(165));
      this.addContentField((Field)(new Object(ApplicationResources.getString(157))));
      this.addContentField(new BoldLabelField(ApplicationResources.getString(24)));
      RadioButtonGroup radioGroup = (RadioButtonGroup)(new Object());
      this._ispOutlook = (RadioButtonField)(new Object(ApplicationResources.getString(158), radioGroup, false));
      this._ispProvideSettings = (RadioButtonField)(new Object(ApplicationResources.getString(67), radioGroup, true));
      this.addContentField(this._ispProvideSettings);
      this.addContentField(this._ispOutlook);
      this.addContentField(new BoldLabelField(ApplicationResources.getString(23)));
      this._owa = (RadioButtonField)(new Object(ApplicationResources.getString(166), radioGroup, false));
      this._outlook = (RadioButtonField)(new Object(ApplicationResources.getString(167), radioGroup, false));
      this.addContentField(this._owa);
      this.addContentField(this._outlook);
      this.addContentField(new BoldLabelField(ApplicationResources.getString(25)));
      this._notes = (RadioButtonField)(new Object(ApplicationResources.getString(168), radioGroup, false));
      this.addContentField(this._notes);
      this._emailSetupLinkEvent = new LinkEvent(17, 24);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button backButton = new Button(ApplicationResources.getString(16));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, backButton, nextButton}, false, 2);
      this.attachEventToField(nextButton, this._emailSetupLinkEvent);
      this.setDefaultEvent(this._emailSetupLinkEvent);
      this.attachEventToField(closeButton, new CloseEvent());
      this.attachEventToField(backButton, new BackEvent());
      radioGroup.setChangeListener(new SetupWorkEmailScreen$EmailTypeChoiceFieldListener(this, null));
      this.setHelp("221683.wml");
   }
}
