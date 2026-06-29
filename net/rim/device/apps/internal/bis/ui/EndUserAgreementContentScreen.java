package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class EndUserAgreementContentScreen extends UserSettingsScreen {
   public EndUserAgreementContentScreen(boolean showMenuOptions) {
      super(showMenuOptions ? 31 : 0);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(45));
      Button ok = new Button(ApplicationResources.getString(39));
      if (null != ClientSessionState.getInstance().getUserInfo()) {
         String userName = ClientSessionState.getInstance().getUserInfo().getUsername();
         String agreementVersion = ClientSessionState.getInstance().getTermsVersion();
         long time = ClientSessionState.getInstance().getUserInfo().getTimeStamp();
         DateFormat timeStamp = DateFormat.getInstance(6);
         DateFormat dateStamp = DateFormat.getInstance(48);
         String euaText = MessageFormat.format(
            ApplicationResources.getString(91),
            new String[]{userName, agreementVersion, dateStamp.formatLocal(time).toString(), timeStamp.formatLocal(time).toString()}
         );
         this.addContentField(new LabelField(euaText));
         this.addContentLineBreak();
      }

      BackEvent okEvent = new BackEvent(39);
      this.attachEventToField(ok, okEvent);
      this.setDefaultEvent(okEvent);
      this.addContentField(
         new TextField(
            null,
            ClientSessionState.getInstance().getTermsAndConditions(),
            ClientSessionState.getInstance().getTermsAndConditions().length(),
            9007199254740992L
         )
      );
      this.addButtonBarButtons(new Button[]{ok}, false);
      this.setHelp("66462.wml");
   }
}
