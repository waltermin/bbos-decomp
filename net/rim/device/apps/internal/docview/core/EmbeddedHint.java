package net.rim.device.apps.internal.docview.core;

public final class EmbeddedHint {
   public short _index = -1;
   public byte _type = -1;
   public String _name;
   public String _owner;
   public Object _previewData;
   public boolean _parsedOnce;
   public boolean _complete;
   public int _flipImage;

   public EmbeddedHint(byte type) {
      this._type = type;
   }
}
