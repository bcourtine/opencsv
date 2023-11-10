package com.opencsv.bean.mocks.number;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvNumber;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberMockRoundingMode {
    
    @CsvBindByPosition(position = 0)
    @CsvNumber(value = "0", roundingMode = RoundingMode.UP)
    private BigDecimal roundUp;
    
    @CsvBindByPosition(position = 1)
    @CsvNumber(value = "0", roundingMode = RoundingMode.DOWN)
    private BigDecimal roundDown;
    
    @CsvBindByPosition(position = 2)
    @CsvNumber(value = "0", roundingMode = RoundingMode.CEILING)
    private BigDecimal roundCeiling;
    
    @CsvBindByPosition(position = 3)
    @CsvNumber(value = "0", roundingMode = RoundingMode.FLOOR)
    private BigDecimal roundFloor;
    
    @CsvBindByPosition(position = 4)
    @CsvNumber(value = "0", roundingMode = RoundingMode.HALF_UP)
    private BigDecimal roundHalfUp;
    
    @CsvBindByPosition(position = 5)
    @CsvNumber(value = "0", roundingMode = RoundingMode.HALF_DOWN)
    private BigDecimal roundHalfDown;
    
    @CsvBindByPosition(position = 6)
    @CsvNumber(value = "0", roundingMode = RoundingMode.HALF_EVEN)
    private BigDecimal roundHalfEven;
    
    @CsvBindByPosition(position = 7)
    @CsvNumber(value = "0", roundingMode = RoundingMode.UNNECESSARY)
    private BigDecimal roundUnnecessary;

    public BigDecimal getRoundUp() {
        return roundUp;
    }

    public void setRoundUp(BigDecimal roundUp) {
        this.roundUp = roundUp;
    }

    public BigDecimal getRoundDown() {
        return roundDown;
    }

    public void setRoundDown(BigDecimal roundDown) {
        this.roundDown = roundDown;
    }

    public BigDecimal getRoundCeiling() {
        return roundCeiling;
    }

    public void setRoundCeiling(BigDecimal roundCeiling) {
        this.roundCeiling = roundCeiling;
    }

    public BigDecimal getRoundFloor() {
        return roundFloor;
    }

    public void setRoundFloor(BigDecimal roundFloor) {
        this.roundFloor = roundFloor;
    }

    public BigDecimal getRoundHalfUp() {
        return roundHalfUp;
    }

    public void setRoundHalfUp(BigDecimal roundHalfUp) {
        this.roundHalfUp = roundHalfUp;
    }

    public BigDecimal getRoundHalfDown() {
        return roundHalfDown;
    }

    public void setRoundHalfDown(BigDecimal roundHalfDown) {
        this.roundHalfDown = roundHalfDown;
    }

    public BigDecimal getRoundHalfEven() {
        return roundHalfEven;
    }

    public void setRoundHalfEven(BigDecimal roundHalfEven) {
        this.roundHalfEven = roundHalfEven;
    }

    public BigDecimal getRoundUnnecessary() {
        return roundUnnecessary;
    }

    public void setRoundUnnecessary(BigDecimal roundUnnecessary) {
        this.roundUnnecessary = roundUnnecessary;
    }
}
