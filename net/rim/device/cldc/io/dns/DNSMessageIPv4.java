package net.rim.device.cldc.io.dns;

import java.util.Vector;
import net.rim.device.api.util.DataBuffer;

public final class DNSMessageIPv4 {
   protected int _id;
   protected int _flags;
   protected Vector _questionSection = new Vector();
   protected Vector _answerSection = new Vector();
   protected Vector _authoritySection = new Vector();
   protected Vector _additionalSection = new Vector();
   public static final int QR_QUERY;
   public static final int QR_RESPONSE;
   public static final int OPCODE_QUERY;
   public static final int OPCODE_IQUERY;
   public static final int OPCODE_STATUS;
   public static final int AA_NOTAUTHORITY;
   public static final int AA_AUTHORITY;
   public static final int TC_NOTTRUNCATED;
   public static final int TC_TRUNCATED;
   public static final int RD_RECURSIONNOTDESIRED;
   public static final int RD_RECURSIONDESIRED;
   public static final int RA_RECURSIONNOTAVAILABLE;
   public static final int RA_RECURSIONAVAILABLE;
   public static final int RCODE_NOERROR;
   public static final int RCODE_FORMATERROR;
   public static final int RCODE_SERVERFAILURE;
   public static final int RCODE_NAMEERROR;
   public static final int RCODE_NOTIMPLEMENTED;
   public static final int RCODE_REFUSED;
   public static final int TYPE_A;
   public static final int TYPE_NS;
   public static final int TYPE_MD;
   public static final int TYPE_MF;
   public static final int TYPE_CNAME;
   public static final int TYPE_SOA;
   public static final int TYPE_MB;
   public static final int TYPE_MG;
   public static final int TYPE_MR;
   public static final int TYPE_NULL;
   public static final int TYPE_WKS;
   public static final int TYPE_PTR;
   public static final int TYPE_HINFO;
   public static final int TYPE_MINFO;
   public static final int TYPE_MX;
   public static final int TYPE_TXT;
   public static final int QTYPE_AXFR;
   public static final int QTYPE_MAILB;
   public static final int QTYPE_MAILA;
   public static final int QTYPE_ALL;
   public static final int CLASS_IN;
   public static final int CLASS_CS;
   public static final int CLASS_CH;
   public static final int CLASS_HS;
   public static final int QCLASS_ALL;
   protected static final int RCODE_MASK;
   protected static final int RA_MASK;
   protected static final int RD_MASK;
   protected static final int TC_MASK;
   protected static final int AA_MASK;
   protected static final int OPCODE_MASK;
   protected static final int QR_MASK;
   static final int MAX_CNAME_ATTEMPTS;
   static final int MAX_REFERRAL_ATTEMPTS;

   public final int getID() {
      return this._id;
   }

   public final void setID(int id) {
      this._id = id;
   }

   public final int getQR() {
      return this._flags & 32768;
   }

   public final void setQR(int qr) {
      if (qr != 0 && qr != 32768) {
         throw new IllegalArgumentException();
      }

      this._flags = this._flags & -32769 | qr;
   }

   public final int getOpcode() {
      return this._flags & 30720;
   }

   public final void setOpcode(int opcode) {
      if (opcode != 0 && opcode != 2048 && opcode != 4096) {
         throw new IllegalArgumentException();
      }

      this._flags = this._flags & -30721 | opcode;
   }

   public final int getAA() {
      return this._flags & 1024;
   }

   public final void setAA(int aa) {
      if (aa != 0 && aa != 1024) {
         throw new IllegalArgumentException();
      }

      this._flags = this._flags & -1025 | aa;
   }

   public final int getTC() {
      return this._flags & 512;
   }

   public final void setTC(int tc) {
      if (tc != 0 && tc != 512) {
         throw new IllegalArgumentException();
      }

      this._flags = this._flags & -513 | tc;
   }

   public final int getRD() {
      return this._flags & 256;
   }

   public final void setRD(int rd) {
      if (rd != 0 && rd != 256) {
         throw new IllegalArgumentException();
      }

      this._flags = this._flags & -257 | rd;
   }

   public final int getRA() {
      return this._flags & 128;
   }

   public final void setRA(int ra) {
      if (ra != 0 && ra != 128) {
         throw new IllegalArgumentException();
      }

      this._flags = this._flags & -129 | ra;
   }

   public final int getRcode() {
      return this._flags & 15;
   }

   public final void setRcode(int rcode) {
      if (rcode != 0 && rcode != 1 && rcode != 2 && rcode != 3 && rcode != 4 && rcode != 5) {
         throw new IllegalArgumentException();
      }

      this._flags = this._flags & -16 | rcode;
   }

