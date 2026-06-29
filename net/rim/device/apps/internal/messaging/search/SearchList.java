package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

public final class SearchList extends KeywordFilteredScreen implements ListFieldCallback {
   private MessageSearchImpl _search;
   private SearchCollection _searchCollection;
   private boolean _fromRibbon;

   public final void run() {
      UiApplication.getUiApplication().pushModalScreen(this);
   }

   protected final boolean doEnter() {
      this.getKeywordFilterList().waitForComplete();
      Object element = this.getSelectedElement();
      if (!(element instanceof RIMModel)) {
         return false;
      }

      RIMModel model = (RIMModel)element;
      Verb v = new SearchList$SelectSearchVerb(this, model);
      v.invoke(null);
      return true;
   }

   @Override
   public final Object get(ListField listField, int index) {
      CollectionListField clf = (CollectionListField)listField;
      return clf.getElementAt(index);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField clf = (CollectionListField)listField;
      Object element = clf.getElementAt(index);
      if (!(element instanceof PaintProvider)) {
         graphics.drawText(SearchResources.getString(32), 0, y, 4, width);
      } else {
         PaintProvider paintProvider = (PaintProvider)element;
         paintProvider.paint(graphics, 0, y, width, listField.getHeight(), null);
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   public SearchList(MessageSearchImpl search, boolean fromRibbon, KeywordFilterList keyList) {
      super(CommonResources.getString(9136), search.getKeywordList(), null, false);
      this._search = search;
      this._searchCollection = (SearchCollection)search.getCollection();
      this._fromRibbon = fromRibbon;
      this.setListCallback(this);
      if (!this.getListField().isEmpty()) {
         this.getListField().setSelectedIndex(0);
         this.getListField().setFocus();
      }

      this.getListField().setSize(this._searchCollection.size());
   }

   private final void prologue() {
      UiApplication.getUiApplication().popScreen(this);
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      int index = this.getListField().getSelectedIndex();
      Verb selectVerb = null;
      Object element = this.getSelectedElement();
      if (element instanceof RIMModel) {
         RIMModel model = (RIMModel)element;
         menu.add(new SearchList$DeleteSearchVerb(this, index, model));
         menu.add(new SearchList$EditSearchVerb(this, model));
         selectVerb = new SearchList$SelectSearchVerb(this, model);
         menu.add(selectVerb, 0);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\b':
            if (!this.isSearchStringEmpty()) {
               break;
            }
         case '\u007f':
            this.getKeywordFilterList().waitForComplete();
            Object element = this.getSelectedElement();
            if (element instanceof RIMModel) {
               RIMModel model = (RIMModel)element;
               Verb v = new SearchList$DeleteSearchVerb(this, this.getListField().getSelectedIndex(), model);
               v.invoke(null);
               return true;
            }
         case '\n':
            if (this.doEnter()) {
               return true;
            }
            break;
         case '\u001b':
            if (!this.isSearchStringEmpty()) {
               this.setSearchPattern(null);
               return true;
            }

            this.prologue();
            return true;
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.doEnter();
            return true;
         default:
            return super.invokeAction(action);
      }
   }

   static final CollectionListField access$000(SearchList x0) {
      return x0.getListField();
   }
}
