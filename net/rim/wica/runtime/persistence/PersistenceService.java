package net.rim.wica.runtime.persistence;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.RuntimeInfo;
import net.rim.wica.runtime.util.LongLongHashtable;

public interface PersistenceService {
   Vector loadRestoredApplications();

   void storeRestoredApplications(Vector var1);

   void storeRestoredAG(AGInfo var1);

   AGInfo loadRestoredAG();

   WicletStore createApplication(long var1);

   WicletStore getApplication(long var1);

   WicletStore getApplication(String var1);

   Enumeration getApplications();

   void deleteApplication(WicletStore var1);

   MessageStore getMessageStore();

   CredentialStore getCredentialStore();

   void storeRuntimeInfo(RuntimeInfo var1);

   RuntimeInfo loadRuntimeInfo();

   Object[] loadREKeyPair();

   void storeREKeyPair(Object[] var1);

   void storeAGPublicKeys(byte[] var1);

   byte[] loadAGPublicKeys();

   void storeSecondaryRegKeys(byte[] var1);

   byte[] loadSecondaryRegKeys();

   void storeRegKeys(byte[] var1);

   byte[] loadRegKeys();

   void storeRecoveryRegKeys(byte[] var1);

   byte[] loadRecoveryRegKeys();

   void storeRecoveryResetKeys(byte[] var1);

   byte[] loadRecoveryResetKeys();

   void storeResetKeys(byte[] var1);

   byte[] loadResetKeys();

   void storeWicletAlias(LongLongHashtable var1);

   LongLongHashtable loadWicletAlias();

   void storeMessageUpgradeMap(long var1, IntIntHashtable var3);

   IntIntHashtable loadMessageUpgradeMap(long var1);

   void deleteMessageUpgradeMap(long var1);

   LongIntHashtable loadWicletStatus();

   void storeWicletStatus(LongIntHashtable var1);

   long[] loadIncomingSequence();

   void storeIncomingSequence(long[] var1);

   void storeIncomingSequence(PersistableObject var1);

   long[] loadOutgoingSequence();

   void storeOutgoingSequence(long[] var1);
}
