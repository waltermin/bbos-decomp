package net.rim.device.api.system;

import net.rim.device.api.util.Persistable;
import net.rim.vm.Persistence;
import net.rim.vm.PersistentRootObject;
import net.rim.vm.TraceBack;

public final class PersistentObject extends PersistentRootObject implements Persistable {
   PersistentObject() {
   }

   public final void commit() {
      Persistence.commit(this, false);
   }

   public final void forceCommit() {
      Persistence.commit(this, true);
   }

   public static final void commit(Object obj) {
      Persistence.commit(obj, false);
   }

   public static final void forceCommit(Object obj) {
      Persistence.commit(obj, true);
   }

   public final synchronized Object getContents() {
      int caller = TraceBack.getCallingModule(0);
      return this.getContents(caller, null, null);
   }

   public final synchronized Object getContents(CodeSigningKey readAndReplaceKey) {
      int caller = TraceBack.getCallingModule(0);
      return this.getContents(caller, readAndReplaceKey, readAndReplaceKey);
   }

   public final Object getContents(CodeSigningKey readKey, CodeSigningKey replaceKey) {
      int caller = TraceBack.getCallingModule(0);
      return this.getContents(caller, readKey, replaceKey);
   }

   private final synchronized Object getContents(int moduleHandle, CodeSigningKey readKey, CodeSigningKey replaceKey) {
      Object obj = super._contents;
      if (!(obj instanceof ControlledAccess)) {
         if (obj != null && (readKey != null || replaceKey != null)) {
            throw new ControlledAccessException();
         }
      } else {
         ControlledAccess ca = (ControlledAccess)obj;
         obj = ca.getObject();
         ca.assertReadPermission(moduleHandle);
         ca.assertKeys(readKey, replaceKey);
      }

      return obj;
   }

   public final synchronized void setContents(Object contents) {
      int caller = TraceBack.getCallingModule(0);
      this.setContents(caller, contents);
   }

   public final void setContents(Object contents, int signerId) {
      int caller = TraceBack.getCallingModule(0);
      CodeSigningKey replaceKey = CodeSigningKey.get(caller, signerId);
      if (replaceKey == null) {
         throw new IllegalArgumentException("Key not found for signerId 0x" + Integer.toHexString(signerId) + " (\"" + CodeSigningKey.convert(signerId) + "\")");
      }

      CodeSigningKey readKey = replaceKey;
      this.setContents(caller, new ControlledAccess(contents, readKey, replaceKey));
   }

   public final void setContents(Object contents, int signerId, boolean preventReadAccess) {
      int caller = TraceBack.getCallingModule(0);
      CodeSigningKey replaceKey = CodeSigningKey.get(caller, signerId);
      if (replaceKey == null) {
         throw new IllegalArgumentException("Key not found for signerId 0x" + Integer.toHexString(signerId) + " (\"" + CodeSigningKey.convert(signerId) + "\")");
      }

      CodeSigningKey readKey = preventReadAccess ? replaceKey : null;
      this.setContents(caller, new ControlledAccess(contents, readKey, replaceKey));
   }

   private final synchronized void setContents(int moduleHandle, Object contents) {
      if (super._contents instanceof ControlledAccess) {
         ControlledAccess ca = (ControlledAccess)super._contents;
         ca.assertReplacePermission(moduleHandle);
      }

      super._contents = contents;
   }

   public final ControlledAccess getControlledAccess() {
      return !(super._contents instanceof ControlledAccess) ? new ControlledAccess(super._contents, null, null) : (ControlledAccess)super._contents;
   }

   public final void setReservedMemorySize(int size) {
      if (size < 0) {
         throw new IllegalArgumentException();
      }

      super._reservedMemorySize = size;
      this.forceCommit();
   }
}
