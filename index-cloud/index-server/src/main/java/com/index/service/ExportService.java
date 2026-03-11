package com.index.service;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.index.entity.IndexConfig;
import com.index.entity.IndexData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Excel 导出服务 - 双表结构版本
 */
@Service
public class ExportService {

    /**
     * 导出指标数据到 Excel
     */
    public byte[] exportToExcel(List<IndexData> dataList, String period) throws IOException {
        // 按配置排序
        List<IndexData> sortedData = sortDataByConfig(dataList);
        
        // 转换为导出数据
        List<Map<String, Object>> exportList = convertToExportData(sortedData);
        
        // 定义列
        List<ExcelExportEntity> entityList = createExportColumns();
        
        // 导出参数
        ExportParams exportParams = new ExportParams("大类资产细项结构表（考核）", "数据", ExcelType.XSSF);
        exportParams.setStyle(ExcelExportStyleImpl.class);
        
        System.out.println("调用exportExcel前，exportList大小: " + exportList.size());
        // 创建exportList的副本用于合并处理
        List<Map<String, Object>> exportListCopy = new ArrayList<>(exportList);
        
        // 生成 Workbook
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entityList, exportList);
        System.out.println("调用exportExcel后，exportList大小: " + exportList.size() + ", 副本大小: " + exportListCopy.size());
        
        Sheet sheet = workbook.getSheetAt(0);

        Row titleRow = sheet.getRow(0);
        if (titleRow == null) {
            titleRow = sheet.createRow(0);
        }
        titleRow.setHeightInPoints(28f);
        
        // 合并表头A2和B2单元格，命名为"指标"
        Row headerRow = sheet.getRow(1); // 第2行（索引为1）
        if (headerRow == null) {
            headerRow = sheet.createRow(1);
        }
        
        // 确保A2和B2单元格存在
        Cell cellA = headerRow.getCell(0); // A列（索引0）
        if (cellA == null) {
            cellA = headerRow.createCell(0);
        }
        
        Cell cellB = headerRow.getCell(1); // B列（索引1）
        if (cellB == null) {
            cellB = headerRow.createCell(1);
        }
        
        // 设置合并后的文本
        cellA.setCellValue("指标");
        
