package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class AOLTermsOfServicesScreen extends UserSettingsScreen {
   private static final String PARAM_EMAIL = "email";
   private static final String PARAM_PASSWORD = "password";
   private static final String PARAM_TERMSACCEPTED = "aolTermsAccepted";

   public AOLTermsOfServicesScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(149));
      this.addContentField(new LabelField(ApplicationResources.getString(131)));
      Button accept = new Button(ApplicationResources.getString(125));
      Button decline = new Button(ApplicationResources.getString(126));
      this.addButtonBarButtons(new Button[]{decline, accept}, false, 1);
      LinkEvent declineEvent = new LinkEvent(126, 7);
      this.attachEventToField(decline, declineEvent);
      this.setCloseEvent(declineEvent);
      CommandEvent acceptEvent = new CommandEvent(125, 2, new String[]{"email", "password", "aolTermsAccepted"});
      this.attachEventToField(accept, acceptEvent);
      this.setDefaultEvent(acceptEvent);
      this.setHelp("index.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      ClientSessionState sessionState = ClientSessionState.getInstance();
      inputMap.put("email", sessionState.getIntegrationEmail());
      inputMap.put("password", sessionState.getIntegrationPassword());
      inputMap.put("aolTermsAccepted", "true");
      return true;
   }
}
