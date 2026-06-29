package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.apps.api.utility.props.PropsChangeListener;

public class PropsChangeListenerHelper extends WeakReferenceCollectionUpdater implements PropsChangeListener {
   private long _propID;

   @Override
   protected void updateComponent(Object component) {
      ((PropsChangeListener)component).propChanged(this._propID);
   }

   @Override
   public void propChanged(long propID) {
      this._propID = propID;
      this.doUpdates();
   }
}
