package net.rim.device.apps.internal.browser.wml;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.api.framework.verb.Verb;

final class WMLSingleSelectInputField implements FieldChangeListener, WMLInputField {
   private WMLContextManager _contextManager;
   private String _name;
   private WMLVariable _value;
   private String _iname;
   private WMLVariable _ivalue;
   private Vector _options = (Vector)(new Object());
   private Vector _variables;
   private RadioButtonGroup _group;
   private boolean _initialSelect;

   protected final int validate(String optionIndex) {
      int index = 0;

      try {
         index = Integer.parseInt(optionIndex);
      } finally {
         ;
      }

      return index >= 1 && index <= this._options.size() ? index : 0;
   }

   final void doneAddingChoices() {
      if (this._options.size() == 0) {
         if (this._iname != null) {
            this._contextManager.put(this._iname, "1");
         }

         if (this._name != null) {
            this._contextManager.put(this._name, "");
         }
      }

      this._group = (RadioButtonGroup)(new Object());
      this._group.setNotifyReselected(true);
      int optionsSize = this._options.size();

      for (int i = 0; i < optionsSize; i++) {
         this._group.add(((WMLSingleSelectOption)this._options.elementAt(i)).getRadioButton());
      }
   }

   final void addVariable(WMLSingleSelectOption variable) {
      if (this._variables == null) {
         this._variables = (Vector)(new Object());
      }

      this._variables.addElement(variable);
   }

   final void addOption(WMLSingleSelectOption option) {
      this._options.addElement(option);
   }

   @Override
   public final boolean isModified() {
      if (this._options == null) {
         return false;
      }

      int size = this._options.size();

      for (int i = 0; i < size; i++) {
         if (((WMLSingleSelectOption)this._options.elementAt(i)).getRadioButton().isDirty()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final void setFocus() {
      if (this._options != null && this._options.size() > 0) {
         WMLSingleSelectOption option = (WMLSingleSelectOption)this._options.elementAt(0);
         if (option != null) {
            option.getRadioButton().setFocus();
         }
      }
   }

   @Override
   public final boolean validate() {
      return true;
   }

   @Override
   public final void submit(WMLContext wmlContext) {
   }

   @Override
   public final void setAutoExecVerb(Verb verb) {
      int size = this._options.size();
      if (verb != null) {
         for (int i = 0; i < size; i++) {
            if (((WMLSingleSelectOption)this._options.elementAt(i)).getOnPick() == null) {
               ((WMLSingleSelectOption)this._options.elementAt(i)).addOnPick(verb);
            }
         }
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      int optionsSize = this._options.size();

      for (int i = 0; i < optionsSize; i++) {
         WMLSingleSelectOption option = (WMLSingleSelectOption)this._options.elementAt(i);
         if (option.isOn() && option.getRadioButton() == field) {
            if (this._name != null) {
               String optionValue = option.getValue();
               if (optionValue != null) {
                  this._contextManager.put(this._name, option.getValue());
               }
            }

            if (this._iname != null) {
               this._contextManager.put(this._iname, String.valueOf(i + 1));
            }

            if (!this._initialSelect) {
               Verb verb = option.getOnPick();
               if (verb != null) {
                  verb.invoke(null);
               }
            }
         }
      }
   }

   @Override
   public final void refresh() {
      if (this._variables != null) {
         int variablesSize = this._variables.size();

         for (int i = 0; i < variablesSize; i++) {
            WMLSingleSelectOption variable = (WMLSingleSelectOption)this._variables.elementAt(i);
            variable.updateValues();
         }
      }

      int defaultOptionIndex = 0;
      if (this._iname != null) {
         defaultOptionIndex = this.validate(this._contextManager.get(this._iname));
      }

      if (defaultOptionIndex == 0 && this._ivalue != null) {
         defaultOptionIndex = this.validate(this._ivalue.getName());
      }

      if (defaultOptionIndex == 0 && this._name != null) {
         String defaultOptionValue = this._contextManager.get(this._name);
         if (defaultOptionValue.length() != 0) {
            int optionsSize = this._options.size();

            for (int i = 0; i < optionsSize; i++) {
               String optionValue = ((WMLSingleSelectOption)this._options.elementAt(i)).getValue();
               if (optionValue != null && optionValue.equals(defaultOptionValue)) {
                  defaultOptionIndex = i + 1;
                  break;
               }
            }
         }
      }

      if (defaultOptionIndex == 0 && this._value != null && this._value.getName().length() != 0) {
         int optionsSize = this._options.size();

         for (int i = 0; i < optionsSize; i++) {
            String optionValue = ((WMLSingleSelectOption)this._options.elementAt(i)).getValue();
            if (optionValue != null && optionValue.equals(this._value.getName())) {
               defaultOptionIndex = i + 1;
               break;
            }
         }
      }

      if (defaultOptionIndex == 0) {
         defaultOptionIndex = 1;
      }

      if (defaultOptionIndex <= this._options.size()) {
         this._initialSelect = true;
         ((WMLSingleSelectOption)this._options.elementAt(defaultOptionIndex - 1)).select();
         this._initialSelect = false;
      }
   }

   WMLSingleSelectInputField(WMLContextManager contextManager, String name, String title, WMLVariable value, String iname, WMLVariable ivalue) {
      this._contextManager = contextManager;
      this._name = name;
      this._value = value;
      this._iname = iname;
      this._ivalue = ivalue;
   }
}
