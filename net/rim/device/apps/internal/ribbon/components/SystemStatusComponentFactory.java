package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonNetworkInfo;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;

final class SystemStatusComponentFactory implements Factory, CollectionListener, TestPoint {
   private ReadableLongMap _networkProps;
   private ComponentFactoryHelper _helper = new ComponentFactoryHelper();
   String _testValue;

   final void init() {
      this._networkProps = RibbonNetworkInfo.getInstance().getNetworkPropsCollection();
      ((CollectionEventSource)this._networkProps).addCollectionListener(this);
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("SystemStatus", this);
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.reset(collection);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.reset(collection);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.reset(collection);
   }

   @Override
   public final void reset(Collection collection) {
      this._helper.doUpdates();
   }

   @Override
   public final Object createInstance(Object context) {
      SystemStatusField sf = new SystemStatusField(this, this._networkProps);
      this._helper.addComponentForUpdate(sf);
      return sf;
   }

   @Override
   public final void test(Object id, Object value) {
      if (value instanceof Object) {
         this._testValue = (String)value;
      } else {
         this._testValue = null;
      }

      this._helper.doUpdates();
   }
}
