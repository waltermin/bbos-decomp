package net.rim.plazmic.internal.mediaengine.model.intarray.v0_0;

import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;

public class PME0_2Reader implements ResourceProvider {
   protected AnimationModel model;
   private ResourceContext _context;
   protected int objIndexMask;
   protected int coordIndexMask;
   protected int varDataMask;
   protected int loopCountMask;
   protected int scenerectwhMask;
   protected int keyTimeIndexMask;
   protected int keyValIndexMask;
   protected int channelIndexMask;
   protected int keyTimeMask;
   protected int keyValMask;
   protected int coordValMask;
   protected int curChildMask;
   protected int vNodeXCoordMask;
   protected int vNodeYCoordMask;
   protected int visibleRefIndex;
   protected int realRefIndex;
   protected int[] visibleRefList;
   protected int[] realRefList;
   protected int realObj = 0;
   protected int interpolatorsCount = 0;
   protected int interpolatorIndex = 0;
   private int numPolys;
   private boolean foundInterp;
   private int maxTextNodeIndex = -1;
   private int checksum;
   private int offset;
   private byte[] data;
   private String encoding;

   protected final String readUTF() {
      if (this.data.length - this.offset < 2) {
         throw new Object(4);
      }

      int ch1 = this.data[this.offset++];
      int ch2 = this.data[this.offset++];
      int utflen = (ch1 << 8) + (ch2 << 0);
      if (this.data.length - this.offset < utflen) {
         throw new Object(4);
      }

      int dataOffset = this.offset;
      this.offset += utflen;
      StringBuffer str = (StringBuffer)(new Object(utflen));
      int count = 0;

      while (count < utflen) {
         int c = this.data[dataOffset + count] & 255;
         switch (c >> 4) {
            case -1:
            case 8:
            case 9:
            case 10:
            case 11:
               throw new Object(4);
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
               count++;
               str.append((char)c);
               break;
            case 12:
            case 13:
               count += 2;
               if (count > utflen) {
                  throw new Object(4);
               }

               int char2 = this.data[dataOffset + count - 1];
               if ((char2 & 192) != 128) {
                  throw new Object(4);
               }

               str.append((char)((c & 31) << 6 | char2 & 63));
               break;
            case 14:
               count += 3;
               if (count > utflen) {
                  throw new Object(4);
               }

               int char2 = this.data[dataOffset + count - 2];
               int char3 = this.data[dataOffset + count - 1];
               if ((char2 & 192) != 128 || (char3 & 192) != 128) {
                  throw new Object(4);
               }

               str.append((char)((c & 15) << 12 | (char2 & 63) << 6 | (char3 & 63) << 0));
         }
      }

      return (String)(new Object(str));
   }

   protected final int readInt() {
      if (this.data.length - this.offset < 4) {
         throw new Object();
      }

      int ch1 = this.data[this.offset++];
      int ch2 = this.data[this.offset++];
      int ch3 = this.data[this.offset++];
      int ch4 = this.data[this.offset++];
      return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
   }

   protected void readNodeData(int nodeIdx) {
      int type = this.readData(4);
      int bits = 128;
      if (type < 0) {
         type = ~type;
         bits = 0;
      }

      this.model.nodes[nodeIdx] = type;
      this.model.nodes[nodeIdx + 1] = 0;
      switch (type) {
         case 10:
         case 20:
         case 60:
         case 105:
            bits = this.readData(0);
         default:
            this.model.setBits(nodeIdx, bits);
      }
   }

   protected void readNodeChildren(int nodeIdx, int varDataOffset, int numChildren) {
      for (int i = 0; i < numChildren; i++) {
         int childIdx = this.readData(1);
         this.model.nodes[nodeIdx + varDataOffset + i] = childIdx;
      }
   }

