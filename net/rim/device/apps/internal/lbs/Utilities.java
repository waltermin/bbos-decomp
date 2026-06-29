package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.utility.URIEncoder;

public final class Utilities {
   private static int[] _cos = new int[]{
      65536,
      65526,
      65496,
      65446,
      65376,
      65287,
      65177,
      65048,
      64898,
      64729,
      64540,
      64332,
      64104,
      63856,
      63589,
      63303,
      62997,
      62672,
      62328,
      61966,
      61584,
      61183,
      60764,
      60326,
      59870,
      59396,
      58903,
      58393,
      57865,
      57319,
      56756,
      56175,
      55578,
      54963,
      54332,
      53684,
      53020,
      52339,
      51643,
      50931,
      50203,
      49461,
      48703,
      47930,
      47143,
      46341,
      45525,
      44695,
      43852,
      42995,
      42126,
      41243,
      40348,
      39441,
      38521,
      37590,
      36647,
      35693,
      34729,
      33754,
      32768,
      31772,
      30767,
      29753,
      28729,
      27697,
      26656,
      25607,
      24550,
      23486,
      22415,
      21336,
      20252,
      19161,
      18064,
      16962,
      15855,
      14742,
      13626,
      12505,
      11380,
      10252,
      9121,
      7987,
      6850,
      5712,
      4572,
      3430,
      2287,
      1144,
      0,
      -804651004,
      56838,
      56874,
      56878,
      56926,
      -804651003,
      56848,
      56875,
      56879,
      56923,
      56849,
      -804651004,
      56851,
      56872,
      56877,
      56924,
      -804650992,
      56865,
      56880,
      56885,
      56927,
      56866,
      56881,
      56886,
      56867,
      56882,
      56887,
      56868,
      56883,
      56888,
      56869,
      56884,
      56889,
      51,
      -804650980,
      56897,
      56890,
      56894,
      56898,
      56891,
      56895,
      56899,
      56892,
      56896,
      56900,
      56893,
      56901,
      56893,
      56901,
      56908,
      56907,
      56906,
      56905,
      56904,
      56918,
      56917,
      56916,
      56915,
      56921,
      56920,
      56919,
      56843,
      56873,
      -804651004,
      56909,
      56910,
      56911,
      56847,
      -804651004,
      5000,
      50000,
      500000,
      5000000,
      -804651000,
      -24,
      -3,
      -3,
      18,
      18,
      -3,
      -3,
      -24,
      712179968,
      712179968,
      16806977,
      -2104615050,
      712376576,
      1870004480,
      16803179,
      1701539702,
      477389633,
      1870004480,
      1849779563,
      56711012,
      1870004480,
      1883333995,
      -1581068944,
      6425430,
      1802466817,
      185683045,
      -977993472,
      -977993472,
      1527120072,
      1812332550,
      100689154,
      1399128684,
      2343819,
      1694657542,
      600017747,
      2031722056,
      3896393,
      638058504,
      134250105,
      119610152,
      205588480,
      1091043425,
      1706652771,
      319578963,
      1665206272,
      1415952756,
      6634344,
      1952661768,
      1096246713,
      134247238,
      57961537,
      134219381,
      -848269247,
      1091043522,
      -1026723728,
      1668506948,
      421357938,
      1091043443,
      -1026723728,
      1091043455,
      -1026723728,
      765355936,
      1916864512,
      134248910,
      134247489,
      409797442,
      54659072,
      1939871853,
      503407,
      -1569766904,
      134247268,
      2120524354,
      -1388181504,
      -1574934677,
      426550884,
      54724608,
      1124597881,
      -2072975508,
      134247557,
      134254147,
      1938472003,
      1698957312,
      8529428,
      342180872,
      8475944,
      342180872,
      134235209,
      1634100548,
      7629941,
      -781630456,
      7543924,
      2010006536,
      192504140,
      1158152307,
      -1065804435,
      1984235520,
      1804160138,
      541460480,
      -1402680723,
      1802465090,
      -1136261120,
      1346832384,
      -1031189421,
      1816594432,
      1092575855,
      1634234476,
      -599324672,
      1225261250,
      134276461,
      1094415433,
      1225261172,
      -1566164108,
      1275592820,
      1963157264,
      1275592710,
      1963157264,
      7612422,
      51465224,
      134219381,
      1963135308,
      7612422,
      1650543624,
      1275592843,
      1284203105,
      1930131809,
      1632372736,
      290491234,
      2036419688,
      1275592715,
      1191934305,
      743362336,
      134247269,
      1717658956,
      1745049535,
      1275592916,
      134267529,
      1103268172,
      134265460,
      1153599820,
      7541861,
      1697402376,
      -682752000,
      10448720,
      -1193849336,
      1325924416,
      56711026,
      134224198,
      1484218706,
      8342605,
      2002866696,
      1376256127,
      12743791,
      -371895800,
      7628977,
      1710707208,
      -145618944,
      1280137317,
      134265627,
      134234707,
      319578963,
      1884489728,
      526191464,
      1959860547,
      1393033244,
      1130589817,
      187332112,
      2035484672,
      743334766,
      1393033317,
      1331916409,
      12806754,
      1853444872,
      1784827747,
      134247363,
      1668184403,
      -1016438193,
      7643814,
      1853444872,
      1930122851,
      1393033244,
      -1503433095,
      1394394052,
      1393033452,
      2024963716,
      -1957492736,
      -1991498813,
      1393033410,
      -1172061301,
      -330102784,
      1918109696,
      526543789,
      1808614252,
      477389633,
      2035550208,
      134243696,
      134272852,
      11130708,
      1145656584,
      1381304320
   };

