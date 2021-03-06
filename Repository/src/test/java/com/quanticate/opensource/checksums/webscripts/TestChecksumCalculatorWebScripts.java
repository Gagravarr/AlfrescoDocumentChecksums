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

import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_MD5;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_SHA_1;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_SHA_256;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_SHA_512;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.TEST_BYTES;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

import com.quanticate.opensource.checksums.calculator.ChecksumCalculator;

/**
 * WebScript tests for {@link ChecksumCalculator} (needs Alfresco)
 */
public class TestChecksumCalculatorWebScripts extends BaseWebScriptTest
{
   protected static final String urlPrefixParts   = "/com/quanitcate/opensource/checksums/node/";
   protected static final String urlPrefixNodeRef = "/com/quanitcate/opensource/checksums/node?nodeRef=";
   
   private AuthenticationComponent authenticationComponent;
   private Repository repositoryHelper;
   private NodeService nodeService;
   private ContentService contentService;
   private NodeRef companyHome;
   private NodeRef testFolder;
   private NodeRef testNode;
   private ChecksumCalculator calculator;

   @Override
   protected void setUp() throws Exception
   {
       super.setUp();

       ApplicationContext ctx = getServer().getApplicationContext();
       this.authenticationComponent = ctx.getBean("authenticationComponent", AuthenticationComponent.class);
       this.repositoryHelper = ctx.getBean("repositoryHelper", Repository.class);
       this.nodeService = ctx.getBean("NodeService", NodeService.class);
       this.contentService = ctx.getBean("ContentService", ContentService.class);
       this.calculator = ctx.getBean("ChecksumCalculator", ChecksumCalculator.class);

       this.authenticationComponent.setSystemUserAsCurrentUser();

       companyHome = this.repositoryHelper.getCompanyHome();
       
       // Create test node
       String name = "HashesTest" + GUID.generate();
       Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
       props.put(ContentModel.PROP_NAME, name);
       testFolder = nodeService.createNode(companyHome, ContentModel.ASSOC_CONTAINS, QName.createQName(name), ContentModel.TYPE_FOLDER, props).getChildRef();
       testNode = nodeService.createNode(testFolder, ContentModel.ASSOC_CONTAINS, QName.createQName(name), ContentModel.TYPE_CONTENT, props).getChildRef();
       
       // Put the test data in it
       InputStream is = new ByteArrayInputStream(TEST_BYTES);
       contentService.getWriter(testNode, ContentModel.PROP_CONTENT, true).putContent(is);
   }

   @Override
   protected void tearDown() throws Exception
   {
      this.authenticationComponent.setSystemUserAsCurrentUser();
      this.nodeService.deleteNode(testFolder);
   }
   
   public void testGetAllowedHashes() throws Exception
   {
      String url = "/com/quanitcate/opensource/checksums/hashes";
      Response resp = sendRequest(new GetRequest(url), Status.STATUS_OK);

      JSONObject json = (JSONObject)new JSONParser().parse(resp.getContentAsString());
      assertNotNull(json);
      assertTrue(json.containsKey("hashes"));
      
      JSONArray hashes = (JSONArray)json.get("hashes");
      assertEquals(calculator.getAllowedHashAlgorithms().size(), hashes.size());
      
      for (int i=0; i<hashes.size(); i++)
      {
         String hash = (String)hashes.get(i);
         assertEquals(true, calculator.getAllowedHashAlgorithms().contains(hash));
      }
   }
   
   public void testGetInvalidHashes() throws Exception
   {
      String url;
      Response resp;
      
      // Try for unsupported hashes
      url = urlPrefixNodeRef + testNode.toString();
      resp = sendRequest(new GetRequest(url), Status.STATUS_BAD_REQUEST);
      assertContains("No hash or hashes given", resp.getContentAsString());
      
      url = urlPrefixNodeRef + testNode.toString() + "&hash=WRONG";
      resp = sendRequest(new GetRequest(url), Status.STATUS_BAD_REQUEST);
      assertContains("Unsupported hash", resp.getContentAsString());
      
      // Try for invalid nodes
      url = urlPrefixParts + "type/store/id?hashes=MD5";
      resp = sendRequest(new GetRequest(url), Status.STATUS_NOT_FOUND);

      url = urlPrefixNodeRef + "space://1234/567&hashes=MD5";
      resp = sendRequest(new GetRequest(url), Status.STATUS_NOT_FOUND);
      
   }
   
   public void testGetHashes() throws Exception
   {
      String url;
      Response resp;
      JSONObject json;
      
      // Request one
      url = makePartsURL(testNode) + "?hash=MD5";
      resp = sendRequest(new GetRequest(url), Status.STATUS_OK);
      json = (JSONObject)new JSONParser().parse(resp.getContentAsString());
      assertEquals(HASH_MD5, json.get("MD5"));
      assertEquals(1, json.size());
 
      url = makeNodeRefURL(testNode) + "&hash=MD5";
      resp = sendRequest(new GetRequest(url), Status.STATUS_OK);
      json = (JSONObject)new JSONParser().parse(resp.getContentAsString());
      assertEquals(HASH_MD5, json.get("MD5"));
      assertEquals(1, json.size());


      // Request several
      url = makePartsURL(testNode) + "?hashes=MD5,SHA-1";
      resp = sendRequest(new GetRequest(url), Status.STATUS_OK);
      json = (JSONObject)new JSONParser().parse(resp.getContentAsString());
      assertEquals(HASH_MD5, json.get("MD5"));
      assertEquals(HASH_SHA_1, json.get("SHA-1"));
      assertEquals(2, json.size());
      
      // Request with duplicates
      url = makeNodeRefURL(testNode) + "&hashes=SHA-1,SHA-1,SHA-256,SHA-512";
      resp = sendRequest(new GetRequest(url), Status.STATUS_OK);
      json = (JSONObject)new JSONParser().parse(resp.getContentAsString());
      assertEquals(HASH_SHA_1, json.get("SHA-1"));
      assertEquals(HASH_SHA_256, json.get("SHA-256"));
      assertEquals(HASH_SHA_512, json.get("SHA-512"));
      assertEquals(3, json.size());
   }
   
   protected static String makePartsURL(NodeRef nodeRef)
   {
      return urlPrefixParts + nodeRef.getStoreRef().getProtocol() + "/" +
                              nodeRef.getStoreRef().getIdentifier() + "/" +
                              nodeRef.getId();
   }
   protected static String makeNodeRefURL(NodeRef nodeRef)
   {
      return urlPrefixNodeRef + nodeRef.toString();
   }
   public static void assertContains(String needle, String haystack)
   {
      assertTrue("'"+needle+"' not found in: " + haystack, haystack.contains(needle));
   }
}
