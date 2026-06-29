package net.rim.device.apps.internal.ribbon.indicators;

import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

class GridIndicatorFactory implements Factory {
   private IndicatorManagerImpl _indicatorManagerImpl;

   GridIndicatorFactory(IndicatorManagerImpl indicatorManagerImpl) {
      this._indicatorManagerImpl = indicatorManagerImpl;
   }

   void init() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("GridIndicators", this);
   }

   @Override
   public Object createInstance(Object context) {
      GridIndicatorField gf = new GridIndicatorField(this._indicatorManagerImpl);
      this._indicatorManagerImpl.registerFieldForUpdates(gf);
      return gf;
   }
}
