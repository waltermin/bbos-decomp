package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.verb.SendKeyInvocableVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.SpecialAddressCard;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.DialPhoneNumberVerb;
import net.rim.device.apps.internal.phone.model.PhoneNumberConverter;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class DialVerb
   extends Verb
   implements PhoneVerb,
   DialPhoneNumberVerb,
   AbstractDialVerb,
   SendKeyInvocableVerb,
   SetParameter,
   Copyable,
   MemoryCleanerListener {
   private String _description;
   private Object _phoneNumber;
   private PersistableRIMModel _addressBookEntry;
   private Object _sourceCallerIDInfo;
   private String _friendlyName;
   private String _alternateAddresscardName;
   private long _uid;
   private Object _context;
   private boolean _skipAddressBookLookup;
   private boolean _confirmOnInvoke;
   private Object _confirmationAddressInfo;
   private boolean _canCoalesce;
   private boolean _invokedFromSecondaryScreen;
   private boolean _invokedFromHyperlink;
   private boolean _useSmartDialing;
   private boolean _rawNumberOnly;
   private Confirmation _switchBackgroundConfirmation;
   private boolean _switchBackgroundOnCompletion;
   private boolean _typeRequired;
   private boolean _multiplePhoneNumbersInAddressCard;
   private boolean _callLogInMsgList;
   private boolean _insideAddressCard;
   private boolean _sendKeyInvoked;
   private boolean _simPhoneBookInvoked;
   private boolean _speedDialInvoked;
   private String _preCallTuneName;
   private static final Recognizer _dialVerbRecognizer = new DialVerb$DialVerbRecognizer();

   public final long getUID() {
      return this._uid;
   }

   public final void setContext(Object context) {
      this._context = ContextObject.clone(context);
   }

   @Override
   public final void setParameter(Object parameter) {
      if (!(parameter instanceof Object)) {
         if (parameter instanceof Object) {
            this._phoneNumber = parameter;
         } else {
            if (!(parameter instanceof Object)) {
               throw new Object();
            }

            this._phoneNumber = parameter;
         }
      } else {
         ContextObject contextObject = (ContextObject)parameter;
         if (contextObject.getFlag(7)) {
            super._ordering = 327973;
         }

         this._alternateAddresscardName = (String)contextObject.get(7065077197339612497L);
         Object phoneNumber = contextObject.get(247);
         if (phoneNumber != null) {
            this._phoneNumber = phoneNumber;
         }

         if (PhoneUtilities.getPrivateFlag(parameter, 75)) {
            this._rawNumberOnly = true;
         } else {
            this._sourceCallerIDInfo = contextObject.get(5898398779440734986L);
            if (this._sourceCallerIDInfo != null) {
               this._friendlyName = ((CallerIDInfo)this._sourceCallerIDInfo).getFriendlyName();
            } else if (this._phoneNumber instanceof Object) {
               VerbDescriptionProvider verbDescriptionProvider = (VerbDescriptionProvider)this._phoneNumber;
               this._friendlyName = verbDescriptionProvider.getVerbDescription(contextObject);
            }
         }

         this._addressBookEntry = (PersistableRIMModel)contextObject.get(252);
      }

      this._sendKeyInvoked = ContextObject.getFlag(parameter, 119);
      this._simPhoneBookInvoked = ContextObject.getFlag(parameter, 123);
      this._speedDialInvoked = PhoneUtilities.getPrivateFlag(parameter, 66);
      this._preCallTuneName = (String)ContextObject.get(parameter, 2848872683723475070L);
      this._callLogInMsgList = PhoneUtilities.getPrivateFlag(parameter, 70);
      this._canCoalesce = !PhoneUtilities.getPrivateFlag(parameter, 74);
      Object ordering = ContextObject.get(parameter, -6956199554576980056L);
      if (ordering instanceof Object) {
         super._ordering = ordering;
      }

      if (ContextObject.getFlag(parameter, 83)) {
         this._invokedFromHyperlink = true;
         this._canCoalesce = false;
         super._ordering = 1265701;
      }

      if (ContextObject.getFlag(parameter, 34)) {
         this._canCoalesce = false;
         this._typeRequired = true;
      }

      if (ContextObject.getFlag(parameter, 82)) {
         this._skipAddressBookLookup = true;
      }

      if (PhoneUtilities.getPrivateFlag(parameter, 59)) {
         this._multiplePhoneNumbersInAddressCard = true;
      } else if (this._addressBookEntry != null) {
         this._multiplePhoneNumbersInAddressCard = PhoneUtilities.countPhoneNumbersInAddressCard(this._addressBookEntry) > 1;
      }

      if (ContextObject.getFlag(parameter, 37) && ContextObject.getFlag(parameter, 18) && ContextObject.getFlag(parameter, 11)) {
         this._insideAddressCard = true;
         this._canCoalesce = false;
      }

      if (ContextObject.getFlag(parameter, 73)) {
         this._confirmOnInvoke = true;
         this._confirmationAddressInfo = ContextObject.get(parameter, 252);
      }

      if (ContextObject.getFlag(parameter, 96)) {
         this._switchBackgroundOnCompletion = true;
         this._switchBackgroundConfirmation = (Confirmation)ContextObject.get(parameter, 8128293842573788963L);
      } else {
         this._switchBackgroundOnCompletion = false;
         this._switchBackgroundConfirmation = null;
      }

      if (PhoneUtilities.getPrivateFlag(parameter, 1)) {
         this._invokedFromSecondaryScreen = true;
      }

      this._useSmartDialing = ContextObject.getFlag(parameter, 117);
   }

   public final boolean canCoalesce() {
      return this._canCoalesce;
   }

   final void setRequiresUserConfirmation(boolean confirm) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final boolean requiresUserConfirmation() {
      return this._confirmOnInvoke;
   }

   public final void setOrdering(int ordering) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final Object getIdentity() {
      return this._addressBookEntry;
   }

   final boolean confirm() {
      String confirmationAddressOrNumber = this._confirmationAddressInfo != null ? this._confirmationAddressInfo.toString() : this._phoneNumber.toString();
      String message = MessageFormat.format(PhoneResources.getString(136), new Object[]{confirmationAddressOrNumber});
      String[] choices = (Object[])PhoneResources.getObject(3040);
      Dialog askDialog = (Dialog)(new Object(message, choices, null, 0, null, 0));
      askDialog.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
      if (Application.isEventDispatchThread()) {
         return askDialog.doModal() == 0;
      }

      Application.getApplication().invokeAndWait(new DialVerb$1(this, askDialog));
      return askDialog.getSelectedValue() == 0;
   }

   @Override
   public final Object getPhoneNumber() {
      return this._phoneNumber;
   }

   @Override
   public final Object copy() {
      return new DialVerb();
   }

   @Override
   public final boolean cleanNow(int event) {
      return event == 10 && this.resetParameters();
   }

   @Override
   public final String getDescription() {
      return null;
   }

   public DialVerb(Object phoneNumber, Object addressCard) {
      this(phoneNumber, addressCard, 1266688, null);
   }

   public DialVerb(CallerIDInfo callerIDInfo) {
      this(callerIDInfo.getNumber(), callerIDInfo.getAddress(), 1266688, null);
      this._sourceCallerIDInfo = callerIDInfo;
   }

   private static final boolean confirmThreeWayCall(LiveCall call) {
      if (call.getFlag(32768)) {
         String prompt = PhoneResources.getString(6045);
         return Dialog.ask(3, prompt) == 4;
      } else {
         return true;
      }
   }

   public static final boolean startThreeWayCall(LiveCall call, String phoneNumber, ContextObject context, ContextObject connectionParameters) {
      if (confirmThreeWayCall(call) && call.isActive()) {
         StringBuffer phoneNumberBuf = (StringBuffer)(new Object());
         PhoneNumberConverter.convertForTransmission(phoneNumberBuf, phoneNumber.toCharArray(), context);
         if (!PhoneUtilities.singleFlash3WC()) {
            VoiceServices.flash(null);
         }

         VoiceServices.flash(phoneNumberBuf.toString());
         VoiceServices.broadcastEvent(3006, call.getCallId(), connectionParameters);
         return true;
      } else {
         return false;
      }
   }

   private final Object startCall(Object context) {
      if (this._confirmOnInvoke && !this.confirm()) {
         return null;
      }

      if (this._phoneNumber == null) {
         if (context instanceof Object) {
            this._phoneNumber = context;
         } else {
            this._phoneNumber = ContextObject.get(context, 247);
         }
      }

      if (this._phoneNumber == null) {
         throw new Object("phone number is null");
      }

      if (this._invokedFromHyperlink || this._useSmartDialing || this._simPhoneBookInvoked) {
         context = ContextObject.clone(context);
         if (this._invokedFromHyperlink) {
            ContextObject.setFlag(context, 83);
         }

         if (this._useSmartDialing) {
            ContextObject.setFlag(context, 117);
         }

         if (this._simPhoneBookInvoked) {
            ContextObject.setFlag(context, 123);
         }
      }

      if (this._preCallTuneName != null) {
         ContextObject.put(context, 2848872683723475070L, this._preCallTuneName);
      }

      Object connectionParameters = PhoneUtilities.getCallConnectionParameters(this._phoneNumber, this._addressBookEntry, this._sourceCallerIDInfo, context);
      Character ch = (Character)ContextObject.get(context, -5448030602898493731L);
      if (ch != null) {
         ContextObject.put(connectionParameters, -5448030602898493731L, ch);
      }

      if (this._switchBackgroundOnCompletion && connectionParameters != null) {
         ContextObject.setFlag(connectionParameters, 96);
         if (this._switchBackgroundConfirmation != null) {
            ContextObject.put(connectionParameters, 8128293842573788963L, this._switchBackgroundConfirmation);
         }
      }

      if (this._sendKeyInvoked) {
         Backlight.enable(true);
      }

      ContextObject resultContext = (ContextObject)(new Object());
      if (PhoneUtilities.cdmaTypeNetwork() && VoiceServices.isPhoneActive()) {
         LiveCall call = (LiveCall)VoiceServices.getVoiceApplication().getCurrentCall();
         if (call == null || call.getStatus() != 1) {
            return null;
         }

         if (!startThreeWayCall(call, this._phoneNumber.toString(), (ContextObject)context, (ContextObject)connectionParameters)) {
            return null;
         }

         resultContext.setFlag(39);
      } else if (this._invokedFromSecondaryScreen) {
         Application.getApplication().invokeLater(new DialVerb$2(this, connectionParameters));
      } else if (!OutgoingCallConnector.startCall(connectionParameters)) {
         PhoneUtilities.setPrivateFlag(resultContext, 6);
      }

      if (!ContextObject.getFlag(connectionParameters, 40) && !this._invokedFromHyperlink && this._sendKeyInvoked) {
         resultContext.setFlag(39);
      }

      Application.getApplication().invokeLater(new DialVerb$3(this));
      return resultContext;
   }

   @Override
   public final Object invoke(Object context) {
      Object result = this.startCall(context);
      this.resetParameters();
      return result;
   }

   private final boolean resetParameters() {
      boolean gc = this._phoneNumber != null || this._addressBookEntry != null;
      this._phoneNumber = null;
      this._addressBookEntry = null;
      this._canCoalesce = true;
      this._confirmOnInvoke = false;
      this._invokedFromHyperlink = false;
      this._useSmartDialing = false;
      return gc;
   }

   @Override
   public final String toString() {
      if (this._description != null) {
         return this._description;
      }

      if (this._rawNumberOnly && this._phoneNumber != null) {
         String displayString;
         if (!(this._phoneNumber instanceof Object)) {
            displayString = this._phoneNumber.toString();
         } else {
            displayString = ((AbstractPhoneNumberModel)this._phoneNumber).getDisplayablePhoneNumber();
         }

         return ((StringBuffer)(new Object())).append(PhoneResources.getString(411)).append(displayString).toString();
      } else {
         String address = null;
         ContextObject contextObject = ContextObject.castOrCreate(this._context);
         StringBuffer buf = (StringBuffer)(new Object());
         boolean addressOnly = contextObject.getFlag(51);
         if (this._sourceCallerIDInfo != null && !this._callLogInMsgList && this._sourceCallerIDInfo instanceof CallerIDInfo) {
            CallerIDInfo cidi = (CallerIDInfo)this._sourceCallerIDInfo;
            if (cidi.displayCompanyInfo()) {
               address = cidi.toString();
            } else if (this._canCoalesce) {
               address = this._sourceCallerIDInfo.toString();
            }
         }

         if (this._addressBookEntry instanceof SpecialAddressCard) {
            address = this._addressBookEntry.toString();
         }

         if (address == null) {
            if (!(this._phoneNumber instanceof Object)) {
               if (this._phoneNumber instanceof Object) {
                  address = (String)this._phoneNumber;
               }
            } else {
               VerbDescriptionProvider verbDescriptionProvider = (VerbDescriptionProvider)this._phoneNumber;
               if (this._friendlyName != null) {
                  contextObject.put(-4886909117188079897L, this._friendlyName);
               }

               if (this._alternateAddresscardName != null) {
                  contextObject.put(7065077197339612497L, this._alternateAddresscardName);
               }

               if (this._addressBookEntry != null) {
                  contextObject.put(252, this._addressBookEntry);
               } else if (this._skipAddressBookLookup) {
                  contextObject.setFlag(82);
               }

               if (this._invokedFromHyperlink) {
                  contextObject.setFlag(83);
               }

               if (!this._canCoalesce && this._multiplePhoneNumbersInAddressCard) {
                  contextObject.setFlag(34);
               } else if (this._typeRequired && this._multiplePhoneNumbersInAddressCard || this._insideAddressCard) {
                  contextObject.setFlag(34);
               }

               address = verbDescriptionProvider.getVerbDescription(contextObject);
            }
         }

         if (!addressOnly) {
            if (PhoneUtilities.cdmaTypeNetwork() && VoiceServices.isPhoneActive()) {
               buf.append(PhoneResources.getString(6319));
            } else {
               buf.append(PhoneResources.getString(411));
            }
         }

         if (address != null) {
            buf.append(address);
            if (PhoneUtilities.getDebugFlag(3149033938084553376L) && this._phoneNumber instanceof Object && this._addressBookEntry != null) {
               buf.append(((StringBuffer)(new Object(" ("))).append(((PhoneNumberModel)this._phoneNumber).getValue()).append(')').toString());
            }
         }

         this._context = null;
         return buf.toString();
      }
   }

   public DialVerb(Object phoneNumber, Object identity, int ordering, String description) {
      super(ordering);
      this._phoneNumber = phoneNumber;
      if (identity instanceof Object) {
         this._addressBookEntry = (PersistableRIMModel)identity;
      }

      this._description = description;
      this._confirmOnInvoke = false;
      this._canCoalesce = true;
      this._invokedFromSecondaryScreen = false;
      this._invokedFromHyperlink = false;
      this._useSmartDialing = false;
   }

   @Override
   public final int getOrdering() {
      if (this._phoneNumber instanceof Object) {
         if (this._insideAddressCard) {
            return 1196288;
         }

         if (!this._canCoalesce && this._addressBookEntry != null && this._multiplePhoneNumbersInAddressCard) {
            return 196608 + ((PhoneNumberModel)this._phoneNumber).getType();
         }

         if (this._callLogInMsgList) {
            return 602624;
         }
      }

      return super.getOrdering();
   }

   public DialVerb() {
      super(1266688);
      MemoryCleanerDaemon.addWeakListener(this, false);
      this._uid = UIDGenerator.getUID();
      this._confirmOnInvoke = false;
      this._canCoalesce = true;
      this._invokedFromSecondaryScreen = false;
      this._invokedFromHyperlink = false;
      this._useSmartDialing = false;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof DialVerb)) {
         return false;
      }

      DialVerb verb = (DialVerb)obj;
      return this.getUID() == verb.getUID();
   }

   public static final Recognizer getRecognizer() {
      return _dialVerbRecognizer;
   }

   @Override
   public final int getVerbGroupId() {
      return 1187214;
   }

   public DialVerb(Object phoneNumber, Object addressCard, Object context) {
      this(phoneNumber, addressCard, 1266688, null);
      this._sendKeyInvoked = ContextObject.getFlag(context, 119);
      this._sourceCallerIDInfo = ContextObject.get(context, 5898398779440734986L);
      this._useSmartDialing = ContextObject.getFlag(context, 117);
      this._speedDialInvoked = PhoneUtilities.getPrivateFlag(context, 66);
      this._preCallTuneName = (String)ContextObject.get(context, 2848872683723475070L);
   }
}
