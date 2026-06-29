package net.rim.device.api.applicationcontrol;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;

public final class ApplicationPermissions {
   private IntIntHashtable _permissions = new IntIntHashtable();
   public static final int PERMISSION_AUTHENTICATOR_API = 0;
   public static final int PERMISSION_BLUETOOTH = 1;
   public static final int PERMISSION_BROWSER_FILTER = 2;
   public static final int PERMISSION_CHANGE_DEVICE_SETTINGS = 3;
   public static final int PERMISSION_CODE_MODULE_MANAGEMENT = 4;
   public static final int PERMISSION_EMAIL = 5;
   public static final int PERMISSION_EVENT_INJECTOR = 6;
   public static final int PERMISSION_EXTERNAL_CONNECTIONS = 7;
   public static final int PERMISSION_FILE_API = 8;
   public static final int PERMISSION_HANDHELD_KEYSTORE = 9;
   public static final int PERMISSION_INTERNAL_CONNECTIONS = 10;
   public static final int PERMISSION_INTER_PROCESS_COMMUNICATION = 11;
   public static final int PERMISSION_INTER_PROCESS_COMMUNUCATION = 11;
   public static final int PERMISSION_KEYSTORE_MEDIUM_SECURITY = 12;
   public static final int PERMISSION_LOCAL_CONNECTIONS = 13;
   public static final int PERMISSION_LOCATION_API = 14;
   public static final int PERMISSION_PHONE = 15;
   public static final int PERMISSION_PIM = 16;
   public static final int PERMISSION_SCREEN_CAPTURE = 17;
   public static final int PERMISSION_THEME_DATA = 18;
   public static final int PERMISSION_WIFI = 19;
   public static final int PERMISSION_IDLE_TIMER = 20;
   public static final int PERMISSION_MEDIA = 21;
   public static final int VALUE_ALLOW = 999;
   public static final int VALUE_PROMPT = 998;
   public static final int VALUE_DENY = 997;
   public static final int VALUE_NOT_SET = -1;

   public final void addPermission(int permission) {
      this.addPermission(permission, 999);
   }

   public final void addPermission(int permission, int value) {
      if (!this.isValidPermission(permission)) {
         throw new IllegalArgumentException();
      }

      this._permissions.put(permission, value);
   }

   public final int getPermission(int permission) {
      if (!this.isValidPermission(permission)) {
         throw new IllegalArgumentException();
      } else {
         int result = this._permissions.get(permission);
         if (result != -1) {
            return result;
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public final int getPermissionInternal(int permission) {
      if (!this.isValidPermission(permission)) {
         throw new IllegalArgumentException();
      }

      int result = this._permissions.get(permission);
      return result != -1 ? result : 997;
   }

   public final int[] difference(ApplicationPermissions other) {
      if (other == null) {
         throw new NullPointerException();
      }

      int[] differenceSet = new int[0];
      IntIntHashtable otherPerms = other._permissions;
      int[] permissionKeys = this.getPermissionKeys();
      int currPermToCheck = -1;

      for (int i = 0; i < permissionKeys.length; i++) {
         currPermToCheck = permissionKeys[i];
         if (this._permissions.get(currPermToCheck) != otherPerms.get(currPermToCheck) && otherPerms.get(currPermToCheck) != -1) {
            Arrays.add(differenceSet, currPermToCheck);
         }
      }

      return differenceSet;
   }

   public final int[] getPermissionKeys() {
      int[] keys = new int[0];
      IntEnumeration keysEnum = this._permissions.keys();

      while (keysEnum.hasMoreElements()) {
         Arrays.add(keys, keysEnum.nextElement());
      }

      return keys;
   }

   public final boolean containsPermissionKey(int permission) {
      if (!this.isValidPermission(permission)) {
         throw new IllegalArgumentException();
      } else {
         return this._permissions.containsKey(permission);
      }
   }

   private final boolean isValidPermission(int permission) {
      switch (permission) {
         case -1:
            return false;
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         default:
            return true;
      }
   }

   public final void addAllPermissions() {
      this._permissions.put(0, 999);
      this._permissions.put(1, 999);
      this._permissions.put(2, 999);
      this._permissions.put(3, 999);
      this._permissions.put(4, 999);
      this._permissions.put(5, 999);
      this._permissions.put(6, 999);
      this._permissions.put(7, 999);
      this._permissions.put(8, 999);
      this._permissions.put(9, 999);
      this._permissions.put(11, 999);
      this._permissions.put(10, 999);
      this._permissions.put(12, 999);
      this._permissions.put(13, 999);
      this._permissions.put(14, 999);
      this._permissions.put(15, 999);
      this._permissions.put(16, 999);
      this._permissions.put(17, 999);
      this._permissions.put(18, 999);
      this._permissions.put(19, 999);
      this._permissions.put(20, 999);
      this._permissions.put(21, 999);
   }
}
