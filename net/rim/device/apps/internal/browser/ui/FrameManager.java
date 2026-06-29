package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.browser.field.Destroyable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.util.Frame;

public final class FrameManager extends VerticalFieldManager implements Destroyable {
   private Frame _frame;

   public FrameManager(Frame frame) {
      this._frame = frame;
   }

   public final Frame getFrame() {
      return this._frame;
   }

   public final FrameManager getTopFrameManager() {
      FrameManager frameManager = this;

      for (Manager manager = this.getManager(); manager != null; manager = manager.getManager()) {
         if (manager instanceof FrameManager) {
            frameManager = (FrameManager)manager;
         }
      }

      return frameManager;
   }

   public static final FrameManager find(String name, Manager manager) {
      if (manager == null) {
         return null;
      }

      FrameManager frameManager = null;
      if (manager instanceof FrameManager) {
         frameManager = (FrameManager)manager;
         Frame frame = frameManager.getFrame();
         if (frame != null && StringUtilities.strEqualIgnoreCase(name, frame.getName(), 1701707776)) {
            return frameManager;
         }
      }

      Field field = null;
      int fieldCount = manager.getFieldCount();

      for (int i = 0; i < fieldCount; i++) {
         field = manager.getField(i);
         if (field instanceof Manager) {
            frameManager = find(name, (Manager)field);
            if (frameManager != null) {
               return frameManager;
            }
         }
      }

      return null;
   }

   @Override
   public final void destroy() {
      for (int i = this.getFieldCount() - 1; i >= 0; i--) {
         Field field = this.getField(i);
         if (field instanceof Destroyable) {
            ((Destroyable)field).destroy();
         }
      }
   }

   @Override
   public final void setDestroyMethod(int method) {
      for (int i = this.getFieldCount() - 1; i >= 0; i--) {
         Field field = this.getField(i);
         if (field instanceof Destroyable) {
            ((Destroyable)field).setDestroyMethod(method);
         }
      }
   }
}
