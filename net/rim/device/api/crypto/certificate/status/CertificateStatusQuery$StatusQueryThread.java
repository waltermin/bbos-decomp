package net.rim.device.api.crypto.certificate.status;

import javax.microedition.io.StreamConnection;
import net.rim.device.cldc.io.ippp.StreamProtocolListener;

class CertificateStatusQuery$StatusQueryThread extends Thread implements StreamProtocolListener {
   private CertificateStatusProvider[] _compatibleProviders;
   private StreamConnection _connection;
   private int _currentState;
   private Object _stateLock;
   private final CertificateStatusQuery this$0;

   public CertificateStatusQuery$StatusQueryThread(CertificateStatusQuery _1, CertificateStatusProvider[] compatibleProviders) {
      this.this$0 = _1;
      this._compatibleProviders = compatibleProviders;
      this._connection = null;
      this._currentState = 0;
      this._stateLock = new Object();
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
      // 000: bipush 9
      // 002: istore 1
      // 003: aload 0
      // 004: bipush 1
      // 005: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.progressUpdate (I)V
      // 008: new java/lang/Object
      // 00b: dup
      // 00c: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 00f: astore 2
      // 010: aload 0
      // 011: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.this$0 Lnet/rim/device/api/crypto/certificate/status/CertificateStatusQuery;
      // 014: aload 0
      // 015: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread._compatibleProviders [Lnet/rim/device/api/crypto/certificate/status/CertificateStatusProvider;
      // 018: new java/lang/Object
      // 01b: dup
      // 01c: aload 2
      // 01d: invokespecial java/io/DataOutputStream.<init> (Ljava/io/OutputStream;)V
      // 020: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery.encodeRequest ([Lnet/rim/device/api/crypto/certificate/status/CertificateStatusProvider;Ljava/io/DataOutputStream;)Z
      // 023: ifne 02d
      // 026: aload 0
      // 027: bipush 6
      // 029: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 02c: return
      // 02d: aload 2
      // 02e: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 031: astore 3
      // 032: aload 0
      // 033: bipush 2
      // 035: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.progressUpdate (I)V
      // 038: aconst_null
      // 039: astore 4
      // 03b: aconst_null
      // 03c: astore 5
      // 03e: new java/lang/Object
      // 041: dup
      // 042: ldc_w "ocsp:"
      // 045: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 048: astore 6
      // 04a: aload 0
      // 04b: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.this$0 Lnet/rim/device/api/crypto/certificate/status/CertificateStatusQuery;
      // 04e: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._serviceUID Ljava/lang/String;
      // 051: ifnull 06a
      // 054: aload 6
      // 056: ldc_w ";connectionuid="
      // 059: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 05c: pop
      // 05d: aload 6
      // 05f: aload 0
      // 060: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.this$0 Lnet/rim/device/api/crypto/certificate/status/CertificateStatusQuery;
      // 063: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._serviceUID Ljava/lang/String;
      // 066: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 069: pop
      // 06a: aload 6
      // 06c: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 06f: bipush 0
      // 070: bipush 1
      // 071: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;IZ)Ljavax/microedition/io/Connection;
      // 074: checkcast java/lang/Object
      // 077: astore 7
      // 079: aload 7
      // 07b: dup
      // 07c: instanceof java/lang/Object
      // 07f: ifne 086
      // 082: pop
      // 083: goto 08d
      // 086: checkcast java/lang/Object
      // 089: aload 0
      // 08a: invokevirtual net/rim/device/cldc/io/ippp/StreamProtocol.setStreamProtocolListener (Lnet/rim/device/cldc/io/ippp/StreamProtocolListener;)V
      // 08d: aload 0
      // 08e: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread._stateLock Ljava/lang/Object;
      // 091: dup
      // 092: astore 8
      // 094: monitorenter
      // 095: aload 0
      // 096: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread._currentState I
      // 099: bipush 9
      // 09b: if_icmpne 0a9
      // 09e: aload 7
      // 0a0: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0a5: aload 8
      // 0a7: monitorexit
      // 0a8: return
      // 0a9: aload 0
      // 0aa: aload 7
      // 0ac: putfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread._connection Ljavax/microedition/io/StreamConnection;
      // 0af: aload 8
      // 0b1: monitorexit
      // 0b2: goto 0bd
      // 0b5: astore 9
      // 0b7: aload 8
      // 0b9: monitorexit
      // 0ba: aload 9
      // 0bc: athrow
      // 0bd: new java/lang/Object
      // 0c0: dup
      // 0c1: aload 0
      // 0c2: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread._connection Ljavax/microedition/io/StreamConnection;
      // 0c5: invokeinterface javax/microedition/io/OutputConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 0ca: invokespecial java/io/DataOutputStream.<init> (Ljava/io/OutputStream;)V
      // 0cd: astore 5
      // 0cf: new java/lang/Object
      // 0d2: dup
      // 0d3: aload 0
      // 0d4: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread._connection Ljavax/microedition/io/StreamConnection;
      // 0d7: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 0dc: invokespecial java/io/DataInputStream.<init> (Ljava/io/InputStream;)V
      // 0df: astore 4
      // 0e1: bipush 10
      // 0e3: istore 1
      // 0e4: aload 5
      // 0e6: bipush 1
      // 0e7: invokevirtual java/io/DataOutputStream.writeByte (I)V
      // 0ea: aload 5
      // 0ec: aload 3
      // 0ed: arraylength
      // 0ee: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 0f1: aload 5
      // 0f3: aload 3
      // 0f4: invokevirtual java/io/OutputStream.write ([B)V
      // 0f7: aload 5
      // 0f9: invokevirtual java/io/DataOutputStream.close ()V
      // 0fc: aload 4
      // 0fe: invokevirtual java/io/DataInputStream.readByte ()B
      // 101: bipush 1
      // 102: if_icmpeq 10c
      // 105: aload 0
      // 106: bipush 11
      // 108: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 10b: return
      // 10c: aload 0
      // 10d: bipush 5
      // 10f: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.progressUpdate (I)V
      // 112: aload 4
      // 114: invokevirtual java/io/DataInputStream.readByte ()B
      // 117: tableswitch 37 0 5 72 37 44 51 58 65
      // 13c: aload 0
      // 13d: bipush 11
      // 13f: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 142: return
      // 143: aload 0
      // 144: bipush 12
      // 146: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 149: return
      // 14a: aload 0
      // 14b: bipush 13
      // 14d: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 150: return
      // 151: aload 0
      // 152: bipush 14
      // 154: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 157: return
      // 158: aload 0
      // 159: bipush 15
      // 15b: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 15e: return
      // 15f: aload 4
      // 161: invokevirtual java/io/DataInputStream.readInt ()I
      // 164: istore 8
      // 166: iload 8
      // 168: ifgt 172
      // 16b: aload 0
      // 16c: bipush 5
      // 16e: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 171: return
      // 172: iload 8
      // 174: newarray 8
      // 176: astore 9
      // 178: aload 4
      // 17a: aload 9
      // 17c: invokevirtual java/io/DataInputStream.read ([B)I
      // 17f: pop
      // 180: aload 4
      // 182: invokevirtual java/io/DataInputStream.close ()V
      // 185: aload 0
      // 186: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread._stateLock Ljava/lang/Object;
      // 189: dup
      // 18a: astore 10
      // 18c: monitorenter
      // 18d: aload 0
      // 18e: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread._currentState I
      // 191: bipush 9
      // 193: if_icmpne 19a
      // 196: aload 10
      // 198: monitorexit
      // 199: return
      // 19a: aload 0
      // 19b: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.closeConnection ()V
      // 19e: aload 10
      // 1a0: monitorexit
      // 1a1: goto 1ac
      // 1a4: astore 11
      // 1a6: aload 10
      // 1a8: monitorexit
      // 1a9: aload 11
      // 1ab: athrow
      // 1ac: aload 0
      // 1ad: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.this$0 Lnet/rim/device/api/crypto/certificate/status/CertificateStatusQuery;
      // 1b0: new java/lang/Object
      // 1b3: dup
      // 1b4: new java/lang/Object
      // 1b7: dup
      // 1b8: aload 9
      // 1ba: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 1bd: invokespecial java/io/DataInputStream.<init> (Ljava/io/InputStream;)V
      // 1c0: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery.decodeResponse (Ljava/io/DataInputStream;)Z
      // 1c3: ifne 1cd
      // 1c6: aload 0
      // 1c7: bipush 7
      // 1c9: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 1cc: return
      // 1cd: aload 0
      // 1ce: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.this$0 Lnet/rim/device/api/crypto/certificate/status/CertificateStatusQuery;
      // 1d1: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery.applyResponseAndNotifyListener ()V
      // 1d4: aload 0
      // 1d5: bipush 6
      // 1d7: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.progressUpdate (I)V
      // 1da: return
      // 1db: astore 2
      // 1dc: aload 0
      // 1dd: iload 1
      // 1de: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 1e1: return
      // 1e2: astore 2
      // 1e3: aload 0
      // 1e4: bipush 5
      // 1e6: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 1e9: return
      // 1ea: astore 2
      // 1eb: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 1ee: new java/lang/Object
      // 1f1: dup
      // 1f2: ldc_w "Fatal exception caught: "
      // 1f5: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 1f8: aload 2
      // 1f9: invokevirtual java/lang/Throwable.toString ()Ljava/lang/String;
      // 1fc: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1ff: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 202: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 205: aload 0
      // 206: bipush 8
      // 208: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$StatusQueryThread.error (I)V
      // 20b: return
      // try (73 -> 81): 88 null
      // try (82 -> 87): 88 null
      // try (88 -> 91): 88 null
      // try (178 -> 184): 190 null
      // try (185 -> 189): 190 null
      // try (190 -> 193): 190 null
      // try (2 -> 22): 217 null
      // try (23 -> 81): 217 null
      // try (82 -> 128): 217 null
      // try (129 -> 138): 217 null
      // try (139 -> 142): 217 null
      // try (143 -> 146): 217 null
      // try (147 -> 150): 217 null
      // try (151 -> 154): 217 null
      // try (155 -> 163): 217 null
      // try (164 -> 184): 217 null
      // try (185 -> 209): 217 null
      // try (210 -> 216): 217 null
      // try (2 -> 22): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (23 -> 81): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (82 -> 128): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (129 -> 138): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (139 -> 142): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (143 -> 146): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (147 -> 150): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (151 -> 154): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (155 -> 163): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (164 -> 184): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (185 -> 209): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (210 -> 216): 222 net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // try (2 -> 22): 227 null
      // try (23 -> 81): 227 null
      // try (82 -> 128): 227 null
      // try (129 -> 138): 227 null
      // try (139 -> 142): 227 null
      // try (143 -> 146): 227 null
      // try (147 -> 150): 227 null
      // try (151 -> 154): 227 null
      // try (155 -> 163): 227 null
      // try (164 -> 184): 227 null
      // try (185 -> 209): 227 null
      // try (210 -> 216): 227 null
   }

   private void closeConnection() {
      try {
         if (this._connection != null) {
            this._connection.close();
            this._connection = null;
            return;
         }
      } finally {
         return;
      }
   }

   public void terminate() {
      synchronized (this._stateLock) {
         this.closeConnection();
         this.progressUpdate(9);
      }
   }

   private void error(int errorCode) {
      synchronized (this._stateLock) {
         if (this._currentState != 9) {
            this.this$0._errorCode = errorCode;
            this.closeConnection();
            this.this$0.applyResponseAndNotifyListener();
            this.progressUpdate(7);
         }
      }
   }

   private void progressUpdate(int state) {
      synchronized (this._stateLock) {
         if (this._currentState != 9) {
            if (state > this._currentState) {
               this._currentState = state;
               if (this.this$0._progressListener != null) {
                  this.this$0._progressListener.updateProgress(this.this$0, state);
               }
            }
         }
      }
   }

   @Override
   public void event(int dgId, int code, Object context) {
      switch (code) {
         case 0:
            this.progressUpdate(4);
         case -1:
            return;
         case 1:
         default:
            this.progressUpdate(2);
            return;
         case 2:
            this.progressUpdate(3);
      }
   }
}
