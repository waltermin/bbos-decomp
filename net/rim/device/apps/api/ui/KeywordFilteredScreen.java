package net.rim.device.apps.api.ui;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.KeywordFilteredListFinder;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;

public class KeywordFilteredScreen extends AppsMainScreen {
   private KeywordFilteredListFinder _finder;
   private KeywordFilterCollectionListField _listField;
   private Field _titleField;

   public KeywordFilteredScreen(String titleString, KeywordFilterList list, ListFieldCallback listCallback) {
      this(titleString, list, listCallback, true, null);
   }

   public KeywordFilteredScreen(String titleString, KeywordFilterList list, ListFieldCallback listCallback, boolean showCaretOnEmptySearch) {
      this(titleString, null, list, listCallback, showCaretOnEmptySearch, null);
   }

   public KeywordFilteredScreen(String titleString, KeywordFilterList list, ListFieldCallback listCallback, boolean showCaretOnEmptySearch, Object context) {
      this(titleString, null, list, listCallback, showCaretOnEmptySearch, context);
   }

   public KeywordFilteredScreen(
      String titleString, String findLabel, KeywordFilterList list, ListFieldCallback listCallback, boolean showCaretOnEmptySearch, Object context
   ) {
      this(titleString, findLabel, (KeywordFilterCollectionListField)(new Object(list, listCallback)), showCaretOnEmptySearch, context);
   }

   protected KeywordFilteredScreen(
      String titleString, String findLabel, KeywordFilterCollectionListField listField, boolean showCaretOnEmptySearch, Object context
   ) {
      this(titleString, findLabel, listField, showCaretOnEmptySearch, context, 0);
   }

   protected KeywordFilteredScreen(
      String titleString,
      String findLabel,
      KeywordFilterCollectionListField listField,
      boolean showCaretOnEmptySearch,
      Object context,
      long complementarySearchFieldStyle
   ) {
      super(17592186044416L);
      this._finder = (KeywordFilteredListFinder)(new Object(titleString, findLabel, showCaretOnEmptySearch, complementarySearchFieldStyle));
      Object obj = ContextObject.get(context, -7261227923983886841L);
      if (!(obj instanceof Object)) {
         this.setTitleField(this._finder);
      } else {
         Field titleField = (Field)obj;
         VerticalFieldManager vfm = (VerticalFieldManager)(new Object(1152921504606846976L));
         this.setBanner(titleField);
         vfm.add(this._finder);
         this.setTitleField(vfm);
         Manager mgr = vfm.getManager();
         if (mgr != null) {
            mgr.setTag(null);
         }
      }

      this._listField = listField;
      this._finder.linkToField(this._listField);
      this.add(this._listField);
      this._finder.resetSearch();
      this.setVerticalQuantization(-1);
   }

   protected Field getTitleField() {
      return this._titleField;
   }

   protected void setTitleField(Field titleField) {
      this._titleField = titleField;
      this.setTitle(this._titleField);
   }

   @Override
   protected boolean invokeAction(int action) {
      this.getKeywordFilterList().waitForComplete();
      boolean result = false;
      if (action == 1) {
         Object selectedElement = this.getSelectedElement();
         if (selectedElement != null) {
         }
      }

      if (!result) {
         super.invokeAction(action);
      }

      return result;
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (!attached) {
         this.setList(null);
      }

      super.onUiEngineAttached(attached);
   }

   public void setDisplayUpperCaseCharsInSearchText(boolean displayUpperCaseCharsInSearchText) {
      this._finder.setDisplayUpperCaseCharsInSearchText(displayUpperCaseCharsInSearchText);
   }

   public void setAllowSpacesInSearchText(boolean allowSpacesInSearchText) {
      this._finder.setAllowSpacesInSearchText(allowSpacesInSearchText);
   }

   protected CollectionListField getListField() {
      return this._listField;
   }

   public void setEmptyString(String emptyString, int style) {
      this._listField.setEmptyString("", style);
   }

   protected KeywordFilteredListFinder getFinderField() {
      return this._finder;
   }

   public String getFinderFieldText() {
      return this._finder.getText();
   }

   public KeywordFilterList getKeywordFilterList() {
      return this._listField.getKeywordFilterList();
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\u001b':
            if (!this.isSearchStringEmpty()) {
               this.setSearchPattern(null);
               return true;
            }

            this.close();
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   public void setList(KeywordFilterList newList) {
      KeywordFilterList oldList = this.getKeywordFilterList();
      this._listField.setKeywordFilterList(newList);
      this._finder.redoSearch(oldList != newList);
   }

   public void setListCallback(ListFieldCallback callback) {
      this._listField.setCallback(callback);
   }

   protected void setElementWithFocus(Object element) {
      this._listField.setElementWithFocus(element);
   }

   protected Object getSelectedElement() {
      return this._listField.getSelectedElement();
   }

   protected int getSelectedIndex() {
      return this._listField.getSelectedIndex();
   }

   protected void setBaseTitle(String title) {
      this._finder.setBaseText(title);
   }

   public void setSearchPattern(String newPattern) {
      this._finder.setSearchPattern(newPattern);
   }

   public String getSearchPattern() {
      return this._finder.getSearchPattern();
   }

   public boolean isSearchStringEmpty() {
      String searchPattern = this._finder.getSearchPattern();
      return searchPattern == null || searchPattern.length() <= 0;
   }
}
