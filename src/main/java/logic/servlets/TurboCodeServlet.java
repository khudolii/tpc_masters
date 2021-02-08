package logic.servlets;

import logic.DecodeUtil;
import logic.DecodingReport;
import logic.beans.DecoderBean;
import logic.delegate.TransportDelegate;
import logic.exceptions.ReportException;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import java.io.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.stream.Collectors;

@WebServlet("/getDecoding")
public class TurboCodeServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(DecodingReport.class);
    private DecoderBean decoderBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletOutputStream outputStream = null;
        try {
            String actionName = req.getParameter("actionName");
            log.info("Catch request: " + req.getMethod() + "; actionName = " + actionName);
            if (actionName == null) {
                if (decoderBean == null) {
                    decoderBean = new DecoderBean();
                    log.info("actionName: null; decoderBean: null; set to new decoderBean constant input data");
                }
            } else if (actionName.equals("startDecoding")) {
                log.info("Action Name = startDecoding. Start ->");
                decoderBean = new DecoderBean();
                String probability = req.getParameter("probability");
                if (probability != null) {
                    decoderBean.setProbability(Double.parseDouble(probability));
                TransportDelegate transportDelegate = new TransportDelegate();
                transportDelegate.startDecodingProcess(decoderBean);
                } else {
                    throw new ServletException("Not correct probability value");
                }
            } else if (actionName.equals("deleteOutputMatrix")) {
                decoderBean.setINPUT_DATA(null);
                decoderBean.setOUTPUT_DATA(null);
                log.info("Action Name = deleteOutputMatrix. Set to outputData - null");
            } else if (actionName.equals("getPdfReport")) {
                log.info("Action Name = getPdfReport. Start ->");
                DecodingReport report = new DecodingReport();
                ByteArrayOutputStream byteArrayOutputStream = report.createReportFile(decoderBean);
                if (byteArrayOutputStream != null) {
                    outputStream = resp.getOutputStream();
                    resp.setContentLength(byteArrayOutputStream.size());
                    byteArrayOutputStream.writeTo(outputStream);
                    resp.setHeader("Content-Disposition", "attachment; filename='TPCReport_" + decoderBean.getDateOfDecoding() + ".pdf'");
                    resp.setHeader("Expires", "0");
                    resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                    resp.setHeader("Pragma", "public");
                    resp.setContentType("application/pdf");
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            log.error(e);
            decoderBean.setErrorOccurred(e.getMessage());
            resp.setStatus(404);
        } finally {
            req.setAttribute("decoderBean", decoderBean);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }

    public double[][] handleMatrix(HttpServletRequest req) {
        double[][] inputData = new double[16][16];
        log.info("Start parsing input matrix from request");
        Enumeration<String> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = req.getParameterValues(paramName);
            for (String paramValue : paramValues) {
                String[] paramIndexes = paramName.split("-");
                if (paramIndexes.length == 2) {
                    int row = Integer.parseInt(paramIndexes[0]);
                    int col = Integer.parseInt(paramIndexes[1]);
                    double value = Double.parseDouble(paramValue);
                    inputData[row][col] = value;
                }
            }
        }
        log.info("Parsed input data: " + Arrays.deepToString(inputData));
        return inputData;
    }
}

