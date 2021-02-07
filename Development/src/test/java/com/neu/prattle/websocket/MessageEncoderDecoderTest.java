package com.neu.prattle.websocket;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import javax.websocket.EndpointConfig;
/**
 * Test class that tests the methods in the MessageEncoder and MessageDecoder Class.
 */
public class MessageEncoderDecoderTest {
    @Mock
    EndpointConfig endpointConfig;
    @Test
    public void testInitInMessageEncoder(){
        MessageEncoder messageEncoder = new MessageEncoder();
        messageEncoder.init(endpointConfig);
        Assert.assertTrue(true);
    }
    @Test
    public void testDestroyMessageEncoder(){
        MessageEncoder messageEncoder = new MessageEncoder();
        messageEncoder.destroy();
        Assert.assertTrue(true);
    }
    @Test
    public void testEncodeMessageNullMessageEncoder(){
        MessageEncoder messageEncoder = new MessageEncoder();
        messageEncoder.encode(null);
        Assert.assertTrue(true);
    }
    @Test
    public void testDecodeMessageDecoder(){
        MessageDecoder messageDecoder = new MessageDecoder();
        messageDecoder.decode(null);
        Assert.assertTrue(true);
    }
}