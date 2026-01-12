package fr.adriencaubel.library.service;

import java.sql.Connection;

public class LibraryService {

    private final Connection connection;

    public LibraryService(Connection connection) {
        this.connection = connection;
    }
}
