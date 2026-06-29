package net.rim.wica.runtime.metadata.internal.component.ui;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.Arrays;

public final class ControlMappingResolver {
   private Hashtable _registeredListeners = (Hashtable)(new Object());
   private UIControlImpl[] _roots = new UIControlImpl[0];
   private boolean _isRootsSorted;

   public final void register(int[] mapping, UIControlImpl uiCtrl) {
      ControlMappingResolver$KeyGenerator keyGenerator = new ControlMappingResolver$KeyGenerator(mapping);

      for (String key = keyGenerator.getKey(2); key != null; key = keyGenerator.nextKey()) {
         if (!this._registeredListeners.containsKey(key)) {
            this._registeredListeners.put(key, new Object());
         }

         ((Vector)this._registeredListeners.get(key)).addElement(uiCtrl);
      }
   }

   public final void notifyMappingChange(String key) {
      if (this._registeredListeners.containsKey(key)) {
         Vector listener = (Vector)this._registeredListeners.get(key);
         int count = listener.size();

         for (int i = 0; i < count; i++) {
            UIControlImpl control = (UIControlImpl)listener.elementAt(i);
            control.onValueChanged(true);
            control.resolveMapping();
         }
      }
   }

   public final void reset() {
      this._registeredListeners.clear();
   }

   public final String getMappingKey(int[] mapping) {
      return new ControlMappingResolver$KeyGenerator(mapping).getKey(mapping.length);
   }

   public final void updateRootControlUI() {
      if (this._roots.length != 0) {
         this.sortRoots();
         Object[] roots = this._roots;
         int numRoots = roots.length;

         for (int i = 0; i < numRoots; i++) {
            ((UIControlImpl)roots[i]).updateUI();
         }
      }
   }

   public final void updateRootControlData() {
      if (this._roots.length != 0) {
         this.sortRoots();
         Object[] roots = this._roots;
         int numRoots = roots.length;

         for (int i = 0; i < numRoots; i++) {
            ((UIControlImpl)roots[i]).updateData();
         }
      }
   }

   private final void sortRoots() {
      if (!this._isRootsSorted) {
         Arrays.sort(this._roots, new ControlMappingResolver$1(this));
         this._isRootsSorted = true;
      }
   }

   public final void registerAsRoot(UIControlImpl ctrl) {
      Arrays.add(this._roots, ctrl);
   }
}
