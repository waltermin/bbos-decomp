package net.rim.tid.im.options.SpellCheck;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class SpellCheckOptions$SpellCheckOptionsSyncItem extends OTASyncCapableSyncItem {
   private DataBuffer _buffer;
   private static final int DB_VERSION = 0;

   @Override
   public final String getSyncName() {
      return "Spell Check Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final synchronized boolean setSyncData(DataBuffer buffer, int version) {
      SpellCheckOptions options = SpellCheckOptions.getOptions();

      label49:
      try {
         buffer.readShort();
         buffer.readByte();

         while (!buffer.eof()) {
            switch (buffer.readInt()) {
               case 1:
                  options.setFlag(1, buffer.readByte());
                  break;
               case 2:
                  options.setFlag(2, buffer.readByte());
                  break;
               case 4:
                  options.setFlag(4, buffer.readByte());
                  break;
               case 8:
                  options.setFlag(8, buffer.readByte());
                  break;
               case 16:
                  options.setFlag(16, buffer.readByte());
                  break;
               case 32:
                  options.setFlag(32, buffer.readByte());
                  break;
               case 64:
                  options.setFlag(64, buffer.readByte());
                  break;
               case 128:
                  options.setFlag(128, buffer.readByte());
                  break;
               case 256:
                  options.setFlag(256, buffer.readByte());
                  break;
               case 65536:
                  options.setSuggestionsLevel(buffer.readByte());
                  break;
               case 131072:
                  options.setMinWordSizeForCheck(buffer.readByte());
                  break;
               case 262144:
                  options.setLearningBufferCustomDictSize(buffer.readByte());
                  break;
               case 524288:
                  options.setLearningBufferReplacementPairsSize(buffer.readByte());
                  break;
               case 1048576:
                  options.setContainerSize(buffer.readByte());
                  break;
               default:
                  buffer.readByte();
            }
         }
      } finally {
         break label49;
      }

      options.commit();
      this.fireSyncItemUpdated();
      return true;
   }

   @Override
   public final synchronized boolean getSyncData(DataBuffer buffer, int version) {
      SpellCheckOptions options = SpellCheckOptions.getOptions();
      this._buffer = new DataBuffer(buffer.isBigEndian());
      this._buffer.writeInt(0);
      this._buffer.writeByte(options.getFlag(1));
      this._buffer.writeInt(2);
      this._buffer.writeByte(options.getFlag(2));
      this._buffer.writeInt(4);
      this._buffer.writeByte(options.getFlag(4));
      this._buffer.writeInt(8);
      this._buffer.writeByte(options.getFlag(8));
      this._buffer.writeInt(16);
      this._buffer.writeByte(options.getFlag(16));
      this._buffer.writeInt(32);
      this._buffer.writeByte(options.getFlag(32));
      this._buffer.writeInt(64);
      this._buffer.writeByte(options.getFlag(64));
      this._buffer.writeInt(65536);
      this._buffer.writeByte(options.getSuggestionsLevel());
      this._buffer.writeInt(131072);
      this._buffer.writeByte(options.getMinWordSizeForCheck());
      this._buffer.writeInt(262144);
      this._buffer.writeByte(options.getLearningBufferCustomDictSize());
      this._buffer.writeInt(524288);
      this._buffer.writeByte(options.getLearningBufferReplacementPairsSize());
      this._buffer.writeInt(1048576);
      this._buffer.writeByte(options.getContainerSize());
      this._buffer.writeInt(128);
      this._buffer.writeByte(options.getFlag(128));
      this._buffer.writeInt(256);
      this._buffer.writeByte(options.getFlag(256));
      this._buffer.writeByte(0);
      int l = this._buffer.getLength();
      this._buffer.rewind();
      buffer.writeShort(l);
      buffer.writeByte(0);
      buffer.write(this._buffer, l);
      this._buffer = null;
      return true;
   }
}
