package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager$EntryChangeListener;

final class EntryComponentFactoryHelper extends WeakReferenceCollectionUpdater implements HierarchyManager$EntryChangeListener {
   private ApplicationEntry _entry;

   @Override
   protected final void updateComponent(Object component) {
      ((HierarchyManager$EntryChangeListener)component).onEntryChange(this._entry);
   }

   @Override
   public final void onEntryChange(ApplicationEntry entry) {
      this._entry = entry;
      this.doUpdates();
   }
}
