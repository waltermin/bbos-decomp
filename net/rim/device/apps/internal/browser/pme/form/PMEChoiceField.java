package net.rim.device.apps.internal.browser.pme.form;

import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.internal.browser.stack.FormData;

public final class PMEChoiceField extends ObjectChoiceField implements FormField {
   private String _name;
   private int _initialIndex;
   private Object[] _values;

   PMEChoiceField(String name, String label, Object[] choices, Object[] values, int initialIndex, long style) {
      super(label, choices, initialIndex, style);
      this._name = name;
      this._initialIndex = initialIndex;
      this._values = values;
   }

   @Override
   public final void submit(FormData formData, Object buffer) {
      int index = this.getSelectedIndex();
      formData.append(buffer, this._name, index == -1 ? null : this._values[index].toString());
   }

   @Override
   public final int getPreferredWidth() {
      return this.getWidth();
   }

   @Override
   protected final void layout(int width, int height) {
      width = this.getWidth();
      height = this.getHeight();
      this.setTextRectPos(0, height - this.getFont().getHeight());
      super.layout(width, height);
      this.setExtent(width, height);
   }

   @Override
   public final void reset() {
      this.setSelectedIndex(this._initialIndex);
   }
}
