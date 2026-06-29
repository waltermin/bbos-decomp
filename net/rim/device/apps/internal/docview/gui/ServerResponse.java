package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.StringTokenizer;

public final class ServerResponse {
   public String _archiveIndicator;
   public String _findPattern;
   public boolean _findNext = true;
   public int _findIncomplete;
   public boolean _findCaseSensitive;
   public XYRect _areaToEnlarge;
   public short _errorCode = 254;
   public DocViewAtomicID _docID = new DocViewAtomicID();
   public int _crtBlockIndex = -1;
   public boolean _ftr;
   public boolean _imgInfoPresent;
   public int _xcsChunkSize;
   public int _chunkSize;
   public String[] _arbDOMIDStartArray;
   public int _lastEndChunkForDomID = -1;
   public String _pageDomIDs;
   int _reqScreenWidth;
   int _reqScreenHeight;

   final void setParam(String paramName, String value) {
      if (paramName.compareTo("ERRNO") == 0) {
         this._errorCode = Short.parseShort(value);
      } else if (paramName.compareTo("PARTIDX") == 0) {
         this._docID._partIndex = Integer.parseInt(value);
      } else if (paramName.compareTo("BLOCKIDX") == 0) {
         this._crtBlockIndex = Integer.parseInt(value);
      } else if (paramName.compareTo("BLOCKCNT") == 0) {
         this._docID._totalBlocks = Integer.parseInt(value);
      } else if (paramName.compareTo("SRCTY") == 0) {
         this._docID._srcType = Byte.parseByte(value);
      } else if (paramName.compareTo("DOMID") == 0) {
         this._docID._domID = value;
      } else if (paramName.compareTo("AIDX") == 0) {
         this._archiveIndicator = value;
      } else if (paramName.compareTo("PTN") == 0) {
         this._findPattern = value;
      } else {
         if (paramName.compareTo("F") == 0) {
            switch (Integer.parseInt(value)) {
               case 0:
                  break;
               case 1:
               default:
                  this._findCaseSensitive = true;
                  this._findNext = true;
                  return;
               case 2:
                  this._findCaseSensitive = false;
                  this._findNext = false;
                  return;
               case 3:
                  this._findCaseSensitive = true;
                  this._findNext = false;
                  return;
            }
         } else {
            if (paramName.compareTo("MV") == 0) {
               this._findIncomplete = Integer.parseInt(value);
               return;
            }

            if (paramName.compareTo("FT") == 0) {
               int dotIndex = value.indexOf(46);
               if (dotIndex == -1) {
                  this._docID._docType = Byte.parseByte(value);
                  return;
               }

               if (dotIndex != value.length() - 1) {
                  this._docID._docType = Byte.parseByte(value.substring(0, dotIndex));
                  this._docID._docSubtype = Byte.parseByte(value.substring(dotIndex + 1));
                  return;
               }
            } else if (paramName.compareTo("EFT") == 0) {
               int dotIndex = value.indexOf(46);
               if (dotIndex == -1) {
                  this._docID._embObjType = Byte.parseByte(value);
                  return;
               }

               if (dotIndex != value.length() - 1) {
                  this._docID._embObjType = Byte.parseByte(value.substring(0, dotIndex));
                  this._docID._embObjSubtype = Byte.parseByte(value.substring(dotIndex + 1));
                  return;
               }
            } else {
               if (paramName.compareTo("IRD") == 0) {
                  StringTokenizer tokenizer = new StringTokenizer(value, 'x');
                  int[] dims = new int[4];
                  dims[0] = dims[1] = -1;
                  dims[2] = dims[3] = 0;
                  int index = 0;

                  while (tokenizer.hasMoreTokens() && index < 4) {
                     try {
                        dims[index++] = Integer.parseInt(tokenizer.nextToken());
                     } finally {
                        break;
                     }
                  }

                  if (dims[0] != -1 && dims[1] != -1 && dims[2] != 0 && dims[3] != 0) {
                     this._areaToEnlarge = new XYRect(dims[0], dims[1], dims[2], dims[3]);
                  }

                  StringTokenizer var12 = null;
                  dims = null;
                  return;
               }

               if (paramName.compareTo("SArDOMID") == 0) {
                  this._docID._arbDOMID = value;
                  return;
               }

               if (paramName.compareTo("S") == 0) {
                  this._docID._arbDOMIDStartBlock = Integer.parseInt(value);
                  return;
               }

               if (paramName.compareTo("L") == 0) {
                  this._docID._arbDOMIDEndBlock = Integer.parseInt(value);
                  return;
               }

               if (paramName.compareTo("FTR") == 0) {
                  this._ftr = Integer.parseInt(value) == 1;
                  return;
               }

               if (paramName.compareTo("SArDOMIDs") == 0) {
                  StringTokenizer tokenizer = new StringTokenizer(value, ',');
                  int count = tokenizer.countTokens();
                  if (count > 0) {
                     this._arbDOMIDStartArray = new String[count];
                     int index = 0;

                     while (tokenizer.hasMoreTokens()) {
                        this._arbDOMIDStartArray[index++] = tokenizer.nextToken();
                     }
                  }
               } else {
                  if (paramName.compareTo("RDomIds") == 0) {
                     this._pageDomIDs = value;
                     return;
                  }

                  if (paramName.compareTo("LEC") == 0) {
                     this._lastEndChunkForDomID = Integer.parseInt(value);
                     return;
                  }

                  if (paramName.compareTo("DI") == 0 || paramName.compareTo("XDI") == 0) {
                     this._imgInfoPresent = true;
                     return;
                  }

                  if (paramName.compareTo("XCS") == 0) {
                     this._xcsChunkSize = Integer.parseInt(value);
                     return;
                  }

                  if (paramName.compareTo("CS") == 0) {
                     this._chunkSize = Integer.parseInt(value);
                  }
               }
            }
         }
      }
   }
}
