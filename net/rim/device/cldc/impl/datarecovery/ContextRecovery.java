package net.rim.device.cldc.impl.datarecovery;

import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.cldc.io.datarecovery.DataRecoveryListener;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelApnList;
import net.rim.device.cldc.io.tunnel.TunnelApnListFactory;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.cldc.io.tunnel.TunnelFactory;

public final class ContextRecovery implements DataRecoveryListener, ContextRecoveryEvent {
   ContextRecovery() {
      EventLogger.register(8278020679766839009L, "net.rim.conrec", 2);
      EventLogger.logEvent(8278020679766839009L, 1231972724, 0);
      DataRecovery.getInstance().addListener(this);
   }

   @Override
   public final void dataRecoveryEventOccurred(int event, int linkType) {
      HostRoutingInfo hri = null;
      if (event == 2) {
         EventLogger.logEvent(8278020679766839009L, 1129210745, 3);
         TunnelConfig tunnelConfig = null;
         switch (RadioInfo.getNetworkType()) {
            case 3:
            case 7:
               hri = HRUtils.getDefaultHRT().getActiveHri();
               if (hri != null && hri instanceof Object) {
                  GprsHRI gprsHri = (GprsHRI)hri;
                  tunnelConfig = (TunnelConfig)(new Object(
                     gprsHri.getApn(), "net.rim.conrec", gprsHri.getQos(), gprsHri.getApnUsername(), gprsHri.getApnPassword(), null
                  ));
               }
               break;
            default:
               tunnelConfig = (TunnelConfig)(new Object("", "net.rim.conrec", null));
         }

         TunnelApnList tunnelApnList = TunnelApnListFactory.getTunnelApnListFactory().createTunnelApnList();
         tunnelApnList.initializeList(hri);
         if (tunnelConfig != null) {
            try {
               while (tunnelApnList.getSize() > 0) {
                  tunnelConfig.setName(tunnelApnList.getFirst());
                  Tunnel tunnel = TunnelFactory.openTunnel(tunnelConfig);
                  tunnel.reset();
                  tunnel.close();
                  Tunnel var10 = null;
                  tunnelApnList.removeFirst();
               }
            } finally {
               return;
            }
         }
      }
   }
}
