package net.rim.blackberry.api.pdap;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.ToDo;
import javax.microedition.pim.UnsupportedFieldException;
import net.rim.blackberry.api.pim.resource.PIMResResource;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.task.TaskCollectionHolder;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoryList;

public final class ToDoListImpl extends PIMListImpl implements BlackBerryToDoList, PIMResResource {
   private TaskCollection _collection;
   private static ResourceBundle _resources = ResourceBundle.getBundle(6683049446475877841L, "net.rim.blackberry.api.pim.resource.PIMRes");
   private static Hashtable _actualListeners;
   private static final long APP_REGISTRY_KEY;
   private static String LIST_CLOSED_MESSAGE;
   private static String READONLY_MESSAGE;
   private static String WRITEONLY_MESSAGE;

   ToDoListImpl(int mode) {
      this();
      this.initialize(mode);
   }

   ToDoListImpl() {
      super._closed = false;
      this._collection = TaskCollectionHolder.getTaskCollection();
   }

   @Override
   public final void initialize(int mode) {
      super._mode = mode;
   }

   @Override
   protected final void removePIMItem(PIMItem pi) {
      try {
         if (pi instanceof ToDo) {
            ToDo todo = (ToDo)pi;
            this.removeToDo(todo);
            return;
         }
      } catch (PIMException var3) {
      }
   }

   @Override
   public final String getFieldLabel(int fieldID) {
      switch (fieldID) {
         case 100:
         case 102:
         case 106:
            throw new UnsupportedFieldException("", fieldID);
         case 101:
         case 16777225:
            return _resources.getString(29);
         case 103:
            return _resources.getString(27);
         case 104:
            return _resources.getString(24);
         case 105:
            return _resources.getString(28);
         case 107:
            return _resources.getString(25);
         case 108:
            return _resources.getString(26);
         case 20000927:
            return "";
         default:
            throw new Object();
      }
   }

   @Override
   public final int getFieldDataType(int fieldID) {
      switch (fieldID) {
         case 100:
         case 102:
         case 106:
            throw new UnsupportedFieldException("", fieldID);
         case 101:
            return 1;
         case 103:
         case 20000927:
            return 2;
         case 104:
         case 107:
         case 108:
            return 4;
         case 105:
         case 16777225:
            return 3;
         default:
            throw new Object();
      }
   }

   @Override
   protected final boolean verifyField(int field) {
      switch (field) {
         case 100:
         case 102:
         case 106:
            return false;
         case 101:
         case 103:
         case 104:
         case 105:
         case 107:
         case 108:
         case 16777225:
         case 20000927:
            return true;
         default:
            throw new Object();
      }
   }

   @Override
   public final String getName() {
      return "ToDo List";
   }

   @Override
   public final void close() {
      if (!super._closed) {
         super._closed = true;
      } else {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }
   }

   @Override
   public final Enumeration items() {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      int size = this._collection.size();
      Object[] matchingElements = new Object[size];

      for (int i = 0; i < size; i++) {
         matchingElements[i] = new ToDoImpl(super._mode, (TaskModel)this._collection.getAt(i), this);
      }

      return (Enumeration)(new Object(matchingElements));
   }

   @Override
   public final Enumeration items(PIMItem matching) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      if (matching == null) {
         throw new Object();
      }

      if (!(matching instanceof ToDo)) {
         throw new Object();
      }

      ToDo matchingToDo = (ToDo)matching;
      int size = this._collection.size();
      Object[] matchingElements = new Object[size];

