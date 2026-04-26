package com.legacy_lens.enums;

//public enum UploadStatus {
//    UPLOADED,
//    PROCESSING,
//    COMPLETED,
//    FAILED
//}

public enum UploadStatus {
    UPLOADED("Uploaded"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed");

    private final String label;

    UploadStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
