package net.rim.device.internal.provisioning;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.TLEUtilities;

public class SuplHandler implements ProvisioningHandler {
   static final long SUPL_CONNECTION_PREFIX = -6699953639456723831L;
   static final long SUPL_APN = -2354981130308907098L;
   static final long SUPL_USERNAME = 7493617955115389875L;
   static final long SUPL_PASSWORD = 8604973231239970469L;
   private static String _connectionPrefix;
   private static String _apn;
   private static String _username;
   private static String _password;

   SuplHandler() {
   }

   @Override
   public void updateProvisioningData(IntIntHashtable chunks, DataBuffer db) {
      try {
         if (chunks != null && db != null) {
            if (chunks.containsKey(20)) {
               db.setPosition(chunks.get(20));
               _connectionPrefix = TLEUtilities.readStringField(db, true);
               PersistentObject _po = PersistentStore.getPersistentObject(-6699953639456723831L);
               Object _provisioningObj = PersistentContent.encode(_connectionPrefix, false, true);
               synchronized (_po) {
                  _po.setContents(_provisioningObj, 51, false);
                  _po.forceCommit();
               }
            } else {
               PersistentStore.destroyPersistentObject(-6699953639456723831L);
            }

            if (chunks.containsKey(35)) {
               db.setPosition(chunks.get(35));
               _apn = TLEUtilities.readStringField(db, true);
               PersistentObject _po = PersistentStore.getPersistentObject(-2354981130308907098L);
               Object _provisioningObj = PersistentContent.encode(_apn, false, true);
               synchronized (_po) {
                  _po.setContents(_provisioningObj, 51, false);
                  _po.forceCommit();
               }
            } else {
               PersistentStore.destroyPersistentObject(-2354981130308907098L);
            }

            if (chunks.containsKey(36)) {
               db.setPosition(chunks.get(36));
               _username = TLEUtilities.readStringField(db, true);
               PersistentObject _po = PersistentStore.getPersistentObject(7493617955115389875L);
               Object _provisioningObj = PersistentContent.encode(_username, false, true);
               synchronized (_po) {
                  _po.setContents(_provisioningObj, 51, false);
                  _po.forceCommit();
               }
            } else {
               PersistentStore.destroyPersistentObject(7493617955115389875L);
            }

            if (!chunks.containsKey(37)) {
               PersistentStore.destroyPersistentObject(8604973231239970469L);
               return;
            }

            db.setPosition(chunks.get(37));
            _password = TLEUtilities.readStringField(db, true);
            PersistentObject _po = PersistentStore.getPersistentObject(8604973231239970469L);
            Object _provisioningObj = PersistentContent.encode(_password, false, true);
            synchronized (_po) {
               _po.setContents(_provisioningObj, 51, false);
               _po.forceCommit();
            }
         }
      } finally {
         ;
      }
   }

   public static String getConnectionPrefix() {
      String connPrefix = "";
      PersistentObject _po = PersistentStore.getPersistentObject(-6699953639456723831L);
      Object _connectionObj = _po.getContents();
      if (_connectionObj != null) {
         connPrefix = PersistentContent.decodeString(_connectionObj);
      }

      System.out.println(((StringBuffer)(new Object("Retrieving SUPL connection prefix:"))).append(connPrefix).toString());
      return connPrefix;
   }

   public static String getApn() {
      String apn = "";
      PersistentObject _po = PersistentStore.getPersistentObject(-2354981130308907098L);
      Object _connectionObj = _po.getContents();
      if (_connectionObj != null) {
         apn = PersistentContent.decodeString(_connectionObj);
      }

      System.out.println(((StringBuffer)(new Object("Retrieving SUPL APN:"))).append(apn).toString());
      return apn;
   }

   public static String getUsername() {
      String username = "";
      PersistentObject _po = PersistentStore.getPersistentObject(7493617955115389875L);
      Object _connectionObj = _po.getContents();
      if (_connectionObj != null) {
         username = PersistentContent.decodeString(_connectionObj);
      }

      System.out.println(((StringBuffer)(new Object("Retrieving SUPL username:"))).append(username).toString());
      return username;
   }

   public static String getPassword() {
      String password = "";
      PersistentObject _po = PersistentStore.getPersistentObject(8604973231239970469L);
      Object _connectionObj = _po.getContents();
      if (_connectionObj != null) {
         password = PersistentContent.decodeString(_connectionObj);
      }

      System.out.println(((StringBuffer)(new Object("Retrieving SUPL password:"))).append(password).toString());
      return password;
   }
}
