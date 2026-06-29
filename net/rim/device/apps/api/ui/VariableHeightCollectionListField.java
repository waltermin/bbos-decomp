package net.rim.device.apps.api.ui;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.component.VariableHeightListField;
import net.rim.device.api.ui.component.VariableHeightListFieldCallback;
import net.rim.vm.WeakReference;

public class VariableHeightCollectionListField extends VariableHeightListField implements FocusChangeListener, CollectionListener {
   private Application _application = Application.getApplication();
   private VariableHeightCollectionListField$UpdaterRunnable _updaterRunnable;
   private VariableHeightCollectionListField$SetFocusedElementRunnable _focusedElementRunnable;
   protected ReadableList _list;
   private WeakReference _collectionListener = (WeakReference)(new Object(this));
   private Object _elementWithFocus;
   private int _extraRowCount;

   public VariableHeightCollectionListField(ReadableList list, VariableHeightListFieldCallback listCallback) {
      this(list, listCallback, 0);
   }

   public VariableHeightCollectionListField(ReadableList list, VariableHeightListFieldCallback listCallback, long style) {
      super(0, style);
      this._updaterRunnable = new VariableHeightCollectionListField$UpdaterRunnable(this);
      this._focusedElementRunnable = new VariableHeightCollectionListField$SetFocusedElementRunnable(this);
      if (listCallback != null) {
         this.setCallback(listCallback);
      }

      this.setList(list);
      this.setFocusListener(this);
      this.setSize(this.getListSize());
   }

   public int getExtraRowCount() {
      return this._extraRowCount;
   }

   public void setExtraRowCount(int newCount) {
      if (this._extraRowCount != newCount) {
         this._extraRowCount = newCount;
         this.setSize(this.getListSize());
      }
   }

   private Application getApplication() {
      try {
         return Application.getApplication();
      } finally {
         return null;
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

   protected int getListSize() {
      return this._list != null ? this._list.size() : 0;
   }

   public void setList(ReadableList newList) {
      if (newList != this._list) {
         if (this._list instanceof Object) {
            ((CollectionEventSource)this._list).removeCollectionListener(this._collectionListener);
         }

         this._list = newList;
         if (this._list instanceof Object) {
            ((CollectionEventSource)this._list).addCollectionListener(this._collectionListener);
         }

         this.invalidate();
      }
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

   private void doSetElementWithFocus(Object element) {
      this._elementWithFocus = element;
      if (element != null && element != this.getSelectedElement() && this._list != null) {
         int index = this._list.getIndex(element);
         if (index != -1) {
            this.setSelectedIndex(index + this.getExtraRowCount());
         }
      }
   }

   public Object getSelectedElement() {
      return this.getElementAt(this.getSelectedIndex());
   }

   public Object getElementWithFocus() {
      return this._elementWithFocus;
   }

   public Object getElementAt(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/api/ui/VariableHeightCollectionListField._list Lnet/rim/device/api/collection/ReadableList;
      // 04: iload 1
      // 05: aload 0
      // 06: invokevirtual net/rim/device/apps/api/ui/VariableHeightCollectionListField.getExtraRowCount ()I
      // 09: isub
      // 0a: invokeinterface net/rim/device/api/collection/ReadableList.getAt (I)Ljava/lang/Object; 2
      // 0f: areturn
      // 10: astore 2
      // 11: aconst_null
      // 12: areturn
      // 13: astore 2
      // 14: aconst_null
      // 15: areturn
      // try (0 -> 7): 8 null
      // try (0 -> 7): 11 null
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

   @Override
   public void focusChanged(Field field, int action) {
      this.setElementWithFocus(this.getElementAt(this.getSelectedIndex()));
   }

   @Override
   public void reset(Collection collection) {
      this.updateList();
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.updateList();
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
}
