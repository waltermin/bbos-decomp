package net.rim.device.api.crypto;

import java.io.EOFException;
import java.util.Hashtable;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.MIDletSecurity;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.util.ByteArray;
import net.rim.vm.Process;

public class MIDletSecurityCrypto {
   public static final int CMS_OK = 0;
   public static final int CMS_BAD_SIG = 1;
   public static final int CMS_BAD_CHAIN = 2;
   public static final int CMS_BAD_ROOT = 3;
   public static final int CMS_BAD_POLICY = 4;
   public static final int CMS_BAD_CRYPTO = 5;
   public static final int CMS_BAD_MIDLET = 6;
   protected static String CURVE_NAME = "EC163K1";
   protected static final int PRIVATE_KEY_LENGTH = 21;
   protected static final int PUBLIC_KEY_LENGTH = 22;
   protected static final int SIGNATURE_R = 1;
   protected static final int SIGNATURE_S = 2;
   protected static final int SIGNER_ID = 1346652493;
   protected static final int VERSION_OFFSET = 36;
   protected static final int CODE_SIZE_OFFSET = 38;
   protected static final int DATA_SIZE_OFFSET = 40;
   protected static final int CODE_OFFSET = 44;
   protected static final int TRAILER_TYPE = 1;
   protected static final int TRAILER_SIZE = 29;
   protected static final int VERSION_NUMBER = 74;
   private static final long PERSISTENT_STORE_KEY = 8257156306518465173L;
   private static PersistentObject _persist;
   private static Hashtable _hashtable;
   private static int _moduleHandle;
   private static byte[] _moduleHash;
   private static ByteArray _moduleHashByteArray;
   private static MIDletSecurityCrypto _impl;
   public static final int VMT_OK = 0;
   public static final int VMT_UNTRUSTED = 1;
   public static final int VMT_VIOLATION = 2;
   public static final int VMT_SIGNED_BY_RIM = 3;

   protected MIDletSecurityCrypto() {
   }

   public static final int checkMIDletSignature(Digest digest, String rsaSHA1Sig, String[] signingCerts, byte[] refTrailerBytes, byte[] signerCertEncoding) {
      return _impl == null ? 5 : _impl.checkMIDletSignatureImpl(digest, rsaSHA1Sig, signingCerts, refTrailerBytes, signerCertEncoding);
   }

   protected int checkMIDletSignatureImpl(Digest _1, String _2, String[] _3, byte[] _4, byte[] _5) {
      throw null;
   }

   public static final Digest getMIDletSignatureDigest() {
      return new SHA1Digest();
   }

   public static final byte[] signMIDletTrailer(byte[] codfile, byte[] trailerBytes) {
      return _impl == null ? null : _impl.signMIDletTrailerImpl(codfile, trailerBytes);
   }

   protected byte[] signMIDletTrailerImpl(byte[] _1, byte[] _2) {
      throw null;
   }

   public static final int checkJADCertChain(String[] certs) {
      return _impl == null ? 5 : _impl.checkJADCertChainImpl(certs);
   }

   protected int checkJADCertChainImpl(String[] _1) {
      throw null;
   }

   public static final int verifyMIDletTrailer(byte[] policy) {
      if (_moduleHandle == 0) {
         return 2;
      }

      if (!ControlledAccess.verifyCodeModuleSignature(_moduleHandle, 51) && !ControlledAccess.verifyCodeModuleSignature(_moduleHandle, 4342354)) {
         byte[] signature = CodeModuleManager.getModuleSignature(_moduleHandle, 1346652493);
         byte[] trailer = CodeModuleManager.getModuleTrailer(_moduleHandle, 2, 0);
         if (signature == null && trailer == null) {
            return 1;
         }

         if (!verify(_moduleHash, trailer, signature)) {
            return 2;
         }

         if (policy != null) {
            MIDletSecurity.copyPolicyFromMIDletTrailer(trailer, policy);
         }

         return 0;
      } else {
         return 3;
      }
   }

   private static boolean verify(byte[] codFileHash, byte[] codFileTrailer, byte[] signature) {
      if (codFileHash != null && codFileTrailer != null && signature != null) {
         if (codFileHash.length != 20) {
            return false;
         }

         SHA1Digest digest = new SHA1Digest();
         digest.update(codFileHash);
         digest.update(codFileTrailer);

         try {
            DataBuffer buffer = new DataBuffer(signature, 0, signature.length, true);
            byte[] r = TLEUtilities.readDataField(buffer, 1);
            byte[] s = TLEUtilities.readDataField(buffer, 2);
            byte[] publicKey = NvStore.readData(23);
            if (publicKey == null) {
               throw new RuntimeException();
            } else {
               return NativeEC.verifyDSA(CURVE_NAME, publicKey, digest.getDigest(), r, s);
            }
         } catch (EOFException e) {
            throw new RuntimeException();
         }
      } else {
         return false;
      }
   }

   public static final byte[] fetchStoredSettings() {
      int numMIDlets = CodeModuleManager.getNumMidlets();
      if (_hashtable.size() > numMIDlets + 25) {
         Hashtable newHashtable = new Hashtable(numMIDlets);
         int[] moduleHandles = CodeModuleManager.getModuleHandles();

         for (int i = moduleHandles.length - 1; i >= 0; i--) {
            int moduleHandle = moduleHandles[i];
            if (CodeModuleManager.isMidlet(moduleHandle)) {
               byte[] moduleHash = CodeModuleManager.getModuleHash(moduleHandle);
               ByteArray moduleHashByteArray = new ByteArray(moduleHash);
               byte[] settings = (byte[])_hashtable.get(moduleHashByteArray);
               newHashtable.put(moduleHashByteArray, settings);
            }
         }

         _persist.setContents(newHashtable, 51);
         _hashtable = newHashtable;
      }

      return (byte[])_hashtable.get(_moduleHashByteArray);
   }

   public static final void updateStoredSettings(byte[] settings) {
      _hashtable.put(_moduleHashByteArray, settings);
      _persist.commit();
   }

   public static final boolean checkDRMTrailer() {
      return CodeStore.checkDRMTrailer(_moduleHandle);
   }

   static {
      try {
         Class implClass = Class.forName("net.rim.device.api.crypto.MIDletSecurityCryptoImpl");
         _impl = (MIDletSecurityCrypto)implClass.newInstance();
      } catch (ClassNotFoundException var3) {
      } catch (Throwable var4) {
      }

      _persist = RIMPersistentStore.getPersistentObject(8257156306518465173L);
      synchronized (_persist) {
         _hashtable = (Hashtable)_persist.getContents();
         if (_hashtable == null) {
            _hashtable = new Hashtable();
            _persist.setContents(_hashtable, 51);
         }
      }

      _moduleHandle = Process.currentProcess().getModuleHandle();

      try {
         _moduleHash = CodeModuleManager.getModuleHash(_moduleHandle);
      } catch (IllegalArgumentException e) {
         _moduleHash = new byte[0];
      }

      _moduleHashByteArray = new ByteArray(_moduleHash);
   }
}
