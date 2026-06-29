package net.rim.device.api.ui.component;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextFactory;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;
import net.rim.device.api.util.Arrays;
import net.rim.vm.TraceBack;
import net.rim.vm.WeakReference;

public class CollectionListField extends ListField implements FocusChangeListener, CollectionListener {
   private Application _application;
   private CollectionListField$UpdaterRunnable _updaterRunnable;
   private CollectionListField$SetFocusedElementRunnable _focusedElementRunnable;
   protected ReadableList _list;
   private WeakReference _collectionListener;
   private Object _elementWithFocus;
   private int _extraRowCount;
   private String[] _extraRowName;
   private boolean _isExtraRowAtBottom = false;

   public void addExtraRowName(String extraRowName) {
      Arrays.add(this._extraRowName, extraRowName);
   }

   protected void doUpdateList() {
      this.setSize(this.getListSize());
   }

   public void updateList() {
      synchronized (this._updaterRunnable) {
         if (this._updaterRunnable._isQueued) {
            this._updaterRunnable.requeue(this._application);
         } else {
            this._updaterRunnable._isQueued = true;
            this._application.invokeLater(this._updaterRunnable);
         }
      }
   }

   public void setList(ReadableList newList) {
      if (newList != this._list) {
         if (this._list instanceof CollectionEventSource) {
            ((CollectionEventSource)this._list).removeCollectionListener(this._collectionListener);
         }

         this._list = newList;
         if (this._list instanceof CollectionEventSource) {
            ((CollectionEventSource)this._list).addCollectionListener(this._collectionListener);
         }

         this.invalidate();
      }
   }

   public void setExtraRowAtBottom(boolean isExtraRowAtBottom) {
      this._isExtraRowAtBottom = isExtraRowAtBottom;
   }

   public void setElementWithFocus(Object element) {
      Application thisApplication = this.getApplication();
      if (this._application == thisApplication && Application.isEventDispatchThread()) {
         this.doSetElementWithFocus(element);
      } else {
         synchronized (this._focusedElementRunnable) {
            this._focusedElementRunnable._element = element;
            if (!this._focusedElementRunnable._isQueued) {
               this._focusedElementRunnable._isQueued = true;
               this._application.invokeLater(this._focusedElementRunnable);
            }
         }
      }
   }

   public Object getElementAt(int index) {
      try {
         return this._list.getAt(index - this.getExtraRowCount());
      } catch (ArrayIndexOutOfBoundsException var3) {
         return null;
      } catch (NullPointerException var4) {
         return null;
      }
   }

   public Object getElementWithFocus() {
      return this._elementWithFocus;
   }

   public int getExtraRowCount() {
      return this._extraRowCount;
   }

   public void setExtraRowCount(int newCount) {
      if (this._extraRowCount != newCount) {
         this._extraRowCount = newCount;
         this.setSize(this.getListSize());
         this._extraRowName = new String[this._extraRowCount];
      }
   }

   protected int getListSize() {
      return this._list != null ? this._list.size() : 0;
   }

   public Object getSelectedElement() {
      return this.getElementAt(this.getSelectedIndex());
   }

   @Override
   public void reset(Collection collection) {
      this.updateList();
   }

   @Override
   public void focusChanged(Field field, int action) {
      this.setElementWithFocus(this.getElementAt(this.getSelectedIndex()));
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this._elementWithFocus == oldElement) {
         this.setElementWithFocus(newElement);
      }

      this.updateList();
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      if (this._elementWithFocus == element) {
         int index = this.getSelectedIndex();
         if (this._list != null && index == this._list.size()) {
            index--;
         }

         this.setElementWithFocus(this.getElementAt(index));
      }

      this.updateList();
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.updateList();
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      if (index == 0) {
         Object temp;
         if (this._isExtraRowAtBottom) {
            if (this._extraRowCount > 0 && this.getSelectedIndex() > this.getSize() - 2 * this._extraRowCount) {
               return new AccessibleContextFactory(this._extraRowName[this.getSelectedIndex() + 2 * this._extraRowCount - this.getSize()], 0, 4);
            }

            temp = this.getElementAt(this.getSelectedIndex() + this._extraRowCount);
         } else {
            if (this._extraRowCount > 0 && this.getSelectedIndex() < this._extraRowCount) {
               return new AccessibleContextFactory(this._extraRowName[this.getSelectedIndex()], 0, 4);
            }

            temp = this.getElementAt(this.getSelectedIndex());
         }

         if (temp != null) {
            if (!(temp instanceof AccessibleContext)) {
               if (!(temp instanceof AccessibleContextProxy)) {
                  return new AccessibleContextFactory(temp.toString(), 0, 4);
               }

               return ((AccessibleContextProxy)temp).getAccessibleContext();
            }

            return (AccessibleContext)temp;
         }
      }

      return null;
   }

   private Application getApplication() {
      try {
         return Application.getApplication();
      } catch (Throwable e) {
         return null;
      }
   }

   public CollectionListField(ReadableList list, ListFieldCallback listCallback, long style) {
      super(0, style);
      this._application = Application.getApplication();
      this._collectionListener = new WeakReference(this);
      this._updaterRunnable = new CollectionListField$UpdaterRunnable(this);
      this._focusedElementRunnable = new CollectionListField$SetFocusedElementRunnable(this);
      if (listCallback != null) {
         this.setCallback(listCallback);
      }

      this.setList(list);
      this.setFocusListener(this);
      this.setSize(this.getListSize());
   }

   private void doSetElementWithFocus(Object element) {
      this._elementWithFocus = element;
      if (element != null && element != this.getSelectedElement() && this._list != null) {
         int index = this._list.getIndex(element);
         if (index != -1) {
            this.setSelectedIndex(index + this.getExtraRowCount());
         }
      }
   }

   @Override
   public void setSize(int count) {
      if (this._list != null) {
         int index = -1;
         if (this._elementWithFocus != null) {
            index = this._list.getIndex(this._elementWithFocus);
         }

         if (index == -1) {
            index = this.getExtraRowCount();
            this.setElementWithFocus(null);
         } else {
            index += this.getExtraRowCount();
         }

         this.setSize(count, index);
      }
   }

   @Override
   public void setSize(int count, int index) {
      if (this._list != null) {
         if (count == 0 && this.getEmptyString() != null) {
            count++;
         }

         count += this.getExtraRowCount();
         int beforeCount = this.getSize();
         super.setSize(count, index);
         if (beforeCount == 0 && count > 0 && this.getManager() != null) {
            this.setFocus();
         }

         if (this._elementWithFocus == null) {
            this.setElementWithFocus(this.getSelectedElement());
         }
      }
   }

   public CollectionListField(ReadableList list, ListFieldCallback listCallback) {
      this(list, listCallback, 0);
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
   }
}
