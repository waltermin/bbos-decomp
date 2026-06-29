package net.rim.device.apps.internal.memo;

import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.KeywordFilteredListFinder;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.commonmodels.categories.FilterByCategoriesVerb;
import net.rim.device.apps.internal.commonmodels.categories.FilteredByCategoriesTitleField;
import net.rim.device.apps.internal.memo.resources.MemoResources;

final class MemoList extends KeywordFilteredScreen implements ListFieldCallback {
   private MemoCollectionImpl _memoCollection = MemoCollectionImpl.getInstance();
   private int[] _filterCategoryIds;
   private PersistentObject _persistentFilterObject;
   private FilteredByCategoriesTitleField _titleField;
   private Object _cachedContext;
   private long _lastBackspaceTime;
   private static final long PERSISTED_MEMO_FILTER;
   private static final long BACKSPACE_DELETE_GUARD;

   MemoList() {
      super(null, null, null);
      this.setHelp("memos");
      this.setList(this._memoCollection.getKeywordList());
      this.setListCallback(this);
      KeywordFilterCollectionListField listField = (KeywordFilterCollectionListField)this.getListField();
      if (!listField.isEmpty()) {
         listField.setFocus();
      }

      listField.setSize(this._memoCollection.size());
      Field oldTitleField = this.getTitleField();
      this.setTitleField(null);
      this._titleField = (FilteredByCategoriesTitleField)(new Object(oldTitleField));
      this.setTitleField(this._titleField);
      this.initFilterCategoryIds();
      FilterByCategoriesVerb.filterByCategories(this, this._titleField, this._filterCategoryIds);
      listField.getKeywordFilterList().waitForComplete();
   }

   @Override
   public final void close() {
      this._persistentFilterObject.commit();
      super.close();
   }

   private final void initFilterCategoryIds() {
      this._persistentFilterObject = RIMPersistentStore.getPersistentObject(3876048698248748016L);
      synchronized (this._persistentFilterObject) {
         this._filterCategoryIds = (int[])this._persistentFilterObject.getContents();
         if (this._filterCategoryIds == null) {
            this._filterCategoryIds = new int[0];
            this._persistentFilterObject.setContents(this._filterCategoryIds, 51, false);
            this._persistentFilterObject.commit();
         }
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      return action == 1 ? this.invokeDefaultMenuItem(0) : super.invokeAction(action);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final Object invokeVerb(Verb verb, Object parameter) {
      ContextObject context = ContextObject.castOrCreate(parameter);
      boolean var8 = false /* VF: Semaphore variable */;

      Object var5;
      try {
         var8 = true;
         Object element = this.getSelectedElement();
         if (element != null) {
            context.put(3696141428889703675L, element);
         }

         var5 = super.invokeVerb(verb, context);
         var8 = false;
      } finally {
         if (var8) {
            context.remove(3696141428889703675L);
         }
      }

      context.remove(3696141428889703675L);
      return var5;
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
      super.verbInvoked(verb, context, result);
      if (verb instanceof MemoList$NewMemoVerb && result != null) {
         if (!this.getListField().isEmpty()) {
            this.getListField().setFocus();
         }

         this.setElementWithFocus(result);
         this.setSearchPattern(null);
      }

      this.getListField().setSize(this.getListField().getSize());
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      KeywordFilteredListFinder finder = this.getFinderField();
      String pattern = finder.getText();
      Verb defaultVerb = new MemoList$NewMemoVerb(pattern);
      menu.add(defaultVerb);
      menu.add(new MemoList$MemoOptionsVerb());
      VerbRepository vr = VerbRepository.getVerbRepository(-7444654586207082127L);
      if (vr != null) {
         menu.add(vr.getVerbs(null));
      }

      Object element = this.getSelectedElement();
      if (element instanceof MemoModelImpl) {
         VerbProvider verbProvider = (VerbProvider)element;
         menu.add(verbProvider);
         Verb[] verbHolder = new Object[0];
         Verb verb = verbProvider.getVerbs(null, verbHolder);
         if (defaultVerb != null) {
            ;
         }

         MemoModelImpl memoModel = (MemoModelImpl)element;
         defaultVerb = new EditMemoVerb(memoModel);
         menu.add(defaultVerb);
         menu.add(new DeleteMemoVerb(memoModel));
      }

      int finderTextLength = this.getFinderField().getTextLength();
      if (finderTextLength != 0) {
         menu.add(new MemoList$ShowAllMemosVerb(this));
      }

      menu.add((Verb)(new Object(this, this._titleField, this._filterCategoryIds)));
      menu.setDefault(defaultVerb);
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      long currentTime = System.currentTimeMillis();
      if (Keypad.key(keycode) == 8) {
         if (!this.isSearchStringEmpty()) {
            this._lastBackspaceTime = currentTime;
         } else {
            if (currentTime - this._lastBackspaceTime < 300) {
               this._lastBackspaceTime = currentTime;
               return 65536;
            }

            this._lastBackspaceTime = currentTime;
         }
      } else {
         this._lastBackspaceTime = 0;
      }

      return super.processKeyEvent(event, key, keycode, time);
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
            Object memo = this.getSelectedElement();
            if (memo instanceof MemoModelImpl) {
               new DeleteMemoVerb((MemoModelImpl)memo, key).invoke(null);
               return true;
            }

            return false;
         case '\n':
            this.getKeywordFilterList().waitForComplete();
            this.invokeDefaultMenuItem(0);
            return true;
      }

      return super.keyChar(key, status, time);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField clf = (CollectionListField)listField;
      Object element = clf.getElementAt(index);
      if (element instanceof Object) {
         this._cachedContext = VariableRowHeightProxy.addHeightAdjusterToContext(this._cachedContext, listField);
         ((PaintProvider)element).paint(graphics, 0, y, width, 100, this._cachedContext);
      } else {
         if (index == clf.getExtraRowCount()) {
            graphics.drawText(MemoResources.getString(180), 0, y, 4, width);
         }
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      CollectionListField clf = (CollectionListField)listField;
      return clf.getElementAt(index);
   }
}
