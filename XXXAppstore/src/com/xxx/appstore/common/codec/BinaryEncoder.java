package com.xxx.appstore.common.codec;

import com.xxx.appstore.common.codec.Encoder;
import com.xxx.appstore.common.codec.EncoderException;

public interface BinaryEncoder extends Encoder {

   byte[] encode(byte[] var1) throws EncoderException;
}
