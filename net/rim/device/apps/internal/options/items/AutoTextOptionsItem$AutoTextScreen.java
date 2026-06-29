package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class AutoTextOptionsItem$AutoTextScreen extends KeywordFilteredScreen implements ListFieldCallback {
   private final AutoTextOptionsItem this$0;

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
      if (element == null) {
         if (index == this.getListField().getExtraRowCount()) {
            graphics.drawText(OptionsResources.getString(311), 0, y, 4, width);
         }
      } else {
         int replacementFlags = AutoTextOptionsItem._autoTextEngine.getReplacementCase(element);
         int caseStringResourceId = 300;
         if (replacementFlags == 1) {
            caseStringResourceId = 301;
         }

         String casingString = OptionsResources.getString(caseStringResourceId);
         int widthDrawn = graphics.drawText(casingString, 0, y, 5, width);
         StringBuffer itemStringBuffer = new StringBuffer(AutoTextOptionsItem._autoTextEngine.getReplacedString(element));
         itemStringBuffer.append(' ');
         itemStringBuffer.append('(');
         itemStringBuffer.append(AutoTextOptionsItem._autoTextEngine.getReplacementStringPattern(element));
         itemStringBuffer.append(')');
         graphics.drawText(itemStringBuffer, 0, itemStringBuffer.length(), 0, y, 70, width - widthDrawn - 2);
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      label23: {
         switch (key) {
            case '\b':
               if (!this.isSearchStringEmpty()) {
                  break;
               }
            case '\u007f':
               this.getKeywordFilterList().waitForComplete();
               Object entry = this.this$0._autoTextScreen.getSelectedEntry();
               if (entry != null) {
                  AutoTextUnitModel model = new AutoTextUnitModel(entry);
                  Verb verb = new DeleteAutoTextUnitVerb(model);
                  verb.invoke(AutoTextOptionsItem.access$200(this.this$0));
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
      Object entry = this.this$0._autoTextScreen.getSelectedEntry();
      Verb verb = null;
      if (entry != null) {
         AutoTextUnitModel model = new AutoTextUnitModel(entry);
         verb = new EditAutoTextUnitVerb(model);
      } else {
         verb = AutoTextOptionsItem._newAutoTextUnitVerb;
      }

      Object result = verb.invoke(AutoTextOptionsItem.access$400(this.this$0));
      this.this$0._autoTextScreen.setFocusToElement(result);
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      menu.add(AutoTextOptionsItem._newAutoTextUnitVerb);
      VerbRepository verbRepository = VerbRepository.getVerbRepository(-7458124633526034381L);
      Verb[] verbs = verbRepository.getVerbs(null);
      menu.add(verbs);
      Object entry = this.this$0._autoTextScreen.getSelectedEntry();
      if (entry != null) {
         AutoTextUnitModel model = new AutoTextUnitModel(entry);
         verbs = new Verb[0];
         Verb defaultVerb = model.getVerbs(AutoTextOptionsItem.access$500(this.this$0), verbs);
         menu.add(verbs);
         menu.setDefault(defaultVerb);
      }
   }

   public AutoTextOptionsItem$AutoTextScreen(AutoTextOptionsItem _1, String title, KeywordFilterList keywordFilterList) {
      super(title, null, null, false);
      this.this$0 = _1;
      this.setHelp("typing");
      this.setListCallback(this);
      this.setList(keywordFilterList);
      this.setDisplayUpperCaseCharsInSearchText(false);
      this.setAllowSpacesInSearchText(false);
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
      if (verb == AutoTextOptionsItem._newAutoTextUnitVerb || verb instanceof EditAutoTextUnitVerb) {
         this.setFocusToElement(result);
      }
   }
}
