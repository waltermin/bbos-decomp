package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.Application;

final class CollectionMonitor implements CollectionListener {
   private CollectionMonitor$CollectionMonitorCallback _callback;
   private Application _app;
   private CollectionEventSource _source;
   private boolean _notificationPending;
   private CollectionMonitor$CollectionChangedRunnable _collectionChangedRunnable = new CollectionMonitor$CollectionChangedRunnable(this);

   CollectionMonitor(CollectionMonitor$CollectionMonitorCallback callback, Application app, CollectionEventSource source) {
      this._callback = callback;
      this._app = app;
      this._source = source;
   }

   final void startMonitor() {
      this._source.addCollectionListener(this);
   }

   final void stopMonitor() {
      this._source.removeCollectionListener(this);
   }

   final void collectionChanged() {
      this._notificationPending = false;
      this._callback.collectionChanged();
   }

   @Override
   public final void reset(Collection collection) {
      if (!this._notificationPending) {
         this._notificationPending = true;
         this._app.invokeLater(this._collectionChangedRunnable);
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.reset(collection);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.reset(collection);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.reset(collection);
   }
}
