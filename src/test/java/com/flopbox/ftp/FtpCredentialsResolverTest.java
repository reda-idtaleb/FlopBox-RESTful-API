package com.flopbox.ftp;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FtpCredentialsResolverTest {

    @Test
    void defaultsToAnonymousWhenNoCredentialsAreSent() {
        FtpCredentials credentials = FtpCredentialsResolver.fromHeaders(null, null);

        assertThat(credentials).isEqualTo(FtpCredentials.anonymous());
    }

    @Test
    void rejectsAPartialCredentialPair() {
        assertThatThrownBy(() -> FtpCredentialsResolver.fromHeaders("ftp-user", null))
                .isInstanceOf(ResponseStatusException.class);
    }
}