        // 创建合并区域
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 1));
        
        // 设置合并单元格的样式
        org.apache.poi.ss.usermodel.CellStyle mergedHeaderStyle = workbook.createCellStyle();
        mergedHeaderStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        mergedHeaderStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        mergedHeaderStyle.setFont(font);
        cellA.setCellStyle(mergedHeaderStyle);
        
        // 处理其他合并单元格
        processMergedCells(sheet, exportListCopy);
        
        // 调整列宽
        adjustColumnWidths(workbook.getSheetAt(0));
        
        // 输出
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        
        return out.toByteArray();
    }

    /**
     * 创建导出列定义
     */
    private List<ExcelExportEntity> createExportColumns() {
        List<ExcelExportEntity> entityList = new ArrayList<>();
        
        entityList.add(new ExcelExportEntity("合并项", "mergeItem", 15));
        entityList.add(new ExcelExportEntity("指标", "indexName", 25));
        entityList.add(new ExcelExportEntity("7月末", "balance", 12));
        entityList.add(new ExcelExportEntity("较上月增量", "momIncrement", 12));
        entityList.add(new ExcelExportEntity("较年初增量", "ytdIncrement", 12));
        entityList.add(new ExcelExportEntity("较年初增速", "ytdGrowthRate", 12));
        entityList.add(new ExcelExportEntity("余额占比", "depositRatio", 12));
        entityList.add(new ExcelExportEntity("占比较年初", "ratioVsYtd", 12));
        entityList.add(new ExcelExportEntity("收益率", "interestRate", 10));
        entityList.add(new ExcelExportEntity("收益率较上月", "rateVsMom", 12));
        entityList.add(new ExcelExportEntity("收益率较年初", "rateVsYoy", 12));
        entityList.add(new ExcelExportEntity("取数路径", "remark", 30));
        
        return entityList;
    }

    /**
     * 转换为导出数据
     */
    private List<Map<String, Object>> convertToExportData(List<IndexData> sortedData) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        System.out.println("开始转换数据，总数据量: " + sortedData.size());
        
        // 按合并项和横幅分组数据
        Map<String, List<IndexData>> mergeItemGroups = groupByMergeItem(sortedData);
        
        // 按顺序处理每个合并项组
        for (Map.Entry<String, List<IndexData>> entry : mergeItemGroups.entrySet()) {
            String mergeItem = entry.getKey();
            List<IndexData> groupData = entry.getValue();
            
            // 如果是有效的合并项，添加合并项标题行
            if (mergeItem != null && !mergeItem.trim().isEmpty()) {
                Map<String, Object> mergeItemHeader = new LinkedHashMap<>();
                mergeItemHeader.put("rowType", "MERGE_ITEM_HEADER");
                mergeItemHeader.put("mergeItem", mergeItem);
                mergeItemHeader.put("indexName", "");
                mergeItemHeader.put("balance", null);
                mergeItemHeader.put("momIncrement", null);
                mergeItemHeader.put("ytdIncrement", null);
                mergeItemHeader.put("ytdGrowthRate", null);
                mergeItemHeader.put("depositRatio", null);
                mergeItemHeader.put("ratioVsYtd", null);
                mergeItemHeader.put("interestRate", null);
                mergeItemHeader.put("rateVsMom", null);
                mergeItemHeader.put("rateVsYoy", null);
                mergeItemHeader.put("remark", "");
                result.add(mergeItemHeader);
            }
            
            // 按横幅分组当前合并项下的数据
            Map<String, List<IndexData>> bannerGroups = groupByBanner(groupData);
            
            // 处理每个横幅组
            for (Map.Entry<String, List<IndexData>> bannerEntry : bannerGroups.entrySet()) {
                String banner = bannerEntry.getKey();
                List<IndexData> bannerData = bannerEntry.getValue();
                
                // 添加横幅行（整行显示）
                if (banner != null && !banner.trim().isEmpty()) {
                    Map<String, Object> bannerRow = new LinkedHashMap<>();
                    bannerRow.put("rowType", "BANNER");
                    bannerRow.put("mergeItem", banner);  // 横幅行使用横幅文本作为合并项
                    bannerRow.put("indexName", null); 
                    bannerRow.put("balance", null);
                    bannerRow.put("momIncrement", null);
                    bannerRow.put("ytdIncrement", null);
                    bannerRow.put("ytdGrowthRate", null);
                    bannerRow.put("depositRatio", null);
                    bannerRow.put("ratioVsYtd", null);
                    bannerRow.put("interestRate", null);
                    bannerRow.put("rateVsMom", null);
                    bannerRow.put("rateVsYoy", null);
                    bannerRow.put("remark", "");
                    result.add(bannerRow);
                }
                
                // 添加该横幅组下的所有数据行
                for (IndexData data : bannerData) {
                    IndexConfig config = data.getConfig();
                    if (config == null) {
                        config = new IndexConfig();
                        config.setIndexName("未知指标");
                        config.setMergeItem("");
                        config.setBanner("");
                        config.setRowType("DATA");
                    }
                    
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("rowType", "DATA");
                    row.put("mergeItem", mergeItem);  // 数据行显示合并项（用于A列合并）
                    row.put("indexName", config.getIndexName());  // 指标名称显示在B列
                    row.put("balance", data.getBalance());
                    row.put("momIncrement", data.getMomIncrement());
                    row.put("ytdIncrement", data.getYtdIncrement());
                    row.put("ytdGrowthRate", data.getYtdGrowthRate());
                    row.put("depositRatio", data.getDepositRatio());
                    row.put("ratioVsYtd", data.getRatioVsYtd());
                    row.put("interestRate", data.getInterestRate());
                    row.put("rateVsMom", data.getRateVsMom());
                    row.put("rateVsYoy", data.getRateVsYoy());
                    row.put("remark", config.getRemark());
                    
                    result.add(row);
                }
            }
        }
        
        System.out.println("导出数据转换完成，共生成 " + result.size() + " 行导出记录");
        return result;
    }
    
    /**
     * 按合并项分组数据
     */
    private Map<String, List<IndexData>> groupByMergeItem(List<IndexData> dataList) {
        Map<String, List<IndexData>> groups = new LinkedHashMap<>();
        
        for (IndexData data : dataList) {
            IndexConfig config = data.getConfig();
            String mergeItem = (config != null) ? config.getMergeItem() : "";
            
            if (mergeItem == null) {
                mergeItem = "";
            }
            
            groups.computeIfAbsent(mergeItem, k -> new ArrayList<>()).add(data);
        }
        
        return groups;
    }
    
    /**
     * 按横幅分组数据
     */
    private Map<String, List<IndexData>> groupByBanner(List<IndexData> dataList) {
        Map<String, List<IndexData>> groups = new LinkedHashMap<>();
        
        for (IndexData data : dataList) {
            IndexConfig config = data.getConfig();
            String banner = (config != null) ? config.getBanner() : "";
            
            if (banner == null) {
                banner = "";
            }
            
            groups.computeIfAbsent(banner, k -> new ArrayList<>()).add(data);
        }
        
        return groups;
    }

    /**
     * 处理合并单元格
     */
    private void processMergedCells(Sheet sheet, List<Map<String, Object>> exportList) {
        int dataStartRow = 2; // Title(0) + Header(1)
        
        Workbook workbook = sheet.getWorkbook();
        CellStyle bannerStyle = null;
        CellStyle mergeItemHeaderStyle = null;
        CellStyle leftAlignStyle = null;
        
        System.out.println("开始处理合并单元格，共 " + exportList.size() + " 行数据");
        
        // 用于记录合并项的合并区域
        String currentMergeItem = null;
        int mergeItemStartRow = -1;
        
        // 用于记录横幅的合并区域
        String currentBanner = null;
        int bannerStartRow = -1;
        
        for (int i = 0; i < exportList.size(); i++) {
            Map<String, Object> rowData = exportList.get(i);
            String rowType = (String) rowData.get("rowType");
            int currentRow = dataStartRow + i;
            
            if ("SECTION".equals(rowType)) {
                String bannerText = (String) rowData.get("indexName"); // SECTION行的标题在indexName字段
                
                // 结束之前的合并项合并
                if (mergeItemStartRow >= 0 && currentRow > mergeItemStartRow) {
                    addMergedRegionSafe(sheet, mergeItemStartRow, currentRow - 1, 0, 0);
                }
                
                // 结束之前的横幅合并
                if (bannerStartRow >= 0 && currentRow > bannerStartRow) {
                    addMergedRegionSafe(sheet, bannerStartRow, currentRow - 1, 0, 11);
                }
                
                // 开始新的横幅合并
                bannerStartRow = currentRow;
                currentBanner = bannerText;
                
                // 应用样式并设置横幅文本
                Row row = sheet.getRow(currentRow);
                if (row == null) {
                    row = sheet.createRow(currentRow);
                }
                
                // 在第0列（合并项列）设置横幅文本
                Cell cell = row.getCell(0);
                if (cell == null) {
                    cell = row.createCell(0);
                }
                cell.setCellValue(bannerText);
                
                if (bannerStyle == null) {
                    bannerStyle = workbook.createCellStyle();
                    bannerStyle.cloneStyleFrom(cell.getCellStyle());
                    bannerStyle.setAlignment(HorizontalAlignment.CENTER);
                    bannerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    bannerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    bannerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    Font font = workbook.createFont();
                    font.setBold(true);
                    bannerStyle.setFont(font);
                }
                cell.setCellStyle(bannerStyle);
            /*
            } else if ("BANNER".equals(rowType)) {
                String bannerText = (String) rowData.get("mergeItem");
                
                // 结束之前的合并项合并
                if (mergeItemStartRow >= 0 && currentRow > mergeItemStartRow) {
                    addMergedRegionSafe(sheet, mergeItemStartRow, currentRow - 1, 0, 0);
                }
                
                // 结束之前的横幅合并
                if (bannerStartRow >= 0 && currentRow > bannerStartRow) {
                    addMergedRegionSafe(sheet, bannerStartRow, currentRow - 1, 0, 11);
                }
                
                // 开始新的横幅合并
                bannerStartRow = currentRow;
                currentBanner = bannerText;
                
                // 应用样式并设置横幅文本
                Row row = sheet.getRow(currentRow);
                if (row == null) {
                    row = sheet.createRow(currentRow);
                }
                
                // 在第0列（合并项列）设置横幅文本
                Cell cell = row.getCell(0);
                if (cell == null) {
                    cell = row.createCell(0);
                }
                cell.setCellValue(bannerText);
                
                if (bannerStyle == null) {
                    bannerStyle = workbook.createCellStyle();
                    bannerStyle.cloneStyleFrom(cell.getCellStyle());
                    bannerStyle.setAlignment(HorizontalAlignment.CENTER);
                    bannerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    bannerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    bannerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    Font font = workbook.createFont();
                    font.setBold(true);
                    bannerStyle.setFont(font);
                }
                cell.setCellStyle(bannerStyle);
            */
                
            } else if ("MERGE_ITEM_HEADER".equals(rowType)) {
                // 合并项标题行：整行合并
                addMergedRegionSafe(sheet, currentRow, currentRow, 0, 11);
                
                // 应用样式
                Row row = sheet.getRow(currentRow);
                if (row != null) {
                    Cell cell = row.getCell(0);
                    if (cell != null) {
                        if (mergeItemHeaderStyle == null) {
                            mergeItemHeaderStyle = workbook.createCellStyle();
                            mergeItemHeaderStyle.cloneStyleFrom(cell.getCellStyle());
                            mergeItemHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
                            mergeItemHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                            mergeItemHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                            mergeItemHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            Font font = workbook.createFont();
                            font.setBold(true);
                            font.setFontHeightInPoints((short) 12);
                            mergeItemHeaderStyle.setFont(font);
                        }
                        cell.setCellStyle(mergeItemHeaderStyle);
                    }
                }
                
                // 结束之前的合并项合并
                if (mergeItemStartRow >= 0 && currentRow > mergeItemStartRow) {
                    addMergedRegionSafe(sheet, mergeItemStartRow, currentRow - 1, 0, 0);
                }
                
                // 结束横幅合并
                if (bannerStartRow >= 0 && currentRow > bannerStartRow) {
                    addMergedRegionSafe(sheet, bannerStartRow, currentRow - 1, 0, 11);
                }
                
                // 记录新的合并项开始
                currentMergeItem = (String) rowData.get("mergeItem");
                mergeItemStartRow = currentRow + 1; // 合并从下一行开始
                
                // 重置横幅记录
                bannerStartRow = -1;
                currentBanner = null;
                
            /*
            } else if ("BANNER".equals(rowType)) {

                String bannerText = (String) rowData.get("mergeItem");

                // 结束之前的合并项合并
                if (mergeItemStartRow >= 0 && currentRow > mergeItemStartRow) {
                    addMergedRegionSafe(sheet, mergeItemStartRow, currentRow - 1, 0, 0);
                }

                // 结束之前的横幅合并
                if (bannerStartRow >= 0 && currentRow > bannerStartRow) {
                    addMergedRegionSafe(sheet, bannerStartRow, currentRow - 1, 0, 11);
                }

                // 开始新的横幅合并
                bannerStartRow = currentRow;
                currentBanner = bannerText;

                // 应用样式并设置横幅文本
                Row row = sheet.getRow(currentRow);
                if (row == null) {
                    row = sheet.createRow(currentRow);
                }

                // 在第0列（合并项列）设置横幅文本
                Cell cell = row.getCell(0);
                if (cell == null) {
                    cell = row.createCell(0);
                }
                cell.setCellValue(bannerText);

                if (bannerStyle == null) {
                    bannerStyle = workbook.createCellStyle();
                    bannerStyle.cloneStyleFrom(cell.getCellStyle());
                    bannerStyle.setAlignment(HorizontalAlignment.CENTER);
                    bannerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    bannerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    bannerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    Font font = workbook.createFont();
                    font.setBold(true);
                    bannerStyle.setFont(font);
                }
                cell.setCellStyle(bannerStyle);
            */
            } else if ("DATA".equals(rowType)) {
                String mergeItem = (String) rowData.get("mergeItem");
                
                // 为B列（指标列）设置左对齐样式
                Row row = sheet.getRow(currentRow);
                if (row != null) {
                    Cell cell = row.getCell(1); // B列是第1列
                    if (cell != null) {
                        if (leftAlignStyle == null) {
                            leftAlignStyle = workbook.createCellStyle();
                            leftAlignStyle.cloneStyleFrom(cell.getCellStyle());
                            leftAlignStyle.setAlignment(HorizontalAlignment.LEFT);
                        }
                        cell.setCellStyle(leftAlignStyle);
                    }
                }
                
                // 处理合并项相同的单元格合并
                if (mergeItem != null && !mergeItem.trim().isEmpty()) {
                    // 有合并项的行 - 记录合并项开始
                    if (!mergeItem.equals(currentMergeItem)) {
                        System.out.println("第" + currentRow + "行: 合并项变化 " + currentMergeItem + " -> " + mergeItem);
                        // 结束之前的合并项合并
                        if (mergeItemStartRow >= 0 && currentRow > mergeItemStartRow) {
                            System.out.println("合并单元格: 行" + mergeItemStartRow + "到" + (currentRow - 1) + "列0");
                            addMergedRegionSafe(sheet, mergeItemStartRow, currentRow - 1, 0, 0);
                        }
                        // 开始新的合并项合并
                        currentMergeItem = mergeItem;
                        mergeItemStartRow = currentRow;
                    }
                } else {
                    System.out.println("第" + currentRow + "行: 合并项为空，合并AB列");
                    // 合并项为空的行 - 合并AB两列
                    addMergedRegionSafe(sheet, currentRow, currentRow, 0, 1);
                    
                    // 为合并后的单元格设置指标值并左对齐
                    Row mergedRow = sheet.getRow(currentRow);
                    if (mergedRow != null) {
                        // 获取指标名称
                        String indexName = (String) rowData.get("indexName");
                        if (indexName != null && !indexName.trim().isEmpty()) {
                            // 在合并区域的第0列（A列）设置指标名称
                            Cell cell = mergedRow.getCell(0);
                            if (cell == null) {
                                cell = mergedRow.createCell(0);
                            }
                            cell.setCellValue(indexName);
                            
                            // 应用左对齐样式
                            if (leftAlignStyle == null) {
                                leftAlignStyle = workbook.createCellStyle();
                                leftAlignStyle.cloneStyleFrom(cell.getCellStyle());
                                leftAlignStyle.setAlignment(HorizontalAlignment.LEFT);
                            }
                            cell.setCellStyle(leftAlignStyle);
                        }
                    }
                    
                    // 结束之前的合并项合并
                    if (mergeItemStartRow >= 0 && currentRow > mergeItemStartRow) {
                        System.out.println("合并单元格: 行" + mergeItemStartRow + "到" + (currentRow - 1) + "列0");
                        addMergedRegionSafe(sheet, mergeItemStartRow, currentRow - 1, 0, 0);
                    }
                    mergeItemStartRow = -1;
                    currentMergeItem = null;
                }
                continue;
            }
        }
        
        // 处理最后一次合并项合并
        int totalRows = dataStartRow + exportList.size();
        if (mergeItemStartRow >= 0 && totalRows > mergeItemStartRow) {
            addMergedRegionSafe(sheet, mergeItemStartRow, totalRows - 1, 0, 0);
        }
        
        // 处理最后一次横幅合并
        if (bannerStartRow >= 0 && totalRows > bannerStartRow) {
            addMergedRegionSafe(sheet, bannerStartRow, totalRows - 1, 0, 11);
        }
    }

    private void addMergedRegionSafe(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        try {
            if (lastRow > firstRow || lastCol > firstCol) {
                sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void adjustColumnWidths(Sheet sheet) {
        int[] widths = {4500, 8000, 3500, 3500, 3500, 3500, 3500, 3500, 3000, 3500, 3500, 8000};
        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i]);
        }
    }

    /**
     * 按配置排序数据
     */
    private List<IndexData> sortDataByConfig(List<IndexData> dataList) {
        List<IndexData> result = new ArrayList<>();
        System.out.println("开始排序数据，原始数据量: " + dataList.size());
        
        // 分离顶级和非顶级
        Map<Long, IndexConfig> configMap = new HashMap<>();
        Map<Long, List<IndexData>> childMap = new HashMap<>();
        List<IndexData> topLevel = new ArrayList<>();
        
        int nullConfigCount = 0;
        for (IndexData data : dataList) {
            IndexConfig config = data.getConfig();
            if (config == null) {
                nullConfigCount++;
                continue;
            }
            
            configMap.put(config.getId(), config);
            
            // 将所有数据都作为顶级数据处理，确保都能导出
            topLevel.add(data);
        }
        
        // 排序顶级
        topLevel.sort((a, b) -> {
            int so1 = a.getConfig().getSortOrder() != null ? a.getConfig().getSortOrder() : 0;
            int so2 = b.getConfig().getSortOrder() != null ? b.getConfig().getSortOrder() : 0;
            return so1 - so2;
        });
        
        // 递归添加
        for (IndexData data : topLevel) {
            addDataWithChildren(data, childMap, result);
        }
        
        System.out.println("排序完成，null config数量: " + nullConfigCount + ", 最终数据量: " + result.size());
        return result;
    }

    private void addDataWithChildren(IndexData data, Map<Long, List<IndexData>> childMap, List<IndexData> result) {
        result.add(data);
        List<IndexData> children = childMap.get(data.getConfig().getId());
        if (children != null) {
            children.sort((a, b) -> {
                int so1 = a.getConfig().getSortOrder() != null ? a.getConfig().getSortOrder() : 0;
                int so2 = b.getConfig().getSortOrder() != null ? b.getConfig().getSortOrder() : 0;
                return so1 - so2;
            });
            for (IndexData child : children) {
                addDataWithChildren(child, childMap, result);
            }
        }
    }
}
