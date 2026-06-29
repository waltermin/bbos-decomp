package net.rim.device.apps.internal.memorycleaner;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.component.PopupDialog;

final class MemoryCleanerDialog extends PopupDialog implements ListFieldCallback {
   LabelField _labelField;
   MemoryCleaner _cleaner;
   MemoryCleanerListener[] _listeners;
   int _numListeners;
   ListField _listField;
   boolean[] _completedOperations;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(-7878136828847559798L, "net.rim.device.apps.internal.resource.MemoryCleaner");

   public MemoryCleanerDialog(MemoryCleaner cleaner, MemoryCleanerListener[] listeners) {
      super((Manager)(new Object()), 33554432);
      this._cleaner = cleaner;
      this._listeners = listeners;
      this._numListeners = listeners.length;

      label40:
      try {
         while (this._numListeners > 0 && this._listeners[this._numListeners - 1].getDescription() == null) {
            this._numListeners--;
         }
      } finally {
         break label40;
      }

      this._completedOperations = new boolean[this._numListeners];
      Image image = ThemeManager.getThemeAwareImage("dialog_cleaning_memory");
      ImageField imageField = (ImageField)(new Object());
      imageField.setImage(image);
      this.add(imageField);
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
      this._labelField = (LabelField)(new Object(_rb.getString(1), 51539607552L));
      vfm.add(this._labelField);
      vfm.add((Field)(new Object()));
      if (this._numListeners > 0) {
         this._listField = (ListField)(new Object(this._numListeners));
         this._listField.setCallback(this);
         VerticalFieldManager scrollingfm = (VerticalFieldManager)(new Object(299067162755072L));
         scrollingfm.add(this._listField);
         vfm.add(scrollingfm);
      }

      this.add(vfm);
   }

   public final void setCompletedOperation(int operationIndex) {
      if (operationIndex < this._numListeners) {
         label35:
         try {
            Thread.sleep(250);
         } finally {
            break label35;
         }

         this._completedOperations[operationIndex] = true;
         synchronized (Application.getApplication().getAppEventLock()) {
            this._listField.invalidate(operationIndex);
            this._listField.setSelectedIndex(operationIndex);
         }

         Application.getApplication().invokeAndWait(new MemoryCleanerDialog$EmptyRunnable());
      }
   }

   public final void GCComplete() {
      synchronized (Application.getApplication().getAppEventLock()) {
         this._labelField.setText(_rb.getString(2));
      }

      Application.getApplication().invokeAndWait(new MemoryCleanerDialog$EmptyRunnable());

      label43:
      try {
         Thread.sleep(1000);
      } finally {
         break label43;
      }

      synchronized (Application.getApplication().getAppEventLock()) {
         this.close(0);
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (listField == this._listField && index >= 0 && index < this._numListeners) {
         StringBuffer label = (StringBuffer)(new Object());
         label.append((char)(this._completedOperations[index] ? '☑' : '☐'));
         label.append(' ');

         label35:
         try {
            label.append(this._listeners[index].getDescription());
         } finally {
            break label35;
         }

         graphics.drawText(label.toString(), 0, y, 64, width);
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return listField == this._listField && index >= 0 && index < this._numListeners ? this._listeners[index] : null;
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._cleaner.dialogDisplayed();
      }
   }
}
