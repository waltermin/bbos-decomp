package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.component.ObjectChoiceField;

public class BooleanChoiceField extends ObjectChoiceField {
   public static final int YES_NO = 0;
   public static final int ON_OFF = 1;
   public static final int ENABLED_DISABLED = 2;

   public BooleanChoiceField(String label, String[] customValues, boolean initialValue) {
      this(label, customValues, initialValue, 0);
   }

   public BooleanChoiceField(String label, String[] customValues, boolean initialValue, long style) {
      super(label, null, 0, style);
      this.initialize(customValues, initialValue);
   }

   public BooleanChoiceField(String label, int type, boolean initialValue) {
      this(label, type, initialValue, 0);
   }

   public BooleanChoiceField(String label, int type, boolean initialValue, long style) {
      super(label, null, 0, style);
      String[] choices;
      switch (type) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            choices = CommonResources.getYesNoArray(0);
            break;
         case 1:
            choices = new String[]{CommonResources.getString(106), CommonResources.getString(107)};
            break;
         case 2:
            choices = new String[]{CommonResources.getString(108), CommonResources.getString(109)};
      }

      this.initialize(choices, initialValue);
   }

   public boolean getSelectedChoice() {
      return this.isAffirmative();
   }

   public boolean isAffirmative() {
      int choiceIndex = this.getSelectedIndex();
      return choiceIndex == 0;
   }

   public void setSelectedChoice(boolean choice) {
      this.setAffirmative(choice);
   }

   public void setAffirmative(boolean choice) {
      this.setSelectedIndex(choice ? 0 : 1);
   }

   private void initialize(String[] values, boolean initialValue) {
      if (values != null && values.length == 2) {
         this.setChoices(values);
         this.setSelectedIndex(initialValue ? 0 : 1);
      } else {
         throw new IllegalArgumentException();
      }
   }
}
