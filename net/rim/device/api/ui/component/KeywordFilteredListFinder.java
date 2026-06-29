package net.rim.device.api.ui.component;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.im.conv.jp.util.KanaConversionUtils;
import net.rim.tid.text.AttributedString;
import net.rim.vm.TraceBack;

public class KeywordFilteredListFinder extends BasicEditField {
   private boolean _displayUpperCaseCharsInSearchText = true;
   private boolean _allowSpacesInSearchText = true;
   private boolean _acceptYomiSearch = false;
   private String _findString;
   private String _baseText;
   protected boolean _preventCall;
   private String _searchPattern;
   private KeywordFilterCollectionListField _listField;
   private XYRect _focusRect;
   private boolean _drawFocusIndicator;
   private boolean _showCaretOnEmptySearch;

   public KeywordFilteredListFinder() {
   }

   public KeywordFilteredListFinder(long style) {
      super(style);
   }

   public KeywordFilteredListFinder(String text) {
      this(text, null, true);
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
   }

   public KeywordFilteredListFinder(String text, long complementarySearchFieldStyle) {
      this(text, null, true, complementarySearchFieldStyle);
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
   }

   public KeywordFilteredListFinder(String text, String findLabel, boolean showCaretOnEmptySearch) {
      this(text, findLabel, showCaretOnEmptySearch, 0);
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
   }

   public KeywordFilteredListFinder(String text, String findLabel, boolean showCaretOnEmptySearch, long complementarySearchFieldStyle) {
      super(1188950302162698240L | complementarySearchFieldStyle);
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (findLabel != null) {
         this._findString = findLabel;
      } else {
         this._findString = CommonResource.getString(10171);
      }

      this._preventCall = false;
      this._baseText = text == null ? this._findString : text;
      this._focusRect = new XYRect();
      this._showCaretOnEmptySearch = StringUtilities.strEqual(this._baseText, this._findString) ? true : showCaretOnEmptySearch;
      this._drawFocusIndicator = showCaretOnEmptySearch;
      super.setLabel(this._baseText);
   }

   private String composeLanguageAdjustedPattern() {
      if (this._acceptYomiSearch && this.getInputContext().getActiveInputMethodID() == 512) {
         StringBuffer compositeSearchPattern = new StringBuffer();
         AttributedString attributedString = this.getAttributedText();
         int composedTextEnd = this.getComposedTextEnd();
         boolean hasHanSymbols = false;

         for (int i = this.getLabelLength(); i < composedTextEnd; i++) {
            char ch = attributedString.charAt(i);
            hasHanSymbols |= CharacterUtilities.isHan(ch);
            compositeSearchPattern.append(ch);
         }

         if (hasHanSymbols) {
            int lengthBefore = compositeSearchPattern.length();
            int converted = KanaConversionUtils.kanaToHalfWidth(compositeSearchPattern, 0, compositeSearchPattern.length(), compositeSearchPattern);
            if (converted != lengthBefore) {
               compositeSearchPattern.setLength(lengthBefore);
            } else {
               compositeSearchPattern.delete(0, lengthBefore);
            }
         }

         return compositeSearchPattern.toString();
      } else {
         return null;
      }
   }

   private String composeSearchPattern(InputMethodEvent event) {
      String langPattern = this.composeLanguageAdjustedPattern();
      if (langPattern != null) {
         return langPattern;
      }

      int labelLen = super.getLabelLength();
      return super.getText(labelLen, this.getComposedTextStart() - labelLen + event.getConvertedCharacterCount());
   }

   @Override
   public void focusChangeNotify(int action) {
      boolean value = this._preventCall;
      this._preventCall = true;
      super.focusChangeNotify(action);
      this._preventCall = value;
   }

   public boolean getFocusIndicatorEnabled() {
      return this._drawFocusIndicator;
   }

   private KeywordFilterList getKeywordFilterList() {
      return this._listField.getKeywordFilterList();
   }

   public String getSearchPattern() {
      return super.getText(super.getLabelLength(), super.getCursorPosition());
   }

   private void initiateSearch(String pattern) {
      this._listField.initiateSearch(pattern);
   }

   @Override
   public int inputMethodTextChanged(InputMethodEvent event) {
      if (!this._preventCall && event.getText().length() > 0 && !super.getLabel().equals(this._findString)) {
         super.setLabel(this._findString);
      }

      int result = super.inputMethodTextChanged(event);
      if (!this._preventCall) {
         String sPattern = this.composeSearchPattern(event);
         if (sPattern.length() != 0 || sPattern.length() == 0 && !this.isComposedTextExist()) {
            SLControlObject controlObject = (SLControlObject)this.getInputContext().getInputMethodControlObject();
            if (this.getInputContext().getActiveInputMethodID() == 4096 && controlObject.getInputMode() != 2) {
               this._searchPattern = sPattern;
            } else {
               this.performSearch(sPattern);
            }
         }

         if (super.getTextLength() == 0 && this._baseText != null && !super.getLabel().equals(this._baseText)) {
            super.setLabel(this._baseText);
         }
      }

      return result;
   }

