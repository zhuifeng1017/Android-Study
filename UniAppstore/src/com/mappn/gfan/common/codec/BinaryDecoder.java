package com.mappn.gfan.common.codec;

import com.mappn.gfan.common.codec.Decoder;
import com.mappn.gfan.common.codec.DecoderException;

public interface BinaryDecoder extends Decoder {

   byte[] decode(byte[] var1) throws DecoderException;
}
