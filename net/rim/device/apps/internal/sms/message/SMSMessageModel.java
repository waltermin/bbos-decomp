package net.rim.device.apps.internal.sms.message;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.io.DatagramStatusListenerUtil;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.PopupVerbWrapper;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ForwardAsVerb;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.sms.SMSChangeStatusVerb;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.apps.internal.sms.SMSOpenVerb;
import net.rim.device.apps.internal.sms.SMSService;
import net.rim.device.apps.internal.sms.SMSUiRegistry;
import net.rim.device.apps.internal.sms.resources.SMSResources;
import net.rim.device.apps.internal.sms.ui.ViewerProvider;
import net.rim.vm.Array;

public class SMSMessageModel extends SMSModel implements PersistableRIMModel {
   public SMSMessageModel(Object initialData) {
      super(initialData);
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      Object delegateUi = SMSUiRegistry.getRegistry().getCurrentUi();
      if (delegateUi != null && delegateUi instanceof VerbProvider) {
         ContextObject contObj = ContextObject.castOrCreate(context);
         ContextObject.put(contObj, 250, this);
         return ((VerbProvider)delegateUi).getVerbs(contObj, verbs);
      }

      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb defaultVerb = null;
      boolean enableSMS = SMSPacketHeader.isSendSupported();
      Array.resize(verbs, 12);
      int numberOfVerbsAdded = 0;
      if (!ContextObject.getFlag(context, 5) && !ContextObject.getFlag(context, 37)) {
         boolean isAddressEmpty = this.isAddressEmpty(context);
         if (this.inbound()) {
            if (enableSMS && !isAddressEmpty) {
               verbs[numberOfVerbsAdded++] = new SMSReplyVerb(this);
            }

            if (this.flagsSet(1)) {
               verbs[numberOfVerbsAdded++] = new SMSChangeStatusVerb(602450, 16, 0, 1, 0, this, null);
            } else {
               verbs[numberOfVerbsAdded++] = new SMSChangeStatusVerb(602448, 14, 1, 0, 0, this, null);
            }

            if (!isAddressEmpty && !ContextObject.getFlag(context, 78)) {
               verbs[numberOfVerbsAdded++] = new SearchAddressVerb(super._payload.getFirstAddress(), 16861456, MessageResources.getBundle(), 112);
            }
         } else {
            if (!this.isSuccessfullySent() && enableSMS && SMSService.canSend() && !this.isDraft()) {
               verbs[numberOfVerbsAdded++] = new SMSResendVerb(this);
            }

            if (!isAddressEmpty && !ContextObject.getFlag(context, 78)) {
               verbs[numberOfVerbsAdded++] = new SearchAddressVerb(super._payload.getFirstAddress(), 16861472, MessageResources.getBundle(), 111);
            }
         }

         if (enableSMS && !this.isDraft()) {
            SMSForwardVerb verb = new SMSForwardVerb();
            verb.setParameter(this);
            verbs[numberOfVerbsAdded++] = verb;
            ForwardAsVerb forwardAsVerb = new ForwardAsVerb(this);
            if (forwardAsVerb.canInvoke(null)) {
               verbs[numberOfVerbsAdded++] = forwardAsVerb;
            }
         }

         if (!this.flagsSet(16)) {
            verbs[numberOfVerbsAdded++] = new SMSChangeStatusVerb(602480, 18, 16, 0, 0, this, null);
         }

         defaultVerb = new SMSOpenVerb(this);
         verbs[numberOfVerbsAdded++] = defaultVerb;
         numberOfVerbsAdded += this.addVoicemailMessageOverSMSVerbs(verbs, numberOfVerbsAdded, context);
      }

      Array.resize(verbs, numberOfVerbsAdded);
      return defaultVerb;
   }

