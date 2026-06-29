package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.apps.internal.mms.service.MMSService;
import net.rim.device.internal.EScreens.EScreenAccess;

final class MMSOptionsScreen extends SaveableMainScreenOptionsListItem {
   private MainScreen _mainScreen;
   private boolean _hasMMSCEditFields;

   MMSOptionsScreen() {
      super(MMSResources.getString(9));
      ContextObject.put(super._context, 244, "mms_messages");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      MMSOptions options = MMSOptions.getInstance();
      this._mainScreen = mainScreen;
      this._hasMMSCEditFields = false;
      String prompt = MMSResources.getString(38);
      int choiceType = MMSClientServiceBook.allowHomeOnly() ? 1 : 0;
      mainScreen.add(new MMSOptionsScreen$1(this, choiceType, prompt, options.getReceptionMode()));
      prompt = MMSResources.getString(39);
      choiceType = MMSClientServiceBook.allowHomeOnly() ? 1 : 0;
      choiceType = this.canEditAutoRetrievalMode() ? choiceType : 3;
      mainScreen.add(new MMSOptionsScreen$2(this, choiceType, prompt, options.getAutomaticRetrievalMode()));
      int lockedOptions = MMSClientServiceBook.getLockedOptionsFlags();
      int allOptions = 195;
      if ((lockedOptions & allOptions) != allOptions) {
         mainScreen.add(new SeparatorField());
         mainScreen.add(new LabelField(MMSResources.getString(29), 36028797018963968L));
         if (!MMSClientServiceBook.isLockedOption(1)) {
            mainScreen.add(new CheckboxOptionField(30, 1, true));
         }

         if (!MMSClientServiceBook.isLockedOption(2)) {
            mainScreen.add(new CheckboxOptionField(31, 2, true));
         }

         if (!MMSClientServiceBook.isLockedOption(64)) {
            mainScreen.add(new CheckboxOptionField(71, 64, false));
         }

         if (!MMSClientServiceBook.isLockedOption(128)) {
            mainScreen.add(new CheckboxOptionField(72, 128, false));
         }
      }

      int var10 = 12;
      if ((lockedOptions & var10) != var10) {
         mainScreen.add(new SeparatorField());
         mainScreen.add(new LabelField(MMSResources.getString(32), 36028797018963968L));
         if (!MMSClientServiceBook.isLockedOption(4)) {
            mainScreen.add(new CheckboxOptionField(33, 4));
         }

         if (!MMSClientServiceBook.isLockedOption(8)) {
            mainScreen.add(new CheckboxOptionField(34, 8));
         }
      }
   }

   @Override
   protected final boolean save() {
      int count = this._mainScreen.getFieldCount();

      for (int idx = 0; idx < count; idx++) {
         Field field = this._mainScreen.getField(idx);
         if (field instanceof MMSOptionsScreen$Saveable) {
            ((MMSOptionsScreen$Saveable)field).saveOption();
         }
      }

      this._mainScreen = null;
      return super.save();
   }

