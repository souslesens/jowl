package com.souslesens.Jowl.model;

public class reasonerExtractTriples {
	private String subject;
	private String property;
	private String object;
		public reasonerExtractTriples(String subject,String property,String object) {
			// TODO Auto-generated constructor stub
			this.setSubject(subject);
			this.setProperty(property);
			this.setObject(object);
			
		}
		public String getObject() {
			return object;
		}
		public void setObject(String object) {
			this.object = object;
		}
		public String getProperty() {
			return property;
		}
		public void setProperty(String property) {
			this.property = property;
		}
		public String getSubject() {
			return subject;
		}
		public void setSubject(String subject) {
			this.subject = subject;
		}
		
}
