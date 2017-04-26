package com.jspring.log;

public final class UserAgent {

	// /////////////////////////////////////////
	//
	// /////////////////////////////////////////
	public static class OS {
		private final nl.bitwalker.useragentutils.OperatingSystem value;

		private OS(nl.bitwalker.useragentutils.OperatingSystem value) {
			this.value = value;
		}

		public boolean isUnknown() {
			return nl.bitwalker.useragentutils.OperatingSystem.UNKNOWN.equals(value);
		}

		public boolean isMobileDevice() {
			return value.isMobileDevice();
		}

		public String getName() {
			return value.name();
		}

		public String getGroup() {
			return value.getGroup().name();
		}

		public String getManufacturer() {
			return value.getManufacturer().name();
		}

		public String getDeviceType() {
			return value.getDeviceType().name();
		}

		@Override
		public String toString() {
			// MICROSOFT WINDOWS COMPUTER
			if (value.isMobileDevice()) {
				return value.getManufacturer().name() + "_" + value.getGroup().name() + "_M_"
						+ value.getDeviceType().name();
			}
			return value.getManufacturer().name() + "_" + value.getGroup().name() + "_" + value.getDeviceType().name();
		}

	}

	public static OS valueOfOS(String uaString) {
		return new OS(nl.bitwalker.useragentutils.OperatingSystem.parseUserAgentString(uaString));
	}

	// /////////////////////////////////////////
	//
	// /////////////////////////////////////////
	public static class Browser {
		private final nl.bitwalker.useragentutils.Browser value;

		private Browser(nl.bitwalker.useragentutils.Browser value) {
			this.value = value;
		}

		public boolean isUnknown() {
			return nl.bitwalker.useragentutils.Browser.UNKNOWN.equals(value);
		}

		public String getName() {
			return value.name();
		}

		public String getGroup() {
			return value.getGroup().name();
		}

		public String getManufacturer() {
			return value.getManufacturer().name();
		}

		public String getBrowserType() {
			return value.getBrowserType().name();
		}

		public String getRenderingEngine() {
			return value.getRenderingEngine().name();
		}

		@Override
		public String toString() {
			// MOZILLA FIREFOX WEB_BROWSER GECKO
			return value.getManufacturer().name() + "_" + value.getGroup().name() + "_" + value.getBrowserType().name()
					+ "_" + value.getRenderingEngine().name();
		}
	}

	public static Browser valueOfBrowser(String uaString) {
		return new Browser(nl.bitwalker.useragentutils.Browser.parseUserAgentString(uaString));
	}

	// /////////////////////////////////////////
	//
	// /////////////////////////////////////////
	public static UserAgent valueOf(String uaString) {
		return new UserAgent(nl.bitwalker.useragentutils.UserAgent.parseUserAgentString(uaString));
	}

	private final OS os;
	private final Browser browser;
	private final String browserVersion;

	private UserAgent(nl.bitwalker.useragentutils.UserAgent value) {
		os = new OS(value.getOperatingSystem());
		browser = new Browser(value.getBrowser());
		browserVersion = (null == value.getBrowserVersion() ? "0" : value.getBrowserVersion().getVersion());
	}

	public OS getOS() {
		return os;
	}

	public Browser getBrowser() {
		return browser;
	}

	public String getBrowserVersion() {
		return browserVersion;
	}

	@Override
	public String toString() {
		return os.toString() + "/" + browser.toString() + "/" + browserVersion;
	}

}
