package net.rim.device.apps.api.quickcontact;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;

public class QuickContactItem implements PersistableRIMModel, SyncObject, EncryptableProvider {
   private int _uid;
   private char _key;
   static final int QUICK_CONTACT_ITEM_TAG = 1;
   static final int QUICK_CONTACT_KEY_TAG = 2;
   private static GhostQuickContactItem _ghostItem = new GhostQuickContactItem();

   public boolean isValidItem() {
      throw null;
   }

   public boolean convertTypeAndKey(long type, Object context, Object target) {
      if (ContextObject.getFlag(context, 79) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addLong(1, type);
         syncBuffer.addInt(2, this._key, 2);
         return true;
      } else {
         return false;
      }
   }

   public char getKey() {
      return this._key;
   }

   @Override
   public int getUID() {
      return this._uid;
   }

   public Object getAddress() {
      throw null;
   }

   public boolean matchAddress(Object _1, Object _2) {
      throw null;
   }

   public String getAddressTypeString() {
      throw null;
   }

   public String getCommTypeString() {
      throw null;
   }

   public String getFriendlyNameString() {
      throw null;
   }

   public String getRawAddressString() {
      throw null;
   }

   public boolean invoke() {
      throw null;
   }

   public void edit() {
      throw null;
   }

   public void view() {
      throw null;
   }

   public String getInvokeVerbString() {
      throw null;
   }

   public boolean addressBookUpdated(int _1, Object _2) {
      throw null;
   }

   public void setKey(char key) {
      this._key = key;
   }

   @Override
   public Object reCrypt(boolean _1, boolean _2) {
      throw null;
   }

   @Override
   public boolean checkCrypt(boolean _1, boolean _2) {
      throw null;
   }

   public static boolean recognize(long param0, SyncBuffer param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 2
      // 01: ifnull 45
      // 04: aload 2
      // 05: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.getFieldType ()I
      // 08: bipush 1
      // 09: if_icmpne 45
      // 0c: aload 2
      // 0d: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.getPosition ()I
      // 10: istore 3
      // 11: bipush 0
      // 12: istore 4
      // 14: aload 2
      // 15: bipush 1
      // 16: bipush 0
      // 17: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.getLong (IZ)J
      // 1a: lload 0
      // 1b: lcmp
      // 1c: ifne 23
      // 1f: bipush 1
      // 20: goto 24
      // 23: bipush 0
      // 24: istore 4
      // 26: aload 2
      // 27: iload 3
      // 28: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.setPosition (I)V
      // 2b: goto 42
      // 2e: astore 5
      // 30: aload 2
      // 31: iload 3
      // 32: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.setPosition (I)V
      // 35: goto 42
      // 38: astore 6
      // 3a: aload 2
      // 3b: iload 3
      // 3c: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.setPosition (I)V
      // 3f: aload 6
      // 41: athrow
      // 42: iload 4
      // 44: ireturn
      // 45: bipush 0
      // 46: ireturn
      // try (11 -> 22): 26 null
      // try (11 -> 22): 31 null
      // try (26 -> 27): 31 null
      // try (31 -> 32): 31 null
   }

   public static char getKey(long type, SyncBuffer syncBuffer) {
      int position = syncBuffer.getPosition();
      if (type != syncBuffer.getLong(1, true)) {
         syncBuffer.setPosition(position);
         throw new Object();
      } else {
         return (char)syncBuffer.getInt(2, true);
      }
   }

   public static Object getGhostItem() {
      return _ghostItem;
   }

   public static boolean isGhostItem(Object o) {
      return o == _ghostItem;
   }

   public QuickContactItem(int uid, char key) {
      this._uid = uid;
      this._key = key;
   }

   public QuickContactItem(char key) {
      this(UIDGenerator.getUID(), key);
   }
}
