package com.Movies.Cinema.City.DTO;

import java.util.ArrayList;
import java.util.List;

public class AddCinemaRoomDTO {
    private Integer numberOfRows;
    private Integer numberOfColumns;
    private List<ExtraPriceDTO> extraPrices;

    public AddCinemaRoomDTO(Integer numberOfRows, Integer numberOfColumns, List<ExtraPriceDTO> extraPrices) {
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.extraPrices = extraPrices;
    }

    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public Integer getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(Integer numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    public List<ExtraPriceDTO> getExtraPrices() {
        if (this.extraPrices == null) {
            this.extraPrices = new ArrayList<>();
        }
        return extraPrices;
    }

    public void setExtraPrices(List<ExtraPriceDTO> extraPrices) {
        this.extraPrices = extraPrices;
    }
}