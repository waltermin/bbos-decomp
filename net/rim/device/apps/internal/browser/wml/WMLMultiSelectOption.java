package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.framework.verb.Verb;

final class WMLMultiSelectOption {
   private Verb _onPick;
   private WMLVariable _value;
   private WMLVariable _title;
   private String _initText;
   private WMLVariable[] _vars;
   private short[] _initVarStarts;
   private WMLCheckboxField _checkbox;
   private WMLMultiSelectInputField _select;

   WMLMultiSelectOption(WMLMultiSelectInputField select, WMLVariable value, WMLVariable title, Verb onPick) {
      this._onPick = onPick;
      this._value = value;
      this._title = title;
      this._select = select;
      this._checkbox = new WMLCheckboxField("", this.getTitle());
      this._checkbox.setChangeListener(this._select);
   }

   final void setLabel(String label) {
      this._initText = label;
      this._checkbox.setLabel(label);
   }

   final void setVariables(WMLVariable[] vars, short[] varStarts) {
      this._vars = vars;
      this._initVarStarts = varStarts;
      this._select.addVariable(this);
   }

   final WMLCheckboxField getCheckbox() {
      return this._checkbox;
   }

   final boolean getChecked() {
      return this._checkbox.getChecked();
   }

   final void addOnPick(Verb verb) {
      this._onPick = verb;
   }

   protected final Verb getOnPick() {
      return this._onPick;
   }

   protected final String getValue() {
      return this._value != null ? this._value.getName() : null;
   }

   protected final String getTitle() {
      return this._title != null ? this._title.getName() : null;
   }

   protected final void select() {
      synchronized (Application.getEventLock()) {
         this._checkbox.setChecked(true);
      }
   }

   final void updateValues() {
      if (this._initVarStarts != null && this._initVarStarts.length != 0) {
         String text = this._initText;
         short[] varStarts = new short[this._initVarStarts.length];
         System.arraycopy(this._initVarStarts, 0, varStarts, 0, this._initVarStarts.length);

         for (int i = 0; i < this._vars.length; i++) {
            String substring = text.substring(0, varStarts[i]);
            String value = "";
            if (this._vars[i].isComposed()) {
               value = this._vars[i].getName();
            } else {
               value = this._vars[i].getValue();
            }

            if (value == null) {
               value = "";
            }

            substring = substring.concat(value);
            substring = substring.concat(text.substring(varStarts[i] + 1));
            text = substring;
            int length = value.length() - 1;

            for (int j = i + 1; j < varStarts.length; j++) {
               varStarts[j] = (short)(varStarts[j] + length);
            }
         }

         this._checkbox.setLabel(text);
      }
   }
}
