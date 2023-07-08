module com.opencsv {
    requires java.desktop; // for java.beans.*
    requires java.sql;
    requires java.sql.rowset;

    requires org.apache.commons.collections4;
    requires org.apache.commons.lang3;
    requires org.apache.commons.text;

    requires commons.beanutils; // filename-based automatic module

    exports com.opencsv.bean;
    exports com.opencsv.bean.function;
    exports com.opencsv.bean.comparator;
    exports com.opencsv.bean.concurrent;
    exports com.opencsv.bean.customconverter;
    exports com.opencsv.bean.exceptionhandler;
    exports com.opencsv.enums;
    exports com.opencsv.exceptions;
    exports com.opencsv.stream.reader;
    exports com.opencsv;
}