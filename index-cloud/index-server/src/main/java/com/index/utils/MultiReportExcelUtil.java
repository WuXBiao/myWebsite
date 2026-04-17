package com.index.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 多报表拼接工具类
 * 支持将多个不同字段的报表拼接到同一个 Excel Sheet 中
 */
public class MultiReportExcelUtil {
    
    /**
     * 报表配置信息
     */
    public static class ReportConfig {
        // 报表名称（作为分段标题）
        private String reportName;
        // 报表描述（可选，显示在报表名称下方）
        private String desc;
        // 复杂表头（支持多行多列合并）- 二维列表：第一维是行，第二维是列
        private List<List<String>> complexHeaders;
        // 数据列表
        private List<List<Object>> dataList;
        // 列宽数组（可选）
        private int[] columnWidths;
        // 业务分组信息（用于表头下方的合并单元格）
        private List<GroupInfo> groups;
        
        /**
         * 构造函数 - 复杂表头版本
         * @param reportName 报表名称
         * @param complexHeaders 复杂表头（二维列表）
         * @param dataList 数据列表
         */
        public ReportConfig(String reportName, List<List<String>> complexHeaders, List<List<Object>> dataList) {
            this.reportName = reportName;
            this.complexHeaders = complexHeaders;
            this.dataList = dataList;
        }
        
        /**
         * 构造函数 - 带描述版本
         * @param reportName 报表名称
         * @param desc 报表描述
         * @param complexHeaders 复杂表头（二维列表）
         * @param dataList 数据列表
         */
        public ReportConfig(String reportName, String desc, List<List<String>> complexHeaders, List<List<Object>> dataList) {
            this.reportName = reportName;
            this.desc = desc;
            this.complexHeaders = complexHeaders;
            this.dataList = dataList;
        }
        
        /**
         * 构造函数 - 带列宽
         * @param reportName 报表名称
         * @param complexHeaders 复杂表头
         * @param dataList 数据列表
         * @param columnWidths 列宽数组
         */
        public ReportConfig(String reportName, List<List<String>> complexHeaders, List<List<Object>> dataList, int[] columnWidths) {
            this.reportName = reportName;
            this.complexHeaders = complexHeaders;
            this.dataList = dataList;
            this.columnWidths = columnWidths;
        }
        
        /**
         * 构造函数 - 带描述和列宽
         * @param reportName 报表名称
         * @param desc 报表描述
         * @param complexHeaders 复杂表头
         * @param dataList 数据列表
         * @param columnWidths 列宽数组
         */
        public ReportConfig(String reportName, String desc, List<List<String>> complexHeaders, List<List<Object>> dataList, int[] columnWidths) {
            this.reportName = reportName;
            this.desc = desc;
            this.complexHeaders = complexHeaders;
            this.dataList = dataList;
            this.columnWidths = columnWidths;
        }
        
        /**
         * 构造函数 - 完整版本
         * @param reportName 报表名称
         * @param complexHeaders 复杂表头
         * @param dataList 数据列表
         * @param columnWidths 列宽数组
         * @param groups 业务分组列表
         */
        public ReportConfig(String reportName, List<List<String>> complexHeaders, List<List<Object>> dataList, int[] columnWidths, List<GroupInfo> groups) {
            this.reportName = reportName;
            this.complexHeaders = complexHeaders;
            this.dataList = dataList;
            this.columnWidths = columnWidths;
            this.groups = groups;
        }
        
        /**
         * 构造函数 - 完整版本（带描述）
         * @param reportName 报表名称
         * @param desc 报表描述
         * @param complexHeaders 复杂表头
         * @param dataList 数据列表
         * @param columnWidths 列宽数组
         * @param groups 业务分组列表
         */
        public ReportConfig(String reportName, String desc, List<List<String>> complexHeaders, List<List<Object>> dataList, int[] columnWidths, List<GroupInfo> groups) {
            this.reportName = reportName;
            this.desc = desc;
            this.complexHeaders = complexHeaders;
            this.dataList = dataList;
            this.columnWidths = columnWidths;
            this.groups = groups;
        }
        
        // Getters and Setters
        public String getReportName() { return reportName; }
        public void setReportName(String reportName) { this.reportName = reportName; }
        public String getDesc() { return desc; }
        public void setDesc(String desc) { this.desc = desc; }
        public List<List<String>> getComplexHeaders() { return complexHeaders; }
        public void setComplexHeaders(List<List<String>> complexHeaders) { this.complexHeaders = complexHeaders; }
        public List<List<Object>> getDataList() { return dataList; }
        public void setDataList(List<List<Object>> dataList) { this.dataList = dataList; }
        public int[] getColumnWidths() { return columnWidths; }
        public void setColumnWidths(int[] columnWidths) { this.columnWidths = columnWidths; }
        public List<GroupInfo> getGroups() { return groups; }
        public void setGroups(List<GroupInfo> groups) { this.groups = groups; }
    }
    
