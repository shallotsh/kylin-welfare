package org.kylin.bean.p2;

import lombok.extern.slf4j.Slf4j;
import org.kylin.bean.BaseCodeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.service.exporter.ExportPropertites;

import java.util.List;

@Slf4j
public class XCodeReq extends BaseCodeReq {

//    private List<String> sequences;
//    private String boldCodeSeq;
//    private String inverseCodeSeq;
//    private String gossipCodeSeq;
//    private String kdSeq;
//    private List<WCode> wCodes;
//    private Boolean freqSeted;
//    private List<WCode>[] wCodesArray;
//    private List<Integer> arrayIndexes;
    private List<XCodePair> xCodePairs;
    /**
     * 导出附加信息
     */
    private ExportPropertites exportPropertites;

    public XCodeReq() {
    }

//    public List<String> getSequences() {
//        return sequences;
//    }
//
//    public XCodeReq setSequences(List<String> sequences) {
//        this.sequences = sequences;
//        return this;
//    }
//
//    public String getBoldCodeSeq() {
//        return boldCodeSeq;
//    }
//
//    public XCodeReq setBoldCodeSeq(String boldCodeSeq) {
//        this.boldCodeSeq = boldCodeSeq;
//        return this;
//    }
//
//    public String getInverseCodeSeq() {
//        return inverseCodeSeq;
//    }
//
//    public XCodeReq setInverseCodeSeq(String inverseCodeSeq) {
//        this.inverseCodeSeq = inverseCodeSeq;
//        return this;
//    }
//
//    public String getGossipCodeSeq() {
//        return gossipCodeSeq;
//    }
//
//    public XCodeReq setGossipCodeSeq(String gossipCodeSeq) {
//        this.gossipCodeSeq = gossipCodeSeq;
//        return this;
//    }
//
//    public String getKdSeq() {
//        return kdSeq;
//    }
//
//    public XCodeReq setKdSeq(String kdSeq) {
//        this.kdSeq = kdSeq;
//        return this;
//    }
//
    public List<WCode> getwCodes() {
        return getWCodes();
    }

    public XCodeReq setwCodes(List<WCode> wCodes) {
        this.setWCodes(wCodes);
        return this;
    }
//
//    public Boolean getFreqSeted() {
//        return freqSeted;
//    }
//
//    public XCodeReq setFreqSeted(Boolean freqSeted) {
//        this.freqSeted = freqSeted;
//        return this;
//    }
//
    public List<XCodePair> getxCodePairs() {
        return xCodePairs;
    }

    public XCodeReq setxCodePairs(List<XCodePair> xCodePairs) {
        this.xCodePairs = xCodePairs;
        return this;
    }

    public ExportPropertites getExportPropertites() {
        return exportPropertites;
    }

    public void setExportPropertites(ExportPropertites exportPropertites) {
        this.exportPropertites = exportPropertites;
    }

    //    @Override
//    public String toString() {
//        return "XCodeReq{" +
//                "sequences=" + sequences +
//                ", boldCodeSeq='" + boldCodeSeq + '\'' +
//                ", inverseCodeSeq='" + inverseCodeSeq + '\'' +
//                ", gossipCodeSeq='" + gossipCodeSeq + '\'' +
//                ", wCodes=" + wCodes +
//                ", freqSeted=" + freqSeted +
//                ", xCodePairs=" + xCodePairs +
//                '}';
//    }
}
