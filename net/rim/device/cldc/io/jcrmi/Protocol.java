package net.rim.device.cldc.io.jcrmi;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javacard.framework.APDUException;
import javacard.framework.CardException;
import javacard.framework.CardRuntimeException;
import javacard.framework.ISOException;
import javacard.framework.PINException;
import javacard.framework.SystemException;
import javacard.framework.TransactionException;
import javacard.framework.UserException;
import javacard.framework.service.ServiceException;
import javacard.security.CryptoException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.StreamConnection;
import javax.microedition.jcrmi.JavaCardRMIConnection;
import javax.microedition.jcrmi.RemoteStub;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.i18n.CommonResource;

public class Protocol implements JavaCardRMIConnection, ConnectionBaseInterface, StreamConnection {
   private net.rim.device.cldc.io.apdu.Protocol _apduConnection = new net.rim.device.cldc.io.apdu.Protocol();
   private byte _invokeINS;
   private RemoteStub _initialStub;
   private RMIReference _rmiReference;
   private static final byte FCI_TAG = 111;
   private static final byte APP_DATA_TAG = 110;
   private static final byte RMI_DATA_TAG = 94;
   private static final byte ERROR_TAG = -103;
   private static final byte EXCEPTION_TAG = -126;
   private static final byte EXCEPTION_SUBCLASS_TAG = -125;
   private static final short REF_NULL_TAG = -1;
   private static final byte NORMAL_RESPONSE_TAG = -127;
   private static final short COMPLETION_STATUS = -28672;
   static final byte RMI_MAJOR_VERSION = 2;
   static final byte RMI_MINOR_VERSION = 2;

   @Override
   public void close() {
      this._apduConnection.close();
   }

