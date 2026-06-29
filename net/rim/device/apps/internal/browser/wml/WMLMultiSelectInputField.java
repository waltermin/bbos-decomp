package net.rim.device.apps.internal.browser.wml;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.apps.api.framework.verb.Verb;

final class WMLMultiSelectInputField implements FieldChangeListener, WMLInputField {
   private WMLContextManager _contextManager;
   private String _name;
   private WMLVariable _value;
   private String _iname;
   private WMLVariable _ivalue;
   private Vector _options;
   private Vector _variables;
   private boolean _initialSelect;

   protected final void updateVariablesFromSelections() {
      if (this._iname != null) {
         if (this._options != null && this._options.size() != 0) {
            StringBuffer sb = (StringBuffer)(new Object());
            boolean firstOne = true;

            for (int i = 0; i < this._options.size(); i++) {
               if (((WMLMultiSelectOption)this._options.elementAt(i)).getChecked()) {
                  if (firstOne) {
                     firstOne = false;
                  } else {
                     sb.append(';');
                  }

                  sb.append(String.valueOf(i + 1));
               }
            }

            if (sb.length() == 0) {
               this._contextManager.put(this._iname, "0");
            } else {
               this._contextManager.put(this._iname, sb.toString());
            }
         } else {
            this._contextManager.put(this._iname, "0");
         }
      }

      if (this._name != null) {
         if (this._options == null || this._options.size() == 0) {
            this._contextManager.put(this._name, "");
            return;
         }

         StringBuffer sb = (StringBuffer)(new Object());
         boolean firstOne = true;

         for (int i = 0; i < this._options.size(); i++) {
            if (((WMLMultiSelectOption)this._options.elementAt(i)).getChecked()) {
               String optionValue = ((WMLMultiSelectOption)this._options.elementAt(i)).getValue();
               if (optionValue != null && optionValue.length() != 0) {
                  if (firstOne) {
                     firstOne = false;
                  } else {
                     sb.append(';');
                  }

                  sb.append(optionValue);
               }
            }
         }

         if (sb.length() == 0) {
            this._contextManager.put(this._name, "");
            return;
         }

         this._contextManager.put(this._name, sb.toString());
      }
   }

   protected final boolean setOptionIndexes(boolean[] optionIndex, String value) {
      boolean wasModified = false;
      if (value != null) {
         int currentPos = 0;
         int valueLength = value.length();

         while (currentPos < valueLength) {
            int semicolon = value.indexOf(59, currentPos);
            String specificValue = null;
            if (semicolon == -1) {
               specificValue = value.substring(currentPos);
               currentPos = valueLength;
            } else {
               specificValue = value.substring(currentPos, semicolon);
               currentPos = semicolon + 1;
            }

            for (int i = 0; i < this._options.size(); i++) {
               String optionValue = ((WMLMultiSelectOption)this._options.elementAt(i)).getValue();
               if (optionValue != null && optionValue.equals(specificValue)) {
                  optionIndex[i] = true;
                  wasModified = true;
                  break;
               }
            }
         }
      }

      return wasModified;
   }

   final boolean validate(boolean[] optionIndexArray, String optionIndex) {
      if (optionIndex == null) {
         return false;
      }

      int currentPos = 0;
      int optionIndexLength = optionIndex.length();
      boolean wasModified = false;

      while (currentPos < optionIndexLength) {
         int semicolon = optionIndex.indexOf(59, currentPos);
         String currentIndex = null;
         if (semicolon == -1) {
            currentIndex = optionIndex.substring(currentPos);
            currentPos = optionIndexLength;
         } else {
            currentIndex = optionIndex.substring(currentPos, semicolon);
            currentPos = semicolon + 1;
         }

         int indexVal = -1;

         try {
            indexVal = Integer.parseInt(currentIndex);
         } finally {
            ;
         }

         if (indexVal >= 1 && indexVal <= this._options.size()) {
            optionIndexArray[indexVal - 1] = true;
            wasModified = true;
         }
      }

      return wasModified;
   }

   final void doneAddingChoices() {
      if (this._options == null || this._options.size() == 0) {
         if (this._name != null) {
            this._contextManager.put(this._name, "");
         }

         if (this._iname != null) {
            this._contextManager.put(this._iname, "1");
         }
      }
   }

   final void addVariable(WMLMultiSelectOption variable) {
      if (this._variables == null) {
         this._variables = (Vector)(new Object());
      }

      this._variables.addElement(variable);
   }

   final void addOption(WMLMultiSelectOption option) {
      if (this._options == null) {
         this._options = (Vector)(new Object());
      }

      this._options.addElement(option);
   }

   @Override
   public final boolean validate() {
      return true;
   }

   @Override
   public final boolean isModified() {
      if (this._options == null) {
         return false;
      }

      int size = this._options.size();

      for (int i = 0; i < size; i++) {
         if (((WMLMultiSelectOption)this._options.elementAt(i)).getCheckbox().isDirty()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final void setFocus() {
      if (this._options != null && this._options.size() > 0) {
         WMLMultiSelectOption option = (WMLMultiSelectOption)this._options.elementAt(0);
         if (option != null) {
            option.getCheckbox().setFocus();
         }
      }
   }

   @Override
   public final void submit(WMLContext wmlContext) {
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      this.updateVariablesFromSelections();
      if (!this._initialSelect) {
         Verb onPickVerb = null;

         for (int i = 0; i < this._options.size(); i++) {
            WMLMultiSelectOption option = (WMLMultiSelectOption)this._options.elementAt(i);
            if (option.getCheckbox() == field && (context & -2147483648) == 0) {
               onPickVerb = option.getOnPick();
               break;
            }
         }

         if (onPickVerb != null) {
            onPickVerb.invoke(null);
         }
      }
   }

   @Override
   public final void setAutoExecVerb(Verb verb) {
   }

   @Override
   public final void refresh() {
      if (this._variables != null) {
         int variablesSize = this._variables.size();

         for (int i = 0; i < variablesSize; i++) {
            WMLMultiSelectOption variable = (WMLMultiSelectOption)this._variables.elementAt(i);
            variable.updateValues();
         }
      }

      boolean[] defaultOptionIndex = new boolean[this._options.size()];
      boolean wasModified = false;
      if (this._iname != null) {
         wasModified = this.validate(defaultOptionIndex, this._contextManager.get(this._iname));
      }

      if (!wasModified && this._ivalue != null) {
         wasModified = this.validate(defaultOptionIndex, this._ivalue.getName());
      }

      if (!wasModified && this._name != null) {
         wasModified = this.setOptionIndexes(defaultOptionIndex, this._contextManager.get(this._name));
      }

      if (!wasModified && this._value != null && this._value.getName() != null) {
         wasModified = this.setOptionIndexes(defaultOptionIndex, this._value.getName());
      }

      this._initialSelect = true;

      for (int i = 0; i < defaultOptionIndex.length; i++) {
         if (defaultOptionIndex[i]) {
            ((WMLMultiSelectOption)this._options.elementAt(i)).select();
         }
      }

      this._initialSelect = false;
   }

   WMLMultiSelectInputField(WMLContextManager contextManager, String name, String title, WMLVariable value, String iname, WMLVariable ivalue) {
      this._name = name;
      this._value = value;
      this._iname = iname;
      this._ivalue = ivalue;
      this._contextManager = contextManager;
   }
}
