package net.rim.device.api.system;

import java.io.UnsupportedEncodingException;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.ForcedResetManager;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.NvStore;
import net.rim.vm.Array;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;

public final class CodeModuleManager {
   public static final int CMM_OK = 0;
   public static final int CMM_OK_MODULE_OVERWRITTEN = 1;
   public static final int CMM_HASH_INVALID = 2;
   public static final int CMM_SIGNATURE_INVALID = 3;
   public static final int CMM_MODULE_INVALID = 4;
   public static final int CMM_MODULE_INCOMPATIBLE = 5;
   public static final int CMM_OK_MODULE_MARKED_FOR_DELETION = 6;
   public static final int CMM_MODULE_IN_USE = 7;
   public static final int CMM_MODULE_IN_USE_BY_PERSISTENT_STORE = 8;
   public static final int CMM_MODULE_REQUIRED = 9;
   public static final int CMM_HANDLE_INVALID = 10;
   public static final int CMM_OUT_OF_MEMORY = 11;
   public static final int CMM_MODULE_EXCLUDED = 12;
   public static final int CMM_TRANSACTION_INVALID = 13;
   public static final int CMM_TRANSACTION_COMPLETE = 14;
   public static final int CMM_TRANSACTION_RESET_REQUIRED = 15;
   public static final int MODULE_FLAG_DELETE = 1;
   public static final int MODULE_FLAG_OTA = 2;
   public static final int MODULE_FLAG_INSTALLED = 4;

   private CodeModuleManager() {
   }

   private static final native void setResetRequired();

   public static final native int[] getModuleHandles();

   public static final native int[] getModuleHandles(boolean var0);

   public static final int getModuleHandleForObject(Object obj) {
      if (obj == null) {
         throw new NullPointerException();
      } else {
         return getModuleHandleForObjectImpl(obj);
      }
   }

   private static final native int getModuleHandleForObjectImpl(Object var0);

   public static final int getModuleHandleForClass(Class clazz) {
      if (clazz == null) {
         throw new NullPointerException();
      } else {
         return getModuleHandleForClassImpl(clazz);
      }
   }

   private static final native int getModuleHandleForClassImpl(Class var0);

   public static final native int getModuleHandle(String var0);

   public static final native int getModuleHandle(byte[] var0);

   public static final boolean deleteModule(int moduleHandle, boolean force) {
      int rc = deleteModuleEx(moduleHandle, force);
      return rc == 0 || rc == 6;
   }

   public static final int deleteModuleEx(int moduleHandle, boolean force) {
      ApplicationControl.assertCMMApiAllowed(true);
      int rc = deleteModuleExImpl(moduleHandle, force);
      if (rc == 0) {
         ApplicationControl.removeModule(moduleHandle);
         return rc;
      }

      if (rc == 6) {
         setResetRequired();
      }

      return rc;
   }

   private static final native int deleteModuleExImpl(int var0, boolean var1);

   public static final native int getModuleFlags(int var0);

   public static final String getModuleName(int moduleHandle) {
      return getModuleName(moduleHandle, 0);
   }

   public static final String getModuleVersion(int moduleHandle) {
      return getModuleVersion(moduleHandle, 0);
   }

   public static final native byte[] getModuleHash(int var0);

   public static final native boolean getModuleHash(int var0, byte[] var1);

   public static final native int getModuleSignerId(int var0, int var1);

   public static final native byte[] getModuleSignature(int var0, int var1);

   public static final String getModuleVendor(int moduleHandle) {
      return getModuleString(moduleHandle, "_vÉ\u0019", 0);
   }

   public static final String getModuleDescription(int moduleHandle) {
      return getModuleString(moduleHandle, "_\u0006scri\u0088", 0);
   }

   public static final String getModuleURL(int moduleHandle) {
      return getModuleString(moduleHandle, "_®l", 0);
   }

   public static final long getModuleTimestamp(int moduleHandle) {
      return (long)getModuleTimestamp0(moduleHandle) * 1000;
   }

