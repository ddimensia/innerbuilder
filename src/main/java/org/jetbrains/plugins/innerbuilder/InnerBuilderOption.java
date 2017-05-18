package org.jetbrains.plugins.innerbuilder;

public enum InnerBuilderOption {

    FINAL_SETTERS("finalSetters"),
    NEW_BUILDER_METHOD("newBuilderMethod"),
    COPY_CONSTRUCTOR("copyConstructor"),
    WITH_NOTATION("withNotation"),
    SET_NOTATION("setNotatinon"),
    JSR305_ANNOTATIONS("useJSR305Annotations"),
    FINDBUGS_ANNOTATION("useFindbugsAnnotation"),
    WITH_JAVADOC("withJavadoc"),
    MAKE_FIELDS_FINAL("finalFields");

    private final String property;

    private InnerBuilderOption(final String property) {
        this.property = String.format("GenerateInnerBuilder.%s", property);
    }

    public String getProperty() {
        return property;
    }
}
