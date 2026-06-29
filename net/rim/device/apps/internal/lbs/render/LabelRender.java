package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.lbs.LBSOptions;
import net.rim.device.apps.internal.lbs.Timing;
import net.rim.device.apps.internal.lbs.Transform;
import net.rim.device.apps.internal.lbs.maplet.DEntry;
import net.rim.device.apps.internal.lbs.maplet.DEntryCache;
import net.rim.device.apps.internal.lbs.maplet.Layer;
import net.rim.device.apps.internal.lbs.maplet.LayerDictionary;
import net.rim.device.apps.internal.lbs.maplet.MapRect;
import net.rim.device.apps.internal.lbs.maplet.Maplet;
import net.rim.device.apps.internal.lbs.maplet.MapletCache;
import net.rim.device.apps.internal.lbs.maplet.MarkerDictionary;
import net.rim.device.apps.internal.lbs.util.ByteBuffer;
import net.rim.device.internal.ui.BorderBitmap;

public final class LabelRender {
   int _scaleX;
   int _scaleY;
   int _width;
   int _height;
   Timing _timer = new Timing();
   PriorityComparator _priorityComparator = new PriorityComparator();
   private Transform _transform;
   static final char POI_PARKING = '\u0001';
   static final char POI_AUTOMOBILE_SERVICE = '\u0002';
   static final char POI_GAS_STATION = '\u0003';
   static final char POI_HOSPITAL_OR_CLINIC = '\u0004';
   static final char POI_EDUCATION_INSTITUTION = '\u0005';
   static final char POI_SHOPPING_AND_SERVICE = '\u0006';
   static final char POI_FINANCIAL_INSTITUTION_OR_ATM = '\u0007';
   static final char POI_HEALTH_SERVICES = '\b';
   static final char POI_REST_AREA = '\t';
   static final char POI_BORDER_CROSSING = '\n';
   static final char POI_ENTERTAINMENT = '\u000b';
   static final char POI_RESTAURANT_FOOD_SERVICE = '\f';
   static final char POI_SPORTS_CLUB_RECREATION_FACILITY = '\r';
   static final char POI_TRAVEL = '\u000e';
   static final char POI_GOVERNMENT_OFFICE = '\u000f';
   static final char POI_POLICE_STATION = '\u0010';
   static final char POI_POST_OFFICE = '\u0011';
   static final char POI_CONVENTION_EXHIBITION_CENTRE = '\u0012';
   static final char POI_LIBRARY = '\u0013';
   static final char POI_PUBLIC_TRANSPORTATION = '\u0014';
   static final char POI_BUILDING_AND_MONUMENT = '\u0015';
   static final char POI_BUSINESS = '\u0016';
   static final char POI_PLACE_OF_WORSHIP = '\u0017';
   static final char POI_BEACH = '\u0018';
   static final char POI_STADIUM_AND_ARENA = '\u0019';
   static final char POI_PARK = '\u001a';
   static final char POI_NATURAL_RESERVES = '\u001b';
   static final char POI_FIRE_STATION = '\u001c';
   static final char POI_ZOOM = '\u001d';
   static final char POI_CEMETERY = '\u001e';
   static final char POI_PRISON = '\u001f';
   static final char POI_WINERY = ' ';
   static final char POI_PUBLIC_PHONE = '!';
   static final char POI_TRAFFIC_SIGN = '"';
   static final char POI_D_LEVEL_INTERSTATE_HWY_SHIELD = ':';
   static final char POI_D_LEVEL_WATER_BODY = ';';
   static final char POI_D_LEVEL_PARK = '<';
   static final char POI_D_LEVEL_ADMIN_BOUNDARY = '=';
   static final char POI_D_LEVEL_STATE_NAME = '>';
   static final char POI_NAMED_PLACE = '?';
   static final String[] _beforeText = new String[]{
      ".............",
      "Autoroute",
      "Ave",
      "Boulevard",
      "Calle",
      "Callejon",
      "Camino",
      "Car",
      "Carretera",
      "Cercle",
      "Chemin",
      "Cote",
      "Cour",
      "Cours",
      "Crois",
      "Ile",
      "Impasse",
      "Montee",
      "Parc",
      "Paseo",
      "Pointe",
      "Promenade",
      "Puente",
      "Rang",
      "Rle",
      "Rue",
      "Sente",
      "Terrasse",
      "Voie",
      "Wynd"
   };
   static final String[] _afterText = new String[]{
      ".............",
      "Aby",
      "Acrs",
      "Ale",
      "Alley",
      "Aly",
      "Anx",
      "Arc",
      "Arpt",
      "Av",
      "Ave",
      "Bay",
      "Bch",
      "Bend",
      "Bg",
      "Blf",
      "Blfs",
      "Blvd",
      "Bnd",
      "Boul",
      "Br",
      "Brg",
      "Brk",
      "Brks",
      "Btm",
      "Byp",
      "Bypass",
      "Byu",
      "Byw",
      "Cape",
      "Car",
      "Carf",
      "Cer",
      "Ch",
      "Chse",
      "Cir",
      "Circt",
      "Cirs",
      "Cl",
      "Clb",
      "Clf",
      "Clfs",
      "Clse",
      "Cmn",
      "Cmns",
      "Com",
      "Conc",
      "Cor",
      "Cors",
      "Cote",
      "Cour",
      "Cove",
      "Cp",
      "Cpe",
      "Cres",
      "Crk",
      "Crnrs",
      "Crois",
      "Cross",
      "Crse",
      "Crst",
      "Crt",
      "Cswy",
      "Ct",
      "Ctr",
      "Cts",
      "Curs",
      "Curv",
      "Cv",
      "Cvs",
      "Cyn",
      "Dell",
      "Dl",
      "Dm",
      "Dr",
      "Drs",
      "Dv",
      "Dvrs",
      "Dwns",
      "E",
      "End",
      "Espl",
      "Est",
      "Estate",
      "Ests",
      "Expy",
      "Ext",
      "Exten",
      "Fall",
      "Farm",
      "Field",
      "Fld",
      "Flds",
      "Fls",
      "Flt",
      "Flts",
      "Forest",
      "Frd",
      "Frg",
      "Frk",
      "Frks",
      "Frst",
      "Fry",
      "Ft",
      "Fwy",
      "Gate",
      "Gdn",
      "Gdns",
      "Gld",
      "Glen",
      "Gln",
      "Glns",
      "Gr",
      "Green",
      "Grn",
      "Grns",
      "Grove",
      "Grv",
      "Grvs",
      "Gt",
      "Gtwy",
      "Harbr",
      "Hbr",
      "Hgld",
      "Hill",
      "Hl",
      "Hls",
      "Hollow",
      "Holw",
      "Ht",
      "Hth",
      "Hts",
      "Hvn",
      "Hwy",
      "Imp",
      "Inlt",
      "Is",
      "Island",
      "Isle",
      "Iss",
      "Jct",
      "Key",
      "Knl",
      "Knls",
      "Knoll",
      "Ky",
      "Kys",
      "Land",
      "Landng",
      "Lane",
      "Lck",
      "Lcks",
      "Ldg",
      "Lf",
      "Lgt",
      "Lgts",
      "Line",
      "Link",
      "Lk",
      "Lkout",
      "Lks",
      "Lmts",
      "Ln",
      "Lndg",
      "Loop",
      "Mall",
      "Manor",
      "Maze",
      "Mdw",
      "Mdws",
      "Meadow",
      "Mews",
      "Ml",
      "Mls",
      "Mnr",
      "Mnrs",
      "Mount",
      "Msn",
      "Mt",
      "Mte",
      "Mtn",
      "Mtns",
      "Mtwy",
      "Nck",
      "Opas",
      "Orch",
      "Oval",
      "Ovlk",
      "Parc",
      "Park",
      "Pass",
      "Path",
      "Pike",
      "Pines",
      "Pk",
      "Pkwy",
      "Pky",
      "Pl",
      "Place",
      "Plat",
      "Plaza",
      "Pln",
      "Plns",
      "Plz",
      "Pne",
      "Pnes",
      "Pr",
      "Prd",
      "Prom",
      "Prt",
      "Psge",
      "Pt",
      "Pts",
      "Ptway",
      "Pvt",
      "Quay",
      "Radl",
      "Ramp",
      "Rang",
      "Rd",
      "Rdg",
      "Rdgs",
      "Rds",
      "Rg",
      "Ridge",
      "Rise",
      "Riv",
      "Rle",
      "Rnch",
      "Row",
      "Rpds",
      "Rst",
      "Rte",
      "Rue",
      "Run",
      "Sdiv",
      "Sent",
      "Shl",
      "Shls",
      "Shr",
      "Shrs",
      "Skwy",
      "Smt",
      "Spg",
      "Spgs",
      "Spur",
      "Sq",
      "St",
      "Sta",
      "Stra",
      "Strm",
      "Sts",
      "Ter",
      "Terr",
      "Tline",
      "Towers",
      "Tpke",
      "Trail",
      "Trak",
      "Trce",
      "Trfy",
      "Trl",
      "Trlr",
      "Trnabt",
      "Trwy",
      "Tsse",
      "Tunl",
      "Un",
      "Upas",
      "Vale",
      "Via",
      "View",
      "Villge",
      "Vis",
      "Vista",
      "Vl",
      "Vlg",
      "Vls",
      "Vly",
      "Voie",
      "Vw",
      "Vws",
      "Walk",
      "Wall",
      "Way",
      "Ways",
      "Wharf",
      "Wl",
      "Wls",
      "Wood",
      "Wynd",
      "Xing",
      "Xrd",
      "Xrds",
      "Zzzz"
   };
   static final String[] _prefix = new String[]{"....", "E", "N", "NE", "NW", "O", "S", "SE", "SW", "W"};
   static final String[] _suffix = new String[]{"....", "E", "N", "NE", "NO", "NW", "O", "S", "SE", "SO", "SW", "W"};
   static final int _beforeTextLength = _beforeText.length;
   static final int _afterTextLength = _afterText.length;
   static final int _prefixLength = _prefix.length;
   static final int _suffixLength = _suffix.length;
   static final boolean DEBUG = false;
   static final int DRAW_MAJOR_CITIES = 0;
   static final int DRAW_CITY_DOTS = 1;
   static final int DRAW_CITY_LABELS = 2;
   static final int DRAW_CITY_DOTS_LABELS = 3;
   static BorderBitmap _hiwayBorder = new BorderBitmap(3, 8, 5, 6, Bitmap.getBitmapResource("hiway.png"));
   static Font _hiwayFont = FontRegistry.get("BBMillbankTall").getFont(1, 12, 0);
   static LayerDictionary _layerDictionary = LayerDictionary.getInstance();
   static LabelPath _labelPath = new LabelPath();
   private static final ByteBuffer _textBuffer = new ByteBuffer();

