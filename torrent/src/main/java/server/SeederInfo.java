package server;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import protocol.Protocol;

import java.net.InetAddress;
import java.util.Date;

@Slf4j
@EqualsAndHashCode
@ToString
//@RequiredArgsConstructor
public class SeederInfo {

    public static final long LIFETIME = 5 * 60 * 1000;

    private final Protocol protocol;
    @Getter
    private final InetAddress inetAddress;
    @Getter
    private final short port;

    private final Date updateTime = new Date();

    public boolean isValid() {
        final long diff = (new Date()).getTime() - updateTime.getTime();
        return (diff <= LIFETIME);
    }

    public SeederInfo(Protocol protocol, InetAddress inetAddress, short port) {
//        System.out.printf("port value in SeederInfo constructor: %d\n", port);
        log.info(String.format("port value in SeederInfo constructor: %d\n", port));
//        System.out.printf("port value in SeederInfo constructor: %d\n", port);
        this.protocol = protocol;
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public SeederInfo(SeederInfo other) {
        this.protocol = other.protocol;
        this.inetAddress = other.inetAddress;
        this.port = other.port;
    }
}