   private Utilities() {
   }

   public static final int cos(int degrees) {
      if (degrees < 91) {
         return _cos[degrees];
      } else if (degrees < 181) {
         return -_cos[180 - degrees];
      } else {
         return degrees < 271 ? -_cos[degrees - 180] : _cos[360 - degrees];
      }
   }

   public static final int sin(int degrees) {
      if (degrees < 91) {
         return _cos[90 - degrees];
      } else if (degrees < 181) {
         return _cos[degrees - 90];
      } else {
         return degrees < 271 ? -_cos[270 - degrees] : -_cos[degrees - 270];
      }
   }

   public static final int getRotation(int dx, int dy) {
      dy = -dy;
      if (dx == 0) {
         return dy > 0 ? 9 : 27;
      }

      if (dy == 0) {
         return dx > 0 ? 0 : 18;
      }

      int arctan = Math.abs(dy) * 1000 / Math.abs(dx);
      int quadrant = (dx < 0 ? 1 : 0) + (dy < 0 ? 2 : 0);
      switch (quadrant) {
         case -1:
            return 0;
         case 0:
         default:
            if (arctan < 87) {
               return 0;
            } else if (arctan < 267) {
               return 1;
            } else if (arctan < 466) {
               return 2;
            } else if (arctan < 700) {
               return 3;
            } else if (arctan < 1000) {
               return 4;
            } else if (arctan < 1428) {
               return 5;
            } else if (arctan < 2144) {
               return 6;
            } else if (arctan < 3732) {
               return 7;
            } else {
               if (arctan < 11430) {
                  return 8;
               }

               return 9;
            }
         case 1:
            if (arctan < 87) {
               return 18;
            } else if (arctan < 267) {
               return 17;
            } else if (arctan < 466) {
               return 16;
            } else if (arctan < 700) {
               return 15;
            } else if (arctan < 1000) {
               return 14;
            } else if (arctan < 1428) {
               return 13;
            } else if (arctan < 2144) {
               return 12;
            } else if (arctan < 3732) {
               return 11;
            } else {
               if (arctan < 11430) {
                  return 10;
               }

               return 9;
            }
         case 2:
            if (arctan < 87) {
               return 0;
            } else if (arctan < 267) {
               return 35;
            } else if (arctan < 466) {
               return 34;
            } else if (arctan < 700) {
               return 33;
            } else if (arctan < 1000) {
               return 32;
            } else if (arctan < 1428) {
               return 31;
            } else if (arctan < 2144) {
               return 30;
            } else if (arctan < 3732) {
               return 29;
            } else {
               if (arctan < 11430) {
                  return 28;
               }

               return 27;
            }
         case 3:
            if (arctan < 87) {
               return 18;
            } else if (arctan < 267) {
               return 19;
            } else if (arctan < 466) {
               return 20;
            } else if (arctan < 700) {
               return 21;
            } else if (arctan < 1000) {
               return 22;
            } else if (arctan < 1428) {
               return 23;
            } else if (arctan < 2144) {
               return 24;
            } else if (arctan < 3732) {
               return 25;
            } else {
               return arctan < 11430 ? 26 : 27;
            }
      }
   }

