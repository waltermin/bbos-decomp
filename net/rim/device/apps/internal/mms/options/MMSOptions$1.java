package net.rim.device.apps.internal.mms.options;

import net.rim.device.internal.system.SystemPropertyProvider;

class MMSOptions$1 implements SystemPropertyProvider {
   @Override
   public String getProperty(String property) {
      return property.equals("wireless.messaging.mms.mmsc") ? MMSTransportServiceBook.getMMSCUrl() : null;
   }
}
