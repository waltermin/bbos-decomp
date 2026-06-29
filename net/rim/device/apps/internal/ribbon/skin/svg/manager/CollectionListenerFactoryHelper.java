package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.blackberry.api.phone.phonelogs.CallLog;
import net.rim.blackberry.api.phone.phonelogs.PhoneLogListener;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.apps.internal.ribbon.components.WeakReferenceCollectionUpdater;

class CollectionListenerFactoryHelper extends WeakReferenceCollectionUpdater implements CollectionListener, PhoneLogListener {
   @Override
   protected void updateComponent(Object component) {
      ((Handler)component).updateLater();
   }

   @Override
   public void reset(Collection collection) {
      this.doUpdates();
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.doUpdates();
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.doUpdates();
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.doUpdates();
   }

   @Override
   public void callLogAdded(CallLog cl) {
      this.doUpdates();
   }

   @Override
   public void callLogUpdated(CallLog cl, CallLog oldCl) {
      this.doUpdates();
   }

   @Override
   public void callLogRemoved(CallLog cl) {
      this.doUpdates();
   }

   @Override
   public void reset() {
      this.doUpdates();
   }
}
