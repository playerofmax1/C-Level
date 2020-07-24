package com.clevel.kudu.util;

import org.apache.commons.math3.util.Pair;
import org.jxls.area.Area;
import org.jxls.builder.AreaBuilder;
import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.CellRef;
import org.jxls.common.Context;
import org.jxls.transform.Transformer;
import org.jxls.util.TransformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class ExcelUtil {

    private static Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    private static byte[] forceLoadFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] data = new byte[4096];
        int count;
        while ((count = inputStream.read(data)) != -1) {
            byteArrayOutputStream.write(data, 0, count);
        }

        return byteArrayOutputStream.toByteArray();
    }

    @SafeVarargs
    public static void createExcel(String outputFileName, String outputSheetName, String templateFileName, Iterable<?> itemList, Iterable<?> columnHeaderList, Pair<String, Object>... variables) throws IOException {
        log.debug("createExcel(output:{},template{}).", outputFileName, templateFileName);
        InputStream inputStream = new BufferedInputStream(new FileInputStream(templateFileName));

        File file = new File(outputFileName);
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            String errorMessage = "Can't create folder:" + parentFile.getAbsolutePath();
            log.error(errorMessage);
            throw new IOException(errorMessage);
        }

        FileOutputStream outputStream = new FileOutputStream(file);
        createExcel(outputStream, outputSheetName, inputStream, itemList, columnHeaderList, variables);
        outputStream.close();
    }

    /**
     * Using JXLS v2.
     *
     * @param outputStream     excel output file
     * @param inputStream      excel template file
     * @param itemList         data used in excel template, ref: items=”itemList”
     * @param columnHeaderList [optional] columns list for the report with unknown number of column, ref: items=”columnHeaderList”
     * @throws IOException
     */
    @SafeVarargs
    public static void createExcel(OutputStream outputStream, String outputSheetName, InputStream inputStream, Iterable<?> itemList, Iterable<?> columnHeaderList, Pair<String, Object>... variables) throws IOException {
        log.debug("createExcel.");
        byte[] templateBytes = forceLoadFromInputStream(inputStream);
        InputStream is = new ByteArrayInputStream(templateBytes);

        Context context = new Context();
        context.putVar("columnHeaderList", columnHeaderList);
        context.putVar("itemList", itemList);

        if (variables != null && variables.length > 0) {
            for (Pair<String, Object> variable : variables) {
                context.putVar(variable.getKey(), variable.getValue());
            }
        }

        Transformer transformer = TransformerFactory.createTransformer(is, outputStream);
        AreaBuilder areaBuilder = new XlsCommentAreaBuilder(transformer);
        List<Area> xlsAreaList = areaBuilder.build();
        Area xlsArea = xlsAreaList.get(0);

        /*GridCommand gridCommand = (GridCommand) xlsArea.getCommandDataList().get(0).getCommand();
        gridCommand.setProps(objectProps);*/

        /*mapping and then print*/
        xlsArea.applyAt(new CellRef(outputSheetName + "!A1"), context);
        transformer.write();
    }

}
