package com.mappn.gfan.common.codec;

import com.mappn.gfan.common.codec.Decoder;
import com.mappn.gfan.common.codec.DecoderException;

public interface StringDecoder extends Decoder {

   String decode(String var1) throws DecoderException;
}
