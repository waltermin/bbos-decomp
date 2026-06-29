package net.rim.device.cldc.io.udp;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.cldc.io.utility.MalformedURLException;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.vm.TraceBack;

public class MIDletDatagram extends DatagramBase {
   private boolean initcomplete = true;
   private static final String DATAGRAMSCHEME;

   MIDletDatagram(byte[] buffer, int offset, int length, String address) {
      super(buffer, offset, length, address);
   }

   @Override
   public String getAddress() {
      String a = super.getAddress();
      int caller = TraceBack.getCallingModule(0);
      if (CodeModuleManager.isMidlet(caller) && a != null && !a.startsWith("datagram:")) {
         a = "datagram:" + a;
      }

      return a;
   }

   @Override
   protected DatagramAddressBase newAddressBase() {
      return new UdpAddress();
   }

   @Override
   public void setAddress(String addr) {
      if (this.initcomplete) {
         try {
            new URL(addr);
         } catch (MalformedURLException mue) {
            throw new IllegalArgumentException(mue.toString());
         }
      }

      if (addr != null && addr.startsWith("datagram:")) {
         addr = addr.substring(9);
      }

      super.setAddress(addr);
      UdpAddress udpaddr = (UdpAddress)super._addressBase;
      if (udpaddr != null && udpaddr.getApn() == null) {
         udpaddr.setApn(TunnelCredentialsProvider.getInstance().getApn());
      }
   }

   @Override
   public void setData(byte[] buffer, int offset, int length) {
      if (buffer == null || offset < buffer.length && offset + length <= buffer.length && offset + length >= 0) {
         super.setData(buffer, offset, length);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void setLength(int length) {
      if (length > this.getArray().length - this.getArrayStart()) {
         throw new IllegalArgumentException();
      }

      super.setLength(length);
   }

   @Override
   public void setAddress(Datagram d) {
      String a = d.getAddress();
      int caller = TraceBack.getCallingModule(0);
      if (CodeModuleManager.isMidlet(caller) && a != null && !a.startsWith("datagram:")) {
         a = "datagram:" + a;
      }

      this.setAddress(a);
   }

   @Override
   public void reset() {
      byte[] buf = this.getArray();
      super.reset();
      this.setData(buf, 0, 0, this.isBigEndian());
   }
}
