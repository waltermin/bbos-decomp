package net.rim.device.cldc.impl.services;

import net.rim.device.cldc.impl.gcmp.Gcmp;
import net.rim.device.cldc.impl.network.Registration;
import net.rim.device.cldc.io.fastdormancy.FastDormancyManager;

public final class ServiceStartup {
   public static final void main(String[] args) {
      Registration.NetworkMain(args);
      net.rim.device.cldc.impl.tunnel.Registration.TunnelMain(args);
      FastDormancyManager.FastDormancyMain(args);
      net.rim.device.cldc.impl.datarecovery.Registration.DataRecoveryMain(args);
      Gcmp.getInstance();
      net.rim.device.cldc.impl.hrt.Registration.HRTMain(args);
      net.rim.device.cldc.impl.sb.Registration.SBMain(args);
      net.rim.device.cldc.impl.ipmodem.Registration.ModemMain(args);
   }
}
