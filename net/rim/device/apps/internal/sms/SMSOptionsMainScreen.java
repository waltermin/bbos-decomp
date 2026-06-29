package net.rim.device.apps.internal.sms;

import java.util.Vector;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.system.SMSParameters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.sms.resources.SMSResources;
import net.rim.device.internal.deviceoptions.SMSOptions;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.ui.component.PhoneNumberEditField;

class SMSOptionsMainScreen extends AppsMainScreen implements FieldChangeListener {
   private PhoneNumberEditField _serviceCenterField;
   private PhoneNumberEditField _callbackNumberField;
   private ObjectChoiceField _sentAsField;
   private ObjectChoiceField _messageCodingField;
   private ObjectChoiceField _messageClassField;
   private ObjectChoiceField _priorityField;
   private ObjectChoiceField _validityPeriodField;
   private ObjectChoiceField _deliveryPeriodField;
   private ObjectChoiceField _userInterfaceField;
   private ObjectChoiceField _messageListUiField;
   private ObjectChoiceField _routeField;
   private BooleanChoiceField _deliveryReportsField;
   private BooleanChoiceField _multipleRecipientsField;
   private BooleanChoiceField _storeOnSimField;
   private BooleanChoiceField _disableAutoTextField;
   private NumericChoiceField _numPreviousItemsField;
   private int _fieldFlags;
   private static final int SHOW_SERVICE_CENTRE = 1;
   private static final int SHOW_CALLBACK_NUMBER = 2;
   private static final int SHOW_SENT_AS = 4;
   private static final int SHOW_MESSAGE_CODING = 8;
   private static final int SHOW_MESSAGE_CLASS = 16;
   private static final int SHOW_PRIORITY = 32;
   private static final int SHOW_VALIDITY_PERIOD = 64;
   private static final int SHOW_DELIVERY_PERIOD = 128;
   private static final int SHOW_MULTIPLE_RECIPIENTS = 256;
   private static final int SHOW_ROUTE = 512;
   private static final int SHOW_DELIVERY_REPORTS = 1024;
   private static final int SHOW_STORE_ON_SIM = 2048;
   private static final int SHOW_NUM_PREVIOUS_ITEMS = 4096;
   private static final int SHOW_DISABLE_AUTO_TEXT = 8192;
   private static final int SHOW_ALL_OPTIONS = 16384;
   private static final int SENT_AS_TEXT = 0;
   private static final int SENT_AS_FAX3 = 1;
   private static final int SENT_AS_FAX4 = 2;
   private static final int SENT_AS_VOICE = 3;
   private static final int SENT_AS_ERMES = 4;
   private static final int MSG_CODING_GPRS_DEFAULT = 0;
   private static final int MSG_CODING_GPRS_UCS2 = 1;
   private static final int MSG_CODING_GPRS_8BIT = 2;
   private static final int MSG_CODING_CDMA_ASCII = 0;
   private static final int MSG_CODING_CDMA_ISO8859 = 1;
   private static final int MESSAGE_CLASS_0 = 0;
   private static final int MESSAGE_CLASS_1 = 1;
   private static final int MESSAGE_CLASS_2 = 2;
   private static final int MESSAGE_CLASS_3 = 3;
   private static final int MESSAGE_CLASS_NOT_GIVEN = 4;
   private static final int PRIORITY_NORMAL = 0;
   private static final int PRIORITY_URGENT = 1;
   private static final int VALIDITY_PERIOD_15_MINUTES = 0;
   private static final int VALIDITY_PERIOD_30_MINUTES = 1;
   private static final int VALIDITY_PERIOD_1_HOUR = 2;
   private static final int VALIDITY_PERIOD_3_HOURS = 3;
   private static final int VALIDITY_PERIOD_6_HOURS = 4;
   private static final int VALIDITY_PERIOD_12_HOURS = 5;
   private static final int VALIDITY_PERIOD_1_DAY = 6;
   private static final int VALIDITY_PERIOD_3_DAYS = 7;
   private static final int VALIDITY_PERIOD_1_WEEK = 8;
   private static final int VALIDITY_PERIOD_2_WEEKS = 9;
   private static final int VALIDITY_PERIOD_MAX = 10;
   private static final int DELIVERY_PERIOD_IMMEDIATE = 0;
   private static final int DELIVERY_PERIOD_15_MINUTES = 1;
   private static final int DELIVERY_PERIOD_30_MINUTES = 2;
   private static final int DELIVERY_PERIOD_1_HOUR = 3;
   private static final int DELIVERY_PERIOD_3_HOURS = 4;
   private static final int DELIVERY_PERIOD_6_HOURS = 5;
   private static final int DELIVERY_PERIOD_12_HOURS = 6;
   private static final int DELIVERY_PERIOD_1_DAY = 7;
   private static final int DELIVERY_PERIOD_3_DAYS = 8;
   private static final int DELIVERY_PERIOD_1_WEEK = 9;
   private static final int DELIVERY_PERIOD_2_WEEKS = 10;
   private static final int MAX_CALLBACK_LENGTH = 20;
   private static final int[] umtsRouteChoices = new int[]{1, 4, -804651006, 39, 40, -805044219, 1718183726, 10};
   private static final int[] gprsRouteChoices = new int[]{
      500, 501, 402, 1963524352, 151060486, -62060939, 10053716, 1819232257, 16780059, 1688366946, 207814912, 1816300141
   };
   private static final int[] RouteChoices = RadioInfo.getNetworkType() == 7 ? umtsRouteChoices : gprsRouteChoices;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   SMSOptionsMainScreen() {
      super(0);
      this.setBackdoorAltStatus(false);
      this.setTitle(SMSResources.getResourceBundle(), 610);
      this.setHelp("sms_messages");
      this._fieldFlags = 0;
      this.setFlag(8192, SMSPacketHeader.isSendSupported());
      int supportedWAFs = RadioInfo.getSupportedWAFs();
      if ((supportedWAFs & 2) != 0) {
         this.setFlag(1024, CDMAInfo.getSMSStatusReportRequest());
         this.setFlag(128, CDMAInfo.getSMSDeliveryTimerSupport());
         this.setFlag(32, true);
         this.setFlag(4096, true);
         this.setFlag(2, true);
         if ((supportedWAFs & 1) != 0) {
            this.setFlag(1, true);
            return;
         }
      } else {
         if ((supportedWAFs & 1) != 0) {
            this.setFlag(8, true);
            this.setFlag(2048, true);
            boolean var4 = false /* VF: Semaphore variable */;

            label53:
            try {
               var4 = true;
               if ((SIMCard.getCSPFlags(4) & 8) != 0) {
                  this.setFlag(1024, true);
               }

               if ((SIMCard.getCSPFlags(4) & 2) != 0) {
                  this.setFlag(64, true);
               }

               if ((SIMCard.getCSPFlags(4) & 4) != 0) {
                  this.setFlag(4, true);
                  var4 = false;
               } else {
                  var4 = false;
               }
            } finally {
               if (var4) {
                  this.setFlag(1024, true);
                  this.setFlag(64, true);
                  this.setFlag(4, true);
                  break label53;
               }
            }

            this.setFlag(4096, true);
            this.setFlag(1, true);
            this.setFlag(512, true);
            return;
         }

         if ((supportedWAFs & 8) != 0) {
            this.setFlag(2048, true);
         }
      }
   }

