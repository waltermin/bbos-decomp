package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.browser.wap.WAPServiceRecord;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;

class TransportConfigProperty {
   protected boolean _restrictedAccess;

   public TransportConfigProperty(boolean restrictedAccess) {
      this._restrictedAccess = restrictedAccess;
   }

   public VerticalFieldManager addFields(VerticalFieldManager _1, boolean _2, String _3, String _4, int _5, int _6, String _7) {
      throw null;
   }

   public void saveProperty(String _1, String _2) {
      throw null;
   }

   public static TransportConfigProperty getInstance(boolean restrictedAccess, String transportCid, String transportUid) {
      if (StringUtilities.strEqualIgnoreCase(transportCid, WAPServiceRecord.SERVICE_CID, 1701707776)) {
         return new WAPTransportConfigProperty(restrictedAccess);
      } else {
         return StringUtilities.strEqualIgnoreCase(transportCid, WPTCPServiceRecord.SERVICE_CID, 1701707776)
            ? new WPTCPTransportConfigProperty(restrictedAccess)
            : null;
      }
   }
}
