package net.rim.device.apps.internal.ribbon.indicators;

import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

final class UnreadCountComponentFactory implements Factory {
   private IndicatorManagerImpl _indicatorManagerImpl;

   UnreadCountComponentFactory(IndicatorManagerImpl indicatorManagerImpl) {
      this._indicatorManagerImpl = indicatorManagerImpl;
   }

   final void init() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("UnreadCount", this);
   }

   @Override
   public final Object createInstance(Object initialData) {
      UnreadCountComponent unreadCount = new UnreadCountComponent(this._indicatorManagerImpl);
      this._indicatorManagerImpl.registerFieldForUpdates(unreadCount);
      return unreadCount;
   }
}