   @Override
   protected final boolean discard() {
      this._mainScreen = null;
      return super.discard();
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      if (!BackdoorKeyProcessor.isDevelopmentDevice() && !EScreenAccess.isAllowed()) {
         if (backdoorCode == 1296913219) {
            this.addMMSCBasicFields(false);
         }

         return super.openProductionBackdoor(backdoorCode);
      } else {
         switch (backdoorCode) {
            case 1280264002:
               if (this._mainScreen.getFieldWithFocusIndex() == 0) {
                  MMSOptions options = MMSOptions.getInstance();
                  boolean value = !options.getOptionFlag(256);
                  options.setOptionFlag(256, value);
                  Dialog.inform("Dump send/receive bytes = " + value);
               }
               break;
            case 1280264006:
               if (this._mainScreen.getFieldWithFocusIndex() == 0) {
                  MMSOptions options = MMSOptions.getInstance();
                  boolean value = !options.getOptionFlag(512);
                  options.setOptionFlag(512, value);
                  Dialog.inform("Dump pdu fields = " + value);
               }
               break;
            case 1296913219:
               if (this._mainScreen.getFieldWithFocusIndex() == 0) {
                  this.addMMSCEditFields();
               }
               break;
            case 1397573964:
               if (this._mainScreen.getFieldWithFocusIndex() == 0) {
                  MMSOptions options = MMSOptions.getInstance();
                  boolean value = !options.getOptionFlag(32);
                  options.setOptionFlag(32, value);
                  Dialog.inform(value ? "Display using raw attachments." : "Display using presentation layout.");
               }
               break;
            case 1413829460:
               if (this._mainScreen.getFieldWithFocusIndex() == 0) {
                  MMSService.testConnectionToMMSC();
                  this.addMMSCEditFields();
               }
               break;
            case 1430736197:
               if (this._mainScreen.getFieldWithFocusIndex() == 0) {
                  MMSOptions options = MMSOptions.getInstance();
                  boolean value = !options.getOptionFlag(16);
                  options.setOptionFlag(16, value);
                  Dialog.inform(value ? "Use GME transport." : "Use default transport.");
               }
         }

         return super.openProductionBackdoor(backdoorCode);
      }
   }

   private final void addMMSCBasicFields(boolean editable) {
      this._mainScreen.add(new SeparatorField());
      this._mainScreen.add(new LabelField("MMSC Access:", 36028797018963968L));
      String ppgURL = MMSTransportServiceBook.getPPGAddress();
      if (ppgURL != null) {
         Field field = new PPGUrlOptionField(ppgURL);
         field.setEditable(editable);
         this._mainScreen.add(field);
      }

      String hostIP = MMSTransportServiceBook.getHostIP();
      if (hostIP != null) {
         Field field = new HostIPOptionField(hostIP);
         field.setEditable(editable);
         this._mainScreen.add(field);
      }

      Field field = new MMSCUrlOptionField();
      field.setEditable(editable);
      this._mainScreen.add(field);
      if (MMSTransportServiceBook.hasMMSServiceRecord() && !MMSTransportServiceBook.isWAPServiceRecord()) {
         String proxyAddress = MMSTransportServiceBook.getProxyAddress();
         if (proxyAddress == null) {
            proxyAddress = "";
         }

         field = new MMSCProxyOptionField(proxyAddress);
         field.setEditable(editable);
         this._mainScreen.add(field);
      }

      Field var9 = new MMSCUsernameOptionField();
      var9.setEditable(editable);
      this._mainScreen.add(var9);
      Field var10 = new MMSCPasswordOptionField();
      var10.setEditable(editable);
      this._mainScreen.add(var10);
      Field var11 = new MMSCUserAgentNameField();
      var11.setEditable(editable);
      this._mainScreen.add(var11);
   }

