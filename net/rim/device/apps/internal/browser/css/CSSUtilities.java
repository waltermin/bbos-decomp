package net.rim.device.apps.internal.browser.css;

import net.rim.device.api.util.StringUtilities;

public final class CSSUtilities {
   public static final int SP_AZIMUTH = 1;
   public static final int SP_BACKGROUND = 2;
   public static final int SP_BACKGROUND_ATTACHMENT = 3;
   public static final int SP_BACKGROUND_COLOR = 4;
   public static final int SP_BACKGROUND_IMAGE = 5;
   public static final int SP_BACKGROUND_POSITION = 6;
   public static final int SP_BACKGROUND_REPEAT = 7;
   public static final int SP_BORDER = 8;
   public static final int SP_BORDER_BOTTOM = 9;
   public static final int SP_BORDER_BOTTOM_COLOR = 10;
   public static final int SP_BORDER_BOTTOM_STYLE = 11;
   public static final int SP_BORDER_BOTTOM_WIDTH = 12;
   public static final int SP_BORDER_COLLAPSE = 13;
   public static final int SP_BORDER_COLOR = 14;
   public static final int SP_BORDER_LEFT = 15;
   public static final int SP_BORDER_LEFT_COLOR = 16;
   public static final int SP_BORDER_LEFT_STYLE = 17;
   public static final int SP_BORDER_LEFT_WIDTH = 18;
   public static final int SP_BORDER_RIGHT = 19;
   public static final int SP_BORDER_RIGHT_COLOR = 20;
   public static final int SP_BORDER_RIGHT_STYLE = 21;
   public static final int SP_BORDER_RIGHT_WIDTH = 22;
   public static final int SP_BORDER_SPACING = 23;
   public static final int SP_BORDER_STYLE = 24;
   public static final int SP_BORDER_TOP = 25;
   public static final int SP_BORDER_TOP_COLOR = 26;
   public static final int SP_BORDER_TOP_STYLE = 27;
   public static final int SP_BORDER_TOP_WIDTH = 28;
   public static final int SP_BORDER_WIDTH = 29;
   public static final int SP_BOTTOM = 30;
   public static final int SP_CAPTION_SIDE = 31;
   public static final int SP_CLEAR = 32;
   public static final int SP_CLIP = 33;
   public static final int SP_COLOR = 34;
   public static final int SP_CONTENT = 35;
   public static final int SP_COUNTER_INCREMENT = 36;
   public static final int SP_COUNTER_RESET = 37;
   public static final int SP_CUE = 38;
   public static final int SP_CUE_AFTER = 39;
   public static final int SP_CUE_BEFORE = 40;
   public static final int SP_CURSOR = 41;
   public static final int SP_DIRECTION = 42;
   public static final int SP_DISPLAY = 43;
   public static final int SP_ELEVATION = 44;
   public static final int SP_EMPTY_CELLS = 45;
   public static final int SP_FLOAT = 46;
   public static final int SP_FONT = 47;
   public static final int SP_FONT_FAMILY = 48;
   public static final int SP_FONT_SIZE = 49;
   public static final int SP_FONT_STYLE = 50;
   public static final int SP_FONT_VARIANT = 51;
   public static final int SP_FONT_WEIGHT = 52;
   public static final int SP_HEIGHT = 53;
   public static final int SP_LEFT = 54;
   public static final int SP_LETTER_SPACING = 55;
   public static final int SP_LINE_HEIGHT = 56;
   public static final int SP_LIST_STYLE = 57;
   public static final int SP_LIST_STYLE_IMAGE = 58;
   public static final int SP_LIST_STYLE_POSITION = 59;
   public static final int SP_LIST_STYLE_TYPE = 60;
   public static final int SP_MARGIN = 61;
   public static final int SP_MARGIN_BOTTOM = 62;
   public static final int SP_MARGIN_LEFT = 63;
   public static final int SP_MARGIN_RIGHT = 64;
   public static final int SP_MARGIN_TOP = 65;
   public static final int SP_MAX_HEIGHT = 66;
   public static final int SP_MAX_WIDTH = 67;
   public static final int SP_MIN_HEIGHT = 68;
   public static final int SP_MIN_WIDTH = 69;
   public static final int SP_ORPHANS = 70;
   public static final int SP_OUTLINE = 71;
   public static final int SP_OUTLINE_COLOR = 72;
   public static final int SP_OUTLINE_STYLE = 73;
   public static final int SP_OUTLINE_WIDTH = 74;
   public static final int SP_OVERFLOW = 75;
   public static final int SP_PADDING = 76;
   public static final int SP_PADDING_BOTTOM = 77;
   public static final int SP_PADDING_LEFT = 78;
   public static final int SP_PADDING_RIGHT = 79;
   public static final int SP_PADDING_TOP = 80;
   public static final int SP_PAGE_BREAK_AFTER = 81;
   public static final int SP_PAGE_BREAK_BEFORE = 82;
   public static final int SP_PAGE_BREAK_INSIDE = 83;
   public static final int SP_PAUSE = 84;
   public static final int SP_PAUSE_AFTER = 85;
   public static final int SP_PAUSE_BEFORE = 86;
   public static final int SP_PITCH = 87;
   public static final int SP_PITCH_RANGE = 88;
   public static final int SP_PLAY_DURING = 89;
   public static final int SP_POSITION = 90;
   public static final int SP_QUOTES = 91;
   public static final int SP_RICHNESS = 92;
   public static final int SP_RIGHT = 93;
   public static final int SP_SPEAK = 94;
   public static final int SP_SPEAK_HEADER = 95;
   public static final int SP_SPEAK_NUMERAL = 96;
   public static final int SP_SPEAK_PUNCTUATION = 97;
   public static final int SP_SPEECH_RATE = 98;
   public static final int SP_STRESS = 99;
   public static final int SP_TABLE_LAYOUT = 100;
   public static final int SP_TEXT_ALIGN = 101;
   public static final int SP_TEXT_DECORATION = 102;
   public static final int SP_TEXT_INDENT = 103;
   public static final int SP_TEXT_TRANSFORM = 104;
   public static final int SP_TOP = 105;
   public static final int SP_UNICODE_BIDI = 106;
   public static final int SP_VERTICAL_ALIGN = 107;
   public static final int SP_VISIBILITY = 108;
   public static final int SP_VOICE_FAMILY = 109;
   public static final int SP_VOLUME = 110;
   public static final int SP_WHITE_SPACE = 111;
   public static final int SP_WIDOWS = 112;
   public static final int SP_WIDTH = 113;
   public static final int SP_WORD_SPACING = 114;
   public static final int SP_Z_INDEX = 115;
   public static final int SP__WAP_ACCESSKEY = 116;
   public static final int SP__WAP_INPUT_FORMAT = 117;
   public static final int SP__WAP_INPUT_REQUIRED = 118;
   public static final int SP__WAP_MARQUEE_DIR = 119;
   public static final int SP__WAP_MARQUEE_LOOP = 120;
   public static final int SP__WAP_MARQUEE_SPEED = 121;
   public static final int SP__WAP_MARQUEE_STYLE = 122;
   public static final int SP_NUM_VALUES = 123;
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
   public static final int SV_UNDEFINED = 0;
   public static final int SV_DEFINED = 1;
   public static final int SV_STRING = 2;
   public static final int SV_URI = 3;
   public static final int SV_FUNCTION = 4;
   public static final int SV_COLOR = 5;
   public static final int SV_NUMBER = 6;
   public static final int SV_REAL = 7;
   public static final int SV_PERCENTAGE = 16;
   public static final int SV_EM = 32;
   public static final int SV_EX = 48;
   public static final int SV_PX = 64;
   public static final int SV_IN = 80;
   public static final int SV_CM = 96;
   public static final int SV_MM = 112;
   public static final int SV_PT = 128;
   public static final int SV_PC = 144;
   public static final int SV_DEG = 160;
   public static final int SV_GRAD = 176;
   public static final int SV_RAD = 192;
   public static final int SV_MS = 208;
   public static final int SV_S = 224;
   public static final int SV_HZ = 240;
   public static final int SV_KHZ = 256;
   public static final int SV_IMPORTANT = 512;
   public static final int SV_TYPE_MASK = 15;
   public static final int SV_UNITS_MASK = 496;
   public static final int SV_FLAGS_MASK = 511;
   public static final int SV_ABOVE = 1;
   public static final int SV_ABSOLUTE = 2;
   public static final int SV_ALTERNATE = 3;
   public static final int SV_ALWAYS = 4;
   public static final int SV_ARMENIAN = 5;
   public static final int SV_AUTO = 6;
   public static final int SV_AVOID = 7;
   public static final int SV_BASELINE = 8;
   public static final int SV_BEHIND = 9;
   public static final int SV_BELOW = 10;
   public static final int SV_BIDI_OVERRIDE = 11;
   public static final int SV_BLINK = 12;
   public static final int SV_BLOCK = 13;
   public static final int SV_BOLD = 14;
   public static final int SV_BOLDER = 15;
   public static final int SV_BOTH = 16;
   public static final int SV_BOTTOM = 17;
   public static final int SV_CAPITALIZE = 18;
   public static final int SV_CAPTION = 19;
   public static final int SV_CENTER = 20;
   public static final int SV_CENTER_LEFT = 21;
   public static final int SV_CENTER_RIGHT = 22;
   public static final int SV_CHILD = 23;
   public static final int SV_CIRCLE = 24;
   public static final int SV_CLOSE_QUOTE = 25;
   public static final int SV_CODE = 26;
   public static final int SV_COLLAPSE = 27;
   public static final int SV_CONTINUOUS = 28;
   public static final int SV_CROSSHAIR = 29;
   public static final int SV_CURSIVE = 30;
   public static final int SV_DASHED = 31;
   public static final int SV_DECIMAL = 32;
   public static final int SV_DECIMAL_LEADING_ZERO = 33;
   public static final int SV_DEFAULT = 34;
   public static final int SV_DIGITS = 35;
   public static final int SV_DISC = 36;
   public static final int SV_DOTTED = 37;
   public static final int SV_DOUBLE = 38;
   public static final int SV_E_RESIZE = 39;
   public static final int SV_EMBED = 40;
   public static final int SV_FALSE = 41;
   public static final int SV_FANTASY = 42;
   public static final int SV_FAR_LEFT = 43;
   public static final int SV_FAR_RIGHT = 44;
   public static final int SV_FAST = 45;
   public static final int SV_FASTER = 46;
   public static final int SV_FEMALE = 47;
   public static final int SV_FIXED = 48;
   public static final int SV_GEORGIAN = 49;
   public static final int SV_GROOVE = 50;
   public static final int SV_HELP = 51;
   public static final int SV_HIDDEN = 52;
   public static final int SV_HIDE = 53;
   public static final int SV_HIGH = 54;
   public static final int SV_HIGHER = 55;
   public static final int SV_ICON = 56;
   public static final int SV_INFINITE = 57;
   public static final int SV_INHERIT = 58;
   public static final int SV_INLINE = 59;
   public static final int SV_INLINE_BLOCK = 60;
   public static final int SV_INLINE_TABLE = 61;
   public static final int SV_INSET = 62;
   public static final int SV_INSIDE = 63;
   public static final int SV_INVERT = 64;
   public static final int SV_ITALIC = 65;
   public static final int SV_JUSTIFY = 66;
   public static final int SV_LARGE = 67;
   public static final int SV_LARGER = 68;
   public static final int SV_LEFT = 69;
   public static final int SV_LEFT_SIDE = 70;
   public static final int SV_LEFTWARDS = 71;
   public static final int SV_LEVEL = 72;
   public static final int SV_LIGHTER = 73;
   public static final int SV_LINE_THROUGH = 74;
   public static final int SV_LIST_ITEM = 75;
   public static final int SV_LOUD = 76;
   public static final int SV_LOW = 77;
   public static final int SV_LOWER = 78;
   public static final int SV_LOWER_GREEK = 79;
   public static final int SV_LOWER_LATIN = 80;
   public static final int SV_LOWER_ROMAN = 81;
   public static final int SV_LOWERCASE = 82;
   public static final int SV_LTR = 83;
   public static final int SV_MALE = 84;
   public static final int SV_MEDIUM = 85;
   public static final int SV_MENU = 86;
   public static final int SV_MESSAGE_BOX = 87;
   public static final int SV_MIDDLE = 88;
   public static final int SV_MIX = 89;
   public static final int SV_MONOSPACE = 90;
   public static final int SV_MOVE = 91;
   public static final int SV_N_RESIZE = 92;
   public static final int SV_NE_RESIZE = 93;
   public static final int SV_NO_CLOSE_QUOTE = 94;
   public static final int SV_NO_OPEN_QUOTE = 95;
   public static final int SV_NO_REPEAT = 96;
   public static final int SV_NONE = 97;
   public static final int SV_NORMAL = 98;
   public static final int SV_NOWRAP = 99;
   public static final int SV_NW_RESIZE = 100;
   public static final int SV_OBLIQUE = 101;
   public static final int SV_ONCE = 102;
   public static final int SV_OPEN_QUOTE = 103;
   public static final int SV_OUTSET = 104;
   public static final int SV_OUTSIDE = 105;
   public static final int SV_OVERLINE = 106;
   public static final int SV_POINTER = 107;
   public static final int SV_PRE = 108;
   public static final int SV_PRE_LINE = 109;
   public static final int SV_PRE_WRAP = 110;
   public static final int SV_PROGRESS = 111;
   public static final int SV_RELATIVE = 112;
   public static final int SV_REPEAT = 113;
   public static final int SV_REPEAT_X = 114;
   public static final int SV_REPEAT_Y = 115;
   public static final int SV_RIDGE = 116;
   public static final int SV_RIGHT = 117;
   public static final int SV_RIGHT_SIDE = 118;
   public static final int SV_RIGHTWARDS = 119;
   public static final int SV_RTL = 120;
   public static final int SV_RUN_IN = 121;
   public static final int SV_S_RESIZE = 122;
   public static final int SV_SANS_SERIF = 123;
   public static final int SV_SCROLL = 124;
   public static final int SV_SE_RESIZE = 125;
   public static final int SV_SEPARATE = 126;
   public static final int SV_SERIF = 127;
   public static final int SV_SHOW = 128;
   public static final int SV_SILENT = 129;
   public static final int SV_SLIDE = 130;
   public static final int SV_SLOW = 131;
   public static final int SV_SLOWER = 132;
   public static final int SV_SMALL = 133;
   public static final int SV_SMALL_CAPS = 134;
   public static final int SV_SMALL_CAPTION = 135;
   public static final int SV_SMALLER = 136;
   public static final int SV_SOFT = 137;
   public static final int SV_SOLID = 138;
   public static final int SV_SPELL_OUT = 139;
   public static final int SV_SQUARE = 140;
   public static final int SV_STATIC = 141;
   public static final int SV_STATUS_BAR = 142;
   public static final int SV_SUB = 143;
   public static final int SV_SUPER = 144;
   public static final int SV_SW_RESIZE = 145;
   public static final int SV_TABLE = 146;
   public static final int SV_TABLE_CAPTION = 147;
   public static final int SV_TABLE_CELL = 148;
   public static final int SV_TABLE_COLUMN = 149;
   public static final int SV_TABLE_COLUMN_GROUP = 150;
   public static final int SV_TABLE_FOOTER_GROUP = 151;
   public static final int SV_TABLE_HEADER_GROUP = 152;
   public static final int SV_TABLE_ROW = 153;
   public static final int SV_TABLE_ROW_GROUP = 154;
   public static final int SV_TEXT = 155;
   public static final int SV_TEXT_BOTTOM = 156;
   public static final int SV_TEXT_TOP = 157;
   public static final int SV_THICK = 158;
   public static final int SV_THIN = 159;
   public static final int SV_TOP = 160;
   public static final int SV_TRANSPARENT = 161;
   public static final int SV_TRUE = 162;
   public static final int SV_UNDERLINE = 163;
   public static final int SV_UPPER_LATIN = 164;
   public static final int SV_UPPER_ROMAN = 165;
   public static final int SV_UPPERCASE = 166;
   public static final int SV_VISIBLE = 167;
   public static final int SV_W_RESIZE = 168;
   public static final int SV_WAIT = 169;
   public static final int SV_X_FAST = 170;
   public static final int SV_X_HIGH = 171;
   public static final int SV_X_LARGE = 172;
   public static final int SV_X_LOUD = 173;
   public static final int SV_X_LOW = 174;
   public static final int SV_X_SLOW = 175;
   public static final int SV_X_SMALL = 176;
   public static final int SV_X_SOFT = 177;
   public static final int SV_XX_LARGE = 178;
   public static final int SV_XX_SMALL = 179;
   public static final int SV__WAP_MARQUEE = 180;
   public static final int SV_LOWER_ALPHA = 181;
   public static final int SV_UPPER_ALPHA = 182;
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
