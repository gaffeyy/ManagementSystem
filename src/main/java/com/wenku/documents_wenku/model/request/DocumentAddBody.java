package com.wenku.documents_wenku.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class DocumentAddBody implements Serializable {
	String documentName;
	String tags;
	String category;
}