    /**
     * 业务分组信息（用于表头下方的合并单元格）
     */
    public static class GroupInfo {
        // 分组名称
        private String groupName;
        // 起始行索引（相对于数据行的位置，从 0 开始）
        private int startRow;
        // 结束行索引（包含）
        private int endRow;
        // 跨列数（默认 1）
        private int colspan;
        
        public GroupInfo(String groupName, int startRow, int endRow) {
            this.groupName = groupName;
            this.startRow = startRow;
            this.endRow = endRow;
            this.colspan = 1;
        }
        
        public GroupInfo(String groupName, int startRow, int endRow, int colspan) {
            this.groupName = groupName;
            this.startRow = startRow;
            this.endRow = endRow;
            this.colspan = colspan;
        }
        
        // Getters and Setters
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
        public int getStartRow() { return startRow; }
        public void setStartRow(int startRow) { this.startRow = startRow; }
        public int getEndRow() { return endRow; }
        public void setEndRow(int endRow) { this.endRow = endRow; }
        public int getColspan() { return colspan; }
        public void setColspan(int colspan) { this.colspan = colspan; }
    }
    
    /**
     * 将多个报表拼接到一个 Excel 文件中
     * 
     * @param reports 报表配置列表
     * @return Excel 文件的字节数组
     * @throws IOException IO 异常
     */
    public static byte[] mergeReportsToExcel(List<ReportConfig> reports) throws IOException {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        
        // 创建第一个 Sheet
        Sheet sheet = workbook.createSheet("汇总报表");
        
        // 设置默认行高
        sheet.setDefaultRowHeightInPoints(15f);
        
        int currentRowNum = 0;
        
        // 依次处理每个报表
        for (int i = 0; i < reports.size(); i++) {
            ReportConfig report = reports.get(i);
            
            // 1. 创建报表标题行（整行合并）
            Row titleRow = sheet.createRow(currentRowNum++);
            titleRow.setHeightInPoints(24f);
            
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(report.getReportName());
            
            // 设置标题样式
            CellStyle titleStyle = createTitleStyle(workbook);
            titleCell.setCellStyle(titleStyle);
            
            // 计算最大列数（取复杂表头的最大宽度）
            int maxColumns = 12; // 默认至少 12 列
            if (report.getComplexHeaders() != null && !report.getComplexHeaders().isEmpty()) {
                for (List<String> headerRow : report.getComplexHeaders()) {
                    maxColumns = Math.max(maxColumns, headerRow.size());
                }
            }
            // 合并单元格（A 到 L 列，即 0-11 列）
            sheet.addMergedRegion(new CellRangeAddress(currentRowNum - 1, currentRowNum - 1, 0, Math.max(maxColumns - 1, 11)));
            
            // 1.5 创建报表描述行（如果有描述）
            if (report.getDesc() != null && !report.getDesc().isEmpty()) {
                Row descRow = sheet.createRow(currentRowNum++);
                descRow.setHeightInPoints(16f);
                
                Cell descCell = descRow.createCell(0);
                descCell.setCellValue(report.getDesc());
                
                // 设置描述样式
                CellStyle descStyle = createDescStyle(workbook);
                descCell.setCellStyle(descStyle);
                
                // 合并该行
                sheet.addMergedRegion(new CellRangeAddress(currentRowNum - 1, currentRowNum - 1, 0, Math.max(maxColumns - 1, 11)));
            }
            
            // 2. 创建复杂表头（支持自动合并重复值）
            int headerRowCount = report.getComplexHeaders().size();
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // 用于记录已处理的合并区域，避免重复
            Set<String> processedMerges = new HashSet<>();
            
            for (int rowIdx = 0; rowIdx < headerRowCount; rowIdx++) {
                Row headerRow = sheet.createRow(currentRowNum++);
                headerRow.setHeightInPoints(18f);
                
                List<String> headerCells = report.getComplexHeaders().get(rowIdx);
                for (int col = 0; col < headerCells.size(); col++) {
                    String cellValue = headerCells.get(col);
                    
                    // 跳过空单元格
                    if (cellValue == null || cellValue.isEmpty()) {
                        continue;
                    }
                    
                    // 检查这个位置是否已经被合并过了
                    String mergeKey = rowIdx + "_" + col;
                    if (processedMerges.contains(mergeKey)) {
                        continue;
                    }
                    
                    // 创建单元格
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(cellValue);
                    cell.setCellStyle(headerStyle);
                    
                    // 只处理左上角的单元格：检查上方和左方是否有相同值
                    boolean isTopLeft = true;
                    
                    // 检查上方是否有相同值
                    if (rowIdx > 0) {
                        List<String> prevRow = report.getComplexHeaders().get(rowIdx - 1);
                        if (col < prevRow.size() && cellValue.equals(prevRow.get(col))) {
                            isTopLeft = false; // 上方有相同值，说明这不是左上角
                        }
                    }
                    
                    // 检查左方是否有相同值
                    if (isTopLeft && col > 0) {
                        if (cellValue.equals(headerCells.get(col - 1))) {
                            isTopLeft = false; // 左方有相同值，说明这不是左上角
                        }
                    }
                    
                    // 只有左上角的单元格才负责合并
                    if (isTopLeft) {
                        
                        // 计算跨行数（向下查找相同值的连续单元格）
                        int rowspan = calculateRowspanByValue(report.getComplexHeaders(), rowIdx, col, cellValue);
                        
                        // 计算跨列数（向右查找相同值的连续单元格）
                        int colspan = calculateColspanByValue(report.getComplexHeaders(), rowIdx, col, cellValue);
                        
                        // 如果有跨行或跨列，添加合并区域
                        if (rowspan > 1 || colspan > 1) {
                            // 注意：currentRowNum 已经自增（指向下一行），所以要减 1 回到当前行
                            int actualRow = currentRowNum - 1;  // 当前表头行的 POI 行号
                            
                            // 计算合并区域的起始和结束行/列
                            // 对于跨行合并：从当前行开始，向下延伸 rowspan 行
                            int startRow = actualRow;
                            int endRow = actualRow + rowspan - 1;
                            int startCol = col;
                            int endCol = col + colspan - 1;
                            
                            // 确保起始行号大于等于 1（避开报表标题行，表头从 POI 行号 1 开始）
                            if (startRow >= 1) {
                                CellRangeAddress mergeAddress = new CellRangeAddress(
                                    startRow,    // 起始行
                                    endRow,      // 结束行
                                    startCol,    // 起始列
                                    endCol       // 结束列
                                );
                                sheet.addMergedRegion(mergeAddress);
                                
                                // 为合并区域应用边框样式
                                applyBordersToMergedRegion(sheet, mergeAddress, headerStyle);
                                
                                // 标记所有被合并的单元格为已处理（使用相对索引 rowIdx）
                                for (int rRel = 0; rRel < rowspan; rRel++) {  // rRel: 相对偏移量
                                    for (int c = startCol; c <= endCol; c++) {
                                        int actualRowIdx = rowIdx - rRel;  // 实际的表头行索引
                                        String key = actualRowIdx + "_" + c;
                                        processedMerges.add(key);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 3. 创建数据行（包含业务分组）
            int dataStartRow = currentRowNum;
            CellStyle dataStyle = createDataStyle(workbook);
            
            // 计算列数（使用复杂表头的最后一行作为准）
            int columnCount = 0;
            if (report.getComplexHeaders() != null && !report.getComplexHeaders().isEmpty()) {
                // 使用表头的最后一行确定列数
                List<String> lastHeaderRow = report.getComplexHeaders().get(report.getComplexHeaders().size() - 1);
                columnCount = lastHeaderRow.size();
            }
            if (columnCount == 0) {
                columnCount = 12; // 默认列数
            }
            
            // 如果有分组，在对应位置插入分组标题行和数据行
            if (report.getGroups() != null && !report.getGroups().isEmpty()) {
                int lastGroupEndRow = -1;
                
                for (GroupInfo group : report.getGroups()) {
                    // 先添加前一个分组结束到当前分组开始之间的数据行（如果有）
                    for (int rowIdx1 = lastGroupEndRow + 1; rowIdx1 < group.getStartRow(); rowIdx1++) {
                        if (rowIdx1 < report.getDataList().size()) {
                            Row dataRow = sheet.createRow(currentRowNum++);
                            dataRow.setHeightInPoints(16f);
                            List<Object> rowData = report.getDataList().get(rowIdx1);
                            for (int col = 0; col < rowData.size(); col++) {
                                Cell cell = dataRow.createCell(col);
                                Object value = rowData.get(col);
                                if (value != null) {
                                    if (value instanceof Number) {
                                        cell.setCellValue(((Number) value).doubleValue());
                                    } else {
                                        cell.setCellValue(value.toString());
                                    }
                                }
                                cell.setCellStyle(dataStyle);
                            }
                        }
                    }
                    
                    // 创建分组标题行
                    Row groupRow = sheet.createRow(currentRowNum++);
                    groupRow.setHeightInPoints(20f);
                    Cell groupCell = groupRow.createCell(0);
                    groupCell.setCellValue(group.getGroupName());
                    CellStyle groupStyle = createGroupStyle(workbook);
                    groupCell.setCellStyle(groupStyle);
                    
                    // 合并该行
                    if (columnCount > 1) {
                        CellRangeAddress mergeAddress = new CellRangeAddress(
                            groupRow.getRowNum(),
                            groupRow.getRowNum(),
                            0,
                            columnCount - 1
                        );
                        sheet.addMergedRegion(mergeAddress);
                        applyBordersToMergedRegion(sheet, mergeAddress, groupStyle);
                    }
                    
                    // 添加该分组的数据行
                    for (int rowIdx2 = group.getStartRow(); rowIdx2 <= group.getEndRow() && rowIdx2 < report.getDataList().size(); rowIdx2++) {
                        Row dataRow = sheet.createRow(currentRowNum++);
                        dataRow.setHeightInPoints(16f);
                        List<Object> rowData = report.getDataList().get(rowIdx2);
                        for (int col = 0; col < rowData.size(); col++) {
                            Cell cell = dataRow.createCell(col);
                            Object value = rowData.get(col);
                            if (value != null) {
                                if (value instanceof Number) {
                                    cell.setCellValue(((Number) value).doubleValue());
                                } else {
                                    cell.setCellValue(value.toString());
                                }
                            }
                            cell.setCellStyle(dataStyle);
                        }
                    }
                    
                    lastGroupEndRow = group.getEndRow();
                }
                
                // 添加最后一个分组之后的数据行（如果有）
                for (int rowIdx3 = lastGroupEndRow + 1; rowIdx3 < report.getDataList().size(); rowIdx3++) {
                    Row dataRow = sheet.createRow(currentRowNum++);
                    dataRow.setHeightInPoints(16f);
                    List<Object> rowData = report.getDataList().get(rowIdx3);
                    for (int col = 0; col < rowData.size(); col++) {
                        Cell cell = dataRow.createCell(col);
                        Object value = rowData.get(col);
                        if (value != null) {
                            if (value instanceof Number) {
                                cell.setCellValue(((Number) value).doubleValue());
                            } else {
                                cell.setCellValue(value.toString());
                            }
                        }
                        cell.setCellStyle(dataStyle);
                    }
                }
            } else {
                // 没有分组，直接创建所有数据行
                for (List<Object> rowData : report.getDataList()) {
                    Row dataRow = sheet.createRow(currentRowNum++);
                    dataRow.setHeightInPoints(16f);
                    
                    for (int col = 0; col < rowData.size(); col++) {
                        Cell cell = dataRow.createCell(col);
                        Object value = rowData.get(col);
                        
                        if (value != null) {
                            if (value instanceof Number) {
                                cell.setCellValue(((Number) value).doubleValue());
                            } else {
                                cell.setCellValue(value.toString());
                            }
                        }
                        
                        cell.setCellStyle(dataStyle);
                    }
                }
            }
            
            // 4. 设置列宽
            if (report.getColumnWidths() != null) {
                for (int col = 0; col < report.getColumnWidths().length; col++) {
                    sheet.setColumnWidth(col, report.getColumnWidths()[col] * 256);
                }
            } else {
                // 自动调整列宽（基于复杂表头的最后一行）
                if (report.getComplexHeaders() != null && !report.getComplexHeaders().isEmpty()) {
                    List<String> lastHeaderRow = report.getComplexHeaders().get(report.getComplexHeaders().size() - 1);
                    for (int col = 0; col < lastHeaderRow.size(); col++) {
                        sheet.autoSizeColumn(col);
                    }
                }
            }
            
            // 5. 在报表之间添加空行（最后一个报表除外）
            if (i < reports.size() - 1) {
                sheet.createRow(currentRowNum++); // 空行
            }
        }
        
        // 输出
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();
        
        return out.toByteArray();
    }
    
    /**
     * 创建标题样式
     */
    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 居中对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setFontName("宋体");
        style.setFont(font);
        
        // 边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
    
    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 居中对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setFontName("宋体");
        style.setFont(font);
        
        // 背景色（浅灰色）
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
    
    /**
     * 创建报表描述样式
     */
    private static CellStyle createDescStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 左对齐
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 设置字体（斜体，稍小）
        Font font = workbook.createFont();
        font.setItalic(true);
        font.setFontHeightInPoints((short) 10);
        font.setFontName("宋体");
        style.setFont(font);
        
        // 灰色字体
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        
        return style;
    }
    
    /**
     * 为合并区域应用边框样式
     */
    private static void applyBordersToMergedRegion(Sheet sheet, CellRangeAddress region, CellStyle style) {
        // 获取工作簿以创建单元格样式
        Workbook workbook = sheet.getWorkbook();
        
        // 创建一个专门用于边框的样式（基于原样式）
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.cloneStyleFrom(style);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        
        // 为合并区域的每个单元格应用边框样式
        for (int row = region.getFirstRow(); row <= region.getLastRow(); row++) {
            Row currentRow = sheet.getRow(row);
            if (currentRow == null) {
                currentRow = sheet.createRow(row);
            }
            
            for (int col = region.getFirstColumn(); col <= region.getLastColumn(); col++) {
                Cell cell = currentRow.getCell(col);
                if (cell == null) {
                    cell = currentRow.createCell(col);
                }
                cell.setCellStyle(borderStyle);
            }
        }
    }
    
    /**
     * 计算跨行数：向下查找相同值的连续单元格
     * 
     * @param headers 表头列表
     * @param startRow 起始行索引
     * @param col 列索引
     * @param value 要匹配的值
     * @return 跨行数（至少为 1）
     */
    private static int calculateRowspanByValue(List<List<String>> headers, int startRow, int col, String value) {
        int rowspan = 1;
        
        // 从下一行开始，向下查找连续相同值的单元格
        for (int rowIdx = startRow + 1; rowIdx < headers.size(); rowIdx++) {
            List<String> currentRow = headers.get(rowIdx);
            
            // 如果当前行没有这一列，或者值不相同，则停止
            if (col >= currentRow.size() || 
                !value.equals(currentRow.get(col))) {
                break;
            }
            rowspan++;
        }
        
        return rowspan;
    }
    
    /**
     * 计算跨列数：向右查找相同值的连续单元格
     * 
     * @param headers 表头列表
     * @param row 行索引
     * @param startCol 起始列索引
     * @param value 要匹配的值
     * @return 跨列数（至少为 1）
     */
    private static int calculateColspanByValue(List<List<String>> headers, int row, int startCol, String value) {
        int colspan = 1;
        List<String> currentRow = headers.get(row);
        
        // 从下一列开始，向右查找连续相同值的单元格
        for (int col = startCol + 1; col < currentRow.size(); col++) {
            // 如果值不相同，则停止
            if (!value.equals(currentRow.get(col))) {
                break;
            }
            colspan++;
        }
        
        return colspan;
    }
    
    /**
     * 处理业务分组（在表头下方插入合并单元格）
     * 
     * @param sheet Sheet
     * @param groups 分组信息列表
     * @param dataStartRow 数据起始行索引
     * @param columnCount 列数
     */
    private static void processGroups(Sheet sheet, List<GroupInfo> groups, int dataStartRow, int columnCount) {
        CellStyle groupStyle = createGroupStyle(sheet.getWorkbook());
        
        for (GroupInfo group : groups) {
            // 计算实际行号（表头后的第几行）
            int actualStartRow = dataStartRow + group.getStartRow();
            
            // 创建分组行（如果不存在）
            Row groupRow = sheet.getRow(actualStartRow);
            if (groupRow == null) {
                groupRow = sheet.createRow(actualStartRow);
            }
            
            // 创建分组单元格
            Cell groupCell = groupRow.createCell(0);
            groupCell.setCellValue(group.getGroupName());
            groupCell.setCellStyle(groupStyle);
            
            // 合并单元格（只跨列，不跨行）
            int colspan = group.getColspan();
            if (colspan > 1) {
                CellRangeAddress mergeAddress = new CellRangeAddress(
                    actualStartRow,              // 起始行（只有当前行）
                    actualStartRow,              // 结束行（只有当前行，不跨行）
                    0,                           // 起始列
                    Math.min(colspan - 1, columnCount - 1)  // 结束列
                );
                sheet.addMergedRegion(mergeAddress);
            }
        }
    }
    
    /**
     * 创建分组样式
     */
    private static CellStyle createGroupStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 居中对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setFontName("宋体");
        style.setFont(font);
        
        // 背景色（浅蓝色）
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
    
    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 左对齐
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("宋体");
        style.setFont(font);
        
        // 边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
}