package com.flopbox.ftp;

import com.flopbox.common.FtpGatewayException;
import com.flopbox.config.FtpProperties;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.Inet6Address;
import java.net.UnknownHostException;

@Component
public class FtpHostPolicy {

    private final FtpProperties properties;

    public FtpHostPolicy(FtpProperties properties) {
        this.properties = properties;
    }

    public InetAddress resolveAllowedHost(String host) {
        if (host == null || host.isBlank() || host.contains("://") || host.contains("/") || host.contains("\\")) {
            throw new FtpGatewayException("The FTP host is invalid.");
        }
        try {
            InetAddress[] resolved = InetAddress.getAllByName(host);
            if (resolved.length == 0) {
                throw new FtpGatewayException("The FTP host cannot be resolved.");
            }
            for (InetAddress address : resolved) {
                if (!properties.allowPrivateAddresses() && isPrivateOrLocal(address)) {
                    throw new FtpGatewayException("Connections to private or local FTP hosts are not allowed.");
                }
            }
            return resolved[0];
        } catch (UnknownHostException exception) {
            throw new FtpGatewayException("The FTP host cannot be resolved.", exception);
        }
    }

    private boolean isPrivateOrLocal(InetAddress address) {
        return address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()
                || isCarrierGradeNat(address)
                || isIpv6UniqueLocalAddress(address);
    }

    private boolean isCarrierGradeNat(InetAddress address) {
        byte[] bytes = address.getAddress();
        return bytes.length == 4
                && Byte.toUnsignedInt(bytes[0]) == 100
                && Byte.toUnsignedInt(bytes[1]) >= 64
                && Byte.toUnsignedInt(bytes[1]) <= 127;
    }

    private boolean isIpv6UniqueLocalAddress(InetAddress address) {
        if (!(address instanceof Inet6Address)) {
            return false;
        }
        return (Byte.toUnsignedInt(address.getAddress()[0]) & 0xfe) == 0xfc;
    }
}
