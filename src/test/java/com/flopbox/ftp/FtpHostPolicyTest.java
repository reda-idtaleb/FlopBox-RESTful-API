package com.flopbox.ftp;

import com.flopbox.common.FtpGatewayException;
import com.flopbox.config.FtpProperties;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FtpHostPolicyTest {

    @Test
    void blocksLoopbackHostsOutsideDevelopment() {
        FtpHostPolicy policy = new FtpHostPolicy(properties(false));

        assertThatThrownBy(() -> policy.resolveAllowedHost("127.0.0.1"))
                .isInstanceOf(FtpGatewayException.class)
                .hasMessageContaining("private or local");
    }

    @Test
    void permitsLoopbackHostsInDevelopment() {
        FtpHostPolicy policy = new FtpHostPolicy(properties(true));

        assertThat(policy.resolveAllowedHost("127.0.0.1").isLoopbackAddress()).isTrue();
    }

    private FtpProperties properties(boolean allowPrivateAddresses) {
        return new FtpProperties(Duration.ofSeconds(10), Duration.ofSeconds(30), 5, allowPrivateAddresses);
    }
}
