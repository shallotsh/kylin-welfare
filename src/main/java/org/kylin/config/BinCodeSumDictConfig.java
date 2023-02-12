package org.kylin.config;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.util.TransferUtil;
import org.kylin.util.WCodeUtils;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class BinCodeSumDictConfig {
    private static final int DIM_3 = 3;
    private static Map<Integer/*二码和*/, List<String>/*3d码列表*/> dict = new HashMap<>();

    static {
        dict.put(0, Arrays.asList("000","118","226","244","299","334","488","550","668","677","100","119","155","200","228","255","300","337","355","400","446","455","500","555","466","556","600","377","557","700","288","558","800","199","559","900","190","280","370","460","128","146","129","237","246","139","238","346","149","248","347","159","258","357","456","169","268","367","179","278","467","189","378","468","289","379","469"));
        dict.put(1, Arrays.asList("299","100","119","155","227","335","344","399","588","669","777","110","229","447","556","566","477","388","190","389","479","569","290","380","470","560","129","138","147","156","120","238","247","256","130","239","347","356","140","249","348","456","150","259","358","457","160","269","368","467","170","279","378","567","180","289","478","568"));
        dict.put(2, Arrays.asList("118","488","668","119","399","669","110","200","228","255","336","444","499","660","688","778","111","166","112","220","266","113","339","366","114","448","466","115","557","566","116","666","117","577","667","280","389","578","290","489","579","390","480","570","120","139","148","157","239","248","257","230","348","357","240","349","457","250","359","458","260","369","468","567","270","379","478"));
        dict.put(3, Arrays.asList("677","588","499","111","166","229","300","337","355","445","599","779","788","112","122","330","449","558","667","127","370","479","578","128","380","489","678","129","390","589","679","120","490","580","670","130","149","158","167","230","249","258","267","123","349","358","367","124","340","458","467","125","350","459","567","126","360","469","568"));
        dict.put(4, Arrays.asList("226","668","677","227","777","228","688","778","229","599","779","112","220","266","338","400","446","455","699","770","888","113","122","177","222","277","133","223","377","224","440","477","225","559","577","136","460","569","137","470","579","678","138","480","589","139","490","689","130","590","680","140","159","168","123","240","259","268","340","359","368","134","459","468","135","450","568"));
        dict.put(5, Arrays.asList("550","669","778","788","699","113","122","177","339","366","447","500","555","799","889","114","223","233","144","145","235","569","578","146","236","560","678","147","237","570","679","148","238","580","689","149","239","590","789","140","230","690","780","123","150","169","178","124","250","269","278","134","350","369","378","234","450","469","478"));
        dict.put(6, Arrays.asList("244","334","488","155","335","588","336","660","688","337","779","788","338","888","339","799","899","115","133","188","224","233","288","333","388","145","460","479","245","560","579","156","246","679","157","247","670","158","248","680","789","159","249","690","240","790","150","124","160","179","125","260","279","135","234","360","379"));
        dict.put(7, Arrays.asList("334","344","255","166","770","889","899","115","133","188","223","377","449","557","566","700","999","116","225","136","235","370","389","146","245","470","489","156","345","570","589","256","346","670","689","167","257","347","789","168","258","348","780","169","259","349","790","160","250","340","890","125","134"," 170","189","126","234","270","289"));
        dict.put(8, Arrays.asList("226","244","299","335","344","399","444","499","355","445","599","266","446","699","177","447","799","448","880","899","449","999","116","224","233","288","440","477","558","666","800","990","117","144","199","127","235","280","137","236","380","147","246","345","482","157","256","580","167","356","680","267","357","780","178","268","358","179","269","359","890","170","260","350","126","135","180"));
        dict.put(9, Arrays.asList("118","227","336","445","455","366","277","188","990","117","144","199","225","333","388","559","577","667","900","127","136","145","190","128","236","245","290","138","237","345","390","148","247","346","490","158","257","356","590","168","267","456","690","178","367","457","790","278","368","458","890","189","279","369","459","180","270","360","450"));
    }

    public List<WCode> getWCodesByBinCodes(List<Integer> binSumCodes){
        if(CollectionUtils.isEmpty(binSumCodes)){
            return Collections.emptyList();
        }
        List<Integer> tempBinSumCodes = binSumCodes.stream().distinct().collect(Collectors.toList());
        List<WCode> wCodes = new ArrayList<>();

        // 组码
        for(Integer sumCode : tempBinSumCodes){
            wCodes.addAll(buildWCodes(DIM_3, dict.get(sumCode)));
        }
        // 去重，防御
        return WCodeUtils.mergeCodes(wCodes, false);
    }

    private List<WCode> buildWCodes(int dim,List<String> codes){
        if(CollectionUtils.isEmpty(codes)){
            return Collections.emptyList();
        }
        if(codes.get(0).length() != dim){
            throw new RuntimeException("组码维度不匹配");
        }

        return codes.stream()
                .map(codeString -> new WCode(dim, TransferUtil.toIntegerList(codeString)))
                .collect(Collectors.toList());

    }
}
