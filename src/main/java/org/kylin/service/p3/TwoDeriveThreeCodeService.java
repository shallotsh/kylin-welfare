package org.kylin.service.p3;

import org.kylin.bean.p3.TwoDeriveThreeReq;
import org.kylin.bean.p5.WCode;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface TwoDeriveThreeCodeService {

    /**
     * 二组三码法，结果为组选三码
     * @param riddleArray
     * @return
     */
    List<WCode> shuffleCodes(List<List<Integer>> riddleArray);

    /**
     * 转直选
     *
     * @param wCodes
     * @return
     */
    List<WCode> convertToDirectCodes(List<WCode> wCodes);

    /**
     * 常规杀码算法
     *
     * @param req
     * @return
     */
    List<WCode> killCode(TwoDeriveThreeReq req);


    /**
     * 导出预测吗到文件
     *
     * @param req
     * @return
     * @throws IOException
     */
    Optional<String> exportCodeToFile(TwoDeriveThreeReq req)  throws IOException;
}
