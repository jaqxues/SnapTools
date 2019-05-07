package com.ljmu.andre.CBIDatabase.Utils;

import com.google.common.base.MoreObjects;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SQLCommand {
    private CommandAction commandAction;
    private String query;

    public SQLCommand(CommandAction commandAction) {
        this.commandAction = commandAction;
    }

    public SQLCommand(String query) {
        this(CommandAction.QUERY, query);
    }

    public SQLCommand(CommandAction commandAction, String query) {
        this.commandAction = commandAction;
        this.query = query;
    }

    public CommandAction getCommandAction() {
        return commandAction;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("commandAction", commandAction)
                .add("query", query)
                .toString();
    }

    public enum CommandAction {
        QUERY, RECYCLE
    }
}
