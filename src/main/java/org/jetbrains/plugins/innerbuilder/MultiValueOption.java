package org.jetbrains.plugins.innerbuilder;

import java.util.Map;

public final class MultiValueOption implements Option {
    private final Map<String, InnerBuilderOption> optionMap;
    private final String caption;
    private final char mnemonic;
    private final String toolTip;

    private MultiValueOption(Builder builder) {
        optionMap = builder.optionMap;
        caption = builder.caption;
        mnemonic = builder.mnemonic;
        toolTip = builder.toolTip;
    }

    public Map<String, InnerBuilderOption> getOptionMap() {
        return optionMap;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public char getMnemonic() {
        return mnemonic;
    }

    @Override
    public String getToolTip() {
        return toolTip;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private Map<String, InnerBuilderOption> optionMap;
        private String caption;
        private char mnemonic;
        private String toolTip;

        private Builder() {
        }

        public Builder withOptionMap(Map<String, InnerBuilderOption> val) {
            optionMap = val;
            return this;
        }

        public Builder withCaption(String val) {
            caption = val;
            return this;
        }

        public Builder withMnemonic(char val) {
            mnemonic = val;
            return this;
        }

        public Builder withToolTip(String val) {
            toolTip = val;
            return this;
        }

        public MultiValueOption build() {
            return new MultiValueOption(this);
        }
    }
}
