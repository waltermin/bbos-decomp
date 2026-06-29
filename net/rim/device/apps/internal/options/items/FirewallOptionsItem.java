package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.MMS;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.firewall.BlockedCountListener;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public final class FirewallOptionsItem extends SaveableMainScreenOptionsListItem implements BlockedCountListener {
   private BooleanChoiceField _enabledField;
   private CheckboxField _blockSMS;
   private CheckboxField _blockMMS;
   private CheckboxField _blockPIN;
   private CheckboxField _blockBIS;
   private LabelField _decryptFailure;
   private LabelField _notEncryptedError;
   private LabelField _addressMismatch;
   private LabelField _invalidDatagram;
   private LabelField _mismatchedKey;
   private LabelField _smsCountLabel;
   private LabelField _mmsCountLabel;
   private LabelField _pinCountLabel;
   private LabelField _bisCountLabel;
   private LabelField _decryptFailureCountLabel;
   private LabelField _notEncryptedErrorCountLabel;
   private LabelField _addressMismatchCountLabel;
   private LabelField _invalidDatagramCountLabel;
   private LabelField _mismatchedKeyCountLabel;
   private Object _optionsAppEventLock;
   private static final int INDENT = 6;
   private static final int SPACER = 18;

   public FirewallOptionsItem() {
      super(OptionsResources.getString(1700), 5294015899860238835L);
      ContextObject.put(super._context, 244, "third_party_program_control");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      Firewall fw = Firewall.getInstance();
      boolean firewallEnabled = fw.isEnabled();
      this._enabledField = new BooleanChoiceField(OptionsResources.getString(1703), 2, firewallEnabled, 268435456);
      this._enabledField.setEditable(!ITPolicyInternal.isITPolicyEnabled());
      mainScreen.add(this._enabledField);
      mainScreen.add(new SeparatorField());
      mainScreen.add(new LabelField(OptionsResources.getString(2019), 36028797018963968L));
      VerticalIndentFieldManager vifm = new VerticalIndentFieldManager(1152921504606846976L);
      String blocked = OptionsResources.getString(1996);
      this._blockSMS = new CheckboxField(OptionsResources.getString(2020), fw.isBlockingEnabled((byte)1));
      this._blockSMS.setEditable(!fw.isBlockingEnabledByItPolicy((byte)1));
      int smsCount = fw.getBlockedCount((byte)1);
      String[] c = new String[]{String.valueOf(smsCount)};
      this._smsCountLabel = new LabelField(MessageFormat.format(blocked, c), 36028797018963968L);
      vifm.add(new FirewallOptionsItem$FieldAndCountManager(this._blockSMS, this._smsCountLabel), 6);
      this._blockMMS = new CheckboxField();
      if (MMS.isEnabled()) {
         this._blockMMS.setLabel(OptionsResources.getString(1988));
         this._blockMMS.setChecked(fw.isBlockingEnabled((byte)2));
         this._blockMMS.setEditable(!fw.isBlockingEnabledByItPolicy((byte)2));
         int mmsCount = fw.getBlockedCount((byte)2);
         c = new String[]{String.valueOf(mmsCount)};
         this._mmsCountLabel = new LabelField(MessageFormat.format(blocked, c), 36028797018963968L);
         vifm.add(new FirewallOptionsItem$FieldAndCountManager(this._blockMMS, this._mmsCountLabel), 6);
      }

      this._blockPIN = new CheckboxField(OptionsResources.getString(1994), fw.isBlockingEnabled((byte)4));
      this._blockPIN.setEditable(!fw.isBlockingEnabledByItPolicy((byte)4));
      int pinCount = fw.getBlockedCount((byte)4);
      c = new String[]{String.valueOf(pinCount)};
      this._pinCountLabel = new LabelField(MessageFormat.format(blocked, c), 36028797018963968L);
      vifm.add(new FirewallOptionsItem$FieldAndCountManager(this._blockPIN, this._pinCountLabel), 6);
      this._blockBIS = new CheckboxField(OptionsResources.getString(1995), fw.isBlockingEnabled((byte)3));
      this._blockBIS.setEditable(!fw.isBlockingEnabledByItPolicy((byte)3));
      int bisCount = fw.getBlockedCount((byte)3);
      c = new String[]{String.valueOf(bisCount)};
      this._bisCountLabel = new LabelField(MessageFormat.format(blocked, c), 36028797018963968L);
      vifm.add(new FirewallOptionsItem$FieldAndCountManager(this._blockBIS, this._bisCountLabel), 6);
      int decryptFailureCount = fw.getBlockedCount((byte)-1);
      int notEncryptedErrorCount = fw.getBlockedCount((byte)-2);
      int addressMismatchErrorCount = fw.getBlockedCount((byte)-3);
      int invalidDatagramCount = fw.getBlockedCount((byte)-4);
      int mismatchedKeyCount = fw.getBlockedCount((byte)-5);
      if (decryptFailureCount > 0 || notEncryptedErrorCount > 0 || addressMismatchErrorCount > 0 || invalidDatagramCount > 0 || mismatchedKeyCount > 0) {
         vifm.add(new SeparatorField(), 6);
         if (decryptFailureCount > 0) {
            this._decryptFailure = new LabelField(OptionsResources.getString(2025), 1170935903116328960L);
            c = new String[]{String.valueOf(decryptFailureCount)};
            this._decryptFailureCountLabel = new LabelField(MessageFormat.format(blocked, c), 36028797018963968L);
            vifm.add(new FirewallOptionsItem$FieldAndCountManager(this._decryptFailure, this._decryptFailureCountLabel), 6);
         }

         if (notEncryptedErrorCount > 0) {
            this._notEncryptedError = new LabelField(OptionsResources.getString(2026), 1170935903116328960L);
            c = new String[]{String.valueOf(notEncryptedErrorCount)};
            this._notEncryptedErrorCountLabel = new LabelField(MessageFormat.format(blocked, c), 36028797018963968L);
            vifm.add(new FirewallOptionsItem$FieldAndCountManager(this._notEncryptedError, this._notEncryptedErrorCountLabel), 6);
         }

         if (addressMismatchErrorCount > 0) {
            this._addressMismatch = new LabelField(OptionsResources.getString(2031), 1170935903116328960L);
            c = new String[]{String.valueOf(addressMismatchErrorCount)};
            this._addressMismatchCountLabel = new LabelField(MessageFormat.format(blocked, c), 36028797018963968L);
            vifm.add(new FirewallOptionsItem$FieldAndCountManager(this._addressMismatch, this._addressMismatchCountLabel), 6);
         }

         if (invalidDatagramCount > 0) {
            this._invalidDatagram = new LabelField(OptionsResources.getString(2071), 1170935903116328960L);
            c = new String[]{String.valueOf(invalidDatagramCount)};
            this._invalidDatagramCountLabel = new LabelField(MessageFormat.format(blocked, c), 36028797018963968L);
            vifm.add(new FirewallOptionsItem$FieldAndCountManager(this._invalidDatagram, this._invalidDatagramCountLabel), 6);
         }

         if (mismatchedKeyCount > 0) {
            this._mismatchedKey = new LabelField(OptionsResources.getString(2072), 1170935903116328960L);
            c = new String[]{String.valueOf(mismatchedKeyCount)};
            this._mismatchedKeyCountLabel = new LabelField(MessageFormat.format(blocked, c), 36028797018963968L);
            vifm.add(new FirewallOptionsItem$FieldAndCountManager(this._mismatchedKey, this._mismatchedKeyCountLabel), 6);
         }
      }

      mainScreen.add(vifm);
      this._optionsAppEventLock = Application.getEventLock();
      Firewall.getInstance().addBlockedCountListener(this);
   }

   @Override
   public final void blockedCountIncremented(byte type, int count) {
      LabelField label = null;
      switch (type) {
         case -3:
         case 0:
            break;
         case -2:
            label = this._notEncryptedErrorCountLabel;
            break;
         case -1:
            label = this._decryptFailureCountLabel;
            break;
         case 1:
         default:
            label = this._smsCountLabel;
            break;
         case 2:
            label = this._mmsCountLabel;
            break;
         case 3:
            label = this._bisCountLabel;
            break;
         case 4:
            label = this._pinCountLabel;
      }

      if (label != null) {
         String[] c = new String[]{String.valueOf(count)};
         String text = MessageFormat.format(OptionsResources.getString(1996), c);
         synchronized (this._optionsAppEventLock) {
            label.setText(text);
         }
      }
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      verbToMenu.addVerb(new FirewallOptionsItem$ResetVerb());
      verbToMenu.addVerb(new FirewallOptionsItem$DefaultPermissionsVerb());
      verbToMenu.addVerb(new FirewallOptionsItem$ResetAllCountsVerb(this));
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      super.addCurrentItemVerbs(verbToMenu, instance);
      Field field = UiApplication.getUiApplication().getActiveScreen().getLeafFieldWithFocus();
      byte type = 0;
      LabelField lf = null;
      if (field instanceof CheckboxField) {
         if (field == this._blockSMS) {
            type = 1;
            lf = this._smsCountLabel;
         } else if (field == this._blockMMS) {
            type = 2;
            lf = this._mmsCountLabel;
         } else if (field == this._blockPIN) {
            type = 4;
            lf = this._pinCountLabel;
         } else if (field == this._blockBIS) {
            type = 3;
            lf = this._bisCountLabel;
         }
      } else if (field instanceof LabelField) {
         if (field == this._decryptFailure) {
            type = -1;
            lf = this._decryptFailureCountLabel;
         }

         if (field == this._notEncryptedError) {
            type = -2;
            lf = this._notEncryptedErrorCountLabel;
         }
      }

      if (type != 0 && lf != null) {
         verbToMenu.addVerb(new FirewallOptionsItem$ResetCountVerb(type, lf));
      }

      return null;
   }

   @Override
   protected final boolean save() {
      Firewall fw = Firewall.getInstance();
      fw.setEnabled(this._enabledField.isAffirmative());
      if (!fw.isBlockingEnabledByItPolicy((byte)1)) {
         fw.setBlocking((byte)1, this._blockSMS.getChecked());
      }

      if (!fw.isBlockingEnabledByItPolicy((byte)2)) {
         fw.setBlocking((byte)2, this._blockMMS.getChecked());
      }

      if (!fw.isBlockingEnabledByItPolicy((byte)4)) {
         fw.setBlocking((byte)4, this._blockPIN.getChecked());
      }

      if (!fw.isBlockingEnabledByItPolicy((byte)3)) {
         fw.setBlocking((byte)3, this._blockBIS.getChecked());
      }

      return super.save();
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean close = super.confirm(verb, context);
      if (close) {
         Firewall.getInstance().removeBlockedCountListener(this);
      }

      return close;
   }
}
