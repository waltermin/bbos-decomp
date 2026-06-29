package net.rim.device.apps.internal.addressbook.addresscard;

import java.util.Vector;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModelFactoryCache;
import net.rim.device.apps.api.framework.model.RIMModelSyncConverter;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.vm.WeakReference;

final class AddressCardModelFactory extends RIMModelFactory {
   private Verb[] _addressBookVerbs;
   private RIMModelFactory _opaqueFactory;
   private RIMModelFactory[] _factoryCache = RIMModelFactoryCache.allocate();
   private RIMModelFactory[] _reverseLookupFactoryCache = RIMModelFactoryCache.allocate();
   private ContextObject _decodeContext;
   private WeakReference _dataBufferWR;
   private WeakReference _syncBufferWR;
   private SyncConverter _syncConverter;
   public static final boolean USE_COMPRESSED_ADDRESS_CARDS = true;

   AddressCardModelFactory() {
      this._opaqueFactory = (RIMModelFactory)ApplicationRegistry.getApplicationRegistry().waitFor(-8058545440370075039L);
      this._decodeContext = new ContextObject(11, 19);
      this._dataBufferWR = new WeakReference(null);
      this._syncBufferWR = new WeakReference(null);
      this._syncConverter = new RIMModelSyncConverter(18, -7921492803965144520L);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object createInstance(Object initialData) {
      AddressCardModel addressCard = null;
      if (ContextObject.getFlag(initialData, 18) && ContextObject.getFlag(initialData, 19)) {
         synchronized (this._decodeContext) {
            label254: {
               boolean var12 = false /* VF: Semaphore variable */;
               boolean var16 = false /* VF: Semaphore variable */;

               Object var24;
               label234: {
                  label233: {
                     label232: {
                        try {
                           try {
                              var16 = true;
                              var12 = true;
                              SyncBuffer var22 = (SyncBuffer)ContextObject.get(initialData, 255);
                              if (var22 == null) {
                                 var24 = null;
                                 var12 = false;
                                 var16 = false;
                                 break label234;
                              }

                              addressCard = new CompressedAddressCardModel(initialData, var22);
                              addressCard.setUID(var22.getUID());
                              var12 = false;
                              var16 = false;
                              break label233;
                           } finally {
                              if (var16) {
                                 label228: {
                                    if (addressCard == null) {
                                       var24 = null;
                                       var12 = false;
                                       break label232;
                                    }

                                    var12 = false;
                                    break label228;
                                 }
                              }
                           }
                        } finally {
                           if (var12) {
                              this._decodeContext.remove(255);
                           }
                        }

                        this._decodeContext.remove(255);
                        break label254;
                     }

                     this._decodeContext.remove(255);
                     return var24;
                  }

                  this._decodeContext.remove(255);
                  break label254;
               }

               this._decodeContext.remove(255);
               return var24;
            }
         }

         if (addressCard.getUID() == 0) {
            addressCard.setUID(AddressCardUtilities.generateUniqueID());
         }

         return this.validateModel(addressCard);
      } else if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
         Vector data = (Vector)ContextObject.get(initialData, 249);
         RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(-5785746452676094833L);
         int numFactories = factories.length;
         if (data == null) {
            return null;
         }

         addressCard = new AddressCardModelImpl(null);

         while (!data.isEmpty() && data.size() >= 2) {
            RIMModelFactory factory = null;
            int i = 0;

            while (true) {
               if (i < numFactories) {
                  factory = factories[i];
                  if (!factory.recognize(initialData)) {
                     i++;
                     continue;
                  }
               } else {
                  factory = null;
               }

               Object headItem = data.elementAt(1);
               if (factory != null) {
                  Object tmpModel = factory.createInstance(initialData);
                  if (tmpModel != null) {
                     addressCard.add(tmpModel);
                  }
               }

               if (data.size() > 1 && headItem == data.elementAt(1)) {
                  data.removeElementAt(0);
                  data.removeElementAt(0);
               }
               break;
            }
         }

         return this.validateModel(addressCard);
      } else {
         return new AddressCardModelImpl(initialData);
      }
   }

   private final AddressCardModel validateModel(AddressCardModel addressCard) {
      if (addressCard != null) {
         if (!addressCard.isValid()) {
            Recognizer emailAddressRecognizer = RecognizerRepository.getRecognizers(-2985347935260258684L);
            Object emailAddress = SubmemberUtilities.getFirstSubmember(addressCard, emailAddressRecognizer);
            if (emailAddress != null) {
               PersonNameModel personNameModel = new PersonNameModelImpl(null, emailAddress.toString(), null);
               addressCard.add(personNameModel);
               AddressCardCache.remove(addressCard);
            }
         }

         if (!addressCard.isValid()) {
            PersonNameModel personNameModel = new PersonNameModelImpl();
            addressCard.add(personNameModel);
         }

         AddressCardUtilities.createGroup(addressCard);
      }

      return addressCard;
   }

