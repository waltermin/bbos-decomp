package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.im.layout.SLKeyLayout;

final class EmailFilterCollectionListField extends CollectionListField {
   private String _userId;
   private MainScreen _mainScreen;
   private SortedReadableList _sortedList;
   private boolean _alerted = true;
   private boolean _movingFilterState;

   public EmailFilterCollectionListField(MainScreen mainScreen, String userId, SortedReadableList list, ListFieldCallback listCallback) {
      super(list, listCallback);
      this._sortedList = list;
      this._mainScreen = mainScreen;
      this._userId = userId;
   }

   final void toggleMoveFilterState() {
      this._movingFilterState = !this._movingFilterState;
   }

   public final boolean isAlerted() {
      return this._alerted;
   }

   public final void setAlerted(boolean alerted) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      if ((status & 1) != 0 || this._movingFilterState) {
         boolean moveFilter = false;
         if ((status & 536870912) != 0) {
            if ((status & 131072) != 0) {
               moveFilter = true;
            }
         } else {
            moveFilter = true;
         }

         if (moveFilter) {
            if (amount > 0) {
               this.moveFilterDown();
            } else {
               this.moveFilterUp();
            }

            this._mainScreen.setDirty(true);
            return 0;
         }
      }

      return super.moveFocus(amount, status, time);
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      if (this._movingFilterState) {
         this._movingFilterState = false;
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               if (this._movingFilterState) {
                  this._movingFilterState = false;
                  return true;
               }

               EmailFilterEnableVerb.getInstance(this).invoke(null);
               this._mainScreen.setDirty(true);
               return true;
         }
      }

      return handled;
   }

   private final void moveFilter(int index, int adjust) {
      EmailFilterModelImplClone m1 = (EmailFilterModelImplClone)this.getSelectedElement();
      EmailFilterModelImplClone m2 = (EmailFilterModelImplClone)this.getElementAt(index + adjust);
      m1.setOrder(index + adjust);
      m1._hasChanged = true;
      m2.setOrder(index);
      m2._hasChanged = true;
      this.sortList();
      this.invalidate();
      this._mainScreen.setDirty(true);
   }

   final boolean moveFilterUp() {
      int size = this.getSize();
      int index = this.getSelectedIndex();
      if (index == 0) {
         return true;
      } else if (index < size) {
         this.moveFilter(index, -1);
         this.setSelectedIndex(index - 1);
         return true;
      } else {
         return false;
      }
   }

   final boolean moveFilterDown() {
      int size = this.getSize();
      int index = this.getSelectedIndex();
      if (index == size - 1) {
         return true;
      } else if (index < size && size > 0) {
         this.moveFilter(index, 1);
         this.setSelectedIndex(index + 1);
         return true;
      } else {
         return false;
      }
   }

   public final void sortList() {
      this._sortedList.sort();
      this.invalidate();
   }

   @Override
   public final void delete(int index) {
      this._sortedList.elementRemoved(null, this.getElementWithFocus());
      this.invalidate();
   }

   public final void add(Object obj) {
      this._sortedList.elementAdded(null, obj);
      this.invalidate();
   }

   public final void update(Object oldObj, Object newObj) {
      this._sortedList.elementUpdated(null, oldObj, newObj);
      this.invalidate();
   }

   public final boolean saveChanges() {
      boolean updated = false;
      int size = this._sortedList.size();
      EmailFilterCollectionImpl collection = EmailFilter.getCollection(this._userId);

      for (int i = 0; i < size; i++) {
         EmailFilterModelImplClone clone = (EmailFilterModelImplClone)this._sortedList.getAt(i);
         if (clone._hasChanged) {
            EmailFilterModelImpl model = new EmailFilterModelImpl(clone);
            collection.update(clone._org, model);
            ObjectGroup.createGroupIgnoreTooBig(model);
            updated = true;
         }
      }

      if (updated) {
         this._mainScreen.setDirty(false);
      }

      return updated;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      EmailFilterModelImplClone selectedFilter = (EmailFilterModelImplClone)this.getSelectedElement();
      if (selectedFilter == null) {
         return super.keyChar(key, status, time);
      }

      switch (key) {
         case '\n':
            EmailFilterEditVerb.getInstance(selectedFilter, this._userId, this).invoke(null);
            return true;
         case ' ':
            EmailFilterEnableVerb.getInstance(this).invoke(null);
            this._mainScreen.setDirty(true);
            return true;
         case '\u007f':
            EmailFilterDeleteVerb.getInstance(this, this._userId).invoke(null);
            return false;
         default:
            char keyToCheck = UiInternal.map(Keypad.getLayout().getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
            keyToCheck = Character.toLowerCase(keyToCheck);
            if (keyToCheck == 'u') {
               this.moveFilterUp();
               this._mainScreen.setDirty(true);
               return true;
            } else if (keyToCheck == 'd') {
               this.moveFilterDown();
               this._mainScreen.setDirty(true);
               return true;
            } else {
               return super.keyChar(key, status, time);
            }
      }
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      contextMenu.addItem(new EmailFilterEnableItem(this));
   }
}
