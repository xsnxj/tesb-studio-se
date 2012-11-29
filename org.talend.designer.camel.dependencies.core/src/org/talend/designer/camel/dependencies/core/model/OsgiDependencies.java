package org.talend.designer.camel.dependencies.core.model;

import java.util.regex.Matcher;

public abstract class OsgiDependencies<T extends OsgiDependencies<?>> extends AbstractDependencyItem{
	protected String minVersion = null;
	protected String maxVersion = null;
	protected boolean isOptional = false;

	public OsgiDependencies(){
	}
	
	public OsgiDependencies(String inputString){
		try{
			parse(inputString);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public OsgiDependencies(T copied){
		setName(copied.getName());
		setMaxVersion(copied.getMaxVersion());
		setMinVersion(copied.getMinVersion());
		setOptional(copied.isOptional());
	}
	
	protected abstract void parse(String inputString);

	public void setMinVersion(String minVersion) {
		this.minVersion = normalizeVersion(minVersion);
	}

	public String getMinVersion() {
		return minVersion;
	}

	public void setMaxVersion(String maxVersion) {
		this.maxVersion = normalizeVersion(maxVersion);
	}

	public String getMaxVersion() {
		return maxVersion;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	/**
	 * only care about the name, ignore others
	 */
	@Override
	public  boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		if (name == null) {
			return false;
		}
		return name.equals(((OsgiDependencies<?>) obj).getName());
	}
	
	@Override
	public boolean strictEqual(Object obj) {
		if(!equals(obj)){
			return false;
		}
		OsgiDependencies<?> o = (OsgiDependencies<?>) obj;
		if(isEquals( minVersion, o.getMinVersion()) && isEquals(maxVersion , o.getMaxVersion())&& isOptional == o.isOptional()){
			return true;
		}
		return false;
	}
	
	protected boolean isEquals(String a, String b){
		if(a == null){
			if( b == null){
				return true;
			}else{
				return false;
			}
		}
		
		return a.equals(b);
	}

	/**
	 * only care about name
	 */
	@Override
	public int hashCode() {
		return name == null ? super.hashCode() : name.hashCode();
	}

	public static final int MIN_INVALID = 4;
	public static final int MAX_INVALID = 8;
	public static final int MIN_MAX_INVALID = 16;


	/**
	 * validate the dependency information is valid or not
	 * 
	 * @return {@link #OK} {@link #NAME_NULL} {@link #MIN_INVALID}
	 *         {@link #MAX_INVALID} {@link #MIN_MAX_INVALID}
	 */
	public int isValid() {
		if (name == null || name.trim().equals("")) { //$NON-NLS-1$
			return NAME_NULL;
		}
		
		if(!namePattern.matcher(name).matches()){
			return NAME_INVALID;
		}
		
		if (minVersion != null && !minVersion.trim().equals("")) { //$NON-NLS-1$
			Matcher matcher = versionPattern.matcher(minVersion);
			if (!matcher.matches()) {
				return MIN_INVALID;
			}
		}

		if (maxVersion != null && !maxVersion.trim().equals("")) { //$NON-NLS-1$
			Matcher matcher = versionPattern.matcher(maxVersion);
			if (!matcher.matches()) {
				return MAX_INVALID;
			}
		}

		if (!compareMinMax()) {
			return MIN_MAX_INVALID;
		}
		return OK;
	}

	private boolean compareMinMax() {
		if (maxVersion == null || maxVersion.trim().equals("") || minVersion == null || minVersion.trim().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		String[] maxSplit = maxVersion.split("\\."); //$NON-NLS-1$
		String[] minSplit = minVersion.split("\\."); //$NON-NLS-1$
		for (int i = 0; i < 3; i++) {
			try {
				if (Integer.parseInt(maxSplit[i])
						- Integer.parseInt(minSplit[i]) < 0) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if (minVersion != null && maxVersion != null) {
			sb.append(" ["); //$NON-NLS-1$
			sb.append(minVersion);
			sb.append(","); //$NON-NLS-1$
			sb.append(maxVersion);
			sb.append(")"); //$NON-NLS-1$
		} else if (minVersion != null) {
			sb.append(" ("); //$NON-NLS-1$
			sb.append(minVersion);
			sb.append(")"); //$NON-NLS-1$
		} else if (maxVersion != null) {
			sb.append(" ("); //$NON-NLS-1$
			sb.append(maxVersion);
			sb.append(")"); //$NON-NLS-1$
		}
		return sb.toString();
	}
	
//	public static void main(String[] args) {
//		ImportPackage importPackage = new ImportPackage();
//		importPackage.setName("aa");
//		ImportPackage importPackage2 = new ImportPackage();
//		importPackage2.setName("aa");
//		importPackage.setMinVersion("1");
//		System.out.println(importPackage.strictEqual(importPackage2));
//		Pattern compile = Pattern.compile(NAME_PATTERN);
//		Matcher matcher = compile.matcher("aA.aaa.bb");
//		System.out.println(matcher.matches()==true);
//		matcher = compile.matcher("aa.aaa.b_-323DSG&*^b");
//		System.out.println(matcher.matches()==true);
//		matcher = compile.matcher("aa.a2a.bb");
//		System.out.println(matcher.matches()==true);
//		matcher = compile.matcher("aa.a|a.bb");
//		System.out.println(matcher.matches()==false);
//		matcher = compile.matcher("aa.a[a.bb");
//		System.out.println(matcher.matches()==false);
//		matcher = compile.matcher("aa.aa].bb");
//		System.out.println(matcher.matches()==false);
//		matcher = compile.matcher("(a.aaa.bb");
//		System.out.println(matcher.matches()==false);
//		matcher = compile.matcher("a).aaa.bb");
//		System.out.println(matcher.matches()==false);
//		matcher = compile.matcher("aa.a;a.bb");
//		System.out.println(matcher.matches()==false);
//		matcher = compile.matcher("aa.a:a.bb");
//		System.out.println(matcher.matches()==false);
//		matcher = compile.matcher("aa.aaa.bb\"");
//		System.out.println(matcher.matches()==false);
//		matcher = compile.matcher("aa.aaa.b,b");
//		System.out.println(matcher.matches()==false);
//		matcher = compile.matcher("aa.aaa.b=b");
//		System.out.println(matcher.matches()==false);
//		matcher = compile.matcher("aa.aaa.b =b");
//		System.out.println(matcher.matches()==false);
//	}
}
