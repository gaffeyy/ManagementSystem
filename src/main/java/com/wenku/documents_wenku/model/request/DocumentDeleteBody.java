package com.wenku.documents_wenku.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class DocumentDeleteBody implements Serializable {
	long deleteDocumentId;
}
