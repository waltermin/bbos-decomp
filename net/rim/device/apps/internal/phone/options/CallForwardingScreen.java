package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;

final class CallForwardingScreen extends PhoneOptionsListItemScreen implements EditForwardingNumbersScreen$Callback, GlobalEventListener {
   private ObjectChoiceField _allCallsNumberField;
   private ObjectChoiceField _busyNumberField;
   private ObjectChoiceField _noAnswerNumberField;
   private ObjectChoiceField _unreachableNumberField;
   private RadioButtonGroup _radioButtons;
   private ObjectChoiceField[] _choiceFields = new Object[4];
   private String[] _currentForwardingNumbers = null;
   private int[] _forwardingStatusFlags;
   private RadioButtonField _allCallsRadioButton;
   private RadioButtonField _unansweredCallsRadioButton;
   private boolean _cfuAvailable;
   private boolean _nonCFUAvailable;
   private boolean _forwardingNumbersOutOfDate;
   private CallForwardingScreen$SaveVerb _saveVerb = new CallForwardingScreen$SaveVerb(this);
   private static int[] _originalChoiceIndices = new int[4];
   private static final int DO_NOT_FWD_INDEX = 0;

   final Verb getSaveVerb() {
      return this._saveVerb;
   }

   public final String[] getActiveForwardingNumbers() {
      return this._currentForwardingNumbers;
   }

   final void populate() {
      if (PhoneUtilities.getAllLineIds().length > 1) {
         this.add(
            (Field)(new Object(
               ((StringBuffer)(new Object()))
                  .append(PhoneUtilities.getLineDescription())
                  .append(" (")
                  .append(PhoneUtilities.getLineNumber(PhoneUtilities.getCurrentLineId(), true))
                  .append(")")
                  .toString()
            ))
         );
         this.add((Field)(new Object()));
      }

      String changeNumberMenuText = PhoneResources.getString(6237);
      this._cfuAvailable = this.isOptionAvailable(this._forwardingStatusFlags[0]);
      boolean cfbAvailable = this.isOptionAvailable(this._forwardingStatusFlags[1]);
      boolean cfnryAvailable = this.isOptionAvailable(this._forwardingStatusFlags[2]);
      boolean cfnrcAvailable = this.isOptionAvailable(this._forwardingStatusFlags[3]);
      boolean allCallsEnabled = (this._forwardingStatusFlags[0] & 2) != 0;
      this._nonCFUAvailable = cfbAvailable || cfnryAvailable || cfnrcAvailable;
      this._currentForwardingNumbers = this.getCurrentForwardingNumbers(this._forwardingStatusFlags);
      String[] allForwardingNumberChoices = this.getAllForwardingNumberChoices(
         this._currentForwardingNumbers, PhoneOptions.getOptions().getSavedForwardingNumbers()
      );
      int selIndex = 0;
      if (!this._nonCFUAvailable) {
         selIndex = this.getForwardingNumberChoiceIndex(allForwardingNumberChoices, this._currentForwardingNumbers, 0);
         this._allCallsNumberField = (ObjectChoiceField)(new Object(PhoneResources.getString(6132), allForwardingNumberChoices, allCallsEnabled ? selIndex : 0));
         this.add(this._allCallsNumberField);
      } else {
         if (this._cfuAvailable) {
            this._radioButtons = (RadioButtonGroup)(new Object());
            this._allCallsRadioButton = (RadioButtonField)(new Object(PhoneResources.getString(6132), this._radioButtons, allCallsEnabled));
            this.add(this._allCallsRadioButton);
            selIndex = this.getForwardingNumberChoiceIndex(allForwardingNumberChoices, this._currentForwardingNumbers, 0);
            this._allCallsNumberField = (ObjectChoiceField)(new Object(null, allForwardingNumberChoices, allCallsEnabled ? selIndex : 0));
            this._allCallsNumberField.setOptionsMenuText(changeNumberMenuText);
            this.add(this._allCallsNumberField);
            this.add((Field)(new Object(6)));
            this._unansweredCallsRadioButton = (RadioButtonField)(new Object(PhoneResources.getString(6133), this._radioButtons, !allCallsEnabled));
            this.add(this._unansweredCallsRadioButton);
            this.add((Field)(new Object(4)));
         }

         boolean busyEnabled = !allCallsEnabled && this.isOptionEnabled(this._forwardingStatusFlags[1]);
         boolean noAnswerEnabled = !allCallsEnabled && this.isOptionEnabled(this._forwardingStatusFlags[2]);
         boolean notReachableEnabled = !allCallsEnabled && this.isOptionEnabled(this._forwardingStatusFlags[3]);
         if (cfbAvailable) {
            selIndex = this.getForwardingNumberChoiceIndex(allForwardingNumberChoices, this._currentForwardingNumbers, 1);
            this._busyNumberField = (ObjectChoiceField)(new Object(PhoneResources.getString(6009), allForwardingNumberChoices, busyEnabled ? selIndex : 0));
            this._busyNumberField.setOptionsMenuText(changeNumberMenuText);
            this.add(this._busyNumberField);
         }

         if (cfnryAvailable) {
            selIndex = this.getForwardingNumberChoiceIndex(allForwardingNumberChoices, this._currentForwardingNumbers, 2);
            this._noAnswerNumberField = (ObjectChoiceField)(new Object(
               PhoneResources.getString(6010), allForwardingNumberChoices, noAnswerEnabled ? selIndex : 0
            ));
            this._noAnswerNumberField.setOptionsMenuText(changeNumberMenuText);
            this.add(this._noAnswerNumberField);
         }

         if (cfnrcAvailable) {
            selIndex = this.getForwardingNumberChoiceIndex(allForwardingNumberChoices, this._currentForwardingNumbers, 3);
            this._unreachableNumberField = (ObjectChoiceField)(new Object(
               PhoneResources.getString(6011), allForwardingNumberChoices, notReachableEnabled ? selIndex : 0
            ));
            this._unreachableNumberField.setOptionsMenuText(changeNumberMenuText);
            this.add(this._unreachableNumberField);
         }
      }

      _originalChoiceIndices[0] = this._allCallsNumberField != null ? this._allCallsNumberField.getSelectedIndex() : -1;
      this._choiceFields[0] = this._allCallsNumberField;
      _originalChoiceIndices[1] = this._busyNumberField != null ? this._busyNumberField.getSelectedIndex() : -1;
      this._choiceFields[1] = this._busyNumberField;
      _originalChoiceIndices[2] = this._noAnswerNumberField != null ? this._noAnswerNumberField.getSelectedIndex() : -1;
      this._choiceFields[2] = this._noAnswerNumberField;
      _originalChoiceIndices[3] = this._unreachableNumberField != null ? this._unreachableNumberField.getSelectedIndex() : -1;
      this._choiceFields[3] = this._unreachableNumberField;
   }

