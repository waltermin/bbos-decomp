package net.rim.device.cldc.impl.hrt;

import net.rim.device.api.system.QOSInfo;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.cldc.io.tunnel.TunnelFactory;
import net.rim.device.cldc.io.tunnel.TunnelListener;

final class HRTRequestThread$ConnInfo implements TunnelListener {
   public int apnId = -1;
   public int connId = -1;
   public Tunnel tunnel;
   private boolean _apnIdValid;
   public static final int NULL = -1;

   public HRTRequestThread$ConnInfo() {
   }

   public final int open(String apn, QOSInfo qos, String apnUsername, String apnPassword) {
      if (this.tunnel == null) {
         this._apnIdValid = false;
         this.tunnel = TunnelFactory.openTunnel(new TunnelConfig(apn, "net.rim.hrtRT", qos, apnUsername, apnPassword, this));
         synchronized (this) {
            if (!this._apnIdValid) {
               try {
                  this.wait();
               } finally {
                  return this.tunnel.getIdentifier();
               }
            }
         }
      }

      return this.tunnel.getIdentifier();
   }

   @Override
   public final void statusChanged(int status, int code) {
      synchronized (this) {
         if (!this._apnIdValid) {
            switch (status) {
               case 2:
                  this._apnIdValid = true;
                  this.notify();
            }
         }
      }
   }
}
