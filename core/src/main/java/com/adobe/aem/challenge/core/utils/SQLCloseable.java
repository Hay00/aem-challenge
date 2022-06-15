package com.adobe.aem.challenge.core.utils;

import java.sql.SQLException;

public interface SQLCloseable extends AutoCloseable{
    @Override public void close() throws SQLException;
}