   @Override
   public Field getField(Object context) {
      Object delegateUi = SMSUiRegistry.getRegistry().getCurrentUi();
      if (delegateUi != null && delegateUi instanceof FieldProvider) {
         ContextObject contObj = ContextObject.castOrCreate(context);
         ContextObject.put(contObj, 250, this);
         return ((FieldProvider)delegateUi).getField(contObj);
      }

      VerticalFieldManager vfm = new VerticalFieldManager(1152921504606846976L);
      if (ContextObject.getFlag(context, 54)) {
         vfm.add(new LabelField(""));
         vfm.add(new SeparatorField());
      }

      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(super._payload.getPriorityChar());
      stringBuffer.append(' ');
      DateFormat.getInstance(53).formatLocal(stringBuffer, super._payload.getDisplayDate());
      LabelField timeLabel = new LabelField(stringBuffer.toString(), 18014398509481984L);
      vfm.add(timeLabel);
      int rc = 0;
      switch (super._payload.getByteField(6)) {
         case 1:
            break;
         case 2:
         default:
            rc = 422;
            break;
         case 3:
            rc = 423;
      }

      if (rc != 0) {
         vfm.add(new LabelField(SMSResources.getString(430) + SMSResources.getString(rc)));
      }

      if (super._payload.isReplyRequested()) {
         vfm.add(new LabelField(SMSResources.getString(412)));
      }

      RIMModel callbackAddress = super._payload.getCallbackAddress();
      if (callbackAddress instanceof FieldProvider) {
         FieldProvider fieldProvider = (FieldProvider)callbackAddress;
         ContextObject addressContextObject = ContextObject.clone(context);
         addressContextObject.setFlag(9);
         addressContextObject.setFlag(1);
         addressContextObject.clearFlag(0);
         HorizontalFieldManager hfm = new HorizontalFieldManager();
         hfm.add(new LabelField(SMSResources.getString(330)));
         Field f = fieldProvider.getField(addressContextObject);
         if (f != null) {
            hfm.add(f);
         }

         vfm.add(hfm);
      }

      vfm.add(new MessageSeparatorField());
      vfm.add(super._payload.getMessageBodyField());
      return vfm;
   }

   public String getStatusString(int index) {
      String statusString = null;
      int startIndex = this.indexIntoStatusArray(index);
      int status = this.getStatus_ViaStartIndex(startIndex);
      if (!this.inbound() && !SMSModel.isSuccessfullySent(status) && status != Integer.MAX_VALUE) {
         int stringResourceToUse;
         switch (status) {
            case 16383:
               stringResourceToUse = 730;
               break;
            case 67108863:
               stringResourceToUse = 710;
               break;
            case 134217727:
            case 536870911:
            case 1073741823:
               stringResourceToUse = 700;
               break;
            default:
               stringResourceToUse = 720;
         }

         if (status == 8191 && super._recipientError != null) {
            int errorStartIndex = this.findRecipientErrorStartIndex(this.getDatagramID_ViaStartIndex(startIndex));
            if (errorStartIndex > -1) {
               int messageTransmissionError = super._recipientError[errorStartIndex + 2];
               if (messageTransmissionError != 0) {
                  statusString = DatagramStatusListenerUtil.getStatusMessage(
                     messageTransmissionError, super._recipientError[errorStartIndex + 3], super._recipientError[errorStartIndex + 1]
                  );
               }
            }
         }

         if (statusString == null) {
            statusString = SMSResources.getString(stringResourceToUse);
         }
      }

      return statusString;
   }

   @Override
   public Object invokeHotkey(Object context, int hotkeyID) {
      Object delegateUi = SMSUiRegistry.getRegistry().getCurrentUi();
      if (delegateUi != null && delegateUi instanceof HotKeyProvider) {
         ContextObject contObj = ContextObject.castOrCreate(context);
         ContextObject.put(contObj, 250, this);
         return ((HotKeyProvider)delegateUi).invokeHotkey(contObj, hotkeyID);
      }

      boolean enableSMS = SMSPacketHeader.isSendSupported();
      switch (hotkeyID) {
         case 148:
            if (this.inbound() && enableSMS && !this.isAddressEmpty(context)) {
               Verb verb = new SMSReplyVerb(this);
               return verb.invoke(context);
            }
            break;
         case 150:
            if (enableSMS) {
               SMSForwardVerb smsForwardVerb = new SMSForwardVerb();
               smsForwardVerb.setParameter(this);
               return smsForwardVerb.invoke(context);
            }
            break;
         case 152:
            Verb verb;
            if (this.flagsSet(1)) {
               verb = new SMSChangeStatusVerb(602450, 16, 0, 1, 0, this, null);
            } else {
               verb = new SMSChangeStatusVerb(602448, 14, 1, 0, 0, this, null);
            }

            return verb.invoke(context);
         default:
            return null;
      }

      return null;
   }

