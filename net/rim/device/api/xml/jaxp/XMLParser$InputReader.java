package net.rim.device.api.xml.jaxp;

import org.xml.sax.InputSource;

class XMLParser$InputReader {
   boolean _returnEndOfEntity;
   protected XMLParser$InputReader _next;
   private boolean _havePeek;
   private int _peek;
   private String _entityName;
   private boolean _isPE;
   private boolean _allowSurrogateLow;
   private String _systemId;
   private String _publicId;
   protected int _row;
   protected int _column;
   protected boolean _isExternalParsedEntity;
   private final XMLParser this$0;

   XMLParser$InputReader(XMLParser _1) {
      this.this$0 = _1;
      this._row = 1;
      this._next = _1._in;
      _1._in = this;
   }

   void setEncoding(String enc) {
   }

   void setReturnEndOfEntity(boolean on) {
      this._returnEndOfEntity = on;
   }

   void setEntityId(String publicId, String systemId) {
      this._systemId = systemId;
      this._publicId = publicId;
      this._isExternalParsedEntity = true;
   }

   String getPublicId() {
      return this._publicId;
   }

   String getSystemId() {
      return this._systemId;
   }

   int getLineNumber() {
      return this._row;
   }

   int getColumnNumber() {
      return this._column;
   }

   void setColumnNumber(int column) {
      this._column = column;
   }

   XMLParser$InputReader getNext() {
      return this._next;
   }

   void setEntityName(String entityName, boolean isPE) {
      if (entityName != null) {
         this._entityName = entityName;
         this._isPE = isPE;

         for (XMLParser$InputReader rdr = this._next; rdr != null; rdr = rdr._next) {
            if (rdr._entityName != null && rdr._isPE == isPE && rdr._entityName.equals(entityName)) {
               this.this$0.fatalError("Entity '%1' may not reference itself, either directly or indirectly.", entityName);
            }
         }
      }
   }

   protected int nextChar() {
      throw null;
   }

   boolean isEntity() {
      return this._entityName != null;
   }

   void setLastRead() {
      this.this$0._lastReadInput = this;
   }

   private void pop() {
      if (this == this.this$0._in) {
         this.this$0._in = this._next;
      } else {
         XMLParser$InputReader curr = this.this$0._in;

         while (curr._next != this) {
            curr = curr._next;
         }

         curr._next = this._next;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int read() {
      this.this$0._in.setLastRead();
      int ch;
      if (this._havePeek) {
         this._havePeek = false;
         ch = this._peek;
      } else {
         ch = this.nextChar();
      }

      switch (ch) {
         case -1:
            if (this._returnEndOfEntity) {
               this._returnEndOfEntity = false;
               return ch;
            } else {
               if (this._next == null) {
                  this.this$0.eof();
                  return -1;
               }

               this.pop();
               return this.this$0._in.read();
            }
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 11:
         case 12:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 65534:
         case 65535:
            this.this$0.illegalCharacter(ch);
            return -1;
         case 13:
            if (!this.this$0._lastReadInput._isExternalParsedEntity) {
               return 13;
            } else {
               this._peek = this.nextChar();
               if (this._peek != 10) {
                  this._havePeek = true;
               }
            }
         case 10:
            this._column = 0;
            this._row++;
            return 10;
         case 37:
            if (!this.this$0._processPERefs) {
               return ch;
            } else {
               if (this.this$0._inInternalSubset && !this.this$0._isDeclSep) {
                  this.this$0.fatalError("A parameter entity reference may only appear between markup in the internal DTD subset.");
               }

               StringBuffer oldDTDBody = this.this$0._dtdBody;
               this.this$0._dtdBody = null;
               boolean var8 = false /* VF: Semaphore variable */;

               String entityName;
               try {
                  var8 = true;
                  this.this$0.pushName();
                  this.this$0.reqName();
                  entityName = this.this$0.nameToString();
                  var8 = false;
               } finally {
                  if (var8) {
                     this.this$0.popName();
                  }
               }

               this.this$0.popName();
               this.this$0.expect(';');
               if (oldDTDBody != null) {
                  oldDTDBody.append('%');
                  oldDTDBody.append(entityName);
                  oldDTDBody.append(';');
               }

               String entity = (String)this.this$0._parameterEntities.get(entityName);
               new XMLParser$SetDTDBody(this.this$0, oldDTDBody);
               if (this.this$0._isDeclSep) {
                  new XMLParser$EndOfEntity(this.this$0);
               }

               if (entity != null) {
                  if (this.this$0._inEntityValue) {
                     new XMLParser$EntityReader(this.this$0, entityName, entity, true);
                  } else {
                     new XMLParser$CharacterEntityReader(this.this$0, 32);
                     new XMLParser$EntityReader(this.this$0, entityName, entity, true);
                     new XMLParser$CharacterEntityReader(this.this$0, 32);
                  }
               } else {
                  XMLParser$ExternalEntity extEnt = (XMLParser$ExternalEntity)this.this$0._externalParameterEntities.get(entityName);
                  if (extEnt == null) {
                     this.this$0.fatalError("Undefined parameter entity reference.", entityName);
                     return -1;
                  }

                  InputSource is = this.this$0._entityResolver.resolveEntity(extEnt.publicId, extEnt.systemId);
                  if (is == null) {
                     this.this$0.sendChars(false);
                     this.this$0._contentHandler.skippedEntity(entityName);
                     return this.this$0._in.read();
                  }

                  if (this.this$0._inEntityValue) {
                     this.this$0.pushExternalParsedEntity(entityName, false, extEnt.publicId, extEnt.systemId, is);
                  } else {
                     new XMLParser$CharacterEntityReader(this.this$0, 32);
                     this.this$0.pushExternalParsedEntity(entityName, false, extEnt.publicId, extEnt.systemId, is);
                     new XMLParser$CharacterEntityReader(this.this$0, 32);
                  }
               }

               if (this.this$0._isDeclSep) {
                  this.this$0.extSubsetDecl();
               }

               return this.this$0._in.read();
            }
         default:
            if (ch <= 55295) {
               return ch;
            } else if (ch >= 57344 && ch <= 65533) {
               return ch;
            } else if (ch >= 56320 && ch <= 57343 && this._allowSurrogateLow) {
               this._allowSurrogateLow = false;
               return ch;
            } else {
               if (ch >= 55296 && ch <= 56319) {
                  this._allowSurrogateLow = true;
                  this._peek = this.nextChar();
                  this._havePeek = true;
                  if (this._peek >= 56320 && this._peek <= 57343) {
                     this._allowSurrogateLow = true;
                     return ch;
                  }
               }

               this.this$0.illegalCharacter(ch);
               return -1;
            }
      }
   }
}
