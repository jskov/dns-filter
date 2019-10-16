package fixture.dns.wiredata;

import java.net.InetAddress;
import java.net.UnknownHostException;

import dk.mada.dns.lookup.Query;
import dk.mada.dns.wire.model.DnsName;
import dk.mada.dns.wire.model.DnsRecords;
import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequests;

public class TestQueries {

	// dig @localhost -p 8053 +nocookie +noadflag +nodnssec jp.dk
	/* Query jp.dk, id:0x4f65, flags:0x0100 (Q,query,,,rd,,,,OK)
	* 0x0000 4f 65 01 00 00 01 00 00  00 00 00 01|02 6a 70 02 Oe...........jp.
	* 0x0010 64 6b 00 00 01 00 01 00  00 29 10 00 00 00 00 00 dk.......)......
	* 0x0020 00 00                                           ..
	 */
	public static byte[] JP_DK = new byte[] {0x4f, 0x65, 0x01, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x6a, 0x70, 0x02, 0x64, 0x6b, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x29, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, };


	// dig @localhost -p 8053 +nocookie +noadflag +nodnssec googleadservices.com
	/* Query googleadservices.com, id:0x0f52, flags:0x0100 (Q,query,,,rd,,,,OK)
	* 0x0000 0f 52 01 00 00 01 00 00  00 00 00 01|10 67 6f 6f .R...........goo
	* 0x0010 67 6c 65 61 64 73 65 72  76 69 63 65 73 03 63 6f gleadservices.co
	* 0x0020 6d 00 00 01 00 01 00 00  29 10 00 00 00 00 00 00 m.......).......
	* 0x0030 00                                              .
	 */
	public static byte[] GOOGLEADSERVICES_COM = new byte[] {0x0f, 0x52, 0x01, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x10, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, 0x61, 0x64, 0x73, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x73, 0x03, 0x63, 0x6f, 0x6d, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x29, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, };

	// dig @localhost -p 8053 +nocookie +noadflag +nodnssec acdn.adnxs.com
	/* Query acdn.adnxs.com, id:0x45eb, flags:0x0100 (Q,query,,,rd,,,,OK)
	* 0x0000 45 eb 01 00 00 01 00 00  00 00 00 01|04 61 63 64 E............acd
	* 0x0010 6e 05 61 64 6e 78 73 03  63 6f 6d 00 00 01 00 01 n.adnxs.com.....
	* 0x0020 00 00 29 10 00 00 00 00  00 00 00                ..)........
	 */
	public static byte[] ADNXS_COM = new byte[] {0x45, (byte)0xeb, 0x01, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x04, 0x61, 0x63, 0x64, 0x6e, 0x05, 0x61, 0x64, 0x6e, 0x78, 0x73, 0x03, 0x63, 0x6f, 0x6d, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x29, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, };

	/**
	 * Canned reply for acdn.adnxs.com, really:
	 * acdn.adnxs.com.		774	IN	CNAME	secure-adnxs.edgekey.net.
	 * secure-adnxs.edgekey.net. 2068	IN	CNAME	e6115.g.akamaiedge.net.
	 * e6115.g.akamaiedge.net.	20	IN	A	95.101.172.253
	 */
	public static DnsReply getAdnxsChainedReply(Query q) throws UnknownHostException {
		var adnxsCom = DnsName.fromName("acdn.adnxs.com");
		var edgekeyNet = DnsName.fromName("secure-adnxs.edgekey.net");
		var akamaiNet = DnsName.fromName("e6115.g.akamaiedge.net");
		
		var adnxsC = DnsRecords.cRecordFrom(adnxsCom, edgekeyNet,  100);
		var edgekeyC = DnsRecords.cRecordFrom(edgekeyNet, akamaiNet, 100);
		var akamaiA = DnsRecords.aRecordFrom(akamaiNet, InetAddress.getByName("95.101.172.253"), 100);
		
		return DnsReplies.fromRequestWithAnswers(q.getRequest(), adnxsC, edgekeyC, akamaiA);
	}
	
	/* Query detectportal.firefox.com, id:0x9cc9, flags:0x0100 (Q,query,,,rd,,,,OK)                                                                                                                                    
	* 0x0000 9c c9 01 00 00 01 00 00  00 00 00 01|0c 64 65 74 .............det                                                                                                                                         
	* 0x0010 65 63 74 70 6f 72 74 61  6c 07 66 69 72 65 66 6f ectportal.firefo                               
	* 0x0020 78 03 63 6f 6d 00 00 01  00 01 00 00 29 10 00 00 x.com.......)...                                                                                                                                         
	* 0x0030 00 00 00 00 00                                  .....                                                                                                                                                     
	 */                                                                                                                                                                                                                
	public static byte[] DETECTPORTAL_FIREFOX_COM = new byte[] {(byte)0x9c, (byte)0xc9, 0x01, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x0c, 0x64, 0x65, 0x74, 0x65, 0x63, 0x74, 0x70, 0x6f, 0x72, 0x74, 0x61, 0x6c, 0x07, 0x66, 0x69, 0x72, 
	0x65, 0x66, 0x6f, 0x78, 0x03, 0x63, 0x6f, 0x6d, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x29, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, };                                                                 

	/*
	 * 
	 * Make canned reply for detectportal.firefox.com, based on real reply:
	 * 	 detectportal.firefox.com.       11      IN      CNAME                                                    
	 *   detectportal.prod.mozaws.net.   60      IN      CNAME                                                    
	 *   detectportal.firefox.com-v2.edgesuite.net 9999 IN CNAME
     *   a1089.dscd.akamai.net.  10      IN      A       95.101.142.120                                           
     *   a1089.dscd.akamai.net.  10      IN      A       104.84.152.177
	*/
	public static DnsReply getDetectportalFirefoxChainedReply(Query q) throws UnknownHostException {
		var firefoxCom = DnsName.fromName("detectportal.firefox.com");
		var mozawsNet = DnsName.fromName("detectportal.prod.mozaws.net");
		var akamaiNet = DnsName.fromName("a1089.dscd.akamai.net");
		
		var firefoxC = DnsRecords.cRecordFrom(firefoxCom, mozawsNet,  100);
		var mozawsC = DnsRecords.cRecordFrom(mozawsNet, akamaiNet, 100);
		var akamaiA = DnsRecords.aRecordFrom(akamaiNet, InetAddress.getByName("95.101.142.120"), 100);
		
		return DnsReplies.fromRequestWithAnswers(q.getRequest(), firefoxC, mozawsC, akamaiA);
	}


	public static Query makeTestQuery(byte[] data) {
		var req = DnsRequests.fromWireData(data);
		
		var query = new Query(req, "127.0.0.1");
		return query;
	}

}
