package org.hofftech.parking.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandContext {
    private int maxTrucks = Integer.MAX_VALUE;
    private String filePath;
    private String algorithm;
    private boolean useEasyAlgorithm;
    private boolean useEvenAlgorithm;
    private boolean saveToFile;
}
