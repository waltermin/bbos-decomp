package net.rim.device.api.ui.component;

public class ObjectChoiceField extends ChoiceField {
   private Object[] _choices;

   public ObjectChoiceField() {
      this(null, null, 0, 0);
   }

   public ObjectChoiceField(String label, Object[] choices) {
      this(label, choices, 0, 0);
   }

   public ObjectChoiceField(String label, Object[] choices, int initialIndex) {
      this(label, choices, initialIndex, 0);
   }

   public ObjectChoiceField(String label, Object[] choices, int initialIndex, long style) {
      super(label, choices != null ? choices.length : 0, initialIndex, style);
      int length = choices != null ? choices.length : 0;
      this._choices = new Object[length];
      if (length != 0) {
         System.arraycopy(choices, 0, this._choices, 0, length);
      }
   }

   public ObjectChoiceField(String label, Object[] choices, Object initialObject) {
      super(label, choices.length, 0);
      this.setChoices(choices);
      this.setSelectedIndex(initialObject);
   }

   @Override
   public Object getChoice(int index) {
      if (this._choices.length == 0 && index == 0) {
         return null;
      } else if (index >= 0 && this._choices.length > index) {
         return this._choices[index];
      } else {
         throw new IllegalArgumentException(Integer.toString(index));
      }
   }

   public void setChoices(Object[] choices) {
      int length = choices != null ? choices.length : 0;
      this._choices = new Object[length];
      if (length != 0) {
         System.arraycopy(choices, 0, this._choices, 0, length);
      }

      this.setSize(length);
      super._selectedWidth = 0;
   }
}