   protected void readVisualNodeData(int nodeIdx) {
      this.addVisibleElement(nodeIdx);
      this.readNodeData(nodeIdx);
      this.model.nodes[nodeIdx + 2] = this.readData(this.vNodeXCoordMask);
      this.model.nodes[nodeIdx + 3] = this.readData(this.vNodeYCoordMask);
      int type = this.model.nodes[nodeIdx];
      switch (type) {
         case 10:
            this.addRealElement(nodeIdx);
            this.readShapeNodeData(nodeIdx);
            this.model.nodes[nodeIdx + 10] = this.readData(this.scenerectwhMask);
            this.model.nodes[nodeIdx + 11] = this.readData(this.scenerectwhMask);
            return;
         case 20:
            this.numPolys++;
            this.addRealElement(nodeIdx);
            this.readShapeNodeData(nodeIdx);
            this.model.nodes[nodeIdx + 10] = this.readData(this.coordIndexMask);
            this.model.nodes[nodeIdx + 11] = this.readData(this.coordIndexMask);
            this.model.polygonCounter++;
            return;
         case 30:
            this.addRealElement(nodeIdx);
            this.model.setBits(nodeIdx, 32);
            this.readShapeNodeData(nodeIdx);
            this.model.nodes[nodeIdx + 7] = this.readData(3);
            int textIndex = nodeIdx + 8;
            this.model.nodes[textIndex] = this.readData(this.objIndexMask);
            if (this.maxTextNodeIndex < this.model.nodes[textIndex]) {
               this.maxTextNodeIndex = this.model.nodes[textIndex];
               this.model.numText++;
               return;
            }
            break;
         case 40:
            this.addRealElement(nodeIdx);
            this.model.nodes[nodeIdx + 4] = this.readData(this.objIndexMask);
            return;
         case 50:
            this.model.nodes[nodeIdx + 4] = this.readData(this.curChildMask);
            int numChildren = this.readData(this.varDataMask);
            this.model.nodes[nodeIdx + 5] = numChildren;
            this.readNodeChildren(nodeIdx, 6, numChildren);

            for (int i = 0; i < numChildren; i++) {
               this.readVisualNodeData(this.model.nodes[nodeIdx + 6 + i]);
            }
            break;
         case 60:
            this.addRealElement(nodeIdx);
            this.readShapeNodeData(nodeIdx);
            this.model.nodes[nodeIdx + 10] = this.readData(this.scenerectwhMask);
            this.model.nodes[nodeIdx + 11] = this.readData(this.scenerectwhMask);
      }
   }

   protected void readShapeNodeData(int nodeIdx) {
      if (this.model.bitsAreSet(nodeIdx, 32)) {
         this.model.nodes[nodeIdx + 4] = this.readData(0);
         this.model.nodes[nodeIdx + 5] = this.readData(0);
         this.model.nodes[nodeIdx + 6] = this.readData(0);
      }

      if (this.model.bitsAreSet(nodeIdx, 64)) {
         this.model.nodes[nodeIdx + 7] = this.readData(0);
         this.model.nodes[nodeIdx + 8] = this.readData(0);
         this.model.nodes[nodeIdx + 9] = this.readData(0);
      }
   }

