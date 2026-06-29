package net.rim.device.cldc.io.gme;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.cldc.io.mdp.MdpAddress;
import net.rim.device.internal.system.DataServices;

public final class MdpBridge extends GmeRouterConnection {
   private HostRoutingTable _defHrt;
   private DataServices _dataServices;
   private static final int MDP_DATA_PORT = 2;
   private static final int CAPABILITIES = 63;

   protected MdpBridge(Transport transport) {
      super(transport, (DatagramConnectionBase)Connector.open(MdpAddress.makeAddress(true, (String)((Object)null), 2, 2)));
      super._subConnection.setup(1462989747, null);
      this._defHrt = HRUtils.getDefaultHRT();
      this._dataServices = DataServices.getInstance();
      ServiceRouting.getInstance().addInterface(new ServiceRoutingProperties(ServiceRoutingProperties.MDP, 1, 6, 2, 2, 63));
   }

   @Override
   public final boolean isSendChoked() {
      return this._defHrt.getActiveHri() == null || !this._dataServices.isDataServicesEnabled();
   }

   private final void findRoutingInformation(HostRoutingTable hrt, GMEDatagramInfo di) {
      HostRoutingInfo hri = hrt.getActiveHri();
      if (hri == null) {
         if (!di.failWhenMissing && (hrt == this._defHrt || HRUtils.getNpcForActiveNetwork() == -1)) {
            Transport.logWarning(1414090098);
            throw new Object();
         }

         super._transport.throwIOException(di, 1414090344, 4226);
      }

      if (di.dstHri == null) {
         di.dstHri = hri;
      } else if (!hri.hasEquivalentDestination(di.dstHri)) {
         super._transport.throwIOException(di, 1414090084, 4228);
      } else {
         hri.sendSuccessful();
      }
   }

