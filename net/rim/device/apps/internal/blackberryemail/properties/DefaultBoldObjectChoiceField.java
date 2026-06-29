package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.apps.api.ui.BoldObjectChoiceField;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public class DefaultBoldObjectChoiceField extends BoldObjectChoiceField {
   private int _defaultChoiceIndex;

   public DefaultBoldObjectChoiceField(String label, long style) {
      super(label, null, null, 0, style);
      this.addDefaultChoice();
   }

   public DefaultBoldObjectChoiceField(String label, Object[] choices, boolean[] boldChoices, long style) {
      super(label, choices, boldChoices, 0, style);
      this.addDefaultChoice();
   }

   private void addDefaultChoice() {
      int numActualChoices = this.getSize();
      this._defaultChoiceIndex = numActualChoices;
      this.setSize(numActualChoices + 1);
      this.setSelectedIndex(numActualChoices);
   }

   public void removeDefaultChoice() {
      if (this._defaultChoiceIndex >= 0) {
         this._defaultChoiceIndex = -1;
         int numActualChoices = this.getSize() - 1;
         int selectedIndex = Math.min(this.getSelectedIndex(), numActualChoices - 1);
         this.setSize(numActualChoices);
         this.setSelectedIndex(selectedIndex);
      }
   }

   public boolean isDefaultSelected() {
      return this._defaultChoiceIndex >= 0 && this.getSelectedIndex() == this._defaultChoiceIndex;
   }

   @Override
   public void setChoices(Object[] choices, boolean[] boldChoices) {
      super.setChoices(choices, boldChoices);
      if (this._defaultChoiceIndex >= 0) {
         this.addDefaultChoice();
      }
   }

   @Override
   public Object getChoice(int index) {
      return this._defaultChoiceIndex >= 0 && index == this._defaultChoiceIndex ? EmailResources.getString(99) : super.getChoice(index);
   }

   @Override
   public boolean isBold(int index) {
      return index == this._defaultChoiceIndex ? false : super.isBold(index);
   }
}
