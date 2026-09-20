package com.flopbox.ftp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameRequest(
        @NotBlank @Size(max = 2048) String path,
        @NotBlank @Size(max = 255) String newName
) {
}