   public static final String createLbsUrl(Location location) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   public static final String createLbsUrl(
      int latitude,
      int longitude,
      int zoom,
      String label,
      String description,
      String address,
      String city,
      String region,
      String country,
      String postalCode,
      String phone,
      String fax,
      String webPage,
      String email,
      String categories,
      String rating
   ) {
      StringBuffer url = (StringBuffer)(new Object());
      synchronized (url) {
         url.append("http://maps.BlackBerry.com?lat=");
         appendDegrees(url, latitude / 4681608360884174848L);
         url.append("&lon=");
         appendDegrees(url, longitude / 4681608360884174848L);
         url.append("&z=");
         url.append(zoom);
         StringBuffer temp = (StringBuffer)(new Object());
         if (label != null && !label.equals("")) {
            URIEncoder.encode(temp, label, "UTF-8", true);
            url.append("&label=").append(temp);
            temp.setLength(0);
         }

         if (description != null && !description.equals("")) {
            URIEncoder.encode(temp, description, "UTF-8", true);
            url.append("&desc=").append(temp);
            temp.setLength(0);
         }

         if (address != null && !address.equals("")) {
            URIEncoder.encode(temp, address, "UTF-8", true);
            url.append("&address=").append(temp);
            temp.setLength(0);
         }

         if (city != null && !city.equals("")) {
            URIEncoder.encode(temp, city, "UTF-8", true);
            url.append("&city=").append(temp);
            temp.setLength(0);
         }

         if (region != null && !region.equals("")) {
            URIEncoder.encode(temp, region, "UTF-8", true);
            url.append("&region=").append(temp);
            temp.setLength(0);
         }

         if (country != null && !country.equals("")) {
            URIEncoder.encode(temp, country, "UTF-8", true);
            url.append("&country=").append(temp);
            temp.setLength(0);
         }

         if (postalCode != null && !postalCode.equals("")) {
            URIEncoder.encode(temp, postalCode, "UTF-8", true);
            url.append("&postalCode=").append(temp);
            temp.setLength(0);
         }

         if (phone != null && !phone.equals("")) {
            URIEncoder.encode(temp, phone, "UTF-8", true);
            url.append("&phone=").append(temp);
            temp.setLength(0);
         }

         if (fax != null && !fax.equals("")) {
            URIEncoder.encode(temp, fax, "UTF-8", true);
            url.append("&fax=").append(temp);
            temp.setLength(0);
         }

         if (webPage != null && !webPage.equals("")) {
            URIEncoder.encode(temp, webPage, "UTF-8", true);
            url.append("&url=").append(temp);
            temp.setLength(0);
         }

         if (email != null && !email.equals("")) {
            URIEncoder.encode(temp, email, "UTF-8", true);
            url.append("&email=").append(temp);
            temp.setLength(0);
         }

         if (categories != null && !categories.equals("")) {
            URIEncoder.encode(temp, categories, "UTF-8", true);
            url.append("&categories=").append(temp);
            temp.setLength(0);
         }

         if (rating != null && !rating.equals("")) {
            URIEncoder.encode(temp, rating, "UTF-8", true);
            url.append("&rating=").append(temp);
            temp.setLength(0);
         }

         url.append("\n");
      }

      return url.toString();
   }

