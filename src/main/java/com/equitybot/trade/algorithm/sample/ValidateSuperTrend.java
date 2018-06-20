package com.equitybot.trade.algorithm.sample;


import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
@Service
public class ValidateSuperTrend {

    private TimeSeries timeSeries;
    private static Calendar customCalendar = Calendar.getInstance();


    public void uploadCSV() {

        String csvFile = "/Users/lalitsingh/Downloads/test.csv";
        String line = "";
        String cvsSplitBy = ",";
        this.timeSeries = new BaseTimeSeries();
        Bar bar;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] tradeData = line.split(cvsSplitBy);
                customCalendar.add(Calendar.SECOND, 1);
                bar = new BaseBar(getEndTime(customCalendar.getTime()), Double.valueOf(tradeData[1]), Double.valueOf(tradeData[2]),
                        Double.valueOf(tradeData[3]), Double.valueOf(tradeData[4]), 5);
                timeSeries.addBar(bar);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[]arg) throws IOException {
        ValidateSuperTrend validateSuperTrend = new ValidateSuperTrend();
        validateSuperTrend.uploadCSV();
        validateSuperTrend.action();
    }

    public void action() throws IOException {

        TrueRange trueRange = new TrueRange();
        trueRange.buildTrueRange(timeSeries);
       // System.out.println("  ------------------ size - "+trueRange.getTrueRangeList().size());
        AverageTrueRange averageTrueRange = new AverageTrueRange();
        averageTrueRange.buildAverageTrueRange(trueRange.getTrueRangeList(), 10);
       // System.out.println("  ------------------ size - "+averageTrueRange.getAverageTrueRangeList().size());
        BasicLowerBand basicLowerBand = new BasicLowerBand();
        basicLowerBand.buildBasicLowerBand(timeSeries, averageTrueRange, 2);
       // System.out.println("  ------------------ size - "+basicLowerBand.getBasicLowerBandList().size());
        BasicUpperBand basicUpperBand = new BasicUpperBand();
        basicUpperBand.buildBasicUpperBand(timeSeries, averageTrueRange, 2);
       // System.out.println("  ------------------ size - "+basicUpperBand.getBasicUpperBandList().size());
        FinalLowerBand finalLowerBand = new FinalLowerBand();
        finalLowerBand.buildFinalLowerBand(timeSeries, basicLowerBand);
       // System.out.println("  ------------------ size - "+finalLowerBand.getFinalLowerBandList().size());
        FinalUpperBand finalUpperBand = new FinalUpperBand();
        finalUpperBand.buildFinalUpperBand(timeSeries, basicUpperBand);
      //  System.out.println("  ------------------  finalUpperBand size - "+finalUpperBand.getFinalUpperBandList().size());
        SuperTrend superTrend = new SuperTrend();
        superTrend.buildSuperTrend(timeSeries, finalLowerBand, finalUpperBand);
       // System.out.println("  ------------------ superTrend size - "+superTrend.getSuperTrendList().size());
        SuperTrendBuySell superTrendBuySell = new SuperTrendBuySell();
        superTrendBuySell.buildSuperTrendBuySell(timeSeries, superTrend);
       // System.out.println("  ------------------ superTrendBuySell size - "+superTrendBuySell.getSuperTrendBuySellList().size());

        writeInCSVFile( trueRange,  averageTrueRange,  basicLowerBand,
                 basicUpperBand, finalLowerBand, finalUpperBand,
                superTrend, superTrendBuySell);
    }

    private void writeInCSVFile(TrueRange trueRange, AverageTrueRange averageTrueRange, BasicLowerBand basicLowerBand,
                                BasicUpperBand basicUpperBand, FinalLowerBand finalLowerBand, FinalUpperBand finalUpperBand,
                                SuperTrend superTrend, SuperTrendBuySell superTrendBuySell) throws IOException {
        File outputFile = new File("/data/logsoutput.csv");
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            for (int index = 0; index < timeSeries.getBarData().size(); index++) {
                bw.write(timeSeries.getBarData().get(index).getOpenPrice() + "," +
                        timeSeries.getBarData().get(index).getMinPrice() + "," +
                        timeSeries.getBarData().get(index).getMinPrice() + "," +
                        timeSeries.getBarData().get(index).getClosePrice() + "," +
                        trueRange.getTrueRangeList().get(index) + "," +
                        averageTrueRange.getAverageTrueRangeList().get(index) + "," +
                        basicUpperBand.getBasicUpperBandList().get(index) + "," +
                        basicLowerBand.getBasicLowerBandList().get(index) + "," +
                        finalUpperBand.getFinalUpperBandList().get(index) + "," +
                        finalLowerBand.getFinalLowerBandList().get(index) + "," +
                        superTrend.getSuperTrendList().get(index) + "," +
                        superTrendBuySell.getSuperTrendBuySellList().get(index));
                bw.write(System.lineSeparator());
            }
        } catch (IOException e) {

            e.printStackTrace();

        }
    }


    public ZonedDateTime getEndTime(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    public static Calendar getCustomCalendar() {
        return customCalendar;
    }

}
