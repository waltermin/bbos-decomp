package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.i18n.CommonResource;

final class SpellCheckOptionsItem$CustomDictScreen extends KeywordFilteredScreen implements ListFieldCallback {
   protected Object _context;
   private Verb _newCustomDictUnitVerb;
   private Verb _saveCustomDictVerb;
   private Verb _clearCustomDictVerb;
   private SpellCheckOptionsItem$TransactedCustomDictionary _transactedCustomDict;

   final void showScreen() {
      UiApplication.getUiApplication().pushModalScreen(this);
   }

   public final void setFocusToElement(Object element) {
      if (element != null && !this.getListField().isEmpty()) {
         this.getListField().setFocus();
         this.setElementWithFocus(element);
      }
   }

   public final Object getSelectedEntry() {
      return this.getSelectedElement();
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      CollectionListField collectionListField = (CollectionListField)listField;
      return collectionListField.getElementAt(index);
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField collectionListField = (CollectionListField)listField;
      Object element = collectionListField.getElementAt(index);
      if (element != null) {
         if (element instanceof String) {
            graphics.drawText((String)element, 0, ((String)element).length(), 0, y, 70, width - 2);
            return;
         }
      } else if (index == this.getListField().getExtraRowCount()) {
         graphics.drawText(SpellCheckOptionsItem.getResource(311), 0, y, 4, width);
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         String pattern = this.getSearchPattern();
         if (pattern != null && pattern.length() > 0) {
            this.setSearchPattern(null);
            return true;
         } else {
            return false;
         }
      } else {
         if ((!this.isSearchStringEmpty() || key != '\b') && key != 127) {
            if (key == '\n') {
               this.invokeEditOrNewAction();
               return true;
            }
         } else {
            this.getKeywordFilterList().waitForComplete();
            Object entry = this.getSelectedEntry();
            if (entry != null) {
               CustomDictUnitModel model = new CustomDictUnitModel(entry, this._transactedCustomDict);
               Verb verb = new DeleteCustomDictUnitVerb(model);
               verb.invoke(this._context);
               return true;
            }
         }

         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               this.invokeEditOrNewAction();
               return true;
         }
      }

      return handled;
   }

   private final void invokeEditOrNewAction() {
      this.getKeywordFilterList().waitForComplete();
      Object entry = this.getSelectedEntry();
      Verb verb = null;
      if (entry != null) {
         CustomDictUnitModel model = new CustomDictUnitModel(entry, this._transactedCustomDict);
         verb = new EditCustomDictUnitVerb(model);
      } else {
         verb = new SpellCheckOptionsItem$NewCustomDictUnitVerb(null, this._transactedCustomDict);
      }

      Object result = verb.invoke(this._context);
      this.setFocusToElement(result);
   }

   @Override
   public final void close() {
      if (!this._transactedCustomDict.isDirty()) {
         super.close();
         SpellCheckOptionsItem.access$102(null);
      } else {
         switch (Dialog.ask(1, CommonResource.getString(10003))) {
            case 1:
            default:
               super.close();
               this._transactedCustomDict.commit();
               SpellCheckOptionsItem.access$102(null);
               return;
            case 2:
               super.close();
               this._transactedCustomDict.rollback();
               SpellCheckOptionsItem.access$102(null);
            case 0:
         }
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this._newCustomDictUnitVerb == null) {
         this._newCustomDictUnitVerb = new SpellCheckOptionsItem$NewCustomDictUnitVerb(CommonResource.getString(13), this._transactedCustomDict);
      }

      menu.add(this._newCustomDictUnitVerb);
      Object entry = this.getSelectedEntry();
      if (entry != null) {
         CustomDictUnitModel model = new CustomDictUnitModel(entry, this._transactedCustomDict);
         Verb[] verbs = new Verb[0];
         Verb defaultVerb = model.getVerbs(this._context, verbs);
         menu.add(verbs);
         menu.setDefault(defaultVerb);
      } else {
         menu.setDefault(this._newCustomDictUnitVerb);
      }

      if (this._transactedCustomDict.isDirty()) {
         if (this._saveCustomDictVerb == null) {
            this._saveCustomDictVerb = new SpellCheckOptionsItem$CustomDictSaveVerb(this._transactedCustomDict);
         }

         menu.add(this._saveCustomDictVerb);
      }

      if (this._transactedCustomDict.size() > 0) {
         if (this._clearCustomDictVerb == null) {
            this._clearCustomDictVerb = new SpellCheckOptionsItem$ClearCustomDictVerb(this._transactedCustomDict);
         }

         menu.add(this._clearCustomDictVerb);
      }
   }

   public SpellCheckOptionsItem$CustomDictScreen(
      String title, KeywordFilterList keywordFilterList, SpellCheckOptionsItem$TransactedCustomDictionary transactedCustomDict
   ) {
      super(title, null, null, false);
      this._transactedCustomDict = transactedCustomDict;
      this.setListCallback(this);
      this.setList(keywordFilterList);
      this.setDisplayUpperCaseCharsInSearchText(false);
      this.setAllowSpacesInSearchText(false);
      this._context = new ContextObject(3, 5, 2);
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
      if (verb == this._newCustomDictUnitVerb
         || verb == this._saveCustomDictVerb
         || verb == this._clearCustomDictVerb
         || verb instanceof EditCustomDictUnitVerb) {
         this.setFocusToElement(result);
      }
   }
}
