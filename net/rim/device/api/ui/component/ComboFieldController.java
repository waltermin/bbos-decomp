package net.rim.device.api.ui.component;

public class ComboFieldController {
   protected ComboField _comboField;
   public static final int SELECT_TRACKWHEEL_CLICK;
   public static final int SELECT_TRACKBALL_CLICK;
   public static final int SELECT_ENTER;

   protected void setComboField(ComboField comboField) {
      this._comboField = comboField;
   }

   protected void textChanged(String newText, int context) {
   }

   protected void select(Object selection, int type) {
   }

   protected void escape() {
   }
}
