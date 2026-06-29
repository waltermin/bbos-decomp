package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.RibbonNetworkInfo;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.ui.SimplePhoneNumberFilter;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class VoiceMailOption extends VoiceOptionsListItem {
   private boolean _isDefaultVoicemailOption;
   private Field _voicemailNumberField;
   private Field _additionalTonesField;
   private String _originalVoiceMailNumber;
   private static final int MAX_PHONE_NUMBER_LENGTH;
   private static final int ACCESS_NUMBER_FILTER_FLAGS;
   private static final int ADDITIONAL_NUMBERS_FILTER_FLAGS;

   public VoiceMailOption(Object context) {
      super(PhoneResources.getResourceBundle(), 190, context);
   }

   @Override
   public final String getDisplayName() {
      if (this._isDefaultVoicemailOption) {
         StringBuffer buf = (StringBuffer)(new Object(PhoneResources.getString(190)));
         String operator = DeviceInfo.isSimulator() ? "Rogers ATT" : RibbonNetworkInfo.getInstance().getOperatorName();
         if (operator != null && operator.length() > 0) {
            buf.append(" (");
            buf.append(operator);
            buf.append(')');
         }

         return buf.toString();
      } else {
         return super.getDisplayName();
      }
   }

   final void setIsDefaultVoicemailOption(boolean isDefault) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      super.populateMainScreen(screen);
      this._originalVoiceMailNumber = PhoneUtilities.getVoiceMailNumber();
      if (this._originalVoiceMailNumber == null) {
         this._originalVoiceMailNumber = "";
      }

      String[] numbers = PhoneUtilities.splitNumberAtFirstSpecialCharacter(this._originalVoiceMailNumber);
      this._originalVoiceMailNumber = numbers[0];
      String additionalNumbers = super._phoneOptions.getVoiceMailAdditionalTones();
      if (additionalNumbers == null || additionalNumbers.length() <= 0) {
         additionalNumbers = numbers[1];
      }

      TextFilter filter = new SimplePhoneNumberFilter(6);
      this._voicemailNumberField = this.addPhoneNumberEditField(
         stripInvalidCharacters(this._originalVoiceMailNumber, filter), PhoneResources.getString(6003), screen, filter
      );
      filter = new SimplePhoneNumberFilter(30);
      this._additionalTonesField = this.addPhoneNumberEditField(
         stripInvalidCharacters(additionalNumbers, filter), PhoneResources.getString(196), screen, filter
      );
      if (this._voicemailNumberField != null) {
         this._voicemailNumberField.setFocus();
      }
   }

   private static final String stripInvalidCharacters(String number, TextFilter filter) {
      if (filter != null && number != null) {
         StringBuffer buf = (StringBuffer)(new Object());
         int count = number.length();

         for (int idx = 0; idx < count; idx++) {
            char ch = number.charAt(idx);
            if (filter.validate(PhoneNumberServices.convertCharToDisplay(ch))) {
               buf.append(ch);
            }
         }

         return buf.toString();
      } else {
         return number;
      }
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      VerbRepository vr = VerbRepository.getVerbRepository(5586592803833705892L);
      if (vr != null) {
         Verb[] verbs = vr.getVerbs(null);
         if (verbs != null) {
            verbToMenu.addVerbs(verbs);
         }
      }
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      Field field = super._mainScreen.getLeafFieldWithFocus();
      if (field == this._voicemailNumberField) {
         PhoneUtilities.setPrivateFlag(super._context, 63);
         Verb verb = super.addCurrentItemVerbs(verbToMenu, instance);
         PhoneUtilities.clearPrivateFlag(super._context, 63);
         return verb;
      } else {
         return super.addCurrentItemVerbs(verbToMenu, instance);
      }
   }

   @Override
   protected final boolean save() {
      boolean commitRequired = false;
      if (this._voicemailNumberField.isDirty() && this._voicemailNumberField instanceof Object) {
         EditField editField = (EditField)this._voicemailNumberField;
         String number = editField.getText();
         editField = (EditField)this._additionalTonesField;
         super._phoneOptions.setVoiceMailAdditionalTones(editField.getText());
         if (!number.equals(this._originalVoiceMailNumber)) {
            super._phoneOptions.setVoiceMailNumber(number, true);
            commitRequired = true;
         }
      }

      if (this._additionalTonesField.isDirty() && this._additionalTonesField instanceof Object) {
         EditField editField = (EditField)this._additionalTonesField;
         super._phoneOptions.setVoiceMailAdditionalTones(editField.getText());
         commitRequired = true;
      }

      if (commitRequired) {
         super._phoneOptions.commit();
      }

      return super.save();
   }

   private final Field addPhoneNumberEditField(String number, String fieldLabel, Screen screen, TextFilter filter) {
      if (number == null) {
         number = "";
      }

      ContextObject context = (ContextObject)(new Object(32));
      context.put(253, number);
      RIMModel numberModel = (RIMModel)FactoryUtil.createInstance(3797587162219887872L, context);
      Field field = null;
      if (numberModel instanceof Object) {
         FieldProvider fieldProvider = (FieldProvider)numberModel;
         context.setFlag(0);
         context.put(3986845832244503196L, fieldLabel);
         field = fieldProvider.getField(context);
      }

      if (field != null) {
         if (field instanceof Object) {
            BasicEditField edit = (BasicEditField)field;
            edit.setMaxSize(20);
            if (filter != null) {
               edit.setFilter(filter);
            }

            field = edit;
         }

         screen.add(field);
      }

      return field;
   }

   @Override
   public final int getOptionsScreenOrder() {
      return 3000;
   }
}
