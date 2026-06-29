package net.rim.device.internal.io.tunnel;

import java.io.IOException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelApnList;
import net.rim.device.cldc.io.tunnel.TunnelApnListFactory;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.cldc.io.tunnel.TunnelFactory;
import net.rim.device.cldc.io.tunnel.TunnelListener;

public final class TunnelWorker implements TunnelListener {
   private int _tunnelState;
   private int _tunnelCode;
   private static final int TUNNEL_OPEN_TIMEOUT = 30000;

   public final Tunnel open(TunnelConfig config) throws IOException {
      this._tunnelState = 0;
      this._tunnelCode = 0;
      TunnelApnList tunnelApnList = TunnelApnListFactory.getTunnelApnListFactory().createTunnelApnList();
      tunnelApnList.initializeList(null);
      if ((RadioInfo.getActiveWAFs() & 1) != 0) {
         tunnelApnList.addLast(config.getName());
      } else {
         tunnelApnList.addFirst(config.getName());
      }

      Tunnel tunnel = null;

      while (tunnelApnList.getSize() > 0) {
         config.setName(tunnelApnList.getFirst());
         if (StringUtilities.strEqual(config.getName(), "") && (RadioInfo.getActiveWAFs() & 1) != 0) {
            throw new IOException("Invalid tunnel name");
         }

         long quitTime = System.currentTimeMillis() + 30000;
         tunnel = TunnelFactory.openTunnel(config);

         try {
            int tunnelState;
            do {
               synchronized (this) {
                  tunnelState = this.getTunnelState();
                  if (tunnelState != 3) {
                     this.timeout(quitTime);

                     try {
                        this.wait(30000);
                     } catch (InterruptedException var10) {
                     }

                     tunnelState = this.getTunnelState();
                  }
               }

               if (tunnelState == 3) {
                  tunnelApnList = null;
                  return tunnel;
               }
            } while (tunnelState != 4);

            tunnel.close();
            tunnelApnList.removeFirst();
         } catch (IOException ioe) {
            if (tunnel != null) {
               tunnel.close();
            }

            if (tunnelApnList.getSize() <= 1) {
               TunnelApnList var13 = null;
               throw ioe;
            }

            tunnelApnList.removeFirst();
         }
      }

      TunnelApnList var14 = null;
      throw new IOException(this.getTunnelCode() == 1 ? "Open tunnel - max attempts" : "Open tunnel - failure");
   }

   private final void timeout(long quitTime) throws IOException {
      if (System.currentTimeMillis() > quitTime) {
         throw new IOException("Open tunnel - max timeout");
      }
   }

   private final synchronized int getTunnelState() {
      return this._tunnelState;
   }

   private final synchronized int getTunnelCode() {
      return this._tunnelCode;
   }

   @Override
   public final void statusChanged(int status, int code) {
      synchronized (this) {
         this._tunnelState = status;
         this._tunnelCode = code;
         this.notify();
      }
   }
}
