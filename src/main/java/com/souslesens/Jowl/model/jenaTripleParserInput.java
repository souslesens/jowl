package com.souslesens.Jowl.model;

public class jenaTripleParserInput {

		private String filePath;
		private String url;
		private String ontologyContentEncoded64;
		
		public jenaTripleParserInput(String filePath , String url, String ontologyContentEncoded64) {
			this.filePath = filePath;
			this.url = url;
			this.ontologyContentEncoded64 = ontologyContentEncoded64;
		}
		public String getFilePath() {
			return filePath;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getOntologyContentEncoded64() {
			return ontologyContentEncoded64;
		}

	}