   public void rebuildFieldCollection() {
      if (this.getFieldCount() > 0) {
         this.deleteAll();
      }

      this.setFlag(16384, false);
      SMSParameters smsp = (SMSParameters)(new Object());

      label485:
      try {
         RadioInternal.getDefaultSMSParameters(smsp);
      } finally {
         break label485;
      }

      String serviceCenter = smsp.getSCAddress();
      String callbackNumber = smsp.getCallbackAddress();
      if (callbackNumber == null) {
         label481:
         try {
            callbackNumber = Phone.getInstance().getNumber(0);
         } finally {
            break label481;
         }
      }

      int sentAsIndex;
      switch (smsp.getProtocolId()) {
         case 1:
            sentAsIndex = 0;
            break;
         case 2:
         default:
            sentAsIndex = 1;
            break;
         case 3:
            sentAsIndex = 2;
            break;
         case 4:
            sentAsIndex = 3;
            break;
         case 5:
            sentAsIndex = 4;
      }

      int msgCodingIndex;
      if ((RadioInfo.getSupportedWAFs() & 2) != 0) {
         switch (smsp.getMessageCoding()) {
            case 5:
               msgCodingIndex = 1;
               break;
            default:
               msgCodingIndex = 0;
         }
      } else {
         switch (smsp.getMessageCoding()) {
            case 0:
               msgCodingIndex = 0;
               break;
            case 1:
            default:
               msgCodingIndex = 2;
               break;
            case 2:
               msgCodingIndex = 1;
         }
      }

      int msgClassIndex;
      switch (smsp.getMessageClass()) {
         case -1:
         case 1:
            msgClassIndex = 1;
            break;
         case 0:
         default:
            msgClassIndex = 0;
            break;
         case 2:
            msgClassIndex = 2;
            break;
         case 3:
            msgClassIndex = 3;
            break;
         case 4:
            msgClassIndex = 4;
      }

      int priorityIndex;
      switch (smsp.getPriority()) {
         case 2:
            priorityIndex = 1;
            break;
         default:
            priorityIndex = 0;
      }

      this._disableAutoTextField = (BooleanChoiceField)(new Object(SMSResources.getString(386), 0, SMSOptions.getDisableAutoText()));
      if (this.getFlag(8192)) {
         this.add(this._disableAutoTextField);
      }

      this._storeOnSimField = (BooleanChoiceField)(new Object(SMSResources.getString(530), 0, SMSOptions.getStoreOnSIM()));
      if (this.getFlag(2048)) {
         this.add(this._storeOnSimField);
      }

      this._deliveryReportsField = (BooleanChoiceField)(new Object(SMSResources.getString(520), 1, SMSOptions.getDeliveryReports()));
      if (this.getFlag(1024)) {
         this.add(this._deliveryReportsField);
      }

      this._multipleRecipientsField = (BooleanChoiceField)(new Object(SMSResources.getString(419), 1, SMSOptions.getMultipleRecipients()));
      if (this.getFlag(256)) {
         this.add(this._multipleRecipientsField);
      }

      String[] validityPeriodChoices = new Object[]{
         SMSResources.getString(460),
         SMSResources.getString(461),
         SMSResources.getString(462),
         SMSResources.getString(463),
         SMSResources.getString(464),
         SMSResources.getString(465),
         SMSResources.getString(466),
         SMSResources.getString(467),
         SMSResources.getString(468),
         SMSResources.getString(469),
         SMSResources.getString(470)
      };
      int valPeriodIndex = this.getValidityPeriodIndex(smsp.getValidityPeriod());
      this._validityPeriodField = (ObjectChoiceField)(new Object(SMSResources.getString(480), validityPeriodChoices, valPeriodIndex, 134217728));
      if (this.getFlag(64)) {
         this.add(this._validityPeriodField);
      }

      String[] deliveryPeriodChoices = new Object[]{
         SMSResources.getString(371),
         SMSResources.getString(460),
         SMSResources.getString(461),
         SMSResources.getString(462),
         SMSResources.getString(463),
         SMSResources.getString(464),
         SMSResources.getString(465),
         SMSResources.getString(466),
         SMSResources.getString(467),
         SMSResources.getString(468),
         SMSResources.getString(469)
      };
      int delPeriodIndex = this.getDeliveryPeriodIndex(smsp.getDeliveryPeriod());
      this._deliveryPeriodField = (ObjectChoiceField)(new Object(SMSResources.getString(490), deliveryPeriodChoices, delPeriodIndex));
      if (this.getFlag(128)) {
         this.add(this._deliveryPeriodField);
      }

      String[] sentAsChoices = new Object[]{
         SMSResources.getString(340), SMSResources.getString(341), SMSResources.getString(342), SMSResources.getString(343), SMSResources.getString(344)
      };
      this._sentAsField = (ObjectChoiceField)(new Object(SMSResources.getString(350), sentAsChoices, sentAsIndex));
      if (this.getFlag(4)) {
         this.add(this._sentAsField);
      }

      if ((RadioInfo.getSupportedWAFs() & 2) != 0) {
         String[] messageCodingChoices = new Object[]{SMSResources.getString(363), SMSResources.getString(364)};
         this._messageCodingField = (ObjectChoiceField)(new Object(SMSResources.getString(370), messageCodingChoices, msgCodingIndex));
      } else {
         String[] messageCodingChoices = new Object[]{SMSResources.getString(360), SMSResources.getString(362)};
         if (msgCodingIndex == 2) {
            Arrays.add(messageCodingChoices, SMSResources.getString(361));
         }

         this._messageCodingField = (ObjectChoiceField)(new Object(SMSResources.getString(370), messageCodingChoices, msgCodingIndex));
      }

      if (this.getFlag(8)) {
         this.add(this._messageCodingField);
      }

      String[] messageClassChoices = new Object[]{
         SMSResources.getString(380), SMSResources.getString(381), SMSResources.getString(382), SMSResources.getString(383), SMSResources.getString(384)
      };
      this._messageClassField = (ObjectChoiceField)(new Object(SMSResources.getString(390), messageClassChoices, msgClassIndex));
      String[] priorityChoices = new Object[]{SMSResources.getString(420), SMSResources.getString(422)};
      this._priorityField = (ObjectChoiceField)(new Object(SMSResources.getString(430), priorityChoices, priorityIndex));
      if (this.getFlag(32)) {
         this.add(this._priorityField);
      }

      this._numPreviousItemsField = (NumericChoiceField)(new Object(SMSResources.getString(310), 0, 50, 1, SMSOptions.getNumPreviousItems()));
      int filterType = 12;
      this._serviceCenterField = (PhoneNumberEditField)(new Object(SMSResources.getString(320), null, 20, filterType));

      label462:
      try {
         this._serviceCenterField.setText(serviceCenter);
      } finally {
         break label462;
      }

      if (this.getFlag(1)) {
         this.add(this._serviceCenterField);
      }

      this._callbackNumberField = (PhoneNumberEditField)(new Object(SMSResources.getString(330), null, 20, filterType));

      label456:
      try {
         this._callbackNumberField.setText(callbackNumber);
      } finally {
         break label456;
      }

      if (this.getFlag(2)) {
         this.add(this._callbackNumberField);
      }

      int routeIndex = this.routeToChoiceIndex(SMSOptions.getRoute());
      String[] routeObjectChoices = new Object[RouteChoices.length];
      int i = RouteChoices.length;

      while (--i >= 0) {
         routeObjectChoices[i] = SMSResources.getString(RouteChoices[i]);
      }

      this._routeField = (ObjectChoiceField)(new Object(SMSResources.getString(510), routeObjectChoices, routeIndex));
      if (this.getFlag(512)) {
         this.add(this._routeField);
      }

      SMSUiRegistry registry = SMSUiRegistry.getRegistry();
      Vector delegates = registry.getUiDelegates();
      if (delegates != null && delegates.size() > 0) {
         String[] userInterface = new Object[delegates.size() + 1];
         int index = 0;

         for (userInterface[index++] = SMSResources.getString(403); index <= delegates.size(); index++) {
            userInterface[index] = delegates.elementAt(index - 1).toString();
         }

         this._userInterfaceField = (ObjectChoiceField)(new Object(SMSResources.getString(752), userInterface, SMSOptions.getPresetUiId()));
         this.add(this._userInterfaceField);
         this._userInterfaceField.setChangeListener(this);
         String[] messageListUi = new Object[]{SMSResources.getString(406), SMSResources.getString(407)};
         this._messageListUiField = (ObjectChoiceField)(new Object(SMSResources.getString(405), messageListUi, SMSOptions.getMessageListUiId()));
         if (SMSUiRegistry.getRegistry().getCurrentUi() != null) {
            this.add(this._messageListUiField);
         }
      }

      if (this.getFlag(4096) && SMSUiRegistry.getRegistry().getCurrentUi() == null) {
         this.add(this._numPreviousItemsField);
      }
   }

