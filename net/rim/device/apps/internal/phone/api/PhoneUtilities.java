package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.SIMPhoneNumberWriter;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.api.verbs.UseOncePhoneNumberVerb;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberConverter;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.apps.internal.phone.options.CallTunes;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.pattern.SmartDialingOptions;
import net.rim.device.apps.internal.phone.resource.PhoneContexts;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.apps.internal.profiles.Profile;
import net.rim.device.apps.internal.profiles.Profiles;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;

public final class PhoneUtilities {
   public static final int CLIP_QUERY_EVENT = 1129072976;
   public static final long BOLD_FONT_IN_PHONE_NUMBER_INPUT = 2907897523069873290L;
   public static final String NUMERIC_HELP = "#4357*";
   public static final String SERVICE_CALL = "*611";
   private static final int DEVICE_PHONE_NUMBER_MSISDN = 0;
   private static final int VOICE_MAIL_NUMBER_INDEX = 0;
   private static final int _screenHeight = Display.getHeight();
   private static final int _screenWidth = Display.getWidth();
   private static final int _systemKeyboardId = Keypad.getHardwareLayout();
   private static final int _networkType = RadioInfo.getNetworkType();
   private static final int _screenVerticalResolution = Display.getVerticalResolution();
   private static final int HIGH_RES_COLOUR_SCREEN_VRES_MAGIC_NUMBER = 5000;
   public static final long LAST_NUMBER_DIALED_KEY = 1197739752382153834L;
   public static final long PERSISTENT_CONTENT_LISTENER_KEY = -3841047781361656227L;
   private static PhoneNumberCounter _phoneNumberCounter;

   public static final boolean isCDMAServiceCall(String phoneNumber) {
      return isEmptyString(phoneNumber)
         ? false
         : cdmaTypeNetwork()
            && (
               phoneNumber.compareTo("#4357*") == 0
                  || phoneNumber.compareTo("*611") == 0
                  || phoneNumber.startsWith("##") && phoneNumber.length() == 8
                  || phoneNumber.startsWith("*2") && phoneNumber.length() <= 6
            );
   }

