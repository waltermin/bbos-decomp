package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.CommonResources;

public final class ContactFindField extends BasicEditField {
   private String _findString;
   private String _baseText;
   private boolean _preventCall;
   private String _searchPattern;
   private XYRect _focusRect;
   private boolean _drawFocusIndicator;
   private boolean _showCaretOnEmptySearch;

   public ContactFindField() {
      this(null, null, true);
   }

   public ContactFindField(String text, String findLabel, boolean showCaretOnEmptySearch) {
      super(1188950302162698240L);
      this._findString = findLabel != null ? findLabel : CommonResources.getString(700);
      this._baseText = text == null ? this._findString : text;
      this._focusRect = new XYRect();
      this._showCaretOnEmptySearch = StringUtilities.strEqual(this._baseText, this._findString) ? true : showCaretOnEmptySearch;
      this._drawFocusIndicator = showCaretOnEmptySearch;
      super.setLabel(this._baseText);
   }

   public final boolean hasSearchPattern() {
      return this._searchPattern != null && this._searchPattern.length() != 0;
   }

   @Override
   public final boolean isDirty() {
      return false;
   }

   protected final void updateText() {
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

   public final void resetSearch() {
      this._searchPattern = null;
   }

   private final void setSearchPattern(String newPattern, boolean forceUpdate) {
      if (newPattern != null && newPattern.trim().length() == 0) {
         newPattern = null;
      }

      boolean oldInMoveFocus = this.isInMoveFocus();
      this.inMoveFocus(true);
      this._searchPattern = newPattern;
      if (newPattern != null && !forceUpdate) {
         this._drawFocusIndicator = true;
      } else {
         this.updateText();
      }

      this.inMoveFocus(oldInMoveFocus);
   }

   public final String getSearchPattern() {
      return this._searchPattern;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      String newSearchPattern = this.getSearchPattern();
      int newPatternLength = newSearchPattern != null ? newSearchPattern.length() : 0;
      if (!this._preventCall && !super.getLabel().equals(this._findString)) {
         super.setLabel(this._findString);
      }

      switch (key) {
         case '\n':
         case '\u007f':
            return false;
         case '\u001b':
            this.resetSearch();
            this.setText("");
            return true;
         case ' ':
            if (newPatternLength == 0 || newSearchPattern.charAt(newPatternLength - 1) == ' ') {
               break;
            }
         case '\b':
            if (super.getCommittedTextLength() == 0) {
               this.resetSearch();
            }

            if (this._searchPattern != null && this._searchPattern.length() > 0) {
               this._searchPattern = this._searchPattern.substring(0, this._searchPattern.length() - 1);
               super.keyChar(key, status, time);
            }
            break;
         default:
            if (newPatternLength == 0 || newSearchPattern.charAt(newPatternLength - 1) == ' ') {
               key = Character.toUpperCase(key);
            }

            super.keyChar(key, status, time);
      }

      if (key != 17 && key != 19) {
         this.performSearch(super.getText(super.getLabelLength(), super.getCommittedTextLength()));
         return true;
      } else {
         return false;
      }
   }

   private final void performSearch(String newSearchPattern) {
      if (newSearchPattern.length() == 0) {
         newSearchPattern = null;
      }

      String searchPattern = this.getSearchPattern();
      if (searchPattern == null && newSearchPattern != null || searchPattern != null && !searchPattern.equals(newSearchPattern)) {
         this.setSearchPattern(newSearchPattern, false);
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      super.paint(graphics);
      if (this._drawFocusIndicator) {
         this.getFocusRect(this._focusRect);
         this.drawFocus(graphics, true);
      }
   }
}
