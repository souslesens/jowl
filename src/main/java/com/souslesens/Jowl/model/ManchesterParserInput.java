package com.souslesens.Jowl.model;

import org.springframework.web.multipart.MultipartFile;

public class ManchesterParserInput {

		private String filePath;
		private String url;
		private String ontologyContentEncoded64;

		private MultipartFile file;
		private String input;

		public ManchesterParserInput(String filePath , String url, String ontologyContentEncoded64, String input) {
			this.filePath = filePath;
			this.url = url;
			this.ontologyContentEncoded64 = ontologyContentEncoded64;
			this.input = input;
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

	public MultipartFile getFile() {
		return file;
	}

	public String getInput() {
			return input;
		}

	}


