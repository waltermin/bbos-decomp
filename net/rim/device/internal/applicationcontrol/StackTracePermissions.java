package net.rim.device.internal.applicationcontrol;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntHashtable;

final class StackTracePermissions {
   private IntHashtable _delegate;
   private PersistentObject _persistentDelegate = RIMPersistentStore.getPersistentObject(2849651058031202388L);
   private static final long STACK_HASHES_KEY = 2849651058031202388L;

   StackTracePermissions() {
      synchronized (this._persistentDelegate) {
         this._delegate = (IntHashtable)this._persistentDelegate.getContents();
         if (this._delegate == null) {
            this._delegate = new IntHashtable();
            this._persistentDelegate.setContents(this._delegate, 51);
            this._persistentDelegate.commit();
         }
      }
   }

   final void setResponse(int processHandle, int[] stackHandles, int allowFlag, int promptFlag, boolean allow) {
      byte[] stackHash = this.getHashOfStackTrace(stackHandles);
      if (stackHash != null) {
         Vector stackTracePermissionsVector = this.getResponses(processHandle);
         if (stackTracePermissionsVector == null) {
            stackTracePermissionsVector = new Vector();
            this._delegate.put(processHandle, stackTracePermissionsVector);
         }

         CachedStackTraceResponse stackTraceResponse = this.getResponse(stackTracePermissionsVector, stackHash);
         if (stackTraceResponse == null) {
            stackTraceResponse = new CachedStackTraceResponse(stackHash);
            stackTracePermissionsVector.addElement(stackTraceResponse);
         }

         stackTraceResponse.setAllowed(allowFlag, promptFlag, allow);
         this.persistChanges();
      }
   }

   final int getResponsePermission(int processHandle, int[] stackHandles, int allowFlag, int promptFlag) {
      byte[] stackHash = this.getHashOfStackTrace(stackHandles);
      CachedStackTraceResponse stackTraceResponse = this.getResponse(processHandle, stackHash);
      return CachedStackTraceResponse.responseToPermission(stackTraceResponse, allowFlag, promptFlag);
   }

   final void removeAllResponses() {
      this._delegate.clear();
      this.persistChanges();
   }

   final void removeAllResponses(int allowFlag, int promptFlag) {
      Enumeration processVectors = this._delegate.elements();
      Vector stackTracePermissionsVector = null;

      while (processVectors.hasMoreElements()) {
         stackTracePermissionsVector = (Vector)processVectors.nextElement();
         this.removeResponses(stackTracePermissionsVector, allowFlag, promptFlag);
      }

      this.persistChanges();
   }

   final void removeResponses(int processHandle) {
      this._delegate.remove(processHandle);
      this.persistChanges();
   }

   final void removeResponses(int processHandle, int allowFlag, int promptFlag) {
      this.removeResponses(this.getResponses(processHandle), allowFlag, promptFlag);
      this.persistChanges();
   }

   private final CachedStackTraceResponse getResponse(int processHandle, byte[] stackHash) {
      Vector stackTracePermissionsVector = this.getResponses(processHandle);
      return stackTracePermissionsVector == null ? null : this.getResponse(stackTracePermissionsVector, stackHash);
   }

   private final CachedStackTraceResponse getResponse(Vector stackTracePermissionsVector, byte[] stackHash) {
      Enumeration responseElements = stackTracePermissionsVector.elements();
      CachedStackTraceResponse stackTracePermissions = null;

      while (responseElements.hasMoreElements()) {
         stackTracePermissions = (CachedStackTraceResponse)responseElements.nextElement();
         if (stackTracePermissions.equals(stackHash)) {
            return stackTracePermissions;
         }
      }

      return null;
   }

   private final Vector getResponses(int processHandle) {
      return (Vector)this._delegate.get(processHandle);
   }

   private final void removeResponses(Vector stackTracePermissionVector, int allowFlag, int promptFlag) {
      Enumeration responseElements = stackTracePermissionVector.elements();
      CachedStackTraceResponse stackTracePermissions = null;

      while (responseElements.hasMoreElements()) {
         stackTracePermissions = (CachedStackTraceResponse)responseElements.nextElement();
         stackTracePermissions.reset(allowFlag, promptFlag);
      }
   }

   private final void persistChanges() {
      this._persistentDelegate.commit();
   }

   private final byte[] getHashOfStackTrace(int[] handles) {
      try {
         SHA1Digest sha1 = (SHA1Digest)DigestFactory.getInstance("SHA1");

         for (int i = 0; i < handles.length; i++) {
            sha1.update(handles[i]);
         }

         return sha1.getDigest();
      } catch (NoSuchAlgorithmException var4) {
         return null;
      }
   }
}
