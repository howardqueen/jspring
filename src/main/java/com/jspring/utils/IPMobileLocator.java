package com.jspring.utils;

import com.jspring.Encodings;
import com.jspring.Strings;
import com.jspring.net.WebClient;
import com.jspring.net.WebClient.RequestProperties;
import com.jspring.net.WebClient.WebClientArgs;

public class IPMobileLocator {

    private IPMobileLocator() {
    }

    public static class IPLocation {
        public String ip;
        public String country;
        public String area;
        public String region;
        public String city;
        public String county;
        public String isp;
    }

    private static Strings country = Strings.newSubstringAnalyzer("\"country\":\"", '"');
    private static Strings area = Strings.newSubstringAnalyzer("\"area\":\"", '"');
    private static Strings region = Strings.newSubstringAnalyzer("\"region\":\"", '"');
    private static Strings city = Strings.newSubstringAnalyzer("\"city\":\"", '"');
    private static Strings county = Strings.newSubstringAnalyzer("\"county\":\"", '"');
    private static Strings isp = Strings.newSubstringAnalyzer("\"isp\":\"", '"');

    public static IPLocation getIPLocation(String ip) {
        WebClientArgs wa = new WebClientArgs("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
        wa.addProperty(RequestProperties.userAgent, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537");
        String json = WebClient.get(wa);
        String[] t = Strings.getSubstrings(json, country, area, region, city, county, isp);
        IPLocation r = new IPLocation();
        r.ip = ip;
        r.country = Strings.decodeUnicode(t[0]);
        r.area = Strings.decodeUnicode(t[1]);
        r.region = Strings.decodeUnicode(t[2]);
        r.city = Strings.decodeUnicode(t[3]);
        r.county = Strings.decodeUnicode(t[4]);
        r.isp = Strings.decodeUnicode(t[5]);
        return r;
    }

    public static class MobileLocation {
        public String mobile;
        public String province;
        public String catName;
        public String carrier;
    }

    private static Strings province = Strings.newSubstringAnalyzer("province:'", "'");
    private static Strings catName = Strings.newSubstringAnalyzer("catName:'", "'");
    private static Strings carrier = Strings.newSubstringAnalyzer("carrier:'", "'");

    public static MobileLocation getMobileLocation(String mobile, Encodings readAsEncoding) {
        //https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=15850781443
        /*
        __GetZoneResult_ = {
            mts:'1585078',
            province:'江苏',
            catName:'中国移动',
            telString:'15850781443',
        	areaVid:'30511',
        	ispVid:'3236139',
        	carrier:'江苏移动'
        }
         */
        WebClientArgs wa = new WebClientArgs(
                "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=" + mobile.substring(0, 7) + "0000");
        wa.addProperty(RequestProperties.userAgent, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537");
        wa.addProperty(RequestProperties.charset, Encodings.GBK.value);
        wa.responseEncoding = Encodings.GBK;
        wa.readAsEncoding = readAsEncoding;
        String json = WebClient.get(wa);
        String[] t = Strings.getSubstrings(json, province, catName, carrier);
        MobileLocation r = new MobileLocation();
        r.mobile = mobile;
        r.province = t[0];
        r.catName = t[1];
        r.carrier = t[2];
        return r;
    }

}