   public static final String createDirectionsUrl(
      String startAddress, int startLatitude, int startLongitude, String endAddress, int endLatitude, int endLongitude
   ) {
      StringBuffer url = (StringBuffer)(new Object());
      synchronized (url) {
         url.append("http://maps.BlackBerry.com?startLat=");
         appendDegrees(url, startLatitude / 4681608360884174848L);
         url.append("&startLon=");
         appendDegrees(url, startLongitude / 4681608360884174848L);
         StringBuffer temp = (StringBuffer)(new Object());
         if (startAddress != null && startAddress.length() > 0) {
            URIEncoder.encode(temp, startAddress, "UTF-8", true);
            url.append("&startAddress=").append(temp);
            temp.setLength(0);
         }

         url.append("&endLat=");
         appendDegrees(url, endLatitude / 4681608360884174848L);
         url.append("&endLon=");
         appendDegrees(url, endLongitude / 4681608360884174848L);
         if (endAddress != null && endAddress.length() > 0) {
            URIEncoder.encode(temp, endAddress, "UTF-8", true);
            url.append("&endAddress=").append(temp);
            temp.setLength(0);
         }

         url.append("\n");
      }

      return url.toString();
   }

   private static final void appendDegrees(StringBuffer sb, double degrees) {
      int whole = (int)degrees;
      int fraction = Math.abs((int)((degrees - whole) * 4681608360884174848L));
      if (whole == 0 && degrees < 0L) {
         sb.append('-');
      }

      sb.append(whole);
      sb.append('.');
      if (fraction <= 0) {
         sb.append("0");
      } else {
         for (int magnitude = 10000; magnitude / fraction > 0; magnitude /= 10) {
            sb.append("0");
         }

         sb.append(fraction);
      }
   }

   public static final int drawMultiLineListItem(Graphics graphics, Font font, String text, int x, int y, int width, boolean draw) {
      if (graphics != null) {
         graphics.setFont(font);
      }

      String line = text;
      String displayLine = text;
      int endIndex = text.length();
      int height = font.getHeight();
      int totalHeight = height;
      int offset = 0;
      boolean abnormalBreak = false;

      while (line.length() > 0) {
         for (; font.getAdvance(line) > width; line = line.substring(0, endIndex)) {
            int lastSpace = line.lastIndexOf(32, endIndex);
            int lastDash = line.lastIndexOf(45, endIndex);
            int lastSlash = line.lastIndexOf(47, endIndex);
            int lastComma = line.lastIndexOf(44, endIndex);
            endIndex = Math.max(lastSpace, lastDash);
            endIndex = Math.max(endIndex, lastSlash);
            endIndex = Math.max(endIndex, lastComma);
            if (endIndex == -1) {
               abnormalBreak = true;
               endIndex = line.length() - 1;
               displayLine = line.substring(0, Math.min(endIndex, line.length()));
            } else {
               abnormalBreak = false;
               displayLine = line.substring(0, Math.min(endIndex + 1, line.length()));
            }
         }

         if (draw) {
            graphics.drawText(displayLine, x, y);
         }

         if (abnormalBreak) {
            offset = Math.min(offset + endIndex, text.length());
         } else {
            offset = Math.min(offset + endIndex + 1, text.length());
         }

         displayLine = text.substring(offset, text.length());
         line = text.substring(offset, text.length());
         if (line.length() > 0) {
            y += height;
            totalHeight += height;
            endIndex = line.length();
         }
      }

      return totalHeight;
   }

