package net.rim.device.cldc.impl.hrt;

import net.rim.device.api.hrt.HostRoutingInfo;

final class HRTRequestThread$RegistrationInfo {
   public HostRoutingInfo[] defaultHris;
   public HostRoutingInfo[] regHris;
   public int refId;
   public int dynamicPIN;
   public int pendingLifetimeDynPIN;
   public int timeToLive;
   int regError;
}
