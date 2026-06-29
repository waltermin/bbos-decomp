package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Audio;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.EnhanceCallAudioServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.InternalServices;

final class GeneralPhoneOption extends VoiceOptionsListItem {
   private ObjectChoiceField _blockIdentityField;
   private ObjectChoiceField _autoCallAnswerChoiceField;
   private ObjectChoiceField _autoCallHangupChoiceField;
   private BooleanChoiceField _confirmDelete;
   private ObjectChoiceField _phoneListViewField;
   private BooleanChoiceField _dialFromHomeScreenField;
   private BooleanChoiceField _showMyNumberField;
   private ObjectChoiceField _defaultCallVolumeField;
   private ObjectChoiceField _defaultEnhanceCallAudioField;
   private ObjectChoiceField _ringtoneLightField;
   private ObjectChoiceField _currentLineField;
   private static final int VOLUME_CHOICE_INCREMENT = 25;

   public GeneralPhoneOption(Object context) {
      super(PhoneResources.getString(504), context);
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      int[] lineIds = PhoneUtilities.getAllLineIds();
      if (lineIds.length > 1) {
         String[] lineDesc = new String[lineIds.length];

         for (int i = lineIds.length - 1; i >= 0; i--) {
            lineDesc[i] = PhoneUtilities.getLineDescription(lineIds[i]);
            if (lineDesc[i] == null) {
               lineDesc[i] = PhoneUtilities.getLineNumber(lineIds[i], false);
               if (lineDesc[i] == null) {
                  lineDesc[i] = PhoneResources.getString(117);
               }
            }
         }

         if (lineDesc.length > 1) {
            int currentLine = PhoneUtilities.getCurrentLineId();

            for (int i = lineIds.length - 1; i >= 0; i--) {
               if (currentLine == lineIds[i]) {
                  this._currentLineField = new ObjectChoiceField(PhoneResources.getString(6307), lineDesc, i);
                  screen.add(this._currentLineField);
                  break;
               }
            }
         }
      }

      if (Audio.hasBuiltInHeadset()) {
         this._autoCallAnswerChoiceField = new ObjectChoiceField(PhoneResources.getString(5), this.getCallAnswerArray(), this.getCallAnswerIndex());
         this._autoCallHangupChoiceField = new ObjectChoiceField(PhoneResources.getString(6), this.getCallHangupArray(), this.getCallHangupIndex());
         screen.add(this._autoCallAnswerChoiceField);
         screen.add(this._autoCallHangupChoiceField);
      }

      this._confirmDelete = new BooleanChoiceField(CommonResources.getString(2008), 0, !super._phoneOptions.getBooleanOption(8192));
      screen.add(this._confirmDelete);
      if (PhoneUtilities.canBlockIdentity()) {
         int initialIndex = 2;
         switch (super._phoneOptions.getCLIR()) {
            case -1:
               break;
            case 0:
               initialIndex = 2;
               break;
            case 1:
            default:
               initialIndex = 0;
               break;
            case 2:
               initialIndex = 1;
         }

         this._blockIdentityField = new ObjectChoiceField(
            PhoneResources.getString(456),
            new String[]{PhoneResources.getString(6309), PhoneResources.getString(6330), PhoneResources.getString(6334)},
            initialIndex
         );
         screen.add(this._blockIdentityField);
      }

      int phoneListViewType = PhoneOptions.getOptions().getPhoneListViewType();
      this._phoneListViewField = new ObjectChoiceField(PhoneResources.getString(6245), this.getPhoneListViewChoices(), phoneListViewType);
      screen.add(this._phoneListViewField);
      if (!PhoneUtilities.isQwertyReducedKeyboard()) {
         this._dialFromHomeScreenField = new BooleanChoiceField(PhoneResources.getString(6039), 0, super._phoneOptions.getBooleanOption(16384));
         screen.add(this._dialFromHomeScreenField);
      }

      if (lineIds.length < 2) {
         this._showMyNumberField = new BooleanChoiceField(PhoneResources.getString(6085), 0, !super._phoneOptions.getBooleanOption(65536));
         screen.add(this._showMyNumberField);
      }

      int defaultVolumeSetting = super._phoneOptions.getDefaultCallVolume();
      int volumeChoiceIndex = defaultVolumeSetting / 25;
      this._defaultCallVolumeField = new ObjectChoiceField(PhoneResources.getString(6129), this.getDefaultCallVolumeChoices(), volumeChoiceIndex);
      screen.add(this._defaultCallVolumeField);
      if (EnhanceCallAudioServices.getInstance().isECASupported()) {
         int defaultECASetting = super._phoneOptions.getDefaultEnhanceCallAudio();
         this._defaultEnhanceCallAudioField = new ObjectChoiceField(PhoneResources.getString(6324), this.getDefaultECAChoices(), defaultECASetting);
         screen.add(this._defaultEnhanceCallAudioField);
      }

      if (InternalServices.isDeviceCapable(23)) {
         String[] ringtoneLightStyles = new String[]{CommonResources.getString(107), PhoneResources.getString(6302)};
         this._ringtoneLightField = new ObjectChoiceField(PhoneResources.getString(6301), ringtoneLightStyles, 0, 134217728);
         this._ringtoneLightField.setSelectedIndex(super._phoneOptions.getRingtoneLightStyle());
         screen.add(this._ringtoneLightField);
      }
   }