   private static final native int getModuleTimestamp0(int var0);

   public static final native boolean isLibrary(int var0);

   public static final native boolean isMidlet(int var0);

   public static final native int getNumMidlets();

   public static final boolean isMidlet() {
      int handle = Process.currentProcess().getModuleHandle();
      return isMidlet(handle);
   }

   private static final native boolean hasEntryPoint(int var0, int var1);

   public static final native int getModuleCodeSize(int var0);

   public static final native String getModuleName(int var0, int var1);

   public static final native String getModuleAliasName(int var0, int var1);

   public static final native String getModuleVersion(int var0, int var1);

   public static final int createNewModule(int length) {
      ApplicationControl.assertCMMApiAllowed(true);
      return createNewModuleImpl(length);
   }

   private static final native int createNewModuleImpl(int var0);

   public static final int createNewModule(int totalLength, byte[] data, int length) {
      ApplicationControl.assertCMMApiAllowed(true);
      return createNewModuleImpl(totalLength, data, length);
   }

   private static final native int createNewModuleImpl(int var0, byte[] var1, int var2);

   public static final boolean writeNewModule(int newModuleHandle, byte[] data, int newModuleOffset, int length) {
      return writeNewModule(newModuleHandle, newModuleOffset, data, 0, length);
   }

   public static final boolean writeNewModule(int newModuleHandle, int newModuleOffset, byte[] data, int offset, int length) {
      ApplicationControl.assertCMMApiAllowed(true);
      return writeNewModuleImpl(newModuleHandle, newModuleOffset, data, offset, length);
   }

   private static final native boolean writeNewModuleImpl(int var0, int var1, byte[] var2, int var3, int var4);

   public static final int saveNewModule(int newModuleHandle) {
      return saveNewModule(newModuleHandle, false);
   }

   public static final int saveNewModule(int newModuleHandle, boolean forceOverwrite) {
      return saveNewModule(newModuleHandle, forceOverwrite, 0);
   }

   public static final int saveNewModule(int newModuleHandle, boolean forceOverwrite, int transactionHandle) {
      ApplicationControl.assertCMMApiAllowed(true);
      int rc = saveNewModuleImpl(newModuleHandle, forceOverwrite, transactionHandle);
      if (rc == 6 && transactionHandle == 0) {
         setResetRequired();
      }

      return rc;
   }

   private static final native int saveNewModuleImpl(int var0, boolean var1, int var2);

   public static final int deleteNewModule(int newModuleHandle) {
      ApplicationControl.assertCMMApiAllowed(true);
      int rc = deleteNewModuleImpl(newModuleHandle);
      if (rc == 6) {
         setResetRequired();
      }

      return rc;
   }

   private static final native int deleteNewModuleImpl(int var0);

   public static final int beginTransaction() {
      return beginTransactionImpl();
   }

   private static final native int beginTransactionImpl();

   public static final int endTransaction(int transactionHandle) {
      int rc = endTransactionImpl(transactionHandle);
      if (rc == 15) {
         setResetRequired();
      }

      return rc;
   }

   private static final native int endTransactionImpl(int var0);

   public static final int cancelTransaction(int transactionHandle) {
      return cancelTransactionImpl(transactionHandle);
   }

   private static final native int cancelTransactionImpl(int var0);

   public static final native boolean isResetRequired();

   public static final void promptForResetIfRequired() {
      ApplicationControl.assertCMMApiAllowed(true);
      if (isResetRequired()) {
         ForcedResetManager frm = ForcedResetManager.getInstance();
         String text = CommonResource.getString(10127);
         frm.scheduleDeviceResetNoTimeout(text, 3600000, false);
      }
   }

   public static final byte[] getModuleResourceData(int moduleHandle) {
      return getModuleData(moduleHandle, "_\fso®\u0007\u0094t\u0013s\u001cs");
   }

   public static final byte[] getModuleLanguageData(int moduleHandle) {
      return getModuleData(moduleHandle, "_áuå so®\u0007s");
   }

