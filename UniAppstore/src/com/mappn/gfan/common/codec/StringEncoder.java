package com.mappn.gfan.common.codec;

import com.mappn.gfan.common.codec.Encoder;
import com.mappn.gfan.common.codec.EncoderException;

public interface StringEncoder extends Encoder {

   String encode(String var1) throws EncoderException;
}
