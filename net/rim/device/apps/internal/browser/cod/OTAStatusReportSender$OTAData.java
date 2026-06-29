package net.rim.device.apps.internal.browser.cod;

import java.util.Hashtable;
import net.rim.device.api.util.Persistable;

final class OTAStatusReportSender$OTAData implements Persistable {
   public Hashtable _sendQueue = new Hashtable();
   public Hashtable _deleteNotifyApps = new Hashtable();

   public OTAStatusReportSender$OTAData() {
   }
}
