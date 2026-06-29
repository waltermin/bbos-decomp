package net.rim.device.apps.internal.docview.gui;

final class EmbeddedBreak extends BreakObj {
   final String _domID;
   final int _type;
   final String _name;

   EmbeddedBreak(String name, int type, String domID, int charOffset) {
      super(charOffset);
      this._type = type;
      this._name = name;
      this._domID = domID;
   }

   @Override
   final BreakObj cloneObject() {
      return new EmbeddedBreak(this._name, this._type, this._domID, super._charOffset);
   }
}
