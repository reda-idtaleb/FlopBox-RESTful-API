package com.flopbox.ftp;

import java.time.Instant;

public record LastModifiedResponse(String path, Instant modifiedAt) {
}
