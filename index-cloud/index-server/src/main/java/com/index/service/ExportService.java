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
        
        // 生成 Workbook
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entityList, exportList);
        
        // 处理合并单元格
        processMergedCells(workbook.getSheetAt(0), sortedData);
        
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
        
        entityList.add(new ExcelExportEntity("分类", "category", 15));
        entityList.add(new ExcelExportEntity("子分类", "subCategory", 15));
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
        
        String currentCategory = null;
        String currentSubCategory = null;
        
        for (IndexData data : sortedData) {
            IndexConfig config = data.getConfig();
            if (config == null) continue;
            
            Map<String, Object> row = new LinkedHashMap<>();
            
            // 横向分类标题行
            if ("SECTION".equals(config.getRowType())) {
                row.put("category", "");
                row.put("subCategory", "");
                row.put("indexName", config.getSectionTitle());
                row.put("balance", null);
                row.put("momIncrement", null);
                row.put("ytdIncrement", null);
                row.put("ytdGrowthRate", null);
                row.put("depositRatio", null);
                row.put("ratioVsYtd", null);
                row.put("interestRate", null);
                row.put("rateVsMom", null);
                row.put("rateVsYoy", null);
                row.put("remark", config.getSectionNote());
                
                currentCategory = null;
                currentSubCategory = null;
            } else {
                // 普通数据行
                String itemCategory = config.getCategory();
                String itemSubCategory = config.getSubCategory();
                
                // 只在第一行显示分类
                if (itemCategory != null && !itemCategory.equals(currentCategory)) {
                    row.put("category", itemCategory);
                    currentCategory = itemCategory;
                    currentSubCategory = null;
                } else {
                    row.put("category", "");
                }
                
                // 只在第一行显示子分类
                if (itemSubCategory != null && !itemSubCategory.equals(currentSubCategory)) {
                    row.put("subCategory", itemSubCategory);
                    currentSubCategory = itemSubCategory;
                } else {
                    row.put("subCategory", "");
                }
                
                row.put("indexName", config.getIndexName());
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
            }
            
            result.add(row);
        }
        
        return result;
    }

    /**
     * 处理合并单元格
     */
    private void processMergedCells(Sheet sheet, List<IndexData> sortedData) {
        int dataStartRow = 2;
        
        List<int[]> categoryMerges = new ArrayList<>();
        List<int[]> subCategoryMerges = new ArrayList<>();
        List<int[]> sectionMerges = new ArrayList<>();
        
        String currentCategory = null;
        String currentSubCategory = null;
        int categoryStartRow = -1;
        int subCategoryStartRow = -1;
        
        for (int i = 0; i < sortedData.size(); i++) {
            IndexData data = sortedData.get(i);
            IndexConfig config = data.getConfig();
            if (config == null) continue;
            
            int currentRow = dataStartRow + i;
            
            if ("SECTION".equals(config.getRowType())) {
                sectionMerges.add(new int[]{currentRow, currentRow, 2, 5});
                
                if (currentCategory != null && categoryStartRow >= 0 && currentRow > categoryStartRow + 1) {
                    categoryMerges.add(new int[]{categoryStartRow, currentRow - 1, 0, 0});
                }
                if (currentSubCategory != null && subCategoryStartRow >= 0 && currentRow > subCategoryStartRow + 1) {
                    subCategoryMerges.add(new int[]{subCategoryStartRow, currentRow - 1, 1, 1});
                }
                
                currentCategory = null;
                currentSubCategory = null;
                categoryStartRow = -1;
                subCategoryStartRow = -1;
            } else {
                String itemCategory = config.getCategory();
                String itemSubCategory = config.getSubCategory();
                
                if (itemCategory != null && !itemCategory.equals(currentCategory)) {
                    if (currentCategory != null && categoryStartRow >= 0 && currentRow > categoryStartRow + 1) {
                        categoryMerges.add(new int[]{categoryStartRow, currentRow - 1, 0, 0});
                    }
                    currentCategory = itemCategory;
                    categoryStartRow = currentRow;
                    currentSubCategory = null;
                    subCategoryStartRow = -1;
                }
                
                if (itemSubCategory != null && !itemSubCategory.equals(currentSubCategory)) {
                    if (currentSubCategory != null && subCategoryStartRow >= 0 && currentRow > subCategoryStartRow + 1) {
                        subCategoryMerges.add(new int[]{subCategoryStartRow, currentRow - 1, 1, 1});
                    }
                    currentSubCategory = itemSubCategory;
                    subCategoryStartRow = currentRow;
                }
            }
        }
        
        int lastRow = dataStartRow + sortedData.size() - 1;
        if (currentCategory != null && categoryStartRow >= 0 && lastRow > categoryStartRow) {
            categoryMerges.add(new int[]{categoryStartRow, lastRow, 0, 0});
        }
        if (currentSubCategory != null && subCategoryStartRow >= 0 && lastRow > subCategoryStartRow) {
            subCategoryMerges.add(new int[]{subCategoryStartRow, lastRow, 1, 1});
        }
        
        for (int[] range : sectionMerges) {
            addMergedRegionSafe(sheet, range[0], range[1], range[2], range[3]);
        }
        for (int[] range : categoryMerges) {
            addMergedRegionSafe(sheet, range[0], range[1], range[2], range[3]);
        }
        for (int[] range : subCategoryMerges) {
            addMergedRegionSafe(sheet, range[0], range[1], range[2], range[3]);
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
        int[] widths = {4500, 4000, 8000, 3500, 3500, 3500, 3500, 3500, 3500, 3000, 3500, 3500, 8000};
        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i]);
        }
    }

    /**
     * 按配置排序数据
     */
    private List<IndexData> sortDataByConfig(List<IndexData> dataList) {
        List<IndexData> result = new ArrayList<>();
        
        // 分离顶级和非顶级
        Map<Long, IndexConfig> configMap = new HashMap<>();
        Map<Long, List<IndexData>> childMap = new HashMap<>();
        List<IndexData> topLevel = new ArrayList<>();
        
        for (IndexData data : dataList) {
            IndexConfig config = data.getConfig();
            if (config == null) continue;
            
            configMap.put(config.getId(), config);
            
            if (config.getParentId() == null) {
                topLevel.add(data);
            } else {
                childMap.computeIfAbsent(config.getParentId(), k -> new ArrayList<>()).add(data);
            }
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