   private final void addMMSCEditFields() {
      if (!this._hasMMSCEditFields) {
         this.addMMSCBasicFields(true);
         String apn = MMSTransportServiceBook.getAPN();
         if (apn != null) {
            this._mainScreen.add(new ApnOptionField(apn));
         }

         this._mainScreen.add(new SeparatorField());
         this._mainScreen.add(new LabelField("Client Configuration:", 36028797018963968L));
         this._mainScreen.add(new MMSCVersionOptionField());
         this._mainScreen.add(new UAProfUrlOptionField());
         this._mainScreen.add(new ConnectionTimeoutOptionField());
         this._mainScreen.add(new AuthenticationHeaderOptionField());
         this._mainScreen.add(new SendTextAsSimpleContentOptionField());
         this._mainScreen.add(new InferAcknowledgementUrlOptionField());
         this._mainScreen.add(new RetrievalUrlSchemeOptionField());
         this._mainScreen.add(new MessageUrlPrefixOptionField());
         this._mainScreen.add(new MaximumTransportThreadOptionField());
         this._mainScreen.add(new AllowHomeOnlyOptionField());
         this._mainScreen.add(new FromFieldSchemeOptionField());
         if (RadioInfo.getNetworkType() == 4) {
            this._mainScreen.add(new AddressingSchemeOptionField());
         }

         String prompt = MMSResources.getString(39);
         int choiceType = 2;
         this._mainScreen.add(new MMSOptionsScreen$3(this, choiceType, prompt, MMSClientServiceBook.getAutoRetrievalMode()));
         this._mainScreen.add(new OneVideoPerMMSOptionField());
         this._mainScreen.add(new SeparatorField());
         this._mainScreen.add(new LabelField("Restricted Mode Settings", 36028797018963968L));
         this._mainScreen.add(new RestrictedSizeModeOptionField());
         this._mainScreen.add(new MaximumMessageSizeOptionField());
         this._mainScreen.add(new RestrictedSendModeOptionField());
         this._mainScreen.add(new MaximumRecipientSizeOptionField());
         this._mainScreen.add(new MaximumRecipientCountOptionField());
         this._mainScreen.add(new MaximumTextLengthOptionField());
         this._mainScreen.add(new MaximumImageWidthOptionField());
         this._mainScreen.add(new MaximumImageHeightOptionField());
         this._mainScreen.add(new AllowImageReductionBeforeSendOptionField());
         this._mainScreen.add(new SeparatorField());
         this._mainScreen.add(new LabelField("Voice Note Settings:", 36028797018963968L));
         this._mainScreen.add(new MaximumVoiceNoteRecordTimeOptionField());
         this._mainScreen.add(new MaximumVoiceNoteRecordSizeOptionField());
         this._mainScreen.add(new SeparatorField());
         this._mainScreen.add(new LabelField("Default Options:", 36028797018963968L));
         prompt = "Multimedia Reception:";
         int var6 = 1;
         this._mainScreen.add(new MMSOptionsScreen$4(this, var6, prompt, MMSClientServiceBook.getDefaultReceptionMode()));
         prompt = "Automatic Retrieval:";
         var6 = 1;
         this._mainScreen.add(new MMSOptionsScreen$5(this, var6, prompt, MMSClientServiceBook.getDefaultAutomaticRetrievalMode()));
         this._mainScreen.add(new SBCheckboxOptionField("Allow Delivery Confirmation", 1, true));
         this._mainScreen.add(new SBCheckboxOptionField("Allow Read Confirmation", 2, true));
         this._mainScreen.add(new SBCheckboxOptionField("Confirm Delivery", 64, false));
         this._mainScreen.add(new SBCheckboxOptionField("Confirm Read", 128, false));
         this._mainScreen.add(new SBCheckboxOptionField("Reject Anonymous Messages", 4, false));
         this._mainScreen.add(new SBCheckboxOptionField("Reject Advertisements", 8, false));
         this._mainScreen.add(new SeparatorField());
         this._mainScreen.add(new LabelField("Locked Options:", 36028797018963968L));
         this._mainScreen.add(new SBLockedCheckboxOptionField("Allow Delivery Confirmation", 1, false));
         this._mainScreen.add(new SBLockedCheckboxOptionField("Allow Read Confirmation", 2, false));
         this._mainScreen.add(new SBLockedCheckboxOptionField("Confirm Delivery", 64, false));
         this._mainScreen.add(new SBLockedCheckboxOptionField("Confirm Read", 128, false));
         this._mainScreen.add(new SBLockedCheckboxOptionField("Reject Anonymous Messages", 4, false));
         this._mainScreen.add(new SBLockedCheckboxOptionField("Reject Advertisements", 8, false));
         if (MMSTransportServiceBook.isWAPServiceRecord()) {
            this._mainScreen.add(new SeparatorField());
            this._mainScreen.add(new LabelField("WAP Configuration:", 36028797018963968L));
            this._mainScreen.add(new MMSCWAPAccessModeField());
            this._mainScreen.add(new MMSCWTLSModeField());
            this._mainScreen.add(new MMSCWTLSClientTypeField());
            this._mainScreen.add(new MMSCWAP20ConformanceField());
         }

         this._hasMMSCEditFields = true;
      }
   }

   private final boolean canEditAutoRetrievalMode() {
      return MMSClientServiceBook.getAutoRetrievalMode() == -1;
   }
}
