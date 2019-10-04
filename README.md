![](https://github.com/jskov/dns-filter/workflows/Build%20and%20test/badge.svg)
![](https://github.com/jskov/dns-filter/.github/workflows/run-tests.yml/badge.svg)
# dns-filter
DNS server with filtering of ad hosts/domains


# Maven

./mvnw surefire-report:report

./mvnw compile quarkus:dev

Trigger rebuild with

curl localhost:8080 > /dev/null

# Capture query

Lookup mada.dk, following this, the next 20 queries will be logged in full:

$ dig @localhost -p 8053 mada.dk
$ dig @localhost -p 8053 jp.dk

2019-09-29 11:06:29,854 INFO  [dk.mad.dns.ser.DnsLookupService] (pool-4-thread-1) Decoded reply: Optional[DnsReply [getQuestion()=DnsRecordQ [getRecordType()=A, getName()=DnsName [name=jp.dk]], getAnswer()=DnsSection [type=ANSWER, records=[DnsRecordA [address=jp.dk./91.214.22.208, getDnsClass()=IN, getRecordType()=A, getName()=DnsName [name=jp.dk], getTtl()=55]]], getAuthority()=null]]
/* Request jp.dk : 0x0000
 * 0x0000 74 46 01 20 00 01 00 00  00 00 00 01 02 6a 70 02 tF. .........jp.
 * 0x0010 64 6b 00 00 01 00 01 00  00 29 10 00 00 00 00 00 dk.......)......
 * 0x0020 00 0c 00 0a 00 08 f4 bc  0b 5d c7 d2 02 3c       .........]...<
 */
byte[] req = new byte[] {0x74, 0x46, 0x01, 0x20, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x6a, 0x70, 0x02, 0x64, 0x6b, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x29, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0c, 0x00, 0x0a, 0x00, 0x08, (byte)0xf4, (byte)0xbc, 0x0b, 0x5d, (byte)0xc7, (byte)0xd2, 0x02, 0x3c, };
