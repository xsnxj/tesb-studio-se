package org.talend.designer.camel.dependencies.core.model;

import org.eclipse.osgi.service.resolver.VersionRange;

public abstract class OsgiDependencies extends AbstractDependencyItem {

    private boolean isOptional;
    private String version;

    public OsgiDependencies() {
    }

    public OsgiDependencies(String inputString) {
        try {
            parse(inputString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        try {
            // not parse empty range.
            if (!(new VersionRange(version).equals(VersionRange.emptyRange))) {
                this.version = version;
            } else {
                this.version = null;
            }
        } catch (Exception e) {
            // version format illegal.
            this.version = null;
        }
    }

    private void parse(String inputString) {
        final String[] split = inputString.split(";"); //$NON-NLS-1$
        setName(split[0]);
        for (int i = 1; i < split.length; i++) {
            final String s = split[i];
            if (s.startsWith(getVersionPrefix())) {
                parseVersions(s);
            } else if ("resolution:=optional".equals(s)) { //$NON-NLS-1$
                setOptional(true);
            }
        }
    }

    private void parseVersions(String input) {
        int firstQuote = input.indexOf('"');
        int lastQuote = input.lastIndexOf('"');
        setVersion(new VersionRange(input.substring(firstQuote + 1, lastQuote)).toString());
    }

    public boolean isOptional() {
        return isOptional;
    }

    public void setOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }

    public boolean strictEqual(final OsgiDependencies obj) {
        return equals(obj) && (isOptional == obj.isOptional())
            && ((version == null && obj.version == null) || (version != null && version.equals(obj.version)));
    }

    @Override
    public String getLabel() {
        String label = name;
        if (null != version) {
            label += ' ' + (Character.isDigit(version.charAt(0)) ? '(' + version + ')' : version);
        }
        return label;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        if (version != null) {
            sb.append(";");
            sb.append(getVersionPrefix());
            sb.append("=\"");
            sb.append(version);
            sb.append("\"");
        }
        if (isOptional) {
            sb.append(";resolution:=optional"); //$NON-NLS-1$
        }
        return sb.toString();
    }

    abstract protected String getVersionPrefix();

}