   public static final XYRect drawMultilineText(
      Graphics graphics,
      Font font,
      String label,
      int x,
      int y,
      int width,
      int height,
      int justificationX,
      int justificationY,
      int textHeightCorrection,
      int screenWidth,
      int screenHeight,
      boolean draw
   ) {
      XYRect rect = (XYRect)(new Object());
      String labelRemaining = label.trim();
      int labelLength = font.getAdvance(labelRemaining);
      int labelHeight = font.getHeight() * textHeightCorrection / 100;
      Word[] words = new Word[0];
      Word lastWord = null;
      int beginIndex = 0;
      int endIndex = 0;
      int nextSpace = 0;
      int nextDash = 0;
      int nextSlash = 0;
      int numWords = 0;
      int space = 0;
      int[] lineOffsets = null;
      int longestWord = 0;

      while (beginIndex < labelRemaining.length()) {
         labelRemaining = labelRemaining.substring(beginIndex).trim();
         nextSpace = labelRemaining.indexOf(" ", 0);
         if (nextSpace == -1) {
            nextSpace = 65536;
         }

         nextDash = labelRemaining.indexOf("-", 0);
         if (nextDash == -1) {
            nextDash = 65536;
         }

         nextSlash = labelRemaining.indexOf("/", 0);
         if (nextSlash == -1) {
            nextSlash = 65536;
         }

         if (nextSpace == 65536 && nextDash == 65536 && nextSlash == 65536) {
            break;
         }

         int min = Minimum(nextSpace, nextDash, nextSlash);
         endIndex = min;
         if (min == nextDash) {
            space = 1;
         } else if (min == nextSlash) {
            space = 2;
         } else {
            space = 0;
         }

         Word word = new Word(font, labelRemaining.substring(0, endIndex), space);
         if (lastWord != null) {
            word._text = ((StringBuffer)(new Object())).append(lastWord._text).append(" ").append(word._text).toString();
            word._length = font.getAdvance(word._text);
            lastWord = null;
         } else if (word._text.equalsIgnoreCase("Mc")
            || word._text.equalsIgnoreCase("Mac")
            || word._text.equalsIgnoreCase("St")
            || word._text.equalsIgnoreCase("St.")
            || word._text.equalsIgnoreCase("New")
            || word._text.equalsIgnoreCase("San")
            || word._text.equalsIgnoreCase("Los")
            || word._text.equalsIgnoreCase("Las")) {
            lastWord = word;
            beginIndex = endIndex + 1;
            continue;
         }

         longestWord = Math.max(word._length, longestWord);
         Arrays.add(words, word);
         numWords++;
         beginIndex = endIndex + 1;
      }

      Word word = new Word(font, labelRemaining.trim(), space);
      if (lastWord != null) {
         word._text = ((StringBuffer)(new Object())).append(lastWord._text).append(" ").append(word._text).toString();
         word._length = font.getAdvance(word._text);
         lastWord = null;
      }

      longestWord = Math.max(word._length, longestWord);
      Arrays.add(words, word);
      if (++numWords <= 1) {
         rect.width = Math.min(labelLength, screenWidth);
         rect.height = Math.min(labelHeight, screenHeight);
      } else if (width != -1) {
         if (height == -1) {
            rect.width = width;
            rect.height = labelLength / rect.width * labelHeight;
            int numLines = rect.height / labelHeight;
            lineOffsets = new int[numLines];

            while (
               rect.height < screenHeight
                  && !drawTextInRect(graphics, font, words, lineOffsets, numWords, rect, labelHeight, justificationX, justificationY, false)
            ) {
               rect.height += labelHeight;
               numLines = rect.height / labelHeight;
               lineOffsets = new int[numLines];
            }
         } else {
            rect.width = width;
            rect.height = height;
         }
      } else {
         rect.height = (int)Math.sqrt(labelLength * labelHeight) / labelHeight * labelHeight;
         rect.width = labelLength * labelHeight / rect.height;
         if (rect.width < longestWord) {
            rect.width = longestWord;
            if (labelLength > longestWord) {
               rect.height = (labelLength / rect.width + 1) * labelHeight;
            } else {
               rect.height = labelLength / rect.width * labelHeight;
            }
         }

         if (rect.height / labelHeight == 1) {
            rect.width = Math.min(labelLength, screenWidth);
            rect.height = Math.min(labelHeight, screenHeight);
         } else {
            int numLines = rect.height / labelHeight;
            lineOffsets = new int[numLines];

            while (
               rect.width < screenWidth
                  && !drawTextInRect(graphics, font, words, lineOffsets, numWords, rect, labelHeight, justificationX, justificationY, false)
            ) {
               rect.width += labelHeight;
            }
         }
      }

      if (justificationX == 0) {
         rect.x = x;
         rect.y = y;
      } else {
         rect.x = x - rect.width / 2;
         rect.y = y - rect.height / 2;
      }

      if (draw) {
         if (lineOffsets == null) {
            int numLines = rect.height / labelHeight;
            lineOffsets = new int[numLines];
            drawTextInRect(graphics, font, words, lineOffsets, numWords, rect, labelHeight, justificationX, justificationY, false);
            if (justificationX == 0) {
               rect.x = x;
            } else {
               rect.x = x - rect.width / 2;
            }
         }

         drawTextInRect(graphics, font, words, lineOffsets, numWords, rect, labelHeight, justificationX, justificationY, true);
      }

      return rect;
   }