   protected final void genPathLabels(
      LabelPath[] labelPaths,
      Maplet maplet,
      MapRect rect,
      byte[] labelEntry,
      DEntry partsDEntry,
      int zoom,
      int rotation,
      int priority,
      MapRect orgRect,
      boolean marker,
      int paddingShield
   ) {
      int[] x = partsDEntry._x;
      int[] y = partsDEntry._y;
      int[] parts = partsDEntry._startpoints;
      int labelPos = 0;
      int labelStart = 0;
      int labelEnd = 0;
      int markerStart = 0;
      int markerMiddle = 0;
      byte markerEnd = 0;
      int markerExtrFlag = 0;
      String markerInfo = "";
      boolean europeanHwyV2 = false;
      boolean europeanHwyV1 = false;
      int partCount = parts == null ? 1 : parts.length - 1;

      for (int iPart = 0; iPart < partCount; iPart++) {
         if (marker) {
            europeanHwyV2 = false;
            europeanHwyV1 = false;
            markerStart = labelEntry[labelPos++] & 255;
            markerMiddle = (labelEntry[labelPos++] & 255 | (labelEntry[labelPos++] & 255) << 8) & 65535;
            markerEnd = labelEntry[labelPos++];
            int newHwyInfo = labelEntry[labelPos] & 255;
            if (newHwyInfo != 0) {
               markerExtrFlag = (labelEntry[labelPos] & 255) >> 7;
               if (markerExtrFlag == 0) {
                  int startIdx = labelPos;
                  int endIdx = startIdx;
                  ByteBuffer buffer = new ByteBuffer();

                  do {
                     endIdx++;
                  } while ((labelEntry[labelPos++] & 255) != 0);

                  buffer.append(labelEntry, startIdx, endIdx - startIdx);
                  markerInfo = buffer.toString();
                  europeanHwyV1 = true;
                  europeanHwyV2 = false;
               } else {
                  int secBit = (labelEntry[labelPos] & 64) << 2 & 0xFF;
                  int sDicIdx = ((labelEntry[labelPos++] & 255) << 2 | (labelEntry[labelPos] & 255) >> 6) & 0xFF;
                  int eDicIdx = labelEntry[labelPos++] & 31;
                  String start = "";
                  String end = "";
                  sDicIdx += secBit;
                  if (sDicIdx >= 0 && sDicIdx < MarkerDictionary.MARKER_EUROPEAN_TYPE_ARRAY.length) {
                     start = MarkerDictionary.MARKER_EUROPEAN_TYPE_ARRAY[sDicIdx];
                     europeanHwyV2 = true;
                  } else {
                     europeanHwyV2 = false;
                  }

                  if (eDicIdx >= 0 && eDicIdx < MarkerDictionary.MARKER_END_ARRAY.length) {
                     end = MarkerDictionary.MARKER_END_ARRAY[eDicIdx];
                     europeanHwyV2 = true;
                  } else {
                     europeanHwyV2 = false;
                  }

                  if (europeanHwyV2) {
                     markerStart = sDicIdx;
                     markerInfo = (start + markerMiddle + end).trim();
                     europeanHwyV1 = false;

                     while (labelEntry[labelPos++] != 0) {
                     }
                  }
               }
            }

            if (newHwyInfo == 0 || !europeanHwyV1 && !europeanHwyV2) {
               String ending = markerEnd >= 0 && markerEnd < MarkerDictionary.MARKER_END_ARRAY.length ? MarkerDictionary.MARKER_END_ARRAY[markerEnd] : "";
               markerInfo = (markerMiddle + ending).trim();
               europeanHwyV2 = false;
               europeanHwyV1 = false;

               while (labelEntry[labelPos++] != 0) {
               }
            }

            if (markerStart == 0 && zoom > 5 || markerMiddle == 0 && !europeanHwyV1) {
               continue;
            }
         } else {
            labelStart = labelPos + 4;
            labelEnd = labelStart;
            if (labelEnd >= labelEntry.length) {
               return;
            }

            while (labelEntry[labelEnd] != 0) {
               labelEnd++;
            }

            labelPos = labelEnd;
            labelPos++;
            if (labelStart == labelEnd) {
               continue;
            }

            synchronized (_textBuffer) {
               _textBuffer.setLength(0);
               _textBuffer.append(labelEntry, labelStart, labelEnd - labelStart);
               addLabelInfo(_textBuffer, labelEntry, labelStart - 4);
               _textBuffer.append((byte)0);
               byte[] labelArray = _textBuffer.toArray();
               _labelPath._labelData = labelArray;
            }
         }

         _labelPath._priority = priority;
         int start = parts == null ? 0 : parts[iPart];
         int end = parts == null ? x.length : parts[iPart + 1];
         if (_labelPath.clip(x, y, start, end, rect, this._scaleX, this._scaleY)) {
            _labelPath._maplet = maplet;
            _labelPath._path = partsDEntry;
            if (marker) {
               _labelPath._pathMarker = new PathMarker(this._width, this._height);
               if (markerStart >= MarkerDictionary.MARKER_START_WEIGHT.length) {
                  markerStart -= 112;
               }

               _labelPath._pathMarker.setMarker(europeanHwyV2, markerStart, markerInfo, maplet, orgRect, zoom, rotation);
               _labelPath._textLength = _labelPath._pathMarker._rect.width / 2;
            } else {
               _labelPath._textIndex = 0;
               _labelPath._textLength = TextOnPath.getTextLength(_labelPath._labelData, _labelPath._textIndex);
            }

            _labelPath.chooseDir(rotation);
            _labelPath.convertEndpointsToWorld(maplet);
            int index = Arrays.binarySearch(labelPaths, _labelPath, _labelPath, 0, labelPaths.length);
            if (index < 0) {
               Arrays.insertAt(labelPaths, _labelPath, -index - 1);
            } else {
               labelPaths[index] = LabelPath.add(labelPaths[index], _labelPath, zoom);
            }

            _labelPath = new LabelPath();
         }
      }
   }

