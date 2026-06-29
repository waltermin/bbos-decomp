package net.rim.device.apps.api.addressbook;

import net.rim.device.api.collection.FilterStatusListener;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ComboField;
import net.rim.device.api.ui.component.ListField;
import net.rim.tid.im.conv.jp.util.KanaConversionUtils;

public class AddressBookComboField extends ComboField implements FilterStatusListener {
   protected KeywordFilterList _filteredList;
   protected long _sortOrder = 1232448844688687736L;
   protected boolean _hasFocus;
   protected AddressBookComboField$SureTypeVariants _stVariants = new AddressBookComboField$SureTypeVariants(this);

   public AddressBookComboField(String label) {
      this(label, 255);
   }

   public AddressBookComboField(String label, int maxNumChars) {
      AddressBook addrBook = AddressBookServices.getAddressBook();
      AddressBookOptions options = AddressBookServices.getAddressBookOptions();
      if (options != null) {
         this._sortOrder = options.getSortOrder();
      }

      if (addrBook != null) {
         this._filteredList = addrBook.getView(this._sortOrder);
      }

      this.setList((ListField)(new Object()));
      this.setEditable(new AddressBookComboField$AddressBookEditable(this, label, maxNumChars));
      this.setController(new AddressBookComboField$AddressBookController(this));
      this.getList().setCallback(new AddressBookComboField$AddressBookCallback(this));
   }

   protected void addressSelected(Object selected, int type) {
      this.hideDropList();
   }

   protected void editEnter() {
   }

   protected void editFocusGained() {
   }

   protected void editFocusLost() {
   }

   private String filterUncomposedText(String text) {
      BasicEditField bef = this.getEditable();
      if (bef.getComposedTextLength() == 0) {
         return text;
      }

      int prefixSize = bef.getConvertedCharactersCount() + bef.getComposedTextStart() - bef.getLabelLength();
      return text.substring(0, prefixSize);
   }

   private void initiateSearch(String searchText) {
      if (!this.usingSureType()) {
         if (this.getInputContext().getActiveInputMethodID() == 512) {
            searchText = KanaConversionUtils.composeAdjustedSearchPatternForJapanese(searchText);
         } else {
            searchText = this.filterUncomposedText(searchText);
         }

         this._filteredList.setCriteria(searchText, this);
      } else {
         int cursorPosition = this.getEditable().getCursorPosition();
         if (cursorPosition != -1 && cursorPosition < searchText.length()) {
            this._stVariants.ignoreVariants();
         }

         this._stVariants.update(searchText);
         String[][][] variantSet = this._stVariants.getVariantSet();
         if (variantSet != null && variantSet.length != 0) {
            this._filteredList.setCriteria(variantSet, this);
         } else {
            this._filteredList.setCriteria("", this);
         }
      }
   }

   protected void releaseSureTypeVariantsMemory() {
      this._stVariants.ignoreVariants();
   }

   @Override
   public void filterStarted() {
   }

   @Override
   public void filterDone(boolean interrupted) {
      if (!interrupted) {
         if (this._hasFocus) {
            Application.getApplication().invokeLater(new AddressBookComboField$1(this));
         }
      }
   }
}
