package org.kylin.bean;

import lombok.*;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BaseCodeReq {
    private List<String> sequences;
    private String boldCodeSeq;
    private String inverseCodeSeq;
    private String gossipCodeSeq;
    private String sumTailValues;
    private String kdSeq;
    private List<WCode> wCodes;
    private Boolean freqSeted;

    public List<WCode> getwCodes() {
        return wCodes;
    }

    public BaseCodeReq setwCodes(List<WCode> wCodes) {
        this.wCodes = wCodes;
        return this;
    }

    public List<WCode> getWCodes() {
        return wCodes;
    }

    public BaseCodeReq setWCodes(List<WCode> wCodes) {
        this.wCodes = wCodes;
        return this;
    }

    public WCodeReq adaptToWCodeReq(){
        WCodeReq wCodeReq = new WCodeReq();
        wCodeReq.setWCodes(this.getWCodes());
        wCodeReq.setFreqSeted(this.getFreqSeted());

        return wCodeReq;
    }
}
