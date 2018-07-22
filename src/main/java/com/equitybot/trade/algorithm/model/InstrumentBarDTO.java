package com.equitybot.trade.algorithm.model;

import org.ta4j.core.Bar;

public class InstrumentBarDTO {
    private Long instrument;
    private Bar bar;

    public InstrumentBarDTO() {
    }

    public InstrumentBarDTO(Long instrument, Bar bar) {
        this.instrument = instrument;
        this.bar = bar;
    }

    public Long getInstrument() {
        return instrument;
    }

    public Bar getBar() {
        return bar;
    }
}
