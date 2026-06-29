package net.rim.device.api.system;

import net.rim.device.api.util.Persistable;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;

public final class ControlledAccess implements Persistable {
   private Object _obj;
   private CodeSigningKey _readKey;
   private CodeSigningKey _replaceKey;

   public ControlledAccess(Object obj) {
      this(obj, CodeSigningKey.get(obj));
   }

   public ControlledAccess(Object obj, CodeSigningKey readAndReplaceKey) {
      this(obj, readAndReplaceKey, readAndReplaceKey);
   }

   public ControlledAccess(Object obj, CodeSigningKey readKey, CodeSigningKey replaceKey) {
      this._obj = obj;
      this._readKey = readKey;
      this._replaceKey = replaceKey;
   }

   final Object getObject() {
      return this._obj;
   }

   final void assertReadPermission(int moduleHandle) {
      assertSignature(moduleHandle, this._readKey);
   }

   final void assertReplacePermission(int moduleHandle) {
      assertSignature(moduleHandle, this._replaceKey);
   }

   private static final void assertSignature(int moduleHandle, CodeSigningKey key) {
      if (key != null && !CodeModuleManager.verifySignature(moduleHandle, key.getSignerIdAsInt(), key.getPublicKeyInternal())) {
         throw new ControlledAccessException(key);
      }
   }

   public final boolean checkKeys(CodeSigningKey readKey, CodeSigningKey replaceKey) {
      return (readKey == null || readKey.equals(this._readKey)) && (replaceKey == null || replaceKey.equals(this._replaceKey));
   }

   public final void assertKeys(CodeSigningKey readKey, CodeSigningKey replaceKey) {
      if (!this.checkKeys(readKey, replaceKey)) {
         throw new ControlledAccessException();
      }
   }

   public static final boolean verifyCodeModuleSignature(int moduleHandle, int signerId) {
      return verifyCodeModuleSignature(moduleHandle, CodeSigningKey.getBuiltInKey(signerId));
   }

   public static final boolean verifyCodeModuleSignature(int moduleHandle, CodeSigningKey key) {
      return CodeModuleManager.verifySignature(moduleHandle, key.getSignerIdAsInt(), key.getPublicKeyInternal());
   }

   public static final void assertRRISignature(int moduleHandle) {
      if (!verifyCodeModuleSignature(moduleHandle, 51)) {
         throw new ControlledAccessException();
      }
   }

   public static final void assertRCISignature(int moduleHandle) {
      if (!verifyCodeModuleSignature(moduleHandle, 4801362)) {
         throw new ControlledAccessException();
      }
   }

   public static final boolean verifySignatures(boolean checkProcess, int signerId) {
      if (checkProcess) {
         Process process = Process.currentProcess();
         int moduleHandle = process.getModuleHandle();
         if (!verifyCodeModuleSignature(moduleHandle, signerId)) {
            return false;
         }
      }

      int[] moduleHandles = TraceBack.getCallingModules();

      for (int i = moduleHandles.length - 1; i >= 0; i--) {
         if (!verifyCodeModuleSignature(moduleHandles[i], signerId)) {
            return false;
         }
      }

      return true;
   }

   public static final boolean verifyRRISignature(int moduleHandle) {
      return verifyCodeModuleSignature(moduleHandle, 51);
   }

   public static final boolean verifyRRISignatures(boolean checkProcess) {
      return verifySignatures(checkProcess, 51);
   }

   public static final void assertRRISignatures(boolean checkProcess) {
      if (!verifyRRISignatures(checkProcess)) {
         throw new ControlledAccessException();
      }
   }

   public static final boolean verifyRCISignatures(boolean checkProcess) {
      return verifySignatures(checkProcess, 4801362);
   }

   public static final void assertRCISignatures(boolean checkProcess) {
      if (!verifyRCISignatures(checkProcess)) {
         throw new ControlledAccessException();
      }
   }
}
