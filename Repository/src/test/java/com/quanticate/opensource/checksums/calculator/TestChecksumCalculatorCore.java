/* ====================================================================
  Copyright 2016 Quanticate Ltd

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
==================================================================== */
package com.quanticate.opensource.checksums.calculator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Core (non-Alfresco) test for {@link ChecksumCalculator}
 */
public class TestChecksumCalculatorCore 
{
   protected static final String TEST_TEXT = "The quick brown fox jumped over the lazy dog";
   protected static final String HASH_MD5 = "08a008a01d498c404b0c30852b39d3b8";
   protected static final String HASH_SHA1 = "f6513640f3045e9768b239785625caa6a2588842";
   protected static final String HASH_SHA256 = "7d38b5cd25a2baf85ad3bb5b9311383e671a8a142eb302b324d4a5fba8748c69";
   protected static final String HASH_SHA512 = "db25330cfa5d14eaadf11a6263371cfa0e70fcd7a63a433b91f2300ca25d45b66a7b50d2f6747995c8fa0ff365b28974792e7acd5624e1ddd0d66731f346f0e7";
   
   @Test
   public void testAsHex()
   {
      Map<String,byte[]> bytes = new HashMap<>();
      bytes.put("Test1", new byte[] {0,1,2,3,4,5});
      bytes.put("Test2", new byte[] {-1, -2, -3, -4});
      bytes.put("Test3", new byte[] {0, (byte)0xff, 0x44});
      
      Map<String,String> hexes = ChecksumCalculator.asHex(bytes);
      
      assertEquals(3, hexes.size());
      
      assertEquals("000102030405", hexes.get("Test1"));
      assertEquals("fffefdfc", hexes.get("Test2"));
      assertEquals("00ff44", hexes.get("Test3"));
   }
}