   final void saveChanges() {
      boolean disableFirst = true;
      if (!this._nonCFUAvailable) {
         disableFirst = !PhoneUtilities.cdmaWAFActive();
         this.setForwardingNumbersFromFields(new Object[]{this._allCallsNumberField}, new int[]{0, -804651006, 0, 0}, disableFirst);
      } else if (!this._cfuAvailable) {
         ObjectChoiceField[] fields;
         int[] types;
         if (PhoneUtilities.cdmaWAFActive()) {
            fields = new Object[0];
            types = new int[0];
            disableFirst = false;

            for (int i = 1; i < 4; i++) {
               ObjectChoiceField f = this._choiceFields[i];
               if (_originalChoiceIndices[i] >= 0) {
                  Array.resize(fields, fields.length + 1);
                  Array.resize(types, types.length + 1);
                  fields[fields.length - 1] = f;
                  types[types.length - 1] = i;
               }
            }
         } else {
            fields = new Object[]{this._busyNumberField, this._noAnswerNumberField, this._unreachableNumberField};
            types = new int[]{1, 2, 3, -804651005, 2, 3, 4, -804651007, 3, -804651006, 3, 20};
         }

         this.setForwardingNumbersFromFields(fields, types, disableFirst);
      } else {
         boolean allCallsSelected = this._allCallsRadioButton.isSelected();
         boolean unansweredCallsSelected = !allCallsSelected;
         if (!this._allCallsRadioButton.isSelected()) {
            if (this._allCallsNumberField != null) {
               this._allCallsNumberField.setSelectedIndex(0);
            }

            ObjectChoiceField[] fields;
            int[] types;
            if (PhoneUtilities.cdmaWAFActive()) {
               fields = new Object[0];
               types = new int[0];
               disableFirst = false;

               for (int i = 0; i < 4; i++) {
                  ObjectChoiceField field = this._choiceFields[i];
                  if (field != null && _originalChoiceIndices[i] >= 0) {
                     Array.resize(fields, fields.length + 1);
                     Array.resize(types, i);
                     fields[fields.length - 1] = field;
                     types[types.length - 1] = i;
                  }
               }
            } else {
               fields = new Object[]{this._busyNumberField, this._noAnswerNumberField, this._unreachableNumberField};
               types = new int[]{1, 2, 3, -804651005, 2, 3, 4, -804651007, 3, -804651006, 3, 20};
            }

            this.setForwardingNumbersFromFields(fields, types, disableFirst);
         } else {
            for (int i = 1; i < 4; i++) {
               if (this._choiceFields[i] != null) {
                  this._choiceFields[i].setSelectedIndex(0);
               }
            }

            ObjectChoiceField[] fields;
            int[] types;
            if (PhoneUtilities.cdmaWAFActive()) {
               fields = new Object[0];
               types = new int[0];
               disableFirst = false;

               for (int i = 0; i < 4; i++) {
                  ObjectChoiceField field = this._choiceFields[i];
                  if (field != null && _originalChoiceIndices[i] >= 0) {
                     Array.resize(fields, fields.length + 1);
                     Array.resize(types, i);
                     fields[fields.length - 1] = field;
                     types[types.length - 1] = i;
                  }
               }
            } else {
               fields = new Object[]{this._allCallsNumberField};
               types = new int[]{0, -804651006, 0, 0};
            }

            this.setForwardingNumbersFromFields(fields, types, disableFirst);
         }
      }
   }

