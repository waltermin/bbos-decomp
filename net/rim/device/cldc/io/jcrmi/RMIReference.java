package net.rim.device.cldc.io.jcrmi;

import java.rmi.RemoteException;
import javax.microedition.jcrmi.RemoteRef;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.util.DataBuffer;

class RMIReference implements RemoteRef {
   private String _hashModifier;
   private String _className;
   private int _refId;
   Protocol _protocol;

   public String getClassName() {
      return this._className;
   }

   @Override
   public boolean remoteEquals(RemoteRef remoteref) {
      if (!(remoteref instanceof RMIReference)) {
         return false;
      }

      RMIReference rmiRef = (RMIReference)remoteref;
      return rmiRef._refId == this._refId && rmiRef._protocol == this._protocol;
   }

   @Override
   public int remoteHashCode() {
      return this._refId;
   }

   @Override
   public Object invoke(String s, Object[] aobj) throws RemoteException {
      DataBuffer buffer = new DataBuffer();
      byte cla = -128;
      buffer.writeByte(cla);
      buffer.writeByte(this._protocol.getInvokeINS());
      buffer.writeByte(2);
      buffer.writeByte(2);
      buffer.writeByte(0);
      buffer.writeShort((short)this._refId);
      SHA1Digest digest = new SHA1Digest();
      String str = this._hashModifier + s;
      digest.update(str.getBytes());
      byte[] dArray = digest.getDigest();
      buffer.writeByte(dArray[0]);
      buffer.writeByte(dArray[1]);
      if (aobj != null) {
         for (int i = 0; i < aobj.length; i++) {
            Object obj = aobj[i];
            if (obj instanceof Byte) {
               buffer.writeByte((Byte)obj);
            } else if (!(obj instanceof Boolean)) {
               if (obj instanceof Short) {
                  buffer.writeShort((Short)obj);
               } else if (obj instanceof Integer) {
                  buffer.writeInt((Integer)obj);
               } else if (!(obj instanceof byte[])) {
                  if (!(obj instanceof boolean[])) {
                     if (!(obj instanceof short[])) {
                        if (!(obj instanceof int[])) {
                           throw new RemoteException("Parameter type not supported");
                        }

                        int[] iArray = (int[])obj;
                        buffer.writeByte(iArray.length);

                        for (int j = 0; j < iArray.length; j++) {
                           buffer.writeInt(iArray[j]);
                        }
                     } else {
                        short[] sArray = (short[])obj;
                        buffer.writeByte(sArray.length);

                        for (int j = 0; j < sArray.length; j++) {
                           buffer.writeShort(sArray[j]);
                        }
                     }
                  } else {
                     boolean[] bArray = (boolean[])obj;
                     buffer.writeByte(bArray.length);

                     for (int j = 0; j < bArray.length; j++) {
                        buffer.writeByte(bArray[j] ? 1 : 0);
                     }
                  }
               } else {
                  byte[] bArray = (byte[])obj;
                  buffer.writeByte(bArray.length);

                  for (int j = 0; j < bArray.length; j++) {
                     buffer.writeByte(bArray[j]);
                  }
               }
            } else {
               Boolean b = (Boolean)obj;
               buffer.writeByte(b ? 1 : 0);
            }
         }
      }

      byte[] barray = new byte[buffer.getLength()];
      buffer.setPosition(0);
      buffer.read(barray);
      barray[4] = (byte)(barray.length - 5);
      return this._protocol.invokeMethod(barray, s);
   }

   RMIReference(int refId, String hash, String cName, Protocol p) {
      this._refId = refId;
      this._hashModifier = hash;
      this._className = cName;
      this._protocol = p;
   }
}
