package net.rim.blackberry.api.phone.phonelogs;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.device.apps.internal.phone.data.PhoneFolders;

final class PhoneLogListenerManager implements CollectionListener {
   private Vector _listeners;
   private PersistedSortedCollection _calls = PhoneFolders.getContainedItems(5390902206192375236L);
   private PersistedSortedCollection _missedCalls = PhoneFolders.getContainedItems(7042951934619290849L);
   private static final long ID = -903902942794875202L;
   static final int LOG_ADDED = 0;
   static final int LOG_UPDATED = 1;
   static final int LOG_REMOVED = 2;
   static final int RESET = 3;

   final void register(PhoneLogListener l) {
      if (!this._listeners.contains(l)) {
         this._listeners.addElement(l);
      }
   }

   final void deregister(PhoneLogListener l) {
      this._listeners.removeElement(l);
   }

   final void dispatch(int var1, CallLog var2, CallLog var3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Could not find destination nodes for stat id {Block}:17 from source 46_condition
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.FlattenStatementsHelper.setEdges(FlattenStatementsHelper.java:563)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.FlattenStatementsHelper.buildDirectGraph(FlattenStatementsHelper.java:50)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.<init>(VarDefinitionHelper.java:151)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.<init>(VarDefinitionHelper.java:52)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarDefinitions(VarProcessor.java:52)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:458)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/blackberry/api/phone/phonelogs/PhoneLogListenerManager._listeners Ljava/util/Vector;
      // 04: ifnull 85
      // 07: aload 2
      // 08: ifnonnull 12
      // 0b: iload 1
      // 0c: bipush 3
      // 0e: if_icmpeq 12
      // 11: return
      // 12: aload 0
      // 13: getfield net/rim/blackberry/api/phone/phonelogs/PhoneLogListenerManager._listeners Ljava/util/Vector;
      // 16: invokevirtual java/util/Vector.size ()I
      // 19: bipush 1
      // 1a: isub
      // 1b: istore 4
      // 1d: iload 4
      // 1f: iflt 85
      // 22: aload 0
      // 23: getfield net/rim/blackberry/api/phone/phonelogs/PhoneLogListenerManager._listeners Ljava/util/Vector;
      // 26: iload 4
      // 28: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 2b: checkcast net/rim/blackberry/api/phone/phonelogs/PhoneLogListener
      // 2e: astore 5
      // 30: iload 1
      // 31: tableswitch 35 -1 3 76 35 46 58 69
      // 54: aload 5
      // 56: aload 2
      // 57: invokeinterface net/rim/blackberry/api/phone/phonelogs/PhoneLogListener.callLogAdded (Lnet/rim/blackberry/api/phone/phonelogs/CallLog;)V 2
      // 5c: goto 7d
      // 5f: aload 5
      // 61: aload 2
      // 62: aload 3
      // 63: invokeinterface net/rim/blackberry/api/phone/phonelogs/PhoneLogListener.callLogUpdated (Lnet/rim/blackberry/api/phone/phonelogs/CallLog;Lnet/rim/blackberry/api/phone/phonelogs/CallLog;)V 3
      // 68: goto 7d
      // 6b: aload 5
      // 6d: aload 2
      // 6e: invokeinterface net/rim/blackberry/api/phone/phonelogs/PhoneLogListener.callLogRemoved (Lnet/rim/blackberry/api/phone/phonelogs/CallLog;)V 2
      // 73: goto 7d
      // 76: aload 5
      // 78: invokeinterface net/rim/blackberry/api/phone/phonelogs/PhoneLogListener.reset ()V 1
      // 7d: iinc 4 -1
      // 80: goto 1d
      // 83: astore 4
      // 85: return
      // try (9 -> 42): 42 null
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (this.validate(collection, element)) {
         this.dispatch(2, element, null);
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this.validate(collection, newElement)) {
         this.dispatch(1, newElement, oldElement);
      }
   }

   @Override
   public final void reset(Collection collection) {
      if (collection == this._calls || collection == this._missedCalls) {
         this.dispatch(3, (CallLog)null, (CallLog)null);
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (this.validate(collection, element)) {
         this.dispatch(0, element, null);
      }
   }

   private final boolean validate(Collection c, Object o) {
      return o instanceof Object && (c == this._calls || c == this._missedCalls);
   }

   private PhoneLogListenerManager() {
      this._listeners = (Vector)(new Object());
      this._calls.addCollectionListener(this);
      this._missedCalls.addCollectionListener(this);
   }

   private final void dispatch(int type, Object internalModel, Object oldInternalModel) {
      CallLog cl = internalModel == null ? null : PhoneLogs.createCallLogFromInternalModel(internalModel);
      CallLog oldCl = oldInternalModel == null ? null : PhoneLogs.createCallLogFromInternalModel(oldInternalModel);
      this.dispatch(type, cl, oldCl);
   }

   public static final PhoneLogListenerManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      PhoneLogListenerManager pllm = (PhoneLogListenerManager)ar.getOrWaitFor(-903902942794875202L);
      if (pllm == null) {
         pllm = new PhoneLogListenerManager();
         ar.put(-903902942794875202L, pllm);
      }

      return pllm;
   }
}
