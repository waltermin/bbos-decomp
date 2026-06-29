package net.rim.device.apps.internal.browser.css;

import net.rim.device.api.util.StringUtilities;

public final class CSSUtilities {
   public static final int SP_AZIMUTH;
   public static final int SP_BACKGROUND;
   public static final int SP_BACKGROUND_ATTACHMENT;
   public static final int SP_BACKGROUND_COLOR;
   public static final int SP_BACKGROUND_IMAGE;
   public static final int SP_BACKGROUND_POSITION;
   public static final int SP_BACKGROUND_REPEAT;
   public static final int SP_BORDER;
   public static final int SP_BORDER_BOTTOM;
   public static final int SP_BORDER_BOTTOM_COLOR;
   public static final int SP_BORDER_BOTTOM_STYLE;
   public static final int SP_BORDER_BOTTOM_WIDTH;
   public static final int SP_BORDER_COLLAPSE;
   public static final int SP_BORDER_COLOR;
   public static final int SP_BORDER_LEFT;
   public static final int SP_BORDER_LEFT_COLOR;
   public static final int SP_BORDER_LEFT_STYLE;
   public static final int SP_BORDER_LEFT_WIDTH;
   public static final int SP_BORDER_RIGHT;
   public static final int SP_BORDER_RIGHT_COLOR;
   public static final int SP_BORDER_RIGHT_STYLE;
   public static final int SP_BORDER_RIGHT_WIDTH;
   public static final int SP_BORDER_SPACING;
   public static final int SP_BORDER_STYLE;
   public static final int SP_BORDER_TOP;
   public static final int SP_BORDER_TOP_COLOR;
   public static final int SP_BORDER_TOP_STYLE;
   public static final int SP_BORDER_TOP_WIDTH;
   public static final int SP_BORDER_WIDTH;
   public static final int SP_BOTTOM;
   public static final int SP_CAPTION_SIDE;
   public static final int SP_CLEAR;
   public static final int SP_CLIP;
   public static final int SP_COLOR;
   public static final int SP_CONTENT;
   public static final int SP_COUNTER_INCREMENT;
   public static final int SP_COUNTER_RESET;
   public static final int SP_CUE;
   public static final int SP_CUE_AFTER;
   public static final int SP_CUE_BEFORE;
   public static final int SP_CURSOR;
   public static final int SP_DIRECTION;
   public static final int SP_DISPLAY;
   public static final int SP_ELEVATION;
   public static final int SP_EMPTY_CELLS;
   public static final int SP_FLOAT;
   public static final int SP_FONT;
   public static final int SP_FONT_FAMILY;
   public static final int SP_FONT_SIZE;
   public static final int SP_FONT_STYLE;
   public static final int SP_FONT_VARIANT;
   public static final int SP_FONT_WEIGHT;
   public static final int SP_HEIGHT;
   public static final int SP_LEFT;
   public static final int SP_LETTER_SPACING;
   public static final int SP_LINE_HEIGHT;
   public static final int SP_LIST_STYLE;
   public static final int SP_LIST_STYLE_IMAGE;
   public static final int SP_LIST_STYLE_POSITION;
   public static final int SP_LIST_STYLE_TYPE;
   public static final int SP_MARGIN;
   public static final int SP_MARGIN_BOTTOM;
   public static final int SP_MARGIN_LEFT;
   public static final int SP_MARGIN_RIGHT;
   public static final int SP_MARGIN_TOP;
   public static final int SP_MAX_HEIGHT;
   public static final int SP_MAX_WIDTH;
   public static final int SP_MIN_HEIGHT;
   public static final int SP_MIN_WIDTH;
   public static final int SP_ORPHANS;
   public static final int SP_OUTLINE;
   public static final int SP_OUTLINE_COLOR;
   public static final int SP_OUTLINE_STYLE;
   public static final int SP_OUTLINE_WIDTH;
   public static final int SP_OVERFLOW;
   public static final int SP_PADDING;
   public static final int SP_PADDING_BOTTOM;
   public static final int SP_PADDING_LEFT;
   public static final int SP_PADDING_RIGHT;
   public static final int SP_PADDING_TOP;
   public static final int SP_PAGE_BREAK_AFTER;
   public static final int SP_PAGE_BREAK_BEFORE;
   public static final int SP_PAGE_BREAK_INSIDE;
   public static final int SP_PAUSE;
   public static final int SP_PAUSE_AFTER;
   public static final int SP_PAUSE_BEFORE;
   public static final int SP_PITCH;
   public static final int SP_PITCH_RANGE;
   public static final int SP_PLAY_DURING;
   public static final int SP_POSITION;
   public static final int SP_QUOTES;
   public static final int SP_RICHNESS;
   public static final int SP_RIGHT;
   public static final int SP_SPEAK;
   public static final int SP_SPEAK_HEADER;
   public static final int SP_SPEAK_NUMERAL;
   public static final int SP_SPEAK_PUNCTUATION;
   public static final int SP_SPEECH_RATE;
   public static final int SP_STRESS;
   public static final int SP_TABLE_LAYOUT;
   public static final int SP_TEXT_ALIGN;
   public static final int SP_TEXT_DECORATION;
   public static final int SP_TEXT_INDENT;
   public static final int SP_TEXT_TRANSFORM;
   public static final int SP_TOP;
   public static final int SP_UNICODE_BIDI;
   public static final int SP_VERTICAL_ALIGN;
   public static final int SP_VISIBILITY;
   public static final int SP_VOICE_FAMILY;
   public static final int SP_VOLUME;
   public static final int SP_WHITE_SPACE;
   public static final int SP_WIDOWS;
   public static final int SP_WIDTH;
   public static final int SP_WORD_SPACING;
   public static final int SP_Z_INDEX;
   public static final int SP__WAP_ACCESSKEY;
   public static final int SP__WAP_INPUT_FORMAT;
   public static final int SP__WAP_INPUT_REQUIRED;
   public static final int SP__WAP_MARQUEE_DIR;
   public static final int SP__WAP_MARQUEE_LOOP;
   public static final int SP__WAP_MARQUEE_SPEED;
   public static final int SP__WAP_MARQUEE_STYLE;
   public static final int SP_NUM_VALUES;
   public static final int[] SP_TOKENS = new int[]{
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      26,
      27,
      28,
      29,
      30,
      31,
      32,
      33,
      34,
      35,
      36,
      37,
      38,
      39,
      40,
      41,
      42,
      43,
      44,
      45,
      46,
      47,
      48,
      49,
      50,
      51,
      52,
      53,
      54,
      55,
      56,
      57,
      58,
      59,
      60,
      61,
      62,
      63,
      64,
      65,
      66,
      67,
      68,
      69,
      70,
      71,
      72,
      73,
      74,
      75,
      76,
      77,
      78,
      79,
      80,
      81,
      82,
      83,
      84,
      85,
      86,
      87,
      88,
      89,
      90,
      91,
      92,
      93,
      94,
      95,
      96,
      97,
      98,
      99,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      110,
      111,
      112,
      113,
      114,
      115,
      116,
      117,
      118,
      119,
      120,
      121,
      122,
      -804650826,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      26,
      27,
      28,
      29,
      30,
      31,
      32,
      33,
      34,
      35,
      36,
      37,
      38,
      39,
      40,
      41,
      42,
      43,
      44,
      45,
      46,
      47,
      48,
      49,
      50,
      51,
      52,
      53,
      54,
      55,
      56,
      57,
      58,
      59,
      60,
      61,
      62,
      63,
      64,
      65,
      66,
      67,
      68,
      69,
      70,
      71,
      72,
      73,
      74,
      75,
      76,
      77,
      78,
      181,
      79,
      80,
      81,
      82,
      83,
      84,
      85,
      86,
      87,
      88,
      89,
      90,
      91,
      92,
      93,
      94,
      95,
      96,
      97,
      98,
      99,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      110,
      111,
      112,
      113,
      114,
      115,
      116,
      117,
      118,
      119,
      120,
      121,
      122,
      123,
      124,
      125,
      126,
      127,
      128,
      129,
      130,
      131,
      132,
      133,
      134,
      135,
      136,
      137,
      138,
      139,
      140,
      141,
      142,
      143,
      144,
      145,
      146,
      147,
      148,
      149,
      150,
      151,
      152,
      153,
      154,
      155,
      156,
      157,
      158,
      159,
      160,
      161,
      162,
      163,
      182,
      164,
      165,
      166,
      167,
      168,
      169,
      170,
      171,
      172,
      173,
      174,
      175,
      176,
      177,
      178,
      179,
      180,
      -805044223,
      2,
      -805044222,
      2573,
      -805044219,
      1718183726,
      10,
      -804651007,
      51,
      -805044106,
      944130375,
      942393,
      11730960,
      0,
      -16776961,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      11519,
      917504,
      67108880,
      1237848107,
      1597552811,
      1591786409,
      -2145679866,
      1652932869,
      -1397849949,
      -51265127,
      -358894966,
      1073371445,
      -197907616,
      143953492,
      15104,
      -805043264,
      944130375,
      942393,
      16187408,
      0,
      275462408,
      957899586,
      1665276003,
      409682456,
      1109488458,
      1800020323,
      829639201,
      1665301330,
      -1670165884,
      1384409938,
      1801096299,
      -1938597244,
      1520204634,
      -2074434181,
      -1938594907,
      1670151011,
      2070647931,
      -1803261036,
      1940227179,
      -1938580348,
      -1382777947,
      -2071690108,
      -1803248500,
      -1248033619,
      -2067948412,
      -1668498011,
      -1247441739,
      -1800034924,
      -1382234451,
      -961700675,
      -1513706075,
      -1247426891,
      -692214314,
      -1109997899,
      -960045362,
      -691616058,
      -959525178,
      -825829682,
      -405354786,
      -824258866,
      -690559018,
      -271132962,
      -690037034,
      -555814946,
      -404824354,
      -554705186,
      -404230169,
      -270014489,
      -403183641,
      -269486097,
      -135270417,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1
   };
   public static final String[] SP_NAMES = new String[]{
      "azimuth",
      "background",
      "background-attachment",
      "background-color",
      "background-image",
      "background-position",
      "background-repeat",
      "border",
      "border-bottom",
      "border-bottom-color",
      "border-bottom-style",
      "border-bottom-width",
      "border-collapse",
      "border-color",
      "border-left",
      "border-left-color",
      "border-left-style",
      "border-left-width",
      "border-right",
      "border-right-color",
      "border-right-style",
      "border-right-width",
      "border-spacing",
      "border-style",
      "border-top",
      "border-top-color",
      "border-top-style",
      "border-top-width",
      "border-width",
      "bottom",
      "caption-side",
      "clear",
      "clip",
      "color",
      "content",
      "counter-increment",
      "counter-reset",
      "cue",
      "cue-after",
      "cue-before",
      "cursor",
      "direction",
      "display",
      "elevation",
      "empty-cells",
      "float",
      "font",
      "font-family",
      "font-size",
      "font-style",
      "font-variant",
      "font-weight",
      "height",
      "left",
      "letter-spacing",
      "line-height",
      "list-style",
      "list-style-image",
      "list-style-position",
      "list-style-type",
      "margin",
      "margin-bottom",
      "margin-left",
      "margin-right",
      "margin-top",
      "max-height",
      "max-width",
      "min-height",
      "min-width",
      "orphans",
      "outline",
      "outline-color",
      "outline-style",
      "outline-width",
      "overflow",
      "padding",
      "padding-bottom",
      "padding-left",
      "padding-right",
      "padding-top",
      "page-break-after",
      "page-break-before",
      "page-break-inside",
      "pause",
      "pause-after",
      "pause-before",
      "pitch",
      "pitch-range",
      "play-during",
      "position",
      "quotes",
      "richness",
      "right",
      "speak",
      "speak-header",
      "speak-numeral",
      "speak-punctuation",
      "speech-rate",
      "stress",
      "table-layout",
      "text-align",
      "text-decoration",
      "text-indent",
      "text-transform",
      "top",
      "unicode-bidi",
      "vertical-align",
      "visibility",
      "voice-family",
      "volume",
      "white-space",
      "widows",
      "width",
      "word-spacing",
      "z-index",
      "-wap-accesskey",
      "-wap-input-format",
      "-wap-input-required",
      "-wap-marquee-dir",
      "-wap-marquee-loop",
      "-wap-marquee-speed",
      "-wap-marquee-style"
   };
   public static final int[] SP_INDICIES = new int[]{
      0,
      1,
      30,
      41,
      43,
      45,
      52,
      52,
      53,
      53,
      53,
      53,
      60,
      69,
      69,
      75,
      90,
      91,
      93,
      99,
      105,
      106,
      110,
      114,
      114,
      114,
      115,
      -804650981,
      0,
      7,
      17,
      30,
      38,
      40,
      48,
      50,
      55,
      65,
      66,
      66,
      84,
      92,
      101,
      107,
      112,
      112,
      122,
      146,
      163,
      168,
      169,
      171,
      181,
      181,
      181,
      -805044213,
      775162112,
      774909491,
      3420721,
      -805044199,
      1699878656,
      1918985587,
      1226860643,
      1867325550,
      1852795252,
      1685343264,
      46,
      -804650886,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      26,
      27,
      28,
      29,
      30,
      31,
      32,
      33,
      34,
      35,
      36,
      37,
      38,
      39,
      40
   };
   public static final int SV_UNDEFINED;
   public static final int SV_DEFINED;
   public static final int SV_STRING;
   public static final int SV_URI;
   public static final int SV_FUNCTION;
   public static final int SV_COLOR;
   public static final int SV_NUMBER;
   public static final int SV_REAL;
   public static final int SV_PERCENTAGE;
   public static final int SV_EM;
   public static final int SV_EX;
   public static final int SV_PX;
   public static final int SV_IN;
   public static final int SV_CM;
   public static final int SV_MM;
   public static final int SV_PT;
   public static final int SV_PC;
   public static final int SV_DEG;
   public static final int SV_GRAD;
   public static final int SV_RAD;
   public static final int SV_MS;
   public static final int SV_S;
   public static final int SV_HZ;
   public static final int SV_KHZ;
   public static final int SV_IMPORTANT;
   public static final int SV_TYPE_MASK;
   public static final int SV_UNITS_MASK;
   public static final int SV_FLAGS_MASK;
   public static final int SV_ABOVE;
   public static final int SV_ABSOLUTE;
   public static final int SV_ALTERNATE;
   public static final int SV_ALWAYS;
   public static final int SV_ARMENIAN;
   public static final int SV_AUTO;
   public static final int SV_AVOID;
   public static final int SV_BASELINE;
   public static final int SV_BEHIND;
   public static final int SV_BELOW;
   public static final int SV_BIDI_OVERRIDE;
   public static final int SV_BLINK;
   public static final int SV_BLOCK;
   public static final int SV_BOLD;
   public static final int SV_BOLDER;
   public static final int SV_BOTH;
   public static final int SV_BOTTOM;
   public static final int SV_CAPITALIZE;
   public static final int SV_CAPTION;
   public static final int SV_CENTER;
   public static final int SV_CENTER_LEFT;
   public static final int SV_CENTER_RIGHT;
   public static final int SV_CHILD;
   public static final int SV_CIRCLE;
   public static final int SV_CLOSE_QUOTE;
   public static final int SV_CODE;
   public static final int SV_COLLAPSE;
   public static final int SV_CONTINUOUS;
   public static final int SV_CROSSHAIR;
   public static final int SV_CURSIVE;
   public static final int SV_DASHED;
   public static final int SV_DECIMAL;
   public static final int SV_DECIMAL_LEADING_ZERO;
   public static final int SV_DEFAULT;
   public static final int SV_DIGITS;
   public static final int SV_DISC;
   public static final int SV_DOTTED;
   public static final int SV_DOUBLE;
   public static final int SV_E_RESIZE;
   public static final int SV_EMBED;
   public static final int SV_FALSE;
   public static final int SV_FANTASY;
   public static final int SV_FAR_LEFT;
   public static final int SV_FAR_RIGHT;
   public static final int SV_FAST;
   public static final int SV_FASTER;
   public static final int SV_FEMALE;
   public static final int SV_FIXED;
   public static final int SV_GEORGIAN;
   public static final int SV_GROOVE;
   public static final int SV_HELP;
   public static final int SV_HIDDEN;
   public static final int SV_HIDE;
   public static final int SV_HIGH;
   public static final int SV_HIGHER;
   public static final int SV_ICON;
   public static final int SV_INFINITE;
   public static final int SV_INHERIT;
   public static final int SV_INLINE;
   public static final int SV_INLINE_BLOCK;
   public static final int SV_INLINE_TABLE;
   public static final int SV_INSET;
   public static final int SV_INSIDE;
   public static final int SV_INVERT;
   public static final int SV_ITALIC;
   public static final int SV_JUSTIFY;
   public static final int SV_LARGE;
   public static final int SV_LARGER;
   public static final int SV_LEFT;
   public static final int SV_LEFT_SIDE;
   public static final int SV_LEFTWARDS;
   public static final int SV_LEVEL;
   public static final int SV_LIGHTER;
   public static final int SV_LINE_THROUGH;
   public static final int SV_LIST_ITEM;
   public static final int SV_LOUD;
   public static final int SV_LOW;
   public static final int SV_LOWER;
   public static final int SV_LOWER_GREEK;
   public static final int SV_LOWER_LATIN;
   public static final int SV_LOWER_ROMAN;
   public static final int SV_LOWERCASE;
   public static final int SV_LTR;
   public static final int SV_MALE;
   public static final int SV_MEDIUM;
   public static final int SV_MENU;
   public static final int SV_MESSAGE_BOX;
   public static final int SV_MIDDLE;
   public static final int SV_MIX;
   public static final int SV_MONOSPACE;
   public static final int SV_MOVE;
   public static final int SV_N_RESIZE;
   public static final int SV_NE_RESIZE;
   public static final int SV_NO_CLOSE_QUOTE;
   public static final int SV_NO_OPEN_QUOTE;
   public static final int SV_NO_REPEAT;
   public static final int SV_NONE;
   public static final int SV_NORMAL;
   public static final int SV_NOWRAP;
   public static final int SV_NW_RESIZE;
   public static final int SV_OBLIQUE;
   public static final int SV_ONCE;
   public static final int SV_OPEN_QUOTE;
   public static final int SV_OUTSET;
   public static final int SV_OUTSIDE;
   public static final int SV_OVERLINE;
   public static final int SV_POINTER;
   public static final int SV_PRE;
   public static final int SV_PRE_LINE;
   public static final int SV_PRE_WRAP;
   public static final int SV_PROGRESS;
   public static final int SV_RELATIVE;
   public static final int SV_REPEAT;
   public static final int SV_REPEAT_X;
   public static final int SV_REPEAT_Y;
   public static final int SV_RIDGE;
   public static final int SV_RIGHT;
   public static final int SV_RIGHT_SIDE;
   public static final int SV_RIGHTWARDS;
   public static final int SV_RTL;
   public static final int SV_RUN_IN;
   public static final int SV_S_RESIZE;
   public static final int SV_SANS_SERIF;
   public static final int SV_SCROLL;
   public static final int SV_SE_RESIZE;
   public static final int SV_SEPARATE;
   public static final int SV_SERIF;
   public static final int SV_SHOW;
   public static final int SV_SILENT;
   public static final int SV_SLIDE;
   public static final int SV_SLOW;
   public static final int SV_SLOWER;
   public static final int SV_SMALL;
   public static final int SV_SMALL_CAPS;
   public static final int SV_SMALL_CAPTION;
   public static final int SV_SMALLER;
   public static final int SV_SOFT;
   public static final int SV_SOLID;
   public static final int SV_SPELL_OUT;
   public static final int SV_SQUARE;
   public static final int SV_STATIC;
   public static final int SV_STATUS_BAR;
   public static final int SV_SUB;
   public static final int SV_SUPER;
   public static final int SV_SW_RESIZE;
   public static final int SV_TABLE;
   public static final int SV_TABLE_CAPTION;
   public static final int SV_TABLE_CELL;
   public static final int SV_TABLE_COLUMN;
   public static final int SV_TABLE_COLUMN_GROUP;
   public static final int SV_TABLE_FOOTER_GROUP;
   public static final int SV_TABLE_HEADER_GROUP;
   public static final int SV_TABLE_ROW;
   public static final int SV_TABLE_ROW_GROUP;
   public static final int SV_TEXT;
   public static final int SV_TEXT_BOTTOM;
   public static final int SV_TEXT_TOP;
   public static final int SV_THICK;
   public static final int SV_THIN;
   public static final int SV_TOP;
   public static final int SV_TRANSPARENT;
   public static final int SV_TRUE;
   public static final int SV_UNDERLINE;
   public static final int SV_UPPER_LATIN;
   public static final int SV_UPPER_ROMAN;
   public static final int SV_UPPERCASE;
   public static final int SV_VISIBLE;
   public static final int SV_W_RESIZE;
   public static final int SV_WAIT;
   public static final int SV_X_FAST;
   public static final int SV_X_HIGH;
   public static final int SV_X_LARGE;
   public static final int SV_X_LOUD;
   public static final int SV_X_LOW;
   public static final int SV_X_SLOW;
   public static final int SV_X_SMALL;
   public static final int SV_X_SOFT;
   public static final int SV_XX_LARGE;
   public static final int SV_XX_SMALL;
   public static final int SV__WAP_MARQUEE;
   public static final int SV_LOWER_ALPHA;
   public static final int SV_UPPER_ALPHA;
   public static final int[] SV_TOKENS = new int[]{
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      26,
      27,
      28,
      29,
      30,
      31,
      32,
      33,
      34,
      35,
      36,
      37,
      38,
      39,
      40,
      41,
      42,
      43,
      44,
      45,
      46,
      47,
      48,
      49,
      50,
      51,
      52,
      53,
      54,
      55,
      56,
      57,
      58,
      59,
      60,
      61,
      62,
      63,
      64,
      65,
      66,
      67,
      68,
      69,
      70,
      71,
      72,
      73,
      74,
      75,
      76,
      77,
      78,
      181,
      79,
      80,
      81,
      82,
      83,
      84,
      85,
      86,
      87,
      88,
      89,
      90,
      91,
      92,
      93,
      94,
      95,
      96,
      97,
      98,
      99,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      110,
      111,
      112,
      113,
      114,
      115,
      116,
      117,
      118,
      119,
      120,
      121,
      122,
      123,
      124,
      125,
      126,
      127,
      128,
      129,
      130,
      131,
      132,
      133,
      134,
      135,
      136,
      137,
      138,
      139,
      140,
      141,
      142,
      143,
      144,
      145,
      146,
      147,
      148,
      149,
      150,
      151,
      152,
      153,
      154,
      155,
      156,
      157,
      158,
      159,
      160,
      161,
      162,
      163,
      182,
      164,
      165,
      166,
      167,
      168,
      169,
      170,
      171,
      172,
      173,
      174,
      175,
      176,
      177,
      178,
      179,
      180,
      -805044223,
      2,
      -805044222,
      2573,
      -805044219,
      1718183726,
      10,
      -804651007,
      51,
      -805044106,
      944130375,
      942393,
      11730960,
      0,
      -16776961,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      11519,
      917504,
      67108880,
      1237848107,
      1597552811,
      1591786409,
      -2145679866,
      1652932869,
      -1397849949,
      -51265127,
      -358894966,
      1073371445,
      -197907616,
      143953492,
      15104,
      -805043264,
      944130375,
      942393,
      16187408,
      0,
      275462408,
      957899586,
      1665276003,
      409682456,
      1109488458,
      1800020323,
      829639201,
      1665301330,
      -1670165884,
      1384409938,
      1801096299,
      -1938597244,
      1520204634,
      -2074434181,
      -1938594907,
      1670151011,
      2070647931,
      -1803261036,
      1940227179,
      -1938580348,
      -1382777947,
      -2071690108,
      -1803248500,
      -1248033619,
      -2067948412,
      -1668498011,
      -1247441739,
      -1800034924,
      -1382234451,
      -961700675,
      -1513706075,
      -1247426891,
      -692214314,
      -1109997899,
      -960045362,
      -691616058,
      -959525178,
      -825829682,
      -405354786,
      -824258866,
      -690559018,
      -271132962,
      -690037034,
      -555814946,
      -404824354,
      -554705186,
      -404230169,
      -270014489,
      -403183641,
      -269486097,
      -135270417,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      11519,
      917504,
      134217744,
      138084517,
      1622165532,
      1446256913,
      1650542744,
      1159826693,
      1357381726,
      294060836,
      414474799,
      -1002217039,
      -1135983223,
      1178808704,
      1211439245,
      -2106022788,
      807584775,
      1498977401,
      -1739864860,
      -1600356283,
      231170681,
      75514398,
      646087137,
      1883119114,
      -2083397408,
      134794536,
      983044112,
      -1991802656,
      -2144059628,
      -2050842256,
      -800715002,
      -341779756,
      -1322645417,
      25195734,
      353044355,
      -346756054,
      75644962,
      1747886602,
      214479681,
      1209537297,
      637743360,
      327686102,
      -1609808341,
      -1865948239,
      989856259,
      23855360,
      1721794405,
      599328672,
      712179968,
      712179968,
      16806977,
      -2104615050,
      1870004480,
      16803179,
      1701539702,
      1466199617,
      16778081,
      1701539702,
      -848269247,
      190226882,
      1979777122,
      1181051759,
      16802817,
      1701539702,
      1634640466,
      16809570,
      100713909,
      7938075,
      416518,
      -1073325306,
      40633856,
      16274021,
      134273799,
      1076234240,
      134265460,
      1678536486,
      4407629,
      2121868808,
      -2111436800,
      455608320,
      134219553,
      1970412328,
      1864803,
      1914382344,
      477389685,
      671613043,
      272842608,
      1885038,
      1742219272,
      1702200843,
      1279330304,
      134245121,
      1869832769,
      1432698732,
      134237266,
      487023425,
      1665206272,
      -1339351801,
      7564379,
      123945224,
      1886999581,
      134247269,
      487023425,
      -2044837704,
      7562613,
      123945224,
      1836008489,
      134218081,
      688350017,
      6820176,
      123945224,
      134258985,
      7629889,
      -1318305528,
      1091043438,
      1676910,
      1886404872,
      134267597,
      -848269247,
      -1637375806,
      1091043373,
      1706661490,
      1950418944,
      1651077748,
      134243647,
      1920234561,
      1698652777,
      6649222,
      1953775880,
      1063414130,
      1702200933,
      1948808001,
      1950418944,
      1651077748,
      1971742015,
      1114849637,
      561934191,
      1967196160,
      2030246183,
      -2143221760,
      2120394601,
      134282071,
      -1486258111,
      -1310097822,
      134247528,
      1937186882,
      1107820555,
      192116512,
      9092163,
      1998602760,
      -1136456845,
      1980777354,
      134220586,
      1822844738,
      1107820569,
      543648685,
      134243490,
      134267202,
      1079296322,
      1124597768,
      1948326672,
      272828416,
      1349787931,
      7106415,
      1980777224,
      1864459,
      1980777224,
      134220586,
      -1972802493,
      -1126229688,
      543380255,
      1124597956,
      1451911532,
      1394564394,
      12853347,
      1768702728,
      1919506058,
      1461679476,
      1124598011,
      1451911532,
      1970565737,
      1756490015,
      1124597876,
      1468688748,
      1124598011,
      -376805012,
      7628977,
      1082016520,
      -1371338752,
      1098149388,
      1676910,
      212747016,
      1078162542,
      1124597860,
      1953369262,
      7107157,
      -1967373560,
      1701869908,
      -1136457728,
      1124597975,
      1145662413,
      574883840,
      12197647,
      1864516616,
      1309399971,
      134243628,
      1145917252,
      9080713,
      1717912584,
      1953264993,
      1766066176,
      6778655,
      -781630456,
      134225012,
      -1485792700,
      2120376697,
      9092163,
      1533953288,
      134251620,
      -1167823291,
      1984235520,
      1804160138,
      21366784,
      1644910180,
      1631979520,
      1174929415,
      6630514,
      -948812280,
      134253165,
      1841787462,
      1956816522,
      -1572468736,
      1864803,
      1671579144,
      1867521140,
      134245487,
      6793030,
      1739015688,
      1208483955,
      134243852,
      -513405880,
      1414006784,
      1873170516,
      6646123,
      460277768,
      1934100480,
      134219762,
      2031722056,
      -42766512,
      1833502720,
      1592805,
      1795247112,
      1632372736,
      134253410,
      1717658956,
      1745049535,
      1309147348,
      134243628,
      1916899150,
      134247271,
      -1991471282,
      134247199,
      455661902,
      134219553,
      1229838670,
      2035565901,
      134243696,
      -1016438193,
      1917782016,
      1174626647,
      1325924377,
      1141599863,
      9080713,
      192237832,
      1376256121,
      1102775827,
      -1026723728,
      324143104,
      -1488536826,
      134247271,
      -1157229742,
      7571535,
      101929480,
      485839803,
      -1957558272,
      575866880,
      761357328,
      1934381653,
      1666385920,
      134222604,
      1769104211,
      1393033245,
      493449827,
      23555653,
      1393033317,
      6625384,
      -1368435960,
      1393033223,
      134243186,
      1698403923,
      -1017968640,
      15487772,
      -87076088,
      1533953315,
      7570532,
      -87076088,
      1425952035,
      6647929,
      -87076088,
      1425952035
   };
   public static final String[] SV_NAMES = new String[]{
      "above",
      "absolute",
      "alternate",
      "always",
      "armenian",
      "auto",
      "avoid",
      "baseline",
      "behind",
      "below",
      "bidi-override",
      "blink",
      "block",
      "bold",
      "bolder",
      "both",
      "bottom",
      "capitalize",
      "caption",
      "center",
      "center-left",
      "center-right",
      "child",
      "circle",
      "close-quote",
      "code",
      "collapse",
      "continuous",
      "crosshair",
      "cursive",
      "dashed",
      "decimal",
      "decimal-leading-zero",
      "default",
      "digits",
      "disc",
      "dotted",
      "double",
      "e-resize",
      "embed",
      "false",
      "fantasy",
      "far-left",
      "far-right",
      "fast",
      "faster",
      "female",
      "fixed",
      "georgian",
      "groove",
      "help",
      "hidden",
      "hide",
      "high",
      "higher",
      "icon",
      "infinite",
      "inherit",
      "inline",
      "inline-block",
      "inline-table",
      "inset",
      "inside",
      "invert",
      "italic",
      "justify",
      "large",
      "larger",
      "left",
      "left-side",
      "leftwards",
      "level",
      "lighter",
      "line-through",
      "list-item",
      "loud",
      "low",
      "lower",
      "lower-alpha",
      "lower-greek",
      "lower-latin",
      "lower-roman",
      "lowercase",
      "ltr",
      "male",
      "medium",
      "menu",
      "message-box",
      "middle",
      "mix",
      "monospace",
      "move",
      "n-resize",
      "ne-resize",
      "no-close-quote",
      "no-open-quote",
      "no-repeat",
      "none",
      "normal",
      "nowrap",
      "nw-resize",
      "oblique",
      "once",
      "open-quote",
      "outset",
      "outside",
      "overline",
      "pointer",
      "pre",
      "pre-line",
      "pre-wrap",
      "progress",
      "relative",
      "repeat",
      "repeat-x",
      "repeat-y",
      "ridge",
      "right",
      "right-side",
      "rightwards",
      "rtl",
      "run-in",
      "s-resize",
      "sans-serif",
      "scroll",
      "se-resize",
      "separate",
      "serif",
      "show",
      "silent",
      "slide",
      "slow",
      "slower",
      "small",
      "small-caps",
      "small-caption",
      "smaller",
      "soft",
      "solid",
      "spell-out",
      "square",
      "static",
      "status-bar",
      "sub",
      "super",
      "sw-resize",
      "table",
      "table-caption",
      "table-cell",
      "table-column",
      "table-column-group",
      "table-footer-group",
      "table-header-group",
      "table-row",
      "table-row-group",
      "text",
      "text-bottom",
      "text-top",
      "thick",
      "thin",
      "top",
      "transparent",
      "true",
      "underline",
      "upper-alpha",
      "upper-latin",
      "upper-roman",
      "uppercase",
      "visible",
      "w-resize",
      "wait",
      "x-fast",
      "x-high",
      "x-large",
      "x-loud",
      "x-low",
      "x-slow",
      "x-small",
      "x-soft",
      "xx-large",
      "xx-small",
      "-wap-marquee"
   };
   public static final int[] SV_INDICIES = new int[]{
      0,
      7,
      17,
      30,
      38,
      40,
      48,
      50,
      55,
      65,
      66,
      66,
      84,
      92,
      101,
      107,
      112,
      112,
      122,
      146,
      163,
      168,
      169,
      171,
      181,
      181,
      181,
      -805044213,
      775162112,
      774909491,
      3420721,
      -805044199,
      1699878656,
      1918985587,
      1226860643,
      1867325550,
      1852795252,
      1685343264,
      46,
      -804650886,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      26,
      27,
      28,
      29,
      30,
      31,
      32,
      33,
      34,
      35,
      36,
      37,
      38,
      39,
      40,
      41,
      42,
      43,
      44,
      45,
      46,
      47,
      48,
      49,
      50,
      51,
      52,
      53,
      54,
      55,
      56,
      57,
      58,
      59,
      60,
      61,
      62,
      63,
      64,
      65,
      66,
      67,
      68
   };

