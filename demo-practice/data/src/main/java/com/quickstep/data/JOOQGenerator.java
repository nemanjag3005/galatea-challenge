package com.quickstep.data;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.*;

import javax.sql.DataSource;

public class JOOQGenerator {


    public static void generateJOOQSource(DataProperties dataProperties) {
        generateJOOQSource(dataProperties, null);
    }

    public static void generateJOOQSource(DataSource dataSource) {
        generateJOOQSource(null, dataSource);
    }

    public static void generateJOOQSource(DataProperties dataProperties, DataSource dataSource) {
        if (dataProperties != null && dataSource != null) {
            throw new RuntimeException("Cannot have both set");
        } else if (dataProperties == null && dataSource == null) {
            throw new RuntimeException("Cannot have neither set");
        }

        Configuration configuration = new Configuration();

        if (dataProperties != null) {
            configuration.withJdbc(new Jdbc()
                    .withDriver("org.postgresql.Driver")
                    .withUrl(dataProperties.url)
                    .withUser(dataProperties.user)
                    .withPassword(dataProperties.password));
        }
        configuration.withGenerator(new Generator()
                .withDatabase(new Database()
                        .withName("org.jooq.meta.postgres.PostgresDatabase")
                        .withForcedTypes(
                                new ForcedType()
                                        .withName("INSTANT")
                                        .withTypes("timestamp")
                        )
                        .withIncludes(".*")
                        .withExcludes("")
                        .withInputSchema(dataProperties != null ? dataProperties.schema : "public"))
                .withTarget(new Target()
                        .withPackageName("com.quickstep.jooq.generated")
                        .withDirectory("webapp/target/generated-sources/jooq")));
        try {
            if (dataSource != null) {
                var generationTool = new GenerationTool();
                generationTool.setDataSource(dataSource);
                generationTool.run(configuration);
            } else {
                GenerationTool.generate(configuration);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating JOOQ source code", e);
        }
    }

}
