package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;

final class ComponentFactoryHelper extends WeakReferenceCollectionUpdater {
   @Override
   protected final void updateComponent(Object component) {
      ((RibbonComponent$RibbonComponentChangeListener)component).ribbonComponentChanged(null);
   }
}
