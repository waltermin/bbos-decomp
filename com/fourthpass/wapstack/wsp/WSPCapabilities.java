package com.fourthpass.wapstack.wsp;

import com.fourthpass.wapstack.util.VarLengthInt;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class WSPCapabilities {
   private short _methodMOR;
   private short _pushMOR;
   private long _protocolOptions;
   private int _maxClientSDU;
   private int _maxServerSDU;
   private Hashtable _extendedMethodMap;
   private Hashtable _headerPageMap;
   private WSPAddress[] _aliases;
   private ByteArrayOutputStream _baos;
   private ByteArrayOutputStream _encodedCapabilities;
   private byte[] _varLengthInt;
   private int _decodePos;
   private int _nullTermStringPos;
   private boolean _sarMode;

   public WSPCapabilities(boolean sarMode) {
      this._sarMode = sarMode;
      this.reset();
   }

   public final void reset() {
      this._aliases = null;
      if (this._sarMode) {
         this._maxClientSDU = 350000;
      } else {
         this._maxClientSDU = 1400;
      }

      if (this._extendedMethodMap == null) {
         this._extendedMethodMap = (Hashtable)(new Object());
      } else {
         this._extendedMethodMap.clear();
      }

      if (this._headerPageMap == null) {
         this._headerPageMap = (Hashtable)(new Object());
      } else {
         this._headerPageMap.clear();
      }

      this._protocolOptions = 0;
      this._methodMOR = 4;
      this._pushMOR = 1;
      if (this._sarMode) {
         this._maxServerSDU = 350000;
      } else {
         this._maxServerSDU = 1400;
      }

      this._baos = null;
      this._encodedCapabilities = null;
      this._baos = (ByteArrayOutputStream)(new Object());
      this._encodedCapabilities = (ByteArrayOutputStream)(new Object());
      this._varLengthInt = new byte[5];
      this._decodePos = 0;
   }

   public final int getServerSDUSize() {
      return this._maxServerSDU;
   }

   public final void setProtocolOptions(long options) {
      this._protocolOptions = options;
   }

   public final long getProtocolOptions() {
      return this._protocolOptions;
   }

   public final void addExtendedMethod(short PDUType, String methodName) {
      if (this._extendedMethodMap == null) {
         this._extendedMethodMap = (Hashtable)(new Object());
      }

      this._extendedMethodMap.put(new Object(PDUType), methodName);
   }

   public final void addHeaderCodePages(short pageCode, String pageName) {
      if (this._headerPageMap == null) {
         this._headerPageMap = (Hashtable)(new Object());
      }

      this._headerPageMap.put(new Object(pageCode), pageName);
   }

   public final byte[] getCapabilitiesData() {
      this.initializeByteBuffers();
      if (this._aliases != null) {
         this.writeCapabilityField((byte)7);
      }

      this.writeCapabilityField((byte)0);
      if (this._extendedMethodMap.size() > 0) {
         this.writeCapabilityField((byte)5);
      }

      if (this._headerPageMap.size() > 0) {
         this.writeCapabilityField((byte)6);
      }

      this.writeCapabilityField((byte)2);
      this.writeCapabilityField((byte)3);
      this.writeCapabilityField((byte)4);
      this.writeCapabilityField((byte)1);
      return this._encodedCapabilities.toByteArray();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void writeCapabilityField(byte type) {
      this._baos.write(type | 128);
      switch (type) {
         case -1:
            break;
         case 0:
            this.writeUIntVar(this._maxClientSDU, this._baos);
            break;
         case 1:
            this.writeUIntVar(this._maxServerSDU, this._baos);
            break;
         case 2:
            this.writeOctet((byte)this._protocolOptions, this._baos);
            break;
         case 3:
            this.writeOctet((byte)this._methodMOR, this._baos);
            break;
         case 4:
            this.writeOctet((byte)this._pushMOR, this._baos);
            break;
         case 5:
            Enumeration e = this._extendedMethodMap.keys();

            while (e.hasMoreElements()) {
               Short extMethodPDUType = (Short)e.nextElement();
               String extMethodName = (String)this._extendedMethodMap.get(extMethodPDUType);
               this.writeOctet((byte)extMethodPDUType.shortValue(), this._baos);
               this.writeNulTermString(extMethodName, this._baos);
            }
            break;
         case 6:
            Enumeration var11 = this._headerPageMap.keys();

            while (var11.hasMoreElements()) {
               Short pageCode = (Short)var11.nextElement();
               String pageName = (String)this._headerPageMap.get(pageCode);
               this.writeOctet((byte)pageCode.shortValue(), this._baos);
               this.writeNulTermString(pageName, this._baos);
            }
            break;
         case 7:
         default:
            for (int i = 0; i < this._aliases.length; i++) {
               try {
                  this._baos.write(this._aliases[i].getWSPAddress());
               } finally {
                  continue;
               }
            }
      }

      int capabilityLen = this._baos.size();
      this.writeUIntVar(capabilityLen, this._encodedCapabilities);

      try {
         this.writeOctet(this._baos.toByteArray(), this._encodedCapabilities);
         this._baos.reset();
      } catch (Throwable var9) {
         ioe.printStackTrace();
         return;
      }
   }

   private final void writeOctet(byte octet, ByteArrayOutputStream out) {
      out.write(octet);
   }

   private final void writeOctet(byte[] octet, ByteArrayOutputStream out) {
      out.write(octet);
   }

   private final void writeUIntVar(long n, ByteArrayOutputStream out) {
      int numBytes = VarLengthInt.encode(n, this._varLengthInt);
      int maxBytes = this._varLengthInt.length;
      out.write(this._varLengthInt, maxBytes - numBytes, numBytes);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void writeNulTermString(String s, ByteArrayOutputStream out) {
      if (s != null && s.length() >= 1) {
         label21:
         try {
            out.write(s.getBytes());
         } catch (Throwable var5) {
            ioe.printStackTrace();
            break label21;
         }
      }

      out.write(0);
   }

   public final void decodeCapabilitiesData(byte[] capabilities) {
      if (capabilities != null) {
         this.reset();

         while (this._decodePos < capabilities.length) {
            this.readCapability(capabilities);
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void readCapability(byte[] capabilities) {
      try {
         int numBytes = VarLengthInt.getVarLengthCount(capabilities, this._decodePos);
         long length = VarLengthInt.decode(capabilities, this._decodePos);
         this._decodePos += numBytes;
         long count = 1;
         byte identifier = (byte)(capabilities[this._decodePos++] & 127);
         switch (identifier) {
            case -1:
               this._decodePos += (int)(length - 1);
               return;
            case 0:
            default:
               numBytes = VarLengthInt.getVarLengthCount(capabilities, this._decodePos);
               this._maxClientSDU = (int)VarLengthInt.decode(capabilities, this._decodePos);
               this._decodePos += numBytes;
               break;
            case 1:
               numBytes = VarLengthInt.getVarLengthCount(capabilities, this._decodePos);
               this._maxServerSDU = (int)VarLengthInt.decode(capabilities, this._decodePos);
               this._decodePos += numBytes;
               break;
            case 2:
               this._protocolOptions = capabilities[this._decodePos++];
               break;
            case 3:
               this._methodMOR = capabilities[this._decodePos++];
               break;
            case 4:
               this._pushMOR = capabilities[this._decodePos++];
               break;
            case 5:
               while (count < length) {
                  short PDUType = this.makeUInt8(capabilities[this._decodePos++]);
                  String methodName = this.readNulTermString(capabilities);
                  count = count + this._nullTermStringPos + 1;
                  this._decodePos = this._decodePos + this._nullTermStringPos;
                  this.addExtendedMethod(PDUType, methodName);
               }
               break;
            case 6:
               while (count < length) {
                  short pageCode = this.makeUInt8(capabilities[this._decodePos++]);
                  String pageName = this.readNulTermString(capabilities);
                  count = count + this._nullTermStringPos + 1;
                  this._decodePos = this._decodePos + this._nullTermStringPos;
                  this.addHeaderCodePages(pageCode, pageName);
               }
               break;
            case 7:
               Vector all = (Vector)(new Object());
               byte bearertype = 0;
               int portnumber = 0;

               while (count < length) {
                  int alen = capabilities[this._decodePos++];
                  count += 1;
                  int bearer = alen & 128;
                  int port = alen & 64;
                  alen &= 63;
                  if (bearer == 128) {
                     bearertype = capabilities[this._decodePos++];
                     count += 1;
                     if (port == 64) {
                        int var28 = capabilities[this._decodePos++];
                        portnumber = (var28 << 8) + capabilities[this._decodePos++];
                        count += 2;
                     }
                  }

                  byte[] addresses = new byte[alen];

                  for (int i = 0; i < alen; i++) {
                     addresses[i] = capabilities[this._decodePos++];
                  }

                  count += alen;
                  WSPAddress address;
                  if (bearer == 128) {
                     if (port == 64) {
                        address = new WSPAddress(bearertype, portnumber, addresses);
                     } else {
                        address = new WSPAddress(bearertype, addresses);
                     }
                  } else {
                     address = new WSPAddress(addresses);
                  }

                  all.addElement(address);
               }

               this._aliases = new WSPAddress[all.size()];

               for (int i = 0; i < all.size(); i++) {
                  this._aliases[i] = (WSPAddress)all.elementAt(i);
               }

               all.removeAllElements();
         }
      } catch (Throwable var22) {
         aioobe.printStackTrace();
         return;
      }
   }

   private final String readNulTermString(byte[] b) {
      this._nullTermStringPos = 0;
      StringBuffer sb = (StringBuffer)(new Object());
      char c = (char)b[this._decodePos + this._nullTermStringPos];
      this._nullTermStringPos++;

      while (c != 0) {
         sb.append(c);
         c = (char)b[this._decodePos + this._nullTermStringPos++];
      }

      return sb.toString();
   }

   private final short makeUInt8(byte b) {
      short retVal = 0;
      byte val = (byte)(b & 127);
      boolean signed = (b & -128) != 0;
      retVal = (short)(retVal | val);
      if (signed) {
         retVal = (short)(retVal | 128);
      }

      return retVal;
   }

   private final void initializeByteBuffers() {
      if (this._baos == null) {
         this._baos = (ByteArrayOutputStream)(new Object());
      } else {
         this._baos.reset();
      }

      if (this._encodedCapabilities == null) {
         this._encodedCapabilities = (ByteArrayOutputStream)(new Object());
      } else {
         this._encodedCapabilities.reset();
      }
   }
}
