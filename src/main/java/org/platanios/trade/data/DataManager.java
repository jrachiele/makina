package org.platanios.trade.data;

import org.hibernate.Session;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Emmanouil Antonios Platanios
 */
public class DataManager {
    public static class ExchangeBuilder {
        private final String code;
        private final String name;

        private String city = null;
        private String country = null;
        private String currency = null;

        public ExchangeBuilder(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public ExchangeBuilder city(String city) {
            this.city = city;
            return this;
        }

        public ExchangeBuilder country(String country) {
            this.country = country;
            return this;
        }

        public ExchangeBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        protected Exchange build() {
            Exchange exchange = new Exchange();
            exchange.setCode(code);
            exchange.setName(name);
            exchange.setCity(city);
            exchange.setCountry(country);
            exchange.setCurrency(currency);
            Timestamp currentDateTime = new Timestamp(new Date().getTime());
            exchange.setDateTimeCreated(currentDateTime);
            exchange.setDateTimeUpdated(currentDateTime);
            return exchange;
        }
    }

    public static class DataVendorBuilder {
        private final String name;
        private final String abbreviation;

        private String websiteUrl = null;
        private String supportEmail = null;

        public DataVendorBuilder(String name, String abbreviation) {
            this.name = name;
            this.abbreviation = abbreviation;
        }

        public DataVendorBuilder websiteUrl(String websiteUrl) {
            this.websiteUrl = websiteUrl;
            return this;
        }

        public DataVendorBuilder supportEmail(String supportEmail) {
            this.supportEmail = supportEmail;
            return this;
        }

        public DataVendor build() {
            DataVendor dataVendor = new DataVendor();
            dataVendor.setName(name);
            dataVendor.setAbbreviation(abbreviation);
            dataVendor.setWebsiteUrl(websiteUrl);
            dataVendor.setSupportEmail(supportEmail);
            Timestamp currentDateTime = new Timestamp(new Date().getTime());
            dataVendor.setDateTimeCreated(currentDateTime);
            dataVendor.setDateTimeUpdated(currentDateTime);
            return dataVendor;
        }
    }

    private void createAndStoreExchange(ExchangeBuilder exchangeBuilder) {
        Session session = HibernateUtilities.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(exchangeBuilder.build());
        session.getTransaction().commit();
    }

    private void createAndStoreDataVendor(DataVendorBuilder dataVendorBuilder) {
        Session session = HibernateUtilities.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(dataVendorBuilder.build());
        session.getTransaction().commit();
    }

    public void initializeDatabase() {
        createAndStoreExchange(new ExchangeBuilder("N", "NYSE").city("New York").country("USA").currency("USD"));
        createAndStoreExchange(new ExchangeBuilder("A", "NYSE MKT").city("New York").country("USA").currency("USD"));
        createAndStoreExchange(new ExchangeBuilder("R", "NYSE Arca").city("New York").country("USA").currency("USD"));
        createAndStoreExchange(new ExchangeBuilder("Q", "NASDAQ").city("New York").country("USA").currency("USD"));
        createAndStoreExchange(new ExchangeBuilder("X", "Other").city("New York").country("USA").currency("USD"));
        createAndStoreDataVendor(
                new DataVendorBuilder("Wharton Research Data Services / The Center for Research in Security Prices",
                                      "WRDS/CSRP")
                        .websiteUrl("http://wrds-web.wharton.upenn.edu/wrds/ds/crsp/index.cfm")
        );
    }

    public static void main(String[] args) {
        DataManager dataManager = new DataManager();
        dataManager.initializeDatabase();
        HibernateUtilities.getSessionFactory().close();
    }
}
