package net.rim.tid.im.conv.europe.repository;

import java.io.DataInputStream;

public final class Reader$Header {
   public int signature = 1397507668;
   public short header_size;
   public short version_major;
   public short version_minor;
   public short iPriority;
   public int type;
   public String iLocale;
   public int iMaxWordLength;
   public byte iMinWordLength;
   public int iMaxLevel;
   public int i1CharWordNo;
   public short i1CharWordUpperCase;
   public byte _caseControlCount;

   protected Reader$Header() {
   }

   public final void read(DataInputStream aDis) throws Exception {
      aDis.mark(Integer.MAX_VALUE);
      if (this.signature != aDis.readInt()) {
         throw new Exception("Incorrect signature");
      }

      this.header_size = aDis.readShort();
      this.version_major = aDis.readShort();
      this.version_minor = aDis.readShort();
      this.iPriority = aDis.readShort();
      this.type = aDis.readByte();
      this.iMaxWordLength = aDis.readByte();
      this.iMaxLevel = aDis.readByte();
      int len = aDis.readByte();
      byte[] locale = new byte[len];
      aDis.read(locale);
      this.iLocale = new String(locale);
      if (this.version_major <= 1 && this.version_minor <= 3) {
         this.i1CharWordNo = 1;
         this.i1CharWordUpperCase = 0;
         this.iMinWordLength = 1;
      } else {
         this.i1CharWordNo = aDis.readByte();
         this.i1CharWordUpperCase = aDis.readShort();
         this.iMinWordLength = aDis.readByte();
         if (this.version_major <= 1 && this.version_minor <= 4) {
            this._caseControlCount = 5;
         } else {
            this._caseControlCount = aDis.readByte();
         }
      }

      aDis.reset();
      aDis.skip(this.header_size);
   }
}
