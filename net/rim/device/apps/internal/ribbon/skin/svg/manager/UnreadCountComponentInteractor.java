package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import java.util.Hashtable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;

class UnreadCountComponentInteractor extends SimpleRibbonComponentContentInteractor {
   private String[] _displayOptions;
   private boolean _forceNew;
   private boolean _roundBrackets;
   static final String UNREAD_COUNT_FACTORY;
   private static ResourceBundleFamily _rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");

   UnreadCountComponentInteractor(TextNode node, Hashtable params) {
      super(node, "UnreadCount", params);
      this._forceNew = params.get("onlyNew") != null;
      this._roundBrackets = params.get("roundBrackets") != null;
      this.ribbonComponentChanged(null);
   }

   @Override
   public void ribbonComponentChanged(RibbonComponent component) {
      if (super._component != null) {
         String result = super._component.getText();
         super._workerBuffer.delete(0, super._workerBuffer.length());
         if (result != null && result.length() > 0) {
            super._workerBuffer.append((char)(this._roundBrackets ? '(' : '[')).append(result);
            if (this._forceNew) {
               String newLabel = _rbf.getString(150);
               super._workerBuffer.append(' ').append(newLabel);
            } else {
               this._displayOptions = MessageResources.getStringArray(216);
               int type = UnreadCountManager.getCountOptions().getDisplayMessageCount();
               if (type > 0 && type < this._displayOptions.length) {
                  super._workerBuffer.append(' ').append(this._displayOptions[type]);
               }
            }

            super._workerBuffer.append((char)(this._roundBrackets ? ')' : ']'));
         }

         synchronized (super._node.getModel()) {
            super._node.setString(super._workerBuffer.toString().toCharArray());
         }
      }
   }
}
