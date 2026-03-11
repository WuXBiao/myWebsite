package com.index.service;

import cn.afterturn.easypoi.excel.export.styler.ExcelExportStylerDefaultImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 * EasyPoi 自定义导出样式
 * 继承默认实现，只覆盖需要修改的方法
 */
public class ExcelExportStyleImpl extends ExcelExportStylerDefaultImpl {

    private static byte[] rgb(int r, int g, int b) {
        return new byte[] { (byte) r, (byte) g, (byte) b };
    }

    private static void setRgbFillForeground(CellStyle style, int r, int g, int b, short fallbackIndexedColor) {
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        if (style instanceof XSSFCellStyle) {
            XSSFCellStyle xssfStyle = (XSSFCellStyle) style;
            xssfStyle.setFillForegroundColor(new XSSFColor(rgb(r, g, b), null));
            return;
        }
        style.setFillForegroundColor(fallbackIndexedColor);
    }

    public ExcelExportStyleImpl(Workbook workbook) {
        super(workbook);
    }

    @Override
    public CellStyle getHeaderStyle(short color) {
        CellStyle style = workbook.createCellStyle();
        // 蓝色背景
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 居中
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        // 字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("方正准圆简体");
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }

    @Override
    public CellStyle getTitleStyle(short color) {
        CellStyle style = workbook.createCellStyle();
        setRgbFillForeground(style, 0, 112, 192, IndexedColors.LIGHT_BLUE.getIndex());
        // 居中
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("方正准圆简体");
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }
}
