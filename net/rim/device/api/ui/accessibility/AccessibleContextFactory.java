package net.rim.device.api.ui.accessibility;

public class AccessibleContextFactory implements AccessibleContext {
   String _name;
   String _description;
   int _roll;
   int _state;
   AccessibleContext _parent;
   AccessibleText _text;

   public AccessibleContextFactory(String name) {
      this(name, null, 0, 0, null, null);
   }

   public AccessibleContextFactory(String name, int roll) {
      this(name, null, roll, 0, null, null);
   }

   public AccessibleContextFactory(String name, int roll, int state) {
      this(name, null, roll, state, null, null);
   }

   public AccessibleContextFactory(String name, int roll, int state, AccessibleContext parent) {
      this(name, null, roll, state, parent, null);
   }

   public AccessibleContextFactory(String name, String description, int roll, int state, AccessibleContext parent, AccessibleText text) {
      this._name = name;
      this._description = description;
      this._roll = roll;
      this._state = state;
      this._parent = parent;
      this._text = text;
   }

   @Override
   public String getAccessibleName() {
      return this._name;
   }

   @Override
   public String getAccessibleDescription() {
      return this._description;
   }

   @Override
   public AccessibleText getAccessibleText() {
      return this._text;
   }

   @Override
   public AccessibleValue getAccessibleValue() {
      return null;
   }

   @Override
   public int getAccessibleStateSet() {
      return this._state;
   }

   @Override
   public boolean isAccessibleStateSet(int state) {
      return (this._state & state) != 0;
   }

   @Override
   public int getAccessibleRole() {
      return this._roll;
   }

   @Override
   public AccessibleContext getAccessibleParent() {
      return this._parent;
   }

   @Override
   public int getAccessibleChildCount() {
      return 0;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      return null;
   }

   @Override
   public String getAccessibleIconDescription() {
      return null;
   }

   @Override
   public int getAccessibleSelectionCount() {
      return 0;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      return null;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return false;
   }
}
