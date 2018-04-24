package com.hpcnt.releaseNoteAutomation.vo;

/**
 * 
 * @author owen151128
 *
 *         Issue ValueObject
 *
 */
public class Issue {

	/**
	 * Issue number
	 */
	private int issueNo;

	/**
	 * Issue title
	 */
	private String summary;

	/**
	 * Issue key
	 */
	private String issueKey;

	/**
	 * Issue taskType
	 */
	private String issueType;

	/**
	 * Issue status
	 */
	private String status;

	/**
	 * Issue labels
	 */
	private String labels;

	/**
	 * 
	 * @author owen151128
	 * 
	 *         Use java builder pattern
	 */
	public static class Builder {
		private final int issueNo;
		private final String summary;
		private String issueKey;
		private String issueType;
		private String status;
		private String labels;

		public Builder(int issueNo, String summary) {
			this.issueNo = issueNo;
			this.summary = summary;
		}

		public Builder issueKey(String issueKey) {
			this.issueKey = issueKey;
			return this;
		}

		public Builder issueType(String issueType) {
			this.issueType = issueType;
			return this;
		}

		public Builder status(String status) {
			this.status = status;
			return this;
		}

		public Builder labels(String labels) {
			this.labels = labels;
			return this;
		}

		public Issue build() {
			return new Issue(this);
		}

	}

	public Issue(Builder builder) {
		this.issueNo = builder.issueNo;
		this.summary = builder.summary;
		this.issueKey = builder.issueKey;
		this.issueType = builder.issueType;
		this.status = builder.status;
		this.labels = builder.labels;
	}

	public int getIssueNo() {
		return issueNo;
	}

	public String getSummary() {
		return summary;
	}

	public String getIssueKey() {
		return issueKey;
	}

	public String getIssueType() {
		return issueType;
	}

	public String getStatus() {
		return status;
	}

	public String getLabels() {
		return labels;
	}

}
