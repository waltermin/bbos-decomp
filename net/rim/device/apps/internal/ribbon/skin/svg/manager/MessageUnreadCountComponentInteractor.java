package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.indicators.UnreadCount;
import net.rim.device.apps.internal.ribbon.indicators.UnreadCountComponent;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;

class MessageUnreadCountComponentInteractor extends UnreadCountComponentInteractor {
   protected ModelInteractorImpl _modelInteractor;
   private static final String TYPE_NAME_MESSAGE_FILED = UnreadCount.getTypeName(1);
   private static final String TYPE_NAME_MESSAGE = UnreadCount.getTypeName(0);
   public static final long NORMAL_MSGS_UNREAD_KEY = -3230658477323968044L;
   public static final long NO_FILED_MSGS_UNREAD_KEY = 7279758559317528089L;
   private static final UnreadCountComponent _normalMsgsUnread;
   private static final UnreadCountComponent _noFiledMsgsUnread;

   MessageUnreadCountComponentInteractor(TextNode node, Hashtable params, ModelInteractorImpl mi) {
      super(node, params);
      this._modelInteractor = mi;
      UnreadCountComponent ucComponent = this.currentUnreadMessagesComponent();
      super._component = ucComponent;
      super._component.setChangeListener(this);
   }

   @Override
   public void ribbonComponentChanged(RibbonComponent component) {
      UnreadCountComponent ucComponent = this.currentUnreadMessagesComponent();
      super._component = ucComponent;
      super.ribbonComponentChanged(component);
   }

   private UnreadCountComponent currentUnreadMessagesComponent() {
      return MessageListOptions.getOptions().getFlag(16) ? _noFiledMsgsUnread : _normalMsgsUnread;
   }

   static {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory ucFactory = repos.getFactory("UnreadCount");
      Hashtable params = (Hashtable)(new Object());
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      UnreadCountComponent normalMsgsUnread = (UnreadCountComponent)ar.getOrWaitFor(-3230658477323968044L);
      if (normalMsgsUnread == null) {
         params.put("type", TYPE_NAME_MESSAGE);
         _normalMsgsUnread = (UnreadCountComponent)ucFactory.createInstance(null);
         _normalMsgsUnread.initialize(params, null);
         params.clear();
         ar.put(-3230658477323968044L, _normalMsgsUnread);
      } else {
         _normalMsgsUnread = normalMsgsUnread;
      }

      UnreadCountComponent noFiledMsgsUnread = (UnreadCountComponent)ar.getOrWaitFor(7279758559317528089L);
      if (noFiledMsgsUnread == null) {
         params.put("type", TYPE_NAME_MESSAGE_FILED);
         _noFiledMsgsUnread = (UnreadCountComponent)ucFactory.createInstance(null);
         _noFiledMsgsUnread.initialize(params, null);
         ar.put(7279758559317528089L, _noFiledMsgsUnread);
      } else {
         _noFiledMsgsUnread = noFiledMsgsUnread;
      }
   }
}
