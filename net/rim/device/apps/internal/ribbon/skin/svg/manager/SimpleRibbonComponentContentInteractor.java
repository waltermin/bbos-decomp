package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import java.util.Hashtable;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.StringRibbonComponent;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;

class SimpleRibbonComponentContentInteractor implements RibbonComponent$RibbonComponentChangeListener {
   protected StringBuffer _workerBuffer;
   protected TextNode _node;
   protected StringRibbonComponent _component;

   SimpleRibbonComponentContentInteractor(TextNode node, String factoryName, Hashtable params) {
      this._node = node;
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory factory = repos.getFactory(factoryName);
      if (factory != null) {
         this._component = (StringRibbonComponent)factory.createInstance(null);
         this._component.initialize(params, null);
         this._component.setChangeListener(this);
      }

      this._workerBuffer = (StringBuffer)(new Object());
   }

   @Override
   public void ribbonComponentChanged(RibbonComponent component) {
      if (this._component != null) {
         String result = this._component.getText();
         this._workerBuffer.delete(0, this._workerBuffer.length());
         this._workerBuffer.append(result);
         synchronized (this._node.getModel()) {
            this._node.setString(this._workerBuffer.toString().toCharArray());
         }
      }
   }
}
