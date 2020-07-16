package com.lhq.superboot.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.lhq.superboot.exception.SuperBootException;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Description: 使用阿里easyExcel导出excel，进一步封装导出方法
 * @author: lihaoqi
 * @date: 2019年11月20日 下午4:17:18
 * @version: v1.0.0
 */
@Component
public class ExportExcelUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExportExcelUtils.class);

    /**
     * 一个sheet页最大放置条数
     **/
    private static final int SHEET_MAX_LONG = 10000;
    /**
     * 处理数据最大值
     **/
    private static final int DATA_MAX_LONG = 200000;
    /**
     * 默认sheet名
     **/
    private static final String DEFAULT_SHEET_NAME = "lhq_sheet";

    /**
     * 默认样式
     **/
    private HorizontalCellStyleStrategy defaultStrategy;

    public ExportExcelUtils() {
        // 头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        headWriteCellStyle.setWrapped(Boolean.TRUE);
        // 行样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setWrapped(Boolean.TRUE);
        // 垂直居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 默认样式创建
        defaultStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    /**
     * @param response
     * @param fileName
     * @param titleList
     * @param dataList
     * @Description: 通过response，导出excel文件，使用缺省数据
     */
    public void downloadExcel(HttpServletResponse response, String fileName, List<List<String>> titleList,
                              List<List<Object>> dataList) {
        downloadExcel(response, fileName, null, titleList, dataList, null, null);
    }

    /**
     * @param response
     * @param fileName
     * @param titleMap    头信息map，例如：{"name":"名字", "id":"编码"}
     * @param allDataList 所有信息的数据，可能出了name与id还有别的字段
     * @param isTitleMap  是否按照titleMap的顺序导出，是则按照title顺序，否则默认为dataList的顺序导出
     * @Description: 通过response导出excel文件，使用缺省数据，并只导出titleMap存在的字段
     */
    public void downloadExcel(HttpServletResponse response, String fileName, LinkedHashMap<String, Object> titleMap,
                              List<LinkedHashMap<String, Object>> allDataList, boolean isTitleMap) {
        if (allDataList.isEmpty()) {
            logger.info("[downloadExcel] -> 导出结果集为空");
            throw new SuperBootException("导出结果集为空");
        }
        int titleSize = titleMap.keySet().size();
        int dataSize = allDataList.size();
        if (dataSize > DATA_MAX_LONG) {
            logger.error("[downloadExcel] -> 该方法不支持20w以上的数据处理导出");
            throw new SuperBootException("该方法不支持20w以上的数据处理导出");
        }
        List<List<String>> titleList = new ArrayList<>(titleSize);
        List<List<Object>> dataList = new ArrayList<>(dataSize);

        getTitleAndData(isTitleMap, titleSize, titleMap, titleList, allDataList, dataList);
        downloadExcel(response, fileName, null, titleList, dataList, null, null);
    }

    /**
     * @param response
     * @param fileName
     * @param strategy
     * @param titleList
     * @param dataList
     * @param sheetName
     * @param sheetMaxLong
     * @Description: 通过response导出excel文件，传入样式sheet名等数据
     */
    public void downloadExcel(HttpServletResponse response, String fileName, HorizontalCellStyleStrategy strategy,
                              List<List<String>> titleList, List<List<Object>> dataList, String sheetName, Integer sheetMaxLong) {
        long startTime = System.currentTimeMillis();
        ExcelWriter excelWriter = null;
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            fileName = fileName == null ? "" : fileName + DateConvertUtils.dateTimeToString(new Date(), "yyyyMMddHHmmss");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ".xlsx");

            ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(response.getOutputStream());
            excelWriter = getExcelWriter(strategy, excelWriterBuilder, titleList, dataList, sheetName, sheetMaxLong);
            // 刷新缓冲
            response.flushBuffer();
        } catch (Exception e) {
            logger.debug("[downloadExcel] -> 导出异常:{}", e.getMessage());
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
            long endTime = System.currentTimeMillis();
            logger.debug("[downloadExcel] -> 导出用时 " + (endTime - startTime) + "ms");
        }
    }

    /**
     * @param filePath
     * @param fileName
     * @param titleList
     * @param dataList
     * @throws IOException
     * @Description: 通过文件路径，导出excel文件，使用缺省数据
     */
    public void downloadExcel(String filePath, String fileName, List<List<String>> titleList, List<List<Object>> dataList)
            throws IOException {
        downloadExcel(filePath, fileName, null, titleList, dataList, null, null);
    }

    /**
     * @param filePath
     * @param fileName
     * @param titleMap    头信息map，例如：{"name":"名字", "id":"编码"}
     * @param allDataList 所有信息的数据，可能出了name与id还有别的字段
     * @param isTitleMap  是否按照titleMap的顺序导出，是则按照title顺序，否则默认为dataList的顺序导出
     * @throws IOException
     * @Description: 通过文件路径，导出excel文件，使用缺省数据，并只导出titleMap存在的字段
     */
    public void downloadExcel(String filePath, String fileName, LinkedHashMap<String, Object> titleMap,
                              List<LinkedHashMap<String, Object>> allDataList, boolean isTitleMap) throws IOException {
        if (allDataList.isEmpty()) {
            logger.info("[downloadExcel] -> 导出结果集为空");
            throw new SuperBootException("导出结果集为空");
        }
        int titleSize = titleMap.keySet().size();
        int dataSize = allDataList.size();
        if (dataSize > DATA_MAX_LONG) {
            logger.error("[downloadExcel] -> 该方法不支持20w以上的数据处理导出");
            throw new SuperBootException("该方法不支持20w以上的数据处理导出");
        }
        List<List<String>> titleList = new ArrayList<>(titleSize);
        List<List<Object>> dataList = new ArrayList<>(dataSize);

        getTitleAndData(isTitleMap, titleSize, titleMap, titleList, allDataList, dataList);
        downloadExcel(filePath, fileName, null, titleList, dataList, null, null);
    }

    /**
     * @param filePath
     * @param fileName
     * @param strategy
     * @param titleList
     * @param dataList
     * @param sheetName
     * @param sheetMaxLong
     * @throws IOException
     * @Description: 通过FileOutputStream导出excel文件，传入样式sheet名等数据
     */
    public void downloadExcel(String filePath, String fileName, HorizontalCellStyleStrategy strategy,
                              List<List<String>> titleList, List<List<Object>> dataList, String sheetName, Integer sheetMaxLong)
            throws IOException {
        long startTime = System.currentTimeMillis();
        // 指定文件输出位置
        fileName = fileName == null ? "" : fileName + DateConvertUtils.dateTimeToString(new Date(), "yyyyMMddHHmmss");
        ExcelWriter excelWriter = null;
        try (OutputStream outputStream = new FileOutputStream(filePath + fileName + ".xlsx")) {
            ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream);
            excelWriter = getExcelWriter(strategy, excelWriterBuilder, titleList, dataList, sheetName, sheetMaxLong);
        } catch (Exception e) {
            logger.debug("[downloadExcel] -> 导出异常:{}", e.getMessage());
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
            long endTime = System.currentTimeMillis();
            logger.debug("[downloadExcel] -> 导出用时 " + (endTime - startTime) + "ms");
        }
    }

    /**
     * @param strategy
     * @param excelWriterBuilder
     * @param titleList
     * @param dataList
     * @param sheetName
     * @param sheetMaxLong
     * @return
     * @Description: 获取ExcelWriter
     */
    @SuppressWarnings("unchecked")
    public ExcelWriter getExcelWriter(HorizontalCellStyleStrategy strategy, ExcelWriterBuilder excelWriterBuilder,
                                      List<List<String>> titleList, List<List<Object>> dataList, String sheetName, Integer sheetMaxLong) {
        // 默认样式
        if (strategy == null) {
            strategy = defaultStrategy;
        }
        // 注册样式并创建excelWriter对象
        ExcelWriter excelWriter = excelWriterBuilder.registerWriteHandler(strategy).build();

        // 默认sheet分页
        if (sheetMaxLong == null || sheetMaxLong <= 0 || sheetMaxLong >= Short.MAX_VALUE) {
            sheetMaxLong = SHEET_MAX_LONG;
        }

        // 默认sheet名
        if (StringUtils.isEmpty(sheetName)) {
            sheetName = DEFAULT_SHEET_NAME;
        }

        // 数据大于分页时
        int dataSize = dataList.size();
        if (dataSize > sheetMaxLong) {
            List<List<?>> pageDataList = ListUtils.listGrouping(dataList, sheetMaxLong);
            for (int i = 0; i < pageDataList.size(); i++) {
                excelWriter = getExcelWriter(excelWriter, i, titleList, (List<List<Object>>) pageDataList.get(i),
                        sheetName + (i + 1));
            }
        } else {
            excelWriter = getExcelWriter(excelWriter, 0, titleList, dataList, sheetName);
        }
        return excelWriter;
    }

    /**
     * @param excelWriter
     * @param i
     * @param titleList
     * @param dataList
     * @param sheetName
     * @return
     * @Description: 分sheet生成excelWriter
     */
    private ExcelWriter getExcelWriter(ExcelWriter excelWriter, int i, List<List<String>> titleList, List<List<Object>> dataList,
                                       String sheetName) {
        // 行信息写入
        WriteSheet sheet = new WriteSheet();
        sheet.setSheetNo(i + 1);
        sheet.setSheetName(sheetName);
        sheet.setAutoTrim(Boolean.TRUE);

        // 头信息写入
        if (titleList != null && !titleList.isEmpty()) {
            WriteTable title = new WriteTable();
            title.setTableNo(i + 1);
            title.setHead(titleList);
            title.setAutoTrim(Boolean.TRUE);
            excelWriter.write(dataList, sheet, title);
        } else {
            excelWriter.write(dataList, sheet);
        }
        return excelWriter;
    }

    /**
     * @param isTitleMap
     * @param titleSize
     * @param titleMap
     * @param titleList
     * @param allDataList
     * @param dataList
     * @Description: 获取title与data信息
     */
    private void getTitleAndData(boolean isTitleMap, int titleSize, LinkedHashMap<String, Object> titleMap,
                                 List<List<String>> titleList, List<LinkedHashMap<String, Object>> allDataList, List<List<Object>> dataList) {
        // title信息的顺序
        Set<String> titleOrder = titleMap.keySet();
        // 数据信息的顺序
        Set<String> dataOrder = allDataList.get(0).keySet();

        // 获取title与data信息
        if (isTitleMap) {
            for (String data : titleOrder) {
                List<String> titleColumn = new ArrayList<>(1);
                titleColumn.add((String) titleMap.get(data));
                titleList.add(titleColumn);
            }
            for (LinkedHashMap<String, Object> map : allDataList) {
                List<Object> data = new ArrayList<>(titleSize);
                for (String key : titleOrder) {
                    data.add(map.get(key));
                }
                dataList.add(data);
            }
        } else {
            for (String data : dataOrder) {
                if (titleOrder.contains(data)) {
                    List<String> titleColumn = new ArrayList<>(1);
                    titleColumn.add((String) titleMap.get(data));
                    titleList.add(titleColumn);
                }
            }
            for (LinkedHashMap<String, Object> map : allDataList) {
                List<Object> data = new ArrayList<>(titleSize);
                for (String key : dataOrder) {
                    if (titleOrder.contains(key)) {
                        data.add(map.get(key));
                    }
                }
                dataList.add(data);
            }
        }
    }

}
