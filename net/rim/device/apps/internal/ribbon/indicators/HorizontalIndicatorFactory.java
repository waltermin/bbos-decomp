package net.rim.device.apps.internal.ribbon.indicators;

import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

class HorizontalIndicatorFactory implements Factory {
   private IndicatorManagerImpl _indicatorManagerImpl;

   HorizontalIndicatorFactory(IndicatorManagerImpl indicatorManagerImpl) {
      this._indicatorManagerImpl = indicatorManagerImpl;
   }

   void init() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("HorizontalIndicators", this);
   }

   @Override
   public Object createInstance(Object context) {
      HorizontalIndicatorField gf = new HorizontalIndicatorField(this._indicatorManagerImpl);
      this._indicatorManagerImpl.registerFieldForUpdates(gf);
      return gf;
   }
}
