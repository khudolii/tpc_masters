package logic.beans;

import logic.TurboCodeDecoder;
import org.ejml.simple.SimpleMatrix;

import java.util.List;

public class DecoderBean {
    private double probability;
    private String isErrorOccurred;
    private String dateOfDecoding;
    private Integer numOfIterations;
    private List<TurboCodeDecoder.IterationHistory> iterationsHistory;

    public String getErrorOccurred() {
        return isErrorOccurred;
    }

    public void setErrorOccurred(String errorOccurred) {
        isErrorOccurred = errorOccurred;
    }

    public String getDateOfDecoding() {
        return dateOfDecoding;
    }

    public void setDateOfDecoding(String dateOfDecoding) {
        this.dateOfDecoding = dateOfDecoding;
    }

    public Integer getNumOfIterations() {
        return numOfIterations;
    }

    public void setNumOfIterations(Integer numOfIterations) {
        this.numOfIterations = numOfIterations;
    }

    public List<TurboCodeDecoder.IterationHistory> getIterationsHistory() {
        return iterationsHistory;
    }

    public void setIterationsHistory(List<TurboCodeDecoder.IterationHistory> iterationsHistory) {
        this.iterationsHistory = iterationsHistory;
    }

    private SimpleMatrix INPUT_DATA;
    private SimpleMatrix OUTPUT_DATA;

    public String getIsErrorOccurred() {
        return isErrorOccurred;
    }

    public void setIsErrorOccurred(String isErrorOccurred) {
        this.isErrorOccurred = isErrorOccurred;
    }

    public SimpleMatrix getINPUT_DATA() {
        return INPUT_DATA;
    }

    public void setINPUT_DATA(SimpleMatrix INPUT_DATA) {
        this.INPUT_DATA = INPUT_DATA;
    }

    public SimpleMatrix getOUTPUT_DATA() {
        return OUTPUT_DATA;
    }

    public void setOUTPUT_DATA(SimpleMatrix OUTPUT_DATA) {
        this.OUTPUT_DATA = OUTPUT_DATA;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