   final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      if (!this.isDirty() && !PhoneUtilities.cdmaWAFActive()) {
         verbToMenu.addVerb(new CallForwardingScreen$CancelVerb(this));
      } else {
         verbToMenu.addVerb(this._saveVerb);
         verbToMenu.addVerb(new CallForwardingScreen$CancelVerb(this));
      }
   }

   @Override
   public final void onForwardingNumbersChanged() {
      this._forwardingNumbersOutOfDate = true;
   }

   CallForwardingScreen(int[] forwardingStatusFlags) {
      super(null);
      this._forwardingStatusFlags = forwardingStatusFlags;
      this.populate();
   }

   private final String[] getAllForwardingNumberChoices(String[] setForwardingNumbers, String[] savedOptionsForwardingNumbers) {
      String[] numbers = new Object[1 + setForwardingNumbers.length + savedOptionsForwardingNumbers.length];
      numbers[0] = PhoneResources.getString(6136);
      int count = 1;

      for (int i = 0; i < setForwardingNumbers.length; i++) {
         String num = setForwardingNumbers[i];
         if (!PhoneUtilities.isEmptyString(num) && !this.existsAlready(num, numbers)) {
            numbers[count++] = setForwardingNumbers[i];
         }
      }

      for (int i = 0; i < savedOptionsForwardingNumbers.length; i++) {
         String numberToAdd = savedOptionsForwardingNumbers[i];
         if (!PhoneUtilities.isEmptyString(numberToAdd) && !this.existsAlready(numberToAdd, numbers)) {
            numbers[count++] = numberToAdd;
         }
      }

      Array.resize(numbers, count);
      return numbers;
   }

   private final boolean existsAlready(String number, String[] numbers) {
      if (numbers != null) {
         for (int i = 0; i < numbers.length; i++) {
            if (this.numbersEqual(numbers[i], number)) {
               return true;
            }
         }
      }

      return false;
   }

   private final boolean typeIsEnabled(int fwdingType) {
      int flags = this._forwardingStatusFlags[fwdingType];
      return this.isOptionAvailable(flags) && this.isOptionEnabled(flags);
   }

   private final int getForwardingNumberChoiceIndex(String[] allChoices, String[] setForwardingNumbers, int fwdingType) {
      if (!this.typeIsEnabled(fwdingType)) {
         return 0;
      }

      for (int i = 0; i < allChoices.length; i++) {
         if (this.numbersEqual(allChoices[i], setForwardingNumbers[fwdingType])) {
            return i;
         }
      }

      return 0;
   }

   private final boolean numbersEqual(String num1, String num2) {
      num1 = PhoneNumberServices.stripPlusSign(num1);
      num2 = PhoneNumberServices.stripPlusSign(num2);
      Object n1 = PhoneUtilities.createNumberModel(num1);
      Object n2 = PhoneUtilities.createNumberModel(num2);
      return n1 != null && n2 != null && n1.equals(n2);
   }

   @Override
   public final void save() {
      this.saveChanges();
   }

   private final boolean isOptionAvailable(int flags) {
      return SSManager.isSSOptionProvisioned(flags);
   }

   private final void setForwardingNumbersFromFields(ObjectChoiceField[] fields, int[] types, boolean disableFirst) {
      String[] numbers = new Object[fields.length];

      for (int i = 0; i < fields.length; i++) {
         ObjectChoiceField field = fields[i];
         if (field != null) {
            int index = field.getSelectedIndex();
            switch (index) {
               case 0:
                  if (PhoneUtilities.cdmaWAFActive()) {
                     numbers[i] = "";
                  }
                  break;
               default:
                  try {
                     Object o = field.getChoice(index);
                     if (o != null) {
                        numbers[i] = o.toString();
                     }
                  } finally {
                     continue;
                  }
            }
         }
      }

      boolean showStatus = true;
      if (PhoneUtilities.cdmaWAFActive() && Branding.getVendorId() == 122) {
         showStatus = false;
      }

      new SetForwardingNumbers(types, numbers, disableFirst, showStatus).start();
   }

   private final String[] getCurrentForwardingNumbers(int[] fwdingFlags) {
      int[] types = ForwardingTypes.ALL_TYPES;
      String[] numbers = new Object[4];
      int lineBearer = PhoneUtilities.getCurrentLineId();
      byte var6;
      if (lineBearer == 1) {
         var6 = 2;
      } else if (lineBearer == 2) {
         var6 = 11;
      } else {
         var6 = 0;
      }

      for (int i = 0; i < types.length; i++) {
         if (this.isOptionAvailable(fwdingFlags[i])) {
            numbers[i] = VoiceServices.getCallForwardingNumber(CallForwardingOption.mapForwardingType(types[i]), var6);
            System.out.println(((StringBuffer)(new Object("PH-NWFN("))).append(types[i]).append(")=").append(numbers[i]).toString());
            if (!this.isOptionEnabled(fwdingFlags[i]) || numbers[i] == null) {
               numbers[i] = "";
            }

            if (!PhoneUtilities.isEmptyString(numbers[i])) {
               PhoneOptions.getOptions().addSavedForwardingNumber(numbers[i]);
            }
         }
      }

      return numbers;
   }

   @Override
   protected final void onExposed() {
      if (this._forwardingNumbersOutOfDate) {
         this._forwardingNumbersOutOfDate = false;
         Field focusedField = this.getLeafFieldWithFocus();
         int fieldIndex = focusedField.getIndex();
         this.deleteAll();
         this.populate();
         focusedField = this.getField(fieldIndex);
         focusedField.setFocus();
      }

      super.onExposed();
   }

   @Override
   public final boolean keyChar(char key, int time, int status) {
      if (key == 27) {
         new CallForwardingScreen$CancelVerb(this).invoke(null);
         return true;
      } else {
         return super.keyChar(key, time, status);
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this.isDirty() || PhoneUtilities.cdmaWAFActive()) {
         menu.add((MenuItem)(new Object(this._saveVerb, 100)));
      }

      menu.add((MenuItem)(new Object(new CallForwardingScreen$CancelVerb(this), 500)));
      menu.add((MenuItem)(new Object(new CallForwardingScreen$EditNumbersVerb(this), 500)));
      menu.add((MenuItem)(new Object(new AddNumberVerb(this), 500)));
   }

   private final boolean isOptionEnabled(int flags) {
      return (flags & 2) != 0;
   }

   @Override
   protected final void optionsUpdated() {
      this.onForwardingNumbersChanged();
   }

   @Override
   protected final void setDefaultMenuItem(SystemEnabledMenu menu, ContextObject amenuContext) {
      if (this.isDirty() || PhoneUtilities.cdmaWAFActive()) {
         menu.setDefault(this._saveVerb);
      }
   }
}
