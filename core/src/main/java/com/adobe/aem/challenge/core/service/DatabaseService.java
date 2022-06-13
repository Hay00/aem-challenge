package com.adobe.aem.challenge.core.service;

import java.sql.Connection;

public interface DatabaseService {
    Connection getConnection();
}
