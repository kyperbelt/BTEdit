package com.kyper.btedit;

public class Utils {
	
	public static final String DEFAULT_PROJ = "{\r\n" + //
			"	\"type\": \"BehaviorTree\",\r\n  \"ext\": \"btree\" \r\n}";

	public static final String DEFAULT_NODES = "{\r\n" + //
			"	\"composite\": {\r\n" + //
			"		\"Sequence\": {\r\n" + //
			"			\"properties\": {}\r\n" + //
			"		},\r\n" + //
			"		\"Selector\": {\r\n" + //
			"			\"properties\": {}\r\n" + //
			"		},\r\n" + //
			"		\"RandomSequence\": {\r\n" + //
			"			\"properties\": {}\r\n" + //
			"		},\r\n" + //
			"		\"RandomSelector\": {\r\n" + //
			"			\"properties\": {}\r\n" + //
			"		}\r\n" + //
			"\r\n" + //
			"	},\r\n" + //
			"	\"supplement\": {\r\n" + //
			"		\"Invert\": {\r\n" + //
			"			\"properties\": {}\r\n" + //
			"		},\r\n" + //
			"		\"Success\": {\r\n" + //
			"			\"properties\": {}\r\n" + //
			"		},\r\n" + //
			"		\"Repeat\": {\r\n" + //
			"			\"properties\": {\r\n" + //
			"				\"count\": {\r\n" + //
			"					\"type\": \"int\",\r\n" + //
			"					\"value\": \"-1\"\r\n" + //
			"				}\r\n" + //
			"			}\r\n" + //
			"		},\r\n" + //
			"		\"RepeatUntilFail\": {\r\n" + //
			"			\"properties\": {}\r\n" + //
			"		}\r\n" + //
			"	},\r\n" + //
			"	\"leaf\": {\r\n" + //
			"		\"Test\": {\r\n" + //
			"			\"properties\": {\r\n" + //
			"				\"property1\": {\r\n" + //
			"					\"type\": \"string\"\r\n" + //
			"				},\r\n" + //
			"				\"property2\": {\r\n" + //
			"					\"type\": \"bool\",\r\n" + //
			"					\"value\": \"false\"\r\n" + //
			"				}\r\n" + //
			"			}\r\n" + //
			"		},\r\n" + //
			"		\"Wait\": {\r\n" + //
			"			\"properties\": {\r\n" + //
			"				\"time\": {\r\n" + //
			"					\"type\": \"int\",\r\n" + //
			"					\"value\": \"1\"\r\n" + //
			"				}\r\n" + //
			"			}\r\n" + //
			"		}\r\n" + //
			"\r\n" + //
			"	}\r\n" + //
			"}";//
	
	public static String tab = "    ";
	
	public static String tab(int amount) {
		String r = "";
		for (int i = 0; i < amount; i++) {
			r+=tab;
		}
		return r;
	}
	
}
