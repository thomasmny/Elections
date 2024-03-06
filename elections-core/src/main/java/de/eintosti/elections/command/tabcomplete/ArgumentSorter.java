package com.eintosti.elections.command.tabcomplete;

import com.eintosti.elections.util.external.StringUtils;

import java.util.List;

abstract class ArgumentSorter {

    public void addArgument(String input, String argument, List<String> arrayList) {
        if (input.equals("") || StringUtils.startsWithIgnoreCase(argument, input)) {
            arrayList.add(argument);
        }
    }
}