   static final native byte[] getModuleData(int var0, String var1);

   private static final int getAppCount(int moduleHandle) {
      if (isLibrary(moduleHandle)) {
         if (hasEntryPoint(moduleHandle, 1)) {
            return 1;
         }
      } else {
         byte[] data = getModuleData(moduleHandle, "_\u0018p¦¢t");
         if (data != null) {
            return data[0];
         }
      }

      return 0;
   }

   public static final ApplicationDescriptor[] getApplicationDescriptors(int moduleHandle) {
      int numApps = getAppCount(moduleHandle);
      if (numApps <= 0) {
         return null;
      }

      ApplicationDescriptor[] descriptors = new ApplicationDescriptor[numApps];

      for (int i = 0; i < numApps; i++) {
         descriptors[i] = new ApplicationDescriptor(moduleHandle, i);
      }

      return descriptors;
   }

   static final byte[] getModuleData(int moduleHandle, String id, int index) {
      byte[] data = getModuleData(moduleHandle, id);
      if (data == null) {
         return null;
      }

      int length = 0;
      int offset = 0;

      try {
         for (int i = 0; i <= index; i++) {
            offset += length;
            length = (data[offset++] & 255) << 8;
            length += data[offset++] & 255;
         }

         if (length == 0) {
            return null;
         }

         System.arraycopy(data, offset, data, 0, length);
         Array.resize(data, length);
         return data;
      } catch (ArrayIndexOutOfBoundsException ex) {
         return null;
      }
   }

   static final String getModuleString(int moduleHandle, String id, int index) {
      byte[] data = getModuleData(moduleHandle, id, index);
      if (data == null) {
         return null;
      }

      try {
         return new String(data, "UTF8");
      } catch (UnsupportedEncodingException ex) {
         return null;
      }
   }

   public static final native boolean verifySignature(int var0, int var1, byte[] var2);

   public static final CodeSigningKey getADCCodeSigningKey() {
      byte[] publicKey = NvStore.readData(13);
      if (publicKey != null) {
         int signerId = NvStore.readInt(12, 0);
         byte[] b = NvStore.readData(14);
         String description = null;
         if (b != null) {
            try {
               description = new String(b, "UTF8");
            } catch (UnsupportedEncodingException var5) {
            }
         }

         return new CodeSigningKey(signerId, publicKey, description);
      } else {
         return null;
      }
   }