      for (int i = 0; i < size; i++) {
         ToDoImpl todoImpl = new ToDoImpl(super._mode, (TaskModel)this._collection.getAt(i), this);
         if ((
               matchingToDo.countValues(104) <= 0
                  || todoImpl.countValues(104) != 0 && this.matchString(todoImpl.getString(104, 0), matchingToDo.getString(104, 0))
            )
            && (
               matchingToDo.countValues(107) <= 0
                  || todoImpl.countValues(107) != 0 && this.matchString(todoImpl.getString(107, 0), matchingToDo.getString(107, 0))
            )
            && (matchingToDo.countValues(105) <= 0 || todoImpl.countValues(105) != 0 && matchingToDo.getInt(105, 0) == todoImpl.getInt(105, 0))
            && (matchingToDo.countValues(103) <= 0 || todoImpl.countValues(103) != 0 && matchingToDo.getDate(103, 0) == todoImpl.getDate(103, 0))
            && (
               matchingToDo.countValues(16777225) <= 0
                  || todoImpl.countValues(16777225) != 0 && matchingToDo.getInt(16777225, 0) == todoImpl.getInt(16777225, 0)
            )
            && (
               matchingToDo.countValues(108) <= 0
                  || todoImpl.countValues(108) != 0 && this.matchString(todoImpl.getString(108, 0), matchingToDo.getString(108, 0))
            )
            && (matchingToDo.countValues(101) <= 0 || todoImpl.countValues(101) != 0 && matchingToDo.getBoolean(101, 0) == todoImpl.getBoolean(101, 0))
            && (
               matchingToDo.countValues(20000927) <= 0
                  || todoImpl.countValues(20000927) != 0 && matchingToDo.getDate(20000927, 0) == todoImpl.getDate(20000927, 0)
            )) {
            matchingElements[i] = todoImpl;
         }
      }