   protected void readSequenceNodeData(int nodeIdx, int parentIdx) {
      this.readNodeData(nodeIdx);
      this.model.nodes[nodeIdx + 2] = parentIdx;
      int type = this.model.nodes[nodeIdx];
      switch (type) {
         case 85:
            this.interpolatorIndex++;
            this.model.nodes[nodeIdx + 3] = this.readData(this.loopCountMask);
            this.model.nodes[nodeIdx + 4] = this.readData(4);
            int keyTimeIndex = this.readData(this.keyTimeIndexMask);
            this.model.nodes[nodeIdx + 5] = keyTimeIndex;
            this.model.nodes[nodeIdx + 6] = this.readData(this.keyValIndexMask);
            int numTargets = this.readData(this.varDataMask);
            this.model.nodes[nodeIdx + 9] = numTargets;

            for (int i = 0; i < numTargets; i++) {
               int objInterpIndex = this.readData(1);
               int obj = this.lesserSearch(objInterpIndex);
               this.model.nodes[nodeIdx + 10 + i] = objInterpIndex | obj << 16;
               this.model.nodes[obj + 1] = this.model.nodes[obj + 1] | 2;
            }
            break;
         case 90:
            this.model.nodes[nodeIdx + 3] = this.readData(this.channelIndexMask);
            this.model.nodes[nodeIdx + 4] = this.readData(4);
            return;
         case 95:
            this.model.nodes[nodeIdx + 3] = this.readData(this.objIndexMask);
            return;
         case 105:
            this.model.nodes[nodeIdx + 3] = this.readData(this.objIndexMask);
            return;
         case 110:
            this.model.hotspotList[this.model.numHotspots++] = nodeIdx;
            this.model.nodes[nodeIdx + 3] = this.readData(1);
            this.model.nodes[nodeIdx + 4] = this.readData(1);
            this.model.nodes[nodeIdx + 5] = this.readData(1);
            this.readSequenceNodeData(this.model.nodes[nodeIdx + 3], nodeIdx);
            this.readSequenceNodeData(this.model.nodes[nodeIdx + 4], nodeIdx);
            this.readSequenceNodeData(this.model.nodes[nodeIdx + 5], nodeIdx);
            break;
         case 115:
         case 120:
         case 125:
            this.readSequenceBranchData(nodeIdx);
            return;
      }
   }

   protected void readSequenceBranchData(int nodeIdx) {
      int type = this.model.nodes[nodeIdx];
      int numChildren = this.readData(this.varDataMask);
      this.model.nodes[nodeIdx + 3] = numChildren;
      int varDataOffset = 0;
      switch (type) {
         case 115:
            varDataOffset = 4;
            break;
         case 120:
            varDataOffset = 4;
            break;
         case 125:
            this.model.nodes[nodeIdx + 4] = this.readData(this.loopCountMask);
            varDataOffset = 7;
      }

      this.readNodeChildren(nodeIdx, varDataOffset, numChildren);

      for (int i = 0; i < numChildren; i++) {
         this.readSequenceNodeData(this.model.nodes[nodeIdx + varDataOffset + i], nodeIdx);
      }
   }

   protected int lesserSearch(int searchee) {
      if (this.visibleRefList.length >= 4) {
         int lo = 0;
         int hi = this.visibleRefList.length - 1;
         if (this.visibleRefList[hi] < searchee) {
            return this.visibleRefList[hi];
         }

         do {
            int mid = hi + lo >> 1;
            if (searchee > this.visibleRefList[mid]) {
               lo = mid;
            } else {
               hi = mid;
            }
         } while (hi - lo > 1);

         return this.visibleRefList[lo];
      } else {
         int i = 0;

         while (i < this.visibleRefList.length && searchee > this.visibleRefList[i]) {
            i++;
         }

         if (i != 0) {
            i--;
         }

         return this.visibleRefList[i];
      }
   }

   protected void addVisibleElement(int data) {
      if (this.visibleRefIndex > this.visibleRefList.length - 1) {
         int[] tempArray = new int[this.visibleRefList.length * 2];

         for (int i = this.visibleRefList.length - 1; i >= 0; i--) {
            tempArray[i] = this.visibleRefList[i];
         }

         this.visibleRefList = tempArray;
      }

      this.visibleRefList[this.visibleRefIndex] = data;
      this.visibleRefIndex++;
   }

   protected void addRealElement(int data) {
      if (this.realRefIndex > this.realRefList.length - 1) {
         int[] tempArray = new int[this.realRefList.length * 2];

         for (int i = this.realRefList.length - 1; i >= 0; i--) {
            tempArray[i] = this.realRefList[i];
         }

         this.realRefList = tempArray;
      }

      this.realRefList[this.realRefIndex] = data;
      this.realRefIndex++;
   }