   private static final int Minimum(int a, int b, int c) {
      int min = a;
      if (b < min) {
         min = b;
      }

      if (c < min) {
         min = c;
      }

      return min;
   }

   static final boolean drawTextInRect(
      Graphics graphics,
      Font font,
      Word[] words,
      int[] lineOffsets,
      int numWords,
      XYRect rect,
      int textHeight,
      int justificationX,
      int justificationY,
      boolean draw
   ) {
      int numLines = rect.height / textHeight;
      int currentLine = 0;
      int currentWord = 0;
      int spaceAvailable = rect.width;
      int minSpaceAvailable = rect.width;
      int offset = 0;
      int spaceLength = font.getAdvance(" ");
      int separatorLength = 0;
      int space = 0;
      int lineOffset = rect.x + lineOffsets[currentLine];

      while (currentLine < numLines && currentWord < numWords) {
         Word word = words[currentWord];
         if (spaceAvailable < rect.width) {
            if (space == 0) {
               if (draw) {
                  graphics.drawText(" ", lineOffset + offset, rect.y + currentLine * textHeight, 64, rect.width);
               }

               separatorLength = spaceLength;
            } else if (space == 1) {
               if (draw) {
                  graphics.drawText("-", lineOffset + offset, rect.y + currentLine * textHeight, 64, rect.width);
               }

               separatorLength = spaceLength;
            } else if (space == 2) {
               if (draw) {
                  graphics.drawText("/", lineOffset + offset, rect.y + currentLine * textHeight, 64, rect.width);
               }

               separatorLength = spaceLength;
            }

            spaceAvailable -= separatorLength;
            offset += separatorLength;
         }

         if (spaceAvailable >= word._length) {
            if (draw) {
               graphics.drawText(word._text, lineOffset + offset, rect.y + currentLine * textHeight, 64, rect.width);
            }

            spaceAvailable -= word._length;
            offset += word._length;
            currentWord++;
         } else {
            if (!draw) {
               if (justificationX == 0) {
                  lineOffsets[currentLine] = 0;
               } else if (justificationX == 1) {
                  lineOffsets[currentLine] = (spaceAvailable + separatorLength) / 2;
               } else {
                  lineOffsets[currentLine] = spaceAvailable + separatorLength;
               }
            }

            if (++currentLine < numLines) {
               lineOffset = rect.x + lineOffsets[currentLine];
            }

            minSpaceAvailable = Math.min(minSpaceAvailable, spaceAvailable + spaceLength);
            spaceAvailable = rect.width;
            offset = 0;
         }

         space = word._space;
      }

      if (!draw) {
         if (justificationX == 0) {
            lineOffsets[numLines - 1] = 0;
         } else if (justificationX == 1) {
            lineOffsets[numLines - 1] = spaceAvailable / 2;
         } else {
            lineOffsets[numLines - 1] = spaceAvailable;
         }
      }

      minSpaceAvailable = Math.min(minSpaceAvailable, spaceAvailable);
      if (currentWord != numWords) {
         return false;
      }

      if (!draw) {
         if (currentLine < numLines - 1) {
            if (justificationY == 0) {
               rect.height -= textHeight * (numLines - 1 - currentLine);
            } else if (justificationY == 1) {
               int yOffset = (rect.height - (currentLine + 1) * textHeight) / 2;
               rect.height -= 2 * yOffset;
               rect.y += yOffset;
            } else if (justificationY == 2) {
               int yOffset = rect.height - (currentLine + 1) * textHeight;
               rect.height -= yOffset;
               rect.y += yOffset;
            }
         }

         if (minSpaceAvailable > 0) {
            rect.width -= minSpaceAvailable;

            for (int i = 0; i < numLines; i++) {
               if (justificationX == 1) {
                  lineOffsets[i] -= minSpaceAvailable / 2;
               } else if (justificationX == 2) {
                  lineOffsets[i] -= minSpaceAvailable;
               }
            }
         }
      }

      return true;
   }
}