   private CSSUtilities() {
   }

   public static final boolean isStylePropertySupported(int property) {
      switch (property) {
         case 1:
         case 13:
         case 23:
         case 30:
         case 31:
         case 32:
         case 33:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 44:
         case 45:
         case 54:
         case 55:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 103:
         case 105:
         case 106:
         case 109:
         case 110:
         case 112:
         case 114:
         case 115:
         case 116:
            return false;
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
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
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 34:
         case 42:
         case 43:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 56:
         case 57:
         case 58:
         case 59:
         case 60:
         case 68:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 100:
         case 101:
         case 102:
         case 104:
         case 107:
         case 108:
         case 111:
         case 113:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         default:
            return true;
      }
   }

   public static final int resolveStyleProperty(String property, int startIndex, int endIndex) {
      return resolveToken(property, startIndex, endIndex, SP_NAMES, SP_TOKENS, SP_INDICIES);
   }

   public static final String resolveStyleProperty(int property) {
      int count = SP_TOKENS.length;

      for (int i = 0; i < count; i++) {
         if (SP_TOKENS[i] == property) {
            return SP_NAMES[i];
         }
      }

      return null;
   }

   public static final int resolveStyleValue(String value, int startIndex, int endIndex) {
      return resolveToken(value, startIndex, endIndex, SV_NAMES, SV_TOKENS, SV_INDICIES);
   }