   private final void genPathLabels(
      RenderThread renderThread, LabelPath[] labelPaths, Maplet maplet, int layerId, MapRect rect, int zoom, int rotation, int priority, int paddingShield
   ) {
      boolean marker = _layerDictionary.isMarkerLayer(layerId);
      LabelPathCallback._transform.update(rect, zoom, rotation);
      LabelPathCallback._transform.setMaplet(maplet);
      Layer layer = maplet.getLayerByID(layerId);
      if (layer != null) {
         MapRect mapletRect = new MapRect(rect);
         maplet.convertToMapletUnits(mapletRect);
         int dentryCount = layer.getLayerAttribute((byte)7);

         for (int i = 0; i < dentryCount; i++) {
            short[] header = layer.getDEntryHeader(i);
            if (mapletRect.intersects(header)) {
               byte[] labelEntry = layer.getDEntry(i);
               if (labelEntry == null) {
                  renderThread.dataMissing();
               } else {
                  Layer pathsLayer = null;
                  if (zoom >= 0 && zoom <= 4) {
                     pathsLayer = maplet.getLayerByID(layerId - 7);
                  } else if (zoom >= 5 && zoom <= 8) {
                     pathsLayer = maplet.getLayerByID(layerId - 25);
                  } else if (zoom >= 9 && zoom <= 13) {
                     pathsLayer = maplet.getLayerByID(layerId - 25);
                  }

                  if (pathsLayer != null && pathsLayer.getDEntryCount() > i) {
                     short[] pathsHeader = pathsLayer.getDEntryHeader(i);
                     if (mapletRect.intersects(pathsHeader)) {
                        byte[] pathsData = pathsLayer.getDEntry(i);
                        if (pathsData != null) {
                           DEntry paths = DEntryCache.get(maplet, pathsLayer, pathsHeader, pathsData);
                           this.genPathLabels(labelPaths, maplet, mapletRect, labelEntry, paths, zoom, rotation, priority, rect, marker, paddingShield);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static final String toLowerCase(String label, int type) {
      if (label == null) {
         return "";
      }

      if (label.equals("")) {
         return "";
      }

      String labelUpperCase = label.toUpperCase();
      if (!label.equals(labelUpperCase)) {
         return label;
      }

      int length = label.length();
      label = label.toLowerCase();
      String letter = label.substring(0, 1);
      label = letter.toUpperCase() + label.substring(1, length);
      int nextSpace = nextDelimiter(label, " ", 0);
      int nextDash = nextDelimiter(label, "-", 0);
      int nextApostrophe = nextDelimiter(label, "'", 0);
      int start = -1;
      int end = -1;
      if (nextSpace != 1024 && nextSpace < nextDash && nextSpace < nextApostrophe) {
         start = nextSpace;
         nextSpace = nextDelimiter(label, " ", start + 2);
      } else if (nextDash != 1024 && nextDash < nextSpace && nextDash < nextApostrophe) {
         start = nextDash;
         nextDash = nextDelimiter(label, "-", start + 2);
      } else if (nextApostrophe != 1024) {
         start = nextApostrophe;
         nextApostrophe = nextDelimiter(label, "'", start + 2);
      }

      while (start != -1) {
         int delimiter;
         if (nextSpace != 1024 && nextSpace < nextDash && nextSpace < nextApostrophe) {
            end = nextSpace;
            nextSpace = nextDelimiter(label, " ", end + 2);
            delimiter = 0;
         } else if (nextDash != 1024 && nextDash < nextSpace && nextDash < nextApostrophe) {
            end = nextDash;
            nextDash = nextDelimiter(label, "-", end + 2);
            delimiter = 1;
         } else if (nextApostrophe != 1024) {
            end = nextApostrophe;
            nextApostrophe = nextDelimiter(label, "'", end + 2);
            delimiter = 2;
         } else {
            end = length;
            delimiter = -1;
         }

         letter = label.substring(start + 1, Math.min(start + 2, length));
         if (letter.equals("")) {
            break;
         }

         String word = label.substring(start + 1, Math.min(end, length));
         if (capitalizeWord(word, delimiter)) {
            label = label.substring(0, start + 1) + letter.toUpperCase() + label.substring(start + 2, length);
         }

         if (end == length) {
            start = -1;
         } else {
            start = end;
         }
      }

      if (type == 0 && label.substring(Math.max(0, length - 2), length - 1).equals(" ")) {
         letter = label.substring(length - 1, length);
         label = label.substring(0, length - 1) + letter.toUpperCase();
      }

      if (type == 2) {
         if (label.endsWith("Usa")) {
            label = label.substring(0, label.length() - 3) + "USA";
         } else if (label.endsWith("Uk")) {
            label = label.substring(0, label.length() - 2) + "UK";
         }

         if (label.endsWith("USA") || label.endsWith("Canada")) {
            int endOffset = label.lastIndexOf(44);
            int startOffset = label.lastIndexOf(44, endOffset - 1);
            label = label.substring(0, startOffset) + label.substring(startOffset, endOffset).toUpperCase() + label.substring(endOffset, label.length());
         }
      }

      return label;
   }

   private static final int nextDelimiter(String label, String character, int offset) {
      int nextDelimiter = label.indexOf(character, offset);
      if (nextDelimiter == -1) {
         nextDelimiter = 1024;
      }

      return nextDelimiter;
   }

   private static final boolean capitalizeWord(String word, int delimiter) {
      int length = word.length();
      if (delimiter != 0 && delimiter != 1) {
         if (delimiter == 2 && length == 1 && word.equals("s")) {
            return false;
         }
      } else {
         switch (length) {
            case 0:
               break;
            case 1:
            default:
               if (word.equals("d") || word.equals("l") || word.equals("a")) {
                  return false;
               }
               break;
            case 2:
               if (word.equals("of") || word.equals("de") || word.equals("le") || word.equals("du") || word.equals("la") || word.equals("et")) {
                  return false;
               }
               break;
            case 3:
               if (word.equals("des") || word.equals("les") || word.equals("the") || word.equals("and")) {
                  return false;
               }
         }
      }

      return true;
   }

   public static final void addLabelInfo(ByteBuffer fullLabel, byte[] labelEntry, int index) {
      int labelData = labelEntry[index] & 255 | (labelEntry[index + 1] & 255) << 8 | (labelEntry[index + 2] & 255) << 16 | (labelEntry[index + 3] & 255) << 24;
      int beforeAfterIndex = labelData & 4095;
      int beforeAfterFlag = (labelData & 12288) >> 12;
      int prefixIndex = (labelData & 245760) >> 14;
      int suffixIndex = (labelData & 3932160) >> 18;
      if (beforeAfterIndex != 0) {
         beforeAfterIndex--;
      }

      if (prefixIndex != 0) {
         prefixIndex--;
      }

      if (suffixIndex != 0) {
         suffixIndex--;
      }

      synchronized (fullLabel) {
         switch (beforeAfterFlag) {
            case -1:
               break;
            case 0:
            default:
               if (prefixIndex > 0 && prefixIndex < _prefixLength) {
                  fullLabel.insert(0, _prefix[prefixIndex]);
                  fullLabel.insert(_prefix[prefixIndex].length(), " ");
               }

               if (beforeAfterIndex > 0 && beforeAfterIndex < _beforeTextLength) {
                  fullLabel.append(_beforeText[beforeAfterIndex]);
               }

               if (suffixIndex > 0 && suffixIndex < _suffixLength) {
                  fullLabel.append(" ");
                  fullLabel.append(_suffix[suffixIndex]);
               }
               break;
            case 1:
               if (beforeAfterIndex > 0 && beforeAfterIndex < _beforeTextLength) {
                  fullLabel.insert(0, _beforeText[beforeAfterIndex]);
                  fullLabel.insert(_beforeText[beforeAfterIndex].length(), " ");
               }

               if (prefixIndex > 0 && prefixIndex < _prefixLength) {
                  fullLabel.append(_prefix[prefixIndex]);
                  fullLabel.append(" ");
               }

               if (suffixIndex > 0 && suffixIndex < _suffixLength) {
                  fullLabel.append(" ");
                  fullLabel.append(_suffix[suffixIndex]);
               }
               break;
            case 2:
               if (prefixIndex > 0 && prefixIndex < _prefixLength) {
                  fullLabel.insert(0, _prefix[prefixIndex]);
                  fullLabel.insert(_prefix[prefixIndex].length(), " ");
               }

               if (beforeAfterIndex > 0 && beforeAfterIndex < _afterTextLength) {
                  fullLabel.append(_afterText[beforeAfterIndex]);
               }

               if (suffixIndex > 0 && suffixIndex < _suffixLength) {
                  fullLabel.append(" ");
                  fullLabel.append(_suffix[suffixIndex]);
               }
               break;
            case 3:
               if (prefixIndex > 0 && prefixIndex < _prefixLength) {
                  fullLabel.insert(0, _prefix[prefixIndex]);
                  fullLabel.insert(_prefix[prefixIndex].length(), " ");
               }

               if (beforeAfterIndex > 0 && beforeAfterIndex < _afterTextLength) {
                  fullLabel.append(" ");
                  fullLabel.append(_afterText[beforeAfterIndex]);
               }

               if (suffixIndex > 0 && suffixIndex < _suffixLength) {
                  fullLabel.append(" ");
                  fullLabel.append(_suffix[suffixIndex]);
               }
         }
      }
   }

   private final void drawPathLabels(LabelPath[] labelPaths, Graphics graphics, MapRect rect, int zoom, int rotation, int paddingShield, boolean tryAgain) {
      LabelPathCallback._transform.update(rect, zoom, rotation);
      int count = labelPaths.length;

      for (int i = 0; i < count; i++) {
         LabelPath labelPath = labelPaths[i].place();
         if (labelPath._priority > labelPaths[i]._priority) {
            labelPaths[i]._priority = labelPath._priority;
         }
      }

      Arrays.sort(labelPaths, 0, count, this._priorityComparator);
      boolean bSuccess = false;

      for (int i = 0; i < count; i++) {
         if (!tryAgain || labelPaths[i]._pathMarker != null && !labelPaths[i]._pathMarker._isDrawn) {
            LabelPath labelPath = labelPaths[i].place();
            labelPath = labelPath.center();
            if (labelPath != null) {
               LabelPathCallback._transform.setMaplet(labelPath._maplet);
               bSuccess = labelPath.render(graphics, rotation, paddingShield);
               if (!bSuccess) {
                  LabelPath nextPath = labelPaths[i];
                  LabelPath currentPath = null;

                  while (nextPath != null) {
                     if (nextPath != labelPath) {
                        currentPath = nextPath.center();
                        if (currentPath != null) {
                           LabelPathCallback._transform.setMaplet(currentPath._maplet);
                           bSuccess = currentPath.render(graphics, rotation, paddingShield);
                           if (bSuccess) {
                              break;
                           }
                        }
                     }

                     nextPath = nextPath._next;
                  }
               }
            }

            if (labelPaths[i]._pathMarker != null && bSuccess) {
               labelPaths[i]._pathMarker._isDrawn = true;
            }
         }
      }
   }

   private final void genLabels(int[] priorities, SimpleLabel[] labels, Maplet maplet, int layerId, MapRect rect, int zoom, int rotation, int paddingTown) {
      if (_layerDictionary.isVisible(layerId, zoom)) {
         Layer layer = maplet.getLayerByID(layerId);
         if (layer != null) {
            int[] matrix = maplet.getGraphicsMatrix(rect, zoom, rotation);
            MapRect mapletRect = new MapRect();
            maplet.convertToMapletUnits(mapletRect, rect);
            int dentryCount = layer.getLayerAttribute((byte)7);

            for (int i = 0; i < dentryCount; i++) {
               short[] header = layer.getDEntryHeader(i);
               if (mapletRect.intersects(header)) {
                  byte[] labelEntry = layer.getDEntry(i);
                  if (labelEntry != null) {
                     int blx = DEntry.getBLX(header);
                     int bly = DEntry.getBLY(header);
                     int end = labelEntry.length;
                     int pos = 0;

                     while (pos + 9 < end) {
                        int mx = blx + ((labelEntry[pos++] & 255) + (labelEntry[pos++] << 8) >> 1);
                        if (mx >= mapletRect._left && mx <= mapletRect._right) {
                           int my = bly + ((labelEntry[pos++] & 255) + (labelEntry[pos++] << 8) >> 1);
                           if (my >= mapletRect._bottom && my <= mapletRect._top) {
                              int attrib = (labelEntry[pos++] & 255)
                                 + ((labelEntry[pos++] & 255) << 8)
                                 + ((labelEntry[pos++] & 255) << 16)
                                 + ((labelEntry[pos++] & 255) << 24);
                              int priority = attrib & 1023;
                              priority = 1023 - priority;
                              int index = Arrays.binarySearch(priorities, priority);
                              if (index < 0) {
                                 index = -index - 1;
                              }

                              int fac_type = attrib >> 10 & 63;
                              if (zoom < 12 && fac_type == 63
                                 || zoom >= 14 && fac_type == 61
                                 || zoom >= 11 && zoom < 14 && fac_type != 58
                                 || zoom < 9 && fac_type == 9
                                 || zoom < 5 && fac_type == 6
                                 || zoom < 3 && fac_type == 1
                                 || zoom < 3 && fac_type == 5
                                 || zoom < 5 && fac_type == 4) {
                                 int start = pos;

                                 while (labelEntry[pos++] != 0) {
                                 }

                                 String text;
                                 if (pos - start - 1 < 1) {
                                    if (fac_type != 9 && fac_type != 4 && fac_type != 5 && fac_type != 1 && fac_type != 6) {
                                       continue;
                                    }

                                    text = "";
                                 } else {
                                    text = new String(labelEntry, start, pos - start - 1);
                                 }

                                 int x = mx * matrix[0] + my * matrix[1] + matrix[2] >> 16;
                                 int y = mx * matrix[3] + my * matrix[4] + matrix[5] >> 16;
                                 if (fac_type == 4) {
                                    priority = 1018;
                                 } else if (fac_type == 1) {
                                    priority = 1019;
                                 } else if (fac_type == 6) {
                                    priority = 1020;
                                 } else if (fac_type == 9) {
                                    priority = 1021;
                                 } else if (fac_type == 5) {
                                    priority = 1022;
                                 }

                                 Arrays.insertAt(priorities, priority, index);
                                 Arrays.insertAt(labels, new SimpleLabel(fac_type, text, x, y, priority, zoom, paddingTown), index);
                              } else {
                                 while (labelEntry[pos++] != 0) {
                                 }
                              }
                           } else {
                              pos += 4;

                              while (labelEntry[pos++] != 0) {
                              }
                           }
                        } else {
                           pos += 6;

                           while (labelEntry[pos++] != 0) {
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private final void drawLabels(SimpleLabel[] labels, Graphics graphics, int zoom, int drawType) {
      graphics.setMatrix(65536, 0, 0, 0, 65536, 0, 0, 0, 65536);
      int count = 0;
      count = labels.length;

      for (int i = 0; i < count; i++) {
         labels[i].render(graphics, drawType);
      }
   }

   public final void setTransform(Transform transform) {
      this._transform = transform;
   }

   public final void render(RenderThread renderThread, Graphics graphics, MapRect rect, int zoom, int rotation, int paddingTown, int paddingShield) {
      this._timer.startTimer(2);
      graphics.setMatrix(65536, 0, 0, 0, 65536, 0, 0, 0, 65536);
      boolean drawStyleTemp = graphics.isDrawingStyleSet(2);
      graphics.setDrawingStyle(2, false);
      LabelPathCallback._collisionArray.clear();
      int level = Maplet.getMapletLevel(zoom);
      int mapletScale = Maplet.getMapletZoomScale(level);
      this._scaleY = 65536 << mapletScale >> zoom;
      if (LBSOptions.SPHERICAL_CORRECTION) {
         int sphericalCorrection = Transform.getSphericalCorrection((rect._bottom + rect._top) / 2, zoom);
         this._scaleX = Fixed32.mul(this._scaleY, Fixed32.div(sphericalCorrection, 65536));
      } else {
         this._scaleX = this._scaleY;
      }

      this._width = this._transform._cropView.width() >> zoom;
      this._height = this._transform._cropView.height() >> zoom;
      LabelPathCallback._transform.setYOffset(this._transform.getYOffset());
      LabelPath[] labelPaths = new LabelPath[0];
      SimpleLabel[] labels = new SimpleLabel[0];
      int[] priorities = new int[0];
      Maplet[] maplets = MapletCache.getInstance().getMaplets(rect, zoom);
      int[] layerOrder = _layerDictionary.getLabelLayers(zoom);
      int priority = 0;

      for (int layer = 0; layer < layerOrder.length; layer++) {
         int layerId = layerOrder[layer];
         int pathsId = _layerDictionary.getLabelPathLayer(layerId);

         for (int maplet = 0; maplet < maplets.length; maplet++) {
            if (renderThread.isPending()) {
               return;
            }

            if (pathsId == -1) {
               this.genLabels(priorities, labels, maplets[maplet], layerOrder[layer], rect, zoom, rotation, paddingTown);
            } else {
               this.genPathLabels(renderThread, labelPaths, maplets[maplet], layerId, this._transform._cropView, zoom, rotation, priority, paddingShield);
            }
         }

         priority++;
      }

      this.dumpSplices(labelPaths);
      this._timer.endTimer(2);
      this._timer.startTimer(3);
      this.drawLabels(labels, graphics, zoom, 0);
      if (!renderThread.isPending()) {
         this.drawLabels(labels, graphics, zoom, 1);
         if (!renderThread.isPending()) {
            this.drawPathLabels(labelPaths, graphics, rect, zoom, rotation, paddingShield, false);
            if (!renderThread.isPending()) {
               this.drawLabels(labels, graphics, zoom, 2);
               if (!renderThread.isPending()) {
                  this.drawPathLabels(labelPaths, graphics, rect, zoom, rotation, paddingShield, true);
                  if (!renderThread.isPending()) {
                     this.drawLabels(labels, graphics, zoom, 3);
                     if (!renderThread.isPending()) {
                        graphics.setDrawingStyle(2, drawStyleTemp);
                        this._timer.endTimer(3);
                        this._timer.dataRendered();
                     }
                  }
               }
            }
         }
      }
   }

   final void dumpSplices(LabelPath[] labelPaths) {
   }
}
