package com.zyd.kedaxunfeivoicetestapplication.voice.recog;

import java.util.List;

public class RecognizeBean {

    private String sn;
    private String ls;
    private String bg;
    private String ed;
    private List<WS> ws;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getLs() {
        return ls;
    }

    public void setLs(String ls) {
        this.ls = ls;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getEd() {
        return ed;
    }

    public void setEd(String ed) {
        this.ed = ed;
    }

    public List<WS> getWs() {
        return ws;
    }

    public void setWs(List<WS> ws) {
        this.ws = ws;
    }

    public static class WS {
        private String bg;
        private List<CW> cw;

        public String getBg() {
            return bg;
        }

        public void setBg(String bg) {
            this.bg = bg;
        }

        public List<CW> getCw() {
            return cw;
        }

        public void setCw(List<CW> cw) {
            this.cw = cw;
        }
    }

    public static class CW {
        private String sc;
        private String w;

        public String getSc() {
            return sc;
        }

        public void setSc(String sc) {
            this.sc = sc;
        }

        public String getW() {
            return w;
        }

        public void setW(String w) {
            this.w = w;
        }
    }

}