   @Override
   public Connection openPrim(String param1, int param2, boolean param3) throws IOException, ConnectionNotFoundException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 16
      // 002: newarray 8
      // 004: astore 4
      // 006: bipush 0
      // 007: istore 5
      // 009: bipush 0
      // 00a: istore 6
      // 00c: aload 1
      // 00d: bipush 59
      // 00f: invokevirtual java/lang/String.indexOf (I)I
      // 012: istore 7
      // 014: iload 7
      // 016: bipush -1
      // 018: if_icmpne 023
      // 01b: new java/lang/IllegalArgumentException
      // 01e: dup
      // 01f: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 022: athrow
      // 023: iload 7
      // 025: ifeq 044
      // 028: aload 1
      // 029: bipush 0
      // 02a: iload 7
      // 02c: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 02f: bipush 16
      // 031: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;I)I
      // 034: i2b
      // 035: istore 6
      // 037: goto 044
      // 03a: astore 8
      // 03c: new java/lang/IllegalArgumentException
      // 03f: dup
      // 040: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 043: athrow
      // 044: iload 6
      // 046: ifeq 051
      // 049: new javax/microedition/io/ConnectionNotFoundException
      // 04c: dup
      // 04d: invokespecial javax/microedition/io/ConnectionNotFoundException.<init> ()V
      // 050: athrow
      // 051: aload 1
      // 052: iload 7
      // 054: bipush 1
      // 055: iadd
      // 056: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 059: astore 1
      // 05a: aload 1
      // 05b: ldc_w "AID="
      // 05e: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 061: ifne 06c
      // 064: new java/lang/IllegalArgumentException
      // 067: dup
      // 068: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 06b: athrow
      // 06c: aload 1
      // 06d: bipush 4
      // 06f: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 072: astore 1
      // 073: new net/rim/device/api/util/StringTokenizer
      // 076: dup
      // 077: aload 1
      // 078: bipush 46
      // 07a: invokespecial net/rim/device/api/util/StringTokenizer.<init> (Ljava/lang/String;C)V
      // 07d: astore 8
      // 07f: aload 8
      // 081: invokevirtual net/rim/device/api/util/StringTokenizer.hasMoreTokens ()Z
      // 084: ifeq 0dc
      // 087: iload 5
      // 089: bipush 16
      // 08b: if_icmplt 099
      // 08e: new java/lang/IllegalArgumentException
      // 091: dup
      // 092: ldc_w "Invalid AID; Too Long"
      // 095: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 098: athrow
      // 099: aload 8
      // 09b: invokevirtual net/rim/device/api/util/StringTokenizer.nextToken ()Ljava/lang/String;
      // 09e: astore 9
      // 0a0: aload 9
      // 0a2: invokevirtual java/lang/String.length ()I
      // 0a5: bipush 2
      // 0a7: if_icmple 0b5
      // 0aa: new java/lang/IllegalArgumentException
      // 0ad: dup
      // 0ae: ldc_w "Invalid AID"
      // 0b1: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 0b4: athrow
      // 0b5: aload 4
      // 0b7: iload 5
      // 0b9: aload 9
      // 0bb: bipush 16
      // 0bd: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;I)I
      // 0c0: i2b
      // 0c1: bastore
      // 0c2: goto 0d2
      // 0c5: astore 10
      // 0c7: new java/lang/IllegalArgumentException
      // 0ca: dup
      // 0cb: ldc_w "Invalid AID"
      // 0ce: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 0d1: athrow
      // 0d2: iload 5
      // 0d4: bipush 1
      // 0d5: iadd
      // 0d6: i2b
      // 0d7: istore 5
      // 0d9: goto 07f
      // 0dc: iload 5
      // 0de: bipush 5
      // 0e0: if_icmplt 0ea
      // 0e3: iload 5
      // 0e5: bipush 16
      // 0e7: if_icmple 0f5
      // 0ea: new java/lang/IllegalArgumentException
      // 0ed: dup
      // 0ee: ldc_w "Invalid AID; Too short or too long"
      // 0f1: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 0f4: athrow
      // 0f5: aload 0
      // 0f6: getfield net/rim/device/cldc/io/jcrmi/Protocol._apduConnection Lnet/rim/device/cldc/io/apdu/Protocol;
      // 0f9: aload 4
      // 0fb: iload 5
      // 0fd: invokevirtual net/rim/device/cldc/io/apdu/Protocol.openRMIConnection ([BI)[B
      // 100: astore 10
      // 102: aload 0
      // 103: aload 10
      // 105: invokespecial net/rim/device/cldc/io/jcrmi/Protocol.readFCI ([B)V
      // 108: aload 0
      // 109: areturn
      // 10a: astore 11
      // 10c: aload 0
      // 10d: getfield net/rim/device/cldc/io/jcrmi/Protocol._apduConnection Lnet/rim/device/cldc/io/apdu/Protocol;
      // 110: invokevirtual net/rim/device/cldc/io/apdu/Protocol.close ()V
      // 113: aload 11
      // 115: athrow
      // 116: astore 11
      // 118: aload 0
      // 119: getfield net/rim/device/cldc/io/jcrmi/Protocol._apduConnection Lnet/rim/device/cldc/io/apdu/Protocol;
      // 11c: invokevirtual net/rim/device/cldc/io/apdu/Protocol.close ()V
      // 11f: new java/lang/IllegalArgumentException
      // 122: dup
      // 123: aload 11
      // 125: invokevirtual java/lang/Throwable.toString ()Ljava/lang/String;
      // 128: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 12b: athrow
      // try (20 -> 28): 29 null
      // try (87 -> 94): 95 null
      // try (124 -> 127): 129 null
      // try (124 -> 127): 135 null
   }

   @Override
   public int getProperties(String name) {
      return 32;
   }

   byte getChannel() {
      return this._apduConnection.getChannelNumber();
   }

   byte getInvokeINS() {
      return this._invokeINS;
   }

   Object invokeMethod(byte[] command, String method) throws RemoteException {
      byte[] response = this._apduConnection.exchangeAPDU(command);
      if (response != null && response.length >= 2) {
         DataBuffer buffer = new DataBuffer();
         buffer.setData(response, 0, response.length);
         buffer.setPosition(response.length - 2);
         if (buffer.readShort() != -28672) {
            throw new RemoteException();
         }

         buffer.setPosition(0);
         byte tag = buffer.readByte();
         if (tag == -103) {
            this.throwError(buffer.readShort());
         } else if (tag == -126 || tag == -125) {
            byte type = buffer.readByte();
            short reason = buffer.readShort();
            this.throwException(type, reason);
         }

         if (tag != -127) {
            throw new RemoteException();
         }

         int index = method.indexOf(41);
         char rType = method.charAt(index + 1);
         switch (rType) {
            case 'B':
               return new Byte(buffer.readByte());
            case 'I':
               return new Integer(buffer.readInt());
            case 'L':
               return this.readReference(buffer.getArray(), buffer.getPosition() - 1, buffer.getLength() - 2);
            case 'S':
               return new Short(buffer.readShort());
            case 'V':
               return null;
            case 'Z':
               return new Boolean(buffer.readByte() == 1);
            case '[':
               int length = buffer.readByte();
               char arrayType = method.charAt(index + 2);
               switch (arrayType) {
                  case 'B':
                     byte[] byteArray = new byte[length];

                     for (int i = 0; i < byteArray.length; i++) {
                        byteArray[i] = buffer.readByte();
                     }

                     return byteArray;
                  case 'I':
                     int[] intArray = new int[length];

                     for (int i = 0; i < intArray.length; i++) {
                        intArray[i] = buffer.readInt();
                     }

                     return intArray;
                  case 'S':
                     short[] shortArray = new short[length];

                     for (int i = 0; i < shortArray.length; i++) {
                        shortArray[i] = buffer.readShort();
                     }

                     return shortArray;
                  case 'Z':
                     boolean[] boolArray = new boolean[length];

                     for (int i = 0; i < boolArray.length; i++) {
                        boolArray[i] = buffer.readByte() == 1;
                     }

                     return boolArray;
               }
            default:
               return null;
         }
      } else {
         throw new RemoteException();
      }
   }

   @Override
   public DataInputStream openDataInputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   @Override
   public Remote getInitialReference() {
      return (Remote)this._initialStub;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public short enterPin(int pinID) throws RemoteException {
      if (this._apduConnection.isOpen() && this._rmiReference != null) {
         try {
            byte[] currentPin = this._apduConnection.getPIN(pinID);
            if (currentPin == null) {
               return -1;
            }

            Object result = this._rmiReference.invoke("verifyPin([B)S", new Object[]{currentPin});
            return (Short)result;
         } catch (Throwable var6) {
            throw new RemoteException(null, e);
         }
      } else {
         throw new RemoteException();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public short changePin(int pinID) throws RemoteException {
      if (this._apduConnection.isOpen() && this._rmiReference != null) {
         try {
            byte[] currentPin = this._apduConnection.getPIN(pinID);
            if (currentPin == null) {
               return -1;
            }

            byte[] newPin = this._apduConnection.getNewPin(pinID);
            if (newPin == null) {
               return -1;
            }

            Object result = this._rmiReference.invoke("changePin([B[B)S", new Object[]{currentPin, newPin});
            return (Short)result;
         } catch (Throwable var7) {
            throw new RemoteException(null, e);
         }
      } else {
         throw new RemoteException();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public short disablePin(int pinID) throws RemoteException {
      if (this._apduConnection.isOpen() && this._rmiReference != null) {
         try {
            byte[] currentPin = this._apduConnection.getPIN(pinID);
            if (currentPin == null) {
               return -1;
            }

            Object result = this._rmiReference.invoke("disablePin([B)S", new Object[]{currentPin});
            return (Short)result;
         } catch (Throwable var6) {
            throw new RemoteException(null, e);
         }
      } else {
         throw new RemoteException();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public short enablePin(int pinID) throws RemoteException {
      if (this._apduConnection.isOpen() && this._rmiReference != null) {
         try {
            byte[] currentPin = this._apduConnection.getPIN(pinID);
            if (currentPin == null) {
               return -1;
            }

            Object result = this._rmiReference.invoke("enablePin([B)S", new Object[]{currentPin});
            return (Short)result;
         } catch (Throwable var6) {
            throw new RemoteException(null, e);
         }
      } else {
         throw new RemoteException();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public short unblockPin(int blockedPinID, int unblockingPinID) throws RemoteException {
      if (this._apduConnection.isOpen() && this._rmiReference != null) {
         try {
            String pin = this._apduConnection.getSIMCode(false, CommonResource.getString(10139));
            if (pin != null && pin.length() != 0) {
               byte[] blockedPin = this._apduConnection.getNewPin(blockedPinID);
               if (blockedPin == null) {
                  return -1;
               }

               Object result = this._rmiReference.invoke("unblockPin([B[B)S", new Object[]{this._apduConnection.ensurePINSize(pin.getBytes()), blockedPin});
               return (Short)result;
            } else {
               return -1;
            }
         } catch (Throwable var8) {
            throw new RemoteException(null, e);
         }
      } else {
         throw new RemoteException();
      }
   }

   @Override
   public InputStream openInputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   @Override
   public DataOutputStream openDataOutputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   @Override
   public OutputStream openOutputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   private void readFCI(byte[] fci) throws RemoteException {
      if (fci[0] == 111 && fci[2] == 110 && fci[4] == 94 && fci[3] == fci[5] + 2 && fci.length >= 9) {
         this._invokeINS = fci[8];
         int length = fci[5] & 255;
         this._initialStub = this.readReference(fci, 9, length - 2);
      } else {
         throw new RemoteException("Invalid FCI format");
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private RemoteStub readReference(byte[] array, int offset, int length) throws RemoteException {
      try {
         DataBuffer buffer = new DataBuffer();
         buffer.setData(array, offset, length);
         byte ntag = buffer.readByte();
         if (ntag != -127) {
            return null;
         }

         short refID = buffer.readShort();
         if (refID == -1) {
            return null;
         }

         int hashLength = buffer.readByte();
         byte[] hashModifier = new byte[hashLength];
         buffer.readFully(hashModifier);
         int interfaceCount = buffer.readByte();
         Class[] classes = new Class[interfaceCount];
         Class fClass = null;
         String fFullName = null;

         for (int j = 0; j < interfaceCount; j++) {
            int pkgLength = buffer.readByte();
            byte[] pkg = new byte[pkgLength];
            buffer.readFully(pkg);

            for (int i = 0; i < pkg.length; i++) {
               if (pkg[i] == 47) {
                  pkg[i] = 46;
               }
            }

            int cLength = buffer.readByte();
            byte[] cname = new byte[cLength];
            buffer.readFully(cname);
            String fullName = new String(pkg, "UTF-8") + '.' + new String(cname, "UTF-8");
            Class tempClass = Class.forName(fullName);
            if (fClass == null) {
               fClass = tempClass;
               fFullName = fullName;
            } else if (fClass.isAssignableFrom(tempClass)) {
               fClass = tempClass;
               fFullName = fullName;
            }

            classes[j] = tempClass;
         }

         for (int i = 0; i < interfaceCount; i++) {
            if (!classes[i].isAssignableFrom(fClass)) {
               throw new RemoteException();
            }
         }

         RMIReference reference = new RMIReference(refID, new String(hashModifier, "UTF-8"), fFullName, this);
         RemoteStub stub = null;
         boolean var24 = false /* VF: Semaphore variable */;

         try {
            var24 = true;
            stub = (RemoteStub)Class.forName(fFullName + "_Stub").newInstance();
            var24 = false;
         } finally {
            if (var24) {
               throw new RemoteException("Cannot find remote object stub");
            }
         }

         stub.setRef(reference);
         this._rmiReference = reference;
         return stub;
      } catch (Throwable var26) {
         throw new RemoteException(null, exception);
      }
   }

   private void throwException(short type, short reason) throws IOException, Exception, RemoteException, CardException, UserException {
      String s = "User defined exception thrown by remote method";
      switch (type) {
         case 0:
            throw new Exception(s);
         case 1:
            throw new ArithmeticException(s);
         case 2:
            throw new ArrayIndexOutOfBoundsException(s);
         case 3:
            throw new ArrayStoreException(s);
         case 4:
            throw new ClassCastException(s);
         case 5:
            throw new Exception(s);
         case 6:
            throw new IndexOutOfBoundsException(s);
         case 7:
            throw new NegativeArraySizeException(s);
         case 8:
            throw new NullPointerException(s);
         case 9:
            throw new RuntimeException(s);
         case 10:
            throw new SecurityException(s);
         case 11:
            throw new IOException(s);
         case 12:
            throw new RemoteException(s);
         case 32:
            throw new APDUException(reason);
         case 33:
            throw new CardException(reason);
         case 34:
            throw new CardRuntimeException(reason);
         case 35:
            throw new ISOException(reason);
         case 36:
            throw new PINException(reason);
         case 37:
            throw new SystemException(reason);
         case 38:
            throw new TransactionException(reason);
         case 39:
            throw new UserException(reason);
         case 48:
            throw new CryptoException(reason);
         case 64:
            throw new ServiceException(reason);
         default:
            throw new RemoteException();
      }
   }

   private void throwError(int detail) throws RemoteException {
      String s = "";
      switch (detail) {
         case 1:
            s = "The Remote Object Identifier is invalid or ineligible for Java Card RMI";
            break;
         case 2:
            s = "The Remote Mothod could not be identified";
            break;
         case 3:
            s = "The Remote Method signature did not match the parameter format";
            break;
         case 4:
            s = "Insufficient resources available to unmarshall parameters";
            break;
         case 5:
            s = "Insufficient resources available to marshall response";
            break;
         case 6:
            s = "Java Card Remote Method Invocation protocol error";
            break;
         case 65535:
            s = "Internal Error Occurred";
      }

      throw new RemoteException(s);
   }
}