   @Override
   public boolean isDirty() {
      return false;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      String newSearchPattern = this._searchPattern;
      int newPatternLength = newSearchPattern != null ? newSearchPattern.length() : 0;
      switch (key) {
         case '\n':
         case '\u001b':
         case '\u007f':
            return false;
         case ' ':
         case '　':
            if (!this._allowSpacesInSearchText || newPatternLength == 0 || CharacterUtilities.isSpaceChar(newSearchPattern.charAt(newPatternLength - 1))) {
               break;
            }
         case '\b':
            if (super.getCommittedTextLength() == 0) {
               this.resetSearch();
               break;
            }
         default:
            if (!this._preventCall && key != '\b' && !super.getLabel().equals(this._findString)) {
               super.setLabel(this._findString);
            }

            if (this._displayUpperCaseCharsInSearchText) {
               if (newPatternLength == 0 || newSearchPattern.charAt(newPatternLength - 1) == ' ') {
                  key = Character.toUpperCase(key);
               }
            } else {
               key = Character.toLowerCase(key);
            }

            if (key != '\b') {
               this._listField.setElementWithFocus(null);
            }

            super.keyChar(key, status, time);
      }

      if (key != 17 && key != 19) {
         String langAbjPattern = this.composeLanguageAdjustedPattern();
         if (langAbjPattern != null) {
            this.performSearch(langAbjPattern);
            return true;
         } else {
            this.performSearch(super.getText(super.getLabelLength(), super.getCommittedTextLength()));
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyControl(char key, int status, int time) {
      return key == 128 ? super.keyControl(key, status, time) : false;
   }

   public void linkToField(KeywordFilterCollectionListField field) {
      this._listField = field;
      field.setInputProcessor(this);
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      return amount;
   }

   @Override
   protected void paint(Graphics graphics) {
      super.paint(graphics);
      if (this._drawFocusIndicator) {
         this.getFocusRect(this._focusRect);
         this.drawFocus(graphics, true);
      }
   }

   private void performSearch(String newSearchPattern) {
      if (newSearchPattern.length() == 0) {
         newSearchPattern = null;
      }

      if (!StringUtilities.strEqual(this._searchPattern, newSearchPattern)) {
         this.setSearchPattern(newSearchPattern, false);
      }
   }

   public void redoSearch(boolean force) {
      if (this.getKeywordFilterList() != null) {
         if (!force) {
            Object currentCriteria = this.getKeywordFilterList().getCriteria();
            if (!(currentCriteria instanceof String)) {
               if (currentCriteria != null) {
                  force = true;
               }
            } else if (!StringUtilities.strEqual((String)currentCriteria, this._searchPattern)) {
               force = true;
            }
         }

         if (force) {
            this.initiateSearch(this._searchPattern);
         }
      }
   }

   public void resetSearch() {
      this._searchPattern = null;
      this.redoSearch(false);
      this.setText(null);
   }

   public void setAllowSpacesInSearchText(boolean allowSpacesInSearchText) {
      this._allowSpacesInSearchText = allowSpacesInSearchText;
      if (!this._allowSpacesInSearchText && this._searchPattern != null) {
         this._searchPattern = this._searchPattern.replace(' ', '_');
      }
   }

   public void acceptYomiSearch(boolean acceptYomiSearch) {
      this._acceptYomiSearch = acceptYomiSearch;
   }

   public void setBaseText(String baseText) {
      boolean sameText = StringUtilities.strEqual(this._baseText, baseText);
      this._baseText = baseText;
      if (!sameText) {
         this.updateText();
      }
   }

   public void setDisplayUpperCaseCharsInSearchText(boolean displayUpperCaseCharsInSearchText) {
      this._displayUpperCaseCharsInSearchText = displayUpperCaseCharsInSearchText;
      if (!this._displayUpperCaseCharsInSearchText && this._searchPattern != null) {
         this._searchPattern = this._searchPattern.toLowerCase();
      }
   }

   public void setFocusIndicatorEnabled(boolean enabled) {
      this._drawFocusIndicator = enabled;
   }

   public void setSearchPattern(String newPattern) {
      this.setSearchPattern(newPattern, true);
   }

   private void setSearchPattern(String newPattern, boolean forceUpdate) {
      if (newPattern != null && newPattern.trim().length() == 0) {
         newPattern = null;
      }

      if (this._listField.getKeywordFilterList() != null) {
         boolean oldInMoveFocus = this.isInMoveFocus();
         this.inMoveFocus(true);
         this._searchPattern = newPattern;
         if (newPattern != null && !forceUpdate) {
            this._drawFocusIndicator = true;
         } else {
            this.updateText();
         }

         this.inMoveFocus(oldInMoveFocus);
         this.initiateSearch(this._searchPattern);
      }
   }

   @Override
   protected void updateInputStyle() {
      super.updateInputStyle();
      SLControlObject controlObject = (SLControlObject)this.getInputContext().getInputMethodControlObject();
      KeywordFilterList list = this._listField.getKeywordFilterList();
      if (list != null) {
         controlObject.actionPerformed(38, list.getSearcher());
      }
   }

   private void updateText() {
      this._preventCall = true;
      if (this._searchPattern == null) {
         if (this._baseText == null) {
            super.setLabel(this._findString);
         } else {
            super.setLabel(this._baseText);
         }

         super.setText("");
         this._drawFocusIndicator = this._showCaretOnEmptySearch;
      } else {
         super.setLabel(this._findString);
         super.setText(this._searchPattern);
         this._drawFocusIndicator = true;
      }

      this._preventCall = false;
   }
}
