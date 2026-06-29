package net.rim.device.apps.internal.api.quincy;

import java.util.Vector;

public class Report {
   private String _body;
   private String _subject;
   private String _mimeType;
   private Vector _extra;
   private long _timestamp;
   public static final String TYPE_TEXT_PLAIN = "text/plain";
   public static final String TYPE_TEXT_XML = "text/xml";
   public static final String TYPE_APPLICATION_XBUGDISP = "application/x-bugdisp";
   public static final String TYPE_APPLICATION_ZIP = "application/zip";
   public static final String TYPE_APPLICATION_XUNKNOWN = "application/x-unknown";
   public static final String TYPE_APPLICATION_UNKNOWN = "application/unknown";

   public Report() {
      this("", "", "");
   }

   public Report(String subject, String body) {
      this(subject, body, "");
   }

   public Report(String subject, String body, String mime) {
      this.setSubject(subject);
      this.setBody(body);
      this.setMIMEType(mime);
      this.setExtra((Vector)(new Object()));
      this.setTimestamp(System.currentTimeMillis());
   }

   public String getBody() {
      return this._body == null ? "" : this._body;
   }

   public void setBody(String body) {
      if (body != null) {
         this._body = body;
      }
   }

   public String getSubject() {
      return this._subject != null ? this._subject : "";
   }

   public void setSubject(String subject) {
      if (subject != null) {
         this._subject = subject;
      }
   }

   public String getMIMEType() {
      return this._mimeType != null ? this._mimeType : "application/x-unknown";
   }

   public void setMIMEType(String mime) {
      if (mime != null) {
         this._mimeType = mime;
      }
   }

   public void addExtra(Object o) {
      if (o != null) {
         if (!(o instanceof Report)) {
            this._extra.addElement(o);
         } else {
            Report r = (Report)o;
            r.setBody(r.getBody().trim());
            this._extra.addElement(r);
         }
      }
   }

   public void setExtra(Vector v) {
      if (v != null) {
         this._extra = v;
      }
   }

   public Vector getExtra() {
      return this._extra;
   }

   public long getTimestamp() {
      return this._timestamp;
   }

   public void setTimestamp(long time) {
      this._timestamp = time >= 0 ? time : this._timestamp;
   }
}