   @Override
   public ModelScreen getViewer(Object context) {
      Object delegateUi = SMSUiRegistry.getRegistry().getCurrentUi();
      if (delegateUi != null && delegateUi instanceof ViewerProvider) {
         ContextObject contObj = ContextObject.castOrCreate(context);
         ContextObject.put(contObj, 250, this);
         return ((ViewerProvider)delegateUi).getViewer(contObj);
      } else if (this.getOverallStatus() == Integer.MAX_VALUE && RadioInfo.getNetworkType() != 5) {
         SMSEditorScreen.runEditor(context, this);
         return null;
      } else {
         return new SMSViewerScreen(context);
      }
   }

   @Override
   public boolean isAddressEmpty(Object context) {
      PersistableRIMModel[] addresses = super._payload.getAddresses();
      if (addresses.length == 0) {
         return true;
      }

      StringBuffer strbuf = new StringBuffer();

      for (int i = addresses.length - 1; i > -1; i--) {
         RIMModel address = addresses[i];
         boolean transmission = ContextObject.getFlag(context, 21);
         boolean sms = ContextObject.getFlag(context, 55);
         ContextObject.setFlag(context, 21);
         ContextObject.setFlag(context, 55);
         if (address instanceof ConversionProvider) {
            ConversionProvider conversionProvider = (ConversionProvider)address;
            conversionProvider.convert(context, strbuf);
         }

         if (!transmission) {
            ContextObject.clearFlag(context, 21);
         }

         if (!sms) {
            ContextObject.clearFlag(context, 55);
         }

         if (strbuf.length() > 0) {
            return false;
         }
      }

      return true;
   }

   public boolean setupAddress(Object context) {
      RIMModel model = (RIMModel)ContextObject.get(context, 254);
      if (model != null) {
         if (!SMSService.validateAddress(model)) {
            return false;
         }

         this.addAddress((PersistableRIMModel)model);
         return true;
      } else {
         SMSEditAddressVerb addressVerb = new SMSEditAddressVerb(this, 0, null);
         return addressVerb.invoke(null) != null;
      }
   }

   static AddressSelectionContext getAddressSelectionContext() {
      Verb[] useOnceVerbs;
      Recognizer recognizer;
      if (!SMSService.isEmailAddressAsSMSAddressSupported()) {
         useOnceVerbs = new Verb[]{new UseOnceSMSAddressVerb()};
         recognizer = RecognizerRepository.getRecognizers(3797587162219887872L);
      } else {
         Verb[] wrappedVerbs = new Verb[]{new UseOnceSMSAddressVerb(null, 0), new UseOnceSMSAddressVerb(null, 1)};
         useOnceVerbs = new Verb[]{
            new PopupVerbWrapper(
               SMSResources.getString(190),
               SMSResources.getString(415),
               wrappedVerbs[0].getOrdering(),
               wrappedVerbs,
               new String[]{SMSResources.getString(418), SMSResources.getString(414)},
               wrappedVerbs[0]
            )
         };
         CompoundRecognizer compoundRecognizer = new CompoundRecognizer();
         compoundRecognizer.addRecognizer(RecognizerRepository.getRecognizers(3797587162219887872L));
         compoundRecognizer.addRecognizer(RecognizerRepository.getRecognizers(-2985347935260258684L));
         recognizer = compoundRecognizer;
      }

      String findLabel = SMSResources.getString(378);
      AddressSelectionContext selectionContext = new AddressSelectionContext(findLabel, SMSResources.getString(180), null, recognizer, useOnceVerbs);
      ContextObject phoneNumberSelectionContext = new ContextObject();
      phoneNumberSelectionContext.setFlag(42, 34);
      phoneNumberSelectionContext.put(6609423255094033855L, new Integer(15307058));
      selectionContext.setContext(phoneNumberSelectionContext);
      selectionContext.setUseEntryPrefixes(new String[]{SMSResources.getString(610)});
      return selectionContext;
   }

   static PersistableRIMModel selectAddress(AddressSelectionContext selectionContext) {
      if (selectionContext == null) {
         selectionContext = getAddressSelectionContext();
      }

      Verb addressSelectionVerb = AddressBookServices.getAddressSelectionVerb(4738722199580714034L);
      PersistableRIMModel address = null;
      if (addressSelectionVerb != null) {
         return (PersistableRIMModel)addressSelectionVerb.invoke(selectionContext);
      }

      Verb[] useOnceVerbs = selectionContext.getUseOnceVerbs();
      if (useOnceVerbs != null && useOnceVerbs.length > 0 && useOnceVerbs[0] != null) {
         address = (PersistableRIMModel)useOnceVerbs[0].invoke(selectionContext);
      }

      return address;
   }

   @Override
   protected int getType() {
      return 0;
   }
}
