package com.equitybot.trade.algorithm.model;

import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.Decimal;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AlgorithmPipelineDataTest {

    private AlgorithmPipelineData algorithmPipelineData;

    private String inputFolder = "/Users/lalitsingh/Documents/GitHub/equitybot-algorithm-svc/src/test/resources/data/input/test.csv";
    private String outputFolder = "/Users/lalitsingh/Documents/GitHub/equitybot-algorithm-svc/src/test/resources/data/output";
    private File tickFile;



    @Before
    public void setup() {
        algorithmPipelineData = new AlgorithmPipelineData(222227L, 2, 10);
    }

    @Test
    public void getInstrument() throws IOException, InterruptedException {
        List<Bar> barList = getTestBarList();
        for (Bar bar:barList) {
            algorithmPipelineData.processStart(bar);
            algorithmPipelineData.getSuperTrend();
            serializeTick();
            algorithmPipelineData.processComplite();
        }
    }


    public List<Bar> getTestBarList() throws IOException, InterruptedException {
        List<Bar> barList = new LinkedList<>();
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(inputFolder))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                ZonedDateTime barEndTime = ZonedDateTime.now(ZoneId.systemDefault());
                Bar bar = new BaseBar(barEndTime, data[1], data[2], data[3], data[4], "10");
                barList.add(bar);
                Thread.sleep(100);
            }
        }
        return barList;
    }

    public synchronized void serializeTick() throws IOException {

        if (this.tickFile == null || !this.tickFile.exists()) {

            this.tickFile = new File(this.outputFolder + getNewFileName());
            if (!this.tickFile.exists()) {
                this.tickFile.createNewFile();
            }
        }

        String st = getString(algorithmPipelineData.getTrueRange())+","+getString(algorithmPipelineData.getExponentialMovingAverage())+","+getString(algorithmPipelineData.getBasicLowerBand())+","+getString(
                algorithmPipelineData.getBasicUpperBand())+","+getString(algorithmPipelineData.getFinalUpperBand())+","+getString(algorithmPipelineData.getFinalLowerBand())+","+getString(
                algorithmPipelineData.getSuperTrend());

        Files.write(Paths.get(this.tickFile.getAbsolutePath()), st.getBytes(), StandardOpenOption.APPEND);
        Files.write(Paths.get(this.tickFile.getAbsolutePath()), System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
    }

    private String getString(Decimal decimal){
        if(decimal != null){
            return decimal.doubleValue()+"";
        }
        return null;
    }

    private String getNewFileName() {
        return "/TickData_" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss'.txt'").format(new Date());
    }
}
