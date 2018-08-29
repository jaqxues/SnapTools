package com.ljmu.andre.CBIDatabase.Utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.common.base.MoreObjects;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class QueryBuilder {
    private boolean distinct;
    private String table;
    private String[] arrProjections;
    private List<String> projections = new ArrayList<>();

    private List<String> selections = new ArrayList<>();
    private String whereStatement;

    private String groupBy;
    private String having;
    private String limit;
    private String sort;


    public QueryBuilder setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    public QueryBuilder setProjections(List<String> projections) {
        this.projections = projections;
        return this;
    }

    public QueryBuilder addProjection(String projection) {
        projections.add(projection);
        return this;
    }

    public QueryBuilder addSelection(String column, String argument) {
        addSelection(column, "=", argument);

        return this;
    }

    public QueryBuilder addSelection(String column, String comparator, String argument) {
        if (whereStatement == null || whereStatement.isEmpty())
            whereStatement = column + " " + comparator + " ?";
        else
            whereStatement += " AND " + column + " " + comparator + " ?";

        selections.add(argument);

        return this;
    }

    public QueryBuilder setWhere(String whereStatement) {
        this.whereStatement = whereStatement;
        selections.clear();
        return this;
    }

    public QueryBuilder setGroupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public QueryBuilder setHaving(String having) {
        this.having = having;
        return this;
    }

    public QueryBuilder setLimit(String limit) {
        this.limit = limit;
        return this;
    }

    public QueryBuilder addSort(String column, String direction) {
        if (sort == null)
            sort = column + " " + direction;
        else
            sort += " AND " + column + " " + direction;

        return this;
    }

    public Cursor query(SQLiteDatabase database) {
        if (arrProjections == null)
            arrProjections = getProjections().toArray(new String[0]);

        Timber.d("Table: " + table);
        Timber.d("Query: " + toString());

        return database.query(
                distinct,
                getTable(),
                arrProjections,
                getWhereStatement(),
                getSelections().toArray(new String[0]),
                groupBy,
                having,
                getSort(),
                limit
        );
    }

    public List<String> getProjections() {
        return projections;
    }

    public QueryBuilder setProjections(String[] projections) {
        arrProjections = projections;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("table", table)
                .add("arrProjections", arrProjections)
                .add("projections", projections)
                .add("selections", selections)
                .add("whereStatement", whereStatement)
                .add("sort", sort)
                .toString();
    }

    public String getTable() {
        return table;
    }

    public QueryBuilder setTable(String table) {
        this.table = table;
        return this;
    }

    public String getWhereStatement() {
        return whereStatement;
    }

    public List<String> getSelections() {
        return selections;
    }

    public QueryBuilder setSelections(List<String> selections) {
        this.selections = selections;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public String[] getArrProjections() {
        return arrProjections;
    }
}
