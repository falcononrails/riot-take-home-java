package com.anaslimouri.riot;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {
    @Test
    void domainHasNoFrameworkDependencies() {
        var domain = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.anaslimouri.riot.domain");

        classes().should().onlyDependOnClassesThat(
                resideInAnyPackage("java..", "com.anaslimouri.riot.domain..")
                    .or(name("lombok.Generated")))
            .check(domain);
    }

    @Test
    void controllersGoThroughUseCases() {
        var controllers = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.anaslimouri.riot.endpoints");

        noClasses().should().dependOnClassesThat()
            .resideInAnyPackage("com.anaslimouri.riot.adapters..", "com.anaslimouri.riot.application..",
                "com.anaslimouri.riot.domain.ports..")
            .check(controllers);
    }

    @Test
    void adaptersDoNotDependOnEndpointsOrApplicationWiring() {
        var adapters = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.anaslimouri.riot.adapters");

        noClasses().should().dependOnClassesThat()
            .resideInAnyPackage("com.anaslimouri.riot.endpoints..", "com.anaslimouri.riot.application..")
            .check(adapters);
    }
}
