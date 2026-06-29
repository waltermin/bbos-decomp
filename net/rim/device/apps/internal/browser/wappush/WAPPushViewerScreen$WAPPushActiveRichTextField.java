package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.GeneralProperty;

class WAPPushViewerScreen$WAPPushActiveRichTextField extends ActiveRichTextField {
   private final WAPPushViewerScreen this$0;

   public WAPPushViewerScreen$WAPPushActiveRichTextField(WAPPushViewerScreen _1, String text, long style) {
      super(text, style);
      this.this$0 = _1;
   }

   @Override
   protected MenuItem addCookieMenuItems(CookieProvider provider, int cookieId, ContextMenu contextMenu, Object context) {
      return super._cookieIDs != null && super._cookieIDs.containsKey(cookieId) && cookieId >= 0 && super._cookieIDs.get(cookieId) != null
         ? super.addCookieMenuItems(provider, this.getCookieInternal(cookieId), contextMenu, context)
         : null;
   }

   @Override
   public Object getCookie(int id) {
      return super._cookieIDs != null && super._cookieIDs.containsKey(id) && id >= 0 && super._cookieIDs.get(id) != null ? this.getCookieInternal(id) : null;
   }

   private Object getCookieInternal(int id) {
      WAPPushModel message = (WAPPushModel)WAPPushViewerScreen.access$000(this.this$0);
      Object context = new Object();
      ContextObject.put(context, 253, this.getText().substring(this.getRunStart(), this.getRunEnd()));
      String configUID = null;
      String preferredConfigUID = message.getPreferredConfigUID();
      int preferredConfigType = message.getPreferredConfigType();
      String preferredTransportCID = message.getPreferredTransportCID();
      if (preferredConfigUID == null
         && preferredConfigType == 0
         && preferredTransportCID != null
         && StringUtilities.strEqualIgnoreCase(preferredTransportCID, "WAP", 1701707776)) {
         configUID = GeneralProperty.getDefaultWapBrowserConfigServiceUID();
      } else {
         BrowserConfigRecord record = BrowserConfigRecord.getDecodedConfig(preferredConfigUID, preferredConfigType, preferredTransportCID);
         if (record != null) {
            configUID = record.getUid();
         }
      }

      if (configUID != null) {
         ContextObject.put(context, 867508017068302662L, configUID);
      }

      long[] ids = (long[])super._cookieIDs.get(id);
      Object[] cookies = new Object[ids.length];

      for (int i = 0; i < ids.length; i++) {
         cookies[i] = FactoryUtil.createInstance(ids[i], context);
      }

      return cookies;
   }
}
