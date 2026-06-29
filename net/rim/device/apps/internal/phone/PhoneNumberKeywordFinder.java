package net.rim.device.apps.internal.phone;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.collection.util.KeywordSearcher;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.KeywordFilteredListFinder;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.text.AttributedString;
import net.rim.vm.Array;

final class PhoneNumberKeywordFinder extends KeywordFilteredListFinder {
   PhoneAppScreen _pas;
   KeywordFilterList _filterList = null;
   KeywordFilterCollectionListField _list = null;
   String[][] _variants = new String[0][];

   public PhoneNumberKeywordFinder(PhoneAppScreen pas) {
      super("", CommonResources.getString(700), false, 8192);
      this.acceptYomiSearch(true);
      this._pas = pas;
   }

   @Override
   protected final void applyTheme() {
   }

   @Override
   protected final boolean isSymbolScreenAllowed() {
      return false;
   }

   @Override
   public final String getText() {
      String text = super.getText();
      StringBuffer sb = new StringBuffer();
      boolean makeUpperCase = true;

      for (int i = 0; i < text.length(); i++) {
         if (makeUpperCase) {
            sb.append(CharacterUtilities.toUpperCase(text.charAt(i)));
            makeUpperCase = false;
         } else {
            sb.append(text.charAt(i));
         }

         if (CharacterUtilities.isSpaceChar(text.charAt(i))) {
            makeUpperCase = true;
         }
      }

      return sb.toString();
   }

   @Override
   public final void dispatchEvent(Event rEvent) {
      if (rEvent.getID() != 514) {
         super.dispatchEvent(rEvent);
         if (rEvent.getID() == 516) {
            this._pas.updateFields();
         }
      }
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      return event == 513 ? 65536 : super.processKeyEvent(event, key, keycode, time);
   }

   @Override
   public final boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      return false;
   }

   @Override
   public final void linkToField(KeywordFilterCollectionListField field) {
      super.linkToField(field);
      this._list = field;
      this._filterList = field.getKeywordFilterList();
   }

   private final boolean validAltChar(char ch) {
      return CharacterUtilities.isDigit(ch) || ch == '\'';
   }

   private final void runSearch(char key) {
      if (this._filterList != null) {
         Array.resize(this._variants, this.getTextLength());
         if (this._variants.length != 0) {
            char altKey = Keypad.getAltedChar(key);
            String[] newVariant = null;
            if (!CharacterUtilities.isLetter(key) && !CharacterUtilities.isSpaceChar(key) && key != '0') {
               if (key == 127 || key == '\b') {
                  newVariant = this._variants[this._variants.length - 1];
               }
            } else {
               int index = this._variants.length - 2;
               String[] lastVariant = new String[]{""};
               if (index >= 0) {
                  lastVariant = this._variants[index];
               }

               if (this.validAltChar(altKey) && key != altKey) {
                  newVariant = new String[lastVariant.length * 2];

                  for (int i = 0; i < lastVariant.length; i++) {
                     newVariant[i] = lastVariant[i] + key;
                     newVariant[lastVariant.length + i] = lastVariant[i] + altKey;
                  }
               } else {
                  newVariant = new String[lastVariant.length];

                  for (int i = 0; i < lastVariant.length; i++) {
                     newVariant[i] = lastVariant[i] + key;
                  }
               }
            }

            if (newVariant != null) {
               if (newVariant.length > 1) {
                  KeywordSearcher searcher = this._filterList.getSearcher();
                  BitSet results = searcher.searchPrefixes(newVariant);
                  String[] trimmedVariant = new String[0];

                  for (int i = 0; i < newVariant.length; i++) {
                     if (results.isSet(i)) {
                        Array.resize(trimmedVariant, trimmedVariant.length + 1);
                        trimmedVariant[trimmedVariant.length - 1] = newVariant[i];
                     }
                  }

                  this._variants[this._variants.length - 1] = trimmedVariant;
                  this._filterList.setCriteria(trimmedVariant, this._list);
               } else {
                  this._variants[this._variants.length - 1] = newVariant;
               }
            }
         }
      }
   }

   @Override
   public final int inputMethodTextChanged(InputMethodEvent event) {
      if (InputContext.getInstance().getActiveInputMethodID() == 1) {
         super._preventCall = true;
      }

      int result = super.inputMethodTextChanged(event);
      if (InputContext.getInstance().getActiveInputMethodID() == 1) {
         super.setLabel(CommonResources.getString(700));
         if (event.getText() != null && event.getText().length() > 0) {
            char key = event.getText().charAt(0);
            this.runSearch(key);
            if (this.validAltChar(Keypad.getAltedChar(key))) {
               String[] currentVariant = this._variants[this._variants.length - 1];
               if (currentVariant != null && currentVariant.length > 0 && currentVariant[0] != null) {
                  this.setText(currentVariant[0]);
               }
            }
         }
      }

      AttributedString attribString = this.getAttributedText();
      attribString.setAttribToZero(0, attribString.length(), 67895296);
      return result;
   }
}
