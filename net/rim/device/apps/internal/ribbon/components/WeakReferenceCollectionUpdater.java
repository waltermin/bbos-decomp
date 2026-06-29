package net.rim.device.apps.internal.ribbon.components;

import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class WeakReferenceCollectionUpdater {
   WeakReference[] _components = new Object[0];

   public void addComponentForUpdate(Object component) {
      if (component != null) {
         WeakReference wr = (WeakReference)(new Object(component));
         WeakReference[] components = this._components;
         synchronized (components) {
            int count = components.length;
            Array.resize(components, count + 1);
            components[count] = wr;
         }

         this.cleanUpComponents();
      }
   }

   public void doUpdates() {
      this.cleanUpComponents();
      WeakReference[] components = this._components;
      synchronized (components) {
         int count = components.length;

         for (int index = 0; index < count; index++) {
            Object component = components[index].get();
            if (component != null) {
               this.updateComponent(component);
            }
         }
      }
   }

   protected void updateComponent(Object _1) {
      throw null;
   }

   private void cleanUpComponents() {
      WeakReference[] components = this._components;
      synchronized (components) {
         int count = components.length;

         for (int index = 0; index < count; index++) {
            Object wr = components[index].get();
            if (wr == null) {
               System.arraycopy(components, index + 1, components, index, count - index - 1);
               count--;
            }
         }

         if (count != components.length) {
            Array.resize(components, count);
         }
      }
   }
}