   protected String readString() {
      if (this.data.length - this.offset < 2) {
         throw new Object(4);
      }

      int length = this.readData(1);
      if (this.data.length - this.offset < length) {
         throw new Object(4);
      }

      String result = MediaFactory.getPlatform().createString(this.data, this.offset, length, this.encoding);

      while (length > 0) {
         this.readData(4);
         length--;
      }

      return result;
   }

   protected void readDataArray(int[] array, int index, int length, int mask) {
      int bounds = index + length;

      while (index < bounds) {
         array[index++] = this.readData(mask);
      }
   }

   protected void readStringArray(Object[] array, int index, int length) {
      int bounds = index + length;

      while (index < bounds) {
         array[index++] = this.readString();
      }
   }

   protected int readData(int mask) {
      int result;
      switch (mask) {
         case -1:
         case 5:
         case 6:
            result = (short)((this.data[this.offset++] & 255) << 8) | this.data[this.offset++] & 255;
            break;
         case 0:
         default:
            result = this.data[this.offset++] & 255;
            break;
         case 1:
            result = (this.data[this.offset++] & 255) << 8 | this.data[this.offset++] & 255;
            break;
         case 2:
            result = (this.data[this.offset++] & 255) << 16 | (this.data[this.offset++] & 255) << 8 | this.data[this.offset++] & 255;
            break;
         case 3:
         case 7:
            result = (this.data[this.offset++] & 255) << 24
               | (this.data[this.offset++] & 255) << 16
               | (this.data[this.offset++] & 255) << 8
               | this.data[this.offset++] & 255;
            break;
         case 4:
            result = (byte)(this.data[this.offset++] & 0xFF);
      }

      this.checksum += result;
      return result;
   }

   protected void validateChecksum() {
      int checksum = this.readData(7);
      this.checksum -= checksum;
      if (checksum != this.checksum) {
         throw new Object(10);
      }
   }

   protected int readHeader(byte[] data, int offset) {
      int version = -1;
      int headerEnding = -1;

      try {
         this.data = data;
         this.offset = offset;
         if (this.readInt() != -749712059) {
            throw new Object(3);
         }

         version = this.readInt();
         if (version >>> 8 > 512) {
            throw new Object(1, ((StringBuffer)(new Object())).append((version & 0xFF0000) >>> 16).append(".").append(version >>> 8).toString());
         }

         if ((version & 0xFF0000) >>> 16 < 2) {
            throw new Object(2, ((StringBuffer)(new Object())).append((version & 0xFF0000) >>> 16).append(".").append(version >>> 8).toString());
         }

         headerEnding = this.readInt();
         return headerEnding != 218767370 ? version : version;
      } finally {
         throw new Object(4);
      }
   }

   @Override
   public Object createResourceFromURI(String uri, String suggestedType, ResourceContext context, Object referrer) {
      return null;
   }

   @Override
   public synchronized Object createResource(String type, Object data, ResourceContext context, Object referrer) {
      Object object = null;
      if ("application/x-vnd.rim.pme".equals(type)) {
         if (data instanceof AnimationModel) {
            object = this.read(data, ((AnimationModel)data).getRawData(), 0, true, context);
         } else if (data instanceof byte[]) {
            object = this.read(null, (byte[])data, 0, false, context);
            if (referrer instanceof Object) {
               this.getExternalResources((ResourceProvider)referrer, object);
            }
         }
      }

      return object;
   }

