package com.xxx.appstore.common.codec;

import com.xxx.appstore.common.codec.Encoder;
import com.xxx.appstore.common.codec.EncoderException;

public interface StringEncoder extends Encoder {

   String encode(String var1) throws EncoderException;
}
