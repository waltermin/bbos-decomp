package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import java.util.Hashtable;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.indicators.UnreadCount;
import net.rim.device.apps.internal.ribbon.components.WeakReferenceCollectionUpdater;
import net.rim.device.apps.internal.ribbon.indicators.UnreadCountComponent;

class UnreadCountListenerHelper extends WeakReferenceCollectionUpdater implements RibbonComponent$RibbonComponentChangeListener {
   private UnreadCountComponent _normalMsgsUnread;
   private UnreadCountComponent _noFiledMsgsUnread;
   private static final String TYPE_NAME_MESSAGE_FILED = UnreadCount.getTypeName(1);
   private static final String TYPE_NAME_MESSAGE = UnreadCount.getTypeName(0);

   public UnreadCountComponent currentUnreadMessagesComponent() {
      return MessageListOptions.getOptions().getFlag(16) ? this._noFiledMsgsUnread : this._normalMsgsUnread;
   }

   @Override
   public void ribbonComponentChanged(RibbonComponent component) {
      if (component == this.currentUnreadMessagesComponent()) {
         this.doUpdates();
      }
   }

   @Override
   protected void updateComponent(Object component) {
      ((CountChangeListener)component).countChanged(this.currentUnreadMessagesComponent());
   }

   public UnreadCountListenerHelper() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory ucFactory = repos.getFactory("UnreadCount");
      Hashtable params = (Hashtable)(new Object());
      params.put("type", TYPE_NAME_MESSAGE);
      this._normalMsgsUnread = (UnreadCountComponent)ucFactory.createInstance(null);
      this._normalMsgsUnread.initialize(params, null);
      this._normalMsgsUnread.setChangeListener(this);
      params.clear();
      params.put("type", TYPE_NAME_MESSAGE_FILED);
      this._noFiledMsgsUnread = (UnreadCountComponent)ucFactory.createInstance(null);
      this._noFiledMsgsUnread.initialize(params, null);
      this._noFiledMsgsUnread.setChangeListener(this);
   }
}