      return (Enumeration)(new Object(matchingElements));
   }

   @Override
   public final Enumeration items(String matching) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      if (matching == null) {
         throw new Object();
      }

      int size = this._collection.size();
      Object[] matchingElements = new Object[size];

      for (int i = 0; i < size; i++) {
         TaskModel taskModel = (TaskModel)this._collection.getAt(i);
         if (taskModel.getTitleModel() != null && this.matchString(taskModel.getTitleModel().toString(), matching)
            || this.matchString(taskModel.getNotes(), matching)) {
            matchingElements[i] = new ToDoImpl(super._mode, taskModel, this);
         }
      }

      return (Enumeration)(new Object(matchingElements));
   }

   @Override
   public final Enumeration items(int field, long startDate, long endDate) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (startDate > endDate) {
         throw new Object("End date is before start date.");
      }

      if (this.getFieldDataType(field) != 2) {
         throw new Object("Field not of type DATE.");
      }

      if (super._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      }

      int size = this._collection.size();
      Object[] matchingElements = new Object[size];

      for (int i = 0; i < size; i++) {
         TaskModel taskModel = (TaskModel)this._collection.getAt(i);
         if (taskModel.hasDueDate()) {
            long time = taskModel.getDueDate();
            if (endDate >= time && startDate <= time) {
               matchingElements[i] = new ToDoImpl(super._mode, taskModel, this);
            }
         }
      }

      Arrays.sort(matchingElements, new ToDoListImpl$DueDateComparator());
      return (Enumeration)(new Object(matchingElements));
   }

   @Override
   public final ToDo createToDo() {
      return new ToDoImpl(super._mode, this);
   }

   @Override
   public final boolean isSupportedField(int fieldID) {
      switch (fieldID) {
         case 101:
         case 103:
         case 104:
         case 105:
         case 107:
         case 108:
         case 16777225:
         case 20000927:
            return true;
         default:
            return false;
      }
   }

   @Override
   public final int[] getSupportedFields() {
      return new int[]{
         103,
         104,
         105,
         107,
         108,
         101,
         16777225,
         20000927,
         -977993472,
         1933247232,
         526976000,
         671613164,
         467227,
         7612424,
         192161800,
         1867325294,
         134245382,
         1970561857,
         7596831,
         1886404872,
         1738588877,
         134229406,
         134247489,
         2121232194,
         1107820577,
         8283298,
         1736590088,
         1936025881,
         1812361037,
         -2109536256,
         -243721881,
         -2109536256
      };
   }

   @Override
   public final ToDo importToDo(ToDo element) {
      if (element == null) {
         throw new Object();
      }

      ToDo newToDo = this.createToDo();
      int[] fields = element.getFields();

      for (int i = fields.length - 1; i >= 0; i--) {
         int type = this.getFieldDataType(fields[i]);

         try {
            switch (type) {
               case 1:
                  break;
               case 2:
               default:
                  newToDo.addDate(fields[i], 0, element.getDate(fields[i], 0));
                  break;
               case 3:
                  newToDo.addInt(fields[i], 0, element.getInt(fields[i], 0));
                  break;
               case 4:
                  newToDo.addString(fields[i], 0, element.getString(fields[i], 0));
            }
         } finally {
            continue;
         }
      }

      return newToDo;
   }

   @Override
   public final void removeToDo(ToDo element) {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 1) {
         throw new Object(READONLY_MESSAGE);
      }

      if (element == null) {
         throw new Object();
      }

      if (!(element instanceof ToDoImpl)) {
         throw new PIMException(PIMListImpl.NOT_FOUND_MESSAGE, 6);
      }

      ToDoImpl todo = (ToDoImpl)element;
      if (this._collection.contains(todo.getInternalModel())) {
         this._collection.remove(todo.getInternalModel());
         todo.removeFromList();
      } else {
         throw new PIMException(PIMListImpl.NOT_FOUND_MESSAGE, 6);
      }
   }

   private final boolean matchString(String str, String prefix) {
      if (str == null && prefix == null) {
         return true;
      }

      if (str != null && prefix != null) {
         str = str.toLowerCase();
         prefix = prefix.toLowerCase();
         int index = 0;

         while (index != -1) {
            if (str.startsWith(prefix, index)) {
               return true;
            }

            index = str.indexOf(32, index);
            if (index >= 0) {
               index++;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   protected final PIMItem getPIMItemFor(Object element) {
      return new ToDoImpl(3, (TaskModel)element, this);
   }

   @Override
   protected final CollectionEventSource getCollectionEventSource() {
      return this._collection;
   }

   @Override
   protected final Hashtable getActualListeners() {
      return _actualListeners;
   }

   @Override
   protected final Enumeration getItemsInCategory(String category) {
      int categoryID = -1;
      if (category != null) {
         categoryID = CategoryList.getInstance().getCategoryId(category);
         if (categoryID == -1) {
            return (Enumeration)(new Object());
         }
      }

      int listSize = this._collection.size();
      Object[] matchingElements = new Object[listSize];
      int[] categoryIds = new int[0];

      for (int i = listSize - 1; i >= 0; i--) {
         boolean taskMatches = false;
         TaskModel taskModel = (TaskModel)this._collection.getAt(i);
         CategoriesModel categoriesModel = taskModel.getCategoriesModel();
         if (category == null) {
            if (categoriesModel == null) {
               taskMatches = true;
            } else {
               categoriesModel.getCategoryIds(categoryIds);
               if (categoryIds.length == 0) {
                  taskMatches = true;
               }
            }
         } else if (categoriesModel != null) {
            categoriesModel.getCategoryIds(categoryIds);

            for (int j = categoryIds.length - 1; j >= 0; j--) {
               if (categoryIds[j] == categoryID) {
                  taskMatches = true;
                  break;
               }
            }
         }

         if (taskMatches) {
            matchingElements[i] = new ToDoImpl(super._mode, taskModel, this);
         }
      }

      return (Enumeration)(new Object(matchingElements));
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _actualListeners = (Hashtable)ar.getOrWaitFor(-6969096308581534025L);
      if (_actualListeners == null) {
         _actualListeners = (Hashtable)(new Object());
         ar.put(-6969096308581534025L, _actualListeners);
      }

      LIST_CLOSED_MESSAGE = "ToDoList is closed.";
      READONLY_MESSAGE = "ToDoList is read-only.";
      WRITEONLY_MESSAGE = "ToDoList is write-only.";
   }
}
