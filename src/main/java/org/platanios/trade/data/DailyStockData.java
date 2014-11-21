package org.platanios.trade.data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * TODO: Add composite key for stock ID and date (making combinations of those two unique).
 *
 * @author Emmanouil Antonios Platanios
 */
@Entity
@Table(name = "DailyStockData", catalog = "trade")
public class DailyStockData {
    private long id;
    private Stock stock;
    private DataVendor dataVendor;
    private Date date;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal adjustedClosePrice;
    private long volume;
    private Date dateTimeCreated;
    private Date dateTimeUpdated;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    @NotNull
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_vendor_id")
    public DataVendor getDataVendor() {
        return dataVendor;
    }

    public void setDataVendor(DataVendor dataVendor) {
        this.dataVendor = dataVendor;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "date", nullable = false)
    @NotNull
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Basic
    @Column(name = "open_price", nullable = false)
    @NotNull
    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    @Basic
    @Column(name = "high_price", nullable = false)
    @NotNull
    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    @Basic
    @Column(name = "low_price", nullable = false)
    @NotNull
    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    @Basic
    @Column(name = "close_price", nullable = false)
    @NotNull
    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    @Basic
    @Column(name = "adjusted_close_price", nullable = false)
    @NotNull
    public BigDecimal getAdjustedClosePrice() {
        return adjustedClosePrice;
    }

    public void setAdjustedClosePrice(BigDecimal adjustedClosePrice) {
        this.adjustedClosePrice = adjustedClosePrice;
    }

    @Basic
    @Column(name = "volume", nullable = false)
    @NotNull
    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_time_created")
    @CreationTimestamp
    public Date getDateTimeCreated() {
        return dateTimeCreated;
    }

    private void setDateTimeCreated(Date dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_time_updated")
    @UpdateTimestamp
    public Date getDateTimeUpdated() {
        return dateTimeUpdated;
    }

    private void setDateTimeUpdated(Date dateTimeUpdated) {
        this.dateTimeUpdated = dateTimeUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DailyStockData that = (DailyStockData) o;

        if (id != that.id)
            return false;
        if (!stock.equals(that.stock))
            return false;
        if (dataVendor != null ? !dataVendor.equals(that.dataVendor) : that.dataVendor != null)
            return false;
        if (!date.equals(that.date))
            return false;
        if (!openPrice.equals(that.openPrice))
            return false;
        if (!highPrice.equals(that.highPrice))
            return false;
        if (!lowPrice.equals(that.lowPrice))
            return false;
        if (!closePrice.equals(that.closePrice))
            return false;
        if (!adjustedClosePrice.equals(that.adjustedClosePrice))
            return false;
        if (volume != that.volume)
            return false;
        if (!dateTimeCreated.equals(that.dateTimeCreated))
            return false;
        if (!dateTimeUpdated.equals(that.dateTimeUpdated))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + stock.hashCode();
        result = 31 * result + (dataVendor != null ? dataVendor.hashCode() : 0);
        result = 31 * result + date.hashCode();
        result = 31 * result + openPrice.hashCode();
        result = 31 * result + highPrice.hashCode();
        result = 31 * result + lowPrice.hashCode();
        result = 31 * result + closePrice.hashCode();
        result = 31 * result + adjustedClosePrice.hashCode();
        result = 31 * result + (int) (volume ^ (volume >>> 32));
        result = 31 * result + dateTimeCreated.hashCode();
        result = 31 * result + dateTimeUpdated.hashCode();
        return result;
    }
}
