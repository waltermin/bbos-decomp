package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Memory;

final class RIMKeyStoreData$Payload implements Persistable {
   private String _label;
   public Object _privateKey;
   public String _privateKeyEncodingAlgorithm;
   public String _symmetricKeyEncodingAlgorithm;
   public Object _symmetricKey;
   public PublicKey _publicKey;
   public long _certificate = -1;
   public byte[] _encoding;
   public String _type;
   public long _keyUsage;
   public int _hashCode;
   public LongHashtable _associations;
   public int _uid;
   public long[] _indices = new long[0];
   public int[] _hashes = new int[0];
   public long[] _notUsed = new long[0];

   public final String getLabel() {
      return this._label;
   }

   public final void setLabel(String label) {
      if (Memory.isObjectInGroup(label)) {
         try {
            this._label = (String)(new Object(label.getBytes("UTF8"), "UTF8"));
         } finally {
            throw new Object();
         }
      } else {
         this._label = label;
      }
   }
}
