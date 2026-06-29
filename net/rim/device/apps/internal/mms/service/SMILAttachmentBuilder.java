package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.system.Display;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.MMSAttachmentImpl;

final class SMILAttachmentBuilder {
   private AttachmentDataProvider _attachmentProvider;
   private int _indentLevel;
   private int _textCount;
   private int _imageCount;
   private int _videoCount;
   private int _audioCount;
   private int _illegalCount;
   private int _textCountInPar;
   private int _imageCountInPar;
   private int _videoCountInPar;
   private int _audioCountInPar;
   private boolean _inPar;
   private StringBuffer _parBuf = (StringBuffer)(new Object());
   private StringBuffer _buf = (StringBuffer)(new Object("<?xml version=\"1.0\"?>"));

   private SMILAttachmentBuilder(AttachmentDataProvider attachmentProvider) {
      this._attachmentProvider = attachmentProvider;
      this.append("<smil>", 1);
      this.append("<head>", 1);
      this.append("<layout>", 1);
      String width = Integer.toString(Display.getWidth());
      String height = Integer.toString(Display.getHeight());
      this.append(((StringBuffer)(new Object("<root-layout width=\""))).append(width).append("\" height=\"").append(height).append("\"/>").toString());
      this.append("<region id=\"Image\" width=\"100%\" height=\"70%\" fit=\"meet\"/>");
      this.append("<region id=\"Text\" width=\"100%\" height=\"30%\" top=\"70%\" fit=\"scroll\"/>");
      this.append("</layout>", -1);
      this.append("</head>", -1);
      this.append("<body>", 1);
   }

   public static final MMSAttachment convert(MMSPresentationModel presentationModel, AttachmentDataProvider attachmentProvider) {
      SMILAttachmentBuilder builder = new SMILAttachmentBuilder(attachmentProvider);
      if (presentationModel != null) {
         builder.copyFrom(presentationModel);
      }

      return builder.getAttachment();
   }

   private final void copyFrom(MMSPresentationModel presentationModel) {
      presentationModel.copyTo(new SMILAttachmentBuilder$1(this));
   }

   private final MMSAttachment getAttachment() {
      if (this._textCount <= 1 && this._imageCount == 0 && this._audioCount == 0 && this._videoCount == 0) {
         return null;
      }

      if (this._illegalCount > 0) {
         return null;
      }

      this.endPar();
      this.append("</body>", -1);
      this.append("</smil>", -1);

      try {
         String charset = "utf-8";
         byte[] data = this._buf.toString().getBytes(charset);
         int type = 65537;
         String name = "mmm.smil";
         return new MMSAttachmentImpl(name, type, data, charset);
      } finally {
         ;
      }
   }

   private final void addContent(String name, int type) {
      if (MMSUtilities.isTextType(type)) {
         int size = this._attachmentProvider.getAttachment(name).getDataSize();
         if (size == 0) {
            return;
         }
      }

      String paramSrc = ((StringBuffer)(new Object("src=\"cid:"))).append(name).append('"').toString();
      String paramType = ((StringBuffer)(new Object("type=\""))).append(MMSUtilities.getMIMETypeString(type)).append('"').toString();
      if (MMSUtilities.isImageType(type)) {
         if (this._videoCountInPar > 0 || this._imageCountInPar > 0) {
            this.endPar();
         }

         this.beginPar();
         this.append(((StringBuffer)(new Object("<img "))).append(paramSrc).append(" region=\"Image\" ").append(paramType).append("/>").toString());
         this._imageCount++;
         this._imageCountInPar++;
      } else if (MMSUtilities.isAudioType(type)) {
         if (this._videoCountInPar > 0 || this._audioCountInPar > 0) {
            this.endPar();
         }

         this.beginPar();
         this.append(((StringBuffer)(new Object("<audio "))).append(paramSrc).append(' ').append(paramType).append("/>").toString());
         this._audioCount++;
         this._audioCountInPar++;
      } else if (!MMSUtilities.isVideoType(type)) {
         if (this.isIllegalType(type)) {
            this._illegalCount++;
         } else {
            if (this._textCountInPar > 0) {
               this.endPar();
            }

            this.beginPar();
            this.append(((StringBuffer)(new Object("<text "))).append(paramSrc).append(" region=\"Text\" ").append(paramType).append("/>").toString());
            this._textCount++;
            this._textCountInPar++;
         }
      } else {
         if (this._videoCountInPar > 0 || this._audioCountInPar > 0 || this._imageCountInPar > 0) {
            this.endPar();
         }

         this.beginPar();
         this.append(((StringBuffer)(new Object("<video "))).append(paramSrc).append(" region=\"Image\" ").append(paramType).append("/>").toString());
         this._videoCount++;
         this._videoCountInPar++;
      }
   }

   private final void beginPar() {
      if (!this._inPar) {
         this._inPar = true;
         this._parBuf.setLength(0);
         this._textCountInPar = 0;
         this._imageCountInPar = 0;
         this._videoCountInPar = 0;
         this._audioCountInPar = 0;
      }
   }

   private final void endPar() {
      this.endPar(5);
   }

   private final void endPar(int duration) {
      if (this._inPar) {
         this._inPar = false;
         this.append("<par>");
         String dur = ((StringBuffer)(new Object("dur=\""))).append(Integer.toString(duration)).append("s\" ").toString();
         String par = this._parBuf.toString();
         par = insertParam(par, "<img ", dur);
         par = insertParam(par, "<text ", dur);
         this._buf.append(par);
         this.append("</par>");
      }
   }

   private static final String insertParam(String buf, String prefix, String param) {
      int idx = 0;

      while (idx < buf.length()) {
         idx = buf.indexOf(prefix, idx);
         if (idx < 0) {
            return buf;
         }

         idx += prefix.length();
         buf = ((StringBuffer)(new Object())).append(buf.substring(0, idx)).append(param).append(buf.substring(idx)).toString();
         idx += param.length();
      }

      return buf;
   }

   private final void append(String line, int indent) {
      if (indent > 0) {
         this.append(line);
         this._indentLevel += indent;
      } else {
         this._indentLevel += indent;
         this.append(line);
      }
   }

   private final void append(String line) {
      if (this._inPar) {
         append(this._parBuf, line, this._indentLevel);
      } else {
         append(this._buf, line, this._indentLevel);
      }
   }

   private static final void append(StringBuffer buf, String line, int indentLevel) {
      buf.append('\n');

      for (int count = indentLevel; count > 0; count--) {
         buf.append(' ');
      }

      buf.append(line);
   }

   private final boolean isIllegalType(int type) {
      switch (type) {
         case 5:
            return false;
         case 6:
         case 7:
         default:
            return true;
      }
   }
}