   public static final boolean isQuietProfileOn() {
      Profile profile = Profiles.getInstance().getEnabled();
      switch (profile.getIdentifier()) {
         case 2:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isDiscreetProfileOn() {
      Profile profile = Profiles.getInstance().getEnabled();
      switch (profile.getIdentifier()) {
         case 1:
            return true;
         default:
            return false;
      }
   }

   public static final String getDevicePhoneNumber() {
      return getDevicePhoneNumber(-1, true);
   }

   public static final String getDevicePhoneNumber(boolean format) {
      return getDevicePhoneNumber(-1, format);
   }

   public static final String getDevicePhoneNumber(int callId, boolean format) {
      String number = null;

      label61:
      try {
         Phone phone = Phone.getInstance();
         number = phone.getAlternateLineNumber(phone.getAlternateLine(callId));
      } finally {
         break label61;
      }

      if (number == null) {
         label57:
         try {
            number = Phone.getInstance().getNumber(0);
         } finally {
            break label57;
         }
      }

      if (number != null) {
         return format ? PhoneNumberConverter.format(number, SmartDialingOptions.getOptions().getFormattingString()) : number;
      } else {
         return null;
      }
   }

   public static final String getVoiceMailNumber() {
      return getVoiceMailNumber(getCurrentLineId());
   }

   public static final String getVoiceMailNumber(int lineId) {
      String phoneOptionsNumber = PhoneOptions.getOptions().getVoiceMailNumber(lineId);
      return phoneOptionsNumber != null && phoneOptionsNumber.length() > 0 ? phoneOptionsNumber : getSIMVoiceMailNumber(lineId);
   }

   public static final String getSIMVoiceMailNumber(int lineId) {
      return Phone.getInstance().getVoiceMailNumber(lineId);
   }

   public static final String getCallFailureErrorString(int errorCode) {
      return CommonResources.getCallFailedMessage(errorCode);
   }

   public static final boolean getPrivateFlag(Object context, int flag) {
      return !(context instanceof Object) ? false : ((ContextObject)context).getPrivateFlag(4936088360624690805L, flag);
   }

   public static final void setPrivateFlag(Object context, int flag) {
      if (context instanceof Object) {
         ((ContextObject)context).setPrivateFlag(4936088360624690805L, flag);
      }
   }

   public static final void setPrivateFlag(Object context, int flag, boolean value) {
      if (value) {
         setPrivateFlag(context, flag);
      } else {
         clearPrivateFlag(context, flag);
      }
   }

   public static final void clearPrivateFlag(Object context, int flag) {
      if (context instanceof Object) {
         ((ContextObject)context).clearPrivateFlag(4936088360624690805L, flag);
      }
   }

   public static final ContextObject getPrivateContextObject(int privateContextFlag) {
      ContextObject co = (ContextObject)(new Object());
      co.setPrivateFlag(4936088360624690805L, privateContextFlag);
      return co;
   }

   private static final RIMModel getVoicemailRedirectionNumber(RIMModel param0, Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: ifnonnull 06
      // 04: aconst_null
      // 05: areturn
      // 06: aconst_null
      // 07: astore 2
      // 08: aconst_null
      // 09: astore 3
      // 0a: aconst_null
      // 0b: astore 4
      // 0d: bipush 0
      // 0e: invokestatic net/rim/device/api/system/SIMCard.getVoiceMailNumber (I)Ljava/lang/String;
      // 11: astore 4
      // 13: goto 1d
      // 16: astore 5
      // 18: goto 1d
      // 1b: astore 5
      // 1d: aload 4
      // 1f: ifnull 2a
      // 22: aload 4
      // 24: invokevirtual java/lang/String.length ()I
      // 27: ifne 2c
      // 2a: aconst_null
      // 2b: areturn
      // 2c: invokestatic net/rim/device/api/system/Phone.getInstance ()Lnet/rim/device/api/system/Phone;
      // 2f: bipush 0
      // 30: invokevirtual net/rim/device/api/system/Phone.getNumber (I)Ljava/lang/String;
      // 33: astore 3
      // 34: goto 3b
      // 37: astore 5
      // 39: aconst_null
      // 3a: areturn
      // 3b: aconst_null
      // 3c: astore 5
      // 3e: aload 3
      // 3f: ifnull 4f
      // 42: aload 3
      // 43: invokevirtual java/lang/String.length ()I
      // 46: ifle 4f
      // 49: aload 3
      // 4a: invokestatic net/rim/device/apps/internal/phone/api/PhoneUtilities.createNumberModel (Ljava/lang/Object;)Lnet/rim/device/apps/api/framework/model/RIMModel;
      // 4d: astore 5
      // 4f: aload 5
      // 51: dup
      // 52: instanceof java/lang/Object
      // 55: ifne 5c
      // 58: pop
      // 59: goto 71
      // 5c: checkcast java/lang/Object
      // 5f: astore 6
      // 61: aload 6
      // 63: aload 0
      // 64: bipush 1
      // 65: invokevirtual net/rim/device/apps/internal/phone/model/AbstractPhoneNumberModel.equals (Ljava/lang/Object;Z)Z
      // 68: ifeq 71
      // 6b: aload 4
      // 6d: invokestatic net/rim/device/apps/internal/phone/api/PhoneUtilities.createNumberModel (Ljava/lang/Object;)Lnet/rim/device/apps/api/framework/model/RIMModel;
      // 70: astore 2
      // 71: aload 2
      // 72: areturn
      // try (10 -> 13): 14 null
      // try (10 -> 13): 16 null
      // try (24 -> 28): 29 null
   }

   public static final Object getCallConnectionParameters(Object phoneNumber, Object addressCard, Object sourceCallerIDInfo, Object context) {
      return getCallConnectionParameters(phoneNumber, addressCard, sourceCallerIDInfo, getCurrentLineId(), context);
   }

   public static final Object getCallConnectionParameters(Object phoneNumber, Object addressCard, Object sourceCallerIDInfo, int preferredLine, Object context) {
      ContextObject connectionParameters = ContextObject.clone(context);
      ContextObject tempContext = (ContextObject)(new Object());
      RIMModel numberModel = createNumberModel(phoneNumber);
      RIMModel voicemailNumberModel = null;
      boolean usingVoicemailRedirection = false;
      if (!getPrivateFlag(context, 7)) {
         voicemailNumberModel = getVoicemailRedirectionNumber(numberModel, tempContext);
         if (voicemailNumberModel instanceof Object) {
            numberModel = voicemailNumberModel;
            usingVoicemailRedirection = true;
         }
      }

      boolean useSmartDialing = false;
      if (!getDebugFlag(-1032892149017511731L)) {
         boolean fdnEnabled = false;

         label201:
         try {
            fdnEnabled = Phone.getInstance().isFDNEnabled();
         } finally {
            break label201;
         }

         useSmartDialing = (!ContextObject.getFlag(context, 123) || !fdnEnabled) && (addressCard != null || ContextObject.getFlag(context, 117));
      }

      boolean emergencyCall = isEmergencyNumber(phoneNumber) || getPrivateFlag(context, 33);
      if (emergencyCall) {
         useSmartDialing = false;
         addressCard = null;
      }

      String numberToConnect = null;
      StringBuffer buffer = (StringBuffer)(new Object());
      if (numberModel instanceof Object) {
         ConversionProvider conversionProvider = (ConversionProvider)numberModel;
         tempContext.reset();
         tempContext.setFlag(21);
         if (useSmartDialing) {
            ContextObject.setFlag(tempContext, 117);
         }

         if (getPrivateFlag(context, 91)) {
            setPrivateFlag(tempContext, 91);
         }

         if (conversionProvider.convert(tempContext, buffer)) {
            numberToConnect = buffer.toString();
            if (numberToConnect == null || numberToConnect.length() == 0) {
               return null;
            }

            System.out.println(((StringBuffer)(new Object("PHONE: connecting "))).append(numberToConnect).toString());
         }
      }

      if (numberModel instanceof Object) {
         String numberRequested = ((AbstractPhoneNumberModel)numberModel).getValue();
         connectionParameters.put(-799495460678763170L, numberRequested);
      }

      connectionParameters.put(6486659828352467672L, numberToConnect);
      if (useSmartDialing) {
         ContextObject.setFlag(connectionParameters, 117);
      }

      String additionalDTMFTones = (String)tempContext.get(7528018505720453076L);
      if (additionalDTMFTones != null) {
         connectionParameters.put(7528018505720453076L, additionalDTMFTones);
      }

      tempContext.reset();
      tempContext.setPrivateFlag(4936088360624690805L, 24);
      tempContext.put(247, numberModel);
      if (addressCard != null && !usingVoicemailRedirection) {
         tempContext.put(252, addressCard);
      } else if (getPrivateFlag(context, 7) || usingVoicemailRedirection) {
         setPrivateFlag(tempContext, 7);
         setPrivateFlag(connectionParameters, 7);
      }

      if (sourceCallerIDInfo instanceof CallerIDInfo) {
         CallerIDInfo cidi = (CallerIDInfo)sourceCallerIDInfo;
         String friendlyNameString = cidi.getFriendlyName();
         if (friendlyNameString != null) {
            tempContext.put(-4886909117188079897L, friendlyNameString);
         }

         tempContext.put(5898398779440734986L, sourceCallerIDInfo);
      }

      if (emergencyCall) {
         setPrivateFlag(tempContext, 33);
         setPrivateFlag(connectionParameters, 33);
      }

      if (useSmartDialing) {
         ContextObject.setFlag(tempContext, 117);
      }

      setPrivateFlag(tempContext, 58);
      RIMModel callerIDInfo = (RIMModel)FactoryUtil.createInstance(2629643229137268956L, tempContext);
      if (callerIDInfo != null) {
         connectionParameters.put(5898398779440734986L, callerIDInfo);
      }

      connectionParameters.putIntegerData(preferredLine);
      return connectionParameters;
   }

   public static final Object getCallAnsweringParameters(int callId, RIMModel callerIDInfo, Object context) {
      ContextObject connectionParameters = PhoneContexts.CONNECTION_CONTEXT_WR.getContextObject();
      connectionParameters.reset();
      if (callerIDInfo instanceof Object) {
         connectionParameters.put(5898398779440734986L, callerIDInfo);
      }

      if (getPrivateFlag(context, 23)) {
         setPrivateFlag(connectionParameters, 23);
      }

      if (ContextObject.getFlag(context, 80)) {
         ContextObject.setFlag(connectionParameters, 80);
      }

      if (getPrivateFlag(context, 57)) {
         setPrivateFlag(connectionParameters, 57);
         Object number = ContextObject.get(context, 9190530831625408279L);
         if (number != null) {
            connectionParameters.put(9190530831625408279L, number);
         }
      }

      return connectionParameters;
   }

   public static final RIMModel createNumberModel(Object number) {
      ContextObject tempContext = (ContextObject)(new Object());
      tempContext.reset();
      if (number == null) {
         return null;
      }

      long contextId;
      if (number instanceof Object) {
         contextId = 253;
      } else {
         if (!(number instanceof Object)) {
            throw new Object();
         }

         contextId = 254;
      }

      tempContext.put(contextId, number);
      RIMModel numberModel = (RIMModel)FactoryUtil.createInstance(3797587162219887872L, tempContext);
      if (numberModel == null) {
         throw new Object();
      } else {
         return numberModel;
      }
   }

   public static final CallerIDInfo createCallerIDInfo(Object number) {
      RIMModel phoneNumberModel = null;
      if (number instanceof Object) {
         phoneNumberModel = createNumberModel(number);
      } else {
         if (!(number instanceof Object)) {
            return null;
         }

         phoneNumberModel = (RIMModel)number;
      }

      return createCallerIDInfo(phoneNumberModel, 22, 0, null);
   }

   public static final CallerIDInfo createCallerIDInfo(RIMModel numberModel, int callTypeFlag, int callId, Object context) {
      ContextObject tempContext = (ContextObject)(new Object());
      tempContext.setPrivateFlag(4936088360624690805L, callTypeFlag);
      String friendlyName = (String)ContextObject.get(context, -4886909117188079897L);
      int clipMode = 0;
      if (callId != 0) {
         label67:
         try {
            clipMode = Phone.getInstance().getCLIPDisplayMode(callId);
            if (friendlyName == null) {
               friendlyName = VoiceServices.getCallName(callId);
            }
         } finally {
            break label67;
         }

         Out.p(1128352844, 1129072976, clipMode);
      }

      if (clipMode == 1) {
         tempContext.setPrivateFlag(4936088360624690805L, 26);
      } else if (numberModel != null && clipMode != 2) {
         tempContext.put(247, numberModel);
      } else {
         tempContext.setPrivateFlag(4936088360624690805L, 25);
      }

      if (friendlyName != null) {
         tempContext.put(253, friendlyName);
      }

      boolean useSmartDialing = ContextObject.getFlag(context, 117);
      if (useSmartDialing) {
         ContextObject.setFlag(tempContext, 117);
      }

      boolean isEmergencyCall = getPrivateFlag(context, 33);
      if (isEmergencyCall) {
         setPrivateFlag(tempContext, 33);
      }

      CallerIDInfo callerIDInfo = (CallerIDInfo)FactoryUtil.createInstance(2629643229137268956L, tempContext);
      if (callerIDInfo == null) {
         throw new Object();
      } else {
         return callerIDInfo;
      }
   }

   public static final RIMModel createPhoneCallModel(Object initialData) {
      Object o = FactoryUtil.createInstance(4846413703361859244L, initialData);
      return (RIMModel)(!(o instanceof Object) ? null : o);
   }

   public static final void rejectCall(int callId) {
      if (canReject()) {
         VoiceServices.rejectCall(callId);
         if (cdmaTypeNetwork() && Phone.getInstance().getActiveCallId() != 0) {
            VoiceServices.broadcastEvent(2001, callId, null);
            return;
         }
      } else {
         VoiceServices.broadcastEvent(2001, callId, null);
      }
   }

   public static final boolean isEmergencyCall() {
      int callId = Phone.getInstance().getActiveCallId();
      if (callId == 0) {
         return false;
      }

      try {
         String phoneNumber = Phone.getInstance().getCallPhoneNumber(callId);
         return isEmergencyNumber(phoneNumber);
      } finally {
         ;
      }
   }

   public static final boolean isEmergencyNumber(Object phoneNumber) {
      String number;
      if (!(phoneNumber instanceof Object)) {
         if (!(phoneNumber instanceof Object)) {
            return false;
         }

         number = ((AbstractPhoneNumberModel)phoneNumber).getValue();
      } else {
         number = (String)phoneNumber;
      }

      if (number != null && number.length() != 0) {
         try {
            if (Phone.getInstance().isEmergencyNumber(number)) {
               return true;
            }
         } finally {
            return getDebugFlag(8128272366344448397L) && number.equals("01911");
         }

         return getDebugFlag(8128272366344448397L) && number.equals("01911");
      } else {
         return false;
      }
   }

   public static final int getMaxConferenceCallMembers() {
      return Phone.getInstance().getMaxConferenceMembers();
   }

   public static final boolean explicitCallTransferSupported() {
      return (VoiceServices.getVoiceNetworkCapabilities() & 131072) != 0;
   }

   public static final boolean canHold(int callId) {
      return Phone.getInstance().canHold(callId);
   }

   public static final boolean canSwap(int callId) {
      return Phone.getInstance().canSwap(callId);
   }

   public static final boolean canFlash() {
      return (VoiceServices.getVoiceNetworkCapabilities() & 2) != 0;
   }

   public static final boolean canExit911CallbackMode() {
      return (VoiceServices.getVoiceNetworkCapabilities() & 262144) != 0;
   }

   public static final boolean singleFlash3WC() {
      return (VoiceServices.getVoiceNetworkCapabilities() & 128) != 0;
   }

   public static final boolean canReject() {
      return (VoiceServices.getVoiceNetworkCapabilities() & 1) != 0 || gsmTypeNetwork();
   }

   public static final boolean platformSupportsCallBarring() {
      return (VoiceServices.getVoiceNetworkCapabilities() & 256) != 0;
   }

   public static final boolean platformNotifiesOnPrivacyChanges() {
      return cdmaTypeNetwork();
   }

   public static final boolean platformNotifiesOnRoamingChanges() {
      return false;
   }

   public static final boolean canDialPlus() {
      return gsmTypeNetwork();
   }

   public static final boolean canBlockIdentity() {
      switch (_networkType) {
         case 2:
         case 5:
            return false;
         case 3:
         case 6:
         case 7:
            return true;
         case 4:
         default:
            return (VoiceServices.getVoiceNetworkCapabilities() & 8192) != 0;
      }
   }

   public static final boolean gsmTypeNetwork() {
      switch (_networkType) {
         case 2:
         case 4:
            return false;
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            return true;
      }
   }

   public static final boolean gsmWAFActive() {
      return gsmTypeNetwork() || (RadioInfo.getActiveWAFs() & 1) != 0;
   }

   public static final boolean cdmaWAFActive() {
      return (RadioInfo.getActiveWAFs() & 2) != 0;
   }

   public static final boolean cdmaTypeNetwork() {
      return _networkType == 4;
   }

   public static final boolean idenTypeNetwork() {
      return _networkType == 5;
   }

   public static final boolean wifiTypeNetwork() {
      return _networkType == 6;
   }

   public static final boolean headsetButtonEvent(Object context) {
      return getPrivateFlag(context, 30);
   }

   public static final boolean inHolsterInputEvent(Object context) {
      return getPrivateFlag(context, 31);
   }

   public static final boolean callIncoming(Object context) {
      return getPrivateFlag(context, 22);
   }

   public static final boolean callWaiting(Object context) {
      return getPrivateFlag(context, 23);
   }

   public static final boolean dualMode(Object context) {
      return getPrivateFlag(context, 21);
   }

   public static final boolean heldConnectingMode(Object context) {
      return getPrivateFlag(context, 17);
   }

   public static final boolean emergencyCall(Object context) {
      return getPrivateFlag(context, 33);
   }

   public static final int getPersistentInt(long persistentId, int defaultValue) {
      int id = PersistentInteger.getId(persistentId, defaultValue);
      return PersistentInteger.get(id);
   }

   public static final void setPersistentInt(long persistentId, int value) {
      int id = PersistentInteger.getId(persistentId, value);
      PersistentInteger.set(id, value);
   }

   public static final boolean getDebugFlag(long flag, boolean defaultValue) {
      return getPersistentInt(flag, defaultValue ? 1 : 0) == 1;
   }

   public static final boolean getDebugFlag(long flag) {
      return getPersistentInt(flag, 0) == 1;
   }

   public static final void setDebugFlag(long flag, boolean on) {
      setPersistentInt(flag, on ? 1 : 0);
   }

   public static final boolean toggleBooleanDebugFlag(long flag) {
      boolean isSet = !getDebugFlag(flag);
      setDebugFlag(flag, isSet);
      return isSet;
   }

   public static final CallerIDInfo getCallDisplayInfo(int callId, int callTypeFlag, Object context) {
      RIMModel numberModel = null;
      int clipMode = 0;
      ContextObject contextObject = ContextObject.castOrCreate(context);

      label70:
      try {
         clipMode = Phone.getInstance().getCLIPDisplayMode(callId);
      } finally {
         break label70;
      }

      CallerIDInfo callerIDInfo = null;
      if (clipMode != 0) {
         callerIDInfo = createCallerIDInfo(numberModel, callTypeFlag, callId, context);
      } else {
         String originalNumber = VoiceServices.getCallPhoneNumber(callId, true);
         String actualNumber = VoiceServices.getCallPhoneNumber(callId, false);
         String displayNumber = null;
         String redirectedNumber = null;
         String friendlyName = null;
         if (originalNumber != null && originalNumber.length() > 0) {
            friendlyName = VoiceServices.getCallName(callId, true);
            displayNumber = originalNumber;
            redirectedNumber = actualNumber;
            contextObject.put(7641283818372650975L, originalNumber);
         } else if (actualNumber != null && actualNumber.length() > 0) {
            friendlyName = VoiceServices.getCallName(callId, false);
            displayNumber = actualNumber;
         }

         numberModel = createNumberModel(displayNumber);
         if (displayNumber != null) {
            contextObject.put(-6346895525857192403L, displayNumber);
         }

         if (redirectedNumber != null && redirectedNumber.length() > 0) {
            setPrivateFlag(contextObject, 57);
            contextObject.put(9190530831625408279L, redirectedNumber);
         }

         if (friendlyName != null) {
            contextObject.put(-4886909117188079897L, friendlyName);
         }

         callerIDInfo = createCallerIDInfo(numberModel, callTypeFlag, callId, contextObject);
      }

      return callerIDInfo;
   }

   public static final void toggleBooleanDebugFlag(long flag, String description) {
      boolean set = toggleBooleanDebugFlag(flag);
      if (description != null) {
         Dialog.inform(((StringBuffer)(new Object())).append(description).append(set).toString());
      }
   }

   public static final boolean callNumberFromAddressBook(String initialSearchPattern, Field titleField, boolean terminateOnEsc) {
      Field title = titleField;
      if (titleField == null) {
         RibbonBanner ribbonBanner = RibbonBanner.getInstance();
         if (ribbonBanner != null) {
            title = ribbonBanner.getStatusBanner(null, 3);
         }
      }

      boolean is3WC = false;
      if (cdmaTypeNetwork() && VoiceServices.isPhoneActive()) {
         is3WC = true;
      }

      ContextObject selectionContext = selectFromAddressBook(initialSearchPattern, title, null, null, terminateOnEsc, is3WC);
      if (selectionContext != null) {
         Object phoneNumber = selectionContext.get(247);
         Object address = selectionContext.get(252);
         DialVerb dialVerb = new DialVerb(phoneNumber, address);
         PhoneLogger.log("startcall from AB");
         dialVerb.invoke(null);
         return true;
      } else {
         return false;
      }
   }

   public static final Object getCallConnectionFromAddressBook(String initialSearchPattern, Field titleField, boolean terminateOnEsc, boolean is3WC) {
      ContextObject selectionContext = selectFromAddressBook(initialSearchPattern, titleField, null, null, terminateOnEsc, is3WC);
      if (selectionContext != null) {
         Object phoneNumber = selectionContext.get(247);
         Object address = selectionContext.get(252);
         return getCallConnectionParameters(phoneNumber, address, null, null);
      } else {
         return null;
      }
   }

   public static final ContextObject selectFromAddressBook(
      String initialSearchPattern, Field titleField, String selectAddressMenuItemText, String findLabel, boolean terminateOnEsc
   ) {
      return selectFromAddressBook(initialSearchPattern, titleField, selectAddressMenuItemText, findLabel, terminateOnEsc, false);
   }

   public static final ContextObject selectFromAddressBook(
      String initialSearchPattern, Field titleField, String selectAddressMenuItemText, String findLabel, boolean terminateOnEsc, boolean is3WC
   ) {
      AddressSelectionContext selectionContext = null;
      Verb addressSelectionVerb = AddressBookServices.getAddressSelectionVerb(3797587162219887872L);
      if (addressSelectionVerb == null) {
         return null;
      }

      Verb[] useOnceVerbs = new Object[]{new UseOncePhoneNumberVerb()};
      if (findLabel == null) {
         findLabel = PhoneResources.getString(6033);
      }

      String pickNumberString = PhoneResources.getString(116);
      selectionContext = (AddressSelectionContext)(new Object(
         findLabel, pickNumberString, null, RecognizerRepository.getRecognizers(3797587162219887872L), useOnceVerbs
      ));
      ContextObject tmpContext = PhoneContexts.CONNECTION_CONTEXT_WR.getContextObject();
      tmpContext.reset();
      tmpContext.setFlag(42, 34, 119);
      if (terminateOnEsc) {
         tmpContext.setFlag(14);
      }

      tmpContext.put(6609423255094033855L, new Object(1187214));
      if (titleField != null) {
         tmpContext.put(-7261227923983886841L, titleField);
      }

      if (initialSearchPattern != null) {
         selectionContext.setInitialSearchPattern(initialSearchPattern);
      }

      selectionContext.setContext(tmpContext);
      if (selectAddressMenuItemText != null) {
         selectionContext.setUseEntryPrefixes(new Object[]{selectAddressMenuItemText});
      } else if (is3WC) {
         selectionContext.setUseEntryPrefixes(new Object[]{PhoneResources.getString(6318)});
      } else {
         selectionContext.setUseEntryPrefixes(new Object[]{PhoneResources.getString(139)});
      }

      selectionContext.setFindLabel(findLabel);
      Object phoneNumber = addressSelectionVerb.invoke(selectionContext);
      Object address = null;
      if (phoneNumber instanceof Object) {
         if (selectionContext != null) {
            Object addr = selectionContext.getSelectedSource();
            if (addr instanceof Object) {
               address = addr;
            }
         }

         ContextObject addressContext = (ContextObject)(new Object());
         addressContext.put(247, phoneNumber);
         if (address != null) {
            addressContext.put(252, address);
         }

         return addressContext;
      } else {
         return null;
      }
   }

   public static final boolean isQwertyReducedKeyboard() {
      return InternalServices.isReducedFormFactor();
   }

   public static final boolean isFullQwertyKeyboard() {
      return _systemKeyboardId == 1179602501;
   }

   public static final boolean isFullQwertyKeyboard32() {
      return _systemKeyboardId == 1364669234;
   }

   public static final boolean speakerPhoneSupported() {
      return true;
   }

   public static final boolean isSpeakerPhoneKey(int keycode) {
      if (!speakerPhoneSupported()) {
         return false;
      }

      if (isMuteKey(keycode)) {
         return false;
      }

      int key = Keypad.key(keycode);
      if (key == 36) {
         return true;
      }

      char keyChar = Keypad.map(keycode);
      return isFullQwertyKeyboard() && keyChar == 128 || isQwertyReducedKeyboard() && (keyChar == 'o' || key == 273);
   }

   public static final boolean isMuteKey(int keycode) {
      int key = Keypad.key(keycode);
      if (Keypad.hasMuteKey()) {
         return key == 273;
      } else {
         int formFactor = InternalServices.getFormFactor();
         if (formFactor == 5) {
            return key == 21;
         } else {
            return formFactor == 9 ? Keypad.map(keycode) == 'q' : false;
         }
      }
   }

   public static final boolean smallScreen() {
      return _screenHeight < 160;
   }

   public static final boolean isHighResColourScreen() {
      return _screenVerticalResolution > 5000;
   }

   public static final boolean isLowResColourScreen() {
      return !isHighResColourScreen() && Graphics.isColor();
   }

   public static final boolean smallMonoScreen() {
      return _screenHeight < 160;
   }

   public static final boolean largeMonoScreen() {
      return _screenHeight == 160 && _screenWidth == 160;
   }

   public static final boolean is240x160Screen() {
      return _screenHeight == 160 && _screenWidth == 240;
   }

   public static final boolean is240x240Screen() {
      return _screenHeight == 240 && _screenWidth == 240;
   }

   public static final boolean isLargeColourScreen() {
      return _screenHeight > 160;
   }

   public static final boolean isCharm240x260Screen() {
      return _screenWidth == 240 && _screenHeight == 260;
   }

   public static final boolean isElectron320x240Screen() {
      return _screenWidth == 320 && _screenHeight == 240;
   }

   public static final boolean hasPhoneKey() {
      return !Keypad.hasSendEndKeys();
   }

   public static final boolean isBacklitScreen() {
      return (Display.getProperties() & 16384) != 0;
   }

   public static final String getLastNumberDialed() {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         return null;
      }

      PersistentObject store = RIMPersistentStore.getPersistentObject(1197739752382153834L);
      synchronized (store) {
         return PersistentContent.decodeString(store.getContents());
      }
   }

   public static final void setLastNumberDialed(String lastNumberDialed) {
      if (!isCDMAServiceCall(lastNumberDialed)) {
         if (!isEmergencyNumber(lastNumberDialed)) {
            PersistentObject store = RIMPersistentStore.getPersistentObject(1197739752382153834L);
            synchronized (store) {
               store.setContents(PersistentContent.encode(lastNumberDialed, true, true), 51);
               store.commit();
            }
         }
      }
   }

   public static final int countPhoneNumbersInAddressCard(Object addressCard) {
      _phoneNumberCounter.resetCount();
      visitObject(addressCard, _phoneNumberCounter);
      return _phoneNumberCounter.getCount();
   }

   public static final void visitObject(Object o, Visitor visitor) {
      if (o instanceof Object) {
         ReadableList list = (ReadableList)o;
         int size = list.size();

         for (int i = 0; i < size; i++) {
            if (!visitor.visit(list.getAt(i))) {
               return;
            }
         }
      }
   }

   public static final void updateFont(Field field, Font font) {
      if (!(field instanceof Object)) {
         if (field != null) {
            field.setFont(font);
         }
      } else {
         Manager mgr = (Manager)field;

         for (int i = mgr.getFieldCount() - 1; i >= 0; i--) {
            updateFont(mgr.getField(i), font);
         }
      }
   }

   public static final String getLongestString(String s1, String s2) {
      if (s1 != null) {
         if (s2 == null) {
            return s1;
         } else {
            return s1.length() > s2.length() ? s1 : s2;
         }
      } else {
         return s2 != null ? s2 : null;
      }
   }

   public static final void appendVerb(Verb[] verbs, Verb verb) {
      Array.resize(verbs, verbs.length + 1);
      verbs[verbs.length - 1] = verb;
   }

   public static final void appendVerbs(Verb[] verbs, Verb verb1, Verb verb2) {
      Array.resize(verbs, verbs.length + 2);
      verbs[verbs.length - 2] = verb1;
      verbs[verbs.length - 1] = verb2;
   }

   public static final boolean isEmptyString(String s) {
      return s == null || s.length() == 0;
   }

   public static final ButtonField getCloseButton() {
      int closeId = 9;
      String closeString = CommonResource.getString(closeId);
      return (ButtonField)(new Object(closeString, 0));
   }

   public static final int getArrayIndex(String testNumber, String[] numberList) {
      if (numberList.length != 0 && !isEmptyString(testNumber)) {
         testNumber = PhoneNumberServices.stripPlusSign(testNumber);
         Object testNumberModel = createNumberModel(testNumber);

         for (int i = 0; i < numberList.length; i++) {
            if (!isEmptyString(numberList[i])) {
               String num = PhoneNumberServices.stripPlusSign(numberList[i]);
               Object numberModel = createNumberModel(num);
               if (numberModel.equals(testNumberModel)) {
                  return i;
               }
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static final void performLongRunningOperationWithStatus(Runnable runnable, String statusText) {
      Dialog status = (Dialog)(new Object(statusText, null, null, 0, Bitmap.getPredefinedBitmap(3), 33554432));
      status.show(10);
      Runnable _runnable = runnable;
      Thread thread = new PhoneUtilities$1(_runnable, status);
      thread.start();
   }

   public static final int getInCallToneVolume(int defaultVolume) {
      switch (_networkType) {
         case 3:
            return 100;
         default:
            return defaultVolume;
      }
   }

   public static final int playInCallTune(int tuneType, int preferredVolume) {
      return playInCallTune(tuneType, preferredVolume, false);
   }

   public static final int playInCallTune(int tuneType, int preferredVolume, boolean forcePreferredVolume) {
      short[] tune = CallTunes.getTune(tuneType);
      int volume = forcePreferredVolume ? preferredVolume : getInCallToneVolume(preferredVolume);
      Alert.startAudio(tune, volume);
      int tuneLen = 0;

      for (int idx = 1; idx < tune.length; idx += 2) {
         tuneLen += tune[idx];
      }

      return tuneLen;
   }

   public static final int getCurrentLineId() {
      return getCurrentLineId(-1);
   }

   public static final int getCurrentLineId(int callId) {
      return VoiceServices.getCurrentLineId(callId);
   }

   public static final int[] getAllLineIds() {
      return Phone.getInstance().getAlternateLines();
   }

   public static final int[] getAvailableLineIds() {
      int[] lines = Phone.getInstance().getAlternateLines();

      for (int idx = lines.length - 1; idx >= 0; idx--) {
         if (!isLineAvailable(lines[idx])) {
            Arrays.removeAt(lines, idx);
         }
      }

      return lines;
   }

   public static final boolean isLineAvailable(int line) {
      try {
         return Phone.getInstance().isAlternateLineAvailable(line);
      } finally {
         ;
      }
   }

   public static final String[] getAllLineNumbers() {
      int[] id = getAllLineIds();
      String[] returnNumbers = new Object[id.length];

      for (int index = returnNumbers.length - 1; index >= 0; index--) {
         returnNumbers[index] = getLineNumber(id[index], false);
      }

      return returnNumbers;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final String getLineNumber(int lineId, boolean format) {
      String number = null;
      boolean var7 = false /* VF: Semaphore variable */;
      boolean var10 = false /* VF: Semaphore variable */;

      label113: {
         try {
            label107:
            try {
               var10 = true;
               var7 = true;
               number = Phone.getInstance().getAlternateLineNumber(lineId);
               var7 = false;
               var10 = false;
               break label113;
            } finally {
               if (var10) {
                  PhoneLogger.log("Radio Exception: getLineNumber");
                  var7 = false;
                  break label107;
               }
            }
         } finally {
            if (var7) {
               if (number != null && number.length() > 0) {
                  if (format) {
                     return PhoneNumberConverter.format(number, SmartDialingOptions.getOptions().getFormattingString());
                  }

                  return number;
               }

               return PhoneResources.getString(117);
            }
         }

         if (number != null && number.length() > 0) {
            if (format) {
               return PhoneNumberConverter.format(number, SmartDialingOptions.getOptions().getFormattingString());
            }

            return number;
         }

         return PhoneResources.getString(117);
      }

      if (number == null || number.length() <= 0) {
         return PhoneResources.getString(117);
      } else {
         return format ? PhoneNumberConverter.format(number, SmartDialingOptions.getOptions().getFormattingString()) : number;
      }
   }

   public static final boolean setCurrentLine(int lineId) {
      try {
         boolean result = false;
         if ((RadioInfo.getNetworkService() & 256) != 0) {
            return false;
         }

         if (RadioInfo.getNetworkType() == 4 && RadioInfo.getState() == 1) {
            LineSwitchDialog lsd = new LineSwitchDialog();
            result = Phone.getInstance().setAlternateLine(lineId);
            lsd.showModal();
         } else {
            result = Phone.getInstance().setAlternateLine(lineId);
         }

         if (result) {
            VoiceServices.broadcastEvent(150120);
         }

         return result;
      } finally {
         PhoneLogger.log("RadioException: setActiveLine");
         return false;
      }
   }

   public static final String getLineDescription() {
      return getLineDescription(getCurrentLineId());
   }

   public static final String getLineDescription(int param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 1
      // 02: invokestatic net/rim/device/api/system/Phone.getInstance ()Lnet/rim/device/api/system/Phone;
      // 05: iload 0
      // 06: invokevirtual net/rim/device/api/system/Phone.getAlternateLineLabel (I)Ljava/lang/String;
      // 09: astore 1
      // 0a: aload 1
      // 0b: ifnull 18
      // 0e: aload 1
      // 0f: invokevirtual java/lang/String.length ()I
      // 12: ifeq 18
      // 15: goto 9c
      // 18: sipush 6305
      // 1b: invokestatic net/rim/device/apps/internal/phone/resource/PhoneResources.getString (I)Ljava/lang/String;
      // 1e: bipush 1
      // 1f: anewarray 3534
      // 22: dup
      // 23: bipush 0
      // 24: new java/lang/Object
      // 27: dup
      // 28: ldc_w ""
      // 2b: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 2e: iload 0
      // 2f: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 32: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 35: aastore
      // 36: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 39: astore 1
      // 3a: aload 1
      // 3b: areturn
      // 3c: astore 2
      // 3d: aload 1
      // 3e: ifnull 48
      // 41: aload 1
      // 42: invokevirtual java/lang/String.length ()I
      // 45: ifne 9c
      // 48: sipush 6305
      // 4b: invokestatic net/rim/device/apps/internal/phone/resource/PhoneResources.getString (I)Ljava/lang/String;
      // 4e: bipush 1
      // 4f: anewarray 3576
      // 52: dup
      // 53: bipush 0
      // 54: new java/lang/Object
      // 57: dup
      // 58: ldc_w ""
      // 5b: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 5e: iload 0
      // 5f: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 62: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 65: aastore
      // 66: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 69: astore 1
      // 6a: aload 1
      // 6b: areturn
      // 6c: astore 3
      // 6d: aload 1
      // 6e: ifnull 78
      // 71: aload 1
      // 72: invokevirtual java/lang/String.length ()I
      // 75: ifne 9a
      // 78: sipush 6305
      // 7b: invokestatic net/rim/device/apps/internal/phone/resource/PhoneResources.getString (I)Ljava/lang/String;
      // 7e: bipush 1
      // 7f: anewarray 3618
      // 82: dup
      // 83: bipush 0
      // 84: new java/lang/Object
      // 87: dup
      // 88: ldc_w ""
      // 8b: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 8e: iload 0
      // 8f: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 92: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 95: aastore
      // 96: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 99: astore 1
      // 9a: aload 3
      // 9b: athrow
      // 9c: aload 1
      // 9d: areturn
      // try (2 -> 6): 30 null
      // try (2 -> 6): 54 null
      // try (30 -> 31): 54 null
      // try (54 -> 55): 54 null
   }

   public static final void setLineDescription(int lineId, String description, boolean writeToSIM) {
      setLineInfo(lineId, getLineNumber(lineId, false), description, writeToSIM);
   }

   public static final void setLineInfo(int lineId, String number, String description, boolean writeToSIM) {
      if (RadioInfo.getNetworkType() != 4 || !writeToSIM) {
         label49:
         try {
            Phone.getInstance().setAlternateLineLabel(lineId, description);
         } finally {
            break label49;
         }
      }

      if (writeToSIM) {
         if ((lineId == 1 || lineId == 2) && RadioInfo.areWAFsSupported(3) && !DeviceInfo.isSimulator()) {
            SIMPhoneNumberWriter.write(number, description, 1, 24, lineId, true);
         }
      }
   }

   public static final String[] splitNumberAtFirstSpecialCharacter(String originalNumber) {
      String[] numbers = new Object[2];
      numbers[0] = originalNumber;
      if (originalNumber != null) {
         char dudChar = ' ';
         originalNumber = originalNumber.replace(dudChar, ',');
         int firstSpecialChar = originalNumber.indexOf(44);
         int firstWait = originalNumber.indexOf(33);
         if (firstWait > -1) {
            if (firstSpecialChar > -1) {
               firstSpecialChar = Math.min(firstSpecialChar + 1, firstWait);
            } else {
               firstSpecialChar = firstWait;
            }
         }

         if (firstSpecialChar > 0) {
            firstSpecialChar++;
         }

         if (firstSpecialChar > 0 && firstSpecialChar < originalNumber.length()) {
            String additionalNumbers;
            if (firstWait != -1) {
               additionalNumbers = originalNumber.substring(firstSpecialChar - 1);
            } else {
               additionalNumbers = originalNumber.substring(firstSpecialChar);
            }

            originalNumber = originalNumber.substring(0, firstSpecialChar - 1);
            numbers[0] = originalNumber;
            numbers[1] = additionalNumbers;
            return numbers;
         }
      }

      return numbers;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Object persistentContentListener = ar.getOrWaitFor(-3841047781361656227L);
      if (persistentContentListener == null) {
         ar.put(-3841047781361656227L, new PhoneUtilities$MyPersistentContentListener());
      }

      _phoneNumberCounter = new PhoneNumberCounter();
   }
}