   private void getExternalResources(ResourceProvider param1, Object param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 2
      // 01: checkcast net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel
      // 04: astore 3
      // 05: aload 3
      // 06: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel.numSounds I
      // 09: aload 3
      // 0a: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel.numImages I
      // 0d: iadd
      // 0e: istore 4
      // 10: ldc_w ""
      // 13: astore 5
      // 15: bipush 0
      // 16: istore 6
      // 18: iload 6
      // 1a: iload 4
      // 1c: if_icmplt 22
      // 1f: goto cb
      // 22: aload 3
      // 23: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel.externalResources [Ljava/lang/String;
      // 26: iload 6
      // 28: aaload
      // 29: astore 5
      // 2b: aload 3
      // 2c: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel.objects [Ljava/lang/Object;
      // 2f: iload 6
      // 31: aload 1
      // 32: aload 5
      // 34: iload 6
      // 36: aload 3
      // 37: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel.numSounds I
      // 3a: if_icmpge 43
      // 3d: ldc_w "audio/midi"
      // 40: goto 46
      // 43: ldc_w "image/png"
      // 46: aload 0
      // 47: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/PME0_2Reader._context Lnet/rim/plazmic/internal/mediaengine/ResourceContext;
      // 4a: aconst_null
      // 4b: invokeinterface net/rim/plazmic/internal/mediaengine/ResourceProvider.createResourceFromURI (Ljava/lang/String;Ljava/lang/String;Lnet/rim/plazmic/internal/mediaengine/ResourceContext;Ljava/lang/Object;)Ljava/lang/Object; 5
      // 50: aastore
      // 51: goto c5
      // 54: astore 7
      // 56: ldc2_w -7509200465648525729
      // 59: new java/lang/Object
      // 5c: dup
      // 5d: ldc_w "PME02: Failed to load: "
      // 60: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 63: aload 5
      // 65: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 68: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 6b: invokevirtual java/lang/String.getBytes ()[B
      // 6e: bipush 3
      // 70: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 73: pop
      // 74: goto c5
      // 77: astore 7
      // 79: invokestatic net/rim/plazmic/internal/mediaengine/MediaFactory.getPlatform ()Lnet/rim/plazmic/internal/mediaengine/util/Platform;
      // 7c: aload 0
      // 7d: bipush 22
      // 7f: bipush -1
      // 81: aload 7
      // 83: invokeinterface net/rim/plazmic/internal/mediaengine/util/Platform.logDebug (Ljava/lang/Object;IILjava/lang/Object;)V 5
      // 88: aload 3
      // 89: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel.objects [Ljava/lang/Object;
      // 8c: iload 6
      // 8e: aconst_null
      // 8f: aastore
      // 90: new java/lang/Object
      // 93: dup
      // 94: invokespecial java/lang/StringBuffer.<init> ()V
      // 97: aload 3
      // 98: dup_x1
      // 99: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel.missingURLs Ljava/lang/String;
      // 9c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 9f: aload 5
      // a1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // a4: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // a7: putfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel.missingURLs Ljava/lang/String;
      // aa: new java/lang/Object
      // ad: dup
      // ae: invokespecial java/lang/StringBuffer.<init> ()V
      // b1: aload 3
      // b2: dup_x1
      // b3: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel.missingURLs Ljava/lang/String;
      // b6: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // b9: ldc_w "\n"
      // bc: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // bf: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // c2: putfield net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel.missingURLs Ljava/lang/String;
      // c5: iinc 6 1
      // c8: goto 18
      // cb: aload 0
      // cc: aload 3
      // cd: invokespecial net/rim/plazmic/internal/mediaengine/model/intarray/v0_0/PME0_2Reader.linkForeignObjects (Lnet/rim/plazmic/internal/mediaengine/model/intarray/v0_0/AnimationModel;)V
      // d0: return
      // try (22 -> 39): 40 null
      // try (22 -> 39): 54 null
   }

