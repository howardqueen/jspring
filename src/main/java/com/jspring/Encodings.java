package com.jspring;

public enum Encodings {
    ISO_8859_1("ISO-8859-1"),
    ASCII("ASCII"),
    GB2312("GB2312"),
    GBK("GBK"),
    Big5("BIG5"),
    UTF8("UTF-8"),
    UnicodeOrUTF16("UTF-16"),
    EUC_KR("EUC-KR"),
    EUC_JP("EUC-JP"),
    Shift_JIS("Shift_JIS");

    public final String value;
    private Encodings(String value){
    	this.value = value;
    }
    
    @Override
	public String toString(){
    	return value;
    }
    
    public static Encodings parse(String value){
    	for(Encodings e: values()){
    		if(e.value.equalsIgnoreCase(value)){
    			return e;
    		}
    	}
    	return UTF8;
    }

}
