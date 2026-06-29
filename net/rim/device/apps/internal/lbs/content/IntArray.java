package net.rim.device.apps.internal.lbs.content;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringTokenizer;

public final class IntArray {
   int _count;
   DataBuffer _db;

   public IntArray() {
   }

   IntArray(DataBuffer db) {
      int length = db.readCompressedInt();
      this._count = db.readShort();
      db.setPosition(db.getPosition() - 2);
      this._db = new DataBuffer();
      byte[] data = new byte[length];
      int start = db.getPosition();
      System.arraycopy(db.getArray(), start, data, 0, length);
      this._db.setData(data, 0, length);
      db.setPosition(start + length);
      this.end();
   }

   IntArray(String csv) {
      StringTokenizer tokenizer = new StringTokenizer(csv, ',');
      this.start();

      while (tokenizer.hasMoreTokens()) {
         this.add(Integer.parseInt(tokenizer.nextToken()));
      }

      this.end();
   }

   public final void start() {
      this._db = new DataBuffer();
      this._db.writeShort(0);
   }

   public final void add(int i) {
      LocationDocumentConverter.writeCompressedSignedInt(this._db, i);
      this._count++;
   }

   public final void end() {
      int end = this._db.getPosition();
      this._db.setPosition(0);
      byte hi = (byte)(this._count >> 8 & 0xFF);
      byte lo = (byte)(this._count & 0xFF);
      this._db.writeByte(hi);
      this._db.writeByte(lo);
      this._db.setPosition(end);
   }

   final byte[] getBytes() {
      return this._db.toArray();
   }

   public final int getPointCount() {
      return this._count >> 1;
   }

   public final void getPoints(int[] ax, int[] ay) {
      try {
         this._db.setPosition(2);
         int count = this.getPointCount();
         int x = 0;
         int y = 0;

         for (int i = 0; i < count; i++) {
            x += LocationDocumentConverter.readCompressedSignedInt(this._db);
            ax[i] = x;
            y += LocationDocumentConverter.readCompressedSignedInt(this._db);
            ay[i] = y;
         }
      } finally {
         return;
      }
   }
}
