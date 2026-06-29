package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.util.VarLengthInt;
import com.fourthpass.wapstack.wsp.WSPHeaders;
import java.io.InputStream;

public final class WSP_ReplyPDU extends WSP_PDU {
   private int _headersLen;
   private int _headersSize;

   public WSP_ReplyPDU(boolean param1, InputStream param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial com/fourthpass/wapstack/wsp/pdu/WSP_PDU.<init> ()V
      // 04: bipush 0
      // 05: istore 3
      // 06: iload 1
      // 07: ifeq 10
      // 0a: aload 2
      // 0b: invokevirtual java/io/InputStream.read ()I
      // 0e: i2b
      // 0f: istore 3
      // 10: aload 2
      // 11: invokevirtual java/io/InputStream.read ()I
      // 14: istore 8
      // 16: iload 8
      // 18: bipush -1
      // 1a: if_icmpne 28
      // 1d: new java/lang/Object
      // 20: dup
      // 21: ldc_w "Null data"
      // 24: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 27: athrow
      // 28: iload 8
      // 2a: i2b
      // 2b: istore 5
      // 2d: aload 2
      // 2e: invokevirtual java/io/InputStream.read ()I
      // 31: i2b
      // 32: istore 4
      // 34: aload 2
      // 35: invokestatic com/fourthpass/wapstack/util/VarLengthInt.decodeEx (Ljava/io/InputStream;)J
      // 38: lstore 6
      // 3a: aload 0
      // 3b: bipush 3
      // 3d: lload 6
      // 3f: l2i
      // 40: iadd
      // 41: newarray 8
      // 43: putfield com/fourthpass/wapstack/wsp/pdu/WSP_PDU._PDU [B
      // 46: aload 0
      // 47: getfield com/fourthpass/wapstack/wsp/pdu/WSP_PDU._PDU [B
      // 4a: bipush 0
      // 4b: iload 3
      // 4c: bastore
      // 4d: aload 0
      // 4e: getfield com/fourthpass/wapstack/wsp/pdu/WSP_PDU._PDU [B
      // 51: bipush 1
      // 52: iload 5
      // 54: bastore
      // 55: aload 0
      // 56: getfield com/fourthpass/wapstack/wsp/pdu/WSP_PDU._PDU [B
      // 59: bipush 2
      // 5b: iload 4
      // 5d: bastore
      // 5e: aload 2
      // 5f: aload 0
      // 60: getfield com/fourthpass/wapstack/wsp/pdu/WSP_PDU._PDU [B
      // 63: bipush 3
      // 65: lload 6
      // 67: l2i
      // 68: invokevirtual java/io/InputStream.read ([BII)I
      // 6b: pop
      // 6c: aload 0
      // 6d: iload 1
      // 6e: putfield com/fourthpass/wapstack/wsp/pdu/WSP_PDU._connectionLessMode Z
      // 71: aload 0
      // 72: lload 6
      // 74: l2i
      // 75: putfield com/fourthpass/wapstack/wsp/pdu/WSP_ReplyPDU._headersSize I
      // 78: aload 0
      // 79: bipush 0
      // 7a: putfield com/fourthpass/wapstack/wsp/pdu/WSP_ReplyPDU._headersLen I
      // 7d: aload 0
      // 7e: bipush 2
      // 80: i2l
      // 81: lload 6
      // 83: ladd
      // 84: l2i
      // 85: i2s
      // 86: putfield com/fourthpass/wapstack/wsp/pdu/WSP_PDU._pduLength I
      // 89: aload 0
      // 8a: aload 0
      // 8b: getfield com/fourthpass/wapstack/wsp/pdu/WSP_PDU._pduLength I
      // 8e: putfield com/fourthpass/wapstack/wsp/pdu/WSP_PDU._dataLength I
      // 91: aload 0
      // 92: aload 2
      // 93: putfield com/fourthpass/wapstack/wsp/pdu/WSP_PDU._pduData Ljava/io/InputStream;
      // 96: return
      // 97: astore 8
      // 99: aload 8
      // 9b: athrow
      // 9c: astore 8
      // 9e: return
      // try (4 -> 86): 87 null
      // try (4 -> 86): 90 null
   }

   public WSP_ReplyPDU(boolean connectionless, byte tid, byte status, WSPHeaders headers, byte[] data) {
      super._connectionLessMode = connectionless;
      super._PDU[0] = tid;
      super._PDU[1] = 4;
      super._PDU[2] = status;
      super._dataLength = 3;
      int length = 0;
      if (headers != null) {
         length = headers.getLength();
      }

      int encodedLen = VarLengthInt.encode(length, super._uintvar);
      this.appendPDU(super._uintvar, super._uintvar.length - encodedLen, super._uintvar.length);
      if (headers != null) {
         this.appendPDU(headers.getAttributeList());
      }

      this.appendPDU(data);
   }

   public final byte getStatusCode() {
      return super._PDU[2];
   }

   public final WSPHeaders getHeaderInfo(WSPHeaders headers) {
      byte[] headerInfo = null;
      if (this._headersSize > 0) {
         headerInfo = new byte[this._headersSize];
         System.arraycopy(super._PDU, 3 + this._headersLen, headerInfo, 0, headerInfo.length);
      }

      if (headers == null) {
         return new WSPHeaders(headerInfo);
      }

      headers.setAttributeList(headerInfo);
      return headers;
   }

   public final InputStream getData() {
      return super._pduData;
   }
}
