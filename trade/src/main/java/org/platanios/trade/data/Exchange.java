package org.platanios.trade.data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author Emmanouil Antonios Platanios
 */
@Entity
@Table(name = "Exchanges",
        catalog = "trade",
        uniqueConstraints = @UniqueConstraint(name = "uk_name", columnNames = "name"))
public class Exchange {
    private long id;
    private String name;
    private String city;
    private String country;
    private String currency;
    private Date dateTimeCreated;
    private Date dateTimeUpdated;
    private List<Stock> stocksTraded;

    public static class Builder {
        private final String name;

        private String city = null;
        private String country = null;
        private String currency = null;

        public Builder(String name) {
            this.name = name;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        protected Exchange build() {
            Exchange exchange = new Exchange();
            exchange.setName(name);
            exchange.setCity(city);
            exchange.setCountry(country);
            exchange.setCurrency(currency);
            return exchange;
        }
    }

    protected Exchange() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false)
    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Basic
    @Column(name = "currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    @OneToMany(mappedBy = "primaryExchange", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Stock> getStocksTraded() {
        return stocksTraded;
    }

    private void setStocksTraded(List<Stock> stocksTraded) {
        this.stocksTraded = stocksTraded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Exchange that = (Exchange) o;

        if (id != that.id)
            return false;
        if (!name.equals(that.name))
            return false;
        if (city != null ? !city.equals(that.city) : that.city != null)
            return false;
        if (country != null ? !country.equals(that.country) : that.country != null)
            return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null)
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
        result = 31 * result + name.hashCode();
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + dateTimeCreated.hashCode();
        result = 31 * result + dateTimeUpdated.hashCode();
        return result;
    }
}