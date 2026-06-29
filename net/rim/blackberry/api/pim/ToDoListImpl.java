package net.rim.blackberry.api.pim;

import java.util.Enumeration;
import net.rim.blackberry.api.pim.resource.PIMResResource;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.ObjectEnumerator;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.task.TaskCollectionHolder;
import net.rim.device.apps.api.task.TaskModel;

public final class ToDoListImpl extends PIMListImpl implements BlackBerryToDoList, PIMResResource {
   private TaskCollection _collection;
   private static ResourceBundle _resources = ResourceBundle.getBundle(6683049446475877841L, "net.rim.blackberry.api.pim.resource.PIMRes");
   private static String LIST_CLOSED_MESSAGE = "ToDoList is closed.";
   private static String READONLY_MESSAGE = "ToDoList is read-only.";
   private static String WRITEONLY_MESSAGE = "ToDoList is write-only.";

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
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final String getFieldLabel(int fieldID) {
      switch (fieldID) {
         case 100:
         case 101:
         case 102:
         case 106:
            throw new UnsupportedFieldException(fieldID);
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
         case 16777225:
            return _resources.getString(29);
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final int getFieldDataType(int fieldID) {
      switch (fieldID) {
         case 100:
         case 101:
         case 102:
         case 106:
            throw new UnsupportedFieldException(fieldID);
         case 103:
            return 2;
         case 104:
         case 107:
         case 108:
            return 4;
         case 105:
         case 16777225:
            return 3;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   protected final boolean verifyField(int field) {
      switch (field) {
         case 100:
         case 101:
         case 102:
         case 106:
            return false;
         case 103:
         case 104:
         case 105:
         case 107:
         case 108:
         case 16777225:
            return true;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getName() {
      return "ToDo List";
   }

   @Override
   public final void close() throws PIMException {
      if (!super._closed) {
         super._closed = true;
      } else {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }
   }

   @Override
   public final Enumeration items() throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITEONLY_MESSAGE);
      }

      int size = this._collection.size();
      Object[] matchingElements = new Object[size];

      for (int i = 0; i < size; i++) {
         matchingElements[i] = new ToDoImpl(super._mode, (TaskModel)this._collection.getAt(i), this);
      }

      return new ObjectEnumerator(matchingElements);
   }

   @Override
   public final Enumeration items(PIMItem matching) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITEONLY_MESSAGE);
      }

      if (!(matching instanceof ToDo)) {
         throw new IllegalArgumentException();
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
            )) {
            matchingElements[i] = todoImpl;
         }
      }

      return new ObjectEnumerator(matchingElements);
   }

   @Override
   public final Enumeration items(String matching) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITEONLY_MESSAGE);
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

      return new ObjectEnumerator(matchingElements);
   }

   @Override
   public final Enumeration items(long startDate, long endDate) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (startDate > endDate) {
         throw new PIMException("End date specified occurs prior to specified start date.");
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITEONLY_MESSAGE);
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

      return new ObjectEnumerator(matchingElements);
   }

   @Override
   public final ToDo createToDo() {
      return new ToDoImpl(super._mode, this);
   }

   @Override
   public final boolean isSupportedField(int fieldID) {
      switch (fieldID) {
         case 103:
         case 104:
         case 105:
         case 107:
         case 108:
         case 16777225:
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
         16777225,
         -805044223,
         -1258225470,
         990052549,
         1761869939,
         134278175,
         134247464,
         -848269247,
         -1637375806,
         1091043373,
         1107820660,
         561934191,
         -1572730880,
         134250084,
         1147499844,
         1141375106,
         1309147266,
         134243628,
         -1118564526
      };
   }

   @Override
   public final ToDo importToDo(ToDo element) {
      if (element == null) {
         throw new NullPointerException();
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
   public final void removeToDo(ToDo element) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 1) {
         throw new SecurityException(READONLY_MESSAGE);
      }

      if (element == null) {
         throw new IllegalArgumentException();
      }

      if (element instanceof ToDoImpl) {
         if (element.getPIMList().equals(this)) {
            this._collection.remove(((ToDoImpl)element).getRimTodo());
         } else {
            throw new PIMException(PIMListImpl.NOT_FOUND_MESSAGE, 6);
         }
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
}