   public static final String resolveStyleValue(int value) {
      int count = SV_TOKENS.length;

      for (int i = 0; i < count; i++) {
         if (SV_TOKENS[i] == value) {
            return SV_NAMES[i];
         }
      }

      return null;
   }

   private static final int resolveToken(String name, int startIndex, int endIndex, String[] names, int[] tokens, int[] indices) {
      char item = name.charAt(startIndex);
      if (item >= 'a' && item <= 'z') {
         item = (char)(item - 'a');
      } else if (item >= 'A' && item <= 'Z') {
         item = (char)(item - 'A');
      } else {
         if (item != '-') {
            return -1;
         }

         item = '\u001a';
      }

      int index = indices[item];
      int count = names.length;
      int length = endIndex - startIndex;
      if (item < 26) {
         count = indices[item + 1];
      }

      while (index < count) {
         if (names[index].length() == length && StringUtilities.regionMatches(names[index], true, 0, name, startIndex, length, 1701707776)) {
            return tokens[index];
         }

         index++;
      }

      return -1;
   }

   public static final boolean isNumberCharacter(char c) {
      return c >= '0' && c <= '9';
   }

   public static final boolean isHexadecimalCharacter(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
   }

   public static final boolean isNonAsciiCharacter(char c) {
      return c >= 128;
   }

   public static final boolean isNameStartCharacter(char c) {
      return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c >= 128;
   }

   public static final boolean isNameCharacter(char c) {
      return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c >= 128;
   }

   public static final boolean isStringCharacter(char c) {
      return c >= '(' && c <= '~' || c == ' ' || c == '!' || c >= '#' && c <= '&' || c == '\t' || c >= 128;
   }

   public static final boolean isURICharacter(char c) {
      return c >= '*' && c <= '~' || c == '!' || c >= '#' && c <= '&' || c >= 128;
   }

   public static final boolean isWhitespaceCharacter(char c) {
      switch (c) {
         case '\t':
         case '\n':
         case '\f':
         case '\r':
         case ' ':
            return true;
         default:
            return false;
      }
   }

   public static final boolean isNewLineCharacter(char c) {
      switch (c) {
         case '\t':
         case '\u000b':
            return false;
         case '\n':
         case '\f':
         case '\r':
         default:
            return true;
      }
   }
}
