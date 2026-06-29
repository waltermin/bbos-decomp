package net.rim.device.apps.api.pim;

import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.vm.Array;

public class TimeBasedCollection {
   private SimpleSortingVector _providers;
   private LongIntHashtable _activeProviders = new LongIntHashtable(2);
   private SimpleSortingVector[] _cachedEntries;
   private TimeBasedObjectProviderComparator _providerComparator = new TimeBasedObjectProviderComparator();
   private int[] _cacheIndexPointer;
   long _startRange = Long.MAX_VALUE;
   long _endRange = Long.MIN_VALUE;
   private static long AP_REG_KEY = 7242869349082579147L;
   private static TimeBasedCollection _timeBasedCollection;

   TimeBasedCollection() {
      this._providers = new SimpleSortingVector();
      this._cacheIndexPointer = new int[0];
      this._cachedEntries = new SimpleSortingVector[0];
   }

   public static void register() {
      TimeBasedCollection tbc = new TimeBasedCollection();
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         reg.put(AP_REG_KEY, tbc);
      }
   }

   public static TimeBasedCollection getInstance() {
      if (_timeBasedCollection == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            while (_timeBasedCollection == null) {
               _timeBasedCollection = (TimeBasedCollection)reg.getOrWaitFor(AP_REG_KEY);
            }
         }
      }

      return _timeBasedCollection;
   }

   public synchronized void registerProvider(TimeBasedObjectProvider timeBasedProvider, boolean primary) {
      if (primary) {
         this._providers.insertElementAt(timeBasedProvider, 0);
         this._activeProviders.put(timeBasedProvider.getProviderID(), 1);
      } else {
         this._providers.addElement(timeBasedProvider);
      }

      int size = this._cachedEntries.length;
      Array.resize(this._cachedEntries, size + 1);
      Array.resize(this._cacheIndexPointer, size + 1);
      this._cachedEntries[size] = new SimpleSortingVector();
   }

   public synchronized void unregisterProvider(long providerID) {
      TimeBasedObjectProvider provider = this.getTimeBasedObjectProvider(providerID);
      int providerIndex = this._providers.indexOf(provider);
      if (this._providers.removeElement(provider)) {
         this._activeProviders.remove(providerID);
         Arrays.removeAt(this._cachedEntries, providerIndex);
      }
   }

   public synchronized void activateProvider(long providerID, boolean activate) {
      if (activate) {
         this._activeProviders.put(providerID, 1);
      } else {
         this._activeProviders.remove(providerID);
      }
   }

   private synchronized boolean IsProviderActivated(long providerID) {
      return this._activeProviders.containsKey(providerID);
   }

   public synchronized TimeBasedObjectProvider getTimeBasedObjectProvider(long providerID) {
      for (int i = 0; i < this._providers.size(); i++) {
         TimeBasedObjectProvider tbp = (TimeBasedObjectProvider)this._providers.elementAt(i);
         if (tbp.getProviderID() == providerID) {
            return tbp;
         }
      }

      return null;
   }

   public synchronized void getElementsVisibleDuring(long start, long duration, TimeZone tz, Vector timeBasedObjectVector) {
      int totalElements = 0;

      for (int i = 0; i < this._cachedEntries.length; i++) {
         this._cacheIndexPointer[i] = 0;
         this._cachedEntries[i].setSize(0);
         TimeBasedObjectProvider timeBasedProvider = (TimeBasedObjectProvider)this._providers.elementAt(i);
         if (this.IsProviderActivated(timeBasedProvider.getProviderID())) {
            timeBasedProvider.getElementsVisibleDuring(start, duration, tz, this._cachedEntries[i]);
            totalElements += this._cachedEntries[i].size();
         }
      }

      this.mergeResults(totalElements, timeBasedObjectVector, tz);
      this.resetCachedEntries();
   }

   public synchronized long[] getElementsStartingAround(long time, int maxBefore, int maxOnOrAfter, TimeZone tz, Vector timeBasedObjectVector) {
      long[] result = new long[5];
      this._startRange = Long.MAX_VALUE;
      this._endRange = Long.MIN_VALUE;
      int totalElements = 0;
      int totalBeforeCount = 0;
      int totalOnOrAfterCount = 0;
      int totalOutsideRange = 0;
      this._providers.setSortComparator(this._providerComparator);
      this._providers.reSort();
      this._providers.setSortComparator(null);

      for (int i = 0; i < this._cachedEntries.length; i++) {
         this._cacheIndexPointer[i] = 0;
         this._cachedEntries[i].setSize(0);
         TimeBasedObjectProvider timeBasedProvider = (TimeBasedObjectProvider)this._providers.elementAt(i);
         if (this.IsProviderActivated(timeBasedProvider.getProviderID())) {
            if (this._startRange != Long.MAX_VALUE && this._endRange != Long.MIN_VALUE) {
               timeBasedProvider.getElementsVisibleDuring(this._startRange, this._endRange - this._startRange, tz, this._cachedEntries[i], true);
               int size = this._cachedEntries[i].size();

               int onOrAfterIndex;
               for (onOrAfterIndex = 0; onOrAfterIndex < size; onOrAfterIndex++) {
                  Object o = this._cachedEntries[i].elementAt(onOrAfterIndex);
                  if (o instanceof TimeBasedObject) {
                     TimeBasedObject tbo = (TimeBasedObject)o;
                     long date = tbo.getStart(tz);
                     if (date >= time) {
                        break;
                     }
                  }
               }

               totalBeforeCount += onOrAfterIndex;
               totalOnOrAfterCount += size - onOrAfterIndex;
               int beforeCount = timeBasedProvider.getEventCountBeforeTime(this._startRange, tz);
               int afterCount = timeBasedProvider.getEventCountAfterTime(this._endRange, tz);
               totalOutsideRange += beforeCount + afterCount;
            } else {
               long[] returnVal = null;
               if (!(timeBasedProvider instanceof CalDB)) {
                  returnVal = timeBasedProvider.getElementsStartingAround(time, maxBefore, maxOnOrAfter, tz, this._cachedEntries[i]);
               } else {
                  returnVal = ((CalDB)timeBasedProvider).getElementsStartingAround(time, maxBefore, maxOnOrAfter, tz, this._cachedEntries[i]);
               }

               totalBeforeCount = (int)(totalBeforeCount + returnVal[0]);
               totalOnOrAfterCount = (int)(totalOnOrAfterCount + returnVal[1]);
               totalOutsideRange = (int)(totalOutsideRange + returnVal[2]);
               if (totalBeforeCount >= maxBefore && totalOnOrAfterCount >= maxOnOrAfter) {
                  this.updateListTimeRange(tz);
               }
            }
         }

         totalElements += this._cachedEntries[i].size();
      }

      this.mergeResults(totalElements, timeBasedObjectVector, tz);
      result[0] = totalBeforeCount;
      result[1] = totalOnOrAfterCount;
      result[2] = totalOutsideRange;
      if (this._startRange == Long.MAX_VALUE || this._endRange == Long.MIN_VALUE) {
         this.updateListTimeRange(tz);
         if (this._startRange == Long.MAX_VALUE) {
            this._startRange = time;
         }

         if (this._endRange == Long.MIN_VALUE) {
            this._endRange = time;
         }
      }

      result[3] = this._startRange;
      result[4] = this._endRange;
      this.resetCachedEntries();
      return result;
   }

   private void updateListTimeRange(TimeZone tz) {
      for (int index = 0; index < this._cachedEntries.length; index++) {
         Vector v = this._cachedEntries[index];
         if (v.size() > 0) {
            Object o = this._cachedEntries[index].elementAt(0);
            if (o instanceof TimeBasedObject) {
               TimeBasedObject tbo = (TimeBasedObject)o;
               long date = tbo.getStart(tz);
               if (date < this._startRange) {
                  this._startRange = date;
               }
            }

            o = this._cachedEntries[index].lastElement();
            if (o instanceof TimeBasedObject) {
               TimeBasedObject tbo = (TimeBasedObject)o;
               long date = tbo.getStart(tz);
               if (date > this._endRange) {
                  this._endRange = date;
               }
            }
         }
      }
   }

   private void mergeResults(int totalElements, Vector timeBasedObjectVector, TimeZone tz) {
      timeBasedObjectVector.setSize(totalElements);
      int index = 0;

      for (int i = 0; i < totalElements; i++) {
         long earliestDate = Long.MAX_VALUE;
         int earliestDateIndex = -1;
         earliestDateIndex = -1;

         for (int j = 0; j < this._cachedEntries.length; j++) {
            if (this._cacheIndexPointer[j] < this._cachedEntries[j].size()) {
               Object o = this._cachedEntries[j].elementAt(this._cacheIndexPointer[j]);
               if (!(o instanceof TimeBasedObject)) {
                  this._cacheIndexPointer[j]++;
                  timeBasedObjectVector.setSize(timeBasedObjectVector.size() - 1);
               } else {
                  TimeBasedObject tbo = (TimeBasedObject)o;
                  long date = tbo.getStart(tz);
                  if (date < earliestDate) {
                     earliestDateIndex = j;
                     earliestDate = date;
                  }
               }
            }
         }

         if (earliestDateIndex != -1) {
            timeBasedObjectVector.setElementAt(this._cachedEntries[earliestDateIndex].elementAt(this._cacheIndexPointer[earliestDateIndex]), index);
            this._cacheIndexPointer[earliestDateIndex]++;
            index++;
         }
      }
   }

   private void resetCachedEntries() {
      int size = this._cachedEntries.length;

      for (int i = 0; i < size; i++) {
         Vector v = this._cachedEntries[i];
         v.setSize(0);
      }
   }
}
