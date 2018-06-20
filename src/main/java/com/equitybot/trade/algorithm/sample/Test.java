package com.equitybot.trade.algorithm.sample;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class Test {

    /**
     * Close price of the last bar
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    IgniteCache<String, TimeSeries> cache;

    @Autowired
    private TrueRange trueRange;
    @Autowired
    private AverageTrueRange averageTrueRange;
    @Autowired
    private BasicLowerBand basicLowerBand;
    @Autowired
    private BasicUpperBand basicUpperBand;
    @Autowired
    private FinalLowerBand finalLowerBand;
    @Autowired
    private FinalUpperBand finalUpperBand;
    @Autowired
    private SuperTrend superTrend;
    @Autowired
    private SuperTrendBuySell superTrendBuySell;

    private boolean isLastActionBuy;

    private boolean isInitTradeStrategy;

    File buySellLogFile;


    public Test() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryKubernetesIpFinder ipFinder = new TcpDiscoveryKubernetesIpFinder();
        ipFinder.setServiceName("ignite");
        tcpDiscoverySpi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setClientMode(true);
        Ignite ignite = Ignition.start(cfg);
        Ignition.setClientMode(true);
        ignite.active(true);
        CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
        ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfg.setCacheMode(CacheMode.PARTITIONED);
        ccfg.setRebalanceMode(CacheRebalanceMode.NONE);
        ccfg.setDataRegionName("1GB_Region");
        this.cache = ignite.getOrCreateCache(ccfg);
        trueRange = new TrueRange();
        averageTrueRange = new AverageTrueRange();
        basicLowerBand = new BasicLowerBand();
        basicUpperBand = new BasicUpperBand();
        finalLowerBand = new FinalLowerBand();
        finalUpperBand = new FinalUpperBand();
        superTrend = new SuperTrend();
        superTrendBuySell = new SuperTrendBuySell();
    }

    public IgniteCache<String, TimeSeries> getCache() {
        return cache;
    }

  /*  public static void main(String[] arg) throws IOException {
        ValidateSuperTrend validateSuperTrend = new ValidateSuperTrend();
        validateSuperTrend.uploadCSV();
        TimeSeries timeSeries = validateSuperTrend.getTimeSeries();
        Test test = new Test();
        test.run(timeSeries);
        //
        String csvFile = "C:/project/sensex/TestData/csvFile/allDataTestH2.csv";
        String line = "";
        String cvsSplitBy = ",";
        Bar bar;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] tradeData = line.split(cvsSplitBy);
                validateSuperTrend.getCustomCalendar().add(Calendar.SECOND, 1);
                bar = new BaseBar(validateSuperTrend.getEndTime(validateSuperTrend.getCustomCalendar().getTime()),
                        Double.valueOf(tradeData[1]), Double.valueOf(tradeData[2]),
                        Double.valueOf(tradeData[3]), Double.valueOf(tradeData[4]), 5);
                timeSeries.addBar(bar);
                test.run(timeSeries);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        }*/




    public void runStartegy(String seriesName) throws IOException {
        TimeSeries timeSeries = cache.get(seriesName);
        run(timeSeries);
		/*if (timeSeries != null) {
			TrueRange trueRange = new TrueRange();
			trueRange.buildTrueRange(timeSeries);
			AverageTrueRange averageTrueRange = new AverageTrueRange();
			averageTrueRange.buildAverageTrueRange(trueRange.getTrueRangeList(), 10);
			BasicLowerBand basicLowerBand = new BasicLowerBand();
			basicLowerBand.buildBasicLowerBand(timeSeries, averageTrueRange, 2);
			BasicUpperBand basicUpperBand = new BasicUpperBand();
			basicUpperBand.buildBasicUpperBand(timeSeries, averageTrueRange, 2);
			FinalLowerBand finalLowerBand = new FinalLowerBand();
			finalLowerBand.buildFinalLowerBand(timeSeries, basicLowerBand);
			FinalUpperBand finalUpperBand = new FinalUpperBand();
			finalUpperBand.buildFinalUpperBand(timeSeries, basicUpperBand);
			SuperTrend superTrend = new SuperTrend();
			superTrend.buildSuperTrend(timeSeries, finalLowerBand, finalUpperBand);
			SuperTrendBuySell superTrendBuySell = new SuperTrendBuySell();
			superTrendBuySell.buildSuperTrendBuySell(timeSeries, superTrend);
			superTrendBuySell.getSuperTrendBuySellList();
		}*/
    }

    private void run(TimeSeries timeSeries) throws IOException {
        if (timeSeries != null && timeSeries.getBarData().size() > 10) {
            if (!isInitTradeStrategy) {
                initTradeStrategy(timeSeries);
                isInitTradeStrategy=true;
            } else {
                newBarAddedForTradeStrategy(timeSeries);
            }
        }
    }

    private void initTradeStrategy(TimeSeries timeSeries) {
        trueRange.buildTrueRange(timeSeries);
        averageTrueRange.buildAverageTrueRange(trueRange.getTrueRangeList(), 10);
        basicLowerBand.buildBasicLowerBand(timeSeries, averageTrueRange, 2);
        basicUpperBand.buildBasicUpperBand(timeSeries, averageTrueRange, 2);
        finalLowerBand.buildFinalLowerBand(timeSeries, basicLowerBand);
        finalUpperBand.buildFinalUpperBand(timeSeries, basicUpperBand);
        superTrend.buildSuperTrend(timeSeries, finalLowerBand, finalUpperBand);
        superTrendBuySell.buildSuperTrendBuySell(timeSeries, superTrend);
        isInitTradeStrategy = true;
    }

    private void newBarAddedForTradeStrategy(TimeSeries timeSeries) throws IOException {
        trueRange.addNewTrueRange(timeSeries);
        averageTrueRange.addNewAverageTrueRange(trueRange);
        basicLowerBand.addNewBasicLowerBand(timeSeries, averageTrueRange);
        basicUpperBand.addNewBasicUpperBand(timeSeries, averageTrueRange);
        finalLowerBand.addNewFinalLowerBand(timeSeries, basicLowerBand);
        finalUpperBand.addNewFinalUpperBand(timeSeries, basicUpperBand);
        superTrend.addNewSuperTrend(timeSeries, finalLowerBand, finalUpperBand);
        superTrendBuySell.addNewSuperTrendBuySell(timeSeries, superTrend);
        logBuySell( timeSeries, superTrendBuySell);
    }

    private void logBuySell(TimeSeries timeSeries, SuperTrendBuySell superTrendBuySell) throws IOException {

        String action = superTrendBuySell.getSuperTrendBuySellList()
                .get(superTrendBuySell.getSuperTrendBuySellList().size() - 1);
        if ((Constant.BUY.equals(action) && !isLastActionBuy)
                || (Constant.SELL.equals(action) && isLastActionBuy)) {

            if(buySellLogFile == null) {
                buySellLogFile = new File("/data/logs/" + getNewFileName());
                if (!buySellLogFile.exists()) {
                    buySellLogFile.createNewFile();
                }
            }
            String message = timeSeries.getBarData().get(timeSeries.getBarData().size()-1).getEndTime()+
                    ","+timeSeries.getBarData().get(timeSeries.getBarData().size()-1).getClosePrice()+","
                    +superTrendBuySell.getSuperTrendBuySellList().get(superTrendBuySell.getSuperTrendBuySellList().size()-1);
            Files.write(Paths.get(buySellLogFile.getAbsolutePath()), message.getBytes(), StandardOpenOption.APPEND);
            Files.write(Paths.get(buySellLogFile.getAbsolutePath()), System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            if (Constant.BUY.equals(action)) {
                isLastActionBuy = true;
            } else {
                isLastActionBuy = false;

            }
        }
    }

    private static String getNewFileName() {
        return "/buySellLog" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss'.csv'").format(new Date());
    }
}
