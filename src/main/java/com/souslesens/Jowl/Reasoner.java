package com.souslesens.Jowl;

public class Reasoner {
		private String filePath;
		private String operation;
		
		public Reasoner(String filePath , String operation) {
			this.filePath = filePath;
			this.operation = operation;
		}
		public String getUrl() {
			return filePath;
		}
		
		public String getOperation() {
			return operation;
		}
}