   private void linkForeignObjects(AnimationModel model) {
      int[] nodes = model.getNodes();
      Object[] objects = model.getObjects();
      AnimationPeer peer = null;

      for (int i = this.realRefList.length - 1; i >= 0; i--) {
         int nodeIndex = this.realRefList[i];
         if (nodes[nodeIndex] == 40) {
            int imgIndex = nodes[nodeIndex + 4];
            if (objects[imgIndex] != null) {
               Object obj = objects[imgIndex];
               if (obj instanceof Object) {
                  if (peer == null) {
                     peer = new AnimationPeer();
                     peer.setModel(model);
                  }

                  ForeignObject fo = (ForeignObject)obj;
                  fo.setHandle(nodeIndex);
                  fo.setPeer(peer);
               }
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Object read(Object m, byte[] data, int offset, boolean initOnly, ResourceContext context) {
      if (m == null) {
         m = new AnimationModel();
      }

      this._context = context;
      if (this._context != null) {
         this._context.set("Media", m);
      }

      this.model = (AnimationModel)m;
      this.model.reader = this;
      this.model.rawData = data;
      this.model.contentType = "application/x-vnd.rim.pme";
      this.model.numText = 0;
      this.model.init();
      this.data = data;
      this.offset = offset;
      this.checksum = 0;
      this.numPolys = 0;
      this.maxTextNodeIndex = -1;
      this.model.version = this.readHeader(data, offset);

      label214:
      try {
         this.encoding = this.readUTF();
      } catch (Throwable var28) {
         MediaFactory.getPlatform().logDebug(this, 22, -1, e);
         break label214;
      }

      this.visibleRefIndex = this.realRefIndex = 0;
      this.model.title = this.readString();
      this.model.description = this.readString();
      int narrowingBits = this.readData(3);
      this.objIndexMask = narrowingBits >>> 24 & 1;
      this.coordIndexMask = narrowingBits >>> 23 & 1;
      this.varDataMask = narrowingBits >>> 22 & 1;
      this.loopCountMask = narrowingBits >>> 21 & 1;
      this.scenerectwhMask = narrowingBits >>> 20 & 1;
      this.keyTimeIndexMask = narrowingBits >>> 19 & 1;
      this.keyValIndexMask = narrowingBits >>> 18 & 1;
      this.channelIndexMask = narrowingBits >>> 17 & 1;
      this.keyTimeMask = narrowingBits >>> 15 & 3;
      this.keyValMask = narrowingBits >>> 12 & 7;
      this.coordValMask = narrowingBits >>> 9 & 7;
      this.curChildMask = narrowingBits >>> 6 & 7;
      this.vNodeXCoordMask = narrowingBits >>> 3 & 7;
      this.vNodeYCoordMask = narrowingBits & 7;
      this.model.width = this.readData(this.scenerectwhMask);
      this.model.height = this.readData(this.scenerectwhMask);
      this.model.bkgColor = MediaFactory.getPlatform().getColor(this.readData(0) & 0xFF, this.readData(0) & 0xFF, this.readData(0) & 0xFF);
      this.model.sequenceRoot = this.readData(1);
      int dataSize = this.readData(1);
      this.model.nodes = new int[dataSize];
      int keyTimesSize = this.readData(1);
      this.model.keyTimes = new int[keyTimesSize];
      int keyValuesSize = this.readData(1);
      this.model.keyValues = new int[keyValuesSize];
      int numCoordArrays = this.readData(this.coordIndexMask);
      this.model.coords = new int[numCoordArrays][];
      int numObjects = this.readData(this.objIndexMask);
      if (this.model.objects == null || this.model.objects.length < numObjects) {
         this.model.objects = new Object[numObjects];
      }

      int numSounds = this.readData(this.objIndexMask);
      this.model.numSounds = numSounds;
      int numImages = this.readData(this.objIndexMask);
      this.model.numImages = numImages;
      this.interpolatorsCount = this.readData(1);
      this.interpolatorIndex = 0;
      this.model.numHotspots = this.readData(1);
      this.model.hotspotList = new int[this.model.numHotspots];
      this.model.numHotspots = 0;
      int channelDataSize = this.readData(1);
      this.model.channels = new int[channelDataSize];
      this.validateChecksum();
      this.model.polygonCounter = 0;
      this.visibleRefList = new int[10];
      this.realRefList = new int[10];
      this.readVisualNodeData(0);
      this.model.numMedia = numObjects - this.model.numText - this.model.numImages - this.model.numSounds;
      int[] tempArray = new int[this.visibleRefIndex];

      for (int i = this.visibleRefIndex - 1; i >= 0; i--) {
         tempArray[i] = this.visibleRefList[i];
      }

      this.visibleRefList = tempArray;
      tempArray = new int[this.realRefIndex];

      for (int i = this.realRefIndex - 1; i >= 0; i--) {
         tempArray[i] = this.realRefList[i];
      }

      this.realRefList = tempArray;
      this.readSequenceNodeData(this.model.sequenceRoot, -1);
      this.model.delta = new short[6 * this.realRefList.length];

      for (int i = this.realRefList.length - 1; i >= 0; i--) {
         this.model.nodes[this.realRefList[i] + 1] = this.model.nodes[this.realRefList[i] + 1] | 6 * i << 16;
         if (this.model.nodes[this.realRefList[i]] == 20) {
            this.model.delta[6 * i + 5] = (short)this.model.nodes[this.realRefList[i] + 11];
         }
      }

      this.model.nodes[1] = this.model.nodes[1] | 1;
      this.validateChecksum();

      for (int i = 0; i < numCoordArrays; i++) {
         int numCoords = this.readData(this.varDataMask);
         this.model.coords[i] = new int[numCoords];
         this.readDataArray(this.model.coords[i], 0, numCoords, this.coordValMask);
      }

      this.validateChecksum();
      int keyTimeIndex = 0;

      while (keyTimeIndex < keyTimesSize) {
         int numKeyTimes = this.readData(this.varDataMask);
         this.model.keyTimes[keyTimeIndex++] = numKeyTimes;
         this.model.keyTimes[keyTimeIndex++] = 0;
         this.readDataArray(this.model.keyTimes, keyTimeIndex, numKeyTimes - 1, this.keyTimeMask);
         keyTimeIndex += numKeyTimes - 1;
      }

      this.validateChecksum();
      int keyValueIndex = 0;

      while (keyValueIndex < keyValuesSize) {
         int numKeyValues = this.readData(this.varDataMask);
         this.model.keyValues[keyValueIndex++] = numKeyValues;
         this.readDataArray(this.model.keyValues, keyValueIndex, numKeyValues, this.keyValMask);
         keyValueIndex += numKeyValues;
      }

      this.validateChecksum();
      int numMedia = numSounds + numImages;
      if (numMedia == 0) {
         this.model.externalResources = null;
      } else {
         if (this.model.externalResources == null || this.model.externalResources.length != numMedia) {
            this.model.externalResources = new Object[numMedia];
         }

         for (int i = 0; i < numMedia; i++) {
            this.model.externalResources[i] = this.readString();
         }
      }

      this.readStringArray(this.model.objects, numSounds + numImages, numObjects - numSounds - numImages);
      this.validateChecksum();
      int channelIndex = 0;

      while (channelIndex < channelDataSize) {
         int numChannelValues = this.readData(this.varDataMask);
         this.model.channels[channelIndex++] = numChannelValues;
         this.readDataArray(this.model.channels, channelIndex, numChannelValues, 1);
         channelIndex += numChannelValues;
      }

      int k = 0;

      while (k < this.model.channels.length) {
         int chan = this.model.channels[k];

         for (int j = k + 1; j < k + chan + 1; j++) {
            this.model.nodes[this.model.channels[j] + 1] = this.model.nodes[this.model.channels[j] + 1] | 2;
         }

         k = k + chan + 1;
      }

      this.validateChecksum();
      this.model = null;
      this._context = null;
      return m;
   }

   private void determineStaticBackground(int index) {
   }

   public PME0_2Reader() {
      this.foundInterp = this.foundInterp;
   }
}
