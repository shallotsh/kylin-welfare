package org.kylin.service.exporter;


import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.util.CommonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

@Slf4j
public abstract class AbstractDocumentExporter implements IDocExportTool<WCodeReq> {

    private static final String DEFAULT_DOC_TITLE = "《我要发·排列5》福彩3D预测";
    protected static final String BASE_PATH = "/var/attachment/";

    protected ThreadLocal<String> titleLocal = new ThreadLocal<>();

    @Override
    public void writeTitleAsDefaultFormat(DocHolder docHolder, String title) {
        XWPFParagraph header = docHolder.getDocument().createParagraph();
        header.setVerticalAlignment(TextAlignment.TOP);
        header.setWordWrap(true);
        header.setAlignment(ParagraphAlignment.CENTER);

        String printTitle = (StringUtils.isBlank(title) ? DEFAULT_DOC_TITLE: title) + LocalDate.now();

        XWPFRun hr1 = header.createRun();
        hr1.setText(printTitle);
        hr1.setBold(true);
        hr1.setUnderline(UnderlinePatterns.DOT_DOT_DASH);
        hr1.setTextPosition(12);
        hr1.setFontSize(18);
        hr1.addBreak();
        titleLocal.set(printTitle);
    }

    @Override
    public String exportDocAsFile(DocHolder docHolder, String fullPath, String fileName) throws IOException {

        String targetDirName = getTargetFilePath(fullPath);
        String subDirectory = CommonUtils.getCurrentTimeString().substring(0,6);
        String targetPath = CommonUtils.createIfNotExist(targetDirName, subDirectory);
        fileName = (StringUtils.isBlank(fileName) ? (CommonUtils.getCurrentTimeString() + "_" + new Random().nextInt(100)) : (fileName )) + ".docx";
        String exportFileName = targetPath + File.separator + fileName;

        // save data
        @Cleanup FileOutputStream out = new FileOutputStream(exportFileName);
        docHolder.getDocument().write(out);

        titleLocal.remove();

        return fileName;
    }

    private String getTargetFilePath(String fullPath){

        return Optional.ofNullable(fullPath)
                .map(path -> path.endsWith("/")?path : path + "/")
                .orElse(BASE_PATH);
    }

}
