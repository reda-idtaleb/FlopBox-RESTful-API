package com.flopbox.ftp;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RemoteFileResponse(
        String name,
        String path,
        String type,
        long size,
        Instant modifiedAt,
        String owner,
        String group,
        List<RemoteFileResponse> children
) {
}
