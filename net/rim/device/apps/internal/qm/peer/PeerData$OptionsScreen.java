package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.qm.resource.QmResources;
import net.rim.device.internal.i18n.CommonResource;

final class PeerData$OptionsScreen extends MainScreen {
   private String[] YES_NO_STRINGS = new String[]{CommonResources.getString(100), CommonResources.getString(101)};
   private ObjectChoiceField _messagelistIntegration;
   private ObjectChoiceField _askQuestion;
   private ObjectChoiceField _vibrateOnBuzz;
   private ObjectChoiceField _allowForwardInvite;

   private PeerData$OptionsScreen() {
      super(0);
      this.setTitle(new LabelField(QmResources.getString(54)));
      this._vibrateOnBuzz = new ObjectChoiceField(PeerResources.getString(898), this.YES_NO_STRINGS, PeerData.isVibrateOnBuzz() ? 0 : 1);
      this.add(this._vibrateOnBuzz);
      this._askQuestion = new ObjectChoiceField(PeerResources.getString(899), this.YES_NO_STRINGS, PeerData.isAskInviteQuestion() ? 0 : 1);
      this.add(this._askQuestion);
      this._allowForwardInvite = new ObjectChoiceField(PeerResources.getString(2005), this.YES_NO_STRINGS, PeerData.isAllowForwardInvite() ? 0 : 1);
      this.add(this._allowForwardInvite);
      this._messagelistIntegration = new ObjectChoiceField(QmResources.getString(100), this.YES_NO_STRINGS, PeerData.isMessagelistIntegration() ? 0 : 1);
      this.add(this._messagelistIntegration);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(new PeerData$OptionsScreen$1(this, CommonResource.getString(18), 1, 100));
   }

   @Override
   public final void save() {
      PeerData.saveVibrateOnBuzz(this._vibrateOnBuzz.getSelectedIndex() == 0);
      PeerData.saveAskInviteQuestion(this._askQuestion.getSelectedIndex() == 0);
      PeerData.saveAllowForwardInvite(this._allowForwardInvite.getSelectedIndex() == 0);
      if (this._messagelistIntegration.isDirty()) {
         boolean yes = this._messagelistIntegration.getSelectedIndex() == 0;
         PeerData.saveMessagelistIntegration(yes);
         if (yes) {
            PeerConversationsFolder.getInstance().register();
         } else {
            PeerConversationsFolder.getInstance().deregister();
         }
      }

      PeerData.access$300(true);
   }

   PeerData$OptionsScreen(PeerData$1 x0) {
      this();
   }
}
