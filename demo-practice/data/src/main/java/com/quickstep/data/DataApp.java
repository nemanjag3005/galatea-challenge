package com.quickstep.data;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DataApp {


    public static void main(String[] args) {
        System.out.println("Starting DataApp with args: " + Joiner.on(",").join(args));
        HideBanners();
        var profiles = getProfilesFromArgs(args);
        if (args.length > 0 && args[0].equalsIgnoreCase("generateJooqCodeFromEmbeddedPostgres")) {
            new DBMigrationUtil().generateJooqCodeFromEmbeddedPostgres();
        } else if (args.length > 0 && args[0].equalsIgnoreCase("initOrMigrate")) {
            new DBMigrationUtil().initOrMigrateAndOptionallyRunJooq(false, profiles);
        } else if (args.length > 0 && args[0].equalsIgnoreCase("initOrMigrateAndGenerateJooq")) {
            new DBMigrationUtil().initOrMigrateAndOptionallyRunJooq(true, profiles);
        } else {
            System.out.println("Application was run without valid argument.  Valid arguments are generateJooqCodeFromEmbeddedPostgres, initOrMigrate, or initOrMigrateAndGenerateJooq or renameSchemaNameInDump");
        }
    }


    //Jooq Banner disable
    //https://stackoverflow.com/questions/28272284/how-to-disable-jooqs-self-ad-message-in-3-4
    private static void HideBanners() {
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");
    }

    private static List<String> getProfilesFromArgs(String[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg.toLowerCase(Locale.ROOT).contains("--profile="))
                .map(arg -> arg.toLowerCase(Locale.ROOT).replace("--profile=", ""))
                .collect(Collectors.toList());
    }
}
