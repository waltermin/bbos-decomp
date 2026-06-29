package net.rim.device.apps.internal.memo;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.collection.util.PrefixKeywordFilterList;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.RIMModelFactoryCache;
import net.rim.device.apps.api.framework.model.RIMModelOrderHelper;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.memo.MemoCollection;
import net.rim.device.apps.api.utility.framework.SimplePersistentEncryptedSyncCollection;
import net.rim.device.apps.api.utility.framework.SimplePersistentSyncCollection$SimpleData;
import net.rim.device.apps.internal.memo.resources.MemoResources;

final class MemoCollectionImpl extends SimplePersistentEncryptedSyncCollection implements MemoCollection, OTASyncCapable, OTASyncPriorityProvider, SyncConverter {
   private SimplePersistentSyncCollection$SimpleData _data = (SimplePersistentSyncCollection$SimpleData)this._persistentObject.getContents();
   private KeywordFilterList _keywordList;
   private boolean _dirty;
   private static final int MEMO_INITIAL_SIZE = 16;
   private static final long MEMO_DATA_NAME = -1744790729544506738L;
   private static final long MEMO_COLLECTION_ID = -5965364677584041048L;
   private static ContextObjectWR _encodeContextWR = new ContextObjectWR(8, 35, 19);
   private static ContextObjectWR _decodeContextWR = new ContextObjectWR(8, 35, 19);
   private static RIMModelFactory[] _factoryCache = RIMModelFactoryCache.allocate();

   private MemoCollectionImpl() {
      super(new MemoCollectionImpl$MemoComparator(), -1744790729544506738L);
      this.initialize();
      this.commonCtorEpilogue();
   }

   private final synchronized void initialize() {
      if (this._data == null) {
         this._data = new SimplePersistentSyncCollection$SimpleData(16);
         super._persistentObject.setContents(this._data, 51);
         this.commit();
      }

      this.initList(this._data._items, 1);
   }

   static final MemoCollectionImpl getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         MemoCollectionImpl collection = (MemoCollectionImpl)ar.get(-5965364677584041048L);
         if (collection == null) {
            collection = new MemoCollectionImpl();
            ar.put(-5965364677584041048L, collection);
         }

         return collection;
      }
   }

   final boolean isDirty() {
      return this._dirty;
   }

   @Override
   protected final void clearPersistentData() {
      this._data = null;
      this.initialize();
   }

   @Override
   protected final String getContentProtectionEnabledMessage() {
      return MemoResources.getString(145);
   }

   final KeywordFilterList getKeywordList() {
      if (this._keywordList == null) {
         this._keywordList = new PrefixKeywordFilterList(this, new RIMModelOrderHelper(new MemoCollectionImpl$MemoComparator(), new ContextObject(35)), false);
      }

      return this._keywordList;
   }

   public final void markDirty(boolean dirty) {
      if (!MemoOptions.getOptions().isWirelessSyncAllowed()) {
         this._dirty = dirty;
      } else {
         dirty = false;
      }
   }

   private final void prepareForAdd(Object o) {
      if (!ObjectGroup.isInGroup(o)) {
         ObjectGroup.createGroupIgnoreTooBig(o);
      }
   }

   @Override
   public final void add(Object o) {
      this.prepareForAdd(o);
      super.add(o);
      this.markDirty(true);
   }

   @Override
   public final void update(Object oldObject, Object newObject) {
      this.prepareForAdd(newObject);
      super.update(oldObject, newObject);
      this.markDirty(true);
   }

   @Override
   public final void remove(Object o) {
      super.remove(o);
      this.markDirty(true);
   }

   @Override
   public final void removeAll() {
      super.removeAll();
      this.markDirty(true);
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Memos";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final int getSyncObjectCount() {
      return this._data._items.size();
   }

   @Override
   public final int getSyncPriority() {
      return 8;
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object instanceof RIMModel) {
         SyncBuffer syncBuffer = new SyncBuffer(buffer, version, object.getUID());
         return syncBuffer.addModel((RIMModel)object, _encodeContextWR.getContextObject());
      } else {
         return false;
      }
   }

   @Override
   public final SyncObject convert(DataBuffer param1, int param2, int param3) {
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
      // 00: iload 3
      // 01: ifne 0b
      // 04: invokestatic net/rim/device/api/synchronization/UIDGenerator.getUniqueScopingValue ()I
      // 07: invokestatic net/rim/device/api/synchronization/UIDGenerator.getUID (I)I
      // 0a: istore 3
      // 0b: new net/rim/device/apps/internal/memo/MemoModelImpl
      // 0e: dup
      // 0f: iload 3
      // 10: invokespecial net/rim/device/apps/internal/memo/MemoModelImpl.<init> (I)V
      // 13: astore 4
      // 15: new net/rim/device/apps/api/framework/model/SyncBuffer
      // 18: dup
      // 19: aload 1
      // 1a: iload 2
      // 1b: iload 3
      // 1c: invokespecial net/rim/device/apps/api/framework/model/SyncBuffer.<init> (Lnet/rim/device/api/util/DataBuffer;II)V
      // 1f: astore 5
      // 21: ldc2_w 8809206174646860213
      // 24: invokestatic net/rim/device/apps/api/framework/registration/RIMModelFactoryRepository.getModelFactories (J)[Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;
      // 27: astore 6
      // 29: getstatic net/rim/device/apps/internal/memo/MemoCollectionImpl._decodeContextWR Lnet/rim/device/apps/api/framework/model/ContextObjectWR;
      // 2c: invokevirtual net/rim/device/apps/api/framework/model/ContextObjectWR.getContextObject ()Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 2f: astore 7
      // 31: aload 7
      // 33: sipush 255
      // 36: i2l
      // 37: aload 5
      // 39: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 3c: pop
      // 3d: getstatic net/rim/device/apps/internal/memo/MemoCollectionImpl._factoryCache [Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;
      // 40: aload 6
      // 42: aconst_null
      // 43: aload 5
      // 45: aload 4
      // 47: aload 7
      // 49: invokestatic net/rim/device/apps/api/framework/model/RIMModelFactoryCache.addToModelWithCache ([Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;[Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;Lnet/rim/device/apps/api/framework/model/SyncBuffer;Lnet/rim/device/api/collection/WritableSet;Lnet/rim/device/apps/api/framework/model/ContextObject;)V
      // 4c: aload 7
      // 4e: sipush 255
      // 51: i2l
      // 52: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 55: pop
      // 56: goto 77
      // 59: astore 8
      // 5b: aload 7
      // 5d: sipush 255
      // 60: i2l
      // 61: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 64: pop
      // 65: goto 77
      // 68: astore 9
      // 6a: aload 7
      // 6c: sipush 255
      // 6f: i2l
      // 70: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 73: pop
      // 74: aload 9
      // 76: athrow
      // 77: aload 4
      // 79: areturn
      // try (29 -> 36): 42 null
      // try (29 -> 36): 49 null
      // try (42 -> 43): 49 null
      // try (49 -> 50): 49 null
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }
}