   @Override
   public final int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public final boolean recognize(Object object) {
      if (object instanceof AddressCardModel) {
         return true;
      }

      if (ContextObject.getFlag(object, 18) && ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         if (syncBuffer == null) {
            return false;
         }

         int position = syncBuffer.getPosition();

         try {
            if (syncBuffer.containsType(44)) {
               byte[] typeByte = syncBuffer.getBytes(44, false);
               if (typeByte.length > 0 && typeByte[0] == 71) {
                  return false;
               }
            }
         } finally {
            ;
         }

         syncBuffer.setPosition(position);
         return syncBuffer.containsType(32, true) || syncBuffer.containsType(33, true) || syncBuffer.containsType(1, true);
      } else {
         return false;
      }
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      if (ContextObject.getFlag(context, 18)) {
         if (this._addressBookVerbs == null) {
            this._addressBookVerbs = new Verb[]{new AddressCardVerb(0, 610816)};
         }

         return this._addressBookVerbs;
      } else {
         return null;
      }
   }

   final AddressCardModel uncompressCard(CompressedAddressCardModel compressedCard) {
      return this.uncompressCard(compressedCard, false);
   }

   final AddressCardModel uncompressCard(CompressedAddressCardModel param1, boolean param2) {
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
      // 000: new net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelImpl
      // 003: dup
      // 004: invokespecial net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelImpl.<init> ()V
      // 007: astore 3
      // 008: aload 1
      // 009: invokevirtual net/rim/device/apps/internal/addressbook/addresscard/CompressedAddressCardModel.getUID ()I
      // 00c: istore 4
      // 00e: aload 3
      // 00f: iload 4
      // 011: invokeinterface net/rim/device/apps/api/addressbook/AddressCardElement.setUID (I)V 2
      // 016: iload 2
      // 017: ifne 041
      // 01a: aload 3
      // 01b: aload 1
      // 01c: invokevirtual net/rim/device/apps/internal/addressbook/addresscard/CompressedAddressCardModel.getName ()Lnet/rim/device/apps/api/addressbook/PersonNameModel;
      // 01f: invokeinterface net/rim/device/api/collection/WritableSet.add (Ljava/lang/Object;)V 2
      // 024: aload 3
      // 025: aload 1
      // 026: invokevirtual net/rim/device/apps/internal/addressbook/addresscard/CompressedAddressCardModel.getCompanyInfo ()Lnet/rim/device/apps/api/addressbook/CompanyInfoModel;
      // 029: invokeinterface net/rim/device/api/collection/WritableSet.add (Ljava/lang/Object;)V 2
      // 02e: aload 3
      // 02f: aload 1
      // 030: new net/rim/device/apps/api/framework/model/ContextObject
      // 033: dup
      // 034: bipush 11
      // 036: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> (I)V
      // 039: invokevirtual net/rim/device/apps/internal/addressbook/addresscard/CompressedAddressCardModel.getContactPicture (Ljava/lang/Object;)Lnet/rim/device/apps/api/addressbook/DisplayPictureModel;
      // 03c: invokeinterface net/rim/device/api/collection/WritableSet.add (Ljava/lang/Object;)V 2
      // 041: aload 0
      // 042: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._decodeContext Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 045: dup
      // 046: astore 5
      // 048: monitorenter
      // 049: aload 1
      // 04a: invokevirtual net/rim/device/apps/internal/addressbook/addresscard/CompressedAddressCardModel.getDataCopy ()[B
      // 04d: astore 6
      // 04f: aload 0
      // 050: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._dataBufferWR Lnet/rim/vm/WeakReference;
      // 053: bipush 0
      // 054: invokestatic net/rim/device/api/util/WeakReferenceUtilities.getDataBuffer (Lnet/rim/vm/WeakReference;Z)Lnet/rim/device/api/util/DataBuffer;
      // 057: astore 7
      // 059: aload 6
      // 05b: ifnull 06a
      // 05e: aload 7
      // 060: aload 6
      // 062: bipush 0
      // 063: aload 6
      // 065: arraylength
      // 066: bipush 0
      // 067: invokevirtual net/rim/device/api/util/DataBuffer.setData ([BIIZ)V
      // 06a: aload 0
      // 06b: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._syncBufferWR Lnet/rim/vm/WeakReference;
      // 06e: invokestatic net/rim/device/apps/api/framework/model/SyncBuffer.getSyncBuffer (Lnet/rim/vm/WeakReference;)Lnet/rim/device/apps/api/framework/model/SyncBuffer;
      // 071: astore 8
      // 073: aload 8
      // 075: aload 7
      // 077: bipush 0
      // 078: iload 4
      // 07a: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.initialize (Lnet/rim/device/api/util/DataBuffer;II)V
      // 07d: iload 2
      // 07e: ifeq 095
      // 081: ldc2_w -5785746452676094832
      // 084: invokestatic net/rim/device/apps/api/framework/registration/RIMModelFactoryRepository.getModelFactories (J)[Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;
      // 087: astore 9
      // 089: aconst_null
      // 08a: astore 10
      // 08c: aload 0
      // 08d: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._reverseLookupFactoryCache [Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;
      // 090: astore 11
      // 092: goto 0a9
      // 095: ldc2_w -5785746452676094833
      // 098: invokestatic net/rim/device/apps/api/framework/registration/RIMModelFactoryRepository.getModelFactories (J)[Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;
      // 09b: astore 9
      // 09d: aload 0
      // 09e: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._opaqueFactory Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;
      // 0a1: astore 10
      // 0a3: aload 0
      // 0a4: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._factoryCache [Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;
      // 0a7: astore 11
      // 0a9: aload 0
      // 0aa: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._decodeContext Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0ad: sipush 255
      // 0b0: i2l
      // 0b1: aload 8
      // 0b3: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 0b6: pop
      // 0b7: aload 11
      // 0b9: aload 9
      // 0bb: aload 10
      // 0bd: aload 8
      // 0bf: aload 3
      // 0c0: aload 0
      // 0c1: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._decodeContext Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0c4: invokestatic net/rim/device/apps/api/framework/model/RIMModelFactoryCache.addToModelWithCache ([Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;[Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;Lnet/rim/device/apps/api/framework/registration/RIMModelFactory;Lnet/rim/device/apps/api/framework/model/SyncBuffer;Lnet/rim/device/api/collection/WritableSet;Lnet/rim/device/apps/api/framework/model/ContextObject;)V
      // 0c7: aload 0
      // 0c8: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._decodeContext Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0cb: sipush 255
      // 0ce: i2l
      // 0cf: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 0d2: pop
      // 0d3: goto 0f8
      // 0d6: astore 12
      // 0d8: aload 0
      // 0d9: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._decodeContext Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0dc: sipush 255
      // 0df: i2l
      // 0e0: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 0e3: pop
      // 0e4: goto 0f8
      // 0e7: astore 13
      // 0e9: aload 0
      // 0ea: getfield net/rim/device/apps/internal/addressbook/addresscard/AddressCardModelFactory._decodeContext Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 0ed: sipush 255
      // 0f0: i2l
      // 0f1: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 0f4: pop
      // 0f5: aload 13
      // 0f7: athrow
      // 0f8: aload 5
      // 0fa: monitorexit
      // 0fb: aload 3
      // 0fc: areturn
      // 0fd: astore 14
      // 0ff: aload 5
      // 101: monitorexit
      // 102: aload 14
      // 104: athrow
      // try (79 -> 94): 101 null
      // try (79 -> 94): 109 null
      // try (101 -> 102): 109 null
      // try (109 -> 110): 109 null
      // try (33 -> 120): 122 null
      // try (122 -> 125): 122 null
   }

   private final AddressCardModel internalCompressCard(AddressCardModel addressCard) {
      AddressCardModel model = null;
      synchronized (this._decodeContext) {
         DataBuffer _dataBuffer = WeakReferenceUtilities.getDataBuffer(this._dataBufferWR, false);
         _dataBuffer.reset();
         if (this._syncConverter.convert((SyncObject)addressCard, _dataBuffer, 0)) {
            _dataBuffer.rewind();
            model = (AddressCardModel)this._syncConverter.convert(_dataBuffer, 0, addressCard.getUID());
         }

         return model;
      }
   }

   static final AddressCardModel compressCard(AddressCardModel addressCard) {
      AddressCardModel newCard = ((AddressCardModelFactory)ApplicationRegistry.getApplicationRegistry().waitFor(-3124646573404667739L))
         .internalCompressCard(addressCard);
      if (newCard != null) {
         addressCard = newCard;
      }

      return addressCard;
   }
}