   public static final boolean setADCCodeSigningKey(CodeSigningKey newKey) {
      CodeSigningKey RADCKey = new CodeSigningKey(
         1128546642,
         new byte[]{
            -99,
            28,
            -80,
            -127,
            107,
            34,
            -41,
            41,
            36,
            65,
            -109,
            -70,
            -17,
            91,
            -15,
            56,
            54,
            21,
            119,
            -51,
            -36,
            -28,
            59,
            56,
            -79,
            44,
            23,
            -107,
            109,
            74,
            -28,
            125,
            -128,
            20,
            -78,
            -99,
            -117,
            -22,
            -118,
            -26,
            -70,
            -127,
            83,
            52,
            -122,
            -87,
            -40,
            76,
            107,
            -75,
            14,
            91,
            104,
            29,
            -55,
            -112,
            -87,
            -46,
            38,
            -71,
            14,
            83,
            -18,
            20,
            -125,
            52,
            -89,
            -10,
            -54,
            118,
            -29,
            -57,
            -83,
            66,
            -50,
            -41,
            90,
            33,
            27,
            -72,
            -81,
            105,
            -115,
            66,
            -73,
            -12,
            113,
            -52,
            -57,
            44,
            -69,
            9,
            65,
            -113,
            -116,
            -72,
            27,
            49,
            36,
            -84,
            -65,
            -62,
            -17,
            52,
            57,
            55,
            -54,
            76,
            115,
            -34,
            77,
            -126,
            51,
            -120,
            -90,
            21,
            -35,
            -13,
            19,
            -35,
            106,
            55,
            -89,
            -78,
            -109,
            -45,
            -71,
            -115
         },
         null
      );
      int caller = TraceBack.getCallingModule(0);
      if (!ControlledAccess.verifyCodeModuleSignature(caller, RADCKey)) {
         return false;
      }

      synchronized (ApplicationRegistry.getApplicationRegistry()) {
         CodeSigningKey currentKey = getADCCodeSigningKey();
         if (currentKey != null) {
            if (currentKey.equals(newKey)) {
               return true;
            }

            if (!ControlledAccess.verifyCodeModuleSignature(caller, currentKey)) {
               return false;
            }
         }

         if (!ControlledAccess.verifyCodeModuleSignature(caller, newKey)) {
            return false;
         }

         NvStore.writeInt(12, newKey.getSignerIdAsInt());
         NvStore.writeData(13, newKey.getPublicKeyInternal());
         String description = newKey.getDescription();
         if (description != null) {
            try {
               NvStore.writeData(14, description.getBytes("UTF8"));
            } catch (UnsupportedEncodingException var11) {
            }
         }
      }

      CodeSigningKey rimKey = CodeSigningKey.getBuiltInKey(51);
      int rimKeyId = rimKey.getSignerIdAsInt();
      byte[] rimPublicKey = rimKey.getPublicKeyInternal();
      int newKeyId = newKey.getSignerIdAsInt();
      byte[] newPublicKey = newKey.getPublicKeyInternal();
      int[] modules = getModuleHandles();
      boolean deleted = false;

      for (int i = modules.length - 1; i >= 0; i--) {
         if (!verifySignature(modules[i], rimKeyId, rimPublicKey) && !verifySignature(modules[i], newKeyId, newPublicKey)) {
            deleteModule(modules[i], true);
            deleted = true;
         }
      }

      if (deleted) {
         InternalServices.initiateReset("CMM ADAC");
      }

      return true;
   }

   public static final native boolean isRRTSignaturePresent(int var0);

   public static final native boolean isRRTSignatureRequired(int var0);

   public static final native long getModuleDownloadTimestamp(int var0);

   public static final native int verifyApplicationControlModules();

   public static final native int getModuleTrailerFlags(int var0, int var1, int var2);

   public static final native byte[] getModuleTrailer(int var0, int var1, int var2);

   public static final byte[] makeTrailer(int id, int flags, byte[] data) {
      int len = data.length;
      if ((len & 3) != 0) {
         throw new IllegalArgumentException();
      }

      byte[] trailer = new byte[4 + len];
      trailer[0] = (byte)id;
      trailer[1] = (byte)flags;
      trailer[2] = (byte)len;
      trailer[3] = (byte)(len >> 8);
      System.arraycopy(data, 0, trailer, 4, len);
      return trailer;
   }

   public static final byte[] appendTrailers(byte[] codfile, byte[][] trailers) {
      int trailerNum = trailers.length;
      int trailerSize = 0;

      for (int i = trailerNum - 1; i >= 0; i--) {
         trailerSize += trailers[i].length;
      }

      int codfileEnd = codfile.length;
      Array.resize(codfile, codfileEnd + trailerSize);

      for (int i = trailerNum - 1; i >= 0; i--) {
         byte[] t = trailers[i];
         int len = t.length;
         System.arraycopy(t, 0, codfile, codfileEnd, len);
         codfileEnd += len;
      }

      return codfile;
   }

   public static final boolean deleteThirdPartyApplications() {
      ControlledAccess.assertRRISignatures(true);
      int[] moduleHandles = getModuleHandles();

      for (int i = moduleHandles.length - 1; i >= 0; i--) {
         int moduleHandle = moduleHandles[i];
         if (!ControlledAccess.verifyCodeModuleSignature(moduleHandle, 51) && !ControlledAccess.verifyCodeModuleSignature(moduleHandle, 4276818)) {
            System.out.println("Deleting cod file " + getModuleName(moduleHandle));
            deleteModuleExImpl(moduleHandle, true);
            setResetRequired();
         }
      }

      return isResetRequired();
   }
}
