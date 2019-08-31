package org.kylin.service.exporter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

@Getter
@AllArgsConstructor
public class DocHolder {
    private XWPFDocument document;

    public DocHolder() {
        this.document = new XWPFDocument();
    }
}
