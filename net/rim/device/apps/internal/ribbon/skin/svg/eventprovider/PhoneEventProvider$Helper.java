package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import java.util.Hashtable;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.internal.ribbon.components.WeakReferenceCollectionUpdater;
import net.rim.device.apps.internal.ribbon.indicators.UnreadCountComponent;

class PhoneEventProvider$Helper extends WeakReferenceCollectionUpdater implements RibbonComponent$RibbonComponentChangeListener {
   private UnreadCountComponent _missedCalls;

   public PhoneEventProvider$Helper() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory ucFactory = repos.getFactory("UnreadCount");
      Hashtable params = (Hashtable)(new Object());
      params.put("type", "missedphonecalls");
      this._missedCalls = (UnreadCountComponent)ucFactory.createInstance(null);
      this._missedCalls.initialize(params, null);
      this._missedCalls.setChangeListener(this);
   }

   @Override
   protected void updateComponent(Object component) {
      ((PhoneEventProvider)component).countChanged(this._missedCalls);
   }

   @Override
   public void ribbonComponentChanged(RibbonComponent component) {
      if (component == this._missedCalls) {
         this.doUpdates();
      }
   }
}
