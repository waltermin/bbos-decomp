package net.rim.device.apps.internal.options;

import net.rim.device.api.collection.util.PrefixKeywordFilterList;
import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.im.customWordRepository.ja.JapaneseCustomWord;

class CustomWordlistScreen$CustomDictScreen extends KeywordFilteredScreen implements ListFieldCallback {
   protected Object _context;
   private Verb _newCustomDictUnitVerb;
   protected Verb _clearCustomDictItem;
   String _newItemValue;
   Locale _inputLocale;
   private final CustomWordlistScreen this$0;

   void showScreen() {
      UiApplication.getUiApplication().pushModalScreen(this);
      InputContext.getInstance().setIMSwitchEnabled(false);
   }

   public void setFocusToElement(Object element) {
      if (element != null && !this.getListField().isEmpty()) {
         this.getListField().setFocus();
         this.setElementWithFocus(element);
      }
   }

   public Object getSelectedEntry() {
      return this.getSelectedElement();
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      CollectionListField collectionListField = (CollectionListField)listField;
      return collectionListField.getElementAt(index);
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField collectionListField = (CollectionListField)listField;
      Object element = collectionListField.getElementAt(index);
      if (element != null) {
         if (element instanceof String) {
            graphics.drawText((String)element, 0, ((String)element).length(), 0, y, 70, width - 2);
         } else {
            String str;
            if (!(element instanceof JapaneseCustomWord)) {
               str = element.toString();
            } else {
               JapaneseCustomWord word = (JapaneseCustomWord)element;
               str = word.getCandidate() + " 〚" + word.getReading() + "〛";
            }

            graphics.drawText(str, 0, str.length(), 0, y, 70, width - 2);
         }
      } else {
         if (index == this.getListField().getExtraRowCount()) {
            if (CustomWordlistScreen._screenType == 3) {
               graphics.drawText(OptionsResources.getString(2073), 0, y, 4, width);
               return;
            }

            graphics.drawText(OptionsResources.getString(311), 0, y, 4, width);
         }
      }
   }

   public CustomWordlistScreen$CustomDictScreen(CustomWordlistScreen _1, String title, String newItemValue) {
      super(title, null, null, false);
      this.this$0 = _1;
      this._inputLocale = Locale.getDefaultInputForSystem();
      this.setListCallback(this);
      this.setList(this.getList());
      this.setDisplayUpperCaseCharsInSearchText(false);
      this.setAllowSpacesInSearchText(false);
      this.setHelp("typing");
      this._newItemValue = newItemValue;
      ContextObject context = new ContextObject(3, 5, 2);
      SLControlObject controlObject = (SLControlObject)this.getInputContext().getInputMethodControlObject();
      if (controlObject.getIMStyleAsBoolean(1)) {
         context.setFlag(85);
      }

      this._context = context;
   }

   @Override
   protected void onFocusNotify(boolean focus) {
      super.onFocusNotify(focus);
      if (focus && !this._inputLocale.equals(Locale.getDefaultInputForSystem())) {
         this._inputLocale = Locale.getDefaultInputForSystem();
         CustomWordlistScreen.access$002(null);
         this.setList(this.getList());
         this.setSearchPattern(null);
         ((SLControlObject)InputContext.getInstance().getInputMethodControlObject()).actionPerformed(38, this.getKeywordFilterList().getSearcher());
      }
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      if (key == '\b' && this.getFinderField().getText().length() == 0) {
         this.onDelete();
         return key | 65536;
      } else {
         return super.processKeyEvent(event, key, keycode, time);
      }
   }

   private boolean onDelete() {
      boolean ret = false;
      this.getKeywordFilterList().waitForComplete();
      Object entry = this.getSelectedEntry();
      if (entry != null) {
         CustomDictUnitModel model = new CustomDictUnitModel(entry, CustomWordlistScreen._screenType, CustomWordlistScreen._customDictionary);
         Verb verb = new DeleteCustomDictUnitVerb(model);
         verb.invoke(this._context);
         ret = true;
      }

      return ret;
   }

   @Override
   public boolean keyChar(char key, int status, int time) {
      label23: {
         switch (key) {
            case '\b':
               if (!this.isSearchStringEmpty()) {
                  break;
               }
            case '\u007f':
               if (this.onDelete()) {
                  return true;
               }
            case '\n':
               break label23;
            case '\u001b':
               if (!this.isSearchStringEmpty()) {
                  this.setSearchPattern(null);
                  return true;
               }

               return false;
         }

         return super.keyChar(key, status, time);
      }

      this.invokeEditOrNewAction();
      return true;
   }

   @Override
   protected boolean invokeAction(int action) {
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

   private void invokeEditOrNewAction() {
      this.getKeywordFilterList().waitForComplete();
      Object entry = this.getSelectedEntry();
      Verb verb = null;
      if (entry != null) {
         CustomDictUnitModel model = new CustomDictUnitModel(entry, CustomWordlistScreen._screenType, CustomWordlistScreen._customDictionary);
         verb = new EditCustomDictUnitVerb(model);
      } else if (CustomWordlistScreen._screenType != 2) {
         verb = new CustomWordlistScreen$NewCustomDictUnitVerb(this.this$0, null, null);
      }

      if (verb != null) {
         Object result = verb.invoke(this._context);
         this.setFocusToElement(result);
      }
   }

   @Override
   public void close() {
      super.close();
      this.this$0._customDictScreen = null;
      CustomWordlistScreen.access$002(null);
      if (CustomWordlistScreen._screenType == 2) {
         InputContext.getInstance().setIMSwitchEnabled(true);
      }
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this._newCustomDictUnitVerb == null) {
         this._newCustomDictUnitVerb = new CustomWordlistScreen$NewCustomDictUnitVerb(this.this$0, CommonResource.getString(13), this._newItemValue);
      }

      menu.add(this._newCustomDictUnitVerb);
      Object entry = this.getSelectedEntry();
      if (entry != null) {
         CustomDictUnitModel model = new CustomDictUnitModel(entry, CustomWordlistScreen._screenType, CustomWordlistScreen._customDictionary);
         Verb[] verbs = new Verb[0];
         Verb defaultVerb = model.getVerbs(this._context, verbs);
         menu.add(verbs);
         menu.setDefault(defaultVerb);
      } else {
         menu.setDefault(this._newCustomDictUnitVerb);
      }

      if (this._clearCustomDictItem == null) {
         this._clearCustomDictItem = new CustomWordlistScreen$ClearCustomDictVerb(this.this$0._rbFamily, this.this$0._rbClrLearningCacheKey);
      }

      if (CustomWordlistScreen._customDictionary.size() > 0 || InputContext.getInstance().getActiveInputMethodID() == 16384) {
         menu.add(this._clearCustomDictItem);
      }
   }

   private PrefixKeywordFilterList getList() {
      SortedReadableList sortedList = new SortedReadableList(CustomWordlistScreen.getCustomDictionary(), new CustomWordlistScreen$CustomDictComparator());
      return new PrefixKeywordFilterList(sortedList, new CustomWordlistScreen$CustomDictIndexHelper());
   }

   @Override
   protected void verbInvoked(Verb verb, Object context, Object result) {
      if (verb != this._newCustomDictUnitVerb && !(verb instanceof EditCustomDictUnitVerb)) {
         if (verb == this._clearCustomDictItem && result != null) {
            this.setList(this.getList());
         }
      } else {
         this.setFocusToElement(result);
      }
   }
}
