package com.mappn.gfan.common.codec;

import com.mappn.gfan.common.codec.Encoder;
import com.mappn.gfan.common.codec.EncoderException;

public interface BinaryEncoder extends Encoder {

   byte[] encode(byte[] var1) throws EncoderException;
}
