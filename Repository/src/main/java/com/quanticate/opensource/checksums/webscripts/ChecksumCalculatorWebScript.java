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
import java.io.Writer;
import java.util.Map;

import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.io.Charsets;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
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
      if (args.get("store_type") != null)
      {
         node = new NodeRef(
               args.get("store_type"),
               args.get("store_id"),
               args.get("id")
         );
      }
      else if (req.getParameter("nodeRef") != null)
      {
         node = new NodeRef(req.getParameter("nodeRef"));
      }
      else
      {
         throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No NodeRef given");
      }

      
      // Work out what hash(es) they want
      String[] hashAlgs;
      if (req.getParameter("hash") != null)
      {
         hashAlgs = new String[] { req.getParameter("hash") };
      }
      else if (req.getParameter("hashes") != null)
      {
         hashAlgs = req.getParameter("hashes").split(",");
      }
      else
      {
         throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No hash or hashes given");
      }

      
      // Start outputting, so the browser doesn't give up on us
      res.setContentType(WebScriptResponse.JSON_FORMAT);
      res.setContentEncoding(Charsets.UTF_8.toString());
      Writer writer = res.getWriter();
      writer.flush();

      
      // Have the calculation performed
      Map<String,String> hashes;
      try {
         hashes = calculator.getContentHashesHex(node, hashAlgs);
      }
      catch (InvalidNodeRefException e)
      {
         throw new WebScriptException(Status.STATUS_NOT_FOUND, node.toString());
      }
      catch (IllegalArgumentException ie)
      {
         throw new WebScriptException(Status.STATUS_BAD_REQUEST, ie.getMessage());
      }
      
      
      // Return the information as JSON
      String json = JSONObject.toJSONString(hashes);
      writer.write(json);
      writer.close();
   }
}
