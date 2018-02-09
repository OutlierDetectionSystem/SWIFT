package util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by yizhouyan on 8/8/17.
 */
public class ParameterSpace {
    public int batchSize = 100;
    public int windowSize = 1000;
    public int itemgap = 1000;
    public int seqGap = 1000;
    public int minLocalSupport = 2;
    public int maxLocalSupport = 50;
    public int minGlobalSupport = 2;
    public String outputFolder = ".";
    public String deviceIdPath;
    public String metaDataFile;
    public String inputPath;

    public void readInParametersFromFile(String parameterFilePath){
        Configurations configs = new Configurations();
        try
        {
            Configuration config = configs.properties(new File(parameterFilePath));
            // access configuration properties
            this.inputPath = config.getString(InputConfigs.inputFileConf);
            this.deviceIdPath = config.getString(InputConfigs.deviceIdConf);
            this.metaDataFile = config.getString(InputConfigs.metaDataConf);
            this.batchSize = config.getInt(InputConfigs.batchSizeConf);
            this.windowSize = config.getInt(InputConfigs.windowSizeConf);
            this.itemgap = config.getInt(InputConfigs.itemGapConf);
            this.seqGap = config.getInt(InputConfigs.seqGapConf);
            this.minLocalSupport = config.getInt(InputConfigs.minLocalSupportConf);
            this.minGlobalSupport = config.getInt(InputConfigs.minGlobalSupportConf);
            this.maxLocalSupport = config.getInt(InputConfigs.maxLocalSupportConf);
            this.outputFolder = config.getString(InputConfigs.outputFolder) + seqGap + "-" + itemgap;
            try {
                FileUtils.deleteDirectory(new File(outputFolder));
            } catch (IOException e) {
                e.printStackTrace();
            }
            new File(outputFolder).mkdir();
        }
        catch (ConfigurationException cex) {
            cex.printStackTrace();
        }
    }

    public void outputParameterSpace(){
        System.out.println("Input Parameters: ");
        System.out.println("Input File: " + inputPath);
        System.out.println("Input Device File: " + deviceIdPath);
        System.out.println("Input Meta data File: " + metaDataFile);
        System.out.println("Batch Size: " +  batchSize);
        System.out.println("Window Size: " + windowSize);
        System.out.println("Item Gap: " + itemgap);
        System.out.println("Sequence Gap: " + seqGap);
        System.out.println("Minimum Local Support: " + minLocalSupport);
        System.out.println("Maximum Local Support: " + maxLocalSupport);
        System.out.println("Minimum Global Support: " + minGlobalSupport);
        System.out.println("Output Results in Folder: " + outputFolder);
    }
}
