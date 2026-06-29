package net.rim.device.apps.internal.task;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.KeywordFilterList;
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
import net.rim.device.apps.api.options.OptionsChangeListener;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.commonmodels.categories.FilterByCategoriesVerb;
import net.rim.device.apps.internal.commonmodels.categories.FilteredByCategoriesTitleField;
import net.rim.device.apps.internal.task.resources.TaskResources;

final class TaskList extends KeywordFilteredScreen implements ListFieldCallback, OptionsChangeListener {
   private TaskCollection _taskCollection;
   private int[] _filterCategoryIds;
   private PersistentObject _persistentFilterObject;
   private FilteredByCategoriesTitleField _titleField;
   private long _lastBackspaceTime;
   private static final long PERSISTED_TASK_FILTER = -4985420032647039156L;
   static Verb[] _verbHolder = new Object[0];
   private static final long BACKSPACE_DELETE_GUARD = 300L;

   private TaskList(ReadableList taskCollection, KeywordFilterList keyList) {
      super(null, keyList, null);
      this._taskCollection = (TaskCollection)taskCollection;
      this.setHelp("tasks");
      this.setListCallback(this);
      KeywordFilterCollectionListField listField = (KeywordFilterCollectionListField)this.getListField();
      if (!listField.isEmpty()) {
         listField.setFocus();
      }

      listField.setSize(this._taskCollection.size());
      Field oldTitleField = this.getTitleField();
      this.setTitleField(null);
      this._titleField = (FilteredByCategoriesTitleField)(new Object(oldTitleField));
      this.setTitleField(this._titleField);
      this.$initFilterCategoryIds();
      FilterByCategoriesVerb.filterByCategories(this, this._titleField, this._filterCategoryIds);
      listField.getKeywordFilterList().waitForComplete();
   }

   private final void $initFilterCategoryIds() {
      this._persistentFilterObject = RIMPersistentStore.getPersistentObject(-4985420032647039156L);
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
   public final void close() {
      this._persistentFilterObject.commit();
      super.close();
   }

   static final TaskList getInstance() {
      Task task = Task.getInstance();
      return new TaskList(task.getUICollection(), task.getFilteredList());
   }

   @Override
   protected final Object invokeVerb(Verb verb, Object parameter) {
      try {
         ContextObject c = (ContextObject)(new Object());
         Object o = this.getSelectedElement();
         if (o != null) {
            c.put(3696141428889703675L, o);
         }

         return super.invokeVerb(verb, c);
      } finally {
         ;
      }
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
      super.verbInvoked(verb, context, result);
      if (verb instanceof NewTaskVerb && result != null) {
         if (!this.getListField().isEmpty()) {
            this.getListField().setFocus();
         }

         this.setElementWithFocus(result);
         this.setSearchPattern(null);
      }

      this.getListField().setSize(this.getListField().getSize());
   }

   @Override
   protected final ContextObject getMenuContextObject() {
      ContextObject co = this.getContext();
      co = ContextObject.castOrCreate(co);
      Object selectedElement = this.getSelectedElement();
      if (selectedElement != null) {
         ContextObject.put(co, 3696141428889703675L, selectedElement);
      }

      return co;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField clf = (CollectionListField)listField;
      Object element = clf.getElementAt(index);
      if (!(element instanceof Object)) {
         if (index == clf.getExtraRowCount()) {
            graphics.drawText(TaskResources.getString(37), 0, y, 4, width);
         }
      } else {
         PaintProvider painter = (PaintProvider)element;
         Object context = VariableRowHeightProxy.addHeightAdjusterToContext(this.getMenuContextObject(), listField);
         painter.paint(graphics, 0, y, width, 100, context);
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      CollectionListField clf = (CollectionListField)listField;
      return clf.getElementAt(index);
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
   protected final boolean invokeAction(int action) {
      return action == 1 ? this.invokeDefaultMenuItem(0) : super.invokeAction(action);
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
            CollectionListField clf = this.getListField();
            TaskModel tm = (TaskModel)clf.getElementAt(clf.getSelectedIndex());
            if (tm != null) {
               new DeleteTaskVerb(tm, key).invoke(null);
            }

            return true;
         case '\n':
            this.getKeywordFilterList().waitForComplete();
            this.invokeDefaultMenuItem(0);
            return true;
         case ' ':
         case '　':
            if (this.isSearchStringEmpty() && TaskUtilities.toggleStatus((TaskModel)this.getSelectedElement())) {
               return true;
            }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected final boolean keyDown(int keyCode, int time) {
      if ((Keypad.status(keyCode) & 1) != 0) {
         int key = Keypad.key(keyCode);
         if (TaskResources.getString(43).indexOf(key) != -1 && TaskUtilities.toggleStatus((TaskModel)this.getSelectedElement())) {
            return true;
         }
      }

      return super.keyDown(keyCode, time);
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      KeywordFilteredListFinder finder = this.getFinderField();
      String pattern = finder.getText();
      Verb defaultVerb = new NewTaskVerb(pattern);
      menu.add(defaultVerb);
      menu.add(TaskOptionsVerb.getInstance(this, this._taskCollection));
      if (this._taskCollection instanceof TaskUICollection) {
         TaskUICollection taskUICollection = (TaskUICollection)this._taskCollection;
         if (taskUICollection.getStatusToHide() == 2) {
            menu.add(ToggleViewableTasks.getInstance(this, taskUICollection, this.getListField(), -1));
         } else {
            menu.add(ToggleViewableTasks.getInstance(this, taskUICollection, this.getListField(), 2));
         }
      }

      VerbRepository vr = VerbRepository.getVerbRepository(-4137793966879797982L);
      if (vr != null) {
         menu.add(vr.getVerbs(null));
      }

      Object element = this.getSelectedElement();
      if (element instanceof Object) {
         VerbProvider verbProvider = (VerbProvider)element;
         menu.add(verbProvider);
         menu.add(new TaskList$DeleteCompletedTasksVerb());
         Verb[] verbs = menu.getVerbs();
         boolean foundDefault = false;

         for (int i = 0; i < verbs.length; i++) {
            if (verbs[i] instanceof OpenTaskVerb) {
               defaultVerb = verbs[i];
               foundDefault = true;
            }
         }

         if (!foundDefault) {
            Verb providedDefault = verbProvider.getVerbs(null, new Object[0]);
            if (providedDefault != null) {
               defaultVerb = providedDefault;
            }
         }
      }

      if (this.getFinderField().getTextLength() != 0) {
         menu.add(ShowAllNamesVerb.getInstance(this));
      }

      menu.add((Verb)(new Object(this, this._titleField, this._filterCategoryIds)));
      menu.setDefault(defaultVerb);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         TaskOptions.getOptions().addOptionsChangeListener(this);
      } else {
         TaskOptions.getOptions().removeOptionsChangeListener(this);
      }
   }

   @Override
   public final void optionsChanged(int changedOptions) {
      if ((changedOptions & TaskOptions.SORT_ORDER) != 0) {
         this.refreshTaskList();
      }
   }

   private final void refreshTaskList() {
      Task.getInstance().getFilteredList().storeSearchCriteria();
      ((TaskUICollection)this._taskCollection).reset(TaskCollectionImpl.getInstance());
      Task.getInstance().getFilteredList().redoSearch();
      this.invalidate();
      this.getListField().invalidate();
      this.getListField().reset((TaskUICollection)this._taskCollection);
      this.getListField().setDirty(true);
      this.getListField().updateList();
      this.updateDisplay();
   }
}
