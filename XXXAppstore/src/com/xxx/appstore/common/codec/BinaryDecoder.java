package com.xxx.appstore.common.codec;

import com.xxx.appstore.common.codec.Decoder;
import com.xxx.appstore.common.codec.DecoderException;

public interface BinaryDecoder extends Decoder {

   byte[] decode(byte[] var1) throws DecoderException;
}
