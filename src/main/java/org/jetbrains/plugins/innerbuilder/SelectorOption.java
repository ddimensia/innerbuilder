package org.jetbrains.plugins.innerbuilder;

public class SelectorOption implements Option {
    private final InnerBuilderOption option;
    private final String caption;
    private final char mnemonic;
    private final String toolTip;

    private SelectorOption(final Builder builder) {
        option = builder.option;
        caption = builder.caption;
        mnemonic = builder.mnemonic;
        toolTip = builder.toolTip;
    }

    public InnerBuilderOption getOption() {
        return option;
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
        private InnerBuilderOption option;
        private String caption;
        private char mnemonic;
        private String toolTip;

        private Builder() { }

        public Builder withOption(final InnerBuilderOption option) {
            this.option = option;
            return this;
        }

        public Builder withCaption(final String caption) {
            this.caption = caption;
            return this;
        }

        public Builder withMnemonic(final char mnemonic) {
            this.mnemonic = mnemonic;
            return this;
        }

        public Builder withToolTip(final String toolTip) {
            this.toolTip = toolTip;
            return this;
        }

        public SelectorOption build() {
            return new SelectorOption(this);
        }
    }
}
