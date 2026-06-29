package net.rim.plazmic.internal.mediaengine.model.intarray.v0_0;

import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.model.intarray.v0_0.util.Tasks;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;
import net.rim.plazmic.internal.mediaengine.util.Platform;
import net.rim.plazmic.mediaengine.MediaListener;

public class AnimationModel implements MediaModel, MediaListener {
   private MediaListener listener;
   int bkgColor;
   protected long mediaTime;
   protected boolean started = false;
   private int _state = 0;
   private boolean inFocus = true;
   Tasks tasks = new Tasks();
   int timerCount;
   int numSounds;
   int numImages;
   int numText;
   int numMedia;
   String[] externalResources;
   String missingURLs;
   Object[] objects;
   int[] nodes;
   int[] channels;
   int hotspotFocus;
   int[] hotspotList;
   int numHotspots;
   int[] keyTimes;
   int[] keyValues;
   int sequenceRoot;
   int[][][] coords;
   int width;
   int height;
   int polygonCounter;
   String title;
   String description;
   byte[] rawData;
   String contentType;
   String source;
   ResourceProvider reader;
   int activeSoundNode = -1;
   short[] delta;
   int firstInterp;
   private int indentLevel = 0;
   private boolean isModified;
   int version;
   protected Platform platform;
   int[] taskItem = new int[2];
   private boolean _enableSound = false;
   private int _soundItem = -1;
   private long _currentSoundStartTime;
   private long _currentSoundMediaStartTime;
   EventEngine _engine;
   private static final String TITLE;
   private static final String DESCRITPTION;
   private static final int NOT_STARTED;
   private static final int STARTED;
   private static final int STOPPED;
   private static final int STOPPED_AT_NEW_TIME;
   private static final int RESUMED;
   private static final int STOPPING;

   public void init() {
      this.started = false;
      this.channels = null;
      this.nodes = null;
      this.keyTimes = null;
      this.keyValues = null;
      this.coords = (int[][][])((int[][])null);
      this.hotspotList = null;
      this.firstInterp = -1;
      this.hotspotFocus = -1;
      this.activeSoundNode = -1;
      this.timerCount = 0;
      this.tasks.init();
      this.indentLevel = this.indentLevel;
      this.missingURLs = "";
   }

   void onStop() {
      this._state = 5;
   }

   public final void setIsModified(boolean modified) {
      this.isModified = modified;
   }

   public final boolean isModified() {
      return this.isModified;
   }

   public void setMediaListener(MediaListener listener) {
      this.listener = listener;
   }

   protected final void fireMediaEvent(int event, int eventParam, Object data) {
      if (this.listener != null) {
         this.listener.mediaEvent(this, event, eventParam, data);
      }
   }

