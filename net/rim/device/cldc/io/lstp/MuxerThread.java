package net.rim.device.cldc.io.lstp;

import javax.microedition.io.Connector;
import net.rim.device.api.io.DatagramBase;

class MuxerThread extends Thread implements LstpListener {
   protected NativeLayer _nativeLayer;
   protected Protocol _conn;

   protected void configureLink() {
   }

   protected void expectLogin() {
      throw null;
   }

   protected void sendChallenge() {
      throw null;
   }

   protected void expectChallengeResponse() {
   }

   @Override
   public void lstpLinkStateChanged(boolean linkState) {
      if (!linkState) {
         LstpUtil.getInstance().removeListener(this);

         try {
            this._conn.close();
         } finally {
            return;
         }
      }
   }

   @Override
   public void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/rim/device/cldc/io/lstp/MuxerThread.expectLogin ()V
      // 04: aload 0
      // 05: invokevirtual net/rim/device/cldc/io/lstp/MuxerThread.sendChallenge ()V
      // 08: aload 0
      // 09: invokevirtual net/rim/device/cldc/io/lstp/MuxerThread.expectChallengeResponse ()V
      // 0c: aload 0
      // 0d: invokespecial net/rim/device/cldc/io/lstp/MuxerThread.sendAppInfo ()V
      // 10: aload 0
      // 11: invokevirtual net/rim/device/cldc/io/lstp/MuxerThread.configureLink ()V
      // 14: sipush 500
      // 17: i2l
      // 18: invokestatic java/lang/Thread.sleep (J)V
      // 1b: invokestatic net/rim/device/cldc/io/lstp/LstpUtil.getInstance ()Lnet/rim/device/cldc/io/lstp/LstpUtil;
      // 1e: bipush 1
      // 1f: bipush 0
      // 20: invokevirtual net/rim/device/cldc/io/lstp/LstpUtil.setLinkState (ZZ)V
      // 23: ldc2_w -754053862978797267
      // 26: ldc_w 1280209508
      // 29: bipush 4
      // 2b: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 2e: pop
      // 2f: aload 0
      // 30: invokespecial net/rim/device/cldc/io/lstp/MuxerThread.expectStandby ()V
      // 33: goto 2f
      // 36: astore 1
      // 37: ldc2_w -754053862978797267
      // 3a: ldc_w 1297642340
      // 3d: bipush 4
      // 3f: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 42: pop
      // 43: return
      // 44: astore 1
      // 45: aload 0
      // 46: getfield net/rim/device/cldc/io/lstp/MuxerThread._nativeLayer Lnet/rim/device/cldc/io/lstp/NativeLayer;
      // 49: bipush 0
      // 4a: invokevirtual net/rim/device/cldc/io/lstp/NativeLayer.close (Z)V
      // 4d: return
      // try (0 -> 25): 25 null
      // try (0 -> 25): 32 null
   }

   protected MuxerThread(NativeLayer nativeLayer) {
      this._nativeLayer = nativeLayer;
      this._conn = (Protocol)Connector.open("lstp:muxer");
      LstpUtil.getInstance().addListener(this);
   }

   private void sendAppInfo() {
      DatagramBase dgram = (DatagramBase)this._conn.newDatagram();
      String[] appNames = LstpUtil.getInstance().getAppNames();
      int size = appNames.length;

      for (int i = 0; i < size; i++) {
         dgram.writeByte(appNames[i].length());
         dgram.writeByte(i);
         dgram.write(appNames[i].getBytes());
      }

      this._conn.send(dgram);
   }

   private void expectStandby() {
      this._conn.receive(this._conn.newDatagram());
   }
}
