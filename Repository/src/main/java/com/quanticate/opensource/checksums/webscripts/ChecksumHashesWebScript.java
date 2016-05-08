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
package com.quanticate.opensource.checksums.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.quanticate.opensource.checksums.calculator.ChecksumCalculator;

/**
 * WebScript to report what hashes can be requested
 */
public class ChecksumHashesWebScript extends DeclarativeWebScript {
   private ChecksumCalculator calculator;
   public void setCalculator(ChecksumCalculator calculator)
   {
      this.calculator = calculator;
   }
   
   @Override
   protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
   {
      Map<String, Object> result = new HashMap<>();
      result.put("hashes", calculator.getAllowedHashAlgorithms());
      return result;
   }
}