   void onNewTime() {
      this._state = 3;
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   void onStart() {
      if (this._state != 2 && this._state != 5) {
         this._state = 1;
      } else {
         this._state = 4;
      }
   }

   public final int getBkgColor() {
      return this.bkgColor;
   }

   public int getUnconsumed(int n) {
      int count = n < 0 ? -n : n;
      if (n > 0) {
         int start = this.hotspotFocus < 0 ? 0 : this.hotspotFocus;

         for (int i = start; i < this.numHotspots - 1; i++) {
            if (this.bitsAreSet(this.hotspotList[i + 1], 129)) {
               count--;
            }

            if (count == 0) {
               return 0;
            }
         }
      } else {
         int start = this.hotspotFocus < 0 ? this.numHotspots - 1 : this.hotspotFocus;

         for (int i = start; i > 0; i--) {
            if (this.bitsAreSet(this.hotspotList[i - 1], 129)) {
               count--;
            }

            if (count == 0) {
               return 0;
            }
         }
      }

      return count;
   }

   public int getItemCount() {
      return this.numHotspots;
   }

   protected void stopSound(int index) {
      if (index != -1) {
         int soundIndex = this.nodes[index + 3];
         this.platform.stopPlayer(this.objects[soundIndex], this);
         if (this.bitsAreSet(index, 2)) {
            this.onFinish(this.nodes[index + 2], index, this.mediaTime);
         }

         if (index == this.activeSoundNode) {
            this.activeSoundNode = -1;
         }
      }

      this._currentSoundStartTime = 0;
      this._currentSoundMediaStartTime = 0;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean sample() {
      long mediaTime = this._engine.getTime();
      boolean updateUI = false;
      synchronized (this) {
         boolean isRevertModel = this.mediaTime > mediaTime || mediaTime == 0 && this._state == 3;
         this.onSample_VerifyState(isRevertModel);
         if (isRevertModel) {
            label384:
            try {
               this.revertModel();
            } catch (Throwable var46) {
               MediaFactory.getPlatform().logDebug(this, 22, -1, e);
               break label384;
            }
         }

         this.mediaTime = mediaTime;
         if (!this.started) {
            this.started = true;
            this.startSequenceNode(this.sequenceRoot, 0);
         }

         if (this.inFocus) {
            this.validateFocus();
         }

         boolean consumed = false;
         this.tasks.captureQueueSnapshot();
         this.timerCount = 0;

         while (this.tasks.heapHasElements() && this.tasks.peekInHeap() <= mediaTime) {
            this.tasks.removeFromHeap();
         }

         while (this.tasks.queueHasMoreElements()) {
            this.tasks.dequeue(this.taskItem);
            if (this.taskItem[0] == 1) {
               this.doChannelModifier(this.taskItem[1]);
               this.validateFocus();
            } else if (this.taskItem[0] == 6) {
               this._enableSound = true;
               this._soundItem = this.taskItem[1];
            } else if (this.taskItem[0] != 3) {
               if (this.taskItem[0] == 4) {
                  synchronized (this.tasks) {
                     this.tasks.setEventActive(true);
                     this.activateItemInFocus();
                     this.tasks.mergeEventList();
                     this.tasks.setEventActive(false);
                  }

                  if (this.inFocus) {
                     this.validateFocus();
                  }
               } else if (this.taskItem[0] == 5) {
                  synchronized (this.tasks) {
                     this.tasks.setEventActive(true);
                     this.setFocus(this.taskItem[1]);
                     this.tasks.mergeEventList();
                     this.tasks.setEventActive(false);
                  }

                  if (this.inFocus) {
                     this.validateFocus();
                  }
               } else {
                  int index = this.taskItem[1];
                  long currentTime = this.mediaTime;
                  int type = this.nodes[index + 4];
                  int keyTimesIndex = this.nodes[index + 5];
                  int keyValuesIndex = this.nodes[index + 6];
                  int numKeys = this.keyTimes[keyTimesIndex];
                  int lastKeyTime = this.keyTimes[keyTimesIndex + numKeys];
                  int loopCount = this.nodes[index + 3];
                  boolean visible = this.bitsAreSet(index, 128);
                  int interpST = this.nodes[index + 7];
                  boolean isFinished = loopCount > 0 && currentTime >= lastKeyTime * loopCount + interpST;
                  boolean isValidTarget = visible && this.nodes[index + 9] > 0;
                  if (isFinished) {
                     if (isValidTarget) {
                        this.setInterpolatorTargetsValue(index, this.keyValues[keyValuesIndex + numKeys]);
                     }

                     this.unsetBits(index, 1);
                     this.unsetBits(index, 16);
                     int parent = this.nodes[index + 2];
                     if (parent > 0) {
                        synchronized (this.tasks) {
                           this.tasks.setEventActive(true);
                           this.onFinish(parent, index, lastKeyTime * loopCount + interpST);
                           this.tasks.mergeEventList();
                           this.tasks.setEventActive(false);
                        }
                     }
                  } else {
                     this.tasks.enqueue(2, index);
                     if ((this.nodes[index + 1] & 8) != 0) {
                        this.timerCount++;
                     }

                     if (!isValidTarget) {
                        if (loopCount == 0) {
                           int looped = (int)(mediaTime - interpST) / lastKeyTime;
                           this.tasks.heapInsert(looped * lastKeyTime + interpST);
                        }
                     } else {
                        int timeWithinLoop = (int)(currentTime - interpST) % lastKeyTime;
                        int interval = this.nodes[index + 8];
                        if (timeWithinLoop < this.keyTimes[keyTimesIndex + interval]) {
                           this.nodes[index + 8] = 1;
                           interval = 1;
                        }

                        while (timeWithinLoop >= this.keyTimes[keyTimesIndex + interval + 1]) {
                           this.nodes[index + 8] = ++interval;
                        }

                        boolean shouldBeTimer = false;
                        boolean meIsTimer = false;
                        if (interval + 1 != numKeys && this.keyValues[keyValuesIndex + interval] == this.keyValues[keyValuesIndex + interval + 1]) {
                           shouldBeTimer = true;
                        }

                        if ((this.nodes[index + 1] & 8) != 0) {
                           meIsTimer = true;
                        }

                        if (!meIsTimer && shouldBeTimer) {
                           int looped = (int)(mediaTime - interpST) / lastKeyTime;
                           int future = looped * lastKeyTime + this.keyTimes[keyTimesIndex + interval + 1] + interpST;
                           this.tasks.heapInsert(future);
                           this.nodes[index + 1] = this.nodes[index + 1] | 8;
                           this.timerCount++;
                        }

                        if (meIsTimer && !shouldBeTimer) {
                           this.timerCount--;
                           this.nodes[index + 1] = this.nodes[index + 1] & -9;
                        }

                        int result = 0;
                        switch (type) {
                           case 10:
                              result = this.keyValues[keyValuesIndex + interval];
                              break;
                           case 20:
                              int keyTimeIndex = keyTimesIndex + interval;
                              int keyValIndex = keyValuesIndex + interval;
                              int val = this.keyValues[keyValIndex];
                              int tim = this.keyTimes[keyTimeIndex];
                              result = val + (this.keyValues[keyValIndex + 1] - val) * (timeWithinLoop - tim) / (this.keyTimes[keyTimeIndex + 1] - tim);
                        }

                        this.setInterpolatorTargetsValue(index, result);
                     }
                  }
               }
            } else {
               boolean wrap = (this.taskItem[1] & 1) == 1;
               int offset = this.taskItem[1] >> 1;
               synchronized (this.tasks) {
                  this.tasks.setEventActive(true);
                  if (offset == 0) {
                     this.moveFocus(0, wrap);
                  } else {
                     consumed = this.moveFocus(offset > 0 ? 1 : -1, wrap) == 0;
                  }

                  if (consumed && Math.abs(offset) > 1) {
                     this.tasks.enqueue(3, (offset > 0 ? offset - 1 : offset + 1) << 1 | this.taskItem[1] & 1);
                  }

                  this.tasks.mergeEventList();
                  this.tasks.setEventActive(false);
               }

               if (this.inFocus) {
                  this.validateFocus();
               }
            }
         }

         updateUI = this.isModified();
         this.setIsModified(false);
         if (!updateUI && this._enableSound) {
            this.startRenderSound();
         }
      }

      if (updateUI) {
         this.fireMediaEvent(20, -1, this);
      }

      if (this.tasks.queueSize() <= 0) {
         this.fireMediaEvent(3, -1, this);
      }

      return updateUI;
   }

   public final int[] getNodes() {
      return this.nodes;
   }

   public byte[] getRawData() {
      return this.rawData;
   }

   public void startRenderSound() {
      if (this._enableSound && this._engine.isRunning()) {
         if (this._soundItem != -1) {
            if (this.activeSoundNode == this._soundItem) {
               int soundIndex = this.nodes[this.activeSoundNode + 3];
               this._currentSoundStartTime = this.mediaTime;
               this.platform.startPlayer(this.objects[soundIndex], this, this._currentSoundMediaStartTime, 1);
            } else {
               int soundIndex = this.nodes[this._soundItem + 3];
               Object sound = this.objects[soundIndex];
               this.activeSoundNode = -1;
               if (sound != null && this.platform.startPlayer(sound, this, 0, 1)) {
                  this.activeSoundNode = this._soundItem;
                  this._currentSoundStartTime = this.mediaTime;
               }
            }
         }

         this._enableSound = false;
      }
   }

   public void setFocus(int item) {
      this.inFocus = item >= 0;
      if (this.numHotspots <= item) {
         throw new Object();
      }

      if (item < 0 || this.bitsAreSet(this.hotspotList[item], 129)) {
         int oldHF = this.hotspotFocus;
         this.hotspotFocus = item;
         if (oldHF != this.hotspotFocus) {
            synchronized (this) {
               if (oldHF >= 0) {
                  int oldHFOutFocus = this.hotspotList[oldHF] + 3;
                  this.startSequenceNode(this.nodes[oldHFOutFocus], this.mediaTime);
                  this.fireMediaEvent(104, (this.hotspotFocus & 65535) << 16 | oldHF & 65535, null);
               }

               if (this.hotspotFocus >= 0) {
                  int hsInfocus = this.hotspotList[this.hotspotFocus] + 4;
                  this.startSequenceNode(this.nodes[hsInfocus], this.mediaTime);
                  this.fireMediaEvent(103, (this.hotspotFocus & 65535) << 16 | oldHF & 65535, null);
               }
            }
         }
      }
   }

   public int getItemInFocus() {
      return this.hotspotFocus;
   }

   public final Object[] getObjects() {
      return this.objects;
   }

   public int[][][] getPolygonCoords() {
      return this.coords;
   }

   protected final void setBits(int node, int bits) {
      this.nodes[node + 1] = this.nodes[node + 1] | bits;
   }

   protected final void unsetBits(int node, int bits) {
      this.nodes[node + 1] = this.nodes[node + 1] & ~bits;
   }

   public final boolean bitsAreSet(int node, int bits) {
      return (this.nodes[node + 1] & bits) == bits;
   }

   protected final boolean startSequenceNode(int index, long startTime) {
      int type = this.nodes[index];
      boolean result = false;
      switch (type) {
         case 85:
            this.setBits(index, 1);
            this.nodes[index + 7] = (int)startTime;
            this.nodes[index + 8] = 1;
            if (this.nodes[index + 9] == 0) {
               this.nodes[index + 1] = this.nodes[index + 1] | 8;
               int loopCount = this.nodes[index + 3];
               if (loopCount > 0) {
                  int keyTimesIndex = this.nodes[index + 5];
                  int numKeys = this.keyTimes[keyTimesIndex];
                  this.tasks.heapInsert(this.keyTimes[keyTimesIndex + numKeys] * loopCount + this.nodes[index + 7]);
               }
            }

            if (this.bitsAreSet(index, 16)) {
               int res = this.tasks.removeAllInstancesOf(2, index);
               if ((this.nodes[index + 1] & 8) != 0) {
                  this.timerCount -= res;
               }
            }

            this.tasks.enqueue(2, index);
            this.setBits(index, 16);
            break;
         case 90:
            this.tasks.enqueue(1, index);
            result = true;
            break;
         case 95:
            int hyperlinkURLIndex = this.nodes[index + 3];
            this.fireMediaEvent(7, -1, this.objects[hyperlinkURLIndex]);
            result = true;
            break;
         case 100:
            this.stopSound(this.activeSoundNode);
            result = true;
            break;
         case 105:
            this.stopSound(this.activeSoundNode);
            this.tasks.enqueue(6, index);
            result = true;
            break;
         case 110:
            this.setBits(index, 1);
            break;
         case 115:
            int var12 = this.nodes[index + 3];
            result = false;
            if (var12 <= 0) {
               result = true;
            }

            var12 = Math.abs(var12);

            for (int i = 0; i < var12; i++) {
               int childIndex = this.nodes[index + 4 + i];
               if (this.startSequenceNode(childIndex, startTime)) {
                  result = true;
               }
            }

            this.nodes[index + 3] = -var12;
            if (result) {
               this.nodes[index + 3] = var12;
            }
            break;
         case 120:
            result = true;
            int numChild = this.nodes[index + 3];

            for (int i = 0; i < numChild; i++) {
               int childIndex = this.nodes[index + 4 + i];
               childIndex = Math.abs(childIndex);
               if (!this.startSequenceNode(childIndex, startTime)) {
                  this.nodes[index + 4 + i] = -childIndex;
                  result = false;
               }
            }
            break;
         case 125:
            this.nodes[index + 5] = -1;
            this.nodes[index + 6] = 0;
            result = this.startNextLoopChild(index, startTime);
      }

      return result;
   }

   public boolean activateItemInFocus() {
      if (this.hotspotFocus >= 0) {
         synchronized (this) {
            this.activateHotspot(this.hotspotFocus);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean hasFocus() {
      return this.hotspotFocus >= 0 && this.bitsAreSet(this.hotspotList[this.hotspotFocus], 129);
   }

   public int moveFocus(int n, boolean wrap) {
      this.inFocus = true;
      int iterator = n;
      if (n == 0 && !this.hasFocus()) {
         n = 1;
      }

      int direction = n > 0 ? 1 : -1;
      if (n != 0 && this.numHotspots != 0) {
         int newHotspot = -1;
         int prevHotspot = this.hotspotFocus;

         for (iterator = n > 0 ? n : -n; iterator > 0; iterator--) {
            newHotspot = this.getNextFocusableItem(direction, prevHotspot, wrap);
            if (newHotspot == prevHotspot) {
               break;
            }

            prevHotspot = newHotspot;
         }

         this.setFocus(newHotspot);
      }

      return n > 0 ? iterator : -iterator;
   }

   public int getNextFocusableItem(int direction, int curItem, boolean wrap) {
      boolean next = direction == 1;
      if (this.numHotspots != 0 && curItem < this.numHotspots) {
         int curr;
         int i;
         if (curItem < 0) {
            if (!next) {
               i = this.numHotspots;
               curr = 0;
            } else {
               i = -1;
               curr = this.numHotspots - 1;
            }
         } else {
            i = curItem;
            curr = curItem;
         }

         do {
            i += next ? 1 : -1;
            if (wrap) {
               i = (i + this.numHotspots) % this.numHotspots;
            } else if (i < 0 || i >= this.numHotspots) {
               break;
            }

            if (this.bitsAreSet(this.hotspotList[i], 129)) {
               return i;
            }
         } while (i != curr);

         return curItem >= 0 && this.bitsAreSet(this.hotspotList[curItem], 129) ? curItem : -1;
      } else {
         return -1;
      }
   }

   @Override
   public String getContentType() {
      return this.contentType;
   }

   @Override
   public void setSource(String s) {
      this.source = s;
   }

   @Override
   public String getSource() {
      return this.source;
   }

   @Override
   public String getMetaValue(int index) {
      if (index == 0) {
         return this.title;
      } else {
         return index == 1 ? this.description : "";
      }
   }

   @Override
   public int getExternalResourcesCount(int category) {
      int count = 0;
      if ((category & 4) != 0) {
         count += this.numSounds;
      }

      if ((category & 2) != 0) {
         count += this.numImages;
      }

      if ((category & 1) != 0) {
         count += this.numMedia;
      }

      return count;
   }

   @Override
   public String[] getExternalResources(int category) {
      int size = this.getExternalResourcesCount(category);
      String[] result = new Object[size];
      if (size > 0) {
         int offset = 0;
         if ((category & 4) != 0) {
            System.arraycopy(this.externalResources, 0, result, offset, this.numSounds);
            offset += this.numSounds;
         }

         if ((category & 2) != 0) {
            System.arraycopy(this.externalResources, this.numSounds, result, offset, this.numImages);
            offset += this.numImages;
         }

         if ((category & 1) != 0) {
            System.arraycopy(this.objects, this.numSounds + this.numImages + this.numText, result, offset, this.numMedia);
            offset += this.numMedia;
         }
      }

      return result;
   }

   @Override
   public String getMissingExtURLs() {
      return this.missingURLs;
   }

   @Override
   public String getMetaKey(int index) {
      if (index == 0) {
         return "Title";
      } else {
         return index == 1 ? "Description" : "";
      }
   }

   @Override
   public void mediaEvent(Object src, int event, int eventParam, Object param) {
      if (event == 3) {
         if (src == this._engine) {
            if (eventParam == this.activeSoundNode) {
               synchronized (this) {
                  if (this.activeSoundNode != -1 && this.bitsAreSet(this.activeSoundNode, 4)) {
                     int soundIndex = this.nodes[this.activeSoundNode + 3];
                     this._currentSoundMediaStartTime = 0;
                     this.platform.startPlayer(this.objects[soundIndex], null, 0, 1);
                  } else {
                     this.stopSound(this.activeSoundNode);
                  }

                  return;
               }
            }
         } else {
            Event ev = this._engine.getEventInstance();
            ev._event = 3;
            ev._eventParam = this.activeSoundNode;
            ev._listener = this;
            this._engine.postEvent(ev, true);
            this._engine.releaseEventInstance(ev);
         }
      }
   }

   @Override
   public String getMetaInfo(String key) {
      if (MediaFactory.getPlatform().strEqualIgnoreCase(key, "title")) {
         return this.title;
      } else {
         return MediaFactory.getPlatform().strEqualIgnoreCase(key, "description") ? this.description : "";
      }
   }

   @Override
   public int getNumMetaInfo() {
      return 2;
   }

   @Override
   public Object getResource(String resourceId) {
      Object result = null;
      int count = 0;

      while (count < this.externalResources.length && !resourceId.equals(this.externalResources[count])) {
         count++;
      }

      if (count < this.externalResources.length) {
         result = this.objects[count];
      }

      return result;
   }

   @Override
   public Class getMediaClass() {
      return this.getClass();
   }

   @Override
   public void disposeModel() {
      for (int i = this.numSounds + this.numImages - 1; i >= 0; i--) {
         this.platform.disposeMedia(this.objects[i]);
         this.objects[i] = null;
      }
   }

   @Override
   public int getVersionNumber() {
      return this.version;
   }

   @Override
   public final ForeignObject[] getForeignObjects() {
      return null;
   }

   private void doChannelModifier(int index) {
      int idx = this.nodes[index + 3];
      int channelSize = this.channels[idx++];
      int operation = this.nodes[index + 4];
      boolean moded = false;

      for (int i = idx; i < idx + channelSize; i++) {
         int nodeidx = this.channels[i];
         if (operation == 10) {
            if (this.bitsAreSet(nodeidx, 128)) {
               this.nodes[nodeidx + 1] = this.nodes[nodeidx + 1] | 1;
               moded = true;
            }

            this.unsetBits(nodeidx, 128);
         } else if (operation == 20) {
            if (!this.bitsAreSet(nodeidx, 128)) {
               this.nodes[nodeidx + 1] = this.nodes[nodeidx + 1] | 1;
               moded = true;
            }

            this.setBits(nodeidx, 128);
         } else if (this.bitsAreSet(nodeidx, 128)) {
            this.nodes[nodeidx + 1] = this.nodes[nodeidx + 1] | 1;
            moded = true;
            this.unsetBits(nodeidx, 128);
         } else {
            this.nodes[nodeidx + 1] = this.nodes[nodeidx + 1] | 1;
            moded = true;
            this.setBits(nodeidx, 128);
         }
      }

      if (moded) {
         this.setIsModified(true);
      }
   }

   private void revertModel() {
      if (this.reader != null) {
         this.reader.createResource(this.contentType, this, null, null);
         this.setIsModified(true);
      }
   }

   private void setInterpolatorTargetsValue(int interpIdx, int value) {
      boolean modified = false;
      int numTargets = this.nodes[interpIdx + 9];

      for (int j = 0; j < numTargets; j++) {
         int variable = this.nodes[interpIdx + 10 + j];
         int change = value - this.nodes[variable & 65535];
         int visualNode = variable >> 16;
         if (change != 0) {
            modified = true;
            this.nodes[visualNode + 1] = this.nodes[visualNode + 1] | 1;
            this.nodes[variable & 65535] = value;
         }
      }

      if (modified) {
         this.setIsModified(true);
      }
   }

   private void onFinish(int parent, int child, long finishTime) {
      if ((this.nodes[child + 1] & 8) != 0) {
         this.nodes[child + 1] = this.nodes[child + 1] & -9;
      }

      int type = this.nodes[parent];
      boolean thisFinished = false;
      switch (type) {
         case 110:
            if (child == this.nodes[parent + 5]) {
               thisFinished = true;
            }
            break;
         case 115:
            int var13 = this.nodes[parent + 3];
            thisFinished = var13 < 0;
            this.nodes[parent + 3] = Math.abs(var13);
            break;
         case 120:
            boolean iAmDone = true;
            int index = parent + 4;
            int numChildren = this.nodes[parent + 3];

            for (int i = 0; i < numChildren; i++) {
               int childIndex = Math.abs(this.nodes[index + i]);
               if (child == childIndex) {
                  this.nodes[index + i] = childIndex;
               }

               iAmDone = iAmDone && this.nodes[index + i] >= 0;
               if (!iAmDone) {
                  break;
               }
            }

            thisFinished = iAmDone;
            break;
         case 125:
            thisFinished = this.startNextLoopChild(parent, finishTime);
      }

      if (thisFinished) {
         int myParentIndex = this.nodes[parent + 2];
         if (myParentIndex >= 0) {
            this.onFinish(myParentIndex, parent, finishTime);
         }
      }
   }

   private void validateFocus() {
      if (this.inFocus) {
         if (!this.hasFocus()) {
            this.moveFocus(1, true);
            return;
         }
      } else if (this.hasFocus()) {
         this.setFocus(-1);
      }
   }

   private final boolean startNextLoopChild(int index, long startTime) {
      int curChild = this.nodes[index + 5];
      int numChildren = this.nodes[index + 3];

      boolean moreChildren;
      do {
         if (++curChild >= numChildren) {
            curChild = 0;
            this.nodes[index + 6]++;
         }

         moreChildren = this.nodes[index + 4] <= 0 || this.nodes[index + 6] < this.nodes[index + 4];
      } while (moreChildren && this.startSequenceNode(this.nodes[index + 7 + curChild], startTime));

      this.nodes[index + 5] = curChild;
      return !moreChildren;
   }

   private void activateHotspot(int index) {
      if (index >= 0) {
         int hs = this.hotspotList[index];
         int onactivate = this.nodes[hs + 5];
         if (this.nodes[onactivate] < 0 || this.startSequenceNode(onactivate, this.mediaTime)) {
            this.onFinish(hs, onactivate, this.mediaTime);
         }

         this.fireMediaEvent(105, index, null);
      }
   }

   private void onSample_VerifyState(boolean isRevertModel) {
      if (this.activeSoundNode != -1) {
         if (isRevertModel) {
            int soundIndex = this.nodes[this.activeSoundNode + 3];
            this.platform.stopPlayer(this.objects[soundIndex], this);
         } else if (!this._engine.isRunning()) {
            if (this._state == 5 || this._state == 3) {
               int soundIndex = this.nodes[this.activeSoundNode + 3];
               this._currentSoundMediaStartTime = this._engine.getTime() - this._currentSoundStartTime + this._currentSoundMediaStartTime;
               this.platform.pausePlayer(this.objects[soundIndex], this);
            }
         } else if (this._state == 4) {
            this._soundItem = this.activeSoundNode;
            this._enableSound = true;
         }
      }

      this.advanceState();
   }

   public AnimationModel() {
      this.platform = MediaFactory.getPlatform();
      this.init();
   }

   private void advanceState() {
      switch (this._state) {
         case 3:
         case 5:
         default:
            this._state = 2;
            return;
         case 4:
            this._state = 1;
         case 2:
      }
   }
}
