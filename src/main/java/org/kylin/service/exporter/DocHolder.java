package org.kylin.service.exporter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

@Getter
@AllArgsConstructor
public class DocHolder {
    private XWPFDocument document;
    private ExportPropertites exportPropertites;

    public DocHolder() {
        this.document = new XWPFDocument();
    }

    public DocHolder(ExportPropertites exportPropertites) {
        this.document = new XWPFDocument();
        this.exportPropertites = exportPropertites;
    }
}
