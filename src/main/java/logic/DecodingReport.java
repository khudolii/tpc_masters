package logic;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import logic.beans.DecoderBean;
import logic.exceptions.ReportException;
import logic.valueobject.TurboCodeDecoderVO;
import org.ejml.simple.SimpleMatrix;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class DecodingReport {
    private static Font anchorFont = new Font(Font.FontFamily.TIMES_ROMAN, 20,
            Font.BOLD);
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.NORMAL, BaseColor.RED);
    private static Font greyFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.NORMAL, BaseColor.DARK_GRAY);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font bold = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.BOLD);
    public static ByteArrayOutputStream bout;

    public static ByteArrayOutputStream getBout() {
        return bout;
    }

    public static Document document = null;
    private static Anchor anchor;
    private static Chapter catPart;

    public DecodingReport() {
    }

    public ByteArrayOutputStream createReportFile(DecoderBean decoderBean) throws ReportException {
        try {
            document = new Document();
            bout = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, bout);
            Paragraph article = new Paragraph();
            document.open();
            article.add(new Paragraph("TURBO CODE DECODING REPORT", anchorFont));
            article.add(new Paragraph("Date Of Decoding: " + decoderBean.getDateOfDecoding(), smallBold));
            article.add(new Paragraph("Num Of Iterations: " + decoderBean.getNumOfIterations(), smallBold));
            document.add(article);

            createNewAnchor("Input Data");
            printMatrixArray(decoderBean.getINPUT_DATA());

            createNewAnchor("DECODING PROCESS");
            for (TurboCodeDecoder.IterationHistory iteration : decoderBean.getIterationsHistory()) {
                createNewAnchor("Iteration №" + iteration.getIterationNum());
                if (decoderBean.getIterationsHistory() != null) {
                    for (TurboCodeDecoderVO halfIteration : iteration.getHalfIterations()) {
                        createNewSubAnchor("Row #: " + halfIteration.getRowNumber());
                        createNewSubAnchor("Input vector (Soft Input vector):");
                        createNewSubAnchorNotBold(Arrays.toString(halfIteration.getSoftInputVector().toArray()));
                        createNewSubAnchor("Hard decision vector:");
                        createNewSubAnchorNotBold(Arrays.toString(halfIteration.getHardDecisionVector().toArray()));
                        createNewSubAnchor("Sindrom vector:");
                        createNewSubAnchorNotBold(Arrays.toString(halfIteration.getSindromVector().toArray()));
                        if (halfIteration.isAllElementsFromSindromZero()) {
                            createNewSubAnchor("All elements from sindrom is zero.");
                        } else {
                            createNewSubAnchor("Test vectors:");
                            if (halfIteration.getTestVectorsList() != null) {
                                for (TurboCodeDecoderVO.TestVectorVO testVector : halfIteration.getTestVectorsList()) {
                                    if (testVector != null) {
                                        createNewSubAnchorNotBold("Value vector: " + Arrays.toString(testVector.getValue().toArray()));
                                        createNewSubAnchorNotBold("Sindrom vector: " + Arrays.toString(testVector.getSindromVector().toArray()));
                                        createNewSubAnchorNotBold("Sindrom position: " + testVector.getSindromPosition());
                                        createNewSubAnchorNotBold("Is zero sindrom vector: " + testVector.getVectorZero());
                                        createNewSubAnchorNotBold("________________________________________");
                                    }
                                }
                            }
                        }
                        createNewSubAnchor("Output vector (Soft Output vector):");
                        createNewSubAnchorNotBold(Arrays.toString(halfIteration.getSoftOutputVector().toArray()));
                        createNewSubAnchorNotBold("____________________________________________________________________");
                    }
                }
                createNewSubAnchor("Output matrix after iteration: ");
                printMatrixArray(iteration.getMatrixAfterIteration());
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReportException("createReportFile: " + e);
        }
        return bout;
    }

    private void createNewAnchor(String name) throws ReportException {
        anchor = new Anchor(name + "\n", anchorFont);
        addAnchorToDocument(anchor);
    }

    private void createNewSubAnchor(String name) throws ReportException {
        anchor = new Anchor(name + "\n", smallBold);
        addAnchorToDocument(anchor);
    }

    private void createNewSubAnchorNotBold(String name) throws ReportException {
        anchor = new Anchor(name + "\n", greyFont);
        addAnchorToDocument(anchor);
    }

    private void printMatrixArray(double[][] matrix) throws ReportException {
        for (int i = 0; i < matrix.length; i++) {
            anchor = new Anchor(i + ". [", greyFont);
            addAnchorToDocument(anchor);
            for (int j = 0; j < matrix[0].length; j++) {
                printMatrixValue(matrix[i][j]);
                if (j == matrix[0].length - 1) {
                    String endLine = "]\n";
                    anchor = new Anchor(endLine, greyFont);
                    addAnchorToDocument(anchor);
                }
            }
        }
    }

    private void printMatrixArray(SimpleMatrix matrix) throws ReportException {
        for (int i = 0; i < matrix.numRows(); i++) {
            anchor = new Anchor(i + ". [", greyFont);
            addAnchorToDocument(anchor);
            for (int j = 0; j < matrix.numCols(); j++) {
                printMatrixValue(matrix.get(i, j));
                if (j == matrix.numCols() - 1) {
                    String endLine = "]\n";
                    anchor = new Anchor(endLine, greyFont);
                    addAnchorToDocument(anchor);
                }
            }
        }
    }

    private void printMatrixValue(double element) throws ReportException {
        String row;
        row = (int) element + " ";
        if ((int) element != DecodeUtil.CV) {
            anchor = new Anchor(row, redFont);
        } else {
            anchor = new Anchor(row, greyFont);
        }
        addAnchorToDocument(anchor);
    }

    private void addAnchorToDocument(Anchor anchor) throws ReportException {
        try {
            document.add(anchor);
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new ReportException("addAnchorToDocument: " + "anchor: " + Arrays.toString(anchor.toArray()) + ", message: " + e);
        }
    }
}

