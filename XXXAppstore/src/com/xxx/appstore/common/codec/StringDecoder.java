package com.xxx.appstore.common.codec;

import com.xxx.appstore.common.codec.Decoder;
import com.xxx.appstore.common.codec.DecoderException;

public interface StringDecoder extends Decoder {

   String decode(String var1) throws DecoderException;
}
