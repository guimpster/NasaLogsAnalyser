package com.semantix

import org.junit._
import Assert._

@Test
class AppTest {

    val LOG_REGEX = """^(.*) - - \[(.*)\].*\"(.*)\" (\d*) ([-|\d]*)$""".r

    @Test
    def test404() = {
        val LOG_REGEX(host, collected_at, request, http_reply_code, reply_bytes)
          = """ts1-and-18.iquest.net - - [02/Jul/1995:21:09:35 -0400] "GET /pub/winvn/readme.txt HTTP/1.0" 404 -"""

        assertTrue("ts1-and-18.iquest.net".equals(host))
        assertTrue("02/Jul/1995:21:09:35 -0400".equals(collected_at))
        assertTrue("GET /pub/winvn/readme.txt HTTP/1.0".equals(request))
        assertTrue("404".equals(http_reply_code))
        assertTrue("-".equals(reply_bytes))
    }

    @Test
    def test200() = {
        val LOG_REGEX(host, collected_at, request, http_reply_code, reply_bytes)
        = """199.120.110.21 - - [01/Jul/1995:00:00:11 -0400] "GET /shuttle/missions/sts-73/sts-73-patch-small.gif" 200 4179"""

        assertTrue("199.120.110.21".equals(host))
        assertTrue("01/Jul/1995:00:00:11 -0400".equals(collected_at))
        assertTrue("GET /shuttle/missions/sts-73/sts-73-patch-small.gif".equals(request))
        assertTrue("200".equals(http_reply_code))
        assertTrue("4179".equals(reply_bytes))
    }
}


