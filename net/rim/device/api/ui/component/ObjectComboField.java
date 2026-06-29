package net.rim.device.api.ui.component;

import net.rim.device.api.util.StringUtilities;

public class ObjectComboField extends ComboField {
   protected ObjectComboField$ListCallback _listCallback;

   public ObjectComboField(String label, Object[] choices) {
      this.setEditable(new ObjectComboField$Editable(label));
      this.setList(new ListField());
      this.setController(new ObjectComboField$Controller(this));
      this._listCallback = new ObjectComboField$ListCallback(this, choices);
      this.getList().setCallback(this._listCallback);
   }

   protected boolean doFilter() {
      return true;
   }

   protected boolean matches(String choice, String criterion) {
      return StringUtilities.startsWithIgnoreCase(choice, criterion);
   }
}
