package net.rim.device.internal.deviceoptions;

import net.rim.device.api.system.GlobalEventListener;

public interface OptionsProviderGlobalEventListener extends GlobalEventListener {
   long[] getGlobalEventUids();
}