   @Override
   public final void send(DatagramBase param1, GMEDatagramInfo param2, Datagram param3) throws IOException, IOCancelledException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/cldc/io/gme/MdpBridge._dataServices Lnet/rim/device/internal/system/DataServices;
      // 004: invokevirtual net/rim/device/internal/system/DataServices.isDataServicesEnabled ()Z
      // 007: ifne 01d
      // 00a: aload 0
      // 00b: getfield net/rim/device/cldc/io/gme/GmeRouterConnection._transport Lnet/rim/device/cldc/io/gme/Transport;
      // 00e: pop
      // 00f: ldc_w 1414090098
      // 012: invokestatic net/rim/device/cldc/io/gme/Transport.logWarning (I)V
      // 015: new java/lang/Object
      // 018: dup
      // 019: invokespecial net/rim/device/api/io/IONotRoutableException.<init> ()V
      // 01c: athrow
      // 01d: aload 2
      // 01e: getfield net/rim/device/cldc/io/gme/GMEDatagramInfo.address Lnet/rim/device/cldc/io/gme/GMEAddress;
      // 021: astore 4
      // 023: aload 4
      // 025: invokevirtual net/rim/device/cldc/io/gme/GMEAddress.getNumTargets ()I
      // 028: istore 5
      // 02a: iload 5
      // 02c: ifne 03b
      // 02f: aload 0
      // 030: aload 0
      // 031: getfield net/rim/device/cldc/io/gme/MdpBridge._defHrt Lnet/rim/device/api/hrt/HostRoutingTable;
      // 034: aload 2
      // 035: invokespecial net/rim/device/cldc/io/gme/MdpBridge.findRoutingInformation (Lnet/rim/device/api/hrt/HostRoutingTable;Lnet/rim/device/cldc/io/gme/GMEDatagramInfo;)V
      // 038: goto 0a3
      // 03b: bipush 0
      // 03c: istore 6
      // 03e: iload 6
      // 040: iload 5
      // 042: if_icmpge 0a3
      // 045: aload 4
      // 047: iload 6
      // 049: invokevirtual net/rim/device/cldc/io/gme/GMEAddress.getTarget (I)Lnet/rim/device/cldc/io/gme/GMETarget;
      // 04c: astore 7
      // 04e: aload 7
      // 050: getfield net/rim/device/cldc/io/gme/GMETarget.type I
      // 053: tableswitch 25 0 2 74 25 65
      // 06c: aload 2
      // 06d: iload 6
      // 06f: invokevirtual net/rim/device/cldc/io/gme/GMEDatagramInfo.getServiceRecord (I)Lnet/rim/device/api/servicebook/ServiceRecord;
      // 072: astore 8
      // 074: aload 8
      // 076: ifnull 084
      // 079: aload 8
      // 07b: invokevirtual net/rim/device/api/servicebook/ServiceRecord.getAttachedHrt ()Lnet/rim/device/api/hrt/HostRoutingTable;
      // 07e: dup
      // 07f: astore 9
      // 081: ifnonnull 08a
      // 084: aload 0
      // 085: getfield net/rim/device/cldc/io/gme/MdpBridge._defHrt Lnet/rim/device/api/hrt/HostRoutingTable;
      // 088: astore 9
      // 08a: aload 0
      // 08b: aload 9
      // 08d: aload 2
      // 08e: invokespecial net/rim/device/cldc/io/gme/MdpBridge.findRoutingInformation (Lnet/rim/device/api/hrt/HostRoutingTable;Lnet/rim/device/cldc/io/gme/GMEDatagramInfo;)V
      // 091: goto 09d
      // 094: aload 0
      // 095: aload 0
      // 096: getfield net/rim/device/cldc/io/gme/MdpBridge._defHrt Lnet/rim/device/api/hrt/HostRoutingTable;
      // 099: aload 2
      // 09a: invokespecial net/rim/device/cldc/io/gme/MdpBridge.findRoutingInformation (Lnet/rim/device/api/hrt/HostRoutingTable;Lnet/rim/device/cldc/io/gme/GMEDatagramInfo;)V
      // 09d: iinc 6 1
      // 0a0: goto 03e
      // 0a3: aload 2
      // 0a4: getfield net/rim/device/cldc/io/gme/GMEDatagramInfo.dstHri Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 0a7: ifnonnull 0b8
      // 0aa: aload 0
      // 0ab: getfield net/rim/device/cldc/io/gme/GmeRouterConnection._transport Lnet/rim/device/cldc/io/gme/Transport;
      // 0ae: aload 2
      // 0af: ldc_w 1414090340
      // 0b2: sipush 4233
      // 0b5: invokevirtual net/rim/device/cldc/io/gme/Transport.throwIOException (Lnet/rim/device/cldc/io/gme/GMEDatagramInfo;II)V
      // 0b8: aload 0
      // 0b9: getfield net/rim/device/cldc/io/gme/GmeRouterConnection._transport Lnet/rim/device/cldc/io/gme/Transport;
      // 0bc: pop
      // 0bd: ldc_w 1415074675
      // 0c0: invokestatic net/rim/device/cldc/io/gme/Transport.logDebugInfo (I)V
      // 0c3: aload 1
      // 0c4: new net/rim/device/cldc/io/mdp/MdpAddress
      // 0c7: dup
      // 0c8: aload 2
      // 0c9: getfield net/rim/device/cldc/io/gme/GMEDatagramInfo.dstHri Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 0cc: invokevirtual net/rim/device/api/hrt/HostRoutingInfo.getAddressBase ()Lnet/rim/device/api/io/DatagramAddressBase;
      // 0cf: bipush 2
      // 0d1: bipush 2
      // 0d3: invokespecial net/rim/device/cldc/io/mdp/MdpAddress.<init> (Lnet/rim/device/api/io/DatagramAddressBase;II)V
      // 0d6: invokevirtual net/rim/device/api/io/DatagramBase.setAddressBase (Lnet/rim/device/api/io/DatagramAddressBase;)V
      // 0d9: aload 0
      // 0da: getfield net/rim/device/cldc/io/gme/GmeRouterConnection._transport Lnet/rim/device/cldc/io/gme/Transport;
      // 0dd: aload 0
      // 0de: getfield net/rim/device/cldc/io/gme/GmeRouterConnection._subConnection Lnet/rim/device/api/io/DatagramConnectionBase;
      // 0e1: aload 1
      // 0e2: aload 2
      // 0e3: getfield net/rim/device/cldc/io/gme/GMEDatagramInfo.listener Lnet/rim/device/api/io/DatagramStatusListener;
      // 0e6: aload 2
      // 0e7: getfield net/rim/device/cldc/io/gme/GMEDatagramInfo.transId I
      // 0ea: aload 3
      // 0eb: invokevirtual net/rim/device/cldc/io/gme/Transport.subSend (Lnet/rim/device/api/io/DatagramConnectionBase;Ljavax/microedition/io/Datagram;Lnet/rim/device/api/io/DatagramStatusListener;ILjavax/microedition/io/Datagram;)V
      // 0ee: aload 2
      // 0ef: getfield net/rim/device/cldc/io/gme/GMEDatagramInfo.dstHri Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 0f2: invokevirtual net/rim/device/api/hrt/HostRoutingInfo.sendSuccessful ()V
      // 0f5: aload 2
      // 0f6: bipush -1
      // 0f8: putfield net/rim/device/cldc/io/gme/GMEDatagramInfo.errorEventCode I
      // 0fb: aload 2
      // 0fc: aconst_null
      // 0fd: putfield net/rim/device/cldc/io/gme/GMEDatagramInfo.errorEventContext Ljava/lang/Object;
      // 100: return
      // 101: astore 6
      // 103: aload 6
      // 105: athrow
      // 106: astore 6
      // 108: aload 0
      // 109: getfield net/rim/device/cldc/io/gme/GmeRouterConnection._transport Lnet/rim/device/cldc/io/gme/Transport;
      // 10c: pop
      // 10d: ldc_w 1415074680
      // 110: invokestatic net/rim/device/cldc/io/gme/Transport.logWarning (I)V
      // 113: aload 2
      // 114: ifnull 12f
      // 117: aload 2
      // 118: getfield net/rim/device/cldc/io/gme/GMEDatagramInfo.dstHri Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 11b: aload 2
      // 11c: dup
      // 11d: getfield net/rim/device/cldc/io/gme/GMEDatagramInfo.retryCount I
      // 120: bipush 1
      // 121: iadd
      // 122: dup_x1
      // 123: putfield net/rim/device/cldc/io/gme/GMEDatagramInfo.retryCount I
      // 126: invokevirtual net/rim/device/api/hrt/HostRoutingInfo.handleSendError (I)Z
      // 129: ifeq 12f
      // 12c: goto 0b8
      // 12f: aload 0
      // 130: getfield net/rim/device/cldc/io/gme/GmeRouterConnection._transport Lnet/rim/device/cldc/io/gme/Transport;
      // 133: pop
      // 134: ldc_w 1415073380
      // 137: invokestatic net/rim/device/cldc/io/gme/Transport.logError (I)V
      // 13a: aload 6
      // 13c: athrow
      // try (74 -> 109): 110 null
      // try (74 -> 109): 113 null
   }

   @Override
   public final void receivedFrom(GMEDatagramInfo dgInfo) {
      HostRoutingTable hrt = null;
      HostRoutingInfo hri = null;
      if (dgInfo.boundSr != null) {
         hrt = dgInfo.boundSr.getAttachedHrt();
      }

      if (hrt == null) {
         hrt = this._defHrt;
      }

      hri = hrt.getActiveHri();
      DatagramAddressBase addressBase = dgInfo.srcAddr.getSubAddressBase();
      if (hri != null && addressBase != null) {
         hri.rcvdFromAddress(addressBase);
      }
   }
}
