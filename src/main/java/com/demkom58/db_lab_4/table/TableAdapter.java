package com.demkom58.db_lab_4.table;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Optional;

public interface TableAdapter {
    Optional<String> execute(@Language("SQL") @NotNull String sql) throws SQLException;
}
