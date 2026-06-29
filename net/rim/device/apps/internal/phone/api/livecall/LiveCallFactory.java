package net.rim.device.apps.internal.phone.api.livecall;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;

public interface LiveCallFactory {
   LiveCall createInstance(PhoneCallInitialData var1, Object var2);

   Application getApplication();
}
