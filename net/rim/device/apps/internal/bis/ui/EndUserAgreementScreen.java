package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.Event;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.api.ui.LinkField;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class EndUserAgreementScreen extends BasicScreen {
   private RadioButtonGroup _viewUserAgreementGroup;
   private RadioButtonField _viewYesButton;
   private RadioButtonField _viewNoButton;
   private Button _closeButton;
   private Button _agreeButton;
   private Event _agreeEvent;
   private static final String PARAM_AGREEMENT = "agreed";
   private static final boolean AGREE_ENABLED_DEFAULT = false;

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(45));
      this._viewUserAgreementGroup = new RadioButtonGroup();
      this._viewYesButton = new RadioButtonField(ApplicationResources.getString(32), this._viewUserAgreementGroup, false);
      this._viewNoButton = new RadioButtonField(ApplicationResources.getString(31), this._viewUserAgreementGroup, true);
      this._viewUserAgreementGroup.setChangeListener(new EndUserAgreementScreen$ViewUserAgreementListener(this, null));
      this._closeButton = new Button(ApplicationResources.getString(69));
      this._agreeButton = new Button(ApplicationResources.getString(70));
      this._agreeButton.setEditable(false);
      this._agreeEvent = new CommandEvent(70, 16, null);
      this._agreeEvent.setOnMenu(false);
      this.addContentField(new FormattedTextField(ApplicationResources.getString(72)));
      LinkField viewEUALink = new LinkField(ApplicationResources.getString(73));
      this.addContentField(viewEUALink);
      LabelField readEUA = new LabelField(ApplicationResources.getString(223));
      readEUA.setMargin(10, 0, 0, 0);
      this.addContentField(readEUA);
      this.addContentField(this._viewYesButton);
      this.addContentField(this._viewNoButton);
      LabelField agreeEUA = new BoldLabelField(ApplicationResources.getString(74));
      agreeEUA.setMargin(10, 0, 10, 0);
      this.addContentField(agreeEUA);
      this.addButtonBarButtons(new Button[]{this._closeButton, this._agreeButton}, false, 1);
      this.attachEventToField(this._agreeButton, this._agreeEvent);
      this.setDefaultEvent(this._agreeEvent);
      this.attachEventToField(viewEUALink, new CommandEvent(73, 15, null));
      Event disagreeEvent = null;
      if (ClientSessionState.getInstance().getUserInfo() != null) {
         disagreeEvent = new CommandEvent(69, 21, null);
      } else {
         disagreeEvent = new LinkEvent(69, 0);
      }

      this.attachEventToField(this._closeButton, disagreeEvent);
      this.setCloseEvent(disagreeEvent);
   }
}
