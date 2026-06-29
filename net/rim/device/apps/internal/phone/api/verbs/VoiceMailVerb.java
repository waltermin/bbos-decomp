package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.ui.GetPhoneNumberDialog;
import net.rim.device.apps.internal.phone.api.ui.ScreenPopper;
import net.rim.device.apps.internal.phone.api.ui.SimplePhoneNumberFilter;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.VerticalSpacerField;
import net.rim.vm.Array;

public final class VoiceMailVerb extends Verb implements PhoneVerb, Copyable, SetParameter {
   private boolean _popScreenOnInvoke;
   private static int MAX_LENGTH = 80;

   public final void setPopScreenOnInvoke(boolean popScreenOnInvoke) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object copy() {
      VoiceMailVerb copy = new VoiceMailVerb();
      copy.setPopScreenOnInvoke(this._popScreenOnInvoke);
      return copy;
   }

   @Override
   public final void setParameter(Object parameter) {
      this.setPopScreenOnInvoke(ContextObject.getFlag(parameter, 122));
   }

   public VoiceMailVerb() {
      super(1376592);
   }

   public VoiceMailVerb(int ordering) {
   }

   @Override
   public final String toString() {
      return PhoneResources.getString(429);
   }

   @Override
   public final Object invoke(Object context) {
      Object o = ContextObject.get(context, -1540107978048774676L);
      if (o instanceof Confirmation) {
         ((Confirmation)o).confirm(this, context);
      }

      ScreenPopper screenPopper = (ScreenPopper)ContextObject.get(context, -116504832846522962L);
      if (screenPopper != null) {
         screenPopper.popScreens();
      }

      String voicemailNumber = PhoneUtilities.getVoiceMailNumber();
      String[] numbers = PhoneUtilities.splitNumberAtFirstSpecialCharacter(voicemailNumber);
      voicemailNumber = numbers[0];
      String additionalDTMFTones = PhoneOptions.getOptions().getVoiceMailAdditionalTones();
      if (additionalDTMFTones == null || additionalDTMFTones.length() <= 0) {
         additionalDTMFTones = numbers[1];
      }

      int preferredLineId = PhoneUtilities.getCurrentLineId();
      if (voicemailNumber == null || voicemailNumber.length() == 0) {
         voicemailNumber = promptForVoicemailNumber();
      } else if (!PhoneUtilities.getPrivateFlag(context, 66) && !Phone.getInstance().isActive()) {
         int[] lineIds = PhoneUtilities.getAllLineIds();
         if (lineIds.length > 1) {
            int[] voicemailLineIds = new int[0];

            for (int i = 0; i < lineIds.length; i++) {
               String currentVoicemail = PhoneUtilities.getVoiceMailNumber(lineIds[i]);
               if (currentVoicemail != null && currentVoicemail.length() > 0) {
                  Array.resize(voicemailLineIds, voicemailLineIds.length + 1);
                  voicemailLineIds[voicemailLineIds.length - 1] = lineIds[i];
               }
            }

            if (voicemailLineIds.length == 0) {
               voicemailNumber = promptForVoicemailNumber();
            } else if (voicemailLineIds.length == 1) {
               preferredLineId = voicemailLineIds[0];
               voicemailNumber = PhoneUtilities.getVoiceMailNumber(preferredLineId);
            } else {
               String[] allVoicemailNumbers = new String[voicemailLineIds.length];
               int defaultIndex = 0;
               VoicemailChooser voiceMailChooser = new VoicemailChooser();
               voiceMailChooser.add(new LabelField(PhoneResources.getString(6317)));
               voiceMailChooser.add(new VerticalSpacerField(4));

               for (int i = 0; i < allVoicemailNumbers.length; i++) {
                  ButtonField button = new ButtonField(PhoneUtilities.getLineDescription(voicemailLineIds[i]), 12884901888L);
                  if (VoicemailIconManager.getInstance().isIndicatorOn(voicemailLineIds[i])) {
                     button.setImage(VoicemailIconManager.getIconCollection().getImage(0));
                  }

                  button.setChangeListener(voiceMailChooser);
                  voiceMailChooser.add(button);
                  if (voicemailLineIds[i] == preferredLineId) {
                     defaultIndex = i;
                  }
               }

               voiceMailChooser.setDefault(defaultIndex);
               voiceMailChooser.show();
               int index = voiceMailChooser.getCloseReason();
               if (index == -1) {
                  return null;
               }

               voicemailNumber = PhoneUtilities.getVoiceMailNumber(voicemailLineIds[index]);
               additionalDTMFTones = PhoneOptions.getOptions().getVoiceMailAdditionalTones(voicemailLineIds[index]);
               preferredLineId = voicemailLineIds[index];
            }
         }
      }

      if (voicemailNumber == null) {
         return null;
      }

      if (additionalDTMFTones != null) {
         additionalDTMFTones = additionalDTMFTones.trim();
      }

      if (additionalDTMFTones != null && additionalDTMFTones.length() > 0) {
         StringBuffer buffer = new StringBuffer(voicemailNumber);
         char lastCharInNumber = voicemailNumber.charAt(voicemailNumber.length() - 1);
         char firstCharInExtra = additionalDTMFTones.charAt(0);
         if (PhoneNumberServices.isDTMFKey(lastCharInNumber) && PhoneNumberServices.isDTMFKey(firstCharInExtra)) {
            buffer.append(',');
         }

         buffer.append(additionalDTMFTones);
         voicemailNumber = buffer.toString();
      }

      ContextObject invocationContext = ContextObject.clone(context);
      invocationContext.setPrivateFlag(4936088360624690805L, 7);
      invocationContext.setFlag(117);
      if (PhoneUtilities.getPrivateFlag(context, 66) && !PhoneUtilities.isQuietProfileOn() && !PhoneUtilities.isDiscreetProfileOn()) {
         invocationContext.put(2848872683723475070L, "speed_dial.mp3");
      }

      Object connectionContext = PhoneUtilities.getCallConnectionParameters(voicemailNumber, null, null, preferredLineId, invocationContext);
      if (this._popScreenOnInvoke) {
         UiApplication app = UiApplication.getUiApplication();
         Screen screen = app.getActiveScreen();
         if (screen != null) {
            UiApplication.getUiApplication().popScreen(screen);
         }
      }

      OutgoingCallConnector.startCall(connectionContext);
      return connectionContext;
   }

   public static final String promptForVoicemailNumber() {
      String prompt = PhoneResources.getString(178);
      GetPhoneNumberDialog dlg = new GetPhoneNumberDialog(prompt, MAX_LENGTH);
      int filterFlags = 6;
      dlg.setFilter(new SimplePhoneNumberFilter(filterFlags));
      String voicemailNumber = dlg.getPhoneNumber();
      if (voicemailNumber != null && voicemailNumber.length() > 0) {
         PhoneOptions phoneOptions = PhoneOptions.getOptions();
         phoneOptions.setVoiceMailNumber(voicemailNumber, true);
         phoneOptions.commit();
      }

      if (voicemailNumber != null) {
         voicemailNumber = voicemailNumber.trim();
      }

      return voicemailNumber;
   }
}
