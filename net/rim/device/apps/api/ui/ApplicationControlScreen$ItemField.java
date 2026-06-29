package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.ContextMenu;

final class ApplicationControlScreen$ItemField extends BoldObjectChoiceField {
   ApplicationControlScreen$ItemGroup _ig;
   String[] _choices;
   int _type;
   int _allowFlag;
   int _promptFlag;
   static final int GROUP;
   static final int BINARY;
   static final int TERNARY;

   public ApplicationControlScreen$ItemField(String label, String[] choices) {
      super(label, choices, new boolean[choices.length], 0, 0);
      this._choices = choices;
      this._type = 1;
   }

   public ApplicationControlScreen$ItemField(String label, String[] choices, int flag) {
      this(label, choices, new boolean[choices.length], flag);
   }

   public ApplicationControlScreen$ItemField(String label, String[] choices, int allowFlag, int promptFlag) {
      this(label, choices, new boolean[choices.length], allowFlag, promptFlag);
   }

   public ApplicationControlScreen$ItemField(String label, String[] choices, boolean[] bolded, int flag) {
      super(label, choices, bolded, 0, 0);
      this._choices = choices;
      this._type = 2;
      this._allowFlag = flag;
   }

   public ApplicationControlScreen$ItemField(String label, String[] choices, boolean[] bolded, int allowFlag, int promptFlag) {
      super(label, choices, bolded, 0, 0);
      this._choices = choices;
      this._type = 3;
      this._allowFlag = allowFlag;
      this._promptFlag = promptFlag;
   }

   @Override
   public final void setChoices(Object[] choices, boolean[] boldChoices) {
      super.setChoices(choices, boldChoices);
      this._choices = choices;
   }

   public final void setChangeListener(ApplicationControlScreen$ItemGroup ig) {
      this._ig = ig;
      super.setChangeListener(this._ig);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key != ' ' || this._ig == null || this._ig.getLeafFieldWithFocus() != this._ig._group) {
         return super.keyChar(key, status, time);
      } else if (this._ig._expanded) {
         this._ig.hideItems();
         return true;
      } else {
         this._ig.showItems();
         return true;
      }
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (this._type == 1) {
         contextMenu.addItem(new ApplicationControlScreen$GroupMenuItem(this._ig._expanded));
      }
   }

   @Override
   public final String toString() {
      return this.getLabel();
   }
}
