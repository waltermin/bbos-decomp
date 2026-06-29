package net.rim.device.cldc.io.dns;

import net.rim.device.api.util.DataBuffer;

public final class DNSMessageIPv4Question {
   private String _qname;
   private int _qtype = 1;
   private int _qclass = 1;

   public final void setQname(String qname) {
      if (qname == null) {
         throw new IllegalArgumentException();
      }

      if (qname.length() == 0) {
         throw new IllegalArgumentException();
      }

      if (qname.length() > 255) {
         throw new IllegalArgumentException();
      }

      this._qname = qname;
   }

   public final String getQname() {
      return this._qname;
   }

   public final void setQtype(int qtype) {
      this._qtype = qtype;
   }

   public final int getQtype() {
      return this._qtype;
   }

   public final void setQclass(int qclass) {
      this._qclass = qclass;
   }

   public final int getQclass() {
      return this._qclass;
   }

   final void writeQuestion(DataBuffer db) {
      DNSMessageIPv4.writeDomainName(db, this._qname);
      db.writeShort(this._qtype);
      db.writeShort(this._qclass);
   }

   final void readQuestion(DataBuffer db) {
      this._qname = DNSMessageIPv4.readDomainName(db);
      this._qtype = db.readShort();
      this._qclass = db.readShort();
   }
}