   private void setFlag(int flag, boolean state) {
      if (state) {
         this._fieldFlags |= flag;
      } else {
         this._fieldFlags &= ~flag;
      }
   }

   public boolean hasFieldsToDisplay() {
      return this._fieldFlags != 0;
   }

   private boolean getFlag(int flag) {
      return (this._fieldFlags & flag) != 0;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._userInterfaceField) {
         if (this._userInterfaceField.getSelectedIndex() == 0) {
            if (this._numPreviousItemsField.getManager() == null) {
               this.insert(this._numPreviousItemsField, this._userInterfaceField.getIndex() + 1);
            }

            if (this._messageListUiField.getManager() != null) {
               this.delete(this._messageListUiField);
               return;
            }
         } else {
            if (this._numPreviousItemsField.getManager() != null) {
               this.delete(this._numPreviousItemsField);
            }

            if (this._messageListUiField.getManager() == null) {
               this.insert(this._messageListUiField, this._userInterfaceField.getIndex() + 1);
            }
         }
      }
   }

   private int getValidityPeriodIndex(int period) {
      if (period < 0 || period > 1209600) {
         return 10;
      } else if (period > 604800) {
         return 9;
      } else if (period > 259200) {
         return 8;
      } else if (period > 86400) {
         return 7;
      } else if (period > 43200) {
         return 6;
      } else if (period > 21600) {
         return 5;
      } else if (period > 10800) {
         return 4;
      } else if (period > 3600) {
         return 3;
      } else if (period > 1800) {
         return 2;
      } else {
         return period > 900 ? 1 : 0;
      }
   }

   private int getDeliveryPeriodIndex(int period) {
      if (period > 604800) {
         return 10;
      } else if (period > 259200) {
         return 9;
      } else if (period > 86400) {
         return 8;
      } else if (period > 43200) {
         return 7;
      } else if (period > 21600) {
         return 6;
      } else if (period > 10800) {
         return 5;
      } else if (period > 3600) {
         return 4;
      } else if (period > 1800) {
         return 3;
      } else if (period > 900) {
         return 2;
      } else {
         return period > 0 ? 1 : 0;
      }
   }

   @Override
   public boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1397576525:
            return super.openDevelopmentBackdoor(backdoorCode);
         case 1397576526:
         default:
            if (!this.getFlag(16384)) {
               this.setFlag(16384, true);
               this.add((Field)(new Object()));
               if (!this.getFlag(2048)) {
                  this.add(this._storeOnSimField);
               }

               if (!this.getFlag(1024)) {
                  this.add(this._deliveryReportsField);
               }

               if (!this.getFlag(64)) {
                  this.add(this._validityPeriodField);
               }

               if (!this.getFlag(128)) {
                  this.add(this._deliveryPeriodField);
               }

               if (!this.getFlag(4)) {
                  this.add(this._sentAsField);
               }

               if (!this.getFlag(8)) {
                  this.add(this._messageCodingField);
               }

               if (!this.getFlag(16)) {
                  this.add(this._messageClassField);
               }

               if (!this.getFlag(32)) {
                  this.add(this._priorityField);
               }

               if (!this.getFlag(256)) {
                  this.add(this._multipleRecipientsField);
               }

               if (!this.getFlag(4096) && SMSUiRegistry.getRegistry().getCurrentUi() == null) {
                  this.add(this._numPreviousItemsField);
               }

               if (!this.getFlag(1)) {
                  this.add(this._serviceCenterField);
               }

               if (!this.getFlag(2)) {
                  this.add(this._callbackNumberField);
               }

               if (!this.getFlag(512)) {
                  this.add(this._routeField);
                  return true;
               }
            }

            return true;
      }
   }

   @Override
   public boolean onSave() {
      String cbAddress = null;
      if (this.getFlag(2)) {
         cbAddress = this._callbackNumberField.getText();
      }

      String scAddress = null;
      if (this.getFlag(1)) {
         scAddress = this._serviceCenterField.getText();
         if (scAddress.length() == 0) {
            Status.show(SMSResources.getString(389), Bitmap.getPredefinedBitmap(2), 2000);
            return false;
         }
      }

      SMSOptions.setDisableAutoText(this._disableAutoTextField.isAffirmative());
      SMSOptions.setMultipleRecipients(this._multipleRecipientsField.isAffirmative());
      SMSOptions.setStoreOnSIM(this._storeOnSimField.isAffirmative());
      SMSOptions.setDeliveryReports(this._deliveryReportsField.isAffirmative());
      SMSOptions.setRoute(this.choiceIndexToRoute(this._routeField.getSelectedIndex()));
      SMSOptions.setNumPreviousItems(this._numPreviousItemsField.getSelectedIndex());
      int index = 0;
      if (this._userInterfaceField != null) {
         index = this._userInterfaceField.getSelectedIndex();
         SMSUiRegistry registry = SMSUiRegistry.getRegistry();
         Object currentDelegate = null;
         if (index != 0) {
            currentDelegate = registry.getUiDelegates().elementAt(index - 1);
            index = currentDelegate.hashCode();
         }

         registry.setCurrentUi(currentDelegate);
         SMSOptions.setUiId(index, true);
      }

      if (this._messageListUiField != null) {
         if (index != 0) {
            index = this._messageListUiField.getSelectedIndex();
         }

         if (SMSOptions.getMessageListUiId() != index) {
            SMSOptions.setMessageListUiId(index, true);
            Storage.changeMesssageListSMSUi();
         }
      }

      SMSParameters smsp = (SMSParameters)(new Object());

      label153:
      try {
         RadioInternal.getDefaultSMSParameters(smsp);
      } finally {
         break label153;
      }

      int delPeriod = this.getDeliveryPeriod(this._deliveryPeriodField.getSelectedIndex());
      smsp.setDeliveryPeriod(delPeriod);
      int valPeriod = this.getValidityPeriod(this._validityPeriodField.getSelectedIndex());
      smsp.setValidityPeriod(valPeriod);
      int priority;
      switch (this._priorityField.getSelectedIndex()) {
         case 1:
            priority = 2;
            break;
         default:
            priority = 4;
      }

      smsp.setPriority(priority);
      int msgClass;
      switch (this._messageClassField.getSelectedIndex()) {
         case -1:
         case 1:
            msgClass = 1;
            break;
         case 0:
         default:
            msgClass = 0;
            break;
         case 2:
            msgClass = 2;
            break;
         case 3:
            msgClass = 3;
            break;
         case 4:
            msgClass = 4;
      }

      smsp.setMessageClass(msgClass);
      int msgCoding;
      if ((RadioInfo.getSupportedWAFs() & 2) != 0) {
         switch (this._messageCodingField.getSelectedIndex()) {
            case 1:
               msgCoding = 5;
               break;
            default:
               msgCoding = 4;
         }
      } else {
         switch (this._messageCodingField.getSelectedIndex()) {
            case 0:
               msgCoding = 0;
               break;
            case 1:
               msgCoding = 2;
               break;
            case 2:
            default:
               msgCoding = 1;
         }
      }

      smsp.setMessageCoding(msgCoding);
      SMSOptions.setFallbackCoding(msgCoding);
      int protocolId;
      switch (this._sentAsField.getSelectedIndex()) {
         case 0:
            protocolId = 0;
            break;
         case 1:
         default:
            protocolId = 2;
            break;
         case 2:
            protocolId = 3;
            break;
         case 3:
            protocolId = 4;
            break;
         case 4:
            protocolId = 5;
      }

      smsp.setProtocolMeaning(0);
      smsp.setProtocolId(protocolId);
      if (cbAddress != null) {
         smsp.setCallbackAddress(cbAddress);
      }

      if (scAddress != null) {
         smsp.setSCAddress(scAddress);
      }

      try {
         RadioInternal.setDefaultSMSParameters(smsp);
         return true;
      } finally {
         ;
      }
   }

   private int getValidityPeriod(int index) {
      switch (index) {
         case -1:
            return -1;
         case 0:
         default:
            return 900;
         case 1:
            return 1800;
         case 2:
            return 3600;
         case 3:
            return 10800;
         case 4:
            return 21600;
         case 5:
            return 43200;
         case 6:
            return 86400;
         case 7:
            return 259200;
         case 8:
            return 604800;
         case 9:
            return 1209600;
      }
   }

   private int getDeliveryPeriod(int index) {
      switch (index) {
         case 0:
            return 0;
         case 1:
         default:
            return 900;
         case 2:
            return 1800;
         case 3:
            return 3600;
         case 4:
            return 10800;
         case 5:
            return 21600;
         case 6:
            return 43200;
         case 7:
            return 86400;
         case 8:
            return 259200;
         case 9:
            return 604800;
         case 10:
            return 1209600;
      }
   }

   private int routeToChoiceIndex(int route) {
      int choice;
      if (RadioInfo.getNetworkType() == 7) {
         switch (route) {
            case 0:
            case 2:
               choice = 4;
               break;
            default:
               choice = 1;
         }
      } else {
         switch (route) {
            case 0:
               choice = 501;
               break;
            case 2:
               choice = 402;
               break;
            default:
               choice = 500;
         }
      }

      int index = RouteChoices.length;

      do {
         index--;
      } while (index > 0 && RouteChoices[index] != choice);

      return index;
   }

   private int choiceIndexToRoute(int index) {
      if (RadioInfo.getNetworkType() == 7) {
         switch (RouteChoices[index]) {
            case 2:
               return 3;
            case 3:
            case 4:
            default:
               return 2;
         }
      } else {
         switch (RouteChoices[index]) {
            case 402:
               return 2;
            case 501:
               return 0;
            default:
               return 1;
         }
      }
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(MenuItem.getPrefab(15));
   }
}