   @Override
   protected final boolean save() {
      boolean commitRequired = false;
      if (this._autoCallAnswerChoiceField != null && this._autoCallAnswerChoiceField.isDirty()) {
         boolean autoCallAnswer = this._autoCallAnswerChoiceField.getSelectedIndex() == 1;
         super._phoneOptions.setBooleanOption(256, autoCallAnswer);
         commitRequired = true;
      }

      if (this._autoCallHangupChoiceField != null && this._autoCallHangupChoiceField.isDirty()) {
         boolean autoCallHangup = this._autoCallHangupChoiceField.getSelectedIndex() == 1;
         super._phoneOptions.setBooleanOption(512, autoCallHangup);
         commitRequired = true;
      }

      if (this._confirmDelete != null && this._confirmDelete.isDirty()) {
         super._phoneOptions.setBooleanOption(8192, !this._confirmDelete.isAffirmative());
         commitRequired = true;
      }

      if (this._blockIdentityField != null && this._blockIdentityField.isDirty()) {
         switch (this._blockIdentityField.getSelectedIndex()) {
            case -1:
               break;
            case 0:
            default:
               super._phoneOptions.setBooleanOption(32, true);
               super._phoneOptions.setBooleanOption(131072, false);
               break;
            case 1:
               super._phoneOptions.setBooleanOption(32, false);
               super._phoneOptions.setBooleanOption(131072, true);
               break;
            case 2:
               super._phoneOptions.setBooleanOption(32, false);
               super._phoneOptions.setBooleanOption(131072, false);
         }

         commitRequired = true;
      }

      if (this._phoneListViewField != null && this._phoneListViewField.isDirty()) {
         int newViewType = this._phoneListViewField.getSelectedIndex();
         int currViewType = super._phoneOptions.getPhoneListViewType();
         if (newViewType != currViewType && (newViewType == 3 || currViewType == 3)) {
            RIMGlobalMessagePoster.postGlobalEvent(3206808455257958298L);
         }

         super._phoneOptions.setPhoneListViewType(newViewType);
         commitRequired = true;
      }

      if (this._dialFromHomeScreenField != null && this._dialFromHomeScreenField.isDirty()) {
         super._phoneOptions.setBooleanOption(16384, this._dialFromHomeScreenField.isAffirmative());
         commitRequired = true;
      }

      if (this._showMyNumberField != null && this._showMyNumberField.isDirty()) {
         super._phoneOptions.setBooleanOption(65536, !this._showMyNumberField.isAffirmative());
         commitRequired = true;
      }

      if (this._currentLineField != null && this._currentLineField.isDirty()) {
         int[] lineIds = PhoneUtilities.getAllLineIds();
         if (!PhoneUtilities.setCurrentLine(lineIds[this._currentLineField.getSelectedIndex()])) {
            Dialog.inform("Line not set");
         }
      }

      if (this._defaultCallVolumeField != null && this._defaultCallVolumeField.isDirty()) {
         int index = this._defaultCallVolumeField.getSelectedIndex();
         int setting = 25 * index;
         super._phoneOptions.setDefaultCallVolume(setting);
         commitRequired = true;
      }

      if (this._defaultEnhanceCallAudioField != null && this._defaultEnhanceCallAudioField.isDirty()) {
         int index = this._defaultEnhanceCallAudioField.getSelectedIndex();
         super._phoneOptions.setDefaultEnhanceCallAudio(index);
         commitRequired = true;
      }

      if (this._ringtoneLightField != null && this._ringtoneLightField.isDirty()) {
         int setting = this._ringtoneLightField.getSelectedIndex();
         super._phoneOptions.setRingtoneLightStyle(setting);
         commitRequired = true;
      }

      if (commitRequired) {
         super._phoneOptions.commit();
      }

      return super.save();
   }

   private final String[] getCallAnswerArray() {
      return new String[]{CommonResources.getString(2014), PhoneResources.getString(7)};
   }

   private final int getCallAnswerIndex() {
      return !PhoneOptions.getOptions().getBooleanOption(256) ? 0 : 1;
   }

   private final String[] getCallHangupArray() {
      return new String[]{CommonResources.getString(2014), PhoneResources.getString(8)};
   }

   private final String[] getPhoneListViewChoices() {
      return new String[]{PhoneResources.getString(6035), PhoneResources.getString(6037), PhoneResources.getString(6034), PhoneResources.getString(6244)};
   }

   private final String[] getDefaultCallVolumeChoices() {
      return new String[]{PhoneResources.getString(6130), "25%", "50%", "75%", "100%"};
   }

   private final String[] getDefaultECAChoices() {
      return new String[]{PhoneResources.getString(6130), PhoneResources.getString(6327), PhoneResources.getString(6325), PhoneResources.getString(6326)};
   }

   private final int getCallHangupIndex() {
      return !PhoneOptions.getOptions().getBooleanOption(512) ? 0 : 1;
   }

   @Override
   public final int getOptionsScreenOrder() {
      return 2000;
   }
}