   final void setFlags(int flags) {
      this._flags = flags;
   }

   final int getFlags() {
      return this._flags;
   }

   public final void addQuestion(DNSMessageIPv4Question dnsQuestion) {
      this._questionSection.addElement(dnsQuestion);
   }

   public final Vector getQuestions() {
      return this._questionSection;
   }

   public final void addAnswer(DNSMessageIPv4Resource dnsAnswer) {
      this._answerSection.addElement(dnsAnswer);
   }

   public final Vector getAnswers() {
      return this._answerSection;
   }

   public final void addAuthority(DNSMessageIPv4Resource dnsAuthority) {
      this._authoritySection.addElement(dnsAuthority);
   }

   public final Vector getAuthorities() {
      return this._authoritySection;
   }

   public final void addAdditional(DNSMessageIPv4Resource dnsAdditional) {
      this._additionalSection.addElement(dnsAdditional);
   }

   public final Vector getAdditional() {
      return this._additionalSection;
   }

   final void writeMessage(DataBuffer db) {
      int qdCount = this._questionSection.size();
      int anCount = this._answerSection.size();
      int nsCount = this._authoritySection.size();
      int arCount = this._additionalSection.size();
      db.writeShort(this.getID());
      db.writeShort(this.getFlags());
      db.writeShort(qdCount);
      db.writeShort(anCount);
      db.writeShort(nsCount);
      db.writeShort(arCount);

      for (int i = 0; i < qdCount; i++) {
         ((DNSMessageIPv4Question)this._questionSection.elementAt(i)).writeQuestion(db);
      }

      for (int var7 = 0; var7 < anCount; var7++) {
         ((DNSMessageIPv4Resource)this._answerSection.elementAt(var7)).writeResource(db);
      }

      for (int var8 = 0; var8 < nsCount; var8++) {
         ((DNSMessageIPv4Resource)this._authoritySection.elementAt(var8)).writeResource(db);
      }

      for (int var9 = 0; var9 < arCount; var9++) {
         ((DNSMessageIPv4Resource)this._additionalSection.elementAt(var9)).writeResource(db);
      }
   }

   final void readMessage(DataBuffer db) {
      this.setID(db.readShort());
      this.setFlags(db.readShort());
      int qd = db.readUnsignedShort();
      int an = db.readUnsignedShort();
      int ns = db.readUnsignedShort();
      int ad = db.readUnsignedShort();

      for (int i = 0; i < qd; i++) {
         DNSMessageIPv4Question question = new DNSMessageIPv4Question();
         question.readQuestion(db);
         this.addQuestion(question);
      }

      for (int var9 = 0; var9 < an; var9++) {
         DNSMessageIPv4Resource resource = new DNSMessageIPv4Resource();
         resource.readResource(db);
         this.addAnswer(resource);
      }

      for (int var10 = 0; var10 < ns; var10++) {
         DNSMessageIPv4Resource resource = new DNSMessageIPv4Resource();
         resource.readResource(db);
         this.addAuthority(resource);
      }

      for (int var11 = 0; var11 < ad; var11++) {
         DNSMessageIPv4Resource resource = new DNSMessageIPv4Resource();
         resource.readResource(db);
         this.addAdditional(resource);
      }
   }

   static final String readDomainName(DataBuffer db) {
      StringBuffer strBuf = new StringBuffer(32);
      readDomainName(db, strBuf);
      return strBuf.toString();
   }

   static final void readDomainName(DataBuffer db, StringBuffer strBuf) {
      int len = db.readUnsignedByte();

      while (len > 0) {
         if ((len & 192) != 0) {
            int backOffset = (len & 63) << 8;
            backOffset |= db.readUnsignedByte();
            int curPos = db.getPosition();
            db.setPosition(backOffset);
            readDomainName(db, strBuf);
            db.setPosition(curPos);
            return;
         }

         while (--len >= 0) {
            strBuf.append((char)db.readUnsignedByte());
         }

         len = db.readUnsignedByte();
         if (len > 0) {
            strBuf.append('.');
         }
      }
   }

   static final void writeDomainName(DataBuffer db, String name) {
      if (name != null && name.length() > 0) {
         byte[] strBytes = name.getBytes();
         int curPos = 0;

         while (curPos < name.length()) {
            int nextPos = name.indexOf(46, curPos);
            if (nextPos == -1) {
               nextPos = name.length();
            }

            int length = nextPos - curPos;
            if (length == 0) {
               throw new IllegalArgumentException();
            }

            if (length > 63) {
               throw new IllegalArgumentException();
            }

            db.writeByte(length);
            db.write(strBytes, curPos, length);
            curPos = nextPos + 1;
         }
      }

      db.writeByte(0);
   }
}
