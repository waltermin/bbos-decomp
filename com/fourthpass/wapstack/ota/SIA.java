package com.fourthpass.wapstack.ota;

import com.fourthpass.wapstack.util.InetAddress;
import com.fourthpass.wapstack.util.VarLengthInt;
import com.fourthpass.wapstack.wsp.WSPAddress;
import java.util.Vector;
import net.rim.device.api.browser.push.PushEventLogger;
import net.rim.device.api.hrt.DAC;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.waphttp.WAPConnectionParams;
import net.rim.device.internal.browser.wap.WAPServiceRecord;

public final class SIA implements PushEventLogger {
   private InetAddress _wapServerAdd;
   private static final boolean DEBUG_ENABLED;

   public final void processSIAContent(byte[] siaContent) {
      EventLogger.logEvent(-1133226195824034738L, 1399415396, 5);
      this.decode(siaContent);
      EventLogger.logEvent(-1133226195824034738L, 1399415140, 5);
      EventLogger.logEvent(-1133226195824034738L, 1399415411, 5);
      WAPConnectionParams params = this.prepareConnectionParams();
      if (params != null) {
         this.makeConnection(params);
      }

      EventLogger.logEvent(-1133226195824034738L, 1399415155, 5);
   }

   public final void close() {
   }

   private final void makeConnection(WAPConnectionParams param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: new net/rim/device/cldc/io/waphttp/WAPRequestImpl
      // 03: dup
      // 04: new java/lang/Object
      // 07: dup
      // 08: ldc_w "rim://openconnection"
      // 0b: invokespecial net/rim/device/cldc/io/utility/URL.<init> (Ljava/lang/String;)V
      // 0e: aload 1
      // 0f: invokespecial net/rim/device/cldc/io/waphttp/WAPRequestImpl.<init> (Lnet/rim/device/cldc/io/utility/URL;Lnet/rim/device/cldc/io/waphttp/WAPConnectionParams;)V
      // 12: astore 2
      // 13: new java/lang/Object
      // 16: dup
      // 17: ldc_w "application/vnd.wap.sic"
      // 1a: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 1d: astore 3
      // 1e: aload 3
      // 1f: bipush 44
      // 21: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 24: pop
      // 25: aload 3
      // 26: ldc_w "application/vnd.wap.slc"
      // 29: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2c: pop
      // 2d: aload 3
      // 2e: bipush 44
      // 30: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 33: pop
      // 34: aload 3
      // 35: ldc_w "application/vnd.wap.coc"
      // 38: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3b: pop
      // 3c: aload 3
      // 3d: bipush 44
      // 3f: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 42: pop
      // 43: aload 3
      // 44: ldc_w "application/vnd.wap.wmlc"
      // 47: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 4a: pop
      // 4b: aload 2
      // 4c: ldc_w "Accept"
      // 4f: aload 3
      // 50: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 53: invokevirtual net/rim/device/cldc/io/http/HttpProtocolBase.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 56: aload 2
      // 57: invokevirtual net/rim/device/cldc/io/http/HttpProtocolBase.getResponseCode ()I
      // 5a: pop
      // 5b: return
      // 5c: astore 2
      // 5d: return
      // 5e: astore 2
      // 5f: return
      // try (0 -> 46): 47 null
      // try (0 -> 46): 49 null
   }

   private final WAPConnectionParams prepareConnectionParams() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid(WAPServiceRecord.SERVICE_CID);
      WAPConnectionParams params = (WAPConnectionParams)(new Object());
      byte[] destAddressBytes = this._wapServerAdd.getAddress();
      if (destAddressBytes != null && destAddressBytes.length == 4) {
         int destAddress = (destAddressBytes[0] & 255) << 24 | (destAddressBytes[1] & 255) << 16 | (destAddressBytes[2] & 255) << 8 | destAddressBytes[3] & 255;

         for (ServiceRecord tempRec : records) {
            HostRoutingTable hrt = tempRec.getAttachedHrt();
            if (hrt != null) {
               HostRoutingInfo hri = hrt.getHris()[0];
               DAC dac = hri.getDac();
               if (dac instanceof Object) {
                  long[] addresses = ((IPv4UdpDAC)dac).getAddresses();
                  if (addresses != null && addresses.length != 0) {
                     for (int j = 0; j < addresses.length; j++) {
                        int remoteIP = IPv4UdpDAC.addr2IpAddress(addresses[j]);
                        if (remoteIP == destAddress && params.loadFrom(tempRec.getUid())) {
                           return params;
                        }
                     }
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private final void decode(byte[] siaContent) {
      int pos = 0;
      int appIdListLen = (int)VarLengthInt.decode(siaContent, ++pos);
      int appIdListLenCount = VarLengthInt.getVarLengthCount(siaContent, pos);
      pos += appIdListLenCount;
      byte[] appIdList = new byte[appIdListLen];
      System.arraycopy(siaContent, pos, appIdList, 0, appIdListLen);

      for (int i = 0; i < appIdListLen; i++) {
         if ((appIdList[i] & 128) != 0) {
            appIdList[i] = (byte)(appIdList[i] & 127);
         }
      }

      pos += appIdListLen;
      int contactPointsLen = (int)VarLengthInt.decode(siaContent, pos);
      int contactPointsLenCount = VarLengthInt.getVarLengthCount(siaContent, pos);
      pos += contactPointsLenCount;
      byte[] addressData = new byte[contactPointsLen];
      System.arraycopy(siaContent, pos, addressData, 0, contactPointsLen);
      WSPAddress[] wspAddresses = this.getAddress(addressData);
      this._wapServerAdd = wspAddresses[0].getInetAddress();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final WSPAddress[] getAddress(byte[] addressData) {
      int pos = 0;
      Vector store = (Vector)(new Object());

      while (pos < addressData.length) {
         int addrLen = addressData[pos] & 63;
         byte[] data = new byte[addrLen];

         try {
            WSPAddress add;
            if ((addressData[pos] & 128) != 128) {
               System.arraycopy(addressData, pos, data, 0, addrLen);
               add = new WSPAddress(data);
            } else if ((addressData[pos++] & 64) == 64) {
               System.arraycopy(addressData, pos + 3, data, 0, addrLen);
               byte bearer = addressData[pos++];
               byte temp = addressData[pos++];
               int port;
               if (temp > 0) {
                  port = temp;
               } else {
                  port = 256 + temp;
               }

               port <<= 8;
               temp = addressData[pos++];
               if (temp > 0) {
                  port += temp;
               } else {
                  port = port + 256 + temp;
               }

               add = new WSPAddress(bearer, port, data);
            } else {
               System.arraycopy(addressData, pos + 1, data, 0, addrLen);
               add = new WSPAddress(addressData[pos++], data);
            }

            store.addElement(add);
            pos += addrLen;
         } catch (Throwable var12) {
            ex.printStackTrace();
            continue;
         }
      }

      WSPAddress[] addresses = new WSPAddress[store.size()];

      for (int i = 0; i < store.size(); i++) {
         addresses[i] = (WSPAddress)store.elementAt(i);
      }

      return addresses;
   }
}
