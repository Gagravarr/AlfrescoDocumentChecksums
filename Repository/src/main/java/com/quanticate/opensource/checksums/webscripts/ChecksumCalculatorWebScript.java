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

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.Charsets;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.quanticate.opensource.checksums.calculator.ChecksumCalculator;

/**
 * WebScript to calculate checksums of Alfresco contents
 */
public class ChecksumCalculatorWebScript extends AbstractWebScript {
   private ChecksumCalculator calculator;
   public void setCalculator(ChecksumCalculator calculator)
   {
      this.calculator = calculator;
   }
   
   @Override
   public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
   {
      // Work out what Node they want
      NodeRef node;
      Map<String,String> args = req.getServiceMatch().getTemplateVars();
      if(args.get("store_type") != null)
      {
         node = new NodeRef(
               args.get("store_type"),
               args.get("store_id"),
               args.get("id")
         );
      }
      else
      {
         node = new NodeRef(args.get("noderef"));
      }
      
      // Work out what hash(es) they want
      // TODO
      
      // Start outputting, so the browser doesn't give up on us
      res.setContentType(WebScriptResponse.JSON_FORMAT);
      res.setContentEncoding(Charsets.UTF_8.toString());
      Writer writer = res.getWriter();
      writer.flush();

      // Have the calculation performed
      Map<String,String> hashes = calculator.getContentHashesHex(node, "TODO");
      
      // Return the information as JSON
      // TODO
   }
}
