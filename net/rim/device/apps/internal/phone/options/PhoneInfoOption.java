package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class PhoneInfoOption extends SaveableMainScreenOptionsListItem {
   private PhoneInfoOption$CallTimerList _callTimerList;
   private boolean _showErrorLog;
   private int[] _initialCallTimers = Arrays.copy(CallTimers.getCallTimers().getTimers());

   public PhoneInfoOption() {
      super(PhoneResources.getString(186));
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      String number = PhoneUtilities.getDevicePhoneNumber();
      if (number == null) {
         number = PhoneResources.getString(138);
      }

      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(1152921504606846976L));
      hfm.add((Field)(new Object(((StringBuffer)(new Object())).append(PhoneResources.getString(168)).append(": ").toString(), 36028797018963968L)));
      hfm.add(new PhoneInfoOption$CopyableNumberField(number, 18014398509481984L));
      mainScreen.add(hfm);
      if (DirectConnect.isSupported()) {
         String ufmiNumber = DirectConnect.getUFMI();
         hfm = (HorizontalFieldManager)(new Object(1152921504606846976L));
         hfm.add((Field)(new Object(PhoneResources.getString(6044), 36028797018963968L)));
         hfm.add(new PhoneInfoOption$CopyableNumberField(ufmiNumber, 18014398509481984L));
         mainScreen.add(hfm);
      }

      mainScreen.add((Field)(new Object()));
      int[] meters;
      if (PhoneUtilities.idenTypeNetwork()) {
         meters = new int[]{2, 3, 0, 1, 51, -804651007, 100, -804651004, 220, 6009, 6010, 6011, -804651004, 500, 501, 502};
      } else {
         meters = new int[]{0, 1, -804651004, 0, 1, 2, 3, -804651005};
      }

      this._callTimerList = new PhoneInfoOption$CallTimerList(meters);
      mainScreen.add(this._callTimerList);
      this._callTimerList.setFocus();
   }

   @Override
   protected final void populateMenuVerbs(VerbToMenu verbToMenu, int instance) {
      super.populateMenuVerbs(verbToMenu, instance);
      Field field = super._mainScreen.getLeafFieldWithFocus();
      if (field instanceof PhoneInfoOption$CallTimerList) {
         verbToMenu.addVerb(new PhoneInfoOption$ClearTimerVerb(this, this._callTimerList.getSelectedCallTimer()));
         verbToMenu.addVerb(new PhoneInfoOption$ClearTimerVerb(this, -1));
      } else {
         if (field instanceof PhoneInfoOption$CopyableNumberField) {
            String text = ((PhoneInfoOption$CopyableNumberField)field).getText();
            verbToMenu.addVerb(new PhoneInfoOption$CopyVerb(text));
         }
      }
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      if (super._closeVerb != null) {
         verbToMenu.addVerb(super._closeVerb);
      }

      if (super._mainScreen.isDirty()) {
         verbToMenu.addVerb(super._saveVerb);
         verbToMenu.setDefaultVerb(super._saveVerb);
      }
   }

   @Override
   public final boolean keyChar(char key, int time, int status) {
      if (key == '?' && !this._showErrorLog) {
         this._showErrorLog = true;
         super._mainScreen.add((Field)(new Object()));
         String title = PhoneResources.getString(6038);
         super._mainScreen.add((Field)(new Object(title, 36028797018963968L)));
         super._mainScreen.add((Field)(new Object(PhoneOptions.getOptions().getPhoneErrorLog(), 18014398509481984L)));
         super._mainScreen.invalidate();
         return true;
      } else {
         return super.keyChar(key, time, status);
      }
   }

   @Override
   protected final boolean discard() {
      CallTimers.getCallTimers().setTimers(this._initialCallTimers);
      return super.discard();
   }

   static final MainScreen access$000(PhoneInfoOption x0) {
      return x0._mainScreen;
   }

   static final MainScreen access$100(PhoneInfoOption x0) {
      return x0._mainScreen;
   }
}
