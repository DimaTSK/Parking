package org.hofftech.parking.model.dto;

public class CommandContext {
    private int maxTrucks = Integer.MAX_VALUE;
    private String filePath;
    private String algorithm;
    private boolean useEasyAlgorithm;
    private boolean useEvenAlgorithm;
    private boolean saveToFile;

    public int getMaxTrucks () {
        return maxTrucks;
    }
    public void setMaxTrucks ( int maxTrucks){
        this.maxTrucks = maxTrucks;
    }
    public String getFilePath () {
        return filePath;
    }
    public void setFilePath (String filePath){
        this.filePath = filePath;
    }
    public String getAlgorithm () {
        return algorithm;
    }
    public void setAlgorithm (String algorithm){
        this.algorithm = algorithm;
    }
    public boolean isUseEasyAlgorithm () {
        return useEasyAlgorithm;
    }
    public void setUseEasyAlgorithm ( boolean useEasyAlgorithm){
        this.useEasyAlgorithm = useEasyAlgorithm;
    }
    public boolean isUseEvenAlgorithm () {
        return useEvenAlgorithm;
    }
    public void setUseEvenAlgorithm ( boolean useEvenAlgorithm){
        this.useEvenAlgorithm = useEvenAlgorithm;
    }
    public boolean isSaveToFile () {
        return saveToFile;
    }
    public void setSaveToFile ( boolean saveToFile){
        this.saveToFile = saveToFile;
    }
}
